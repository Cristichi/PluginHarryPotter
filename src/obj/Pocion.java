package obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

public abstract class Pocion extends ItemStack {
	protected static NamespacedKey keyPocion;

	private static HashMap<String, Pocion> pociones = new HashMap<>(3);

	public static void Init(Plugin plugin) {
		keyPocion = new NamespacedKey(plugin, "pocion");
		pociones.put("solitario", new Pocion("Poción del solitario", Color.MAROON, null, "Una voz en tu cabeza",
				"te saluda cuando la bebes") {
			@Override
			public void Accion(PlayerItemConsumeEvent e) {
				e.getPlayer().sendMessage("¡Hola!");
			}
		});

		pociones.put("invisibilidad", new Pocion("Poción de Invisibilidad", Color.LIME,
				new PotionEffect(PotionEffectType.INVISIBILITY, 240, 0)) {
			@Override
			public void Accion(PlayerItemConsumeEvent e) {
			}
		});

		pociones.put("cerveza_de_mantequilla", new Pocion("Cerveza de Mantequilla", Color.ORANGE, null) {
			@Override
			public void Accion(PlayerItemConsumeEvent e) {
				Player p = e.getPlayer();
				p.setFoodLevel(p.getFoodLevel() + 5);
			}
		});

		pociones.put("muertos_en_vida", new Pocion("Filtro de Muertos en Vida", Color.LIME, null, "Para enemigos") {
			@Override
			public void Accion(PlayerItemConsumeEvent e) {
				BukkitScheduler scheduler = Bukkit.getScheduler();
				Player p = e.getPlayer();
				Location loc = p.getLocation();
				int id = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {

					@Override
					public void run() {
						p.teleport(loc);
					}
				}, 10, 1);
				scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {

					@Override
					public void run() {
						scheduler.cancelTask(id);
					}
				}, 60);
			}
		});

		plugin.getServer().getPluginManager().registerEvents(new Pocion.PocionListener(), plugin);
	}

	public static Pocion getInvisibilidad() {
		return pociones.get("invisibilidad");
	}

	public static Pocion getSolitario() {
		return pociones.get("solitario");
	}

	public static Pocion getCervezaDeMantequilla() {
		return pociones.get("cerveza_de_mantequilla");
	}

	public static Pocion get(String nombre) {
		return pociones.get(nombre);
	}

	static class PocionListener implements Listener {
		@EventHandler
		private static void alBeber(PlayerItemConsumeEvent e) {
			if (e.getItem().hasItemMeta()) {
				String nombre = e.getItem().getItemMeta().getPersistentDataContainer().get(keyPocion,
						PersistentDataType.STRING);
				pociones.forEach(new BiConsumer<String, Pocion>() {
					@Override
					public void accept(String t, Pocion u) {
						if (u.getNombre().equals(nombre)) {
							u.Accion(e);
						}
					}
				});
			}
		}
	}

	// Clase
	private String nombre;
	private Color color;

	private Pocion(String nombre, Color color, @Nullable PotionEffect effecto, String... lore) {
		super(Material.POTION);
		this.nombre = ChatColor.RESET + nombre;
		this.color = color;

		PotionMeta meta = (PotionMeta) getItemMeta();
		meta.setColor(color);
		meta.setDisplayName(nombre);
		if (lore != null && lore.length > 0) {
			ArrayList<String> loreList = new ArrayList<>(lore.length);
			for (String string : lore) {
				loreList.add(string);
			}
			meta.setLore(loreList);
		}
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		if (effecto != null) {
			meta.addCustomEffect(effecto, true);
		}
		meta.getPersistentDataContainer().set(keyPocion, PersistentDataType.STRING, nombre);
		setItemMeta(meta);
	}

	public abstract void Accion(PlayerItemConsumeEvent e);

	@Override
	public void setType(Material type) {
		super.setType(Material.POTION);
	}

	@Override
	public Material getType() {
		return Material.POTION;
	}

	public String getNombre() {
		return nombre;
	}

	public Color getColor() {
		return color;
	}

	public PotionMeta getPotionMeta() {
		return (PotionMeta) super.getItemMeta();
	}
}
