package comando;

import java.io.IOException;

import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;
import servidor.Servidor;

public class CreacionPj extends ComandoServidor{
	@Override
	public void ejecutarComando() {
		// Casteo el paquete personaje
//		paquetePersonaje = (PaquetePersonaje) (gson.fromJson(cadenaLeida, PaquetePersonaje.class));
		
		PaqueteUsuario paqueteUsuario = listener.getPaqueteUsuario();

		// Guardo el personaje en ese usuario
		Servidor.getConector().registrarPersonaje((PaquetePersonaje) paquete, paqueteUsuario);

		// Le envio el id del personaje
//		salida.writeObject(gson.toJson(paquetePersonaje, paquetePersonaje.getClass()));
		try {
			this.listener.getSalida().writeObject(((PaquetePersonaje) paquete).getJson());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
