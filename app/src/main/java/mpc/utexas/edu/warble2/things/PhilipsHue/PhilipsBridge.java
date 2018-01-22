package mpc.utexas.edu.warble2.things.PhilipsHue;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.BridgeDao;
import mpc.utexas.edu.warble2.database.UserDao;
import mpc.utexas.edu.warble2.services.PhilipsHue.PhilipsHueService;
import mpc.utexas.edu.warble2.things.Bridge;
import mpc.utexas.edu.warble2.things.BridgeInterface;
import mpc.utexas.edu.warble2.utils.PhilipsHueUtil;
import mpc.utexas.edu.warble2.utils.SSDPDiscovery;

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
    public String getCapability(){
        return "Return capabilities of Philips Bridge";
    }

    @Override
    public String getThings(){
        return "Return things of Philips Bridge";
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
                // System.out.println(upnp_message);
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
