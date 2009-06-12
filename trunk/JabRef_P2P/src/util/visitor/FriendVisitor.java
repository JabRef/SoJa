/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.visitor;

import model.friend.Friend;

/**
 *
 * @author Thien Rong
 */
public interface FriendVisitor {

    void visitFriend(Friend f);
}
