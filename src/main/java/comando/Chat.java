package comando;

import java.io.IOException;

import mensajeria.PaqueteChat;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class Chat extends ComandoServidor{
	
	private PaqueteChat paqueteChat;
	
	@Override
	public void ejecutarComando() {
//		paqueteChat = (PaqueteChat) gson.fromJson(cadenaLeida, PaqueteChat.class);
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
//			getSalida().writeObject(gson.toJson(paqueteChat));
			listener.getSalida().writeObject(paqueteChat.getJson());
			logueoChat();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void mensajeGlobal() {
		// paqueteChat.setNombreUsuarioActivo(nombreUsuarioActivo);
		for (EscuchaCliente clienteConectado : Servidor.getClientesConectados()) {
			if (!(clienteConectado.getPaquetePersonaje().getNombre().compareTo(paqueteChat.getNombreUsuarioActivo()) == 0)) {
				try {
//					conectado.getSalida().writeObject(gson.toJson(paqueteChat));
					clienteConectado.getSalida().writeObject(paqueteChat.getJson());
					logueoChat();
				} catch (IOException e) {
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
//					conectado.getSalida().writeObject(gson.toJson(paqueteChat));
					clienteConectado.getSalida().writeObject(paqueteChat.getJson());
					logueoChat();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

}
