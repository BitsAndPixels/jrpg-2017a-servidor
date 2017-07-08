package comando;

import java.io.IOException;

import mensajeria.PaqueteItem;
import servidor.Servidor;

public class ObtenerItemRandom extends ComandoServidor{
	private PaqueteItem paqueteItem;
	
	@Override
	public void ejecutarComando() {
		this.paqueteItem = (PaqueteItem) paquete;
		Servidor.log.append("Se solicita el item random " + System.lineSeparator());
		PaqueteItem paqueteItemReturn = Servidor.getConector().getItemRandom();
		paqueteItemReturn.setComando(Comando.OBTENERITEMRANDOM);
		try {
			listener.getSalida().writeObject(paqueteItemReturn.obtenerJson());
		} catch (IOException e) {
			Servidor.log.append("Fallo al intentar obtener item de la BBDD." + this.paqueteItem.getIdItem() + System.lineSeparator());
			e.printStackTrace();
		}
	}

}
