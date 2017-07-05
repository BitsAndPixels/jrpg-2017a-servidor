package comando;

import mensajeria.PaqueteMovimiento;
import mensajeria.PaquetePersonaje;
import servidor.Servidor;

public class Conexion extends ComandoServidor{
	
	private PaquetePersonaje paquetePersonaje;
	
	@Override
	public void ejecutarComando() {
//		paquetePersonaje = (PaquetePersonaje) (gson.fromJson(cadenaLeida, PaquetePersonaje.class)).clone();
		
		this.paquetePersonaje = (PaquetePersonaje) paquete;
		listener.setPaquetePersonaje(paquetePersonaje);

//		Servidor.getPersonajesConectados().put(paquetePersonaje.getId(),
//				(PaquetePersonaje) paquetePersonaje.clone());
//		Servidor.getUbicacionPersonajes().put(paquetePersonaje.getId(),
//				(PaqueteMovimiento) new PaqueteMovimiento(paquetePersonaje.getId()).clone());
		Servidor.getPersonajesConectados().put(paquetePersonaje.getId(), paquetePersonaje);
		Servidor.getUbicacionPersonajes().put(paquetePersonaje.getId(), (PaqueteMovimiento) new PaqueteMovimiento(paquetePersonaje.getId()));
		
		synchronized (Servidor.atencionConexiones) {
			Servidor.atencionConexiones.notify();
		}
	}
	
	

}
