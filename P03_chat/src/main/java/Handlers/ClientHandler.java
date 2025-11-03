package Handlers;

import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandler implements Runnable { 

    private final Socket socketDelCliente; 
    private final Set<ClientHandler> listaClientes; 
    private BufferedReader lector; 
    private PrintWriter escritor; 
    private String nombreDeUsuario = "Anonimo"; 

    public ClientHandler(Socket clientSocket, Set<ClientHandler> clients) {
        this.socketDelCliente = clientSocket;
        this.listaClientes = clients;
    }

    @Override
    public void run() {

        try {
            lector = new BufferedReader(new InputStreamReader(socketDelCliente.getInputStream()));
            escritor = new PrintWriter(new OutputStreamWriter(socketDelCliente.getOutputStream()), true);

            escritor.println("Conexión establecida. Usa /exit para salir.");
            escritor.println("Introduce tu nombre de usuario:");

            nombreDeUsuario = lector.readLine();
            System.err.println(nombreDeUsuario + " se ha unido al chat.");

            String lineaEntrante; 

            while ((lineaEntrante = lector.readLine()) != null) {
                if (lineaEntrante.equalsIgnoreCase("/exit")) {
                    break;
                } else if (lineaEntrante.startsWith("/change-userName ")) {
                    procesarCambioNombre(lineaEntrante); 
                } else if (lineaEntrante.startsWith("/send-msg ")) {
                    procesarMensajePrivado(lineaEntrante); 
                } else if (lineaEntrante.startsWith("/global-msg ")) {
                    procesarMensajeGlobal(lineaEntrante); 
                } else {
                    escritor.println("Comando no reconocido.");
                }
            }

        } catch (IOException ex) {
            System.err.println("Error: " + ex.getMessage());
        } finally {
            try {
                socketDelCliente.close();
                listaClientes.remove(this);
                System.err.println(nombreDeUsuario + " se ha desconectado.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void procesarCambioNombre(String command) {
        String[] partes = command.split(" ", 2);
        if (partes.length < 2) {
            escritor.println("Uso: /change-userName nuevoNombre");
            return;
        }

        String nombrePrevio = nombreDeUsuario; 
        nombreDeUsuario = partes[1];
        escritor.println("Tu nombre de usuario ahora es: " + nombreDeUsuario);

        String notificacion = nombrePrevio + " ahora se llama " + nombreDeUsuario;
        listaClientes.stream()
                .filter(cliente -> cliente != this)
                .forEach(cliente -> cliente.escritor.println(notificacion));
    }

    public void procesarMensajePrivado(String command) {
        String[] partes = command.split(" ", 3);
        if (partes.length < 3) {
            escritor.println("Uso: /send-msg usuario mensaje");
            return;
        }

        String nombreDestino = partes[1]; 
        String mensaje = partes[2];

        Optional<ClientHandler> receptor = listaClientes.stream()
                .filter(cliente -> cliente.nombreDeUsuario.equalsIgnoreCase(nombreDestino))
                .findFirst();

        if (receptor.isPresent()) {
            receptor.get().escritor.println("[Privado de " + nombreDeUsuario + "]: " + mensaje);
        } else {
            escritor.println("Usuario " + nombreDestino + " no está en línea o no existe.");
        }
    }

    public void procesarMensajeGlobal(String command) {
        String[] partes = command.split(" ", 2);
        if (partes.length < 2) {
            escritor.println("Uso: /global-msg mensaje");
            return;
        }

        String mensaje = partes[1];
        AtomicInteger conteo = new AtomicInteger(0); 

        String mensajeFormateado = "[Global de " + nombreDeUsuario + "]: " + mensaje;
        
        listaClientes.stream()
                .filter(cliente -> cliente != this)
                .forEach(cliente -> {
                    cliente.escritor.println(mensajeFormateado);
                    conteo.incrementAndGet();
                });

        escritor.println("Mensaje global enviado a " + conteo.get() + " usuarios.");
    }
}