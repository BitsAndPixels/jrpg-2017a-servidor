package comando;

import mensajeria.PaquetePersonaje;
import servidor.Servidor;

public class MostrarMapas extends ComandoServidor{
	private PaquetePersonaje paquetePersonaje;
	
	@Override
	public void ejecutarComando() {
		// Indico en el log que el usuario se conecto a ese mapa
//		paquetePersonaje = (PaquetePersonaje) gson.fromJson(cadenaLeida, PaquetePersonaje.class);
		this.paquetePersonaje = (PaquetePersonaje) paquete;
//		Servidor.log.append(socket.getInetAddress().getHostAddress() + " ha elegido el mapa "
//				+ paquetePersonaje.getMapa() + System.lineSeparator());
		Servidor.log.append(this.paquetePersonaje.getIp() + " ha elegido el mapa "
				+ paquetePersonaje.getMapa() + System.lineSeparator());
		
	}

}
