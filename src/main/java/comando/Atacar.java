package comando;

import java.io.IOException;

import mensajeria.PaqueteAtacar;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class Atacar extends ComandoServidor {
	private PaqueteAtacar paqueteAtacar;
	
	@Override
	public void ejecutarComando() {
		this.paqueteAtacar = (PaqueteAtacar) paquete;
		for (EscuchaCliente clienteConectado : Servidor.getClientesConectados()) {
			if (clienteConectado.getIdPersonaje() == paqueteAtacar.getIdEnemigo()) {
				try {
					clienteConectado.getSalida().writeObject(paqueteAtacar.obtenerJson());
				} catch (IOException e) {
					Servidor.log.append("Fallo al intentar atacar a " + paqueteAtacar.getIdEnemigo()  + System.lineSeparator());
					e.printStackTrace();
				}
			}
		}
	}

}
