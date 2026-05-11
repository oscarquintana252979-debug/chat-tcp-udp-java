/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor;
import java.net.Socket;
/**
 *
 * @author PC
 */
public class ManejadorCliente implements Runnable {
    
    private Socket socket;
    
    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Hilo asignado correctamente. Atendiendo al nuevo cliente...");
    }
}