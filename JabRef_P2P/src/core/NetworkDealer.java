package core;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import model.BibtexMessage;
import model.DownloadRequest;
import model.DownloadResponse;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.JabRefFrame;
import model.ProfileDetail;
import model.friend.Friend;
import model.friend.MyProfile;
import util.BibtexStringCodec;
import util.GlobalUID;
import util.ObjectCleaner;
import util.thread.SendDataThread;
import util.visitor.FriendVisitor;

/**
 * Server that handle requests from other peers
 */
public class NetworkDealer extends Thread {

    public static void main(String[] args) throws Exception {
        /*ByteArrayOutputStream bb = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bb);
        out.writeObject(new DataPacket("123", "a", "a", "a", null));

        ByteArrayInputStream bin = new ByteArrayInputStream(bb.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bin);
        DataPacket content = (DataPacket) in.readObject();
        System.out.println(content.object);
         */
    }
    private boolean active = true;
    private ServerSocket serverSocket;
    private Map<Friend, ObjectOutputStream> peerSockets = new HashMap<Friend, ObjectOutputStream>();
    private JabRefFrame frame;
    private SidePanel main;
    private FileDealer fileDealer;
    private LinkedList<DataPacket> queue = new LinkedList<DataPacket>();
    private static final String BIBTEX_MESSAGE = "00",  IM = "01", //
             SEARCH_REQUEST = "02",  SEARCH_RESULT = "03", //
             BROWSE_REQUEST = "04",  BROWSE_RESULT = "05",//
             DOWNLOAD_REQUEST = "06",  DOWNLOAD_RESPONSE = "07", //
             FRIEND_STATUS = "08", //
             PULL_RSS_REQUEST = "09",  PULL_RSS_RESULT = "10", //
             PROFILE_REQUEST = "11",  PROFILE_RESULT = "12", //
             FRIEND_REQUEST = "13",  ACCEPTED_FRIEND_REQUEST = "14";

    private NetworkDealer(JabRefFrame frame, ServerSocket serverSocket, FileDealer fileDealer, SidePanel main) {
        this.frame = frame;
        this.serverSocket = serverSocket;
        this.main = main;
        this.peerSockets = Collections.synchronizedMap(peerSockets);
        this.fileDealer = fileDealer;

        new SendDataThread(this, 500).start();
    }

    public static NetworkDealer openNetworkDealer(JabRefFrame frame, MyProfile myProfile, SidePanel main) {
        ServerSocket socket = null, fileSocket = null;
        main.setMyProfile(myProfile);
        try {
            //ServerSocket socket = new ServerSocket(port, 1,
            //        InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
            socket = new ServerSocket(myProfile.getPort());

            FileDealer fileDealer = FileDealer.openFileDealer(frame, myProfile.getFilePort());
            NetworkDealer listener = new NetworkDealer(frame, socket, fileDealer, main);

            listener.start();
            fileDealer.start();

            return listener;
        } catch (IOException e) {
            frame.showMessage(e.getMessage());
            if (!e.getMessage().startsWith("Address already in use")) {
                e.printStackTrace();
            }

            ObjectCleaner.cleanServerSockets(socket, fileSocket);
            return null;
        }
    }

    public void run() {
        while (active) {
            try {
                final Socket peerSocket = serverSocket.accept();
                if (false == active) {
                    return;
                }

                handlePeerSocket(peerSocket);
            } catch (IOException ex) {
                ex.printStackTrace();
                active = false;
            }
        }
    }

