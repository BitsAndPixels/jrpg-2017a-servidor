package comando;

import mensajeria.PaqueteMovimiento;
import servidor.Servidor;

public class Movimiento extends ComandoServidor{
	
	private PaqueteMovimiento paqueteMovimiento;
	
	@Override
	public void ejecutarComando() {

		this.paqueteMovimiento = (PaqueteMovimiento) paquete;

		Servidor.getUbicacionPersonajes().get(paqueteMovimiento.getIdPersonaje())
				.setPosX(paqueteMovimiento.getPosX());
		Servidor.getUbicacionPersonajes().get(paqueteMovimiento.getIdPersonaje())
				.setPosY(paqueteMovimiento.getPosY());
		Servidor.getUbicacionPersonajes().get(paqueteMovimiento.getIdPersonaje())
				.setDireccion(paqueteMovimiento.getDireccion());
		Servidor.getUbicacionPersonajes().get(paqueteMovimiento.getIdPersonaje())
				.setFrame(paqueteMovimiento.getFrame());

		synchronized (Servidor.atencionMovimientos) {
			Servidor.atencionMovimientos.notify();
		}
	}

}
