package model.friend;

/**
 * For events in friends group so can know what group the friend is added to
 * @author Thien Rong
 */
public class FriendGroup {

    Friend f;
    Group g;

    public FriendGroup(Friend f, Group g) {
        this.f = f;
        this.g = g;
    }

    public Friend getF() {
        return f;
    }

    public Group getG() {
        return g;
    }
}
