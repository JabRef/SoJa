package model;

import core.DataPacket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @TODO not used yet. 
 * @author Thien Rong
 */
public class DataPacketQueue {

    // FUID -> Queue of DataPacket
    Map<String, LinkedList<DataPacket>> packets = new HashMap<String, LinkedList<DataPacket>>();

    public void queueUrgentPacket(String FUID, DataPacket packet) {
        getQueue(FUID).addFirst(packet);
    }

    public void queuePacket(String FUID, DataPacket packet) {
        getQueue(FUID).addLast(packet);
    }

    /**
     * Create if don't exists and return
     * @param FUID
     * @return
     */
    private LinkedList<DataPacket> getQueue(String FUID) {
        LinkedList<DataPacket> queue = packets.get(FUID);
        if (queue == null) {
            queue = new LinkedList<DataPacket>();
            packets.put(FUID, queue);
        }
        return queue;
    }
}
