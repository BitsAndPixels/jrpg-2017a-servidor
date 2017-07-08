package comando;

import java.io.IOException;

import estados.Estado;
import mensajeria.PaqueteFinalizarBatalla;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class FinalizarBatalla extends ComandoServidor {
	
	private PaqueteFinalizarBatalla paqueteFinalizarBatalla;

	@Override
	public void ejecutarComando() {

		this.paqueteFinalizarBatalla = (PaqueteFinalizarBatalla) paquete;
		Servidor.getPersonajesConectados().get(paqueteFinalizarBatalla.getId())
				.setEstado(Estado.estadoJuego);
		Servidor.getPersonajesConectados().get(paqueteFinalizarBatalla.getIdEnemigo())
				.setEstado(Estado.estadoJuego);
		for (EscuchaCliente clienteConectado : Servidor.getClientesConectados()) {
			if (clienteConectado.getIdPersonaje() == paqueteFinalizarBatalla.getIdEnemigo()) {

				try {
					clienteConectado.getSalida().writeObject(paqueteFinalizarBatalla.obtenerJson());
				} catch (IOException e) {
					Servidor.log.append("No se ha podido finalizar la batalla." + System.lineSeparator());
					e.printStackTrace();
				}
			}
		}

		synchronized (Servidor.atencionConexiones) {
			Servidor.atencionConexiones.notify();
		}

	}

}
