package util.service;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Base64;
import util.HTTP14;
import util.If;
import util.security.Hash;

/**
 * Note that different app-name will not affect put/get
 * meaning data is shared by all applications using the server
 * 0.5 | 8/20/2009
 * + sort the get values so oldest first, since openLookup does not sort
 * 0.4 | 8/11/2009
 * + Use get only instead of get_details since not using ttl
 * 0.3 | 8/9/2009
 * + Use openlookup instead
 * 0.2 | 11/6/2008
 * + Change to util.service
 * + Start from a random host instead of first
 * + MAX_RETRIES instead of all host
 * + Log exception
 * 0.1 | 6/21/2008
 * + add some static fallback server in case unable to get list
 * @author Thien Rong
 */
public class OpenDHT {

    public static void main(String[] args) throws Exception {

        // String[] ips = OpenDHT.getIPs();
        // System.out.println(ips.length);
        String[] ips = {"any.openlookup.net"};

        // OpenDHT.put_removable(ips, "1999", "1000", 604800); // 1 week(in seconds)

        // OpenDHT.put(ips, "auto_crypt", "995", 604800);
        // OpenDHT.put(ips, "auto_crypt", "999", 604800);
        for (String string : OpenDHT.get(ips, "f3e9496f38137b98efdbd06d33f234fc48236aa9")) {
            System.out.println(string);
            //System.out.println("next");
        }
    }
    public static final int MAX_TTL = 604800;
    private static final int MAX_RETRIES = 5;
    private static final If ipIf = new If(null, ":\t", ":"), putResultIf = new If(null, "<int>", "<"), getIf = new If(null, "<value><base64>", "<"),
            ttlIf = new If(null, "<value><int>", "<");

