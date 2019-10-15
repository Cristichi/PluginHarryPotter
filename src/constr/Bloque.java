package constr;

import org.bukkit.Material;

public class Bloque {
	private int x, y, z;
	private Material tipo;

	public Bloque(int x, int y, int z, Material tipo) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.tipo = tipo;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public Material getTipo() {
		return tipo;
	}

	public void setTipo(Material tipo) {
		this.tipo = tipo;
	}
}