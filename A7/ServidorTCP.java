import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServidorTCP {
    
    //configuramos la IP y el Puerto para el servidor 
    //private static final String _IP="192.168.100.20"
    private static final String _IP="10.30.0.78";
    private static final int _PUERTO=1234;
    private static final int _BACKLOG=50;

    public static void main(String args []) throws UnknownHostException{

        //creamos instancia de clase InetAddress para indicar 
        //el host donde se indica el servidor 
        InetAddress ip = InetAddress.getByName(_IP);

        //usamos un manejador de formato para el log del servidor 
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS");

        //primero indicamos la direccion IP local 
        try{

            //si se usa localhost:
            System.out.println("IP de LocalHost = " + InetAddress.getLocalHost().toString());
            //si se una una ip:
            System.out.println("\nEscuchando en: ");
            System.out.println("IP Host = " + ip.getHostAddress());
            System.out.println("Puerto = " + _PUERTO);
        }catch (UnknownHostException uhe){
            System.err.println("No puedo saber la direccion IP local: " + uhe);
        }
        //Abrimos un Socket de servidor TCP en el puerto 1234 
        ServerSocket serverSocket=null;
        try{
            //serverSocket = new ServerSocket(_PUERTO); // <-- si usa localhosts
            serverSocket= new ServerSocket(_PUERTO,_BACKLOG,ip);//<-- si se usa ip especifica
        }catch(IOException ioe){
            System.err.println("Error al abrir el socket del servidor : " + ioe);
            System.exit(-1);
        }

        int entrada;
        long salida;

        //bucle infinito 
        while(true){
            try{
                //Esperamos a que alguien se conecte a nuestro Socket 
                Socket socketPeticion = serverSocket.accept();

                //Extraemos los flujos de entrada y salida 
                DataInputStream datosEntrada = new DataInputStream(socketPeticion.getInputStream());
                DataOutputStream datosSalida = new DataOutputStream(socketPeticion.getOutputStream());

                //podemos xtraer informacion del socket 
                //N de puerto remoto 
                int puertoRemitente= socketPeticion.getPort();
                //Direccion de Internet remota 
                InetAddress ipRemitente = socketPeticion.getInetAddress();

                //Leemos datos de la peticion 
                entrada=datosEntrada.readInt();

                //Hacemos el calculo correspondiente
                salida = (long) entrada * (long) entrada;

                //Escribimos el resultado 
                datosSalida.writeLong(salida);

                //cerramos los flujos 
                datosEntrada.close();
                datosSalida.close();
                socketPeticion.close();

                //Registramos en salida en log
                System.out.println(formatter.format(new Date()) +
                "Cliente = " + ipRemitente + ": " + puertoRemitente + 
                "\tEntrada = " + entrada 
                + "\tSalida= " + salida);
            }catch (Exception e){
                System.err.println("Se ha producido la excepcion: " + e);
            }
        }

    }
}
