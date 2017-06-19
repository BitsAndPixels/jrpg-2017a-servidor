package servidor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import dominio.MyRandom;
import inventario.Inventario;
import inventario.Item;
import inventario.Mochila;
import mensajeria.PaqueteItem;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;

public class Conector {

	private String url = "primeraBase.bd";
	Connection connect;

	public void connect() {
		try {
			Servidor.log.append("Estableciendo conexi�n con la base de datos..." + System.lineSeparator());
			connect = DriverManager.getConnection("jdbc:sqlite:" + url);
			Servidor.log.append("Conexi�n con la base de datos establecida con �xito." + System.lineSeparator());
		} catch (SQLException ex) {
			Servidor.log.append("Fallo al intentar establecer la conexi�n con la base de datos. " + ex.getMessage()
					+ System.lineSeparator());
		}
	}

	public void close() {
		try {
			connect.close();
		} catch (SQLException ex) {
			Servidor.log.append("Error al intentar cerrar la conexi�n con la base de datos." + System.lineSeparator());
			Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public boolean registrarUsuario(PaqueteUsuario user) {
		ResultSet result = null;
		try {
			PreparedStatement st1 = connect.prepareStatement("SELECT * FROM registro WHERE usuario= ? ");
			st1.setString(1, user.getUsername());
			result = st1.executeQuery();

			if (!result.next()) {

				PreparedStatement st = connect.prepareStatement("INSERT INTO registro (usuario, password, idPersonaje) VALUES (?,?,?)");
				st.setString(1, user.getUsername());
				st.setString(2, user.getPassword());
				st.setInt(3, user.getIdPj());
				st.execute();
				Servidor.log.append("El usuario " + user.getUsername() + " se ha registrado." + System.lineSeparator());
				return true;
			} else {
				Servidor.log.append("El usuario " + user.getUsername() + " ya se encuentra en uso." + System.lineSeparator());
				return false;
			}
		} catch (SQLException ex) {
			Servidor.log.append("Eror al intentar registrar el usuario " + user.getUsername() + System.lineSeparator());
			System.err.println(ex.getMessage());
			return false;
		}

	}

	public boolean registrarPersonaje(PaquetePersonaje paquetePersonaje, PaqueteUsuario paqueteUsuario) {

		try {

			// Registro al personaje en la base de datos
			PreparedStatement stRegistrarPersonaje = connect.prepareStatement(
					"INSERT INTO personaje (idInventario, idMochila,casta,raza,fuerza,destreza,inteligencia,saludTope,energiaTope,nombre,experiencia,nivel,idAlianza) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
					PreparedStatement.RETURN_GENERATED_KEYS);
			stRegistrarPersonaje.setInt(1, -1);
			stRegistrarPersonaje.setInt(2, -1);
			stRegistrarPersonaje.setString(3, paquetePersonaje.getCasta());
			stRegistrarPersonaje.setString(4, paquetePersonaje.getRaza());
			stRegistrarPersonaje.setInt(5, paquetePersonaje.getFuerza());
			stRegistrarPersonaje.setInt(6, paquetePersonaje.getDestreza());
			stRegistrarPersonaje.setInt(7, paquetePersonaje.getInteligencia());
			stRegistrarPersonaje.setInt(8, paquetePersonaje.getSaludTope());
			stRegistrarPersonaje.setInt(9, paquetePersonaje.getEnergiaTope());
			stRegistrarPersonaje.setString(10, paquetePersonaje.getNombre());
			stRegistrarPersonaje.setInt(11, 0);
			stRegistrarPersonaje.setInt(12, 1);
			stRegistrarPersonaje.setInt(13, -1);
			stRegistrarPersonaje.execute();

			// Recupero la �ltima key generada
			ResultSet rs = stRegistrarPersonaje.getGeneratedKeys();
			if (rs != null && rs.next()) {

				// Obtengo el id
				int idPersonaje = rs.getInt(1);

				// Le asigno el id al paquete personaje que voy a devolver
				paquetePersonaje.setId(idPersonaje);

				// Le asigno el personaje al usuario
				PreparedStatement stAsignarPersonaje = connect.prepareStatement("UPDATE registro SET idPersonaje=? WHERE usuario=? AND password=?");
				stAsignarPersonaje.setInt(1, idPersonaje);
				stAsignarPersonaje.setString(2, paqueteUsuario.getUsername());
				stAsignarPersonaje.setString(3, paqueteUsuario.getPassword());
				stAsignarPersonaje.execute();

				// Por ultimo registro el inventario y la mochila
				if (this.registrarInventarioMochila(idPersonaje)) {
					Servidor.log.append("El usuario " + paqueteUsuario.getUsername() + " ha creado el personaje "
							+ paquetePersonaje.getId() + System.lineSeparator());
					return true;
				} else {
					Servidor.log.append("Error al registrar la mochila y el inventario del usuario " + paqueteUsuario.getUsername() + " con el personaje" + paquetePersonaje.getId() + System.lineSeparator());
					return false;
				}
			}
			return false;

		} catch (SQLException e) {
			Servidor.log.append(
					"Error al intentar crear el personaje " + paquetePersonaje.getNombre() + System.lineSeparator());
			e.printStackTrace();
			return false;
		}

	}

	public boolean registrarInventarioMochila(int idInventarioMochila) {
		try {
			// Preparo la consulta para el registro el inventario en la base de
			// datos
			PreparedStatement stRegistrarInventario = connect.prepareStatement("INSERT INTO inventario(idInventario,manos1,manos2,pie,cabeza,pecho,accesorio) VALUES (?,-1,-1,-1,-1,-1,-1)");
			stRegistrarInventario.setInt(1, idInventarioMochila);

			// Preparo la consulta para el registro la mochila en la base de
			// datos
			PreparedStatement stRegistrarMochila = connect.prepareStatement("INSERT INTO mochila(idMochila,item1,item2,item3,item4,item5,item6,item7,item8,item9,item10,item11,item12,item13,item14,item15,item16,item17,item18,item19,item20) VALUES(?,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1)");
			stRegistrarMochila.setInt(1, idInventarioMochila);

			// Registro inventario y mochila
			stRegistrarInventario.execute();
			stRegistrarMochila.execute();

			// Le asigno el inventario y la mochila al personaje
			PreparedStatement stAsignarPersonaje = connect
					.prepareStatement("UPDATE personaje SET idInventario=?, idMochila=? WHERE idPersonaje=?");
			stAsignarPersonaje.setInt(1, idInventarioMochila);
			stAsignarPersonaje.setInt(2, idInventarioMochila);
			stAsignarPersonaje.setInt(3, idInventarioMochila);
			stAsignarPersonaje.execute();

			Servidor.log.append("Se ha registrado el inventario de " + idInventarioMochila + System.lineSeparator());
			return true;

		} catch (SQLException e) {
			Servidor.log.append("Error al registrar el inventario de " + idInventarioMochila + System.lineSeparator());
			e.printStackTrace();
			return false;
		}
	}

	public boolean loguearUsuario(PaqueteUsuario user) {
		ResultSet result = null;
		try {
			// Busco usuario y contrase�a
			PreparedStatement st = connect
					.prepareStatement("SELECT * FROM registro WHERE usuario = ? AND password = ? ");
			st.setString(1, user.getUsername());
			st.setString(2, user.getPassword());
			result = st.executeQuery();

			// Si existe inicio sesion
			if (result.next()) {
				Servidor.log.append("El usuario " + user.getUsername() + " ha iniciado sesi�n." + System.lineSeparator());
				return true;
			}

			// Si no existe informo y devuelvo false
			Servidor.log.append("El usuario " + user.getUsername() + " ha realizado un intento fallido de inicio de sesi�n." + System.lineSeparator());
			return false;

		} catch (SQLException e) {
			Servidor.log.append("El usuario " + user.getUsername() + " fallo al iniciar sesi�n." + System.lineSeparator());
			e.printStackTrace();
			return false;
		}

	}

	public void actualizarPersonaje(PaquetePersonaje paquetePersonaje) {
		try {
			PreparedStatement stActualizarPersonaje = connect
					.prepareStatement("UPDATE personaje SET fuerza=?, destreza=?, inteligencia=?, saludTope=?, energiaTope=?, experiencia=?, nivel=? "
							+ "  WHERE idPersonaje=?");
			
			stActualizarPersonaje.setInt(1, paquetePersonaje.getFuerza());
			stActualizarPersonaje.setInt(2, paquetePersonaje.getDestreza());
			stActualizarPersonaje.setInt(3, paquetePersonaje.getInteligencia());
			stActualizarPersonaje.setInt(4, paquetePersonaje.getSaludTope());
			stActualizarPersonaje.setInt(5, paquetePersonaje.getEnergiaTope());
			stActualizarPersonaje.setInt(6, paquetePersonaje.getExperiencia());
			stActualizarPersonaje.setInt(7, paquetePersonaje.getNivel());
			stActualizarPersonaje.setInt(8, paquetePersonaje.getId());
			
			this.actualizarMochilaPersonaje(paquetePersonaje);
			this.actualizarInventarioPersonaje(paquetePersonaje);
			
			stActualizarPersonaje.executeUpdate();
						
			Servidor.log.append("El personaje " + paquetePersonaje.getNombre() + " se ha actualizado con �xito."  + System.lineSeparator());
		} catch (SQLException e) {
			Servidor.log.append("Fallo al intentar actualizar el personaje " + paquetePersonaje.getNombre()  + System.lineSeparator());
			e.printStackTrace();
		}
		
		
	}
	
	public void actualizarMochilaPersonaje(PaquetePersonaje paquetePersonaje) {
		try {
			PreparedStatement stActualizarMochila = connect
					.prepareStatement("UPDATE mochila set item1=?, item2=?, item3=?, item4=?, item5=?, item6=?, item7=?, item8=?, item9=?, item10=?, item11=?, item12=?, item13=?, item14=?, item15=?, item16=?, item17=?, item18=?, item19=?, item20=? "
							+ "where idMochila=?");
			
			
			/*
			 Map<String, String> map = ...
			for (Map.Entry<String, String> entry : map.entrySet())
			{
			    System.out.println(entry.getKey() + "/" + entry.getValue());
			}
			 */
			
//			ArrayList<Item> items = (ArrayList<Item>) paquetePersonaje.getMochila().getItems().values();
			
//			for (int i=0; i<20; i++) {
//				if (i < items.size())
//					stActualizarMochila.setInt(i, items.get(i).getIdItem());
//				else
//					stActualizarMochila.setInt(i+1, -1);
//			}
			int i = 0;
			int index = 0;
			for (Map.Entry<Integer, Item> item : paquetePersonaje.getMochila().getItems().entrySet()) {
				index = i+1;
				stActualizarMochila.setInt(index, item.getKey());
				System.out.println(index + "agrego a la mochila " +paquetePersonaje.getMochila().getIdMochila()+ " de " + paquetePersonaje.getId() + " el item:" + item.getKey());
				i++;
			}
			
			for (int j=i;j<20;j++) {
				index = j+1;
				stActualizarMochila.setInt(index, -1);
				System.out.println(index + "agrego a la mochila " +paquetePersonaje.getMochila().getIdMochila()+ " de " + paquetePersonaje.getId() + " un item vacio");
			}
			
			stActualizarMochila.setInt(21, paquetePersonaje.getMochila().getIdMochila());
			
			stActualizarMochila.executeUpdate();

			Servidor.log.append("La mochila" + paquetePersonaje.getMochila().getIdMochila() + " del personaje " + paquetePersonaje.getNombre() + " se ha actualizado con �xito."  + System.lineSeparator());
		} catch (SQLException e) {
			Servidor.log.append("Fallo al intentar actualizar la mochila" + paquetePersonaje.getMochila().getIdMochila() + " del personaje " + paquetePersonaje.getNombre()  + System.lineSeparator());
			e.printStackTrace();
		}
		
		
	}
	
	public void actualizarInventarioPersonaje(PaquetePersonaje paquetePersonaje) {
		try {
			PreparedStatement stActualizarInventario = connect
					.prepareStatement("UPDATE inventario set manos1=?, manos2=?, pie=?, cabeza=?, pecho=?, accesorio=? "
							+ "where idInventario=?");
			

			if (paquetePersonaje.getInventario().getManoDer().getEstado().equals("vacio"))
				stActualizarInventario.setInt(1, -1);
			else
				stActualizarInventario.setInt(1, paquetePersonaje.getInventario().getManoDer().getIdItem());
			
			if (paquetePersonaje.getInventario().getManoIzq().getEstado().equals("vacio"))
				stActualizarInventario.setInt(2, -1);
			else
				stActualizarInventario.setInt(2, paquetePersonaje.getInventario().getManoIzq().getIdItem());
				
			if (paquetePersonaje.getInventario().getPie().getEstado().equals("vacio"))
				stActualizarInventario.setInt(3, -1);
			else
				stActualizarInventario.setInt(3, paquetePersonaje.getInventario().getPie().getIdItem());
							
			if (paquetePersonaje.getInventario().getCabeza().getEstado().equals("vacio"))
				stActualizarInventario.setInt(4, -1);
			else
				stActualizarInventario.setInt(4, paquetePersonaje.getInventario().getCabeza().getIdItem());
							
			if (paquetePersonaje.getInventario().getPecho().getEstado().equals("vacio"))
				stActualizarInventario.setInt(5, -1);
			else
				stActualizarInventario.setInt(5, paquetePersonaje.getInventario().getPecho().getIdItem());
							
			if (paquetePersonaje.getInventario().getAccesorio().getEstado().equals("vacio"))
				stActualizarInventario.setInt(6, -1);
			else
				stActualizarInventario.setInt(6, paquetePersonaje.getInventario().getAccesorio().getIdItem());
			
			stActualizarInventario.setInt(7, paquetePersonaje.getInventario().getIdInventario());
			
			stActualizarInventario.executeUpdate();

			Servidor.log.append("El inventario" + paquetePersonaje.getInventario().getIdInventario() + " del personaje " + paquetePersonaje.getNombre() + " se ha actualizado con �xito."  + System.lineSeparator());
		} catch (SQLException e) {
			Servidor.log.append("Fallo al intentar actualizar la mochila" + paquetePersonaje.getInventario().getIdInventario() + " del personaje " + paquetePersonaje.getNombre()  + System.lineSeparator());
			e.printStackTrace();
		}
		
		
	}
	
	
//	public PaqueteItem getItem(int idItem) {
//		ResultSet result = null;
//		try {
//			// Selecciono el item
//			PreparedStatement st = connect.prepareStatement("SELECT * FROM item WHERE idItem = ?");
//			st.setInt(1, idItem);
//			result = st.executeQuery();
//
//			// Obtengo los atributos del item
//			PaqueteItem item = new PaqueteItem();
//			item.setIdItem(idItem);
//			item.setBonoAtaque(result.getInt("bonoAtaque"));
//			item.setBonoDefensa(result.getInt("bonoDefensa"));
//			item.setBonoMagia(result.getInt("bonoMagia"));
//			item.setBonoSalud(result.getInt("bonoSalud"));
//			item.setBonoEnergia(result.getInt("bonoEnergia"));
//			item.setTipo(result.getInt("tipo"));
//			item.setNombre(result.getString("nombre"));
//
//			// Devuelvo el paquete item con sus datos
//			return item;
//
//		} catch (SQLException ex) {
//			Servidor.log.append("Fallo al intentar recuperar el item " + idItem + System.lineSeparator());
//			Servidor.log.append(ex.getMessage() + System.lineSeparator());
//			ex.printStackTrace();
//		}
//
//		return new PaqueteItem();
//		
//	}
	
	public PaqueteItem getItemRandom() {
		ResultSet result = null;
		try {
			MyRandom randomizer = new MyRandom();
			int idItemRandom = randomizer.nextInt(this.getCantItem())+1;
			// Selecciono el item
			PreparedStatement st = connect.prepareStatement("SELECT * FROM item WHERE idItem = ?");
			st.setInt(1, idItemRandom);
			result = st.executeQuery();

			// Obtengo los atributos del item
			PaqueteItem item = new PaqueteItem();
			item.setIdItem(idItemRandom);
			item.setBonoAtaque(result.getInt("bonoAtaque"));
			item.setBonoDefensa(result.getInt("bonoDefensa"));
			item.setBonoMagia(result.getInt("bonoMagia"));
			item.setBonoSalud(result.getInt("bonoSalud"));
			item.setBonoEnergia(result.getInt("bonoEnergia"));
			item.setTipo(result.getInt("tipo"));
			item.setNombre(result.getString("nombre"));

			// Devuelvo el paquete item con sus datos
			return item;

		} catch (SQLException ex) {
			Servidor.log.append("Fallo al intentar recuperar el item random" + System.lineSeparator());
			Servidor.log.append(ex.getMessage() + System.lineSeparator());
			ex.printStackTrace();
		}

		return new PaqueteItem();
		
	}
	
	public int getCantItem() {
		ResultSet result = null;
		try {
			// Selecciono el item
			PreparedStatement st = connect.prepareStatement("SELECT count(1) as cantidad FROM item");
			result = st.executeQuery();

			// Obtengo la cantidad de items
			int cantItem = result.getInt("cantidad");

			// Devuelvo el paquete item con sus datos
			return cantItem;

		} catch (SQLException ex) {
			Servidor.log.append("Fallo al intentar recuperar la cantidad de items." + System.lineSeparator());
			Servidor.log.append(ex.getMessage() + System.lineSeparator());
			ex.printStackTrace();
		}

		return 0;
		
	}
	
//	public Item generarItem(int idItem) {
//		ResultSet result = null;
//		try {
//			// Selecciono el item
//			PreparedStatement st = connect.prepareStatement("SELECT * FROM item WHERE idItem = ?");
//			st.setInt(1, idItem);
//			result = st.executeQuery();
//
//			// Obtengo los atributos del item
//			Item item = new Item(idItem,result.getInt("bonoAtaque"),result.getInt("bonoDefensa"),result.getInt("bonoMagia"),
//								result.getInt("bonoSalud"),result.getInt("bonoEnergia"),result.getInt("tipo"),result.getString("nombre"), 
//								"desequipado");
//			// Devuelvo el paquete item con sus datos
//			return item;
//
//		} catch (SQLException ex) {
//			Servidor.log.append("Fallo al intentar recuperar el item " + idItem + System.lineSeparator());
//			Servidor.log.append(ex.getMessage() + System.lineSeparator());
//			ex.printStackTrace();
//		}
//		//Retorno item vacio
//		return new Item();
//		
//	}
	
	

	public PaquetePersonaje getPersonaje(PaqueteUsuario user) {
		ResultSet result = null;
		try {
			// Selecciono el personaje de ese usuario
			PreparedStatement st = connect.prepareStatement("SELECT * FROM registro WHERE usuario = ?");
			st.setString(1, user.getUsername());
			result = st.executeQuery();

			// Obtengo el id
			int idPersonaje = result.getInt("idPersonaje");

			// Selecciono los datos del personaje
			PreparedStatement stSeleccionarPersonaje = connect
					.prepareStatement("SELECT * FROM personaje WHERE idPersonaje = ?");
			stSeleccionarPersonaje.setInt(1, idPersonaje);
			result = stSeleccionarPersonaje.executeQuery();

			// Obtengo los atributos del personaje
			PaquetePersonaje personaje = new PaquetePersonaje();
			personaje.setId(idPersonaje);
			personaje.setRaza(result.getString("raza"));
			personaje.setCasta(result.getString("casta"));
			personaje.setFuerza(result.getInt("fuerza"));
			personaje.setInteligencia(result.getInt("inteligencia"));
			personaje.setDestreza(result.getInt("destreza"));
			personaje.setEnergiaTope(result.getInt("energiaTope"));
			personaje.setSaludTope(result.getInt("saludTope"));
			personaje.setNombre(result.getString("nombre"));
			personaje.setExperiencia(result.getInt("experiencia"));
			personaje.setNivel(result.getInt("nivel"));
			int idMochila = result.getInt("idMochila");
			int idInventario = result.getInt("idInventario");
			
			// Selecciono los datos de la mochila
			PreparedStatement stSeleccionarMochila = connect
					.prepareStatement("SELECT * FROM mochila WHERE idMochila = ?");
			stSeleccionarMochila.setInt(1, idMochila);
			result = stSeleccionarMochila.executeQuery();
			
			Mochila mochilaPersonaje = new Mochila();
			
			mochilaPersonaje.setIdMochila(idMochila);
			
			for (int i=0;i<20;i++) {
				String nombreColumna = ("item"+(i+1));
				//System.out.println(nombreColumna);
				if (result.getInt(nombreColumna) > 0) {
					mochilaPersonaje.agregaItem(this.generarItem(result.getInt(nombreColumna)));
				}

			}
			
			personaje.setMochila(mochilaPersonaje);
			
			
			// Selecciono los datos del inventario
			PreparedStatement stSeleccionarInventario = connect
					.prepareStatement("SELECT * FROM inventario WHERE idInventario = ?");
			stSeleccionarInventario.setInt(1, idInventario);
			result = stSeleccionarInventario.executeQuery();
			
			Inventario inventarioPersonaje = new Inventario();
			
			inventarioPersonaje.setIdInventario(result.getInt("idInventario"));
			
			if (result.getInt("manos1") > 0 ) {
				inventarioPersonaje.setManoDer(personaje.getMochila().obtenerItem(result.getInt("manos1")));
				inventarioPersonaje.getManoDer().serEquipado();
			}
			
			if (result.getInt("manos2") > 0 ) {
				inventarioPersonaje.setManoIzq(personaje.getMochila().obtenerItem(result.getInt("manos2")));
				inventarioPersonaje.getManoIzq().serEquipado();
			}
			
			if (result.getInt("pie") > 0 ) {
				inventarioPersonaje.setPie(personaje.getMochila().obtenerItem(result.getInt("pie")));
				inventarioPersonaje.getPie().serEquipado();
			}
			
			if (result.getInt("cabeza") > 0 ) {
				inventarioPersonaje.setCabeza(personaje.getMochila().obtenerItem(result.getInt("cabeza")));
				inventarioPersonaje.getCabeza().serEquipado();
			}
			
			if (result.getInt("pecho") > 0 ) {
				inventarioPersonaje.setPecho(personaje.getMochila().obtenerItem(result.getInt("pecho")));
				inventarioPersonaje.getPecho().serEquipado();
			}
			
			if (result.getInt("accesorio") > 0 ) {
				inventarioPersonaje.setAccesorio(personaje.getMochila().obtenerItem(result.getInt("accesorio")));
				inventarioPersonaje.getAccesorio().serEquipado();
			}
			
			personaje.setInventario(inventarioPersonaje);

			// Devuelvo el paquete personaje con sus datos
			return personaje;

		} catch (SQLException ex) {
			Servidor.log.append("Fallo al intentar recuperar el personaje " + user.getUsername() + System.lineSeparator());
			Servidor.log.append(ex.getMessage() + System.lineSeparator());
			ex.printStackTrace();
		}

		return new PaquetePersonaje();
	}
	
	public PaqueteUsuario getUsuario(String usuario) {
		ResultSet result = null;
		PreparedStatement st;
		
		try {
			st = connect.prepareStatement("SELECT * FROM registro WHERE usuario = ?");
			st.setString(1, usuario);
			result = st.executeQuery();

			String password = result.getString("password");
			int idPersonaje = result.getInt("idPersonaje");
			
			PaqueteUsuario paqueteUsuario = new PaqueteUsuario();
			paqueteUsuario.setUsername(usuario);
			paqueteUsuario.setPassword(password);
			paqueteUsuario.setIdPj(idPersonaje);
			
			return paqueteUsuario;
		} catch (SQLException e) {
			Servidor.log.append("Fallo al intentar recuperar el usuario " + usuario + System.lineSeparator());
			Servidor.log.append(e.getMessage() + System.lineSeparator());
			e.printStackTrace();
		}
		
		return new PaqueteUsuario();
	}
}
