/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import util.Configuracion;
/**
 * Clase principal que inicializa el servidor del chat.
 * Maneja las conexiones entrantes (TCP/UDP) y mantiene el registro de usuarios conectados.
 * @author PC
 */
public class ServidorMain {
    
    /** Lista de hilos de clientes actualmente conectados al servidor TCP. */
    public static List<ManejadorCliente> clientesConectados = new ArrayList<>();

    /**
     * Verifica si un nombre de usuario ya está siendo utilizado en la sala.
     * @return true si el usuario ya existe, false si está disponible.
     */
    public static boolean existeUsuario(String nombre) {
        for (ManejadorCliente cliente : clientesConectados) {
            if (cliente.getNombreUsuario() != null && cliente.getNombreUsuario().equalsIgnoreCase(nombre)) { return true; }
        }
        return false;
    }

    /**
     * Guarda el historial de chat en un archivo de texto (Funcionalidad Extra).
     */
    public static synchronized void guardarEnHistorial(String mensaje) {
        try (PrintWriter out = new PrintWriter(new FileWriter("historial_chat.txt", true))) {
            out.println(mensaje);
        } catch (IOException e) {
            System.out.println("Error al guardar en el historial: " + e.getMessage());
        }
    }

    /**
     * Envía un mensaje a todos los clientes conectados.
     */
    public static void enviarMensajeATodos(String mensaje) {
        guardarEnHistorial(mensaje); // Guarda en el txt
        for (ManejadorCliente cliente : clientesConectados) { cliente.enviarMensaje(mensaje); }
    }

    /**
     * Envía un mensaje privado a un usuario específico.
     * Si el usuario no existe, notifica el error al remitente.  
     */
    public static void enviarMensajePrivado(String remitente, String destinatario, String mensaje) {
        guardarEnHistorial(mensaje);
        boolean usuarioEncontrado = false;
        for (ManejadorCliente cliente : clientesConectados) {
            if (cliente.getNombreUsuario() != null && cliente.getNombreUsuario().equalsIgnoreCase(destinatario)) {
                cliente.enviarMensaje(mensaje);
                usuarioEncontrado = true;
                break;
            }
        }
        if (!usuarioEncontrado) {
             for (ManejadorCliente cliente : clientesConectados) {
                if (cliente.getNombreUsuario() != null && cliente.getNombreUsuario().equalsIgnoreCase(remitente)) {
                    cliente.enviarMensaje("Error: El usuario '@" + destinatario + "' no esta conectado o no existe.");
                    break;
                }
            }
        }
    }

    /**
     * Elimina a un cliente de la lista de conexiones activas.
     */
    public static void eliminarCliente(ManejadorCliente cliente) {
        clientesConectados.remove(cliente);
    }

    /**
     * Punto de entrada del programa Servidor.
     * Inicia el servicio en modo TCP o UDP dependiendo de la clase Configuracion.
     */
    public static void main(String[] args) {
        
        if (Configuracion.USAR_TCP) {
            System.out.println("Iniciando el servidor en modo TCP...");
            try (ServerSocket servidorRecepcionista = new ServerSocket(Configuracion.PUERTO_TCP)) {
                System.out.println("Servidor TCP listo en el puerto " + Configuracion.PUERTO_TCP);

                while (true) {
                 Socket socketCliente = servidorRecepcionista.accept();
                    if (clientesConectados.size() >= 5) {
                    System.out.println("Conexion rechazada: Sala llena.");
                    PrintWriter salidaInmediata = new PrintWriter(socketCliente.getOutputStream(), true);
                    salidaInmediata.println("Error: Sala llena. Maximo 5 usuarios.");
                    socketCliente.close();
                    continue; 
    }
    ManejadorCliente nuevoCliente = new ManejadorCliente(socketCliente);
    clientesConectados.add(nuevoCliente);
    Thread hilo = new Thread(nuevoCliente);
    hilo.start();
}
            } catch (IOException e) {
                System.out.println("Error TCP: " + e.getMessage());
            }

        } else {

            System.out.println("Iniciando el servidor en modo UDP...");
            try (DatagramSocket socketUDP = new DatagramSocket(Configuracion.PUERTO_UDP)) {
                System.out.println("Servidor UDP escuchando en el puerto " + Configuracion.PUERTO_UDP);
                
                byte[] buffer = new byte[1024]; 
                
                while (true) {
                    DatagramPacket paqueteRecibido = new DatagramPacket(buffer, buffer.length);
                    socketUDP.receive(paqueteRecibido);                  
                    String mensaje = new String(paqueteRecibido.getData(), 0, paqueteRecibido.getLength());
                    
                    String mensajeLog = "[Mensaje UDP recibido de " + paqueteRecibido.getAddress().getHostAddress() + "]: " + mensaje;
                    System.out.println(mensajeLog);
                    guardarEnHistorial(mensajeLog);
                }
            } catch (IOException e) {
                System.out.println("Error UDP: " + e.getMessage());
            }
        }
    }
}