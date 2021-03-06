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
import mpc.utexas.edu.warble2.database.LocationConverter;
import mpc.utexas.edu.warble2.services.PhilipsHue.PhilipsHueService;
import mpc.utexas.edu.warble2.things.Bridge;
import mpc.utexas.edu.warble2.things.Light;
import mpc.utexas.edu.warble2.things.Thing;
import mpc.utexas.edu.warble2.users.PhilipsHue.PhilipsUser;
import mpc.utexas.edu.warble2.users.User;
import mpc.utexas.edu.warble2.utils.PhilipsHueUtil;
import mpc.utexas.edu.warble2.utils.SSDPDiscovery;
import okhttp3.ResponseBody;

/**
 * Created by yosef on 11/12/2017.
 */

public class PhilipsBridge extends Bridge {
    public static String identifier = "PhilipsBridge";
    public static String TAG = "PhilipsBridge";
    public static String bridgeToUser = "onetomany";
    protected PhilipsHueService service;


    // ======= [start Constructor methods] =======
    public PhilipsBridge(String name, String id, String baseUrl, User user) {
        super(name, id, baseUrl, identifier, user);
        this.service = PhilipsHueUtil.getService(baseUrl);
    }

    public PhilipsBridge(String name, String id, String baseUrl, User user, long dbid) {
        super(name, id, baseUrl, identifier, user, dbid);
        this.service = PhilipsHueUtil.getService(baseUrl);
    }
    // ======== [end Constructor methods] ========


    // ======= [start Getter Setter implementation] =======
     public PhilipsHueService getService() {
         return this.service;
     }
    // ======== [end Getter Setter implementation] ========


