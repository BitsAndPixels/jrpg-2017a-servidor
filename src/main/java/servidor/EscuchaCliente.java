package servidor;

import java.io.*;
import java.net.Socket;

import com.google.gson.Gson;

import cliente.*;
import comando.Comando;
import comando.ComandoServidor;
import dominio.*;
import estados.Estado;
import mensajeria.Paquete;
import mensajeria.PaqueteAtacar;
import mensajeria.PaqueteBatalla;
import mensajeria.PaqueteChat;
import mensajeria.PaqueteDeMovimientos;
import mensajeria.PaqueteDePersonajes;
import mensajeria.PaqueteFinalizarBatalla;
import mensajeria.PaqueteItem;
import mensajeria.PaqueteMovimiento;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;

public class EscuchaCliente extends Thread {

	private final Socket socket;
	private final ObjectInputStream entrada;
	private final ObjectOutputStream salida;
	private int idPersonaje;
	private final Gson gson = new Gson();

	private PaquetePersonaje paquetePersonaje;
	private PaqueteDePersonajes paqueteDePersonajes;
	
	private boolean estaConectado = true;
	private PaqueteUsuario paqueteUsuario = new PaqueteUsuario();
	

	public EscuchaCliente(String ip, Socket socket, ObjectInputStream entrada, ObjectOutputStream salida) {
		this.socket = socket;
		this.entrada = entrada;
		this.salida = salida;
		paquetePersonaje = new PaquetePersonaje();
	}
	
	public void run() {
		Paquete paquete = new Paquete();
		String cadenaLeida;
		

		while (estaConectado){

			try {
				cadenaLeida = (String) entrada.readObject();
				paquete = Paquete.cargarJson(cadenaLeida);
				ComandoServidor comando = (ComandoServidor) paquete.obtenerInstanciaComando(ComandoServidor.COMANDO);
				comando.setListener(this);
				comando.ejecutarComando();

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}

		}
}



	public Socket getSocket() {
		return socket;
	}

	public ObjectInputStream getEntrada() {
		return entrada;
	}

	public ObjectOutputStream getSalida() {
		return salida;
	}

	public PaquetePersonaje getPaquetePersonaje() {
		return paquetePersonaje;
	}
	
	public void setPaquetePersonaje(PaquetePersonaje paquetePersonaje) {
		this.paquetePersonaje = paquetePersonaje;
		
	}

	public int getIdPersonaje() {
		return idPersonaje;
	}

	public void setPaqueteUsuario(PaqueteUsuario paqueteUsuario) {
		this.paqueteUsuario = paqueteUsuario;
	}

	public PaqueteUsuario getPaqueteUsuario() {
		return this.paqueteUsuario;
	}

	public void salir() {
		try {
			entrada.close();
			salida.close();
			socket.close();
		} catch (IOException e) {
			Servidor.log.append("Ocurrio un error al cerrar las conexiones." + System.lineSeparator());
			e.printStackTrace();
		}
		
		
		Servidor.getPersonajesConectados().remove(paquetePersonaje.getId());
		Servidor.getUbicacionPersonajes().remove(paquetePersonaje.getId());
		Servidor.getClientesConectados().remove(this);

		for (EscuchaCliente cliente : Servidor.getClientesConectados()) {
			paqueteDePersonajes = new PaqueteDePersonajes(Servidor.getPersonajesConectados());
			paqueteDePersonajes.setComando(Comando.CONEXION);
			try {
				cliente.salida.writeObject(paqueteDePersonajes.obtenerJson());
			} catch (IOException e) {
				Servidor.log.append("Ocurrio un error al desconectar." + cliente.getId() + System.lineSeparator());
				e.printStackTrace();
			}
		}

//		Servidor.log.append(paquete.getIp() + " se ha desconectado." + System.lineSeparator());
		estaConectado = false;
		
	}

	public void setIdPersonaje(int id) {
		this.idPersonaje = id;
		
	}

	
}
