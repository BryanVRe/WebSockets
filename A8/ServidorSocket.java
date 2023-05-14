import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ServidorSocket {
    
    //configuramoa la IP y el puerto para el servidor 

    private static final String _IP="192.168.137.145";
    //private static final String _IP = "192.168.1.68"
    private static final int _PUERTO=1234;
    private static final int _BACKLOG=50;
    
    
     
    
    public static void main(String args []) throws UnknownHostException{
        //creamos instancia de clase InetAddress para indicar 
        // el host donde se indica el servidor 
        InetAddress ip = InetAddress.getByName(_IP);

        //usamos un manejador de formato para el log del servidor 
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS");

        //primero indicamos la direccion ip Local 
        try{
            //si se usa localhost: 
            System.out.println("IP de LocalHost = " + InetAddress.getLocalHost().toString());
            //si se usa una ip:
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

        //bucle infinito
        while (true){
            try{
                //esperamos a que alguien se conecte a nuestro socket 
                Socket socketPeticion= serverSocket.accept();

                //extraemos los flujos de entrada y salida 
                DataInputStream datosEntrada = new DataInputStream(
                    new BufferedInputStream(socketPeticion.getInputStream()));
                    DataOutputStream datosSalida= new DataOutputStream(socketPeticion.getOutputStream());

                    //podemos extraer informacion del socket 
                    //No de puerto remoto 
                    int puertoRemitente= socketPeticion.getPort();
                    //direccion de internet remota 
                    InetAddress ipRemitente = socketPeticion.getInetAddress();

                    //leemos el tipo de dato del mensaje 
                    char tipoDato=datosEntrada.readChar();

                    //obtenemos la longitud del mensaje 
                    int longitud = datosEntrada.readInt();

                    if(tipoDato == 's'){

                        //Arreglo de bytes que contienen la data del mensaje 
                        
                        
                        byte[] bytesDatos = new byte[longitud];

                        //Bandera para determinar que el contenido de la data ha sido leido 
                        boolean finData = false;

                        //ussamos StringBuiler para armar el mensaje en la data del paquete 
                        Path path = Paths.get("C:/Users/carte/OneDrive/Escritorio/prueba.txt");
                        StringBuilder dataEnMensaje=new StringBuilder(longitud);
                       
                        //inicializamos un acumulador de datos leidos 
                        int totalBytesLeidos= 0; 

                        //comenzamos lectura hasta ñlegar al final de los datos 
                        while(!finData){
                            //leeemos los bytes de la data 
                            int bytesActualesLEidos = datosEntrada.read(bytesDatos  );

                            //Actualixamos el contador de la lectura de datos 
                            totalBytesLeidos=bytesActualesLEidos+totalBytesLeidos;

                            //construimos el mensaje con String Builder 
                            if(totalBytesLeidos <= longitud){
                                dataEnMensaje.append(new String(bytesDatos,0,bytesActualesLEidos,StandardCharsets.UTF_8));
                            }else{
                                dataEnMensaje.append(new String(bytesDatos,0,
                                longitud-totalBytesLeidos+bytesActualesLEidos,StandardCharsets.UTF_8));

                            }

                            //DETERMINAMOS SI TODA LA DATA HA SIDO LEIDA 
                            if(dataEnMensaje.length()>=longitud)
                            finData=true;
                                
                        }
                        
                        try{
                            Files.write(path,
                                         bytesDatos,
                                        StandardOpenOption.APPEND,
                                        StandardOpenOption.CREATE
                                    );//Proporciona un método de escritura sobrecargado para escribir int, matriz de bytes y cadena en el archivo. 
                                    //También puede escribir parte de la cadena o matriz de bytes usando FileWriter. FileWriter escribe directamente en los archivos 
                                } catch (IOException e) {
                                    e.printStackTrace();//Se utiliza para imprimir el registro del stack donde se ha iniciado la excepción.
                                }

                        //Regsitramos en salida en log 
                        System.out.println(formatter.format(new Date()) +  "\tCliente= " + ipRemitente + ":" + puertoRemitente 
                        + "\tEntrada = " + dataEnMensaje.toString() + "\tSalida = " + "OK" );


                    }
               //Escribimos el resultado 
               datosSalida.writeUTF("OK");

               //cerramos los flujos 
               datosEntrada.close();
               datosSalida.close();
               socketPeticion.close();
            }catch (Exception e){
                System.err.println("Se ha producido la excepcion : " + e);
            }
        }

    }
}
