/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package concurrentChat;

import Handlers.WriteHandler;
import Handlers.ReadHandler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author sergi
 */
public class Servidor {

    private static final int PORT = 8080;

    public static void main(String[] args) {

        try {
            ServerSocket server = new ServerSocket(PORT);
            System.err.println("Server active in localhost:" + PORT);

            Socket socket = server.accept();
            System.out.println("new client IP:" + socket.getInetAddress());

            WriteHandler writer = new WriteHandler(socket);
            ReadHandler reader = new ReadHandler(socket);

            Thread writeThread = new Thread(writer);
            Thread readThread = new Thread(reader);

            writeThread.start();
            readThread.start();

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

}
