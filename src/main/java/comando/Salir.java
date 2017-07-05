package comando;

public class Salir extends ComandoServidor {

	@Override
	public void ejecutarComando() {
		listener.salir();

	}
}
