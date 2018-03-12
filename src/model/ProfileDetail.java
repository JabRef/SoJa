package model;

import java.io.Serializable;
import java.util.Collection;
import model.friend.Friend;
import util.BibtexStringCodec;
import net.sf.jabref.BibtexEntry;

/**
 * Contains the friends and items shared,
 * to show when u view profile details
 * @TODO persist and implement profileView
 * @author Thien Rong
 */
public class ProfileDetail implements Serializable {

    int profileView;
    Collection<Friend> friends;
    String entriesStr;

    public ProfileDetail(int profileView, Collection<Friend> friends, String entriesStr) {
        this.profileView = profileView;
        this.friends = friends;
        this.entriesStr = entriesStr;
    }

    public String getEntriesStr() {
        return entriesStr;
    }

    public Collection<Friend> getFriends() {
        return friends;
    }

    public Collection<BibtexEntry> getEntry() {
        return BibtexStringCodec.fromStringList(entriesStr);
    }

    public int getProfileView() {
        return profileView;
    }
}
