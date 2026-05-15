# Chat TCP/UDP en Java - Proyecto Final Redes

**Instituto Tecnológico de Sonora (ITSON)**

Aplicación de chat cliente-servidor desarrollada en Java que permite comunicación en tiempo real mediante sockets TCP y UDP, con soporte para mensajes grupales, privados, guardado de historial e Interfaz Gráfica (GUI).

## Integrantes
* **Oscar Quintana Morales** - ID: 252979

### ¿Qué es y para qué sirve?
Es un **programa de chat en vivo** (como un WhatsApp o un Messenger muy básico) que se hizo para conectar a varias computadoras entre sí y permitirles hablar en tiempo real.
Sirve para demostrar cómo viaja la información a través de internet usando las dos formas principales que existen para mandar datos: una que es súper segura y ordenada (donde ningún mensaje se pierde ni se revuelve), y otra que es ultra rápida (ideal para cuando necesitas mandar algo de inmediato sin importar si hay pequeños parpadeos en la señal).

Qué fue lo que hice?
* **Construí la sala de chat (El Servidor):** Creé la "base central" que se queda encendida esperando a que la gente entre. Es como el recepcionista de un edificio: revisa quién entra, se asegura de que nadie use el mismo nombre, lleva un registro de todo lo que se habla en un archivo de texto y, si la sala ya se llenó (máximo 5 personas), amablemente te dice que esperes.
* **Diseñé la aplicación (La Interfaz):** Programé la ventana visual con sus botones, su caja para escribir y el espacio donde lees los mensajes.
* **Puse candados de seguridad:** Hice que el sistema rechace nombres que tengan símbolos raros o espacios, evitando que el chat se mueva o falle.

## Tecnologías y Arquitectura
* **Lenguaje:** Java 25 
* **Conexión:** Sockets TCP (orientado a conexión) y DatagramSockets UDP (sin conexión).
* **Concurrencia:** Multihilos (`Runnable` y `Thread`) para manejar hasta 5 clientes simultáneos sin bloqueos.
* **Interfaz:** Interfaz Gráfica desarrollada con Java Swing.

## Funcionalidades Implementadas
El sistema utiliza una arquitectura Cliente-Servidor centralizada. El servidor escucha en los puertos `5000` (TCP) y `5001` (UDP).

* **Switch de Protocolo Dinámico (GUI):** Selección de protocolo (TCP o UDP) de forma visual e interactiva mediante una ventana emergente al iniciar el cliente.
* **Validación Alfanumérica de Usuarios:** Al registrarse, el sistema valida localmente mediante expresiones regulares que el nombre de usuario contenga únicamente letras y números.
* **Control de Sala Llena:** El servidor restringe el acceso a un máximo de 5 usuarios TCP simultáneos. Si un sexto cliente intenta conectarse, la GUI recibe un aviso de rechazo por "Sala Llena" y se cierra limpiamente de forma automática.
* **Aviso de Nombre Duplicado:** Si el nombre de usuario ya está activo en la sala TCP, la GUI despliega una alerta de error y le permite al usuario ingresar un nuevo nombre inmediatamente sin congelar ni tumbar la aplicación.
* **Broadcast:** Envío de mensajes a la sala general con Timestamp automático.
* **Mensajería Privada:** Sintaxis `@usuario [mensaje]` para comunicación directa uno a uno (disponible en modo TCP).
* **Desconexión Controlada:** Comando `/salir` para cerrar sockets limpiamente y notificar a los demás integrantes de la sala.
* **Historial (Extra):** Guardado automático sincronizado de eventos y mensajes en el archivo `historial_chat.txt` en el lado del servidor.

## Instrucciones de Ejecución
1. Compilar el proyecto utilizando Maven en NetBeans o desde la terminal.
2. Ejecutar primero la clase `servidor.ServidorMain` para iniciar el servidor.
3. Ejecutar la clase `cliente.ClienteGUI` para iniciar la interfaz gráfica. Puedes ejecutarla múltiples veces (hasta 5 instancias simultáneas en TCP) para simular diferentes usuarios interactuando en el chat.

## Capturas de Pantalla

*Servidor esperando conexiones y registrando usuarios en consola.*
<img width="957" height="496" alt="Servidor espera y primer conexion" src="https://github.com/user-attachments/assets/ae72d317-3a6b-4818-9b52-de671d1bfb09" />

*Conversación entre usuarios.*
<img width="1101" height="782" alt="conversacion entre usuarios" src="https://github.com/user-attachments/assets/96c293ab-2722-4d2f-b3f5-10896d5f4229" />

*Funcionalidad de mensajería privada.*
<img width="1084" height="488" alt="mensaje privado" src="https://github.com/user-attachments/assets/3fc0b44a-4088-49a5-b2e1-dd20e2360614" />
