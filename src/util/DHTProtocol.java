package util;

import model.friend.MyProfile;
import util.security.RSA;

/**
 * Used to store the keys @ lookup
 * @author Thien Rong
 */
public class DHTProtocol {

    public static void main(String[] args) throws Exception {
        System.out.println( toDHTProfile(new MyProfile("test", 5850, 5851, RSA.generateKeyPair())));
    }
    public static final String PREFIX_USERDETAIL = "jabrefpp_userdetail";
    public static final String PREFIX_USERLIST = "jabrefpp_userlist";
    public static final String PREFIX_MSG = "jabrefpp_msg";
    public static final String PREFIX_USERSTATUS = "jabrefpp_userstatus";
    // unimpl
    //public static final String PREFIX_USERMSG = "jabrefpp_usermsg";
    public static final String PREFIX_FOLLOWERS = "jabrefpp_followers";
    public static final String PREFIX_FEED = "jabrefpp_feed";
    
   
    public static String toDHTProfile(MyProfile profile) throws Exception {
        byte[] buf= RSA.encrypt(FriendStringCodec.toString(profile).getBytes("UTF-8"), RSA.toPrivateKey(profile.getPrivateKey()));
        return new String(buf);
    }
}
