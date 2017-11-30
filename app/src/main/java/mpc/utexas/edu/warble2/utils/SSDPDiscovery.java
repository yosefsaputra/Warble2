package mpc.utexas.edu.warble2.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by yosef on 11/29/2017.
 */

public class SSDPDiscovery {
    public static List<String> discover() {
        return discover(5000);
    }

    public static List<String> discover(int duration_microseconds) {
        List<String> upnp_messages = new ArrayList<>();
        int ssdp_port = 1900;
        String multicast_address = "239.255.255.250";

        try {
            String DISCOVER_MESSAGE = "M-SEARCH * HTTP/1.1\r\n"
                    + "HOST: 239.255.255.250:1900\r\n"
                    + "MAN: \"ssdp:discover\"\r\n"
                    + "MX: 3\r\n"
                    + "ST: ssdp:all\r\n";
            MulticastSocket socket = new MulticastSocket(ssdp_port);
            InetAddress host = InetAddress.getByName(multicast_address);
            DatagramPacket sendPacket = new DatagramPacket(DISCOVER_MESSAGE.getBytes(), DISCOVER_MESSAGE.length(), host, 1900);
            socket.setSoTimeout(duration_microseconds);
            socket.send(sendPacket);

            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < duration_microseconds) {
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

        return upnp_messages;
    }
}
