/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Clase que maneja la conexión individual de cada cliente conectado al servidor TCP.
 * Implementa Runnable para ejecutarse en un hilo independiente .
 * @author PC
 */
public class ManejadorCliente implements Runnable {
    
    private Socket socket;
    private String nombreUsuario;
    private PrintWriter salida;
    private BufferedReader entrada;
    /**
     * Constructor que inicializa el manejador con el socket del cliente.
     * *
     */
    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }
    /**
     * Obtiene el nombre de usuario registrado de este cliente.
     * * @return El nombre de usuario en formato String.
     */
    public String getNombreUsuario() { return nombreUsuario; }
    /**
     * Envía un mensaje de texto directamente a la consola de este cliente.
     * * 
     */
    public void enviarMensaje(String mensaje) {
        if (salida != null) {
            salida.println(mensaje);
        }
    }
    /**
     * Método principal del hilo. Se encarga de registrar al usuario,
     * escuchar sus mensajes y retransmitirlos, así como de manejar su desconexión.
     */
    @Override
    public void run() {
        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                salida.println("Por favor, ingresa tu nombre de usuario:");
                String intentoNombre = entrada.readLine();
                
                if (intentoNombre == null) return;

                if (ServidorMain.existeUsuario(intentoNombre)) {
                    salida.println("Error: El nombre '" + intentoNombre + "' ya esta en uso. Intenta con otro.");
                } else {
                    this.nombreUsuario = intentoNombre;
                    salida.println("¡Registro exitoso! Bienvenido al chat, " + nombreUsuario);
                    break;
                }
            }

            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            ServidorMain.enviarMensajeATodos("📢 [" + formatoFecha.format(LocalDateTime.now()) + "] " + nombreUsuario + " se ha unido a la sala.");
            
            String mensajeCliente;
            while ((mensajeCliente = entrada.readLine()) != null) {
                
                if (mensajeCliente.equalsIgnoreCase("/salir")) {
                    break;
                }

                String horaActual = formatoFecha.format(LocalDateTime.now());

                if (mensajeCliente.startsWith("@")) {
                    int espacioIndex = mensajeCliente.indexOf(" "); 
                    if (espacioIndex != -1) {
                        String destinatario = mensajeCliente.substring(1, espacioIndex); 
                        String mensajePrivado = mensajeCliente.substring(espacioIndex + 1); 
                        
                        String mensajeFinal = "[" + horaActual + "] [PRIVADO de " + nombreUsuario + "]: " + mensajePrivado;
                        ServidorMain.enviarMensajePrivado(nombreUsuario, destinatario, mensajeFinal);                       
                        enviarMensaje("[" + horaActual + "] [Tú para " + destinatario + "]: " + mensajePrivado);
                        continue;
                    }
                }
                String mensajeFinal = "[" + horaActual + "] " + nombreUsuario + ": " + mensajeCliente;
                ServidorMain.enviarMensajeATodos(mensajeFinal);
            }

        } catch (IOException e) {
        } finally {
            if (nombreUsuario != null) {
                ServidorMain.eliminarCliente(this);
                String horaActual = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now());
                ServidorMain.enviarMensajeATodos("📢 [" + horaActual + "] " + nombreUsuario + " se ha desconectado.");
                System.out.println("El usuario '" + nombreUsuario + "' se ha desconectado del servidor.");
            }
            try {
                socket.close(); 
            } catch (IOException e) {
                System.out.println("Error al cerrar el socket.");
            }
        }
    }
}