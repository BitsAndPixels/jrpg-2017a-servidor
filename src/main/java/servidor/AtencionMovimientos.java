package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import cliente.Cliente;
import comando.Comando;
import estados.Estado;
import mensajeria.PaqueteDeMovimientos;

public class AtencionMovimientos extends Thread {
	
	private final Gson gson = new Gson();

	public AtencionMovimientos() {
		
	}

	public void run() {

		synchronized(this){
		
			try {
	
				while (true) {
			
					// Espero a que se conecte alguien
					wait();
					
					// Le reenvio la conexion a todos
					for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
					
						if(conectado.getPaquetePersonaje().getEstado() == Estado.estadoJuego){
						
							PaqueteDeMovimientos pdp = (PaqueteDeMovimientos) new PaqueteDeMovimientos(Servidor.getUbicacionPersonajes()).clone();
							pdp.setComando(Comando.MOVIMIENTO);
								
							conectado.getSalida().writeObject(pdp.obtenerJson());
						}
					}
					
				}
				
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
