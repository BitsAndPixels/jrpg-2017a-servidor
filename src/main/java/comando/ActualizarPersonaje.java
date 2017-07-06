package comando;

import java.io.IOException;

import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ActualizarPersonaje extends ComandoServidor{
	private PaquetePersonaje paquetePersonaje;
	
	@Override
	public void ejecutarComando() {
		this.paquetePersonaje = (PaquetePersonaje) paquete;
		Servidor.getConector().actualizarPersonaje(paquetePersonaje);

		Servidor.getPersonajesConectados().remove(paquetePersonaje.getId());
		Servidor.getPersonajesConectados().put(paquetePersonaje.getId(), paquetePersonaje);

		for (EscuchaCliente clienteConectado : Servidor.getClientesConectados()) {
			try {
				clienteConectado.getSalida().writeObject(paquetePersonaje.obtenerJson());
			} catch (IOException e) {
				Servidor.log.append("Fallo al intentar actualizar el personaje" + paquetePersonaje.getNombre()  + System.lineSeparator());
				e.printStackTrace();
			}
		}
	}

}
