package util;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import model.BibtexMessage;
import model.BibtexMessageCodec;
import model.friend.Friend;
import model.friend.MyProfile;
import util.security.Hash;
import util.security.RSA;
import util.service.OpenDHT;
import util.service.OpenDHT.ValueTTL;
import view.friend.Status;

/**
 * Used to update personal information
 * Update main user lists if don't exists
 * Update status
 * Send offline msg
 * Read msgs so won't show? (the signatures)
 * @author Thien Rong
 */
public class OpenDHTHelper {

    // offline feed protocol?
    public static final String FEED_FRIEND_REQUEST = "jabrefpp_feed_friend_request";
    public static final String FEED_ITEMUPDATE = "jabrefpp_feed_itemupdate";
    public static final String FEED_FRIEND_RESPONSE = "jabrefpp_feed_friend_request";
    public static final String FEED_ = "jabrefpp_msg";

//    public static float convertToDecimal(String x) {
//        //String x = "10111111110000000000000000000000";
//        //String x = "01000";
//        //System.out.println(x.length());
//        String excess127 = x.substring(1, 9);
//        //System.out.println(excess127);
//        int y = Integer.parseInt(excess127, 2);
//        //System.out.println(y-127);
//
//        float result = 1;
//        //System.out.println(y + ", " + x.charAt(35));
//        //System.out.println("result of " + result);
//        for (int i = 9; i < 32; i++) {
//            if (x.charAt(i) == '1') {
//                //System.out.println(Math.pow(2, -(i - 8)));
//                result += Math.pow(2, -(i - 8));
//
//            } else {
//            }
//        }
//        if (x.charAt(0) == '1') {
//            result = -result;
//        }
//        //  System.out.println(result + " * " + (float) Math.pow(2, (y - 127)));
//        result = result * (float) Math.pow(2, (y - 127));
//
////System.out.println(result * );
//
//        //System.out.println(result+1);
//        return result;
//    }
//    public static String convertToFloatingPoint(float x) {
//
//        String result = (x < 0) ? "1" : "0";
//    }
    public static void main(String[] args) throws Exception {
//        String x = "01000001010101000000000000000000";
//        float xx = convertToDecimal(x);
//
//        String y = "10111111011000000000000000000000";
//        float yy = convertToDecimal(y);
//        System.out.println(xx);
//        System.out.println(yy);
//        System.out.println(xx * yy);
//
//        System.exit(0);

        OpenDHTHelper dht = new OpenDHTHelper();

        MyProfile my = new MyProfile("Joe2", 8010, 8011, RSA.generateKeyPair());
        String hash = Hash.SHA1(DHTProtocol.PREFIX_USERDETAIL + my.getFUID());
        //
        OpenDHT.put(ips, hash, RSA.sign(FriendStringCodec.toString(my), RSA.toPrivateKey(my.getPrivateKey()), null), 100);//OpenDHT.MAX_TTL);
        //dht.updateUserList(my.getFUID());


        KeyPair fakeMe = RSA.generateKeyPair();
        KeyPair fakeFriend = RSA.generateKeyPair();

        dht.sendOfflineMessage(fakeMe, RSA.toBase64(fakeFriend)[1], BibtexMessageCodec.toString(new BibtexMessage(null, "subj", "msg", "entries", "to")));
        List<BibtexMessage> offlineMails = dht.getOfflineMessages(fakeFriend);
        for (BibtexMessage bibtexMessage : offlineMails) {
            System.out.println("offline message " + bibtexMessage);
            System.out.println(bibtexMessage.getFromFUID());
            System.out.println(bibtexMessage.getMsg());
        }


        hash = Hash.SHA1(DHTProtocol.PREFIX_USERSTATUS + my.getFUID());
        //OpenDHT.put(ips, hash, RSA.sign("hello my friend", RSA.toPrivateKey(my.getPrivateKey()), null), OpenDHT.MAX_TTL);

        //System.out.println("xx "+dht.getUserDetails(my.getFUID()));

        //String s = RSA.toBase64(RSA.generateKeyPair())[1];
        //dht.updateUserList(s);

        /*
        for (String string : dht.getUserList()) {
        System.out.println("xx " + string);
        Friend f = dht.getUserDetails(string);
        if (f != null) {
        System.out.println(FriendStringCodec.toString(f));
        for (Status string1 : dht.getStatus(string)) {
        System.out.println("status x: " + string1.getStatus());
        }
        }
        System.out.println();
        }
         */
    }
    static String[] ips = {"any.openlookup.net"};

    public OpenDHTHelper() {
    }

    /**
     * Update your own status
     * * value is signed
     * @param newStatus
     * @throws Exception
     */
    public void updateStatus(String newStatus, MyProfile my) throws Exception {
        String hash = Hash.SHA1(DHTProtocol.PREFIX_USERSTATUS + my.getFUID());
        OpenDHT.put(ips, hash, RSA.sign(newStatus, RSA.toPrivateKey(my.getPrivateKey()), null), OpenDHT.MAX_TTL);
    }

