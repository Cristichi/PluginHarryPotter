package obj;

import java.util.ArrayList;

import org.bukkit.Material;

public class RecetaPocion {
	private Pocion resultado;
	private ArrayList<Material> materiales;

	public RecetaPocion(Pocion resultado, ArrayList<Material> materiales) {
		this.resultado = resultado;
		this.materiales = materiales;
	}

	public Pocion getResultado() {
		return resultado;
	}

	public void setResultado(Pocion resultado) {
		this.resultado = resultado;
	}

	public ArrayList<Material> getMateriales() {
		return materiales;
	}

	public void setMateriales(ArrayList<Material> materiales) {
		this.materiales = materiales;
	}

	@Override
	public String toString() {
		return "RecetaPocion [resultado=" + resultado.getNombre() + ", materiales=" + materiales + "]";
	}

}
