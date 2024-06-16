package es.cristichi.magiaborras.obj.varita;

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
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.joml.Math;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.varita.material.Flexibilidad;
import es.cristichi.magiaborras.obj.varita.material.Longitud;
import es.cristichi.magiaborras.obj.varita.material.Madera;
import es.cristichi.magiaborras.obj.varita.material.Nucleo;

public class Varita extends ItemStack {
	static HashMap<UUID, Float> numerosMagicos;

	static ShapedRecipe receta;
	static NamespacedKey keyReceta;

	static MagiaPlugin plugin;
	static NamespacedKey keyJugador;
	static NamespacedKey keyNumeroMagico;
	static NamespacedKey keyNucleo;
	static NamespacedKey keyMadera;
	static NamespacedKey keyFlexibilidad;
	static NamespacedKey keyLongitud;
	static NamespacedKey keyConjuro;
	static NamespacedKey keyHack;

	public static void Init(MagiaPlugin plugin) {
		if (plugin == null || plugin.USE == null) {
			throw new NullPointerException("Debes poner un plugin para iniciar la clase Varita.");
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

		Varita result = new Varita();
		ItemMeta im = result.getItemMeta();
		im.setDisplayName(ChatColor.RESET + "Varita Mágica");
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + "Varita mágica para lanzar hechizos");
		im.setLore(lore);
		im.addEnchant(Enchantment.MENDING, 1, true);
		result.setItemMeta(im);

		keyReceta = new NamespacedKey(plugin, "crafteoVarita");
		receta = new ShapedRecipe(keyReceta, result).shape(" T ", "DPD", "WWW").setIngredient('P', Material.STICK)
				.setIngredient('T', Material.TORCH).setIngredient('D', Material.DIAMOND).setIngredient('W',
						new RecipeChoice.MaterialChoice(Material.OAK_LOG, Material.ACACIA_LOG, Material.BIRCH_LOG,
								Material.CHERRY_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.MANGROVE_LOG,
								Material.SPRUCE_LOG));
		plugin.getServer().addRecipe(receta);

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.discoverRecipe(keyReceta);
		}

		Varita.numerosMagicos = new HashMap<>();

		plugin.getServer().getPluginManager().registerEvents(new VaritaListener(), plugin);
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
			} catch (NullPointerException e) {
				plugin.getLogger().log(Level.SEVERE,
						"It looks like Magic numbers could not be saved because something is null (Plugin did not start correctly?).",
						e);
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

	/**
	 * Comprueba si este ItemStack es una varita (o varias, yo qué sé)
	 * 
	 * @param  posibleVarita
	 * @return               Varita si el ItemStack es una varita correcta, null en otro caso
	 */
	public static Varita esItemStackUnaVarita(ItemStack posibleVarita) {
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
						conjuro == null ? null : Conjuro.getConjuro(conjuro), hack);
				// return new Varita(numeroMagico, Nucleo.valueOf(nucleo), Madera.valueOf(madera),
				// Flexibilidad.valueOf(flexibilidad), Longitud.valueOf(longitud),
				// conjuro == null ? null : Conjuro.valueOf(conjuro), hack);
			}
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.WARNING,
					"La varita no es válida. Es posible que tras una actualización se haya jodido la varita o algo.",
					e);
		}
		return null;
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
		this(null, null, null, null, null, null, null, null, false);
	}

	public Varita(Varita otra) {
		this(otra.jugador, null, otra.numeroMagico, otra.nucleo, otra.madera, otra.flexibilidad, otra.longitud,
				otra.conjuro, otra.hack);
	}

	public Varita(String jugador, Long seed, Float numeroMagico, Nucleo nucleo, Madera madera,
			Flexibilidad flexibilidad, Longitud longitud, Conjuro conjuro, boolean hack) {
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
		lore.add(ChatColor.GRAY + "");
		if (isHack()) {
			lore.add(ChatColor.GRAY + "La varita muestra extrañas runas grabadas a su alrededor.");
			lore.add(ChatColor.DARK_PURPLE.toString() + ChatColor.MAGIC.toString() + getNumeroMagico());
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
			im.getPersistentDataContainer().set(keyConjuro, PersistentDataType.STRING, conjuro.getId());
		setItemMeta(im);
	}

	public String getJugador() {
		return jugador;
	}

	public void setJugador(String jugador) {
		this.jugador = jugador;
	}

	/**
	 * 
	 * @param mago Cada varita tiene una potencia distinta según el mago que la empuña.
	 * @return Un valor entre 0 y 1, donde 0 es la peor potencia y 1 es una potencia perfecta.
	 */
	public float getPotencia(Player mago) {
		float pot = 1 - Math.abs(getOrGenerateNumero(mago) - getNumeroMagico());
		return Math.max(0, pot);
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

	public void setNumeroMagico(float numeroMagico) {
		this.numeroMagico = numeroMagico;
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

	
}