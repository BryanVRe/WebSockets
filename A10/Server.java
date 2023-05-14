import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Server{
    //configuramos la ip y el puerto para el servidor 
    private static final String _IP="192.168.137.145";
    //private static final String _IP= "192.168.100.20"(es la red de mi casa XD )
    private static final int _PUERTO=1234;
    private static final int _BACKLOG=50;

    //FORMATO DE SALIDA POR CONSOLA 
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CLEAR = "\033[H\033[2J";

    public static void main(String [] args) throws IOException{

        InetAddress ip = InetAddress.getByName(_IP);
        ServerSocket serverSocket = new ServerSocket(_PUERTO, _BACKLOG, ip);

        try{
            System.out.println(ANSI_CLEAR);
            System.out.println(ANSI_GREEN + 
            " SERVIDOR INICIADO . . . OK " + ANSI_RESET);
            System.out.println(ANSI_CYAN + 
            " INFORMACION DEL SERVIDOR: " + ANSI_RESET + ANSI_YELLOW + 
            serverSocket.getInetAddress() + ": " + serverSocket.getLocalPort() + 
            ANSI_RESET);
            System.out.println("**************************************************************************");
            
            for(;;){
                Socket client= serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(client);
                clientHandler.start();

            }

        }catch(Exception e){
            System.out.println(e);
        }finally{
            serverSocket.close();
        }
    }
     private static class ClientHandler extends Thread{
        //Recibe un socket de una solicitud 
        private Socket socket;
        //bandera de terminacion 
        private boolean flag;

        //recibe un conector para la construccion  y establece el indicador del ciclo en 
        //verdadero 
        ClientHandler(Socket socket){
            this.socket=socket;
            flag=true;
        }

        //Método de ejecución de reescritura 
        @Override
        public void run(){
            super.run();
            System.out.println(
                " CLIENTE: " + socket.getInetAddress() + ": " + 
                socket.getPort() + ANSI_GREEN + "- > SE CONECTO AL SERVIDOR" + ANSI_RESET);

                try{
                    String nombreHilo = this.currentThread().getName();
                    String data="";

                    //Secuencia de impresión, utilizada para que el servidor  envíe datos 
                    PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                    //obtener el flujo de entrada para recibir datos 
                    BufferedReader socketInput = new BufferedReader(new
                     InputStreamReader(socket.getInputStream()));

                     //spin, termina cuando recibe la cadena "bye " del cliente
                     while(flag){
                        //recibir ina linea del requestbody enviada por el cliente 
                        String str = socketInput.readLine();
                        if (str !=null){
                            //si el cliente envia "adiós", establezca la bandera en falso  y cierre 
                            //recurso 
                            if("bye".equalsIgnoreCase(str)){
                                flag=false;
                                socketOutput.println(
                                    "EL CLIENTE: " + ANSI_YELLOW + socket.getInetAddress() + 
                                    " FINALIZO LA CONEXION " + ANSI_RESET);

                            }else{
                                //Salida de la informacion y los caracteres enviados por el cliente 

                                //consola

                             
                                Path path = Paths.get("C:/Users/carte/OneDrive/Escritorio/prueba.txt");
                                 data= "\nSERVIDOR: " + ANSI_GREEN + 
                                "IP: " + socket.getInetAddress() + ": " + socket.getPort() + 
                                ANSI_RESET + ANSI_YELLOW + "\t data: " + str + ANSI_RESET + 
                                ANSI_CYAN + "\t hilo: " + nombreHilo;
                                System.out.println("SERVIDOR: " + ANSI_GREEN + 
                                "IP: " + socket.getInetAddress() + ": " + socket.getPort() + 
                                ANSI_RESET + ANSI_YELLOW + "\t data: " + str + ANSI_RESET + 
                                ANSI_CYAN + "\t hilo: " + nombreHilo);
                                socketOutput.println("Servidor: OK");
                                //Devuelve  respuesta al cliente 
                                try{
                                    Files.write(path,
                                                 data.getBytes(),
                                                StandardOpenOption.APPEND,
                                                StandardOpenOption.CREATE
                                            );//Proporciona un método de escritura sobrecargado para escribir int, matriz de bytes y cadena en el archivo. 
                                            //También puede escribir parte de la cadena o matriz de bytes usando FileWriter. FileWriter escribe directamente en los archivos 
                                        } catch (IOException e) {
                                            e.printStackTrace();//Se utiliza para imprimir el registro del stack donde se ha iniciado la excepción.
                                        }
                    
                            }
                        }
                     }
                     //CERRAR IO 
                     socketInput.close();
                     socketOutput.close();
                     socket.close();

                }catch(SocketException ex){
                    System.out.println(ANSI_RED + "ERROR: " + ex.getMessage() + ANSI_RESET);
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("Conexion anormal");
                } finally{
                    try{ 
                        //cerrar la conexión del socket 
                        socket.close();
                    }catch (IOException e){
                    }
                    System.out.println("EL CLIENTE : " + socket.getInetAddress() + 
                    ANSI_RED + "-> SE DESCONECTO DEL SERVIDOR  " + ANSI_RESET);
                }
        } 
     }
}