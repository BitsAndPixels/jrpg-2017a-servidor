package comando;

import java.io.IOException;

import mensajeria.PaqueteChat;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class Chat extends ComandoServidor{
	
	private PaqueteChat paqueteChat;
	
	@Override
	public void ejecutarComando() {
		this.paqueteChat = (PaqueteChat) paquete;
		switch (paqueteChat.getTipoMensaje()) {
		case COMANDO:
			this.mensajeComando();
			break;
		case PRIVADO:
			this.mensajePrivado();
			break;
		case GLOBAL:
			this.mensajeGlobal();
			break;
		default:
			break;
		}
	}
	
	public void mensajeComando() {
		switch (paqueteChat.getMensajeChat()) {
		case "lista":
			paqueteChat.setMensajeChat(Servidor.getCadenaPersonajesConectados());
			break;
		default:
			paqueteChat.setMensajeChat("No se reconoce el comando");
			break;
		}
		paqueteChat.setNombreUsuarioPasivo(paqueteChat.getNombreUsuarioActivo());
		paqueteChat.setNombreUsuarioActivo("Servidor");
		try {
			listener.getSalida().writeObject(paqueteChat.obtenerJson());
			logueoChat();
		} catch (IOException e) {
			Servidor.log.append("Fallo al intentar enviar un mensaje" + System.lineSeparator());
			e.printStackTrace();
		}
	}

	private void mensajeGlobal() {
		// paqueteChat.setNombreUsuarioActivo(nombreUsuarioActivo);
		for (EscuchaCliente clienteConectado : Servidor.getClientesConectados()) {
			if (!(clienteConectado.getPaquetePersonaje().getNombre().compareTo(paqueteChat.getNombreUsuarioActivo()) == 0)) {
				try {
					clienteConectado.getSalida().writeObject(paqueteChat.obtenerJson());
					logueoChat();
				} catch (IOException e) {
					Servidor.log.append("Fallo al intentar enviar un mensaje" + System.lineSeparator());
					e.printStackTrace();
				}
			}
		}
	}

	private void logueoChat() {
		Servidor.log.append("Se envio el paquete de tipo " + paqueteChat.getTipoMensaje() + " con el mensaje "
				+ paqueteChat.getMensajeChat() + " del usuario " + paqueteChat.getNombreUsuarioActivo() + " al usuario "
				+ paqueteChat.getNombreUsuarioPasivo() + "." + System.lineSeparator());
	}

	private void mensajePrivado() {
		for (EscuchaCliente clienteConectado : Servidor.getClientesConectados()) {
			if (clienteConectado.getPaquetePersonaje().getNombre().compareTo(paqueteChat.getNombreUsuarioPasivo()) == 0) {
				try {
					clienteConectado.getSalida().writeObject(paqueteChat.obtenerJson());
					logueoChat();
				} catch (IOException e) {
					Servidor.log.append("Fallo al intentar enviar un mensaje" + System.lineSeparator());
					e.printStackTrace();
				}
				break;
			}
		}
	}

}
