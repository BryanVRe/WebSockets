/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author juand
 */
public class ClienteUDP {
    private static final int PORT=1234;
    
    public static String generacionVariables(){
        double tempambiente=0;
        double humrelativa=0;
        double CO2PPM=0;
        tempambiente=Math.random()*(100-0)+0;
        humrelativa=Math.random()*(100-0)+0;
        CO2PPM=Math.random()*(10000-0)+0;
        
        String cadena= "TempAmbiente="+tempambiente+"/HumRelativa="+humrelativa+"/CalidadAire(CO2PPM)="+CO2PPM;
        
        return cadena;
    }
    
    public static void main(String args[]){
        InetAddress IPServer=null;
        DatagramSocket dtgSocket=null;
        byte bufferEntrada[];
        
        try{
            IPServer=InetAddress.getByName(args[0]);
            dtgSocket = new DatagramSocket();
        }catch(UnknownHostException UHE){
            Logger.getLogger(ClienteUDP.class.getName()).log(Level.SEVERE, null, UHE);
            System.exit(-1);
        } catch (SocketException SE) {
            Logger.getLogger(ClienteUDP.class.getName()).log(Level.SEVERE, null, SE);
        }
        
        while(true){
            try{
                //creamos un buffer para escribir
                ByteArrayOutputStream arrayEnvio = new ByteArrayOutputStream();
                //Envolvemos el buffer en un flujo de datos de salida
                DataOutputStream datosEnvio = new DataOutputStream(arrayEnvio);
                

                //Escribimos el flujo
                String salida=generacionVariables();
                datosEnvio.writeUTF(salida);
                // y cerramos el buffer
                datosEnvio.close();
                
                //Creamos paquete
                DatagramPacket dtgPaquete = new DatagramPacket(arrayEnvio.toByteArray(), arrayEnvio.toByteArray().length, IPServer, PORT);
                
                //y lo mandamos
                dtgSocket.send(dtgPaquete);
                
                
                
                bufferEntrada=new byte[1024];
                //Creamos el contenedor del paquete
                dtgPaquete = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                //y lo recibimos
                dtgSocket.receive(dtgPaquete);
                
                //Creamos un stream de lectura a partir del buffer
                ByteArrayInputStream arrayRecepcion = new ByteArrayInputStream(bufferEntrada);
                DataInputStream datosEntrada = new DataInputStream(arrayRecepcion);
                
                //Leemos el resultado final
                String resultado=datosEntrada.readUTF();
                
                //Indicamos an pantalla
                System.out.println(resultado);
                
                Thread.sleep(10000);
            }catch(Exception e){
                System.err.print("\nSe ha producido un error: "+e);
            }
        }
    }
}
