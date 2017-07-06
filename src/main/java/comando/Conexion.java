package comando;

import mensajeria.PaqueteMovimiento;
import mensajeria.PaquetePersonaje;
import servidor.Servidor;

public class Conexion extends ComandoServidor{
	
	private PaquetePersonaje paquetePersonaje;
	
	@Override
	public void ejecutarComando() {
	
		this.paquetePersonaje = (PaquetePersonaje) paquete;
		listener.setPaquetePersonaje(paquetePersonaje);

		Servidor.getPersonajesConectados().put(paquetePersonaje.getId(), paquetePersonaje);
		Servidor.getUbicacionPersonajes().put(paquetePersonaje.getId(), (PaqueteMovimiento) new PaqueteMovimiento(paquetePersonaje.getId()));
		
		synchronized (Servidor.atencionConexiones) {
			Servidor.atencionConexiones.notify();
		}
	}
	
	

}
