package es.cristichi.magiaborras.obj.varita.conjuro;

import java.util.ArrayList;

public class TiposLanzamiento extends ArrayList<TipoLanzamiento> {
	private static final long serialVersionUID = -4465391248913994109L;

	public TiposLanzamiento(TipoLanzamiento... tipos) {
		super(tipos.length);
		for (TipoLanzamiento tipo : tipos) {
			add(tipo);
		}
	}
}