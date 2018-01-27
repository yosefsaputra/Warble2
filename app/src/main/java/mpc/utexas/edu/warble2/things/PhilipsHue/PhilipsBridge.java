package mpc.utexas.edu.warble2.things.PhilipsHue;

import android.content.Context;
import android.util.Log;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.BridgeDb;
import mpc.utexas.edu.warble2.services.PhilipsHue.PhilipsHueService;
import mpc.utexas.edu.warble2.things.Bridge;
import mpc.utexas.edu.warble2.things.BridgeInterface;
import mpc.utexas.edu.warble2.things.Light;
import mpc.utexas.edu.warble2.things.Thing;
import mpc.utexas.edu.warble2.users.PhilipsHue.PhilipsUser;
import mpc.utexas.edu.warble2.utils.PhilipsHueUtil;
import mpc.utexas.edu.warble2.utils.SSDPDiscovery;
import okhttp3.ResponseBody;

/**
 * Created by yosef on 11/12/2017.
 */

public class PhilipsBridge extends Bridge implements BridgeInterface {
    public static String identifier = "PhilipsBridge";
    private PhilipsHueService service;

    public PhilipsBridge(String name, String id, String base_url) {
        super(name, id, base_url);
        this.service = PhilipsHueUtil.getService(base_url);
    }

    @Override
    public String getCapability() {
        return "Return capabilities of PhilipsBridge";
    }

    public PhilipsHueService getService() {
        return service;
    }

    // TODO Make the discovery simpler and neater
    public static List<Bridge> discover() {
        int duration_microseconds = 5000;
        List<String> upnp_messages;

        upnp_messages = SSDPDiscovery.discover(duration_microseconds);

        List<Bridge> bridges = new ArrayList<>();
        List<String> uuid_bridges = new ArrayList<>();
        for (String upnp_message : upnp_messages) {
            if (upnp_message.contains("IpBridge")) {
                Log.d(TAG, upnp_message);
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

    private static Document requestWebpage(String string_url) {
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

    private static Bridge createBridgeFromXmlDocument(Document document) {
        Element rootElement = document.getRootElement();
        Element URLBase = rootElement.getChild("URLBase", rootElement.getNamespace());
        Element device = rootElement.getChild("device", rootElement.getNamespace());

        String name = device.getChild("friendlyName", rootElement.getNamespace()).getText();
        String modelName = device.getChild("modelName", rootElement.getNamespace()).getText();
        ;
        String id = device.getChild("UDN", rootElement.getNamespace()).getText().replace("uuid:", "");
        String urlBase = URLBase.getText();

        if (modelName.contains("Philips")) {
            Bridge bridge = new PhilipsBridge(name, id, urlBase);
            bridge.setUUID(id);
            return bridge;
        } else {
            Bridge bridge = new Bridge(name, id, urlBase);
            bridge.setUUID(id);
            return bridge;
        }
    }

    public static List<PhilipsBridge> getAllPhilipsBridgesDb(Context context) {
        Log.d(TAG, "Getting All from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        List<BridgeDb> dbBridgeDbs = appDatabase.bridgeDao().getAllBridgesByCategory(identifier);

        List<PhilipsBridge> philipsBridges = new ArrayList<>();

        for (BridgeDb dbBridgeDb : dbBridgeDbs) {
            philipsBridges.add(new PhilipsBridge(dbBridgeDb.name, dbBridgeDb.UUID, dbBridgeDb.base_url));
        }

        return philipsBridges;
    }

    @Override
    public List<Thing> discoverThings(Context context) {
        Log.d(TAG, "Discover Philips Things");
        final List<PhilipsUser> users = PhilipsUser.getAllDb(context);
        final List<Thing> lights = new ArrayList<>();

        ResponseBody responseBody;
        try {
            responseBody = service.getLights(users.get(0).getId()).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "exception", e);
            responseBody = null;
        }

        JSONObject jsonObject;
        try {
            JSONParser parser = new JSONParser();
            jsonObject = (JSONObject) parser.parse(responseBody.string());
        } catch (ParseException | IOException | NullPointerException e) {
            Log.e(TAG, "exception", e);
            jsonObject = new JSONObject();
        }

        List<String> lightNames = new ArrayList<>();
        for (Object o : jsonObject.keySet()) {
            String lightName = o.toString();
            lightNames.add(lightName);
        }

        for (String lightName : lightNames) {
            Thing philipsLight = new PhilipsLight(lightName, users.get(0), this);
            lights.add(philipsLight);
        }

        return lights;
    }

    @Override
    public List<Light> discoverLights(Context context) {
        Log.d(TAG, "Discover Philips Lights");
        final List<PhilipsUser> users = PhilipsUser.getAllDb(context);
        final List<Light> lights = new ArrayList<>();

        if (!users.isEmpty()) {
            ResponseBody responseBody;
            try {
                responseBody = service.getLights(users.get(0).getId()).execute().body();
            } catch (IOException e) {
                Log.e(TAG, "exception", e);
                responseBody = null;
            }

            JSONObject jsonObject;
            try {
                JSONParser parser = new JSONParser();
                jsonObject = (JSONObject) parser.parse(responseBody.string());
            } catch (ParseException | IOException | NullPointerException e) {
                Log.e(TAG, "exception", e);
                jsonObject = new JSONObject();
            }

            List<String> lightNames = new ArrayList<>();
            for (Object o : jsonObject.keySet()) {
                String lightName = o.toString();
                lightNames.add(lightName);
            }

            for (String lightName : lightNames) {
                Light philipsLight = new PhilipsLight(lightName, users.get(0), this);
                lights.add(philipsLight);
            }
        } else {
        }

        return lights;
    }
}
