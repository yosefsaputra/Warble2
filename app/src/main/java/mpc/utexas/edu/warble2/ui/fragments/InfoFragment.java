package mpc.utexas.edu.warble2.ui.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mpc.utexas.edu.warble2.R;
import mpc.utexas.edu.warble2.things.Bridge;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsBridge;

/**
 * Created by yosef on 11/28/2017.
 */

public class InfoFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.swiperefresh);
        final SwipeRefreshLayout.OnRefreshListener swipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new SSDPDiscovery().execute();
            }
        };
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);

        // Refreshing programmatically
        swipeRefreshLayout.post(new Runnable() {
            @Override public void run() {
                swipeRefreshLayout.setRefreshing(true);
                swipeRefreshListener.onRefresh();
            }
        });
    }

    public class SSDPDiscovery extends AsyncTask<Void, Void, List<Bridge>> {
        @Override
        protected List<Bridge> doInBackground(Void... params){
            List<String> upnp_messages = new ArrayList<>();
            try {
                String DISCOVER_MESSAGE = "M-SEARCH * HTTP/1.1\r\n"
                        + "HOST: 239.255.255.250:1900\r\n"
                        + "MAN: \"ssdp:discover\"\r\n"
                        + "MX: 3\r\n"
                        + "ST: ssdp:all\r\n";
                MulticastSocket socket = new MulticastSocket(1900);
                InetAddress host = InetAddress.getByName("239.255.255.250");
                DatagramPacket sendPacket = new DatagramPacket(DISCOVER_MESSAGE.getBytes(), DISCOVER_MESSAGE.length(), host, 1900);
                socket.setSoTimeout(5000);
                socket.send(sendPacket);

                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 5000) {
                    byte[] buf = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                    socket.receive(receivePacket);

                    String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    upnp_messages.add(msg);

                    // If sleep is not given, Philips HUE bridge is not found consistently. For some reasons.
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<Bridge> bridges = new ArrayList<>();
            List<String> uuid_bridges = new ArrayList<>();
            for (String upnp_message : upnp_messages) {
                if (upnp_message.contains("IpBridge")) {
                    Pattern pattern = Pattern.compile("LOCATION: (.+?)(\\r*)(\\n)", Pattern.DOTALL | Pattern.MULTILINE);
                    Matcher matcher = pattern.matcher(upnp_message);
                    if (matcher.find()) {
                        String xml_url = matcher.group(1);
                        Document document = requestWebpage(xml_url);
                        Bridge bridge = createBridgeFromXmlDocument(document);
                        if (!uuid_bridges.contains(bridge.getUUID())) {
                            bridges.add(bridge);
                            uuid_bridges.add(bridge.getUUID());
                        }
                    }
                }
            }

            return bridges;
        }

        @Override
        protected void onPostExecute(List<Bridge> bridges) {
            List<String> bridge_names = new ArrayList<>();
            for (Bridge bridge: bridges) {
                bridge_names.add(bridge.getUUID());
            }

            ListView bridgeListView = getView().findViewById(R.id.listBridgesView);
            ArrayAdapter<Bridge> adapter = new BridgeArrayAdapter(getContext(), bridges);
            bridgeListView.setAdapter(adapter);

            SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.swiperefresh);
            swipeRefreshLayout.setRefreshing(false);
        }

        private Document requestWebpage(String string_url) {
            Document document = null;
            try {
                URL url = new URL(string_url);

                SAXBuilder saxBuilder = new SAXBuilder();
                document = saxBuilder.build(url);
            } catch (IOException | JDOMException e) {
                e.printStackTrace();
            }
            return document;
        }

        private Bridge createBridgeFromXmlDocument(Document document) {
            Element rootElement = document.getRootElement();
            Element URLBase = rootElement.getChild("URLBase", rootElement.getNamespace());
            Element device = rootElement.getChild("device", rootElement.getNamespace());

            String name = device.getChild("friendlyName", rootElement.getNamespace()).getText();
            String modelName = device.getChild("modelName", rootElement.getNamespace()).getText();;
            String id = device.getChild("UDN", rootElement.getNamespace()).getText().replace("uuid:", "");
            String urlBase = URLBase.getText();

            if (modelName.contains("Philips")) {
                PhilipsBridge bridge = new PhilipsBridge(name, id, urlBase);
                bridge.setUUID(id);
                return bridge;
            } else {
                Bridge bridge = new Bridge(name, id, urlBase);
                bridge.setUUID(id);
                return bridge;
            }
        }
    }

    public class BridgeArrayAdapter extends ArrayAdapter<Bridge> {
        private final Context context;
        private final List<Bridge> bridges;

        public BridgeArrayAdapter(Context context, List<Bridge> bridges) {
            super(context, -1, bridges);
            this.context = context;
            this.bridges = bridges;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.bridge_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.bridgeNameTextView);
            textView.setText(bridges.get(position).getName());
            return rowView;
        }
    }

}
