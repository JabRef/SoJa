package util;

import java.io.BufferedReader;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * 0.13 | 12/25/2008
 * + WebResponse : add String content so if writeToContent will be
filled and can reuse
 * + writeToContent allow reuse else only once
 * + Change useragent to FF 3.05
 * 0.12 | 12/24/2008
 * + Cannot use URLConnection since if cache url will be wrong
 * + Simple WebResponse
 * 0.11 | 12/23/2008
 * + getConn(), getContent() added additional param to set time to
cache(force) or -1 don't cache
 * 0.10 | 12/19/2008
 * ** HttpURLConnection by default already chunked **
 * https://svn.apache.org/repos/asf/webservices/axis/tags/axis1_1beta/java/src/org/apache/axis/transport/http/ChunkedInputStream.java
 * + getCorrectInputStream convert param to String transfer, String
content so other class can use
 * + Add chunkedInputStream (Shared by other class) so can handle them
 * 0.9 | 12/14/2008
 * + implemented ResponseCache with example from web
 * + https://svn.concord.org/svn/projects/trunk/common/java/otrunk/otrunk-udl/src/main/java/org/concord/otrunk/udl/util/TemporaryResponseCache.java
 * 0.8 | 11/22/2008
 * + Merge Log.info to 1 instead of multiple
 * + Generics for debug since 1.5 now
 * 0.7 | 11/10/2008
 * + update If to ver 0.3
 * 0.6 | 11/4/2008
 * + Remove catch IOException in writeToString
 * + Update If to v0.2
 * + Logger instead of sout
 * 0.5 | 10/16/2008
 * + getContent will throw at any exception (ie 404)
 * 0.4 | 10/7/2008
 * + disable followRedirect by default
 * + the location use new URL(curr, newURL) so that relative redirect works
 * 0.3 | 9/22/2008
 * + handle 3xx and auto redirect with the correct headers
 * 0.2 | 5/28/2008
 * + disable follow redirect because no proper header (user-agent: java)
 * 0.1 | 04 May 08
 * + merge dependency from util
 * + If v0.3
 * + Base64
 *
 * now use generics so 1.5
 * http for 1.4 compability
 * without Proxy, StringBuilder, If guessEncoding
 * @author Thien Rong
 */
public class HTTP14 {

