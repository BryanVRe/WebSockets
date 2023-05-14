import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;

public class Servidor {
    //Configuramos la IP y el puerto para servidor
    private static final String _IP = "192.168.100.20";
    private static final int _PUERTO = 1234;

    public static void main(String[] args) throws UnknownHostException {
        //Creamos instancia de clase InetAddress para indicar 
        //El host donde se inicia el servidor 
        InetAddress ip = InetAddress.getByName(_IP);
        //Usamos un manejador de formato para el log del servidor 
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        //Mostramos por consola los datos del servidor 
        try {
            //si se usa localhost
            System.out.println("IP de localhost = " + InetAddress.getLocalHost().toString());
            //si se usa una ip
            System.out.println("\nEscuchando en: ");
            System.out.println("IP Host = "+ ip.getHostAddress());
            System.out.println("Puerto = "+ _PUERTO + "\n");
        } catch (UnknownHostException ex) {
            System.err.println("No se pudo saber la direccion IP local : "+ ex);
        }
        //A traves de este socket enviaremos datagramas del tiopo DatagramPacket
        DatagramSocket dgmSocket = null;

        try {
            //ds= new DatagramSocket(_PUERTO);//<-- use esta linea si trabaja con localhost
            dgmSocket = new DatagramSocket(_PUERTO, ip); //<-- use esta linea para una ip especifica 
        } catch (SocketException se) {
            System.err.println("Se ha producido un error al abrir el socket: "+se);
            System.exit(-1);
        }

        //bucle infinito de escucha
        while (true){
            try {

                //nos preparamos a recibir un numero entero (32 bits = 4 bytes)
                byte bufferEntrada[] = new byte[4];

                //creamos un "contenedor" de datagrama, cuyo buffer
                //sera el array creado antes 
                DatagramPacket dgmPaquete = new DatagramPacket(bufferEntrada, 4);
                // esperamos a recibir un paquete 
                dgmSocket.receive(dgmPaquete);

                //podemos extraer informacion del paquete : 

                //No de puerto desde donde se envio 
                int puertoRemitente = dgmPaquete.getPort();
                //direccion de internet desde donde se envio 
                InetAddress ipRemitente = dgmPaquete.getAddress();

                //Envolvemos el buffer con un ByteArrayInputStream...
                ByteArrayInputStream arrayEntrada = new ByteArrayInputStream(bufferEntrada);
                //... que volvemos a envolver con un DataInputStream
                DataInputStream datosEntrada = new DataInputStream(arrayEntrada);
                //leemos un numero a partir del array de bytes
                int entrada = datosEntrada.readInt();

                //Hacemos los calculos que correspondan
                long salida = (long) entrada * (long) entrada;

                //creamos un ByteArrayOutputStream sobre el que podamos escribir 
                ByteArrayOutputStream arraySalida = new ByteArrayOutputStream();
                //lo envolvemos con un DataOutputStream
                DataOutputStream datosSalida = new DataOutputStream(arraySalida);
                //Escribimos el resultado, ocupa 8 bytes
                datosSalida.writeLong(salida);

                //cerramos el buffer de escritura 
                datosSalida.close();

                //Generamos el paquete de vuelta, usando los datos del remitente de l paquete original
                dgmPaquete = new DatagramPacket(arraySalida.toByteArray(), 8, ipRemitente, puertoRemitente);
                //enviamos 
                dgmSocket.send(dgmPaquete);
                
                //Registramos en salida estandard
                System.out.println(formatter.format(new Date())+
                "\tCliente = "+ipRemitente +":" +
                puertoRemitente +"\tEntrada = " + 
                entrada + " \tSalida = "+ salida);

            } catch (Exception e) {
                System.err.println("Se ha producido un error " + e);
            }
        }
    }
}
