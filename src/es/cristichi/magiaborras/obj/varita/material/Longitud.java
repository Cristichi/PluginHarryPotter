package es.cristichi.magiaborras.obj.varita.material;

public enum Longitud {
	MUY_CORTA, CORTA, MEDIANA, LARGA, MUY_LARGA;
	private String nombre;

	private Longitud() {
		nombre = name().toLowerCase().replace("_", " ");
		char[] cs = nombre.toCharArray();
		boolean nextMayus = true;
		for (int i = 0; i < cs.length; i++) {
			char c = cs[i];
			if (c == ' ') {
				nextMayus = true;
			} else if (nextMayus) {
				cs[i] = Character.toUpperCase(c);
				nextMayus = false;
			}
		}
		nombre = new String(cs);
	}

	public String getNombre() {
		return nombre;
	}

	@Override
	public String toString() {
		return nombre;
	}
}