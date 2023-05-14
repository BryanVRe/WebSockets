import java.io.*;
import java.net.*;
import java.rmi.server.ExportException;

public class Client1 {
    private static final String _IP_SERVIDOR = "10.30.0.57";
    private static final int _PUERTO = 1234;
    private static final int _TIMEUOT = 3000;

    public static void main(String args []) throws IOException{

        //Determinado en el socket del cliente 
        Socket socket= new Socket();
        //Establezca el tiempo de espera de la conexion en 3000 ms 
        socket.setSoTimeout(_TIMEUOT);
        //Conéctese al servidro local, el numero de puerto es 2000 y el tiempo  de
        //espera es de 3000
        socket.connect(new InetSocketAddress(InetAddress.getByName(_IP_SERVIDOR),_PUERTO),
        _TIMEUOT);

        System.out.println("SE HA INICIADO LA CONEXION AL SERVIDOR");
        System.out.println("CLIENTE-IP: " + socket.getLocalAddress() + "PUERTO : " + 
        socket.getLocalPort());
        System.out.println("SERVIDOR IP: " + socket.getInetAddress() + "Puerto : " + 
        socket.getPort());

        try{
            send(socket);
        } catch (SocketException se){
            socket.close();
            System.exit(0);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error de operacion");
        }

        socket.close();
        System.out.println("El cliente termina la conexion ");
    }

    private static void send(Socket client) throws IOException{
        //flujo de entrada del teclado 
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        //flujo de salida de socket 
        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);
        
        //obtener flujo de entrada de socket 
        InputStream inputStream = client.getInputStream();
        BufferedReader socketBufferedReader = new BufferedReader(new 
        InputStreamReader(inputStream));

        boolean flag = true;

        while (flag){
            //el teclado lee una linea 
            String str = input.readLine();
            //enviar al servidor 
            socketPrintStream.println(str);

            //leer una linea del servidor 
            String echo = socketBufferedReader.readLine();
            if(echo.equalsIgnoreCase("bye")){
                flag = false;
                break;
            }else 
            System.out.println(echo);

        }
        outputStream.close();
        socketBufferedReader.close();
        inputStream.close();
        socketBufferedReader.close();
    }
}
