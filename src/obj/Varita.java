package obj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.sun.istack.internal.Nullable;

public class Varita extends ItemStack {
	private static HashMap<UUID, Float> numerosMagicos;

	private static Plugin plugin;
	private static NamespacedKey keyNumeroMagico;
	private static NamespacedKey keyNucleo;
	private static NamespacedKey keyMadera;
	private static NamespacedKey keyFlexibilidad;
	private static NamespacedKey keyLongitud;
	private static NamespacedKey keyConjuro;

	public static void Init(Plugin plugin) {
		Varita.plugin = plugin;
		keyNumeroMagico = new NamespacedKey(plugin, "varitaNumeroMagico");
		keyNucleo = new NamespacedKey(plugin, "varitaNucleo");
		keyMadera = new NamespacedKey(plugin, "varitaMadera");
		keyFlexibilidad = new NamespacedKey(plugin, "varitaFlexibilidad");
		keyLongitud = new NamespacedKey(plugin, "varitaLongitud");
		keyConjuro = new NamespacedKey(plugin, "varitaHechizo");

		Varita.numerosMagicos = new HashMap<>();
	}

	public static void guardarNumeros(File archivo) {
		try {
			archivo.getParentFile().mkdirs();
			if (!archivo.exists()) {
				archivo.createNewFile();
			} else {
				archivo.delete();
				archivo.createNewFile();
			}
			FileWriter fw = new FileWriter(archivo);
			BufferedWriter bw = new BufferedWriter(fw);

			String configTxt = "";
			Set<UUID> keys = numerosMagicos.keySet();
			for (UUID key : keys) {
				Float value = numerosMagicos.get(key);
				configTxt += key.toString() + " (" + Bukkit.getOfflinePlayer(key).getName() + "): " + value + "\n";
			}

			bw.write(configTxt);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void cargarNumeros(File archivo) throws FileSystemException {
		String linea = null;
		int iLinea = 0;
		try {
			archivo.getParentFile().mkdirs();
			if (!archivo.exists()) {
				return;
			}
			FileReader fr = new FileReader(archivo);
			BufferedReader br = new BufferedReader(fr);

			while ((linea = br.readLine()) != null) {
				iLinea++;
				if (!linea.startsWith("#") && !linea.isEmpty()) {
					StringTokenizer st = new StringTokenizer(linea, "(");
					UUID uuid = UUID.fromString(st.nextToken().trim());
					st = new StringTokenizer(st.nextToken(), ":");
					st.nextToken();

					Float numeroMagico = Float.parseFloat(st.nextToken().trim());
					if (numeroMagico != null)
						numerosMagicos.put(uuid, numeroMagico);
				}
			}
			br.close();
		} catch (Exception e) {
			throw new FileSystemException(archivo.getName() + " could not be parsed"
					+ (iLinea > 0 ? " (line " + iLinea + ": " + linea + ")" : ""));
		}
	}

	private float numeroMagico;
	private Nucleo nucleo;
	private Madera madera;
	private Flexibilidad flexibilidad;
	private Longitud longitud;
	private Conjuro conjuro;

	public Varita() {
		this(new Random());
	}

	public Varita(Varita otra) {
		this(otra.numeroMagico, otra.nucleo, otra.madera, otra.flexibilidad, otra.longitud, otra.conjuro);
	}

	public Varita(Varita otra, Conjuro conjuro) {
		this(otra.numeroMagico, otra.nucleo, otra.madera, otra.flexibilidad, otra.longitud, conjuro);
	}

	public Varita(Random rng) {
		this(Nucleo.values()[rng.nextInt(Nucleo.values().length)], Madera.values()[rng.nextInt(Madera.values().length)],
				Flexibilidad.values()[rng.nextInt(Flexibilidad.values().length)],
				Longitud.values()[rng.nextInt(Longitud.values().length)], null);
	}

	public Varita(Nucleo nucleo, Madera madera, Flexibilidad flexibilidad, Longitud longitud,
			@Nullable Conjuro conjuro) {
		this(new Random().nextFloat(), nucleo, madera, flexibilidad, longitud, conjuro);
	}

	public Varita(float numeroMagico, Nucleo nucleo, Madera madera, Flexibilidad flexibilidad, Longitud longitud,
			@Nullable Conjuro conjuro) {
		super(Material.STICK);

		this.numeroMagico = numeroMagico;
		this.nucleo = nucleo;
		this.madera = madera;
		this.flexibilidad = flexibilidad;
		this.longitud = longitud;
		this.conjuro = conjuro;

		recagarDatos();
	}

	/**
	 * 
	 * @param posibleVarita
	 * @return Varita si el ItemStack es una varita correcta, null en otro caso
	 */
	public static Varita convertir(Plugin plugin, ItemStack posibleVarita) {
		String nucleo = null, madera, flexibilidad, longitud, conjuro;
		Float numeroMagico;
		try {
			if (posibleVarita.hasItemMeta() && posibleVarita.getItemMeta().getPersistentDataContainer()
					.has(keyNumeroMagico, PersistentDataType.FLOAT)) {
				PersistentDataContainer data = posibleVarita.getItemMeta().getPersistentDataContainer();
				numeroMagico = data.get(keyNumeroMagico, PersistentDataType.FLOAT);
				nucleo = data.get(keyNucleo, PersistentDataType.STRING);
				madera = data.get(keyMadera, PersistentDataType.STRING);
				flexibilidad = data.get(keyFlexibilidad, PersistentDataType.STRING);
				longitud = data.get(keyLongitud, PersistentDataType.STRING);
				conjuro = data.get(keyConjuro, PersistentDataType.STRING);
				return new Varita(numeroMagico, Nucleo.valueOf(nucleo), Madera.valueOf(madera),
						Flexibilidad.valueOf(flexibilidad), Longitud.valueOf(longitud),
						conjuro == null ? null : Conjuro.valueOf(conjuro));
			}
		} catch (Exception e) {
			System.err.println("La varita tiene valores antiguos y ya no es v�lida.");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public void recagarDatos() {

		ItemMeta im = getItemMeta();
		im.setDisplayName(ChatColor.RESET + "Varita de " + madera.toString());
		ArrayList<String> arrayList = new ArrayList<>();
		if (conjuro != null) {
			arrayList.add(ChatColor.GRAY + "Conjuro: " + conjuro.getColor() + conjuro.toString());
		}
		arrayList.add(ChatColor.GRAY + "N�cleo: " + nucleo.toString());
		arrayList.add(ChatColor.GRAY + "Madera: " + madera.toString());
		arrayList.add(ChatColor.GRAY + "Flexibilidad: " + flexibilidad.toString());
		arrayList.add(ChatColor.GRAY + "Longitud: " + longitud.toString());
		im.setLore(arrayList);
		im.getPersistentDataContainer().set(keyNumeroMagico, PersistentDataType.FLOAT, numeroMagico);
		im.getPersistentDataContainer().set(keyNucleo, PersistentDataType.STRING, nucleo.name());
		im.getPersistentDataContainer().set(keyMadera, PersistentDataType.STRING, madera.name());
		im.getPersistentDataContainer().set(keyFlexibilidad, PersistentDataType.STRING, flexibilidad.name());
		im.getPersistentDataContainer().set(keyLongitud, PersistentDataType.STRING, longitud.name());
		if (conjuro == null)
			im.getPersistentDataContainer().remove(keyConjuro);
		else
			im.getPersistentDataContainer().set(keyConjuro, PersistentDataType.STRING, conjuro.name());
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

	public Flexibilidad getFlexibilidad() {
		return flexibilidad;
	}

	public Nucleo getNucleo() {
		return nucleo;
	}

	public Longitud getLongitud() {
		return longitud;
	}

	public Conjuro getConjuro() {
		return conjuro;
	}

	@Override
	public String toString() {
		return "Varita [numeroMagico=" + numeroMagico + ", " + (nucleo != null ? "nucleo=" + nucleo + ", " : "")
				+ (madera != null ? "madera=" + madera + ", " : "")
				+ (flexibilidad != null ? "flexibilidad=" + flexibilidad + ", " : "")
				+ (longitud != null ? "longitud=" + longitud + ", " : "")
				+ (conjuro != null ? "conjuro=" + conjuro : "") + "]";
	}

	public static enum Nucleo {
		ASTAS_DE_LEBRILOPE("Astas De Lebr�lope"), BIGOTES_DE_KNEAZLE, BIGOTES_DE_TROL, CORAL, CUERNO_DE_BASILISCO,
		CUERNO_DE_SERPIENTE_CORNUDA, ESPINA_DEL_MONSTRUO_DEL_RIO_BLANCO("Espina Del Monstruo Del R�o Blanco"),
		FIBRA_DE_CORAZON_DE_DRAGON("Fibra De Coraz�n De Drag�n"),
		FIBRA_DE_CORAZON_DE_SNALLYGASTER("Fibra De Coraz�n De Snallygaster"), PELO_DE_COLA_DE_THESTRAL,
		PELO_DE_GATO_WAMPUS, PELO_DE_KELPIE, PELO_DE_ROUGAROU, PELO_DE_COLA_DE_UNICORNIO, PELO_DE_VEELA,
		PLUMA_DE_COLA_DE_AVE_DEL_TRUENO, PLUMA_DE_FENIX("Pluma De F�nix");
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

	public static enum Madera {
		ABEDUL, ABETO, ACACIA, ACEBO, ALAMO("�lamo"), ALAMO_TEMBLON("�lamo Tembl�n"), ALERCE, ALISO, ARCE,
		ARCE_AZUCARADO, AVELLANO, CANA("Ca�a"), CAOBA, CARPE, CASTANO("Casta�o"), CEDRO, CEREZO, CIPRES("Cipr�s"),
		CORNEJO, EBANO("�bano"), ENDRINO, ESPINO, ESPINO_DE_MAYO, FRESNO, FRESNO_ESPINOSO, HAYA, HIEDRA, LAUREL,
		MADERA_DE_SERPIENTE, MANZANO, NOGAL, NOGAL_NEGRO, OLIVO, OLMO, PALISANDRO, PERAL, PICEA("P�cea"), PINO,
		ROBLE_INGLES("Roble Ingl�s"), ROBLE_ROJO, SAUCE, SAUCO("Sa�co"), SECOYA, SERBAL, SICOMORO("Sic�moro"), TAMARACK,
		TEJO, TILO_PLATEADO, VID;
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

	public static enum Flexibilidad {
		MUY_RIGIDA("Muy R�gida"), RIGIDA("R�gida"), ALGO_RIGIDA("Algo R�gida"), FLEXIBILIDAD_MEDIA, MUY_FLEXIBLE,
		INCREIBLEMENTE_FLEXIBLE("Incre�blemente Flexible"), EXTREMADAMENTE_FLEXIBLE;
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

	public static enum Longitud {
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

//		private Longitud(String nombre) {
//			this.nombre = nombre;
//		}

		public String getNombre() {
			return nombre;
		}

		@Override
		public String toString() {
			return nombre;
		}
	}

	public static enum Conjuro {
		AVADA_KEDAVRA(Material.GREEN_DYE, ChatColor.GREEN, 5);
		private String nombre;
		private Material ingrediente;
		private ChatColor color;
		private int cooldownSegundos;

		private Conjuro(Material ingrediente, ChatColor color, int cooldownSegundos) {
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
			this.color = color;
			this.cooldownSegundos = cooldownSegundos;
			this.ingrediente = ingrediente;
		}

		private Conjuro(String nombre, ChatColor color, int cooldownSegundos) {
			this.nombre = nombre;
			this.color = color;
			this.cooldownSegundos = cooldownSegundos;
		}

		public String getNombre() {
			return nombre;
		}

		public ChatColor getColor() {
			return color;
		}

		public int getCooldownSegundos() {
			return cooldownSegundos;
		}

		public Material getIngrediente() {
			return ingrediente;
		}

		@Override
		public String toString() {
			return nombre;
		}
	}

	public static class VaritaListener implements Listener {

		@EventHandler
		public void onPrepararCrafteo(PrepareItemCraftEvent e) {
			ItemStack[] items = e.getInventory().getMatrix();
			ArrayList<ItemStack> arrayList = new ArrayList<>(items.length);
			for (int i = 0; i < items.length; i++) {
				ItemStack itemStack = items[i];
				if (itemStack != null) {
					arrayList.add(itemStack);
				}
			}
			if (arrayList.size() == 2) {
				ItemStack ingrediente0 = arrayList.get(0);
				ItemStack ingrediente1 = arrayList.get(1);
				Varita v0 = Varita.convertir(plugin, ingrediente0);
				Varita v1 = Varita.convertir(plugin, ingrediente1);
				if (v0 == null && v1 != null) {
					v0 = new Varita(v1);
					ingrediente1 = ingrediente0;
				}
				if (v0 != null) {
					for (Conjuro c : Conjuro.values()) {
						if (ingrediente1.getType().equals(c.getIngrediente())) {
							e.getInventory().setResult(new Varita(v0, c));
							break;
						}
					}
				}
			}
		}

		@EventHandler
		public void onCrafteo(InventoryClickEvent e) {
			if (e.getSlotType().equals(SlotType.RESULT) && e.getClickedInventory() instanceof CraftingInventory) {
				CraftingInventory inv = (CraftingInventory) e.getClickedInventory();
				ItemStack is = inv.getResult();
				Varita varita = Varita.convertir(plugin, is);
				if (varita!=null) {
					e.setCancelled(true);
					HumanEntity p = e.getView().getPlayer();
					PlayerInventory pi = p.getInventory();
					ItemStack[] matriz = inv.getMatrix();
					for (ItemStack itemStack : matriz) {
						if (itemStack!=null) {
							itemStack.setAmount(itemStack.getAmount()-1);
							if (itemStack.getAmount()>0) {
								if (pi.firstEmpty()<0)
									p.getWorld().dropItem(p.getLocation(), itemStack);
								else
									pi.addItem(itemStack);
							}
						}
					}
					if (pi.firstEmpty()<0)
						p.getWorld().dropItem(p.getLocation(), varita);
					else
						pi.addItem(varita);
					inv.clear();
				}
			}
		}

		@EventHandler
		private void onInteractEntity(PlayerInteractEntityEvent e) {
			Player p = e.getPlayer();
			ItemStack item = p.getInventory().getItemInMainHand();
			if (item != null) {
				Varita varita = Varita.convertir(plugin, item);
				if (varita != null) {
					Float numeroMagicoP = numerosMagicos.get(p.getUniqueId());
					if (numeroMagicoP == null) {
						numeroMagicoP = new Random().nextFloat();
						numerosMagicos.put(p.getUniqueId(), numeroMagicoP);
					}
					if (varita.conjuro != null) {
						switch (varita.conjuro) {
						case AVADA_KEDAVRA:
							if (e.getRightClicked() instanceof LivingEntity) {
								LivingEntity victima = (LivingEntity) e.getRightClicked();
								if (!victima.isDead()) {
									victima.playEffect(EntityEffect.HURT_DROWN);
									victima.getWorld().strikeLightningEffect(victima.getLocation());
									victima.getWorld().playSound(victima.getLocation(), Sound.ENTITY_WITCH_CELEBRATE, 1,
											0.1F);
									victima.getWorld().playSound(victima.getLocation(), Sound.ENCHANT_THORNS_HIT, 1,
											0.1F);
									victima.setHealth(0);
								}
							}
							break;

						default:
							e.getPlayer().sendMessage(varita.conjuro.getColor() + varita.conjuro.toString());
							break;
						}
					}
				}
			}
		}

//		@EventHandler
//		private void onInteract(PlayerInteractEvent e) {
//			if (e.getItem() != null) {
//				Varita varita = Varita.convertir(plugin, e.getItem());
//				if (varita != null) {
//					Player p = e.getPlayer();
//					Float numeroMagicoP = numerosMagicos.get(p.getUniqueId());
//					if (numeroMagicoP == null) {
//						numeroMagicoP = new Random().nextFloat();
//						numerosMagicos.put(p.getUniqueId(), numeroMagicoP);
//					}
//					if (varita.conjuro != null) {
//						switch (varita.conjuro) {
//						case AVADA_KEDAVRA:
//							break;
//
//						default:
//							e.getPlayer().sendMessage(varita.conjuro.getColor() + varita.conjuro.toString());
//							break;
//						}
//					}
//				}
//			}
//		}
	}
}