    // ======== [start Static methods] ========
    // TODO Make the discovery simpler and neater
    public static List<Bridge> discover(Context context) {
        int duration_microseconds = 2000;
        List<String> upnp_messages;

        List<Bridge> existingBridges = PhilipsBridge.getAllDb(context);

        upnp_messages = SSDPDiscovery.discover(duration_microseconds);

        List<Bridge> bridges = new ArrayList<>();
        List<String> uuid_bridges = new ArrayList<>();
        for (String upnp_message : upnp_messages) {
            Log.d(TAG, upnp_message);
            if (upnp_message.contains("IpBridge")) {
                Pattern pattern = Pattern.compile("LOCATION: (.+?)(\\r*)(\\n)", Pattern.DOTALL | Pattern.MULTILINE);
                Matcher matcher = pattern.matcher(upnp_message);
                if (matcher.find()) {
                    String xml_url = matcher.group(1);
                    Document document = requestWebpage(xml_url);
                    Bridge bridge = createBridgeFromXmlDocument(document);

                    if (bridge != null & !uuid_bridges.contains(bridge.getUUID())) {
                        for (Bridge i: existingBridges) {
                            if (i.getUUID().equals(bridge.getUUID())) {
                                bridge = i;
                                break;
                            }
                        }
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

        String id = device.getChild("UDN", rootElement.getNamespace()).getText().replace("uuid:", "");
        String urlBase = URLBase.getText();

        if (modelName.contains("Philips")) {
            Bridge bridge = new PhilipsBridge(name, id, urlBase, null);
            bridge.setUUID(id);
            return bridge;
        } else {
            return null;
        }
    }

    public static List<Bridge> getAllDb(Context context) {
        Log.d(TAG, "Getting All from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        List<BridgeDb> dbBridgeDbs = appDatabase.bridgeDao().getAllBridgesByCategory(identifier);

        List<Bridge> philipsBridges = new ArrayList<>();
        for (BridgeDb dbBridgeDb : dbBridgeDbs) {
            philipsBridges.add(new PhilipsBridge(dbBridgeDb.name, dbBridgeDb.UUID, dbBridgeDb.baseUrl, PhilipsUser.getUserByDbid(context, dbBridgeDb.userDbid)));
        }

        return philipsBridges;
    }

    public static PhilipsBridge getBridgeById(Context context, long dbid) {
        Log.d(TAG, "Getting PhilipsBridge by Id from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        BridgeDb dbBridgeDb = appDatabase.bridgeDao().getBridge(dbid);

        PhilipsBridge bridge = null;

        if (dbBridgeDb.category.equals(PhilipsBridge.identifier)) {
            bridge = new PhilipsBridge(dbBridgeDb.name, dbBridgeDb.UUID, dbBridgeDb.baseUrl, PhilipsUser.getUserByDbid(context, dbBridgeDb.userDbid), dbBridgeDb.dbid);
        }

        return bridge;
    }

    public static void deleteAllDb(Context context) {}
    // ========= [end Static methods] =========


    // ======= [start ThingInterface implementation] =======
    @Override
    public String getCapability() {
        return "Return capabilities of PhilipsBridge";
    }
    // ======== [end ThingInterface implementation] ========


    // ======== [start BridgeInterface methods] ========
    @Override
    public List<Thing> discoverThings(Context context) {
        if (this.user == null) {
            Log.d(TAG, "FAIL: User of this bridge is not defined");
            return new ArrayList<>();
        }
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
        } catch (ParseException | IOException | NullPointerException | ClassCastException e) {
            Log.e(TAG, "exception", e);
            jsonObject = new JSONObject();
        }

        // List<String> lightNames = new ArrayList<>();
        for (Object o : jsonObject.keySet()) {
            String lightId = o.toString();
            JSONObject details = (JSONObject) jsonObject.get(o);
            String lightStringLocation = (String) details.get("name");
            String lightStringId = (String) details.get("uniqueid");

            String regex = "(\\w+,)(\\d+,\\d+)";
            Pattern r = Pattern.compile(regex);
            Matcher m = r.matcher(lightStringLocation);

            Light philipsLight;
            if (m.find()) {
                philipsLight = new PhilipsLight(lightId, lightStringId, LocationConverter.toLocation(String.format("(%s)", m.group(2))), users.get(0), this);
            } else {
                philipsLight = new PhilipsLight(lightId, lightStringId, LocationConverter.toLocation(null), users.get(0), this);
            }
            lights.add(philipsLight);
        }

        // for (String lightName : lightNames) {
        //     Thing philipsLight = new PhilipsLight(lightName, users.get(0), this);
        //     lights.add(philipsLight);
        // }

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
                responseBody = service.getLights(this.user.getId()).execute().body();
            } catch (IOException | NullPointerException e) {
                Log.e(TAG, "exception", e);
                responseBody = null;
            }

            JSONObject jsonObject;
            try {
                JSONParser parser = new JSONParser();
                jsonObject = (JSONObject) parser.parse(responseBody.string());
            } catch (ParseException | IOException | NullPointerException | ClassCastException e) {
                Log.e(TAG, "exception", e);
                jsonObject = new JSONObject();
            }

            // List<String> lightIds = new ArrayList<>();
            // List<String> lightStringLocations = new ArrayList<>();
            for (Object o : jsonObject.keySet()) {
                String lightId = o.toString();
                JSONObject details = (JSONObject) jsonObject.get(o);
                String lightStringLocation = (String) details.get("name");
                String lightStringId = (String) details.get("uniqueid");
                JSONObject state = (JSONObject) details.get("state");
                Boolean lightReachable = (Boolean) state.get("reachable");

                if (lightReachable) {
                    String regex = "(\\w+,)(\\d+,\\d+)";
                    Pattern r = Pattern.compile(regex);
                    Matcher m = r.matcher(lightStringLocation);

                    Light philipsLight;
                    if (m.find()) {
                        philipsLight = new PhilipsLight(lightId, lightStringId, LocationConverter.toLocation(String.format("(%s)", m.group(2))), users.get(0), this);
                    } else {
                        philipsLight = new PhilipsLight(lightId, lightStringId, LocationConverter.toLocation(null), users.get(0), this);
                    }
                    lights.add(philipsLight);
                }
            }

            // for (String lightId : lightIds) {
            //     Light philipsLight = new PhilipsLight(lightId, users.get(0), this);
            //     lights.add(philipsLight);
            // }
        } else {
        }

        return lights;
    }
    // ========= [end BridgeInterface methods] =========

    // ======== [start Other methods] ========
    // ========= [end Other methods] =========
}
