package es.cristichi.magiaborras.obj.pocion;

import java.util.HashMap;

import org.bukkit.Material;

public class RecetaPocion {
	private Pocion resultado;
	private HashMap<Material, Integer> materiales;

	public RecetaPocion(Pocion resultado, HashMap<Material, Integer> materiales) {
		this.resultado = resultado;
		this.materiales = materiales;
	}

	public Pocion getResultado() {
		return resultado;
	}

	public void setResultado(Pocion resultado) {
		this.resultado = resultado;
	}

	public HashMap<Material, Integer> getMateriales() {
		return materiales;
	}

	public void setMateriales(HashMap<Material, Integer> materiales) {
		this.materiales = materiales;
	}

	@Override
	public String toString() {
		return "RecetaPocion [resultado=" + resultado.getNombre() + ", materiales=" + materiales + "]";
	}

}
