/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cliente;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import util.Configuracion;
/**
 * Clase principal que inicializa el cliente del chat.
 * Se conecta al servidor en modo TCP o envía paquetes en modo UDP.
 * @author PC
 */
public class ClienteMain {
    /** Dirección IP del servidor al que intentará conectarse. */
    private static final String IP_SERVIDOR = "localhost"; 
    /**
     * Punto de entrada del programa Cliente.
     * Lee la entrada del usuario y la envía al servidor, además de escuchar las respuestas.
     */
    public static void main(String[] args) {
        
        Scanner teclado = new Scanner(System.in);

        if (Configuracion.USAR_TCP) {
            
            System.out.println("Intentando conectar al servidor TCP...");
            try {
                Socket socket = new Socket(IP_SERVIDOR, Configuracion.PUERTO_TCP);
                BufferedReader entradaServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter salidaServidor = new PrintWriter(socket.getOutputStream(), true);

                System.out.println("Servidor: " + entradaServidor.readLine());
                boolean registrado = false;
                while (!registrado) {
                    String miNombre = teclado.nextLine();
                    salidaServidor.println(miNombre);
                    String respuesta = entradaServidor.readLine();
                    System.out.println("Servidor: " + respuesta);
                    
                    if (respuesta.contains("Registro exitoso")) { registrado = true; } 
                    else { System.out.println("Servidor: " + entradaServidor.readLine()); }
                }
                
                System.out.println("--- Ya puedes empezar a chatear ---");

                Thread hiloEscucha = new Thread(() -> {
                    try {
                        String mensajeRecibido;
                        while ((mensajeRecibido = entradaServidor.readLine()) != null) { System.out.println(mensajeRecibido); }
                    } catch (IOException e) { System.out.println("Conexion cerrada."); }
                });
                hiloEscucha.start();

                while (true) {
                    String miMensaje = teclado.nextLine();
                    salidaServidor.println(miMensaje);
                    if (miMensaje.equalsIgnoreCase("/salir")) {
                        System.out.println("Cerrando el chat. Hasta luego");
                        break;
                    }
                }
                System.exit(0);
                
            } catch (IOException e) {
                System.out.println("Error de conexion TCP: " + e.getMessage());
            }

        } else {

            System.out.println("Iniciando cliente en modo UDP...");
            System.out.println("Escribe un mensaje para enviarlo por UDP (o /salir para terminar):");
            
            try (DatagramSocket socketUDP = new DatagramSocket()) {
                InetAddress ipDestino = InetAddress.getByName(IP_SERVIDOR);
                
                while (true) {
                    String miMensaje = teclado.nextLine();
                    if (miMensaje.equalsIgnoreCase("/salir")) {
                        System.out.println("Cerrando cliente UDP.");
                        break;
                    }
                    
                    byte[] buffer = miMensaje.getBytes();
                    DatagramPacket paqueteEnviar = new DatagramPacket(buffer, buffer.length, ipDestino, Configuracion.PUERTO_UDP);
                    socketUDP.send(paqueteEnviar); 
                    System.out.println("-> Paquete enviado.");
                }
            } catch (IOException e) {
                System.out.println("Error UDP: " + e.getMessage());
            }
        }
    }
}