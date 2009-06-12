package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import model.DownloadRequest;
import model.DownloadResponse;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.JabRefFrame;
import util.FrameUtil;
import util.ObjectCleaner;

/**
 *
 * @author Thien Rong
 */
public class FileDealer extends Thread {

    JabRefFrame frame;
    int filePort;
    ServerSocket fileSocket;
    // friends requests to my file. <requestID, DownloadRequest>
    Map<String, DownloadRequest> peerRequests = new HashMap<String, DownloadRequest>();
    // <requestID, FileDownloader> storing my requests to others
    Map<String, FileDownloader> myRequests = new HashMap<String, FileDownloader>();
    boolean active = true;
    static final long INVALID_FILE = -1;
    static final int BUF_SIZE = 8192;

    public FileDealer(JabRefFrame frame, ServerSocket fileSocket, int filePort) {
        this.frame = frame;
        this.fileSocket = fileSocket;
        this.filePort = filePort;
        this.peerRequests = Collections.synchronizedMap(peerRequests);
        this.myRequests = Collections.synchronizedMap(myRequests);
    }

    public static FileDealer openFileDealer(JabRefFrame frame, int filePort) throws IOException {
        ServerSocket fileSocket = new ServerSocket(filePort);
        return new FileDealer(frame, fileSocket, filePort);
    }

    /**
     * @param requestID
     * @param request
     * @return downloadResponse
     */
    public DownloadResponse handleDownloadRequest(String requestID, DownloadRequest request) {
        long totalSize = INVALID_FILE;
        String errMsg = DownloadResponse.ERR_NO_ERROR;

        File validatedFile = FrameUtil.checkFileExists(frame, request.getFilePath());
        if (validatedFile == null) {
            errMsg = DownloadResponse.ERR_FILE_NOT_FOUND;
            return new DownloadResponse(totalSize, filePort, errMsg);
        }

        BibtexEntry entry = FrameUtil.getEntryWithBUID(request.getBUID(), frame);
        if (entry == null) {
            errMsg = DownloadResponse.ERR_BUID_NOT_FOUND;
            return new DownloadResponse(totalSize, filePort, errMsg);
        }

        if (false == FrameUtil.validateFileLink(frame, entry, request.getFilePath())) {
            errMsg = DownloadResponse.ERR_FILEPATH_NOT_FOUND;
            return new DownloadResponse(totalSize, filePort, errMsg);
        }

        // if valid, update fullPath and totalSize
        totalSize = validatedFile.length();
        request.setFilePath(validatedFile.getAbsolutePath());
        peerRequests.put(requestID, request);
        return new DownloadResponse(totalSize, filePort, errMsg);
    }

    public void run() {
        while (active) {
            try {
                final Socket peerSocket = fileSocket.accept();
                if (false == active) {
                    return;
                }

                handleFileSocket(peerSocket);
            } catch (IOException ex) {
                ex.printStackTrace();
                active = false;
            }
        }
    }

    private void handleFileSocket(final Socket peerSocket) {

        new Thread() {

            public void run() {
                InputStream in = null;
                FileInputStream fileIn = null;
                OutputStream out = null;
                try {
                    in = peerSocket.getInputStream();
                    out = peerSocket.getOutputStream();
                    ObjectInputStream in2 = new ObjectInputStream(in);

                    // get friend id 1st
                    System.out.println("reading request ID for download");
                    String requestID = (String) in2.readObject();
                    System.out.println("sending download with request ID");
                    DownloadRequest request = peerRequests.remove(requestID);
                    if (request != null) {
                        byte[] buf = new byte[BUF_SIZE];
                        fileIn = new FileInputStream(request.getFilePath());
                        int nRead;
                        while ((nRead = fileIn.read(buf)) != -1) {
                            out.write(buf, 0, nRead);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    ObjectCleaner.cleanCloseable(in, out, fileIn);
                }
            }
        }.start();
    }

    //////////////////// DELEGATE TO FILE DOWNLOADER ///////////////////////////////////
    public void addDownloader(String requestID, FileDownloader downloader) {
        myRequests.put(requestID, downloader);
    }

    public FileDownloader removeDownloader(String requestID) {
        return myRequests.remove(requestID);
    }
}

