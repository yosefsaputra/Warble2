package mpc.utexas.edu.warble2.services;

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
    private List<String> upnp_messages = new ArrayList<>();
    private int ssdp_port = 1900;
    private String multicast_address = "239.255.255.250";

    public void multicast() {
        try {
            String DISCOVER_MESSAGE = "M-SEARCH * HTTP/1.1\r\n"
                    + "HOST: 239.255.255.250:1900\r\n"
                    + "MAN: \"ssdp:discover\"\r\n"
                    + "MX: 3\r\n"
                    + "ST: ssdp:all\r\n";
            MulticastSocket socket = new MulticastSocket(ssdp_port);
            InetAddress host = InetAddress.getByName(multicast_address);
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
    }
}
