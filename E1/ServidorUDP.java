
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
 


/**
 *
 * @author juand
 */
public class ServidorUDP {
    //Configuramos la IP y el Puerto para el Servidor
    private static final String IP = "25.40.222.58";
    private static final int Puerto=1234;
    
    
    public static void separador(String cadena, InetAddress ip, int puerto){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        double tempambiente=0;
        double humrelativa=0;
        double CO2PPM=0;
        boolean evento=false;
        String [] cadenaSeparada=cadena.split("/");
        for(String datos:cadenaSeparada){
            String[] datosSeparados=datos.split("=");
            switch (datosSeparados[0]){
                case "TempAmbiente":
                    tempambiente=Double.parseDouble(datosSeparados[1]);
                    break;
                case "HumRelativa":
                    humrelativa=Double.parseDouble(datosSeparados[1]);
                    break;
                default:
                    CO2PPM=Double.parseDouble(datosSeparados[1]);
                    break;
            }
        }
        
        if((tempambiente<16||tempambiente>39)||humrelativa>80||CO2PPM>3000){
            evento=true;
        }
        
            Path path = Paths.get("C:/Users/carte/OneDrive/Escritorio/prueba.txt");//se puede usar para leer todas las líneas de un archivo como una secuencia. Acepta la ruta al archivo de origen y devuelve las líneas del archivo como una secuencia
            
        String datos="\nFecha: "+formatter.format(new Date())+"\tIP remitente: "+ip+"\tPuero: "+puerto+"\tTemperatura ambiente: "+tempambiente+"\tHumedad relativa: "+humrelativa+"\tCalidad aire (CO2 ppm): "+CO2PPM+"\tEvento: "+evento;
        try{
            Files.write(path,
                        datos.getBytes(),
                        StandardOpenOption.APPEND,
                        StandardOpenOption.CREATE
                    );//Proporciona un método de escritura sobrecargado para escribir int, matriz de bytes y cadena en el archivo. 
                    //También puede escribir parte de la cadena o matriz de bytes usando FileWriter. FileWriter escribe directamente en los archivos 
                } catch (IOException e) {
                    e.printStackTrace();//Se utiliza para imprimir el registro del stack donde se ha iniciado la excepción.
                }
        Logger.getLogger(ServidorUDP.class.getName()).log(Level.INFO, datos);
    
      
    }
    
    public static void main (String args[])throws UnknownHostException{
        //Creamos instancia de clase InetAddress para indicar
        //El host donde se inicia el servidor
        
        InetAddress ip= InetAddress.getByName(IP);
        
        //Mostramos por consola los datos del servidor
        try{
            //Si se usa localhost
            System.out.println("IP de Localhost = "+InetAddress.getLocalHost().toString());
            //Si se una una ip
            System.out.println("\nEscuchando en: ");
            System.out.println("IP Host =" + ip.getHostAddress());
            System.out.println("Puerto = "+ Puerto +"\n");
        }catch(UnknownHostException ex){
            System.err.println("No puedo saber la direccion IP Local: "+ex);
        }
        
        //A traves de este Socket enviaremos datagramas del tipo DatagramPacket
        DatagramSocket dgmSocket = null;
        try{
            //ds = new DatagramSocket(Puerto); //<-- use esta linea su trabaja con localhost
            dgmSocket = new DatagramSocket(Puerto, ip); //<--Use esta linea para una IP especifica
        }catch(SocketException se){
            System.err.println("Se ha producido un error al abrir el socket: "+se);
            System.exit(-1);
        }
        
        //Bucle infinito de escucha
        while(true){
            try{
                
                byte bufferEntrada[] = new byte[1024];
                
                //Creamos un "contenedor" de datagrama, cuyo buffer
                //sera el array creado antes
                DatagramPacket dgmPaquete = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                
                //Esperamos a recibir un paquete
                dgmSocket.receive(dgmPaquete);
                
                //Pdemos extraer información del paquete:
                
                //No. de puerto desde donde se envió
                int puertoRemitente = dgmPaquete.getPort();
                //Dirección de  Internet desde donde se envió
                InetAddress ipRemitente= dgmPaquete.getAddress();
                
                //"Envolvemos" el buffer con ByteArrayInputStream...
                ByteArrayInputStream arrayEntrada = new ByteArrayInputStream(bufferEntrada);
                //... que volvemos a "envolver" con un DataInputStream
                DataInputStream datosEntrada = new DataInputStream(arrayEntrada);
                // y leemos un número entero a partir del array de bytes
                String entrada = datosEntrada.readUTF();
                
                separador(entrada, ipRemitente, puertoRemitente);
                
                //Creamos un ByteArrayOutputStream sobre el que podamos escribir
                ByteArrayOutputStream arraySalida = new ByteArrayOutputStream();
                //lo envolvemos con un DataOutputStream
                DataOutputStream datosSalida = new DataOutputStream(arraySalida);
                //Escribimos el resultado que debe ocupar 8 bytes
                datosSalida.writeUTF("Info guardada con exito.");
                
                //Cerramos el buffer de escritura
                datosSalida.close();
                
                //Generamos el paquete de vuelta, usando los datos del remitente del paqueete original
                dgmPaquete = new DatagramPacket(arraySalida.toByteArray(), arraySalida.toByteArray().length, ipRemitente, puertoRemitente);
                //Enviamos
                dgmSocket.send(dgmPaquete);
                
                //Registramos en salida estandard
                /*System.out.println(formatter.format(new Date())+
                        "\tCliente = "+ ipRemitente+"\tEntrada = "+
                        entrada + "\tSalida ="+salida);*/
            }catch(Exception e){
                System.err.println("Se ha producido el error "+ e);
            }
        }
                
    }
}
