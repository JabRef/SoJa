package util.thread;

import core.DataPacket;
import core.NetworkDealer;
import java.util.LinkedList;

/**
 *
 * @author Thien Rong
 */
public class SendDataThread extends Thread {

    NetworkDealer dealer;
    boolean active = true;
    int delay;

    public SendDataThread(NetworkDealer dealer, int delay) {
        this.dealer = dealer;
        this.delay = delay;
    }

    public void run() {
        LinkedList<DataPacket> queue = dealer.getQueue();
        while (active) {
            // sleep a while if empty
            // a better way is to do wait and notify
            if (queue.isEmpty()) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                }
            } else {
                DataPacket packet = queue.removeFirst();
                //System.out.println("dequeuing");
                dealer.sendData(packet);
            }
        }
    }
}
