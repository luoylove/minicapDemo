package com.ly.adb;

import com.ly.mc.MiniCapHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by luoyoujun on 2019/5/29.
 */
public class Test {

    private Socket socket = null;

    public void takeBufferedImageByMinicap() {
        InputStream stream = null;
        DataInputStream input = null;

        try {
            socket = new Socket("localhost", MiniCapHandler.LOCAL_PORT);
            while (true) {
                stream = socket.getInputStream();
                input = new DataInputStream(stream);
                byte[] buffer;
                int len = 0;
                while (len == 0) {
                    len = input.available();
                }
                buffer = new byte[len];
                input.read(buffer);
                System.out.println("length=" + buffer.length);
                System.out.println(buffer);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (socket != null && socket.isConnected()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new Test().takeBufferedImageByMinicap();
    }
}
