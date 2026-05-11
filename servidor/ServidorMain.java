/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author PC
 */
public class ServidorMain {
    
    private static final int PUERTO = 5000;
    private static final int MAX_CLIENTES = 5; 

    public static List<ManejadorCliente> clientesConectados = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Iniciando el servidor...");

        try (ServerSocket servidorRecepcionista = new ServerSocket(PUERTO)) {
            System.out.println("Servidor listo y esperando conexiones en el puerto " + PUERTO);

            while (true) {
                Socket socketCliente = servidorRecepcionista.accept();               
                if (clientesConectados.size() >= MAX_CLIENTES) {
                    System.out.println("Conexion rechazada: La sala ya tiene 5 usuarios.");
                    socketCliente.close();
                    continue;
                }

                System.out.println("Alguien paso la puerta principal");
                ManejadorCliente nuevoCliente = new ManejadorCliente(socketCliente);
                clientesConectados.add(nuevoCliente);              
                Thread hilo = new Thread(nuevoCliente);
                hilo.start();
            }

        } catch (IOException e) {
            System.out.println("Hubo un problema al abrir: " + e.getMessage());
        }
    }
}