/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cliente;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import util.Configuracion;
/**
 *Cliente con Interfaz Gráfica (GUI) para el Chat TCP/UDP.
 * @author PC
 */
public class ClienteGUI extends JFrame {
    
    private JTextArea areaChat;
    private JTextField campoMensaje;
    private JButton botonEnviar;
    private String nombreUsuario;
    private Socket socketTCP;
    private PrintWriter salidaTCP;
    private BufferedReader entradaTCP;
    private DatagramSocket socketUDP;
    private InetAddress ipServidor;

    public ClienteGUI() {
        setTitle("Chat ITSON - Redes");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        areaChat = new JTextArea();
        areaChat.setEditable(false); 
        areaChat.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollChat = new JScrollPane(areaChat); 
        add(scrollChat, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        campoMensaje = new JTextField();
        botonEnviar = new JButton("Enviar");
        
        panelInferior.add(campoMensaje, BorderLayout.CENTER);
        panelInferior.add(botonEnviar, BorderLayout.EAST);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 
        add(panelInferior, BorderLayout.SOUTH);
        
        ActionListener accionEnviar = e -> enviarMensaje();
        botonEnviar.addActionListener(accionEnviar);
        campoMensaje.addActionListener(accionEnviar);

        setLocationRelativeTo(null);
    }

    private void seleccionarProtocolo() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        JRadioButton radioTCP = new JRadioButton("Modo TCP (Conexión estable y chat privado)", true);
        JRadioButton radioUDP = new JRadioButton("Modo UDP (Envío rápido de paquetes)", false);
        
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(radioTCP);
        grupo.add(radioUDP);
        
        panel.add(radioTCP);
        panel.add(radioUDP);

        int seleccion = JOptionPane.showConfirmDialog(
                this, 
                panel, 
                "Selecciona el Protocolo de Red", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.QUESTION_MESSAGE
        );

        if (seleccion == JOptionPane.OK_OPTION) {
            Configuracion.USAR_TCP = radioTCP.isSelected();
        } else {
            System.exit(0);
        }
    }

    private void iniciarConexion() {
        seleccionarProtocolo();

        try {
            if (Configuracion.USAR_TCP) {
                socketTCP = new Socket("localhost", Configuracion.PUERTO_TCP);
                entradaTCP = new BufferedReader(new InputStreamReader(socketTCP.getInputStream()));
                salidaTCP = new PrintWriter(socketTCP.getOutputStream(), true);

                boolean registrado = false;
                while (!registrado) {
                    String peticion = entradaTCP.readLine(); 
                    
                    if (peticion != null && peticion.contains("Sala llena")) {
                        JOptionPane.showMessageDialog(this, peticion, "Sala Llena", JOptionPane.WARNING_MESSAGE);
                        System.exit(0);
                    }

                    String intentoNombre = "";
                    while (true) {
                        intentoNombre = JOptionPane.showInputDialog(this, peticion, "Registro", JOptionPane.QUESTION_MESSAGE);
                        
                        if (intentoNombre == null) System.exit(0);
                        intentoNombre = intentoNombre.trim();

                        if (!intentoNombre.matches("^[a-zA-Z0-9]+$")) {
                            JOptionPane.showMessageDialog(this, "Error: El nombre de usuario solo debe contener letras y números (sin espacios ni símbolos).", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
                        } else {
                            break;
                        }
                    }

                    nombreUsuario = intentoNombre;
                    salidaTCP.println(nombreUsuario);
                    String respuesta = entradaTCP.readLine();
                    
                    if (respuesta.contains("Registro exitoso")) {
                        registrado = true;
                        setTitle("Chat ITSON - " + nombreUsuario + " (Modo TCP)");
                        areaChat.append("--- " + respuesta + " ---\n");
                        areaChat.append("Tips: Usa @usuario para mensaje privado, o /salir para desconectarte.\n\n");
                    } else {
                        JOptionPane.showMessageDialog(this, respuesta, "Nombre Duplicado", JOptionPane.ERROR_MESSAGE);
                    }
                }

                new Thread(() -> {
                    try {
                        String mensajeRecibido;
                        while ((mensajeRecibido = entradaTCP.readLine()) != null) {
                            areaChat.append(mensajeRecibido + "\n");
                            areaChat.setCaretPosition(areaChat.getDocument().getLength()); 
                        }
                    } catch (IOException e) {
                        areaChat.append("\n[Te has desconectado del servidor]\n");
                    }
                }).start();

            } else {
                socketUDP = new DatagramSocket();
                ipServidor = InetAddress.getByName("localhost");
                
                boolean nombreValido = false;
                while (!nombreValido) {
                    nombreUsuario = JOptionPane.showInputDialog(this, "Ingresa tu nombre (Modo UDP):", "Registro UDP", JOptionPane.QUESTION_MESSAGE);
                    if (nombreUsuario == null) System.exit(0);
                    nombreUsuario = nombreUsuario.trim();

                    if (nombreUsuario.matches("^[a-zA-Z0-9]+$")) {
                        nombreValido = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Error: El nombre solo debe contener letras y números.", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
                    }
                }
                
                setTitle("Chat ITSON - " + nombreUsuario + " (Modo UDP)");
                areaChat.append("--- Conectado en modo rápido UDP ---\n\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error: El servidor no está encendido o rechazó la conexión.", "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void enviarMensaje() {
        String texto = campoMensaje.getText().trim();
        if (texto.isEmpty()) return; 

        campoMensaje.setText(""); 

        if (texto.equalsIgnoreCase("/salir")) {
            if (Configuracion.USAR_TCP && salidaTCP != null) salidaTCP.println("/salir");
            System.exit(0);
        }

        try {
            if (Configuracion.USAR_TCP) {
                salidaTCP.println(texto);
            } else {
                String mensajeUDP = nombreUsuario + ": " + texto;
                byte[] buffer = mensajeUDP.getBytes();
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, ipServidor, Configuracion.PUERTO_UDP);
                socketUDP.send(paquete);
                areaChat.append("-> Paquete enviado: " + texto + "\n");
                areaChat.setCaretPosition(areaChat.getDocument().getLength());
            }
        } catch (IOException e) {
            areaChat.append("Error al enviar mensaje.\n");
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        
        SwingUtilities.invokeLater(() -> {
            ClienteGUI ventana = new ClienteGUI();
            ventana.setVisible(true);
            ventana.iniciarConexion();
        });
    }
}