    public void stopThread() {
        active = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // need thread for the readobject
    private void handlePeerSocket(final Socket peerSocket) {
        new Thread() {

            public void run() {

                InputStream in = null;
                try {
                    in = peerSocket.getInputStream();
                    ObjectInputStream in2 = new ObjectInputStream(in);

                    // get profile of sender first
                    System.out.println(in2 + "trying peer");
                    MyProfile p = (MyProfile) in2.readObject();
                    System.out.println(in2 + "complete peer");
                    Friend friend = main.findFriend(p.getFUID());

                    if (friend == null) {
                        //frame.showMessage("Unable to find friend with id " + p.getFUID());
                        main.handleFriendRequest(p);
                    } else {
                        ObjectOutputStream out = new ObjectOutputStream(peerSocket.getOutputStream());
                        peerSockets.put(friend, out);
                        friend.setConnected(true);
                        handleDataPacket(friend, in2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ObjectCleaner.cleanCloseable(in);
                    ObjectCleaner.cleanSockets(peerSocket);

                }
            }
        }.start();
    }

    private void handleDataPacket(Friend friend, DataPacket packet) throws IOException {
        System.out.println("rx header = " + packet.header);
        //if (false == packet.header.equals(BIBTEX_MESSAGE) && false == packet.header.equals(SEARCH_RESULT)) {
        if (false) {
            System.out.println("rx msg = " + packet.msg);
        }

        if (BIBTEX_MESSAGE.equals(packet.header)) {
            BibtexMessage msg = (BibtexMessage) packet.object;
            main.handleRecvBibtexEntry(friend, msg);

        } else if (IM.equals(packet.header)) {
            main.handleRecvIM(friend, packet.msg);
        } else if (SEARCH_REQUEST.equals(packet.header)) {
            String query = (String) packet.msg;
            main.handleSearchRequest(friend, query, packet.requestID);
        } else if (SEARCH_RESULT.equals(packet.header)) {
            Collection<BibtexEntry> entries = BibtexStringCodec.fromStringList(packet.msg);
            if (entries != null) {
                main.handleSearchResult(friend, entries, packet.requestID);
            }
        } else if (BROWSE_REQUEST.equals(packet.header)) {
            Collection<BibtexEntry> entries = main.handleBrowseRequest(friend);
            this.sendResult(friend.getFUID(), entries, packet.requestID, BROWSE_RESULT);
        } else if (BROWSE_RESULT.equals(packet.header)) {
            Collection<BibtexEntry> entries = BibtexStringCodec.fromStringList(packet.msg);
            if (entries != null) {
                main.handleBrowseResult(friend, entries, packet.requestID);
            }

        } else if (DOWNLOAD_REQUEST.equals(packet.header)) {
            DownloadRequest request = DownloadRequest.fromSocketString(packet.msg);
            if (request == null) {
                frame.showMessage("Error Parsing Request " + packet.msg);
            } else {
                DownloadResponse response = fileDealer.handleDownloadRequest(packet.requestID, request);
                this.sendDownloadResponse(friend.getFUID(), response, packet.requestID);
            }
        } else if (DOWNLOAD_RESPONSE.equals(packet.header)) {
            DownloadResponse response = DownloadResponse.fromSocketString(packet.msg);
            if (response == null) {
                frame.showMessage("Error Parsing Response " + packet.msg);
            } else {
                FileDownloader d = fileDealer.removeDownloader(packet.requestID);
                if (d == null) {
                    frame.showMessage("Cannot find downloader for " + packet.requestID);
                } else {
                    d.handleDownloadResponse(response);
                }
            }

        } else if (FRIEND_STATUS.equals(packet.header)) {
            main.findFriend(friend.getFUID()).setCurrStatus(packet.msg);
        } else if (PULL_RSS_REQUEST.equals(packet.header)) {
            Collection<BibtexEntry> entries = main.handleBrowseRequest(friend);
            this.sendResult(friend.getFUID(), entries, packet.requestID, PULL_RSS_RESULT);
        } else if (PULL_RSS_RESULT.equals(packet.header)) {
            Collection<BibtexEntry> entries = BibtexStringCodec.fromStringList(packet.msg);
            if (entries != null) {
                main.handleSubscriptionUpdate(friend, entries);
            }
        } else if (PROFILE_REQUEST.equals(packet.header)) {
            Collection<BibtexEntry> entries = main.handleBrowseRequest(friend);
            String msg = BibtexStringCodec.toStringListForPeer(main.getFrame().basePanel(), entries, main.getMyProfile().getFUID(), null);
            // HashMap.values not serializable
            Collection<Friend> friends = new ArrayList<Friend>();
            friends.addAll(main.getFriendsModel().getFriends());
            this.sendData(friend.getFUID(), PROFILE_RESULT, "",
                    new ProfileDetail(0, friends, msg), packet.requestID);
        } else if (PROFILE_RESULT.equals(packet.header)) {
            main.handleProfileResult(friend, (ProfileDetail) packet.object);
        } else if (FRIEND_REQUEST.equals(packet.header)) {
        } else if (ACCEPTED_FRIEND_REQUEST.equals(packet.header)) {
            main.handleAcceptedFriendRequest((Friend) packet.object);
        } else {
            throw new IOException("Unknown header " + packet.header + "\n\n" + packet.msg);
        }
    }

    public void sendCurrStatus(String FUID, String newStatus) {
        this.sendData(FUID, FRIEND_STATUS, newStatus);
    }

    public void sendFriendRequest(Friend friend) {
        // String name, String FUID, String ip, int mainPort, int filePort
        this.sendData(friend.getFUID(), FRIEND_REQUEST, null, main.getMyProfile());
    }

    /**
     * Never really call sendDownload but delegate to FileDownloader
     * @param friend
     * @param request
     */
    public void sendDownloadRequest(Friend friend, BibtexEntry entry, DownloadRequest request) {
        FileDownloader downloader = new FileDownloader(main, entry, request, friend);
        downloader.sendDownloadRequest();
    }

    /**
     * Called by fileDownloader
     * @param FUID
     * @param request
     * @param requestID
     */
    public void sendDownloadRequest(String FUID, DownloadRequest request, String requestID, FileDownloader downloader) {
        fileDealer.addDownloader(requestID, downloader);
        this.sendData(FUID, DOWNLOAD_REQUEST, request.toSocketString(), requestID);
    }

    /**
     * should only be called by sendDataThread
     * @TODO caller to allow sending to unknown FUID
     * @param destFUID
     * @param header
     * @param msg
     * @param object
     * @param FUID
     */
    public void sendData(DataPacket packet) {
        // @TODO change to find friend that can help route
        Friend friend = main.findFriend(packet.destFUID);
        if (friend == null) {
            main.getFrame().showMessage("Routing is unsupported yet");
            return;
        }

        System.out.println("tx to = " + friend.getName() + ", tx header = " + packet.header);
        //System.out.println("tx msg = " + msg);

        ObjectOutputStream out = peerSockets.get(friend);
        try {
            out.writeObject(packet);
        } catch (Exception ex) {
            friend.setConnected(false);
            ObjectOutputStream useless = peerSockets.remove(friend);
            ObjectCleaner.cleanCloseable(useless);
            frame.showMessage("Connection to " + friend.getName() + " Lost. Please Retry again");
            ex.printStackTrace();
        }
    }

    /**
     * Auto-generate the packetID
     * @param FUID
     * @param header
     * @param msg
     */
    private void sendData(String FUID, String header, String msg) {
        sendData(FUID, header, msg, null);
    }

    private void sendData(String FUID, String header, String msg, Serializable object) {
        sendData(FUID, header, msg, object, GlobalUID.generate(FUID));
    }

    private void sendData(String FUID, String header, String msg, String packetID) {
        sendData(FUID, header, msg, null, packetID);
    }

    private void sendData(String FUID, String header, String msg, Serializable object, String packetID) {
        queueData(FUID, header, msg, object, packetID);
    }

    private void queueData(String destFUID, String header, String msg, Serializable object, String packetID) {
        queue.addLast(new DataPacket(destFUID, packetID, header, msg, object));
    }

    public synchronized void sendConnect(final Friend friend)
            throws UnknownHostException, IOException, ClassNotFoundException {
        Socket socket = new Socket(friend.getIp(), friend.getPort());

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        // send my id 1st before get inputstream
        out.writeObject(main.getMyProfile());
        peerSockets.put(friend, out);
        friend.setConnected(true);

        final ObjectInputStream in2 = new ObjectInputStream(socket.getInputStream());

        new Thread() {

            public void run() {
                try {
                    handleDataPacket(friend, in2);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

            }
        }.start();
    }

    public void sendMsg(String FUID, String msg) {
        sendData(FUID, IM, msg);
    }

    /**
     * Generate BUID before sending if necessary
     * @param FUID
     * @param entry
     * @throws java.io.IOException
     */
    public void sendBibtexEntry(String FUID, BibtexEntry entry) throws IOException {
        String msg = BibtexStringCodec.toStringForPeer(main.getFrame().basePanel(), entry, FUID);
        sendData(FUID, BIBTEX_MESSAGE, msg);
    }

    /**
     * Request for profile info
     * @param FUID
     * @param requestID
     */
    public void sendProfileRequest(String FUID, String requestID) {
        sendData(FUID, PROFILE_REQUEST, "", requestID);
    }

    /**
     * Use same id to recognize the request
     * @param query
     * @param requestID - unique ID to represent the query
     */
    public void sendSearchRequest(String query, String requestID) {

        for (Friend friend : getConnectedFriends()) {
            sendData(friend.getFUID(), SEARCH_REQUEST, query, requestID);
        }
    }

    /**
     * Generate BUID before sending if necessary
     * @param FUID
     * @param entry
     * @param requestID - send using the requestID of the search
     * @param responseType - SEARCH_RESULT or PULL_REQUEST_RESULT
     * @throws java.io.IOException
     */
    private void sendResult(String FUID, Collection<BibtexEntry> results, String requestID, String responseType) throws IOException {
        String msg = BibtexStringCodec.toStringListForPeer(main.getFrame().basePanel(), results, FUID, null);
        sendData(FUID, responseType, msg, requestID);
    }

    /**
     * Allow auto browse for tag cloud/ or manual browse
     * @param FUID
     * @param requestID null => auto browse for tag cloud and not manual browse
     */
    public void sendBrowseRequest(String FUID, String requestID) {
        if (requestID == null) {
            sendData(FUID, BROWSE_REQUEST, "");
        } else {
            sendData(FUID, BROWSE_REQUEST, "", requestID);
        }
    }

    public void sendSearchResult(String FUID, Collection<BibtexEntry> entries, String requestID) throws IOException {
        sendResult(FUID, entries, requestID, SEARCH_RESULT);
    }

    public void handleDataPacket(final Friend friend, final ObjectInputStream in2)
            throws IOException, ClassNotFoundException {
        while (active) {
            //System.out.println(in2 + "trying handleDataPacket");
            DataPacket packet = (DataPacket) in2.readObject();
            //System.out.println(in2 + "complete handleDataPacket");
            handleDataPacket(friend, packet);
        }

    }

    private void sendDownloadResponse(String FUID, DownloadResponse response, String requestID) {
        this.sendData(FUID, DOWNLOAD_RESPONSE, response.toSocketString(), requestID);
    }

    public void sendSubscriptionUpdateRequest(String FUID) {
        this.sendData(FUID, PULL_RSS_REQUEST, "");
    }

    public void queueBibtexMessage(String FUID, BibtexMessage msg) {
        this.sendData(FUID, BIBTEX_MESSAGE, null, msg);
    }

    public LinkedList<DataPacket> getQueue() {
        return queue;
    }

    public Set<Friend> getConnectedFriends() {
        return peerSockets.keySet();
    }
}

