package comando;

import java.io.IOException;

import mensajeria.Paquete;
import mensajeria.PaqueteUsuario;
import servidor.Servidor;

public class Registro extends ComandoServidor{
	private PaqueteUsuario paqueteUsuario;
	@Override
	public void ejecutarComando() {
		
		this.paqueteUsuario = (PaqueteUsuario) paquete;
		// Paquete que le voy a enviar al usuario
		paqueteUsuario.setComando(Comando.REGISTRO);

		// Si el usuario se pudo registrar le envio un msj de exito
		if (Servidor.getConector().registrarUsuario(paqueteUsuario)) {
			paqueteUsuario.setMensajeChat(Paquete.msjExito);
			this.listener.setPaqueteUsuario(paqueteUsuario);
			// Si el usuario no se pudo registrar le envio un msj de
			// fracaso
		} else {
			paqueteUsuario.setMensajeChat(Paquete.msjFracaso);
		}
		
		try {
			this.listener.getSalida().writeObject(paqueteUsuario.obtenerJson());
		} catch (IOException e) {
			Servidor.log.append("Fallo al intentar registrarse." + System.lineSeparator());
			e.printStackTrace();
		}
	}
}
