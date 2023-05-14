import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClienteSocket {

    private static final int _PUERTO=1234;

    public static void main(String args []){
        //Leemos el primer parametro, donde debe ir la direccion 
        // IP del servidor 
        InetAddress ipServidor= null;
        try{
            ipServidor=InetAddress.getByName(args[0]);
        }catch (UnknownHostException uhe){
            System.err.println("Host no encontrado: " + uhe);
            System.exit(-1);
        }

        //determinamos el numero de paquetes a enviar 
       // int noPaquetes=Integer.parseInt(args[1]);

        //para cada uno de los argumentos ... 
        for(;;){

            //por cada valor a procesar configuramos las clases
            //para envio y recepcion de datos 
            Socket socketCliente=null;
            DataInputStream datosRecepcion=null;
            DataOutputStream datosEnvio=null;

            //comenzamos procesos de comunicacion 

            try{
                
                //simulamos obtencion de variables 
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS");
                double temperatura=Math.round((Math.random()*40+16)*100.0)/100.0;
                int humedad = (int)Math.random()*1+99;
                double Co2=Math.round((Math.random()*200+50000)*100.0)/100.0;

                //Establecemos los valores para el paquete segun protocolo 
                char tipo='s';//s para string 
                
                String data= "fecha: " + formatter.format(new Date()) +  "IPRemitente= " +  ipServidor + " Puerto = " +  _PUERTO + "  #Temperatura" + temperatura + " #Humumedad" + humedad + " #Co2" + Co2 + "\n";
                byte[] dataInBytes=data.getBytes(StandardCharsets.UTF_8);

                //CREAMOS EL SOCKET
                socketCliente=new Socket(ipServidor, _PUERTO);

                //Extraemos los stream de entrada y salida 
                datosRecepcion=new DataInputStream(socketCliente.getInputStream());
                datosEnvio=new DataOutputStream(socketCliente.getOutputStream());

                //Escribimos datos en el flujo segun protocolo 
                datosEnvio.writeChar(tipo);
                datosEnvio.writeInt(dataInBytes.length);
                datosEnvio.write(dataInBytes);

                //leemos el resultado final 
                String resultado = datosRecepcion.readUTF();

                //Indicamos en pantalla 
                System.out.println("Solicitud = " + data + "\tResultado = " + resultado);

                //y cerramos los stream y el socket 
                datosRecepcion.close();
                datosEnvio.close();

               Thread.sleep(10000);
            }catch (Exception e){
                System.err.println("Se ha producido la excepcion : " + e);
            }
            try{
                if(socketCliente !=null)
                socketCliente.close();
            }catch (IOException ioe){
                System.err.println("Error al cerrar el socket : " + ioe);
            }

        }
    }
    
}
