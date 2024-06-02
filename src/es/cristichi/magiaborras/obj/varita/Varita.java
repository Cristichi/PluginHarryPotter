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
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
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
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.joml.Math;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.varita.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.varita.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.varita.conjuro.TipoProyectil;
import es.cristichi.magiaborras.obj.varita.prop.Flexibilidad;
import es.cristichi.magiaborras.obj.varita.prop.Longitud;
import es.cristichi.magiaborras.obj.varita.prop.Madera;
import es.cristichi.magiaborras.obj.varita.prop.Nucleo;
import es.cristichi.magiaborras.util.Targeter;

public class Varita extends ItemStack {
	private static HashMap<UUID, Float> numerosMagicos;

	protected static ShapedRecipe receta;
	protected static NamespacedKey keyReceta;

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
		receta = new ShapedRecipe(keyReceta, result).shape("FGS", "BPB", "ERE")
				.setIngredient('F', Material.FERMENTED_SPIDER_EYE).setIngredient('G', Material.GHAST_TEAR)
				.setIngredient('S', Material.SPIDER_EYE).setIngredient('B', Material.WRITABLE_BOOK)
				.setIngredient('P', Material.STICK).setIngredient('E', Material.ENDER_EYE)
				.setIngredient('R', Material.FIREWORK_ROCKET);
		plugin.getServer().addRecipe(receta);

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
			} catch (NullPointerException e) {
				plugin.getLogger().log(Level.INFO,
						"It looks like Magic numbers could not be saved (Plugin did not start correctly?).");
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
			System.err.println("La varita no es válida, ¿ha cambiado algo en alguna actualización del plugin?");
			System.err.println(e.getMessage());
			e.printStackTrace();
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

	public static class VaritaListener implements Listener {

		@EventHandler
		private void onPlayerJoin(PlayerJoinEvent e) {
			Player p = e.getPlayer();
			p.discoverRecipe(keyReceta);
			if (!numerosMagicos.containsKey(p.getUniqueId())) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						Varita nueva = null;

						Varita nueva1 = new Varita();
						nueva1.jugador = p.getName();
						nueva1.recagarDatos();

						Varita nueva2 = new Varita();
						nueva2.jugador = p.getName();
						nueva2.recagarDatos();

						nueva = nueva1.getPotencia(p) > nueva2.getPotencia(p) ? nueva1 : nueva2;
						p.getInventory().addItem(nueva);
						p.sendMessage(MagiaPlugin.header
								+ "Disfruta de tu primera varita. Usa /magia help para más información sobre tu varita y tú.");
					}
				}, 20);
			}
		}

		@EventHandler
		private void onPlaceConVarita(BlockPlaceEvent e) {
			Player p = e.getPlayer();
			Varita varita = esItemStackUnaVarita(p.getInventory().getItemInMainHand());
			if (varita != null) {
				ItemStack otro = p.getInventory().getItemInOffHand();
				for (Conjuro c : Conjuro.getConjuros()) {
					if (c.getIngredientes().test(otro)) {
						e.setCancelled(true);
						break;
					}
				}
			}
		}

		@EventHandler
		private void onSwap(PlayerSwapHandItemsEvent e) {
			Varita varita = esItemStackUnaVarita(e.getOffHandItem());
			if (varita != null) {
				ItemStack otro = e.getMainHandItem();
				for (Conjuro c : Conjuro.getConjuros()) {
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
				Varita v0 = esItemStackUnaVarita(ingrediente0);
				Varita v1 = esItemStackUnaVarita(ingrediente1);
				if (v0 == null && v1 != null) {
					v0 = new Varita(v1);
					ingrediente1 = ingrediente0;
				}
				if (v0 != null) {
					for (Conjuro c : Conjuro.getConjuros()) {
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
					Varita varitaIngrediente = esItemStackUnaVarita(arrayList.get(0));
					if (varitaIngrediente != null && varitaIngrediente.getConjuro() != null) {
						Varita varita2 = new Varita(varitaIngrediente);
						varita2.cambiarConjuro(null);
						e.getInventory().setResult(varita2);
					}
				}
			} else if (e.getRecipe().getResult() != null) {
				Varita varita = esItemStackUnaVarita(e.getRecipe().getResult());
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
		private void onInventoryClick(InventoryClickEvent e) {
			Inventory inventory = e.getClickedInventory();

			// Recetas no stealing
			if (inventory.getType().equals(InventoryType.WORKBENCH)) {
				if (Varita.esItemStackUnaVarita(inventory.getItem(0)) != null) {
					e.setCancelled(true);
					return;
				}
			}

			// When menu conjuros
			if (inventory.getType().equals(InventoryType.CHEST) && e.getCurrentItem() != null) {

				e.setCancelled(true);
				e.getView().getTitle().equals(plugin.invMenuName);

				HumanEntity mago = e.getWhoClicked();
				Varita varita = esItemStackUnaVarita(mago.getInventory().getItemInMainHand());
				if (varita == null) {
					mago.sendMessage(MagiaPlugin.header + "Ponte la varita en la mano anda, ains!");
				} else {
					PlayerInventory pi = mago.getInventory();

					boolean done = false;
					for (Conjuro c : Conjuro.getConjuros()) {
						if (c.getIngredientes().test(e.getCurrentItem())) {
							if (!c.equals(varita.getConjuro())) {
								varita.cambiarConjuro(c);
								pi.setItemInMainHand(varita);
							}
							done = true;
							break;
						}
					}
					if (!done && varita.getConjuro() != null) {
						varita.cambiarConjuro(null);
						pi.setItemInMainHand(varita);
					}
					mago.closeInventory();
				}
			}

			// When clicking to get the craft result
			if (e.getSlotType().equals(SlotType.RESULT)
					&& e.getClickedInventory().getType().equals(InventoryType.WORKBENCH)) {
				CraftingInventory inv = (CraftingInventory) e.getClickedInventory();
				ItemStack is = inv.getResult();
				if (is != null && is.hasItemMeta() && is.getItemMeta().getPersistentDataContainer()
						.has(new NamespacedKey(plugin, "blockcraft"), PersistentDataType.BYTE)) {
					e.setCancelled(true);
					return;
				}
				Varita varita = esItemStackUnaVarita(is);
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
				Varita varita = esItemStackUnaVarita(item);
				if (varita != null)
					if (varita.getConjuro() != null)
						varita.getConjuro().Accionar(plugin, e.getPlayer(), clicada, null, varita,
								TipoLanzamiento.DISTANCIA_ENTIDAD, false);
			}
		}

		@EventHandler
		private void onInteract(PlayerInteractEvent e) {
			if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
					&& e.getItem() != null) {
				Varita varita = esItemStackUnaVarita(e.getItem());
				if (varita != null) {
					Player mago = e.getPlayer();
					if (varita.getConjuro() != null) {
						Conjuro c = varita.getConjuro();
						if (c.isTipoLanzamiento(TipoLanzamiento.DISTANCIA_BLOQUE)
								|| c.isTipoLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD)) {
							if (c.puedeLanzar(plugin, mago, null, varita, 0, true, true)) {
								// c.ponerEnCD(mago);
								if (c.isTipoProyectil(TipoProyectil.INVISIBLE)
										|| c.isTipoProyectil(TipoProyectil.COHETE)) {

									Entity target = Targeter.getTargetEntity(mago);
									if (target != null) {
										c.Accionar(plugin, mago, target, null,
												esItemStackUnaVarita(mago.getInventory().getItemInMainHand()),
												TipoLanzamiento.DISTANCIA_ENTIDAD, true);
										c.ponerEnCD(mago);
									}

									// Arrow rayo = mago.launchProjectile(Arrow.class);
									// PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(
									// rayo.getEntityId());
									// Reflection.sendPacket(mago, packet);
									// for (Player pl : Bukkit.getOnlinePlayers()) {
									// Reflection.sendPacket(pl, packet);
									// }
									//
									// rayo.setSilent(true);
									// rayo.setGravity(false);
									// rayo.setVelocity(rayo.getVelocity().normalize().multiply(10));
									// rayo.setMetadata("jugadorAtacante",
									// new FixedMetadataValue(plugin, mago.getUniqueId()));
									// rayo.setMetadata("numeroMagicoVarita",
									// new FixedMetadataValue(plugin, varita.getNumeroMagico()));
									// rayo.setMetadata(c.getMetaNombre(), c.getMetaFlecha());
									// if (c.isTipoProyectil(TipoProyectil.COHETE)) {
									// rayo.setVelocity(rayo.getVelocity().normalize());
									// int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
									// new Runnable() {
									// @Override
									// public void run() {
									// if (rayo.isValid()) {
									// rayo.getLocation().getWorld().spawnParticle(
									// Particle.REDSTONE, rayo.getLocation(), 5, 0.1, 0.1,
									// 0.1, new DustOptions(c.getColor(), 1));
									// }
									// }
									// }, 0, 1);
									//
									// Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
									// @Override
									// public void run() {
									// rayo.remove();
									// if (id > 0)
									// Bukkit.getScheduler().cancelTask(id);
									// }
									// }, 20);
									// } else {
									// Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
									//
									// @Override
									// public void run() {
									// rayo.remove();
									// }
									// }, 2);
									//
									// }
								}
							}
						}
						if (c.isTipoLanzamiento(TipoLanzamiento.AREA_MAGO)) {
							c.Accionar(plugin, mago, null, null, varita, TipoLanzamiento.AREA_MAGO, false);
							c.ponerEnCD(mago);
						}
					} else {
						// Conjuro nulo
						BlockData datosBloque = null;
						float potencia = varita.getPotencia(mago);
						Particle particula = null;
						double variacion = 2 * potencia + 1;
						variacion = 1;
						int count = (int) (200 * potencia);
						count = 100;
						if (potencia < 0.1) {
							particula = Particle.ASH;
						} else if (potencia < 0.2) {
							particula = Particle.FALLING_WATER;
						} else if (potencia < 0.3) {
							particula = Particle.DRIPPING_WATER;
						} else if (potencia < 0.4) {
							particula = Particle.UNDERWATER;
						} else if (potencia < 0.5) {
							particula = Particle.FALLING_LAVA;
						} else if (potencia < 0.6) {
							particula = Particle.DRIPPING_LAVA;
						} else if (potencia < 0.7) {
							particula = Particle.FLAME;
						} else if (potencia < 0.8) {
							particula = Particle.EGG_CRACK;
						} else if (potencia < 0.9) {
							particula = Particle.FIREWORK;
						} else if (potencia < 1) {
							particula = Particle.CHERRY_LEAVES;
							count = 200;
						} else {
							particula = Particle.DRAGON_BREATH;
							count = 500;
						}
						// mago.sendMessage("DEBUG: Tu potencia: "+potencia);
						mago.getLocation().getWorld().spawnParticle(particula, mago.getEyeLocation(), count, variacion,
								variacion, variacion, datosBloque);
					}

				}
			}
		}

		@EventHandler
		private void onHitEntity(EntityDamageByEntityEvent e) {
			Entity atacado = e.getEntity();
			Entity atacante = e.getDamager();
			if (atacante instanceof Arrow) {
				for (Conjuro c : Conjuro.getConjuros())
					if (atacante.hasMetadata(c.getMetaNombre())) {
						UUID jug = UUID.fromString(atacante.getMetadata("jugadorAtacante").get(0).asString());
						Player mago = Bukkit.getPlayer(jug);
						e.setCancelled(true);
						atacante.remove();
						// float numeroMagicoPlayer = getOrGenerateNumero(p);
						// c.Accionar(p, atacado, atacante.getMetadata("numeroMagicoVarita").get(0).asFloat(),
						// numeroMagicoPlayer);
						c.Accionar(plugin, mago, atacado, null,
								esItemStackUnaVarita(mago.getInventory().getItemInMainHand()),
								TipoLanzamiento.DISTANCIA_ENTIDAD, true);
						break;
					}
			} else if (atacante instanceof Player) {
				Player mago = (Player) atacante;
				Varita varita = esItemStackUnaVarita(mago.getInventory().getItemInMainHand());
				if (varita != null && varita.getConjuro() != null)
					for (Conjuro c : Conjuro.getConjuros())
						if (c.isTipoLanzamiento(TipoLanzamiento.GOLPE) && varita.getConjuro().equals(c)) {
							e.setCancelled(true);
							c.Accionar(plugin, mago, atacado, null, varita, TipoLanzamiento.GOLPE, false);
						}
			}
		}

		@EventHandler
		private static void onHit(ProjectileHitEvent e) {
			Projectile proyectil = e.getEntity();
			if (proyectil instanceof Arrow) {
				for (Conjuro c : Conjuro.getConjuros()) {
					if (proyectil.hasMetadata(c.getMetaNombre())) {
						proyectil.remove();
						UUID jug = UUID.fromString(proyectil.getMetadata("jugadorAtacante").get(0).asString());
						Player mago = Bukkit.getPlayer(jug);
						c.Accionar(plugin, mago, null, e.getHitBlock(),
								esItemStackUnaVarita(mago.getInventory().getItemInMainHand()),
								TipoLanzamiento.DISTANCIA_BLOQUE, true);
						break;
					}
				}
			}
		}
	}
}