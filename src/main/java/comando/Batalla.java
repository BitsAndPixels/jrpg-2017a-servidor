package comando;

import java.io.IOException;

import estados.Estado;
import mensajeria.PaqueteBatalla;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class Batalla extends ComandoServidor{
	private PaqueteBatalla paqueteBatalla;
	
	@Override
	public void ejecutarComando() {
		// Le reenvio al id del personaje batallado que quieren
		// pelear
		this.paqueteBatalla = (PaqueteBatalla) paquete;
		Servidor.log.append(paqueteBatalla.getId() + " quiere batallar con " + paqueteBatalla.getIdEnemigo()
				+ System.lineSeparator());

		// seteo estado de batalla
		Servidor.getPersonajesConectados().get(paqueteBatalla.getId()).setEstado(Estado.estadoBatalla);
		Servidor.getPersonajesConectados().get(paqueteBatalla.getIdEnemigo())
				.setEstado(Estado.estadoBatalla);
		paqueteBatalla.setMiTurno(true);
		
		try {
			listener.getSalida().writeObject(paqueteBatalla.obtenerJson());
		} catch (IOException e) {
			Servidor.log.append("Fallo al intentar iniciar batalla." + System.lineSeparator());
			e.printStackTrace();
		}
		
		for (EscuchaCliente clienteConectado : Servidor.getClientesConectados()) {
			if (clienteConectado.getIdPersonaje() == paqueteBatalla.getIdEnemigo()) {
				int aux = paqueteBatalla.getId();
				paqueteBatalla.setId(paqueteBatalla.getIdEnemigo());
				paqueteBatalla.setIdEnemigo(aux);
				paqueteBatalla.setMiTurno(false);
				try {
					clienteConectado.getSalida().writeObject(paqueteBatalla.obtenerJson());
				} catch (IOException e) {
					Servidor.log.append("Fallo al intentar iniciar batalla." + System.lineSeparator());
					e.printStackTrace();
				}
				break;
			}
		}

		synchronized (Servidor.atencionConexiones) {
			Servidor.atencionConexiones.notify();
		}
	}

}
