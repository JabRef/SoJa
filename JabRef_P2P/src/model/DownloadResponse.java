package model;

/**
 *
 * @author Thien Rong
 */
public class DownloadResponse {

    long totalSize;
    int port;
    // make sure don't have DELIM inside
    String errMsg;
    public static final String ERR_FILE_NOT_FOUND = "File Not Found on peer machine";
    public static final String ERR_BUID_NOT_FOUND = "Bibtex Entry Not Found on peer machine";
    public static final String ERR_FILEPATH_NOT_FOUND = "File for entry Not Found on peer machine ";
    public static final String ERR_NO_ERROR = " ";// not empty else split will have 1 less field
    static final String DELIM = "*";

    public DownloadResponse(long totalSize, int port, String errMsg) {
        this.totalSize = totalSize;
        this.port = port;
        this.errMsg = errMsg;
    }

    public String toSocketString() {
        return totalSize + DELIM + port + DELIM + errMsg;
    }

    public static DownloadResponse fromSocketString(String socketString) {
        String[] data = socketString.split("\\" + DELIM);
        if (data.length != 3) {
            return null;
        }
        return new DownloadResponse(Long.parseLong(data[0]), Integer.parseInt(data[1]), data[2]);
    }

    public String getErrMsg() {
        return errMsg;
    }

    public int getPort() {
        return port;
    }

    public long getTotalSize() {
        return totalSize;
    }


}