    public static void main(String[] args) {
        try {
            /*String url = "http://www.msn.com";
            long sTime = System.currentTimeMillis();

            HTTP14.getContent(url, HTTP14.GET, null, null);
            System.out.println(System.currentTimeMillis() - sTime);
            sTime = System.currentTimeMillis();

            System.out.println("" + HTTP14.getConn(url, HTTP14.GET,
            null, null).getContentLength());
            System.out.println(System.currentTimeMillis() - sTime);
            sTime = System.currentTimeMillis();
             */            
            System.out.println(HTTP14.getContent("http://www.test.com", HTTP14.GET, null, null));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static final String GET = "GET",  POST = "POST";

    public static String debug(WebResponse resp) throws IOException {
        StringBuilder sb = new StringBuilder();

        for (Entry<String, List<String>> entries : resp.getRqstHeader().entrySet()) {
            sb.append(entries.getKey() + ": " + entries.getValue() + "\n");
        }
        Logger.getLogger(HTTP14.class.getName()).log(Level.INFO,
                resp.getResponseMessage(), sb.toString());
        return writeToString(resp, "UTF-8");
    }

    public static WebResponse getConn(String url, String method,
            String params, String headers) throws MalformedURLException,
            IOException, Exception {
        return getConn(url, method, params, headers, -1);
    }

    public static WebResponse getConn(String url, String method,
            String params, String headers, long cacheTime) throws
            MalformedURLException, IOException, Exception {
        HTTP14Worker2 worker = new HTTP14Worker2();
        return worker.getConn(url, method, params, headers, cacheTime, 0);
    }

    public static String getContent(String url, String method, String params, String headers) throws Exception {
        return getContent(url, method, params, headers, 0);
    }
    
    public static String getContent(String url, String method, String params, String headers, int readTimeout) throws Exception {
        return getContent(url, method, params, headers, -1, readTimeout);
    }



    public static String getContent(String url, String method, String params, String headers, long cacheTime, int readTimeout) throws Exception {
        StringBuffer sb = new StringBuffer(4096);

        HTTP14Worker2 worker = new HTTP14Worker2();
        WebResponse resp = worker.getConn(url, method, params,
                headers, (cacheTime == -1) ? -1 : System.currentTimeMillis() + cacheTime, readTimeout);

        InputStream in = getCorrectInputStream(resp);
        byte[] totalBytes = new byte[4096];
        int bytedata = -1;

        while ((bytedata = in.read(totalBytes)) != -1) {
            sb.append(new String(totalBytes, 0, bytedata));
        }
        in.close();

        worker = null;

        return sb.toString();
    }

    public static String writeToString(WebResponse resp, String encoding) throws IOException {
        String content = resp.getContent();
        if (content == null) {
            StringBuffer sb = new StringBuffer();
            InputStreamReader r = new InputStreamReader(getCorrectInputStream(resp), encoding);
            char[] buffer = new char[4096];
            int nReads;

            while ((nReads = r.read(buffer)) != -1) {
                sb.append(buffer, 0, nReads);
            }
            r.close();
            resp.setContent(sb.toString());
            return sb.toString();
        } else {
            return content;
        }
    }

    public static String genBasicAuth(String userinfo) {
        String auth = "Authorization: Basic ";

        return auth + Base64.encode(userinfo);
    }

    public static String genBasicAuth(String username, String password) {
        return genBasicAuth(username + ':' + password);
    }

    public static InputStream getCorrectInputStream(WebResponse resp)
            throws IOException {
        return getCorrectInputStream(resp.getInputStream(), null,
                resp.getRespField("Content-Encoding"));
    }

    public static InputStream getCorrectInputStream(InputStream in,
            String transferEncoding, String compressEncoding) throws IOException {
        if (transferEncoding != null &&
                transferEncoding.equalsIgnoreCase("chunked")) {
            in = new ChunkedInputStream(in);
        }
        if (compressEncoding != null && compressEncoding.indexOf("gzip") >= 0) {
            in = new GZIPInputStream(in);
        }

        return in;
    }

    // assume data till </head> not needed
    // read until </head> then look for <meta http-equiv="Content-Type" content  = "text/html; charset=big5" >
    public static String guessEncoding(InputStream in) throws IOException {
        If encodingIf = new If(null, "charset=", "\"");
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuffer sb = new StringBuffer();
        String line;
        // read until end or </head>
        while ((line = r.readLine()) != null && line.indexOf("</head>") == -1) {
            sb.append(line).append("\r\n");
        }
        return encodingIf.process(sb.toString());
    }

    public static String parseList(List<String> values) {
        if (values == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        for (String v : values) {
            sb.append(v).append(";");
        }
        sb.setLength(sb.length() - 1);

        return sb.toString();
    }

    /**
     * 0.1 | 12/24/2008
     * + Wrapper for both URLConnection or CacheResponse
     * + Needed also to wrap inputstream so when cache can write back
     */
    public static class WebResponse {

        private Map<String, List<String>> rqstHeader;
        private Map<String, List<String>> respHeader;
        private InputStream inputStream;
        private String content;
        private URL url;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public WebResponse(CacheResponse resp, Map<String, List<String>> rqstHeader, URL u) throws Exception {
            this.url = u;
            this.rqstHeader = rqstHeader;
            this.respHeader = resp.getHeaders();
            this.inputStream = resp.getBody();
        }

        public WebResponse(HttpURLConnection conn, final CacheRequest rqst, Map<String, List<String>> rqstHeader, URL u) throws Exception {
            this.url = u;
            this.rqstHeader = rqstHeader;
            this.respHeader = conn.getHeaderFields();
            if (rqst == null) {
                this.inputStream = conn.getInputStream();
            } else {
                this.inputStream = new FilterInputStream(conn.getInputStream()) {

                    @Override
                    public int read() throws IOException {
                        int b = in.read();
                        rqst.getBody().write(b);
                        return b;
                    }

                    @Override
                    public int read(byte b[], int off, int len) throws
                            IOException {
                        int nRead = in.read(b, off, len);
                        if (nRead != -1) {
                            rqst.getBody().write(b, off, nRead);
                        }
                        return nRead;
                    }

                    @Override
                    public void close() throws IOException {
                        in.close();
                        rqst.getBody().close();
                    }

                    @Override
                    public long skip(long n) throws IOException {
                        rqst.abort();
                        return in.skip(n);
                    }
                };
            }
        }

        public Map<String, List<String>> getRespHeader() {
            return respHeader;
        }

        public Map<String, List<String>> getRqstHeader() {
            return rqstHeader;
        }

        public String getRespField(String key) {
            // case-insensitive search header (sun.www.net also do this)
            for (Entry<String, List<String>> entry : respHeader.entrySet()) {
                if (key.equalsIgnoreCase(entry.getKey())) {
                    return parseList(respHeader.get(key));
                }
            }
            return null;
        }

        public int getRespFieldInt(String key, int deft) {
            String value = getRespField(key);
            int intValue = deft;

            if (value == null) {
                return intValue;
            }

            try {
                intValue = Integer.parseInt(value);
            } catch (Exception e) {

                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                        e.getMessage(), e);
            }
            return intValue;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public String getResponseMessage() {
            return parseList(respHeader.get(null));
        }

        public URL getURL() {
            return url;
        }
    }

    /**
     * 0.1 | 12/19/2008
     * + Release (see top for changes)
     */
    public static class ChunkedInputStream extends FilterInputStream {

        protected long chunkSize = 0l;
        protected volatile boolean closed = false;
        private static final int maxCharLong =
                (Long.toHexString(Long.MAX_VALUE)).toString().length();

        private ChunkedInputStream() {
            super(null);
        }

        public ChunkedInputStream(InputStream is) {
            super(is);
        }

        @Override
        public int read()
                throws IOException {
            byte[] d = new byte[1];
            int rc = read(d, 0, 1);

            return rc > 0 ? d[0] : rc;
        }

        @Override
        public int read(byte[] b)
                throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public synchronized int read(byte[] b,
                int off,
                int len)
                throws IOException {
            if (closed) {
                return -1;
            }
            int cs = (int) Math.min(Integer.MAX_VALUE, chunkSize);
            int totalread = 0;
            int bytesread = 0;

            try {
                do {
                    if (chunkSize < 1L) {
                        if (0l == getChunked()) {
                            if (totalread == 0) {
                                return -1;
                            } else {
                                return totalread;
                            }
                        }
                    }
                    bytesread = in.read(b, off + totalread,
                            Math.min(len - totalread,
                            (int) Math.min(chunkSize, Integer.MAX_VALUE)));
                    if (bytesread > 0) {
                        totalread += bytesread;
                        chunkSize -= bytesread;
                    }
                } while (len - totalread > 0 && bytesread > -1);
            } catch (IOException e) {
                closed = true;
                throw e;
            }
            return totalread;
        }

        @Override
        public long skip(final long n)
                throws IOException {
            if (closed) {
                return 0;
            }
            long skipped = 0l;
            byte[] b = new byte[1024];
            int bread = -1;

            do {
                bread = read(b, 0, b.length);
                if (bread > 0) {
                    skipped += bread;
                }
            } while (bread != -1 && skipped < n);
            return skipped;
        }

        @Override
        public int available()
                throws IOException {
            if (closed) {
                return 0;
            }
            int rc = (int) Math.min(chunkSize, Integer.MAX_VALUE);

            return Math.min(rc, in.available());
        }

        protected long getChunked() throws IOException {
            //StringBuffer buf= new StringBuffer(1024);
            byte[] buf = new byte[maxCharLong + 2];
            int bufsz = 0;

            chunkSize = -1L;
            int c = -1;

            do {
                c = in.read();
                if (c > -1) {
                    if (c != '\r' && c != '\n' && c != ' ' && c != '\t') {
                        buf[bufsz++] = ((byte) c);
                    }
                }
            } while (c > -1 && (c != '\n' || bufsz == 0) && bufsz < buf.length);
            if (c < 0) {
                closed = true;
            }
            String sbuf = new String(buf, 0, bufsz);

            if (bufsz > maxCharLong) {
                closed = true;
                throw new IOException("Chunked input stream failed to receive valid chunk size:" + sbuf);
            }
            try {
                chunkSize = Long.parseLong(sbuf, 16);
            } catch (NumberFormatException ne) {
                closed = true;
                throw new IOException("'" + sbuf + "' " + ne.getMessage());
            }
            if (chunkSize < 1L) {
                closed = true;
            }
            if (chunkSize != 0L && c < 0) {
                //If chunk size is zero try and be tolerant that there
                //maybe no cr or lf at the end.
                throw new IOException("HTTP Chunked stream closed in middle of chunk.");
            }
            if (chunkSize < 0L) {
                throw new IOException("HTTP Chunk size received " +
                        chunkSize + " is less than zero.");
            }
            return chunkSize;
        }

        @Override
        public void close() throws IOException {

            synchronized (this) {
                if (closed) {
                    return;
                }
                closed = true;
            }

            byte[] b = new byte[1024];
            int bread = -1;

            do {
                bread = read(b, 0, b.length);
            } while (bread != -1);
        }

        @Override
        public void reset()
                throws IOException {
            throw new IOException("Don't support marked streams");
        }

        @Override
        public boolean markSupported() {
            return false;
        }
    }

    static class HTTP14Worker2 {

        boolean followRedirect = false;
        private String params;
        // kept for redirect
        private long expiryTime;
        //private HttpURLConnection client;
        private URL u;
        private String method;
        private Map<String, List<String>> parsedRqstHeader = new HashMap<String, List<String>>();
        // headers must be \r\n separated
        // proxy code not using different proxy -> use 1.4 v

        public void init(String url, String method, String params,
                String headers, long expiryTime) throws MalformedURLException,
                IOException {
            this.method = method;
            if (method.equals(HTTP14.GET) && params != null) {
                u = new URL(url + '?' + params); // dont set params
            } else {
                u = new URL(url);
                this.params = params;
            }

            parsedRqstHeader.put("Host", Arrays.asList(u.getHost().toString()));
            parsedRqstHeader.put("User-Agent", Arrays.asList("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv: 1.9.0.5) Gecko/2008120122 Firefox/3.0.5(.NET CLR 3.5.30729)"));
            parsedRqstHeader.put("Accept",
                    Arrays.asList("text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"));
            parsedRqstHeader.put("Accept-Charset",
                    Arrays.asList("utf-8;q=0.9,*;q=0.9"));
            parsedRqstHeader.put("Accept-Encoding", Arrays.asList("gzip"));
            //parsedRqstHeader.put("Connection", Arrays.asList("close"));
            parsedRqstHeader.put("Connection", Arrays.asList("keep-alive"));
            parsedRqstHeader.put("Keep-Alive", Arrays.asList("300"));
            parsedRqstHeader.put("Accept-Language",
                    Arrays.asList("en-us,en;q=0.5"));


            if (method.equals(HTTP14.POST)) {
                parsedRqstHeader.put("Content-Type",
                        Arrays.asList("application/x-www-form-urlencoded"));
            }
            this.expiryTime = expiryTime;

            if (headers != null) {
                parseHeaders(headers);
            }
        }

        private void parseHeaders(String headers) {
            BufferedReader headerIn = new BufferedReader(new StringReader(headers));
            String line;
            try {
                while ((line = headerIn.readLine()) != null &&
                        line.length() > 0) {
                    int index = line.indexOf(':');
                    parsedRqstHeader.put(line.substring(0, index),
                            Arrays.asList(line.substring(index + 2))); // +2 because of extra space

                }
                headerIn.close();
            } catch (IOException ex) {

                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                        "parseHeaders", ex);
            }
        }

        private WebResponse checkCache() throws Exception {
            if (expiryTime == -1) {
                return null;
            }

            return null;
        }

        private HttpURLConnection initWebConn() throws Exception {
            HttpURLConnection client = (HttpURLConnection) u.openConnection();
            client.setInstanceFollowRedirects(false);
            client.setRequestMethod(method);

            for (Entry<String, List<String>> entry : parsedRqstHeader.entrySet()) {
                client.setRequestProperty(entry.getKey(),
                        parseList(entry.getValue()));
            }

            if (params != null) {
                client.setDoOutput(true);
                OutputStream out = client.getOutputStream();
                out.write(params.getBytes());
                out.flush();
                out.close();
            }
            return client;
        }

        public CacheRequest storeCache(HttpURLConnection client)
                throws Exception {
            return null;
        }

        public WebResponse getConn(String url, String method, String params, String headers, long expiryTime, int readTimeout) throws Exception {
            init(url, method, params, headers, expiryTime);
            // from cache if possible
            WebResponse resp = checkCache();
            if (resp != null) {
                return resp;
            }

            // get from web
            HttpURLConnection client = initWebConn();
            client.setReadTimeout(readTimeout);
            client.setConnectTimeout(readTimeout);
            if (followRedirect) {
                // handle redirect with new url and null param
                while (client.getResponseCode() / 100 == 3) {
                    // log first
                    StringBuilder sb = new StringBuilder();
                    for (Entry<String, List<String>> entries : client.getHeaderFields().entrySet()) {
                        sb.append(entries.getKey() + ": " +
                                entries.getValue() + "\n");
                    }

                    Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                            "FollowRedirect : " + client.getResponseMessage(), sb.toString());

                    // stop if no location
                    String newURL = client.getHeaderField("location");
                    newURL = new URL(client.getURL(), newURL).toString();
                    if (newURL == null || newURL.length() == 0) {
                        break;
                    }

                    headers = (headers == null) ? "" : headers + "\r\n";
                    headers += "Referer: " + client.getURL();

                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "New header: " + headers);

                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Trying to redirect to " + newURL);
                    init(newURL, HTTP14.GET, null, headers, expiryTime);
                }
            }

            // store to cache if needed
            CacheRequest rqst = storeCache(client);
            return new WebResponse(client, rqst, parsedRqstHeader, u);
        }
    }
}

