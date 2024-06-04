package es.cristichi.magiaborras.obj.flu;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bukkit.Location;

public class ChimeneaFlu implements Serializable {
	private static final long serialVersionUID = 1L;

	private Location loc;
	private String nombre, owner;

	public ChimeneaFlu(Location loc, String nombre, String owner) {
		this.loc = loc;
		this.nombre = nombre;
		this.owner = owner;
	}

	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	private void writeObject(ObjectOutputStream oos) throws Exception {
		oos.writeObject(loc);
		oos.writeObject(nombre);
	}

	private void readObject(ObjectInputStream ois) throws Exception {
		loc = (Location) ois.readObject();
		nombre = (String) ois.readObject();
	}
}
