package mpc.utexas.edu.warble2.services.PhilipsHue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yosef on 11/7/2017.
 */

public class LightStateResponse {
    @SerializedName("on")
    @Expose
    private boolean on;
    @SerializedName("sat")
    @Expose
    private int sat;
    @SerializedName("bri")
    @Expose
    private int bri;
    @SerializedName("hue")
    @Expose
    private int hue;
    @SerializedName("effect")
    @Expose
    private String effect;
    @SerializedName("xy")
    @Expose
    private List<Float> xy;
    @SerializedName("ct")
    @Expose
    private int ct;
    @SerializedName("alert")
    @Expose
    private String alert;
    @SerializedName("colormode")
    @Expose
    private String colormode;
    @SerializedName("reachable")
    @Expose
    private boolean reachable;

    public boolean getOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public int getSat() {
        return sat;
    }

    public void setSat(int sat) {
        // TODO Check boundary condition
        this.sat = sat;
    }

    public int getBri() {
        return bri;
    }

    public void setBri(int bri) {
        // TODO Check boundary condition
        this.bri = bri;
    }

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
        // TODO Check boundary condition
        this.hue = hue;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public List<Float> getXy() {
        return xy;
    }

    public void setXy(List<Float> xy) {
        this.xy = xy;
    }

    public int getCt() {
        return ct;
    }

    public void setCt(int ct) {
        // TODO Check boundary condition
        this.ct = ct;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getColormode() {
        return colormode;
    }

    public void setColormode(String colormode) {
        this.colormode = colormode;
    }

    public boolean getReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }

    public String toString() {
        String temp = "";
        temp += String.format("on: %s\n", on);
        temp += String.format("sat: %s\n", sat);
        temp += String.format("bri: %s\n", bri);
        temp += String.format("hue: %s\n", hue);
        temp += String.format("effect: %s\n", effect);
        temp += String.format("xy: %s\n", xy);
        temp += String.format("ct: %s\n", ct);
        temp += String.format("alert: %s\n", alert);
        temp += String.format("colormode: %s\n", colormode);
        temp += String.format("reachable: %s\n", reachable);
        return temp;
    }
}
