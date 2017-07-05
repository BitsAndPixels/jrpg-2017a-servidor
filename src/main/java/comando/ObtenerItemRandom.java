package comando;

import java.io.IOException;

import mensajeria.PaqueteItem;
import servidor.Servidor;

public class ObtenerItemRandom extends ComandoServidor{
	private PaqueteItem paqueteItem;
	
	@Override
	public void ejecutarComando() {
//		paqueteItem = (PaqueteItem) gson.fromJson(cadenaLeida, PaqueteItem.class);
		this.paqueteItem = (PaqueteItem) paquete;
		Servidor.log.append("Se solicita el item random " + System.lineSeparator());
		PaqueteItem paqueteItemReturn = Servidor.getConector().getItemRandom();
		paqueteItemReturn.setComando(Comando.OBTENERITEMRANDOM);
//		salida.writeObject(gson.toJson(paqueteItemReturn));
		try {
			listener.getSalida().writeObject(paqueteItemReturn.getJson());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
