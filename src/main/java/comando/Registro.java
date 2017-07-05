package comando;

import java.io.IOException;

import mensajeria.Paquete;
import mensajeria.PaqueteUsuario;
import servidor.Servidor;

public class Registro extends ComandoServidor{
	@Override
	public void ejecutarComando() {
		
		PaqueteUsuario paqueteUsuario = (PaqueteUsuario) paquete;
		// Paquete que le voy a enviar al usuario
		paqueteUsuario.setComando(Comando.REGISTRO);

//		paqueteUsuario = (PaqueteUsuario) (gson.fromJson(cadenaLeida, PaqueteUsuario.class)).clone();

		// Si el usuario se pudo registrar le envio un msj de exito
		if (Servidor.getConector().registrarUsuario(paqueteUsuario)) {
			paqueteUsuario.setMensajeChat(Paquete.msjExito);
//			salida.writeObject(gson.toJson(paqueteSv));
			this.listener.setPaqueteUsuario(paqueteUsuario);
			// Si el usuario no se pudo registrar le envio un msj de
			// fracaso
		} else {
			paqueteUsuario.setMensajeChat(Paquete.msjFracaso);
//			salida.writeObject(gson.toJson(paqueteSv));
		}
		
		try {
			this.listener.getSalida().writeObject(paqueteUsuario.getJson());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
