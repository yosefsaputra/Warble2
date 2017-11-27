package mpc.utexas.edu.warble2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mpc.utexas.edu.warble2.things.Bridge;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsBridge;

public class MainActivity extends AppCompatActivity {
    public String username = "yosef";
    public String userid = "QVhaSMPsPn-VVA5KwxSks3Lj1LAp92Wz3SWzWYN3";

//    PhilipsHueService mService = PhilipsHueUtil.getService("http://192.168.1.74");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUsernameView();
        setUseridView();
        setCreateUserButton();
        setShowInfoButton();

//        setLight1OffButton();
//        setLight1OnButton();

        new SSDPDiscovery().execute();
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
                System.out.println(upnp_message);
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
            ProgressBar asyncTaskProgressBar = findViewById(R.id.progressBar);
            asyncTaskProgressBar.setVisibility(View.INVISIBLE);

            List<String> bridge_names = new ArrayList<>();
            for (Bridge bridge: bridges) {
                bridge_names.add(bridge.getUUID());
            }

            ListView bridgeListView = findViewById(R.id.listBridgesView);
            ArrayAdapter<Bridge> adapter = new BridgeArrayAdapter(getApplicationContext(), bridges);
            bridgeListView.setAdapter(adapter);
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
            TextView textView = (TextView) rowView.findViewById(R.id.textView);
            textView.setText(bridges.get(position).getName());
            return rowView;
        }
    }


    private void setUsernameView() {
        TextView usernameView = findViewById(R.id.username);
        if (username == null) {
            usernameView.setText("null");
        } else {
            usernameView.setText(username);
        }
    }

    private void setUseridView() {
        TextView useridView = findViewById(R.id.userid);

        if (userid == null) {
            useridView.setText("null");
        } else {
            useridView.setText(userid);
        }
    }

    private void setCreateUserButton() {
        Button createUserButton = findViewById(R.id.createUserButton);
        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createUser = new Intent(MainActivity.this, CreateUserActivity.class);
                createUser.putExtra("username", username);
                createUser.putExtra("userid", userid);
                startActivity(createUser);
            }
        });
    }

    private void setShowInfoButton() {
        Button showInfoButton = findViewById(R.id.showInfoButton);
        showInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createUser = new Intent(MainActivity.this, ShowInfoActivity.class);
                createUser.putExtra("username", username);
                createUser.putExtra("userid", userid);
                startActivity(createUser);
            }
        });
    }

    private HashMap<String, Object> returnLightStateHashMap(boolean on, int bri) {
        HashMap<String, Object> lightState = new HashMap<>();
        lightState.put("on", on);
        lightState.put("bri", bri);
        lightState.put("transitiontime", 0);

        return lightState;
    }

//    private void setLight1OnButton() {
//        final Button light1OnButton = findViewById(R.id.light1OnButton);
//        light1OnButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            mService.putLight(userid, "10", returnLightStateHashMap(true, 50)).enqueue(new Callback<List<Object>>() {
//                @Override
//                public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
//                    PhilipsUser user = new PhilipsUser(username, userid);
//                    PhilipsLight light = new PhilipsLight("10", "10", user);
//                    light.setOn();
//                }
//
//                @Override
//                public void onFailure(Call<List<Object>> call, Throwable t) {}
//            });
//            }
//        });
//    }
//
//    private void setLight1OffButton() {
//        Button light1OffButton = findViewById(R.id.light1OffButton);
//        light1OffButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PhilipsUser user = new PhilipsUser(username, userid);
//                PhilipsLight light = new PhilipsLight("10", "10", user);
//                light.setOff();
//            }
//        });
//    }
}
