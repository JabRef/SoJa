package model;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import util.BibtexStringCodec;
import util.GlobalUID;

/**
 * Convert to/from BibtexMessage
 * Assuming that SEPARATOR is unique and not in msg
 * Other possible way is to use length encoded, but it means each field cannot be
 * > number of byte allowed
 * @author Thien Rong
 */
public class BibtexMessageCodec {

    //private static final String SEPARATOR = "7e467456-6e99-40a5-9680-b7519a587d3c6ae61716-e1f7-4deb-abae-c3fd6f094d64";
    /**
     * Maximum should be 10 digit (Integer.MAX so padded with zero if length
     * < 10 digit
     * @param s
     * @return
     */
    private static String encodeLength(String s) {
        StringBuilder lenStr = new StringBuilder(String.valueOf(s.length()));
        while (lenStr.length() < 10) {
            lenStr.insert(0, '0');
        }
        return lenStr.toString() + s;
    }

    private static List<String> decodeEncoded(String s) {
        List<String> values = new Vector<String>();
        try {
            StringBuilder sb = new StringBuilder(s);
            int start = 0;
            while (start + 10 < sb.length()) {
                //System.out.println(sb.substring(start, start + 10));
                int length = Integer.parseInt(sb.substring(start, start + 10));
                //System.out.println(length);
                start += 10;
                values.add(sb.substring(start, start + length));
                start += length;
            }

            return values;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(decodeEncoded(encodeLength("tefrewofjpewfjpet123")));

        BibtexMessage m = new BibtexMessage(null, "subject", "msg", "entries", "to");

        //System.out.println(toString(m));
        BibtexMessage m2 = fromString(toString(m));
        System.out.println(m.equals(m2));
        System.out.println(toString(m2));

        // fromCodecString don't close
        System.exit(0);
    }

    public static String toString(BibtexMessage msg) throws IOException {
        return encodeLength(msg.getGUID()) + encodeLength(msg.getSubject()) + encodeLength(msg.getMsg()) +//
                encodeLength(BibtexStringCodec.toStringList(msg.getEntry())) +//
                encodeLength(msg.getTo());
    }

    public static BibtexMessage fromString(String encodedStr) throws IOException {
        List<String> data = decodeEncoded(encodedStr);
        if (data.size() != 5) {
            return null;
        }

        BibtexMessage m = new BibtexMessage(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4));
        return m;
    }
}
