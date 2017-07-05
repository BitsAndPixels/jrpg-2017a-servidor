package comando;



public class Desconectar extends ComandoServidor{
	@Override
	public void ejecutarComando() {
		
//		// Cierro todo
//		entrada.close();
//		salida.close();
//		socket.close();
//
//		// Lo elimino de los clientes conectados
//		Servidor.getClientesConectados().remove(this);
//
//		// Indico que se desconecto
//		Servidor.log.append(paquete.getIp() + " se ha desconectado." + System.lineSeparator());
//
//		return;
		listener.salir();
	}

}
