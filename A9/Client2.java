import java.io.IOException;
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;

public class Client2 {
    private static final String _IP_SERVIDOR = "192.168.100.20";
    private static final int _PUERTO = 1234;
    private static final int _TIMEUOT = 3000;
    private static final int _PAQUETES = 120;

    public static void main(String args []) throws IOException{

        //Determiando en el socket del cliente 
        Socket socket = new Socket();
        // establezca el tiempo de espera de la conexion en 3000 ms 
        socket.setSoTimeout(_TIMEUOT);
        //conectese al servidor local, el numero de puerto es 2000 y el tiempo de
        // espera es de 3000 
        socket.connect(new InetSocketAddress(InetAddress.getByName(_IP_SERVIDOR), _PUERTO),
        _TIMEUOT);
        System.out.println("Se ha iniciado la conexion al servidor");
        System.out.println("Cliente IP: " + socket.getLocalAddress() + "Puerto: " + 
        socket.getLocalPort());
        System.out.println("Servidor IP: " + socket.getInetAddress() + "Puerto: " + 
        socket.getPort());

        try{
            send(socket);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error de operacion");
        }
        socket.close();
        System.out.println("Cliente : -> DESCONECTADO ");
    }

    private static void send(Socket client) throws IOException, InterruptedException{

        //flujo de salida de socket 
        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);

        String data = "";

        //obtener flujo de entrada socket 
        InputStream inputStream = client.getInputStream();
        BufferedReader sockBufferedReader = new BufferedReader(new
            InputStreamReader(inputStream));

            for (int i=0; i < _PAQUETES; i++){
                //simulamos obtencion de variables 
                double temperatura =Math.round((Math.random()*40 + 16 ) * 100.00)/100.00;
                int humedad = (int) Math.random() * 1 + 99;
                double Co2 = Math.round((Math.random() * 200 + 50000) * 100.00)/ 100.00;
                
                DecimalFormat df = new DecimalFormat("#####.##");

                data = "$Temp|" + df.format(temperatura) + "#Hum|" + df.format(humedad) + "%#Co2" + 
                df.format(Co2) + "$";
                socketPrintStream.println(data);
                String echo = sockBufferedReader.readLine();
                System.out.println("Cliente : " + data);
                System.out.println(echo);
                Thread.sleep(10000);
            }

            System.out.println("Cliente: TOKEN 'bye'");
            socketPrintStream.println("bye");
            System.out.println("Cliente: PROCESO FINALIZADO ");
    }
}
