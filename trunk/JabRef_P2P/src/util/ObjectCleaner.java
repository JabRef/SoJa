package util;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thien Rong
 */
public class ObjectCleaner {

    public static void cleanSockets(Socket... sockets) {
        for (int i = 0; i < sockets.length; i++) {
            Socket socket = sockets[i];
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void cleanCloseable(Closeable... closeables) {
        for (int i = 0; i < closeables.length; i++) {
            Closeable closeable = closeables[i];
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void cleanServerSockets(ServerSocket... sockets) {
        for (int i = 0; i < sockets.length; i++) {
            ServerSocket s = sockets[i];
            if (s != null) {
                try {
                    s.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }
}
