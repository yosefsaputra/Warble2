package mpc.utexas.edu.warble2.things;

import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsBridge;

/**
 * Created by yosef on 11/12/2017.
 */

public class Bridge extends Thing {
    public static String identifier = "Bridge";
    protected String name;
    protected String id;
    protected String base_url;
    protected String UUID;

    public static List<Bridge> discover(){
        List<Bridge> bridges = new ArrayList<>();

        // TODO Add more bridge childs, if necessary. Find idea how to do this better because it is too detailed.
        bridges.addAll(PhilipsBridge.discover());

        return bridges;
    }

    public Bridge(String name, String id, String base_url) {
        this.name = name;
        this.id = id;
        this.base_url = base_url;
    }

    public String getUUID(){
        return this.UUID;
    }

    public void setUUID(String UUID){
        this.UUID = UUID;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String toString() {
        String string = "";
        string += String.format("Name: %s\n", this.name);
        string += String.format("ID: %s\n", this.name);
        string += String.format("Base URL: %s\n", this.base_url);
        return string;
    }
}
