package comando;

import java.io.IOException;

import mensajeria.PaqueteComercio;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class IniciarComercio extends ComandoServidor{
	private PaqueteComercio paqueteComercio;
	@Override
	public void ejecutarComando() {
		this.paqueteComercio = (PaqueteComercio) paquete;

//		try {
//			this.listener.getSalida().writeObject(this.paqueteComercio.obtenerJson());
//		} catch (IOException e) {
//			Servidor.log.append("Fallo al intentar enviar el paquete comercio a" + paqueteComercio.getNombreUsuarioPasivo()  + System.lineSeparator());
//			e.printStackTrace();
//		}

		for (EscuchaCliente clienteConectado : Servidor.getClientesConectados()) {
			if (clienteConectado.getIdPersonaje() == paqueteComercio.getIdReceptor()) {
				try {
					clienteConectado.getSalida().writeObject(paqueteComercio.obtenerJson());
				} catch (IOException e) {
					Servidor.log.append("Fallo al intentar enviar el paquete comercio a" + paqueteComercio.getNombreUsuarioPasivo()  + System.lineSeparator());
					e.printStackTrace();
				}
			}
		}
	}
}
