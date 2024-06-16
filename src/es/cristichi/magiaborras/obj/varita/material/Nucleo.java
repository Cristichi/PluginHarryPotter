package es.cristichi.magiaborras.obj.varita.material;

public enum Nucleo {
		ASTAS_DE_LEBRILOPE("Astas De Lebrílope"), BIGOTES_DE_KNEAZLE, BIGOTES_DE_TROL, CORAL, CUERNO_DE_BASILISCO,
		CUERNO_DE_SERPIENTE_CORNUDA, ESPINA_DEL_MONSTRUO_DEL_RIO_BLANCO("Espina Del Monstruo Del Río Blanco"),
		FIBRA_DE_CORAZON_DE_DRAGON("Fibra De Corazón De Dragón"),
		FIBRA_DE_CORAZON_DE_SNALLYGASTER("Fibra De Corazón De Snallygaster"), PELO_DE_COLA_DE_THESTRAL,
		PELO_DE_GATO_WAMPUS, PELO_DE_KELPIE, PELO_DE_ROUGAROU, PELO_DE_COLA_DE_UNICORNIO, PELO_DE_VEELA,
		PLUMA_DE_COLA_DE_AVE_DEL_TRUENO, PLUMA_DE_FENIX("Pluma De Fénix");
		private String nombre;

		private Nucleo() {
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

		private Nucleo(String nombre) {
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