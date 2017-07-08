package comando;

import servidor.Servidor;

public class Desconectar extends ComandoServidor{
	@Override
	public void ejecutarComando() {
		// Indico que se desconecto
		Servidor.log.append(paquete.getIp() + " se ha desconectado." + System.lineSeparator());
		listener.salir();
	}

}
