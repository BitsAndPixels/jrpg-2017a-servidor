package comando;

import java.io.IOException;

import mensajeria.Paquete;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;
import servidor.Servidor;

public class InicioSesion extends ComandoServidor{
	private PaqueteUsuario paqueteUsuario;
	private PaquetePersonaje paquetePersonaje;
	
	@Override
	public void ejecutarComando() {
		
		Paquete paqueteSv = new Paquete();
		paqueteSv.setComando(Comando.INICIOSESION);

		// Recibo el paquete usuario
		this.paqueteUsuario = (PaqueteUsuario) paquete;

		// Si se puede loguear el usuario le envio un mensaje de
		// exito y el paquete personaje con los datos
		if (Servidor.getConector().loguearUsuario(paqueteUsuario)) {

			paquetePersonaje = new PaquetePersonaje();
			paquetePersonaje = Servidor.getConector().getPersonaje(paqueteUsuario);
			paquetePersonaje.setComando(Comando.INICIOSESION);
			paquetePersonaje.setMensajeChat(Paquete.msjExito);
			listener.setIdPersonaje(paquetePersonaje.getId());

			try {
				listener.getSalida().writeObject(paquetePersonaje.obtenerJson());
			} catch (IOException e) {
				Servidor.log.append("Fallo al intentar iniciar sesion." + System.lineSeparator());
				e.printStackTrace();
			}

		} else {
			paqueteSv.setMensajeChat(Paquete.msjFracaso);
			try {
				listener.getSalida().writeObject(paqueteSv.obtenerJson());
			} catch (IOException e) {
				Servidor.log.append("Fallo al intentar iniciar sesion." + System.lineSeparator());
				e.printStackTrace();
			}
		}
		
// --------------------------------------		
		
//		Paquete paqueteSv = new Paquete();
//		paqueteSv.setComando(Comando.INICIOSESION);
//
//		// Recibo el paquete usuario
////		paqueteUsuario = (PaqueteUsuario) (gson.fromJson(cadenaLeida, PaqueteUsuario.class));
//		PaqueteUsuario paqueteUsuario = (PaqueteUsuario) paquete;
//
//		// Si se puede loguear el usuario le envio un mensaje de
//		// exito y el paquete personaje con los datos
//		if (Servidor.getConector().loguearUsuario(paqueteUsuario)) {
//
//			PaquetePersonaje paquetePersonaje = new PaquetePersonaje();
//			paquetePersonaje = Servidor.getConector().getPersonaje(paqueteUsuario);
//			paquetePersonaje.setComando(Comando.INICIOSESION);
//			paquetePersonaje.setMensajeChat(Paquete.msjExito);
//			listener.setIdPersonaje(paquetePersonaje.getId());
//
//			listener.setPaqueteUsuario(paqueteUsuario);
//			listener.setPaquetePersonaje(paquetePersonaje);
//			
////			salida.writeObject(gson.toJson(paquetePersonaje));
//
//		} else {
//			paqueteSv.setComando(Comando.INICIOSESION);
//			paqueteSv.setMensajeChat(Paquete.msjFracaso);
////			salida.writeObject(gson.toJson(paqueteSv));
//		}
//		
//		try {
//			listener.getSalida().writeObject(paqueteSv.getJson());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

}
