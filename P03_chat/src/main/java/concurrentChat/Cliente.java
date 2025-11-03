package concurrentChat;

import Handlers.WriteHandler;
import Handlers.ReadHandler;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente { 

    private static int PUERTO_DESTINO = 8080;
    private static String HOST_DESTINO = "localhost";

    public static Socket socketConexion = null; 

    public static void main(String[] args) throws IOException {

        Scanner lectorComandos = new Scanner(System.in); 
        System.out.println("Comando: ");
        String comandoInput = lectorComandos.nextLine(); 

        if (comandoInput.equals("start-conection")) {
            establecerConexion(); 
        } else {
            System.err.println("Comando no válido.");
            return;
        }

        if (socketConexion == null) {
            System.err.println("No se pudo establecer la conexión. Saliendo.");
            return;
        }

        WriteHandler writer = new WriteHandler(socketConexion);
        ReadHandler reader = new ReadHandler(socketConexion);

        Thread hiloEscritor = new Thread(writer); 
        Thread hiloLector = new Thread(reader); 

        hiloEscritor.start();
        hiloLector.start();
    }

    
    public static void establecerConexion() { 
        try (Scanner lectorDatos = new Scanner(System.in)) {
            System.out.print("Introduce la IP del servidor: ");
            HOST_DESTINO = lectorDatos.nextLine();

            System.out.print("Introduce el puerto: ");
            PUERTO_DESTINO = Integer.parseInt(lectorDatos.nextLine());

            socketConexion = new Socket(HOST_DESTINO, PUERTO_DESTINO);
            System.out.println("Conexión establecida con " + HOST_DESTINO + ":" + PUERTO_DESTINO);

        } catch (IOException ex) {
            System.err.println("Error al conectar: " + ex.getMessage());
            socketConexion = null; 
        } catch (NumberFormatException ex) {
            System.err.println("Error: El puerto debe ser un número.");
            socketConexion = null;
        }
    }
}