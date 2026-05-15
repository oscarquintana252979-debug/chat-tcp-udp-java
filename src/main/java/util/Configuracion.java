/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**

 * Clase de utilidad que almacena las configuraciones globales del sistema de chat.
 * Centraliza los puertos y el switch para cambiar de protocolo fácilmente.
 * @author PC
 */
public class Configuracion {
    public static boolean USAR_TCP = true; 
    public static final int PUERTO_TCP = 5000;
    public static final int PUERTO_UDP = 5001; 
}