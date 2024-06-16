package es.cristichi.magiaborras.obj.varita.material;
public enum Flexibilidad {
		MUY_RIGIDA("Muy Rígida"), RIGIDA("Rígida"), ALGO_RIGIDA("Algo Rígida"), FLEXIBILIDAD_MEDIA, MUY_FLEXIBLE,
		INCREIBLEMENTE_FLEXIBLE("Increíblemente Flexible"), EXTREMADAMENTE_FLEXIBLE;
		private String nombre;

		private Flexibilidad() {
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

		private Flexibilidad(String nombre) {
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