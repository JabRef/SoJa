package util;

import model.friend.Friend;

/**
 * Convert string to friend, for adding friend using text paste easily
 * @author Thien Rong
 */
public class FriendStringCodec {

    public static void main(String[] args) {
        System.out.println(fromString(toString(fromString("Joe\n127.0.0.1\n5050\n5051"))));
    }

    public static Friend fromString(String str) {
        String[] data = str.split("\n");
        if (data.length != 4) {
            return null;
        }

        return new Friend(data[0], data[0], data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3]));
    }

    public static String toString(Friend f) {
        return f.getName() + "\n" + f.getIp() + "\n" + f.getPort() + "\n" + f.getFilePort();
    }
}
