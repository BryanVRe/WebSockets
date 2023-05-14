import java.io.*;
import java.net.*;

class ClienteTCP{
    private static final int _PUERTO = 1234;
    public static void main(String args []){
        //leemos el primer parametro, donde debe ir la direccion 
        //IP del servidor 
        InetAddress ipServidor=null;
        try{
            ipServidor= InetAddress.getByName(args[0]);
        }catch(UnknownHostException uhe){
            System.err.println("Host no encontrado : " + uhe);
            System.exit(-1);
        }
        //para cada uno de los argumentos 

        for(int n = 1; n<args.length;n++){
            //por cada valor a procesar configuramos las clases 
            //para envio y recepcion de datos 
            Socket socketCliente = null;
            DataInputStream datosRecepcion=null;
            DataOutputStream datosEnvio=null;

            //comenzamos proceso de comunicacion 
            try{
                //convertimos el texto en numero 
                int numero=Integer.parseInt(args[n]);

                //creamos el socket 
                socketCliente= new Socket(ipServidor, _PUERTO);

                //Extraemos los stream de entrada y salida 

                datosRecepcion= new DataInputStream(socketCliente.getInputStream());
                datosEnvio= new DataOutputStream(socketCliente.getOutputStream());

                //lo escribimos
                datosEnvio.writeInt(numero);

                //leemos el resultado final 
                long resultado=datosRecepcion.readLong();

                //indicamos en pantalla 
                System.out.println("Solicitud = " + numero + "\tResultado = " + resultado);

                // y cerramos los stream y el socket 
                datosRecepcion.close();
                datosEnvio.close();
            }catch (Exception e){
                System.err.println("Se ha producido la excepcion : " + e);
            }
            try{
                if(socketCliente !=null)
                socketCliente.close();
            }catch(IOException ioe){
                System.err.println("Error al cerrar el socket: " + ioe);
        
          }

       }
    }
}

   