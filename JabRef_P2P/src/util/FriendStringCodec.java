package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.friend.Friend;
import util.security.Hash;

/**
 * Convert string to friend, for adding friend using text paste easily
 * @author Thien Rong
 */
public class FriendStringCodec {

    public static void main(String[] args) {
        String xx = "Joe\n172.22.130.33\n8010\n8011\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCiJiHjEhGF/bj76J2arhZeiMK4ulw+QMZ5rc2b1Foq1c1HEMmlvpAuFHjtF7SC05G1QsIK/O1pkyyPlcC25FGod7nzKvngOsbGWiENU2IF8+7bo+uRHg/w0dQEveSg2UrRjWKlPUrJJt1fibT2SyZRydA8M3KBtkqmUO+R0MMtlwIDAQA\n\n1";
        System.out.println(fromString(xx));
        //System.out.println(toString(fromString(toString(fromString("Joe\n127.0.0.1\n5050\n5051\ntestKey\n")))));
    }

    public static Friend fromString(String str) {
        BufferedReader br = new BufferedReader(new StringReader(str));
        String name = null, ip = null, publicKey = null, avatarURL = null, tags = null;
        int mainPort = 0, filePort = 0;
        String line;
        int i = 0;
        try {
            while ((line = br.readLine()) != null) {
                switch (i) {
                    case 0:
                        name = line;
                        break;
                    case 1:
                        ip = line;
                        break;
                    case 2:
                        mainPort = Integer.parseInt(line);
                        break;
                    case 3:
                        filePort = Integer.parseInt(line);
                        break;
                    case 4:
                        publicKey = line;
                        break;
                    case 5:
                        avatarURL = line;
                        break;
                    case 6:
                        tags = line;
                        break;
                }
                i++;
            }
            if (i < 5) {
                throw new Exception("Invalid friend data");
            }
        } catch (Exception ex) {
            Logger.getLogger(FriendStringCodec.class.getName()).log(Level.SEVERE, null, ex);
        }


        return new Friend(publicKey, name, ip, mainPort, filePort, avatarURL, tags);
    }

    public static String toString(Friend f) {
        return f.getName() + "\n" + f.getIp() + "\n" + f.getPort() + "\n" + f.getFilePort() + "\n" + f.getFUID() + "\n" + f.getAvatarURL() + "\n" + f.getTags();
    }

    public static String toSHA1PublicKey(Friend f) {
        return Hash.SHA1(f.getFUID());
    }
}
