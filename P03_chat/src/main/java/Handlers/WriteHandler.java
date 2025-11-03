/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Handlers;

import Handlers.Handler;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author sergi
 */
public class WriteHandler extends Handler implements Runnable {

    private String messageTx = "_";

    public WriteHandler(Socket socket) {
        super(socket);
    }

    @Override
    public void run() {
        Scanner messageScanner = new Scanner(System.in);

        while (out!= null && !messageTx.equalsIgnoreCase("/exit")) {
            
            if (!messageScanner.hasNextLine()){
                break;
            }
            
            messageTx = messageScanner.nextLine();
            out.print(messageTx + "\r\n");
            System.out.println( "me: " + messageTx );
            out.flush();
        }
        System.out.println("com.uacam.p03_chat.WriteHandler.run()");
        dismiss();

    }
}
