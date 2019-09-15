package obj;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class Varita extends ItemStack {
	private static Plugin plugin;
	private static NamespacedKey keyNumeroMagico;

	private float numeroMagico;
	private Nucleo nucleo;
	private Madera madera;
	private Flexibilidad flexibilidad;

	public Varita(Plugin plugin, Nucleo nucleo, Madera madera, Flexibilidad flexibilidad) {
		super(Material.STICK);

		if (Varita.plugin == null) {
			Varita.plugin = plugin;
			keyNumeroMagico = new NamespacedKey(plugin, "varitaNumeroMagico");
		}

		this.nucleo = nucleo;
		this.madera = madera;
		this.flexibilidad = flexibilidad;
		numeroMagico = new Random().nextFloat();

		ItemMeta im = getItemMeta();
		im.setDisplayName(ChatColor.RESET + "Varita");
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add(ChatColor.GRAY + nucleo.toString());
		arrayList.add(ChatColor.GRAY + madera.toString());
		arrayList.add(ChatColor.GRAY + flexibilidad.toString());
		im.setLore(arrayList);
		im.getPersistentDataContainer().set(keyNumeroMagico, PersistentDataType.FLOAT, numeroMagico);
		setItemMeta(im);
	}

	@Override
	public void setType(Material type) {
		super.setType(Material.STICK);
	}

	@Override
	public Material getType() {
		return Material.STICK;
	}

	public float getNumeroMagico() {
		return numeroMagico;
	}

	public Madera getMadera() {
		return madera;
	}

	public void setMadera(Madera madera) {
		this.madera = madera;
	}

	public Flexibilidad getFlexibilidad() {
		return flexibilidad;
	}

	public void setFlexibilidad(Flexibilidad flexibilidad) {
		this.flexibilidad = flexibilidad;
	}

	public Nucleo getNucleo() {
		return nucleo;
	}

	public void setNucleo(Nucleo nucleo) {
		this.nucleo = nucleo;
	}

	public static enum Madera {
		ABEDUL;
		private String nombre;

		private Madera() {
			nombre = name().toLowerCase().replace("_", " ");
			char[] cs = nombre.toCharArray();
			boolean nextMayus = true;
			for (int i=0; i<cs.length; i++) {
				char c = cs[i];
				if (c==' ') {
					nextMayus = true;
				}else if (nextMayus) {
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

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		@Override
		public String toString() {
			return nombre;
		}
	}

	public static enum Flexibilidad {
		MUY_FLEXIBLE;
		private String nombre;

		private Flexibilidad() {
			nombre = name().toLowerCase().replace("_", " ");
			char[] cs = nombre.toCharArray();
			boolean nextMayus = true;
			for (int i=0; i<cs.length; i++) {
				char c = cs[i];
				if (c==' ') {
					nextMayus = true;
				}else if (nextMayus) {
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

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		@Override
		public String toString() {
			return nombre;
		}
	}

	public static enum Nucleo {
		PLUMA_DE_FENIX("Pluma De Fénix");
		private String nombre;

		private Nucleo() {
			nombre = name().toLowerCase().replace("_", " ");
			char[] cs = nombre.toCharArray();
			boolean nextMayus = true;
			for (int i=0; i<cs.length; i++) {
				char c = cs[i];
				if (c==' ') {
					nextMayus = true;
				}else if (nextMayus) {
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

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		@Override
		public String toString() {
			return nombre;
		}
	}

	/**
	 * 
	 * @param posibleVarita
	 * @return Varita si el ItemStack es una varita correcta, null en otro caso
	 */
	public static Varita convertir(ItemStack posibleVarita) {
		return null;
	}

	public static class VaritaListener implements Listener {
		@EventHandler
		private void onClickDerecho(PlayerInteractEvent e) {
			if (e.getItem() != null) {
				e.getPlayer().sendMessage("?Lo que tenías era Varita? " + (Varita.convertir(e.getItem()) != null));
				switch (e.getAction()) {
				case RIGHT_CLICK_AIR:
				case RIGHT_CLICK_BLOCK:

					break;

				default:
					break;
				}
			}
		}
	}
}
