package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Trim and count number of line of codes in an directory
 * @author Thien Rong
 */
public class LOCounter {

    public static void main(String[] args) throws IOException {
        System.out.println("Lines = " + new LOCounter(2).getLines(new File("src")));
    }
    int minLineSize;

    public LOCounter(int minLineSize) {
        this.minLineSize = minLineSize;
    }

    public int getLines(File f) throws IOException {
        if (f.isFile()) {
            return getLinesInFile(new BufferedReader(new FileReader(f)));

        } else if (f.isDirectory()) {
            File[] files = f.listFiles();
            int lines = 0;
            for (File file : files) {
                lines += getLines(file);
            }
            return lines;
        }
     
        return -1;
    }

    public int getLinesInFile(BufferedReader bufferedreader)
            throws IOException {
        int i = 0;
        boolean flag = false;
        for (String s = bufferedreader.readLine(); s != null; s = bufferedreader.readLine()) {
            int j = 0;
            int k = 0;
            s = s.trim();
            if (s.startsWith("/*")) {
                flag = true;
            }
            if (flag) {
                if (s.indexOf("*/") != -1) {
                    flag = false;
                }
                continue;
            }
            for (; k < s.length() && j < minLineSize; k++) {
                if (s.charAt(k) != ' ') {
                    j++;
                }
            }

            if (j != minLineSize) {
                continue;
            }
            if (!s.startsWith("//") && !s.startsWith("--")) {
                i++;
            }
        }

        return i;
    }
}