class Base64 {

    final static String baseTable =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    /**
     * Encode a byte array.
     *
     * @param bytes a byte array to be encoded.
     * @return encoded object as a String object.
     */
    public static String encode(byte[] bytes) {

        StringBuffer tmp = new StringBuffer();
        int i = 0;
        byte pos;

        for (i = 0; i < (bytes.length - bytes.length % 3); i += 3) {

            pos = (byte) ((bytes[i] >> 2) & 63);
            tmp.append(baseTable.charAt(pos));

            pos = (byte) (((bytes[i] & 3) << 4) + ((bytes[i + 1] >> 4) & 15));
            tmp.append(baseTable.charAt(pos));

            pos = (byte) (((bytes[i + 1] & 15) << 2) + ((bytes[i + 2] >> 6) & 3));
            tmp.append(baseTable.charAt(pos));

            pos = (byte) (((bytes[i + 2]) & 63));
            tmp.append(baseTable.charAt(pos));

            // Add a new line for each 76 chars.
            // 76*3/4 = 57
            if (((i + 2) % 56) == 0) {
                tmp.append("\r\n");
            }
        }

        if (bytes.length % 3 != 0) {

            if (bytes.length % 3 == 2) {

                pos = (byte) ((bytes[i] >> 2) & 63);
                tmp.append(baseTable.charAt(pos));

                pos = (byte) (((bytes[i] & 3) << 4) + ((bytes[i + 1] >> 4) & 15));
                tmp.append(baseTable.charAt(pos));

                pos = (byte) ((bytes[i + 1] & 15) << 2);
                tmp.append(baseTable.charAt(pos));

                tmp.append("=");

            } else if (bytes.length % 3 == 1) {

                pos = (byte) ((bytes[i] >> 2) & 63);
                tmp.append(baseTable.charAt(pos));

                pos = (byte) ((bytes[i] & 3) << 4);
                tmp.append(baseTable.charAt(pos));

                tmp.append("==");
            }
        }
        return tmp.toString();

    }

