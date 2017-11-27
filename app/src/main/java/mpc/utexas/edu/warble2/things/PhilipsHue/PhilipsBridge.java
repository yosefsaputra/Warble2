package mpc.utexas.edu.warble2.things.PhilipsHue;

import mpc.utexas.edu.warble2.services.PhilipsHue.PhilipsHueService;
import mpc.utexas.edu.warble2.things.Bridge;
import mpc.utexas.edu.warble2.things.BridgeInterface;
import mpc.utexas.edu.warble2.utils.PhilipsHueUtil;

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
}
