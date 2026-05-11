/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cliente;
import java.net.Socket;
import java.io.IOException;
/**
 *
 * @author PC
 */
public class ClienteMain {
    
    private static final String IP_SERVIDOR = "localhost"; 
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        System.out.println("Intentando entrar al chat (conectando al servidor)...");

        try {
            Socket socket = new Socket(IP_SERVIDOR, PUERTO);
            System.out.println("Conexion exitosa. Servidor Funcionando.");            
        } catch (IOException e) {
            System.out.println("No se pudo conectar al servidor. Error: " + e.getMessage());
        }
    }
}