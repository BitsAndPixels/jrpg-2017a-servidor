package comando;

import java.io.IOException;

import mensajeria.PaqueteAtacar;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class Atacar extends ComandoServidor {
	private PaqueteAtacar paqueteAtacar;
	
	@Override
	public void ejecutarComando() {
//		paqueteAtacar = (PaqueteAtacar) gson.fromJson(cadenaLeida, PaqueteAtacar.class);
		this.paqueteAtacar = (PaqueteAtacar) paquete;
		for (EscuchaCliente clienteConectado : Servidor.getClientesConectados()) {
			if (clienteConectado.getIdPersonaje() == paqueteAtacar.getIdEnemigo()) {
//				conectado.getSalida().writeObject(gson.toJson(paqueteAtacar));
				try {
					clienteConectado.getSalida().writeObject(paqueteAtacar.getJson());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
