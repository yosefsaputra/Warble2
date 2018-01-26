package mpc.utexas.edu.warble2.things.PhilipsHue;

import java.util.HashMap;
import java.util.List;

import mpc.utexas.edu.warble2.services.PhilipsHue.PhilipsHueService;
import mpc.utexas.edu.warble2.things.Light;
import mpc.utexas.edu.warble2.things.LightInterface;
import mpc.utexas.edu.warble2.users.PhilipsHue.PhilipsUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yosef on 11/7/2017.
 */

public class PhilipsLight extends Light implements LightInterface {
    public static String identifier = "PhilipsLight";
    private PhilipsHueService service;
    private PhilipsUser user;
    private PhilipsBridge parentBridge;

    public PhilipsLight(String name, String id, PhilipsUser user, PhilipsBridge parentBridge) {
        this.name = name;
        this.id = id;
        this.user = user;
        this.parentBridge = parentBridge;
        this.service = parentBridge.getService();
    }

    @Override
    public String getCapability() {
        return "Return capability of Philips Light";
    }

    @Override
    public void setOn() {
        HashMap<String, Object> lightState = new HashMap<>();
        lightState.put("on", true);
        lightState.put("bri", 50);

        service.putLight(this.user.getUserid(), this.id, lightState).enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                System.out.println(response.body());
            }

            @Override
            public void onFailure(Call<List<Object>> call, Throwable t) {}
        });
    }

    @Override
    public void setOff() {
        HashMap<String, Object> lightState = new HashMap<>();
        lightState.put("on", false);
        lightState.put("transitiontime", 0);

        service.putLight(this.user.getUserid(), this.id, lightState).enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                System.out.println(response.body());
            }

            @Override
            public void onFailure(Call<List<Object>> call, Throwable t) {}
        });
    }

    public String getName() {
        return this.name;
    }
}
