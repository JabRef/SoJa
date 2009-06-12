package util;

/**
 * @author Thien Rong
 */
public class If {

    private String pre;
    private String start;
    private String end;
    private int endIndex;

    public If(String pre, String start, String end) {
        this.pre = pre;
        this.start = start;
        this.end = end;
    }

    public String process(String text) {
        return process(text, 0);
    }

    public String process(String text, int startIndex) {
        if (pre != null) {
            startIndex = text.indexOf(pre, startIndex);
            if (startIndex == -1) {
                //System.out.println("pre null");
                return null;
            } else {
                startIndex += pre.length();
            }
        }

        startIndex = text.indexOf(start, startIndex);
        if (startIndex == -1) {
            //System.out.println("start null");
            return null;
        }

        startIndex += start.length();
        endIndex = (end == null) ? text.length() : text.indexOf(end, startIndex);

        if (endIndex < startIndex) {
            return null;
        }
        return text.substring(startIndex, endIndex);
    }

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }
}