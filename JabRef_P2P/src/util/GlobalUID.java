package util;

import java.util.UUID;

/**
 * Global Unique Identifier
 * An generator of id for uniquely globally ID using
 * random UUID + localID(Can be friend ID)
 */
public class GlobalUID {

    public static String generate(String localID) {
        return UUID.randomUUID() + localID;
    }
}