    /**
     * Encode a String object.
     *
     * @param src a String object to be encoded with Base64 schema.
     * @return encoded String object.
     */
    public static String encode(String src) {

        return encode(src.getBytes());
    }

    public static String decodeAsStr(String src) {
        return new String(decode(src));
    }

    public static byte[] decode(String src) {

        byte[] bytes = null;

        StringBuffer buf = new StringBuffer(src);

        // First, Remove white spaces (\r\n, \t, " ");
        int i = 0;
        char c = ' ';
        char oc = ' ';
        while (i < buf.length()) {
            oc = c;
            c = buf.charAt(i);
            if (oc == '\r' && c == '\n') {
                buf.deleteCharAt(i);
                buf.deleteCharAt(i - 1);
                i -= 2;
            } else if (c == '\t') {
                buf.deleteCharAt(i);
                i--;
            } else if (c == ' ') {
                i--;
            }
            i++;
        }

        // The source should consists groups with length of 4 chars.
        if (buf.length() % 4 != 0) {
            //throw new Exception("Base64 decoding invalid length");
        }

        // pre-set byte array size.
        bytes = new byte[3 * (buf.length() / 4)];
        //int len = 3 * (buf.length() % 4);
        //System.out.println("Size of Bytes array: " + len);
        int index = 0;

        // Now decode each group
        for (i = 0; i < buf.length(); i += 4) {

            byte data = 0;
            int nGroup = 0;

            for (int j = 0; j < 4; j++) {

                char theChar = buf.charAt(i + j);

                if (theChar == '=') {
                    data = 0;
                } else {
                    data = getBaseTableIndex(theChar);
                }

                if (data == -1) {
                    //throw new Exception("Base64 decoding bad character");
                }

                nGroup = 64 * nGroup + data;
            }

            bytes[index] = (byte) (255 & (nGroup >> 16));
            index++;

            bytes[index] = (byte) (255 & (nGroup >> 8));
            index++;

            bytes[index] = (byte) (255 & (nGroup));
            index++;
        }

        byte[] newBytes = new byte[index];
        for (i = 0; i < index; i++) {
            newBytes[i] = bytes[i];
        }

        return newBytes;
    }

    /**
     * Find index number in base table for a given character.
     *
     */
    protected static byte getBaseTableIndex(char c) {

        byte index = -1;

        for (byte i = 0; i < baseTable.length(); i++) {

            if (baseTable.charAt(i) == c) {
                index = i;
                break;
            }
        }

        return index;
    }
}