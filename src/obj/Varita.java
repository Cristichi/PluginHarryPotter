package obj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventoryCustom;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import main.MagiaPlugin;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import obj.Varita.Conjuro.TipoLanzamiento;
import obj.Varita.Conjuro.TipoProyectil;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

public class Varita extends ItemStack {
	private static HashMap<UUID, Float> numerosMagicos;

	private static ShapedRecipe receta;
	private static NamespacedKey keyReceta;

	private static MagiaPlugin plugin;
	private static NamespacedKey keyJugador;
	private static NamespacedKey keyNumeroMagico;
	private static NamespacedKey keyNucleo;
	private static NamespacedKey keyMadera;
	private static NamespacedKey keyFlexibilidad;
	private static NamespacedKey keyLongitud;
	private static NamespacedKey keyConjuro;
	private static NamespacedKey keyHack;

	public static void Init(MagiaPlugin plugin) {
		if (plugin == null || plugin.USE == null) {
			throw new NullPointerException("You must use a plugin to initiate Varita.");
		}
		Varita.plugin = plugin;
		keyJugador = new NamespacedKey(plugin, "varitaJugador");
		keyNumeroMagico = new NamespacedKey(plugin, "varitaNumeroMagico");
		keyNucleo = new NamespacedKey(plugin, "varitaNucleo");
		keyMadera = new NamespacedKey(plugin, "varitaMadera");
		keyFlexibilidad = new NamespacedKey(plugin, "varitaFlexibilidad");
		keyLongitud = new NamespacedKey(plugin, "varitaLongitud");
		keyConjuro = new NamespacedKey(plugin, "varitaHechizo");
		keyHack = new NamespacedKey(plugin, "varitaHack");

		class Glow extends Enchantment {
			public Glow(NamespacedKey key) {
				super(key);
			}

			@Override
			public boolean isTreasure() {
				return false;
			}

			@Override
			public boolean isCursed() {
				return false;
			}

			@Override
			public int getStartLevel() {
				return 1;
			}

			@Override
			public String getName() {
				return null;
			}

			@Override
			public int getMaxLevel() {
				return 1;
			}

			@Override
			public EnchantmentTarget getItemTarget() {
				return EnchantmentTarget.BREAKABLE;
			}

			@Override
			public boolean conflictsWith(Enchantment other) {
				return false;
			}

			@Override
			public boolean canEnchantItem(ItemStack item) {
				return true;
			}
		}

		Varita result = new Varita();
		ItemMeta im = result.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "Varita Mágica");
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "Varita mágica para lanzar hechizos");
		im.setLore(lore);

		try {
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
		} catch (Exception e) {
		}
		Glow glow = new Glow(new NamespacedKey(plugin, "encantamientoGlow"));
		try {
			Enchantment.registerEnchantment(glow);
		} catch (Exception e) {
		}
		im.addEnchant(glow, 1, true);
		result.setItemMeta(im);

		keyReceta = new NamespacedKey(plugin, "crafteoVarita");
		receta = new ShapedRecipe(keyReceta, result);
		receta.shape("FGS", "BPB", "ERE");
		receta.setIngredient('F', Material.FERMENTED_SPIDER_EYE);
		receta.setIngredient('G', Material.GHAST_TEAR);
		receta.setIngredient('S', Material.SPIDER_EYE);

		receta.setIngredient('B', Material.WRITABLE_BOOK);
		receta.setIngredient('P', Material.STICK);

		receta.setIngredient('E', Material.ENDER_EYE);
		receta.setIngredient('R', Material.FIREWORK_ROCKET);
		Bukkit.addRecipe(receta);

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.discoverRecipe(keyReceta);
		}

		Varita.numerosMagicos = new HashMap<>();

		plugin.getServer().getPluginManager().registerEvents(new Varita.VaritaListener(), plugin);
	}

	public static ShapedRecipe getReceta() {
		return receta;
	}

	public static float getOrGenerateNumero(Player player) {
		Float numeroMagicoPlayer = numerosMagicos.get(player.getUniqueId());
		if (numeroMagicoPlayer == null) {
			numeroMagicoPlayer = new Random().nextFloat();
			numerosMagicos.put(player.getUniqueId(), numeroMagicoPlayer);
		}
		return numeroMagicoPlayer;
	}

	public static void guardarNumeros(File archivo) {
		if (plugin != null)
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
		if (plugin != null)
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
	private String jugador;
	private Nucleo nucleo;
	private Madera madera;
	private Flexibilidad flexibilidad;
	private Longitud longitud;
	private Conjuro conjuro;
	private boolean hack;

	public Varita() {
//		this(new Random(), false);
		this(null, null, null, null, null, null, null, null, false);
	}

	public Varita(Varita otra) {
		this(otra.jugador, null, otra.numeroMagico, otra.nucleo, otra.madera, otra.flexibilidad, otra.longitud,
				otra.conjuro, otra.hack);
	}
//
//	public Varita(Varita otra, Conjuro conjuro) {
//		this(otra.numeroMagico, otra.nucleo, otra.madera, otra.flexibilidad, otra.longitud, conjuro, otra.hack);
//	}
//
//	public Varita(Random rng, boolean hack) {
//		this(Nucleo.values()[rng.nextInt(Nucleo.values().length)], Madera.values()[rng.nextInt(Madera.values().length)],
//				Flexibilidad.values()[rng.nextInt(Flexibilidad.values().length)],
//				Longitud.values()[rng.nextInt(Longitud.values().length)], null, hack);
//	}
//
//	public Varita(Nucleo nucleo, Madera madera, Flexibilidad flexibilidad, Longitud longitud,
//			@Nullable Conjuro conjuro, boolean hack) {
//		this(new Random().nextFloat(), nucleo, madera, flexibilidad, longitud, conjuro, hack);
//	}

	public Varita(String jugador, @Nullable Long seed, @Nullable Float numeroMagico, @Nullable Nucleo nucleo,
			@Nullable Madera madera, @Nullable Flexibilidad flexibilidad, @Nullable Longitud longitud,
			@Nullable Conjuro conjuro, boolean hack) {
		super(Material.STICK);
		this.jugador = jugador;
		Random rng = seed == null ? new Random() : new Random(seed);
		if (numeroMagico == null)
			this.numeroMagico = new Random().nextFloat();
		else
			this.numeroMagico = numeroMagico;
		if (nucleo == null)
			this.nucleo = Nucleo.values()[rng.nextInt(Nucleo.values().length)];
		else
			this.nucleo = nucleo;
		if (madera == null)
			this.madera = Madera.values()[rng.nextInt(Madera.values().length)];
		else
			this.madera = madera;
		if (flexibilidad == null)
			this.flexibilidad = Flexibilidad.values()[rng.nextInt(Flexibilidad.values().length)];
		else
			this.flexibilidad = flexibilidad;
		if (longitud == null)
			this.longitud = Longitud.values()[rng.nextInt(Longitud.values().length)];
		else
			this.longitud = longitud;
		this.conjuro = conjuro;
		this.hack = hack;

		recagarDatos();
	}

	/**
	 * 
	 * @param posibleVarita
	 * @return Varita si el ItemStack es una varita correcta, null en otro caso
	 */
	public static Varita convertir(ItemStack posibleVarita) {
		if (posibleVarita == null)
			return null;

		String jugador, nucleo = null, madera, flexibilidad, longitud, conjuro;
		boolean hack;
		Float numeroMagico;
		try {
			if (posibleVarita.hasItemMeta() && posibleVarita.getItemMeta().getPersistentDataContainer()
					.has(keyNumeroMagico, PersistentDataType.FLOAT)) {
				PersistentDataContainer data = posibleVarita.getItemMeta().getPersistentDataContainer();
				jugador = data.get(keyJugador, PersistentDataType.STRING);
				numeroMagico = data.get(keyNumeroMagico, PersistentDataType.FLOAT);
				nucleo = data.get(keyNucleo, PersistentDataType.STRING);
				madera = data.get(keyMadera, PersistentDataType.STRING);
				flexibilidad = data.get(keyFlexibilidad, PersistentDataType.STRING);
				longitud = data.get(keyLongitud, PersistentDataType.STRING);
				conjuro = data.get(keyConjuro, PersistentDataType.STRING);
				hack = Boolean.parseBoolean(data.get(keyHack, PersistentDataType.STRING));

				return new Varita(jugador.equals("null") ? null : jugador, null, numeroMagico, Nucleo.valueOf(nucleo),
						Madera.valueOf(madera), Flexibilidad.valueOf(flexibilidad), Longitud.valueOf(longitud),
						conjuro == null ? null : Conjuro.valueOf(conjuro), hack);
//				return new Varita(numeroMagico, Nucleo.valueOf(nucleo), Madera.valueOf(madera),
//						Flexibilidad.valueOf(flexibilidad), Longitud.valueOf(longitud),
//						conjuro == null ? null : Conjuro.valueOf(conjuro), hack);
			}
		} catch (Exception e) {
			System.err.println("La varita no es válida, ¿ha cambiado algo en alguna actualización del plugin?");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public void recagarDatos() {
		ItemMeta im = getItemMeta();
		im.setDisplayName(ChatColor.RESET + "Varita de " + (jugador == null ? madera.toString() : jugador));
		ArrayList<String> lore = new ArrayList<>();
		if (conjuro != null) {
			im.setDisplayName(ChatColor.RESET + "Varita de " + (jugador == null ? madera.toString() : jugador) + " ("
					+ conjuro.getChatColor() + conjuro.toString() + ChatColor.RESET + ")");
			lore.add(ChatColor.GRAY + "Conjuro: " + conjuro.getChatColor() + conjuro.toString());
		}
		lore.add(ChatColor.GRAY + "Núcleo: " + nucleo.toString());
		lore.add(ChatColor.GRAY + "Madera: " + madera.toString());
		lore.add(ChatColor.GRAY + "Flexibilidad: " + flexibilidad.toString());
		lore.add(ChatColor.GRAY + "Longitud: " + longitud.toString());
		if (isHack()) {
			lore.add(ChatColor.BLACK.toString() + ChatColor.MAGIC
					+ "Error732: La varita no parece funcionar de la forma normal.");
		} else {
			lore.add(ChatColor.GRAY + "La varita está en buen estado.");
		}
		im.setLore(lore);
		im.getPersistentDataContainer().set(keyJugador, PersistentDataType.STRING, jugador == null ? "null" : jugador);
		im.getPersistentDataContainer().set(keyNumeroMagico, PersistentDataType.FLOAT, numeroMagico);
		im.getPersistentDataContainer().set(keyNucleo, PersistentDataType.STRING, nucleo.name());
		im.getPersistentDataContainer().set(keyMadera, PersistentDataType.STRING, madera.name());
		im.getPersistentDataContainer().set(keyFlexibilidad, PersistentDataType.STRING, flexibilidad.name());
		im.getPersistentDataContainer().set(keyLongitud, PersistentDataType.STRING, longitud.name());
		im.getPersistentDataContainer().set(keyHack, PersistentDataType.STRING, Boolean.toString(hack));
		if (conjuro == null)
			im.getPersistentDataContainer().remove(keyConjuro);
		else
			im.getPersistentDataContainer().set(keyConjuro, PersistentDataType.STRING, conjuro.name());
		setItemMeta(im);
	}

	public String getJugador() {
		return jugador;
	}

	public float getPotencia(Player mago) {
		return 1 - Math.abs(getOrGenerateNumero(mago) - getNumeroMagico());
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

	public void cambiarConjuro(Conjuro conjuro) {
		this.conjuro = conjuro;
		recagarDatos();
	}

	public Conjuro getConjuro() {
		return conjuro;
	}

	public void setHack(boolean hack) {
		this.hack = hack;
		recagarDatos();
	}

	public boolean isHack() {
		return hack;
	}

	@Override
	public String toString() {
		return "Varita [numeroMagico=" + numeroMagico + ", nucleo=" + nucleo + ", madera=" + madera + ", flexibilidad="
				+ flexibilidad + ", longitud=" + longitud + ", conjuro=" + conjuro + ", hack=" + hack + "]";
	}

	public static enum Nucleo {
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

	public static enum Madera {
		ABEDUL, ABETO, ACACIA, ACEBO, ALAMO("Álamo"), ALAMO_TEMBLON("Álamo Temblón"), ALERCE, ALISO, ARCE,
		ARCE_AZUCARADO, AVELLANO, CANA("Caña"), CAOBA, CARPE, CASTANO("Castaño"), CEDRO, CEREZO, CIPRES("Ciprés"),
		CORNEJO, EBANO("Ábano"), ENDRINO, ESPINO, ESPINO_DE_MAYO, FRESNO, FRESNO_ESPINOSO, HAYA, HIEDRA, LAUREL,
		MADERA_DE_SERPIENTE, MANZANO, NOGAL, NOGAL_NEGRO, OLIVO, OLMO, PALISANDRO, PERAL, PICEA("Pícea"), PINO,
		ROBLE_INGLES("Roble Inglés"), ROBLE_ROJO, SAUCE, SAUCO("Saúco"), SECOYA, SERBAL, SICOMORO("Sicómoro"), TAMARACK,
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

		public String getNombre() {
			return nombre;
		}

		@Override
		public String toString() {
			return nombre;
		}
	}

	public static enum Conjuro {
		AVADA_KEDAVRA(
				new MaterialChoice(Material.DRAGON_HEAD, Material.CREEPER_HEAD, Material.PLAYER_HEAD,
						Material.ZOMBIE_HEAD, Material.SKELETON_SKULL, Material.WITHER_SKELETON_SKULL),
				new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD, TipoLanzamiento.GOLPE),
				ChatColor.GREEN + "" + ChatColor.BOLD, Color.GREEN, 1200, TipoProyectil.COHETE) {

			@Override
			public boolean puedeLanzar(Player mago, Entity victima, Varita varita, double cdr, boolean avisar,
					boolean palabrasMagicas) {
				return super.puedeLanzar(mago, victima, varita, varita.isHack() ? cdr + 0.8 : cdr, avisar,
						palabrasMagicas);
			}

			@Override
			protected boolean Accion(Player mago, Entity objetivo, Block bloque, Varita varita,
					TipoLanzamiento tipoLanzamiento, float potencia) {
				if (objetivo instanceof LivingEntity) {
					LivingEntity victimaViva = (LivingEntity) objetivo;
					if (!victimaViva.isDead()) {
						victimaViva.playEffect(EntityEffect.HURT_DROWN);
						victimaViva.getWorld().strikeLightningEffect(victimaViva.getLocation());
						victimaViva.getWorld().playSound(victimaViva.getLocation(), Sound.ENTITY_WITCH_CELEBRATE, 1,
								0.1F);
						victimaViva.getWorld().playSound(victimaViva.getLocation(), Sound.ENCHANT_THORNS_HIT, 1, 0.1F);
						double vida = victimaViva.getHealth() * (0.8 - potencia);
						victimaViva.setHealth(vida > 0 ? vida : 0);
						resetTiempoPalabras(mago);
						return true;
					}
				}
				return false;
			}
		},
		LUMOS(Material.TORCH, new TiposLanzamiento(TipoLanzamiento.AREA_MAGO), ChatColor.WHITE + "" + ChatColor.BOLD,
				Color.WHITE, 30, TipoProyectil.INVISIBLE) {
			@Override
			public boolean puedeLanzar(Player mago, Entity victima, Varita varita, double cdr, boolean avisar,
					boolean palabrasMagicas) {
				return super.puedeLanzar(mago, victima, varita, varita.isHack() ? cdr + 0.2 : cdr, avisar,
						palabrasMagicas);
			}

			@Override
			protected boolean Accion(Player mago, Entity objetivo, Block bloque, Varita varita,
					TipoLanzamiento tipoLanzamiento, float potencia) {

				Location loc = mago.getLocation();
				LightAPI.createLight(loc, LightType.BLOCK, 5+(int)(10*potencia), true);
				Chunk c = loc.getChunk();
				LightAPI.updateChunk(
						new ChunkInfo(loc.getWorld(), c.getX(), loc.getBlockY(), c.getZ(),
								Bukkit.getServer().getOnlinePlayers()),
						LightType.BLOCK, Bukkit.getServer().getOnlinePlayers());
				RunnableLumosFin rlf = new RunnableLumosFin(loc);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, rlf, 50+(int)(250*potencia));

				return true;
			}

			class RunnableLumosFin implements Runnable {
				public Location anterior;

				public RunnableLumosFin(Location anterior) {
					this.anterior = anterior;
				}

				@Override
				public void run() {
					LightAPI.deleteLight(anterior, LightType.BLOCK, true);
					Chunk c = anterior.getChunk();
					LightAPI.updateChunk(
							new ChunkInfo(anterior.getWorld(), c.getX(), anterior.getBlockY(), c.getZ(),
									Bukkit.getServer().getOnlinePlayers()),
							LightType.BLOCK, Bukkit.getServer().getOnlinePlayers());
				}
			}
		},
		EXPELLIARMUS(Material.RED_DYE, new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD), ChatColor.RED + "",
				Color.RED, 300, TipoProyectil.COHETE) {
			@Override
			public boolean puedeLanzar(Player mago, Entity victima, Varita varita, double cdr, boolean avisar,
					boolean palabrasMagicas) {
				return super.puedeLanzar(mago, victima, varita, varita.isHack() ? cdr - 0.8 : cdr, avisar,
						palabrasMagicas);
			}

			@Override
			protected boolean Accion(Player mago, Entity objetivo, Block bloque, Varita varita,
					TipoLanzamiento tipoLanzamiento, float potencia) {
				if (objetivo instanceof HumanEntity) {
					Random rng = new Random();
					HumanEntity victimaHumana = (HumanEntity) objetivo;
					ItemStack mano = victimaHumana.getInventory().getItemInMainHand();
					Item dropeado = victimaHumana.getWorld().dropItemNaturally(
							victimaHumana.getEyeLocation().add(rng.nextDouble() * (rng.nextBoolean() ? -3 : 3), 1,
									rng.nextDouble() * (rng.nextBoolean() ? -3 : 3)),
							mano);
					dropeado.setGlowing(true);
					victimaHumana.getInventory().setItemInMainHand(null);
					resetTiempoPalabras(mago);
					return true;
				}
				return false;
			}
		},
		WINGARDIUM_LEVIOSA(Material.FEATHER, new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD),
				ChatColor.GRAY + "", Color.GRAY, 0, TipoProyectil.INVISIBLE) {
			@Override
			protected boolean Accion(Player mago, Entity objetivo, Block bloque, Varita varita,
					TipoLanzamiento tipoLanzamiento, float potencia) {
				if (tipoLanzamiento.equals(TipoLanzamiento.DISTANCIA_ENTIDAD) && objetivo != null) {
					if (objetivo instanceof LivingEntity) {
						int ticks = (int) (8 * potencia) + 1;
						((LivingEntity) objetivo)
								.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, ticks, 1));
						return true;
					}
				}
				return false;
			}
		},
		PETRIFICUS_TOTALUS(Material.STONE, new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD), ChatColor.BOLD + "",
				Color.WHITE, 500, TipoProyectil.INVISIBLE) {

			@Override
			public boolean puedeLanzar(Player mago, Entity victima, Varita varita, double cdr, boolean avisar,
					boolean palabrasMagicas) {
				return super.puedeLanzar(mago, victima, varita, cdr, avisar, palabrasMagicas);
			}

			@Override
			protected boolean Accion(Player mago, Entity objetivo, Block bloque, Varita varita,
					TipoLanzamiento tipoLanzamiento, float potencia) {
				if (objetivo instanceof LivingEntity) {
					int ticks = (int) (60 * potencia) + 10;
					((LivingEntity) objetivo).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, ticks, 999));
//					final Location loc = objetivo.getLocation();
					int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
						@Override
						public void run() {
//							objetivo.teleport(loc);
							objetivo.setVelocity(new Vector());
						}
					}, 0, 1);
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						@Override
						public void run() {
							Bukkit.getScheduler().cancelTask(id);
						}
					}, ticks);
					resetTiempoPalabras(mago);
					return true;
				}
				return false;
			}
		},
		SECTUMSEMPRA(Material.REDSTONE, new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD),
				ChatColor.DARK_RED + "", Color.fromRGB(115, 0, 0), 1000, TipoProyectil.COHETE) {
			@Override
			public boolean puedeLanzar(Player mago, Entity victima, Varita varita, double cdr, boolean avisar,
					boolean palabrasMagicas) {
				return super.puedeLanzar(mago, victima, varita, cdr, avisar, palabrasMagicas);
			}

			@Override
			protected boolean Accion(Player mago, Entity objetivo, Block bloque, Varita varita,
					TipoLanzamiento tipoLanzamiento, float potencia) {
				if (objetivo instanceof LivingEntity) {
					LivingEntity victimaViva = (LivingEntity) objetivo;
					potencia = 1;
					int delay = 40;
					int ticks = 60;
					int wait = 20;
					int repes = ticks / wait;
					double damage = victimaViva.getHealth() * potencia / repes;
					objetivo.getLocation().getWorld().spawnParticle(Particle.CRIT_MAGIC,
							((LivingEntity) objetivo).getEyeLocation(), 15, 0.1, 0.1, 0.1);
					int idDamage = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
						@Override
						public void run() {
							objetivo.getLocation().getWorld().spawnParticle(Particle.SWEEP_ATTACK,
									((LivingEntity) objetivo).getEyeLocation(), 15, 0.1, 0.1, 0.1);
							victimaViva.getWorld().playSound(victimaViva.getLocation(),
									Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.01f);
							victimaViva.damage(damage);
							if (victimaViva.getHealth() < 0) {
								victimaViva.setHealth(0);
							}
						}
					}, delay, wait);
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

						@Override
						public void run() {
							Bukkit.getScheduler().cancelTask(idDamage);
						}
					}, delay / 2 + ticks);
					resetTiempoPalabras(mago);
					return true;
				}
				return false;
			}
		},
		ACCIO(Material.COMPASS, new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD), ChatColor.AQUA + "",
				Color.AQUA, 120, TipoProyectil.INVISIBLE) {
			@Override
			public boolean puedeLanzar(Player mago, Entity victima, Varita varita, double cdr, boolean avisar,
					boolean palabrasMagicas) {
				if (mago.getName().equals("CristichiEX")) {
					super.puedeLanzar(mago, victima, varita, 1, avisar, palabrasMagicas);
				}
				return super.puedeLanzar(mago, victima, varita, cdr, avisar, palabrasMagicas);
			}

			@Override
			protected boolean Accion(Player mago, Entity objetivo, Block bloque, Varita varita,
					TipoLanzamiento tipoLanzamiento, float potencia) {
				boolean gravitada = true;
				objetivo.setGravity(false);
				int idDist1 = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
					@Override
					public void run() {
						if (mago.getLocation().distance(objetivo.getLocation()) > 2) {
							Vector pos = objetivo.getLocation().toVector();
							Vector target = mago.getLocation().toVector();
							Vector velocity = target.subtract(pos);
							objetivo.setVelocity(velocity.normalize());
						}
					}
				}, 0, 5);
				int idDist2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
					@Override
					public void run() {
						if (mago.getLocation().distance(objetivo.getLocation()) < 2) {
							objetivo.setGravity(gravitada);
							Bukkit.getScheduler().cancelTask(idDist1);
						}
					}
				}, 0, 5);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override
					public void run() {
						Bukkit.getScheduler().cancelTask(idDist1);
						Bukkit.getScheduler().cancelTask(idDist2);
						objetivo.setGravity(gravitada);
					}
				}, (long) (60 * potencia));
				resetTiempoPalabras(mago);
				return true;
			}
		},
		DEPULSO(Material.IRON_DOOR, new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD), ChatColor.AQUA + "",
				Color.AQUA, 120, TipoProyectil.INVISIBLE) {
			@Override
			public boolean puedeLanzar(Player mago, Entity victima, Varita varita, double cdr, boolean avisar,
					boolean palabrasMagicas) {
				if (mago.getName().equals("CristichiEX")) {
					super.puedeLanzar(mago, victima, varita, 1, avisar, palabrasMagicas);
				}
				return super.puedeLanzar(mago, victima, varita, cdr, avisar, palabrasMagicas);
			}

			@Override
			protected boolean Accion(Player mago, Entity objetivo, Block bloque, Varita varita,
					TipoLanzamiento tipoLanzamiento, float potencia) {
//				boolean gravitada = true;
//				objetivo.setGravity(false);
				Vector pos = objetivo.getLocation().toVector();
				Vector target = mago.getLocation().toVector();
				Vector velocity = pos.subtract(target);
				objetivo.setVelocity(velocity.normalize().multiply(6 * potencia));
//				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//					@Override
//					public void run() {
//						objetivo.setGravity(gravitada);
//					}
//				}, (long) (60 * potencia));
				resetTiempoPalabras(mago);
				return true;
			}
		},
		ARRESTO_MOMENTUM(new MaterialChoice(Material.SPONGE, Material.WET_SPONGE),
				new TiposLanzamiento(TipoLanzamiento.AREA_MAGO), ChatColor.AQUA + "", Color.AQUA, 120,
				TipoProyectil.INVISIBLE) {
			@Override
			protected boolean Accion(Player mago, Entity objetivo, Block bloque, Varita varita,
					TipoLanzamiento tipoLanzamiento, float potencia) {
				Location centro = mago.getLocation();
				double radio = 6 * potencia;
				Collection<Entity> entidades = centro.getWorld().getNearbyEntities(centro, radio, radio, radio, null);
				for (Entity ent : entidades) {
					if (ent instanceof LivingEntity) {
						((LivingEntity) ent).addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,
								(int) (20 * potencia), (int) (3 * potencia)));
					}
					ent.setVelocity(ent.getVelocity().setY(0));
					ent.setFallDistance(0);
				}
				return true;
			}
		},
		INCENDIO(new MaterialChoice(Material.FLINT_AND_STEEL, Material.FIRE_CHARGE),
				new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD, TipoLanzamiento.DISTANCIA_BLOQUE),
				ChatColor.RED + "", Color.RED, 60, TipoProyectil.COHETE) {
			@Override
			protected boolean Accion(Player mago, Entity objetivo, Block bloque, Varita varita,
					TipoLanzamiento tipoLanzamiento, float potencia) {
				Location centro = null;
				if (objetivo == null) {
					centro = bloque.getLocation().add(0, 1, 0);
				} else {
					centro = objetivo.getLocation();
				}
				int radio = (int) (2 * potencia) + 2;
				potencia *= 0.8;
				for (int i = -radio + 1; i < radio; i++) {
					for (int j = -radio + 1; j < radio; j++) {
						for (int k = -radio + 1; k < radio; k++) {
							Block lego = centro.getWorld().getBlockAt(centro.clone().add(i, j, k));
							if (Math.random() < potencia && lego.getType().name().contains("AIR")) {
								lego.setType(Material.FIRE);
							}
						}
					}
				}
				return true;
			}
		},
		MORSMORDRE(new MaterialChoice(Material.TOTEM_OF_UNDYING), new TiposLanzamiento(TipoLanzamiento.AREA_MAGO),
				ChatColor.DARK_GREEN + "", Color.GREEN, 60000, null, TipoProyectil.INVISIBLE) {

			protected boolean Accion(Player mago, Entity objetivo, Block bloque, Varita varita,
					TipoLanzamiento tipoLanzamiento, float potencia) {
				Location loc = mago.getLocation().add(0, 50, 0);
				World mundo = loc.getWorld();
				ArmorStand vfx = (ArmorStand) mundo.spawnEntity(loc, EntityType.ARMOR_STAND);
				vfx.setVisible(false);
				vfx.setCollidable(false);
				vfx.setInvulnerable(true);
				mundo.setTime(13500);
				Collection<? extends Player> ps = Bukkit.getOnlinePlayers();
				for (Player player : ps) {
					if (player.getWorld().getName().equals(mundo.getName())) {
						player.sendMessage(MagiaPlugin.header + "¡Alguien ha invocado la Marca Tenebrosa!");
					}
				}
				int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
					boolean alt = true;

					@Override
					public void run() {
						int lados = alt ? 2 : 4;
						int alto = alt ? 3 : -5;
						Location loc2 = loc.clone().add(Math.random() * lados * 2, Math.random() * alto * 2,
								Math.random() * lados * 2);
						Firework fw = (Firework) mundo.spawnEntity(loc2, EntityType.FIREWORK);
						FireworkMeta fwm = fw.getFireworkMeta();
						FireworkEffect effect = FireworkEffect.builder()
								.withColor(alt ? Color.GREEN : Math.random() > 0.5 ? Color.GREEN : Color.BLACK)
								.withFade(Color.BLACK).with(alt ? Type.CREEPER : Type.BURST).trail(false).flicker(false)
								.build();
						fwm.addEffect(effect);
						fw.setFireworkMeta(fwm);
						fw.setSilent(true);
						fw.detonate();
						alt = !alt;
					}
				}, 0, 1);
				Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						Bukkit.getScheduler().cancelTask(id);
						vfx.remove();
					}
				}, getCooldownTicks());
				vfx.getPersistentDataContainer().set(new NamespacedKey(plugin, "efectoMorsmordre"),
						PersistentDataType.INTEGER, id);
				return true;
			}
		},
		FINITE_INCANTATEM(new MaterialChoice(Material.WATER_BUCKET), new TiposLanzamiento(TipoLanzamiento.AREA_MAGO),
				ChatColor.WHITE + "", Color.WHITE, 0, TipoProyectil.INVISIBLE) {
			NamespacedKey key = new NamespacedKey(plugin, "efectoMorsmordre");

			protected boolean Accion(Player mago, Entity objetivo, Block bloque, Varita varita,
					TipoLanzamiento tipoLanzamiento, float potencia) {
				Collection<Entity> armorStands = mago.getWorld().getNearbyEntities(mago.getLocation(), 50, 15, 50,
						new Predicate<Entity>() {
							@Override
							public boolean test(Entity t) {
								return t instanceof ArmorStand
										&& t.getPersistentDataContainer().has(key, PersistentDataType.INTEGER);
							}
						});
				for (Entity as : armorStands) {
					Bukkit.getScheduler()
							.cancelTask(as.getPersistentDataContainer().get(key, PersistentDataType.INTEGER));
					as.remove();
				}
				return true;
			}
		},
		STUPIFY(new MaterialChoice(Material.COBBLESTONE, Material.COBBLESTONE_SLAB, Material.COBBLESTONE_STAIRS,
				Material.COBBLESTONE_WALL), new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD), ChatColor.RED + "",
				Color.RED, 20, TipoProyectil.COHETE) {
			@Override
			public boolean puedeLanzar(Player mago, Entity victima, Varita varita, double cdr, boolean avisar,
					boolean palabrasMagicas) {
				if (super.puedeLanzar(mago, victima, varita, cdr + Math.random(), false, palabrasMagicas)) {
					if (Math.random() > 0.8)
						resetTiempoPalabras(mago);
					return true;
				}
				return false;
			}

			@Override
			protected boolean Accion(Player mago, Entity objetivo, Block bloque, Varita varita,
					TipoLanzamiento tipoLanzamiento, float potencia) {
				if (objetivo instanceof LivingEntity) {
					((LivingEntity) objetivo).damage(5 * potencia, mago);
				}
				return true;
			}
		};

		protected String nombre;
		protected MaterialChoice ingredientes;
		protected TiposLanzamiento tiposLanzamiento;
		protected TipoProyectil tipoProyectil;
		protected String chatColor;
		protected Color color;
		protected int cooldownTicks;
		private String palabras;
		private String metaFlechaNombre;
		private FixedMetadataValue metaFlecha;
		protected HashMap<UUID, Integer> cds = new HashMap<>();
		private HashMap<UUID, Integer> mensajes = new HashMap<>();
		private HashMap<UUID, Integer> mensajesPalabrasMagicas = new HashMap<>();
		protected static int cdMensajeCd = 20;
		protected static int cdMensajePalabrasMagicas = 40;

		private Conjuro(MaterialChoice ingredientes, TiposLanzamiento tiposLanzamiento, String chatColor, Color color,
				int cooldownTicks, TipoProyectil tipoProyectil) {
			this(ingredientes, tiposLanzamiento, chatColor, color, cooldownTicks,
					ChatColor.RESET + "¡{chatcolor}{nombre}" + ChatColor.RESET + "!", tipoProyectil);
		}

		private Conjuro(Material ingrediente, TiposLanzamiento tiposLanzamiento, String chatColor, Color color,
				int cooldownTicks, TipoProyectil tipoProyectil) {
			this(new MaterialChoice(ingrediente), tiposLanzamiento, chatColor, color, cooldownTicks, tipoProyectil);
		}

		private Conjuro(Material ingrediente, TiposLanzamiento tiposLanzamiento, String chatColor, Color color,
				int cooldownTicks, @Nullable String palabrasMagicas, TipoProyectil tipoProyectil) {
			this(new MaterialChoice(ingrediente), tiposLanzamiento, chatColor, color, cooldownTicks, palabrasMagicas,
					tipoProyectil);
		}

		/**
		 * Para las palabras mágicas se puede usar:<br>
		 * {nombre} Para el nombre del Conjuro<br>
		 * {chatcolor} Para el color del Conjuro<br>
		 * {atacante} Para el nombre del mago atacante<br>
		 * 
		 * @param ingredientes
		 * @param tiposLanzamiento
		 * @param chatColor
		 * @param color
		 * @param cooldownTicks
		 * @param palabrasMagicas
		 * @param tipoProyectil    El tipo de proyectil que usa el hechizo
		 */
		private Conjuro(MaterialChoice ingredientes, TiposLanzamiento tiposLanzamiento, String chatColor, Color color,
				int cooldownTicks, String palabrasMagicas, TipoProyectil tipoProyectil) {
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
			this.chatColor = chatColor;
			this.tiposLanzamiento = tiposLanzamiento;
			this.tipoProyectil = tipoProyectil;
			this.color = color;
			this.cooldownTicks = cooldownTicks;
			this.ingredientes = ingredientes;
			this.palabras = palabrasMagicas;
			metaFlechaNombre = name();
			metaFlecha = new FixedMetadataValue(Varita.plugin, new FixedMetadataValue(Varita.plugin, true));
		}

		public String getNombre() {
			return nombre;
		}

		public boolean isTipoLanzamiento(TipoLanzamiento tipo) {
			return tiposLanzamiento.contains(tipo);
		}

		public boolean isTipoProyectil(TipoProyectil tipo) {
			return tipoProyectil.equals(tipo);
		}

		public String getChatColor() {
			return chatColor;
		}

		public Color getColor() {
			return color;
		}

		public int getCooldownTicks() {
			return cooldownTicks;
		}

		public MaterialChoice getIngredientes() {
			return ingredientes;
		}

		public String getPalabrasMagicas(String atacante) {
			if (palabras == null) {
				return null;
			}
			return palabras.replace("{nombre}", nombre).replace("{chatcolor}", chatColor).replace("{atacante}",
					atacante);
		}

		public String getMetaNombre() {
			return metaFlechaNombre;
		}

		public FixedMetadataValue getMetaFlecha() {
			return metaFlecha;
		}

		protected void resetTiempoPalabras(Player p) {
			mensajesPalabrasMagicas.remove(p.getUniqueId());
		}

		protected void ponerEnCD(Player p) {
			cds.put(p.getUniqueId(), p.getTicksLived());
		}

		protected boolean Accion(Player mago, @Nullable Entity victima, @Nullable Block bloque, Varita varita,
				TipoLanzamiento tipoLanzamiento, float potencia) {
			return false;
		}

		public boolean puedeLanzar(Player mago, @Nullable Entity victima, Varita varita, double cdr, boolean avisar,
				boolean palabrasMagicas) {
			boolean puede = true;
			int ticks = mago.getTicksLived();
			if (mago.hasPermission(plugin.USE)) {
				if (cooldownTicks > 0) {
					if (cds.containsKey(mago.getUniqueId())) {
						int ticksObj = cds.get(mago.getUniqueId()) + cooldownTicks - (int) (cooldownTicks * cdr);
						if (ticksObj > ticks) {
							puede = false;
							if (avisar) {
								int espera = (int) ((ticksObj - ticks) / 20);
								if (!mensajes.containsKey(mago.getUniqueId())
										|| mensajes.get(mago.getUniqueId()) + cdMensajeCd <= ticks) {
									mago.sendMessage(MagiaPlugin.header + "Debes esperar " + plugin.accentColor + espera
											+ plugin.textColor + " segundos para volver a lanzar " + chatColor
											+ toString() + plugin.textColor + ".");
									mensajes.put(mago.getUniqueId(), ticks);
								}
							}
						}
					}
				}
			} else {
				puede = false;
				if (avisar)
					mago.sendMessage(MagiaPlugin.header + plugin.errorColor + "No puedes usar Magia.");
			}
			if (palabras != null && palabrasMagicas && puede) {
				if (!mensajesPalabrasMagicas.containsKey(mago.getUniqueId())
						|| mensajesPalabrasMagicas.get(mago.getUniqueId()) + cdMensajePalabrasMagicas <= ticks) {
					mago.chat(getPalabrasMagicas(mago.getCustomName() == null ? mago.getName() : mago.getCustomName()));
				}
				mensajesPalabrasMagicas.put(mago.getUniqueId(), ticks);
			}
			return puede;
		}

		public void Accionar(Player mago, @Nullable Entity victima, @Nullable Block bloque, Varita varita,
				TipoLanzamiento tipoLanzamiento, boolean ignorarPuede) {
			if (varita == null) {
				return;
			}
			if (ignorarPuede || puedeLanzar(mago, victima, varita, 0, !ignorarPuede, !ignorarPuede)) {
				int ticks = mago.getTicksLived();
				cds.put(mago.getUniqueId(), ticks);
				mensajes.put(mago.getUniqueId(), ticks);
				Accion(mago, victima, bloque, varita, tipoLanzamiento, varita.getPotencia(mago));
			}
		}

		@Override
		public String toString() {
			return nombre;
		}

		public static class TiposLanzamiento extends ArrayList<TipoLanzamiento> {
			private static final long serialVersionUID = -4465391248913994109L;

			public TiposLanzamiento(TipoLanzamiento... tipos) {
				super(tipos.length);
				for (TipoLanzamiento tipo : tipos) {
					add(tipo);
				}
			}
		}

		public static enum TipoLanzamiento {
			DISTANCIA_ENTIDAD, DISTANCIA_BLOQUE, GOLPE, AREA_MAGO
		}

		public static enum TipoProyectil {
			INVISIBLE, COHETE
		}
	}

	public static class VaritaListener implements Listener {

		@EventHandler
		private void onPlayerJoin(PlayerJoinEvent e) {
			Player p = e.getPlayer();
			p.discoverRecipe(keyReceta);
			if (!numerosMagicos.containsKey(p.getUniqueId())) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						Varita nueva = new Varita();
						nueva.jugador = p.getName();
						nueva.recagarDatos();
						p.getInventory().addItem(nueva);
						p.sendMessage(MagiaPlugin.header
								+ "Disfruta de tu primera varita. Usa /magia help para más información sobre tu varita y tú.");
						getOrGenerateNumero(p);
					}
				}, 20);
			}
		}

		@EventHandler
		private void onPlaceConVarita(BlockPlaceEvent e) {
			Player p = e.getPlayer();
			Varita varita = convertir(p.getInventory().getItemInMainHand());
			if (varita != null) {
				ItemStack otro = p.getInventory().getItemInOffHand();
				for (Conjuro c : Conjuro.values()) {
					if (c.getIngredientes().test(otro)) {
						e.setCancelled(true);
						break;
					}
				}
			}
		}

		@EventHandler
		private void onSwap(PlayerSwapHandItemsEvent e) {
			Varita varita = convertir(e.getOffHandItem());
			if (varita != null) {
				ItemStack otro = e.getMainHandItem();
				for (Conjuro c : Conjuro.values()) {
					if (c.getIngredientes().test(otro)) {
						if (!c.equals(varita.getConjuro())) {
							PlayerInventory pi = e.getPlayer().getInventory();
							varita.cambiarConjuro(c);
							otro.setAmount(otro.getAmount() - 1);
							pi.setItemInOffHand(otro);
							pi.setItemInMainHand(varita);
						}
						e.setCancelled(true);
						break;
					}
				}
			}
		}

		@EventHandler
		private void onPrepararCrafteo(PrepareItemCraftEvent e) {
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
				Varita v0 = convertir(ingrediente0);
				Varita v1 = convertir(ingrediente1);
				if (v0 == null && v1 != null) {
					v0 = new Varita(v1);
					ingrediente1 = ingrediente0;
				}
				if (v0 != null) {
					for (Conjuro c : Conjuro.values()) {
						if (c.getIngredientes().test(ingrediente1)) {
							Varita nueva = new Varita(v0);
							nueva.cambiarConjuro(c);
							e.getInventory().setResult(nueva);
							break;
						}
					}
				}
			} else if (e.getRecipe() == null) {
				if (arrayList.size() == 1) {
					Varita varitaIngrediente = convertir(arrayList.get(0));
					if (varitaIngrediente != null && varitaIngrediente.getConjuro() != null) {
						Varita varita2 = new Varita(varitaIngrediente);
						varita2.cambiarConjuro(null);
						e.getInventory().setResult(varita2);
					}
				}
			} else if (e.getRecipe().getResult() != null) {
				Varita varita = convertir(e.getRecipe().getResult());
				if (varita != null) {
					if (!e.getView().getPlayer().hasPermission(plugin.CREATE)) {
						ItemStack block = new ItemStack(Material.BARRIER);
						ItemMeta im = block.getItemMeta();
						im.setDisplayName("No puedes fabricar varitas");
						im.getPersistentDataContainer().set(new NamespacedKey(plugin, "blockcraft"),
								PersistentDataType.BYTE, Byte.MAX_VALUE);
						block.setItemMeta(im);
						e.getInventory().setResult(block);
					} else {
						e.getInventory().setResult(new Varita());
					}
				}
			}
		}

		@EventHandler
		private void onCrafteo(InventoryClickEvent e) {
			Inventory inventory = e.getInventory();
			if (inventory instanceof CraftInventoryCustom) {
				if (Varita.convertir(inventory.getItem(0)) != null) {
					e.setCancelled(true);
					return;
				}
			}

			if (e.getSlotType().equals(SlotType.RESULT) && e.getClickedInventory() instanceof CraftingInventory) {
				CraftingInventory inv = (CraftingInventory) e.getClickedInventory();
				ItemStack is = inv.getResult();
				if (is != null && is.hasItemMeta() && is.getItemMeta().getPersistentDataContainer()
						.has(new NamespacedKey(plugin, "blockcraft"), PersistentDataType.BYTE)) {
					e.setCancelled(true);
					return;
				}
				Varita varita = convertir(is);
				if (varita != null) {
					e.setCancelled(true);
					HumanEntity p = e.getView().getPlayer();
					PlayerInventory pi = p.getInventory();
					varita.jugador = p.getName();
					varita.recagarDatos();

					switch (e.getAction()) {
					case MOVE_TO_OTHER_INVENTORY:
						if (p.getItemOnCursor() != null) {
							if (pi.firstEmpty() < 0)
								p.getWorld().dropItem(p.getLocation(), varita);
							else {
								pi.addItem(varita);
							}
						}
						break;

					default:
						if (p.getItemOnCursor() != null) {
							if (pi.firstEmpty() < 0)
								p.getWorld().dropItem(p.getLocation(), p.getItemOnCursor());
							else {
								pi.addItem(p.getItemOnCursor());
							}
							p.setItemOnCursor(varita);
						}
						break;
					}

					ItemStack[] matriz = inv.getMatrix();
					for (ItemStack itemStack : matriz) {
						if (itemStack != null) {
							itemStack.setAmount(itemStack.getAmount() - 1);
							if (itemStack.getAmount() > 0) {
								if (pi.firstEmpty() < 0)
									p.getWorld().dropItem(p.getLocation(), itemStack);
								else
									pi.addItem(itemStack);
							}
						}
					}
					inv.clear();
				}
			}
		}

		@EventHandler
		private void onInteractEntity(PlayerInteractEntityEvent e) {
			Player p = e.getPlayer();
			Entity clicada = e.getRightClicked();
			ItemStack item = p.getInventory().getItemInMainHand();
			if (item != null) {
				Varita varita = convertir(item);
				if (varita != null)
					if (varita.getConjuro() != null)
						varita.getConjuro().Accionar(e.getPlayer(), clicada, null, varita,
								TipoLanzamiento.DISTANCIA_ENTIDAD, false);
			}
		}

		@EventHandler
		private void onInteract(PlayerInteractEvent e) {
			if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
					&& e.getItem() != null) {
				Varita varita = convertir(e.getItem());
				if (varita != null) {
					Player mago = e.getPlayer();
					if (varita.getConjuro() != null) {
						Conjuro c = varita.getConjuro();
						if (c.isTipoLanzamiento(TipoLanzamiento.DISTANCIA_BLOQUE)
								|| c.isTipoLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD)) {
							if (c.puedeLanzar(mago, null, varita, 0, true, true)) {
								c.ponerEnCD(mago);
								if (c.isTipoProyectil(TipoProyectil.INVISIBLE)
										|| c.isTipoProyectil(TipoProyectil.COHETE)) {
									Arrow rayo = mago.launchProjectile(Arrow.class);
									PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(
											rayo.getEntityId());
									for (Player pl : Bukkit.getOnlinePlayers())
										((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);

									rayo.setSilent(true);
									rayo.setGravity(false);
									rayo.setVelocity(rayo.getVelocity().normalize().multiply(10));
									rayo.setMetadata("jugadorAtacante",
											new FixedMetadataValue(plugin, mago.getUniqueId()));
									rayo.setMetadata("numeroMagicoVarita",
											new FixedMetadataValue(plugin, varita.getNumeroMagico()));
									rayo.setMetadata(c.getMetaNombre(), c.getMetaFlecha());
									if (c.isTipoProyectil(TipoProyectil.COHETE)) {
										rayo.setVelocity(rayo.getVelocity().normalize());
										int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
												new Runnable() {
													@Override
													public void run() {
														if (rayo.isValid()) {
															rayo.getLocation().getWorld().spawnParticle(
																	Particle.REDSTONE, rayo.getLocation(), 5, 0.1, 0.1,
																	0.1, new DustOptions(c.getColor(), 1));
														}
													}
												}, 0, 1);

										Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
											@Override
											public void run() {
												rayo.remove();
												if (id > 0)
													Bukkit.getScheduler().cancelTask(id);
											}
										}, 20);
									} else {
										Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

											@Override
											public void run() {
												rayo.remove();
											}
										}, 2);

									}
								}
							}
						}
						if (c.isTipoLanzamiento(TipoLanzamiento.AREA_MAGO)) {
							c.Accionar(mago, null, null, varita, TipoLanzamiento.AREA_MAGO, false);
						}
					} else {
						// Conjuro nulo
						BlockData datos = null;
						float potencia = varita.getPotencia(mago);
						Particle particula = varita.isHack() ? Particle.DRAGON_BREATH : Particle.FIREWORKS_SPARK;
						double variacion = 2 * potencia + 1;
						if (potencia < 0.1) {
							particula = Particle.BLOCK_DUST;
							datos = Material.COAL_BLOCK.createBlockData();
						} else if (potencia < 0.2) {
							particula = Particle.FALLING_WATER;
						} else if (potencia < 0.3) {
							particula = Particle.WATER_SPLASH;
						} else if (potencia < 0.4) {
							particula = Particle.DRIP_WATER;
						} else if (potencia < 0.5) {
							particula = Particle.WATER_SPLASH;
						} else if (potencia < 0.6) {
							particula = Particle.FALLING_LAVA;
						} else if (potencia < 0.7) {
							particula = Particle.FLAME;
						} else if (potencia < 0.8) {
							particula = Particle.DRIP_LAVA;
						} else if (potencia < 0.9) {
							particula = Particle.LAVA;
						}
						mago.getLocation().getWorld().spawnParticle(particula, mago.getEyeLocation(),
								(int) (200 * potencia), variacion, variacion, variacion, datos);
					}

				}
			}
		}

		@EventHandler
		private void onHitEntity(EntityDamageByEntityEvent e) {
			Entity atacado = e.getEntity();
			Entity atacante = e.getDamager();
			if (atacante instanceof Arrow) {
				for (Conjuro c : Conjuro.values())
					if (atacante.hasMetadata(c.getMetaNombre())) {
						UUID jug = UUID.fromString(atacante.getMetadata("jugadorAtacante").get(0).asString());
						Player p = Bukkit.getPlayer(jug);
						e.setCancelled(true);
						atacante.remove();
//						float numeroMagicoPlayer = getOrGenerateNumero(p);
//						c.Accionar(p, atacado, atacante.getMetadata("numeroMagicoVarita").get(0).asFloat(),
//								numeroMagicoPlayer);
						c.Accionar(p, atacado, null, convertir(p.getInventory().getItemInMainHand()),
								TipoLanzamiento.DISTANCIA_ENTIDAD, true);
						break;
					}
			} else if (atacante instanceof Player) {
				Player p = (Player) atacante;
				Varita varita = convertir(p.getInventory().getItemInMainHand());
				if (varita != null && varita.getConjuro() != null)
					for (Conjuro c : Conjuro.values())
						if (c.isTipoLanzamiento(TipoLanzamiento.GOLPE) && varita.getConjuro().equals(c)) {
							e.setCancelled(true);
							c.Accionar(p, atacado, null, varita, TipoLanzamiento.GOLPE, false);
						}
			}
		}

		@EventHandler
		private static void onHit(ProjectileHitEvent e) {
			Projectile proyectil = e.getEntity();
			if (proyectil instanceof Arrow) {
				for (Conjuro c : Conjuro.values()) {
					if (proyectil.hasMetadata(c.getMetaNombre())) {
						proyectil.remove();
						UUID jug = UUID.fromString(proyectil.getMetadata("jugadorAtacante").get(0).asString());
						Player p = Bukkit.getPlayer(jug);
						c.Accionar(p, null, e.getHitBlock(), convertir(p.getInventory().getItemInMainHand()),
								TipoLanzamiento.DISTANCIA_BLOQUE, true);
						break;
					}
				}
			}
		}
	}
}