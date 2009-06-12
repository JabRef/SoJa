package model.friend;

import model.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import util.visitor.FriendVisitor;

/**
 * @author Thien Rong
 */
public class Group implements Serializable {

    String name;
    List<Friend> friends = new ArrayList<Friend>();

    public Group(String name) {
        this.name = name;
    }

    public void addFriend(Friend friend) {
        friends.add(friend);
    }

    public boolean remove(Friend f) {
        return friends.remove(f);
    }

    public Friend getFriend(int index) {
        return friends.get(index);
    }

    public int getFriendCount() {
        return friends.size();
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public int getFriendIndex(Friend f) {
        for (int i = 0; i < friends.size(); i++) {
            if (f.equals(friends.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public void visitFriend(FriendVisitor v) {
        for (Friend friend : friends) {
            v.visitFriend(friend);
        }
    }

    @Override
    public String toString() {
        return name;// + " (" + friends.size() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Group other = (Group) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    /**
     * For XMLEncoder. DO NOT USE
     */
    public Group() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }
}