package es.cristichi.magiaborras.obj.varita.prop;

public enum Madera {
	ABEDUL, ABETO, ACACIA, ACEBO, ALAMO("Álamo"), ALAMO_TEMBLON("Álamo Temblón"), ALERCE, ALISO, ARCE, ARCE_AZUCARADO,
	AVELLANO, CANA("Caña"), CAOBA, CARPE, CASTANO("Castaño"), CEDRO, CEREZO, CIPRES("Ciprés"), CORNEJO, EBANO("Ábano"),
	ENDRINO, ESPINO, ESPINO_DE_MAYO, FRESNO, FRESNO_ESPINOSO, HAYA, HIEDRA, LAUREL, MADERA_DE_SERPIENTE, MANZANO, NOGAL,
	NOGAL_NEGRO, OLIVO, OLMO, PALISANDRO, PERAL, PICEA("Pícea"), PINO, ROBLE_INGLES("Roble Inglés"), ROBLE_ROJO, SAUCE,
	SAUCO("Saúco"), SECOYA, SERBAL, SICOMORO("Sicómoro"), TAMARACK, TEJO, TILO_PLATEADO, VID;
	private String nombre;

	private Madera() {
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

	private Madera(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	@Override
	public String toString() {
		return nombre;
	}
}