    /**
     * Update my details
     * * value is signed
     * @throws Exception
     */
    public void updateMyDetails(MyProfile my) throws Exception {
        String hash = Hash.SHA1(DHTProtocol.PREFIX_USERDETAIL + my.getFUID());

        OpenDHT.put(ips, hash, RSA.sign(FriendStringCodec.toString(my), RSA.toPrivateKey(my.getPrivateKey()), null), OpenDHT.MAX_TTL);
    }

    /**
     * Update friends/self public key to the main user list
     * No need encryption
     * * value is Hashed
     * @param userPublicKey
     */
    public void updateUserList(String userPublicKey) throws Exception {
        String hash = Hash.SHA1(DHTProtocol.PREFIX_USERLIST);
        OpenDHT.put(ips, hash, userPublicKey, OpenDHT.MAX_TTL);
    }

    /**
     * Get list of FUIDs/public key
     * @return
     * @throws Exception
     */
    public String[] getUserList() throws Exception {
        String hash = Hash.SHA1(DHTProtocol.PREFIX_USERLIST);
        return OpenDHT.get(ips, hash);
    }

    /**
     * @param publicKey
     * @return
     * @throws Exception
     */
    public Friend getUserDetails(String publicKey) throws Exception {
        String hash = Hash.SHA1(DHTProtocol.PREFIX_USERDETAIL + publicKey);
        //System.out.println("get " + hash);
        String[] s = OpenDHT.get(ips, hash);
        Friend f = null;
        for (String string : s) {
            String verified = RSA.verify(string, RSA.toPublicKey(publicKey), null);
            if (verified != null) {
                f = FriendStringCodec.fromString(verified);
            }
        }

        return f;
    }

    public List<Friend> getAll(String[] publicKeys) {
        final List<Friend> result = new ArrayList<Friend>();
        final Semaphore sema = new Semaphore(-publicKeys.length + 1);
        for (final String string : publicKeys) {
            Thread t = new Thread() {

                @Override
                public void run() {
                    try {
                        Friend ff = getUserDetails(string);
                        if (ff != null) {
                            synchronized (sema) {
                                result.add(ff);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    sema.release();

                }
            };
            t.start();
        }
        try {
            sema.acquire();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param publicKey
     * @throws Exception
     */
    public List<Status> getStatus(String publicKey) throws Exception {
        String hash = Hash.SHA1(DHTProtocol.PREFIX_USERSTATUS + publicKey);
        List<Status> result = new ArrayList<Status>();

        SortedSet<ValueTTL> s = OpenDHT.getWithTTL(ips, hash);
        for (ValueTTL value : s) {
            String verified = RSA.verify(value.getValue(), RSA.toPublicKey(publicKey), null);
            if (verified != null) {
                result.add(new Status(verified, value.getTtl()));
            }
        }

        return result;
    }

    public String[] getFeed(String publicKey, String privateKey) throws Exception {
        String hash = Hash.SHA1(DHTProtocol.PREFIX_FEED + publicKey);
        List<String> result = new ArrayList<String>();

        String[] s = OpenDHT.get(ips, hash);
        for (String string : s) {
            String verified = RSA.verifyWithPrefixPublicKey(string, RSA.toPrivateKey(privateKey))[0];
            if (verified != null) {
                result.add(verified);
            }
        }

        return result.toArray(new String[result.size()]);
    }

    public void putFeedUpdate(String destPublicKey, KeyPair keyPair, String data) {
        //String verified = RSA.verifyWithPrefixPublicKey(string);
        //if (verified != null) {
        //       result.add(verified);
        //  }
        // OpenDHT.put(ips, DHTProtocol.PREFIX_FEED + destPublicKey, data, ttl)
    }

    public void sendOfflineMessage(KeyPair myKeyPair, String friendPublicKey, String encodedBibtexMsg) throws Exception {
        String hash = Hash.SHA1(DHTProtocol.PREFIX_MSG + friendPublicKey);
        PublicKey publicKey = RSA.toPublicKey(friendPublicKey);

        String encrypted = RSA.signAndPrefixPublicKey(encodedBibtexMsg, myKeyPair, publicKey);
        OpenDHT.put(ips, hash, encrypted, OpenDHT.MAX_TTL);
    }

    public List<BibtexMessage> getOfflineMessages(KeyPair keyPair) throws Exception {
        String publicKey = RSA.toBase64(keyPair)[1];
        String hash = Hash.SHA1(DHTProtocol.PREFIX_MSG + publicKey);

        String[] s = OpenDHT.get(ips, hash);
        List<BibtexMessage> result = new Vector<BibtexMessage>();
        for (String value : s) {

            String[] values = RSA.verifyWithPrefixPublicKey(value, keyPair.getPrivate());

            BibtexMessage msg = BibtexMessageCodec.fromString(values[0]);

            // make sure msg valid and ensure it is from the same as the signed key
            if (msg != null) {
                msg.setFriend(values[1]);
                result.add(msg);
            }
        }
        return result;
    }
}
