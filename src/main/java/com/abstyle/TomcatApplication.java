package com.abstyle;

import com.abstyle.Server.SocketServer;

import java.io.IOException;

public class TomcatApplication {
    public static String className = null;

    public static void start() {
        try {
            className = new Exception().getStackTrace()[1].getClassName();
            new Thread(new SocketServer()).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void start(int port) {
        try {
            className = new Exception().getStackTrace()[1].getClassName();
            new Thread(new SocketServer(port)).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}