    public static SortedSet<ValueTTL> getWithTTL(String[] ips, String key) throws Exception {
        String data = "<methodCall><methodName>get_details</methodName><params>" +
                "<param><value><base64>" + Base64.encode(key) + "</base64></value></param>" + // name
                "<param><value><int>99999</int></value></param>" + // maximum no. of values to return
                "<param><value><base64></base64></value></param>" + //value
                "<param><value>XmlRpcTest</value></param>" + // name of app
                "</params></methodCall>";


        boolean found = false;
        int start = (int) (Math.random() * ips.length);
        SortedSet<ValueTTL> values = new TreeSet<ValueTTL>();
        for (int i = 0; i < MAX_RETRIES && !found; i++) {
            try {
                String content = sendData(data, ips[(start + i) % ips.length]);
                //System.out.println(content);
                found = true; // assume if gt data -> found the services
                String temp = null;
                while ((temp = getIf.process(content, getIf.getEndIndex())) != null) {

                    temp = temp.trim();
                    if (!temp.equals("AAAAAAAAAAAAAAAAAAAAAAAAAAA=") && temp.length() != 0) { // empty
                        int ttl = Integer.parseInt(ttlIf.process(content, getIf.getEndIndex()));
                        temp = Base64.decodeAsStr(temp.replaceAll("\n", "")).trim();
                        values.add(new ValueTTL(ttl, temp));
                        //  System.out.println(ttl + "/" + temp);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(OpenDHT.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        //reset for future usage
        getIf.setEndIndex(0);
        return values;
    }

    /**
     * @param ips to test
     * @param key
     * @return String[] where [0] is oldest and [size-1] is newest
     * @throws java.lang.Exception
     */
    public static String[] get(String[] ips, String key) throws Exception {
        String data = "<methodCall><methodName>get_details</methodName><params>" +
                "<param><value><base64>" + Base64.encode(key) + "</base64></value></param>" + // name
                "<param><value><int>99999</int></value></param>" + // maximum no. of values to return
                "<param><value><base64></base64></value></param>" + //value
                "<param><value>XmlRpcTest</value></param>" + // name of app
                "</params></methodCall>";


        boolean found = false;
        int start = (int) (Math.random() * ips.length);
        SortedSet<ValueTTL> values = new TreeSet<ValueTTL>();
        for (int i = 0; i < MAX_RETRIES && !found; i++) {
            try {
                String content = sendData(data, ips[(start + i) % ips.length]);
                //System.out.println(content);
                found = true; // assume if gt data -> found the services
                String temp = null;
                while ((temp = getIf.process(content, getIf.getEndIndex())) != null) {

                    temp = temp.trim();
                    if (!temp.equals("AAAAAAAAAAAAAAAAAAAAAAAAAAA=") && temp.length() != 0) { // empty
                        int ttl = Integer.parseInt(ttlIf.process(content, getIf.getEndIndex()));
                        temp = Base64.decodeAsStr(temp.replaceAll("\n", "")).trim();
                        values.add(new ValueTTL(ttl, temp));
                        //  System.out.println(ttl + "/" + temp);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(OpenDHT.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        //reset for future usage
        getIf.setEndIndex(0);

        if (found) {
            List<String> result = new ArrayList<String>();
            for (ValueTTL valueTTL : values) {
                result.add(valueTTL.value);
            }
            return result.toArray(new String[result.size()]);
        }

        throw new Exception("get failed");
    }

    public static void put_removable(String[] ips, String key, String value, int ttl) throws Exception {
        // should be secret but just put hash of key
        String data = "<methodCall><methodName>put_removable</methodName><params>" +
                "<param><value><base64>" + Base64.encode(key) + "</base64></value></param>" + // name
                "<param><value><base64>" + Base64.encode(value) + "</base64></value></param>" + //value
                //"<param><value></value></param>" + // hash type
                //"<param><value><base64>" + Base64.encode(Hash.SHA1(key)) + "</base64></value></param>" + // removable hash
                "<param><value><int>" + ttl + "</int></value></param>" + //ttl in seconds
                "<param><value>XmlRpcTest</value></param>" + // name of app                
                "</params></methodCall>";

        //System.out.println(data);
        boolean found = false;
        int start = (int) (Math.random() * ips.length);
        for (int j = 0; j < MAX_RETRIES && !found; j++) {
            try {
                String result = putResultIf.process(sendData(data, ips[(start + j) % ips.length]));
                if (result.equals("0")) {
                    found = true;
                } else if (result.equals("1")) {
                    throw new Exception("Error found?");
                }
            } catch (Exception ex) {

                Logger.getLogger(OpenDHT.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        if (!found) {
            throw new Exception("all put failed");
        }
    }

    public static void put(String[] ips, String key, String value, int ttl) throws Exception {
        String data = "<methodCall><methodName>put</methodName><params>" +
                "<param><value><base64>" + Base64.encode(key) + "</base64></value></param>" + // name
                "<param><value><base64>" + Base64.encode(value) + "</base64></value></param>" + //value
                "<param><value><int>" + ttl + "</int></value></param>" + //ttl in seconds
                "<param><value>XmlRpcTest</value></param>" + // name of app
                "</params></methodCall>";

        boolean found = false;
        int start = (int) (Math.random() * ips.length);
        for (int j = 0; j < MAX_RETRIES && !found; j++) {
            try {
                if (putResultIf.process(sendData(data, ips[(start + j) % ips.length])).equals("0")) {
                    found = true;
                }
            } catch (Exception ex) {

                Logger.getLogger(OpenDHT.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        if (!found) {
            throw new Exception("all put failed");
        }
    }

    private static String sendData(String data, String ip) throws Exception {
        String content = HTTP14.getContent("http://" + ip + ":5851/", HTTP14.POST, data, null);
        //System.out.println(content);
        return content;
    }

    public static String[] getIPs() throws Exception {
        List<String> ips = new ArrayList<String>(500);
        String content = HTTP14.getContent("http://opendht.org/servers.txt", HTTP14.GET, null, null);
        String ip;

        while ((ip = ipIf.process(content, ipIf.getEndIndex())) != null) {
            ips.add(ip.trim());
        }

        //reset in case future needs
        ipIf.setEndIndex(0);

        // try some fallback static servers in case fail to get any
        if (ips.size() == 0) {
            ips.add("152.3.138.1");
            ips.add("133.1.16.171");
            ips.add("147.46.240.165");
        }

        return (String[]) ips.toArray(new String[ips.size()]);
    }

    public static class ValueTTL implements Comparable<ValueTTL> {

        int ttl;
        String value;

        public ValueTTL(int ttl, String value) {
            this.ttl = ttl;
            this.value = value;
        }

        public int compareTo(ValueTTL o) {
            int comp = o.ttl - ttl;
            if (comp == 0) {
                return o.value.compareTo(value);
            } else if (comp > 0) {
                return -1;
            } else {
                return 1;
            }
        }

        public int getTtl() {
            return ttl;
        }

        public String getValue() {
            return value;
        }
    }
}
