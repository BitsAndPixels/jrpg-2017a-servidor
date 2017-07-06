package comando;

import java.io.IOException;

import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;
import servidor.Servidor;

public class CreacionPj extends ComandoServidor{
	private PaqueteUsuario paqueteUsuario;
	
	@Override
	public void ejecutarComando() {
		// Casteo el paquete personaje
		this.paqueteUsuario = listener.getPaqueteUsuario();

		// Guardo el personaje en ese usuario
		Servidor.getConector().registrarPersonaje((PaquetePersonaje) paquete, paqueteUsuario);

		// Le envio el id del personaje
		try {
			this.listener.getSalida().writeObject(((PaquetePersonaje) paquete).obtenerJson());
		} catch (IOException e) {
			Servidor.log.append("Fallo al intentar crear el usuario" + paqueteUsuario.getUsername()  + System.lineSeparator());
			e.printStackTrace();
		}

	}

}
