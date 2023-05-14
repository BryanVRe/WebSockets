import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server{
    //configuramos la ip y el puerto para el servidor 
    private static final String _IP="192.168.100.20";
    //private static final String _IP= "192.168.100.21"
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
            "Servidor iniciado . . . OK" + ANSI_RESET);
            System.out.println(ANSI_CYAN + 
            "informacion del servidor : " + ANSI_RESET + ANSI_YELLOW + 
            serverSocket.getInetAddress() + ": " + serverSocket.getLocalPort() + 
            ANSI_RESET);
            System.out.println("-------------------------------------------------");
            
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
                "Cliente: " + socket.getInetAddress() + ": " + 
                socket.getPort() + ANSI_GREEN + "- > CONECTADO " + ANSI_RESET);

                try{
                    String nombreHilo = this.currentThread().getName();

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
                                    "Cliente: " + ANSI_YELLOW + socket.getInetAddress() + 
                                    " Termino la conexion " + ANSI_RESET);

                            }else{
                                //Salida de la informacion y los caracteres enviados por el cliente 

                                //consola 
                                System.out.println("Servidor: " + ANSI_GREEN + 
                                "IP: " + socket.getInetAddress() + ": " + socket.getPort() + 
                                ANSI_RESET + ANSI_YELLOW + "\t data: " + str + ANSI_RESET + 
                                ANSI_CYAN + "\t hilo: " + nombreHilo);
                                //Devuelve  respuesta al cliente 
                                socketOutput.println("Servidor: OK");
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
                    System.out.println("Cliente : " + socket.getInetAddress() + 
                    ANSI_RED + "-> DESCONECTADO " + ANSI_RESET);
                }
        } 
     }
}