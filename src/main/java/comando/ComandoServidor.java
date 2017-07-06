package comando;

import servidor.EscuchaCliente;
import comando.Comando;

public abstract class ComandoServidor extends Comando {
	
	public static final String COMANDO = "comando";
	
	protected EscuchaCliente listener;
	
	public void setListener(EscuchaCliente listener) {
		this.listener = listener;
	}

}
