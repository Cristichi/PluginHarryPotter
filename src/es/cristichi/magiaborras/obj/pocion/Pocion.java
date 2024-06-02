package es.cristichi.magiaborras.obj.pocion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import org.bukkit.util.Vector;

public abstract class Pocion extends ItemStack {
	protected static NamespacedKey keyPocion;

	private static HashMap<String, Pocion> pociones = new HashMap<>();

	public static void Init(Plugin plugin) {
		keyPocion = new NamespacedKey(plugin, "pocion");

		pociones.put("felix_felicis", new Pocion("Felix Felicis", Color.ORANGE, null, "Suerte Líquida", "Quizás no está del todo bien...") {
			@Override
			public void Accion(PlayerItemConsumeEvent e) {
				Player p = e.getPlayer();
				switch ((int)(Math.random()*10)) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
					p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60000, 0));
					break;
				case 5:
				case 6:
				case 7:
				case 8:
					p.getInventory().addItem(new ItemStack(Material.GOLD_BLOCK, 2));
					p.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 6000, 0));
					break;
					
				case 9:
					p.getInventory().addItem(new ItemStack(Material.DIAMOND));
					break;

				default:
					break;
				}
			}
		});
		
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
				p.setFoodLevel(p.getFoodLevel() + 11);
			}
		});

		pociones.put("paralizadora", new Pocion("Poción de Parálisis", Color.LIME, null) {
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
				}, 240);
			}
		});

		pociones.put("levitadora", new Pocion("Poción de Aligerar", Color.LIME, new PotionEffect(PotionEffectType.LEVITATION, 150, 0)) {
			@Override
			public void Accion(PlayerItemConsumeEvent e) {
				Player p = e.getPlayer();
				p.setVelocity(p.getVelocity().add(new Vector(0, 0.5, 0)));
			}
		});

		plugin.getServer().getPluginManager().registerEvents(new Pocion.PocionListener(), plugin);
	}
	
	public static HashMap<String, Pocion> getPociones() {
		return pociones;
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

	public static Pocion getParalizadora() {
		return pociones.get("paralizadora");
	}

	public static Pocion getLevitadora() {
		return pociones.get("levitadora");
	}

	public static Pocion getFelixFelicis() {
		return pociones.get("felix_felicis");
	}

	public static Pocion get(String nombre) {
		return pociones.get(nombre.replace(" ", "_"));
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

	private Pocion(String nombre, Color color, PotionEffect effecto, String... lore) {
		super(Material.POTION);
		nombre = ChatColor.RESET + nombre;
		this.nombre = nombre;
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
