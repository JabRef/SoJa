package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.GUIGlobals;
import net.sf.jabref.Globals;
import net.sf.jabref.gui.FileListEntry;
import net.sf.jabref.gui.FileListTableModel;

/**
 * Request to download file from peer
 * @author Thien Rong
 */
public class DownloadRequest {

    public static void main(String[] args) throws IOException {
        BibtexEntry entry = new BibtexEntry();
        entry.setField(GUIGlobals.FILE_FIELD, "a::c;test:C\\:\\\\Users\\\\Vista\\\\TheHibernater\\\\Documents\\\\test.docx:Word;PDF:C\\:\\\\Users\\\\Vista\\\\TheHibernater\\\\Documents\\\\test.pptx:PDF");
        FileListTableModel model = new FileListTableModel();
        
    }
    long currentByte;
    String BUID;
    String filePath;

    public DownloadRequest(long currentByte, String BUID, String filePath) {
        this.currentByte = currentByte;
        this.BUID = BUID;
        this.filePath = filePath;
    }

    public String toSocketString() {
        return currentByte + Globals.NEWLINE + BUID + Globals.NEWLINE + filePath;
    }

    public static DownloadRequest fromSocketString(String socketString) {
        BufferedReader br = new BufferedReader(new StringReader(socketString));
        List<String> data = new ArrayList<String>();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                data.add(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (data.size() != 3) {
            return null;
        }
        return new DownloadRequest(Long.parseLong(data.get(0)), data.get(1), data.get(2));
    }

    public String getBUID() {
        return BUID;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getCurrentByte() {
        return currentByte;
    }

    public void setCurrentByte(long currentByte) {
        this.currentByte = currentByte;
    }
}