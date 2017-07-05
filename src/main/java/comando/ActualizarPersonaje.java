package comando;

import java.io.IOException;

import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ActualizarPersonaje extends ComandoServidor{
	private PaquetePersonaje paquetePersonaje;
	
	@Override
	public void ejecutarComando() {
//		paquetePersonaje = (PaquetePersonaje) gson.fromJson(cadenaLeida, PaquetePersonaje.class);
		this.paquetePersonaje = (PaquetePersonaje) paquete;
		Servidor.getConector().actualizarPersonaje(paquetePersonaje);

		Servidor.getPersonajesConectados().remove(paquetePersonaje.getId());
		Servidor.getPersonajesConectados().put(paquetePersonaje.getId(), paquetePersonaje);

		for (EscuchaCliente clienteConectado : Servidor.getClientesConectados()) {
//			conectado.getSalida().writeObject(gson.toJson(paquetePersonaje));
			try {
				clienteConectado.getSalida().writeObject(paquetePersonaje.getJson());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
