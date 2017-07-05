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
//		paqueteFinalizarBatalla = (PaqueteFinalizarBatalla) gson.fromJson(cadenaLeida,
//				PaqueteFinalizarBatalla.class);
		
		this.paqueteFinalizarBatalla = (PaqueteFinalizarBatalla) paquete;
		Servidor.getPersonajesConectados().get(paqueteFinalizarBatalla.getId())
				.setEstado(Estado.estadoJuego);
		Servidor.getPersonajesConectados().get(paqueteFinalizarBatalla.getIdEnemigo())
				.setEstado(Estado.estadoJuego);
		for (EscuchaCliente clienteConectado : Servidor.getClientesConectados()) {
			if (clienteConectado.getIdPersonaje() == paqueteFinalizarBatalla.getIdEnemigo()) {
//				conectado.getSalida().writeObject(gson.toJson(paqueteFinalizarBatalla));
				try {
					clienteConectado.getSalida().writeObject(paqueteFinalizarBatalla.getJson());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		synchronized (Servidor.atencionConexiones) {
			Servidor.atencionConexiones.notify();
		}

	}

}
