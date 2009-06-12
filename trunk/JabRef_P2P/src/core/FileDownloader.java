package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import model.DownloadRequest;
import model.DownloadResponse;
import model.friend.Friend;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.GUIGlobals;
import net.sf.jabref.gui.FileListEntry;
import net.sf.jabref.gui.FileListTableModel;
import util.FrameUtil;
import util.GlobalUID;
import util.ObjectCleaner;

/**
 * Send request and handle response and start download if valid
 * @author Thien Rong
 */
public class FileDownloader {

    int triesLeft = 3;
    DownloadRequest request;
    SidePanel main;
    Friend friend;
    DownloadResponse response;
    File localFile;
    String requestID;
    BibtexEntry entry;

    public FileDownloader(SidePanel main, BibtexEntry entry, DownloadRequest request, Friend friend) {
        this.entry = entry;
        this.main = main;
        this.request = request;
        this.friend = friend;
        this.requestID = GlobalUID.generate(main.getMyProfile().getFUID());
    }

    private static File getNewFile(File saveDir, String filename) {
        boolean invalid = true;
        File finalFile = new File(saveDir, filename);
        int i = 1;
        while (invalid) {
            if (finalFile.exists()) {
                int indexOfDot = filename.lastIndexOf('.');
                String name, ext;
                if (indexOfDot >= 0) {
                    name = filename.substring(0, indexOfDot);
                    ext = filename.substring(indexOfDot, filename.length());
                } else {
                    name = filename;
                    ext = "";
                }
                finalFile = new File(saveDir, name + "(" + i + ")" + ext);
            } else {
                invalid = false;
            }
        }
        return finalFile;
    }

    /**
     * Can be retry or first try
     */
    public void sendDownloadRequest() {
        // get current byte, 0 if first try, else the size of the file
        if (null != localFile) {
            request.setCurrentByte(localFile.length());
        }

        triesLeft--;
        main.getDealer().sendDownloadRequest(friend.getFUID(), request, requestID, this);
    }

    public void handleDownloadResponse(DownloadResponse response) {
        if (response.getTotalSize() == FileDealer.INVALID_FILE) {
            main.getFrame().showMessage(response.getErrMsg());
        } else {
            this.tryDownload();
        }

        this.response = response;
    }

    private OutputStream getOutputStream() throws FileNotFoundException {
        if (localFile == null) {
            File saveDir = FrameUtil.getSaveDirectory(main.getFrame());
            String fileName = new File(request.getFilePath()).getName();
            localFile = getNewFile(saveDir, stripInvalidChar(fileName));
        } else {
            request.setCurrentByte(localFile.length());
        }

        return new FileOutputStream(localFile);
    }

    public void tryDownload() {
        new Thread() {

            public void run() {
                long totalSize = response.getTotalSize();
                byte[] buf = new byte[FileDealer.BUF_SIZE];

                Socket downloaderSocket = null;
                ObjectOutputStream out = null;
                OutputStream fileOut = null;
                InputStream in = null;
                try {
                    fileOut = getOutputStream();

                    System.out.println("trying to connect to fileDealer");
                    downloaderSocket = new Socket(friend.getIp(), response.getPort());
                    System.out.println("connected");
                    out = new ObjectOutputStream(downloaderSocket.getOutputStream());
                    in = downloaderSocket.getInputStream();

                    // send to peer fileDealer what request ID to get
                    System.out.println("trying to send requestID");
                    out.writeObject(requestID);
                    System.out.println("complete sending requestID");
                    while (request.getCurrentByte() < totalSize) {
                        int nRead = in.read(buf);
                        fileOut.write(buf, 0, nRead);
                        request.setCurrentByte(request.getCurrentByte() + nRead);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    ObjectCleaner.cleanCloseable(out, in, fileOut);
                    ObjectCleaner.cleanSockets(downloaderSocket);
                }

                // request again since not all received and if enough tries
                if (request.getCurrentByte() < totalSize && triesLeft > 0) {
                    sendDownloadRequest();
                } else {
                    if (triesLeft <= 0) {
                        localFile.delete();
                        main.getFrame().showMessage("Failed download too many times trying to get " + request.getFilePath());
                    } else {
                        main.getFrame().showMessage("download for " + localFile.getAbsolutePath() + " complete");
                        updateNewLink(request.getFilePath(), localFile.getAbsolutePath());
                    }
                }
            }
        }.start();
    }

    private void updateNewLink(String prevLink, String newLink) {
        FileListTableModel model = new FileListTableModel();
        String links = entry.getField(GUIGlobals.FILE_FIELD);
        if (links == null) {
            return;
        }
        model.setContent(links);
        for (int i = 0; i < model.getRowCount(); i++) {
            FileListEntry flEntry = model.getEntry(i);
            if (flEntry.getLink().equals(prevLink)) {
                flEntry.setLink(newLink);
            }
        }
        entry.setField(GUIGlobals.FILE_FIELD, model.getStringRepresentation());
    }

    /**     
     * @param fileName
     * @return the filename without invalid char for windows (\/:*?"<>|)
     */
    private String stripInvalidChar(String fileName) {
        return fileName.replaceAll("[\\/:\\*\\?\\\"<>|]", "");
    }
}