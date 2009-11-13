package view.friend;

import java.util.Set;
import java.util.TreeSet;
import model.friend.Friend;

/**
 * String but with ttl so can sort order status of multiple people
 * @author Thien Rong
 */
public class Status implements Comparable<Status> {

    public static void main(String[] args) {
        Set<Status> test = new TreeSet<Status>();
        test.add(new Status("99", 99));
        test.add(new Status("88a", 88));
        test.add(new Status("88b", 88));
        test.add(new Status("777", 777));

        for (Status status : test) {

            System.out.println(status);
        }
    }
    // just for getting name
    Friend friend;
    String status;
    int ttl;

    public Status(String status, int ttl) {
        this.status = status;
        this.ttl = ttl;
    }

    public int compareTo(Status o) {
        if (o.ttl == ttl) {
            int c = status.compareTo(o.status);
            return c;
        } else if (o.ttl < ttl) {
            return 1;
        }
        return -1;
    }

    public String toString() {
        return status + "/" + ttl;
    }

    public String getStatus() {
        return status;
    }

    public int getTtl() {
        return ttl;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }
}
