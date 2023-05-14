
import java.net.*;
import java.io.*;

class clienteUDP {
	private static final int _PUERTO = 1234;
	public static void main(String[] args) {
		//leemos el primer parametro donde debe ir la direccion ip del servidor
		InetAddress ipServidor = null;
		try {
			ipServidor = InetAddress.getByName(args[0]);
		} catch(UnknownHostException uhe) {
			System.err.println("Host no encontrado: " + uhe);
			System.exit(-1);
		}

		//creamos el socket
		DatagramSocket dgmSocket = null;
		try {
			dgmSocket = new DatagramSocket();
		} catch(SocketException se) {
			System.err.println("Error al abrir el socket: " + se);
			System.exit(-1);
		}

		//para cada uno de los argumentos ...
		for (int n=1 ;n < args.length ; n++ ) {
			try {
				//creamos un buffer para escribir 
				ByteArrayOutputStream arrayEnvio = new ByteArrayOutputStream();
				//envolvemos el buffer en el flujo de datos de salida
				DataOutputStream datosEnvio = new DataOutputStream(arrayEnvio);

				//convertimos el texto en mumero 
				int numero = Integer.parseInt(args[n]);
				//Escribimos en el flujo
				datosEnvio.writeInt(numero);
				//y cerramos el buffer
				datosEnvio.close();

				//creamos el paquete
				DatagramPacket dgmPackete = new DatagramPacket(arrayEnvio.toByteArray(), 4, ipServidor, _PUERTO);
				//y lo mandamos
				dgmSocket.send(dgmPackete);

				//preparamos buffer para recibir numerode 8 bytes
				byte bufferEntrada[] = new byte[8];
				//creamos el contenedor del paquete
				dgmPackete = new DatagramPacket(bufferEntrada, 8);
				//y lo recibimos
				dgmSocket.receive(dgmPackete);

				//creamos un stream de lectura a partir del buffer
				ByteArrayInputStream arrayRecepcion = new ByteArrayInputStream(bufferEntrada);
				DataInputStream datosEntrada = new DataInputStream(arrayRecepcion);
				//leemos el resultado final 
				long resultado = datosEntrada.readLong();
				//indicamos en pantalla
				System.out.println("Enviado = "+numero+"/tRecibido = "+resultado);
			} catch(Exception e) {
				System.err.println("Se ha producido un error: "+ e);
			}
		}
	}
}