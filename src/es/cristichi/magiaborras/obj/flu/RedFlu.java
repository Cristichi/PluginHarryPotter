package es.cristichi.magiaborras.obj.flu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import es.cristichi.magiaborras.main.MagiaPlugin;

public class RedFlu implements Listener {
	public static List<Material> COSA_VERDE = Arrays.asList(Material.GREEN_BANNER);
	public static List<Material> FUEGO = Arrays.asList(Material.SOUL_CAMPFIRE, Material.CAMPFIRE);
	public static List<Material> ALFOMBRA = Arrays.asList(Material.GREEN_CARPET, Material.RED_CARPET);
	public static List<Material> POLVOS = Arrays.asList(Material.GREEN_DYE);
	public static HashMap<String, ChimeneaFlu> RED_FLU = new HashMap<>(2);

	public static ChimeneaFlu getChimeneaFlu(String displayName) {
		return RED_FLU.get(displayName);
	}

	@EventHandler
	private void onCosa(EntityDamageByBlockEvent event) {
		Entity afectado = event.getEntity();
		if (afectado instanceof Player) {
			Block lego = event.getDamager();
			if (FUEGO.contains(lego.getType())) {
				Block lego2 = lego.getWorld().getBlockAt(lego.getLocation().clone().add(0, 1, 0));
				if (COSA_VERDE.contains(lego2.getType())) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onClickVerde(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Player mago = e.getPlayer();
			Block lego = e.getClickedBlock();
			try {
				Block legoArriba = lego.getWorld().getBlockAt(lego.getLocation().clone().add(0, 1, 0));
				Block legoAbajo = lego.getWorld().getBlockAt(lego.getLocation().clone().add(0, -1, 0));
				if (lego != null && (FUEGO.contains(lego.getType()) && COSA_VERDE.contains(legoArriba.getType()))
						|| (COSA_VERDE.contains(lego.getType()) && FUEGO.contains(legoAbajo.getType()))) {

					ItemStack mano = mago.getInventory().getItemInMainHand();
					if (POLVOS.contains(mano.getType())) {
						InventoryView iv = mago.getOpenInventory();
						if (iv.getType() == InventoryType.CRAFTING) {
							ArrayList<ItemStack> items = new ArrayList<>(RED_FLU.size() - 1);
							for (ChimeneaFlu fuego : RED_FLU.values()) {
								if (!fuego.getLoc().getBlock().getLocation().equals(lego.getLocation())
										&& !fuego.getLoc().getBlock().getLocation().equals(legoAbajo.getLocation())) {
									Block b = fuego.getLoc().clone().add(0, -1, 0).getBlock();
									items.add(MenuRedFlu.createGuiItem(b.getType(), fuego.getNombre(),
											b.getLocation().toString()));
								}
							}
							MenuRedFlu menu = new MenuRedFlu(items);
							menu.openInventory(mago);
						}
					} else {
						mago.sendMessage(MagiaPlugin.header + "Necesitas tener " + MagiaPlugin.accentColor
								+ POLVOS.get(0) + MagiaPlugin.mainColor + " en la mano para usar la Red Flu.");
					}
					e.setCancelled(true);

				}

			} catch (NullPointerException nullExc) {
				// If there are nullpointerexceptions, bro just do nothing ofc
				nullExc.printStackTrace();
			}

		}

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player mago = e.getPlayer();
		Block lego = e.getBlock();
		try {
			Block legoArriba = lego.getWorld().getBlockAt(lego.getLocation().clone().add(0, 1, 0));
			Block legoAbajo = lego.getWorld().getBlockAt(lego.getLocation().clone().add(0, -1, 0));
			if (lego != null) {
				boolean ok = false;
				Location loc = null;
				if (FUEGO.contains(lego.getType()) && COSA_VERDE.contains(legoArriba.getType())) {
					loc = lego.getLocation();
					ok = true;
				} else if (COSA_VERDE.contains(lego.getType()) && FUEGO.contains(legoAbajo.getType())) {
					loc = legoAbajo.getLocation();
					ok = true;
				}
				if (ok) {
					for (Map.Entry<String, ChimeneaFlu> entrada : RED_FLU.entrySet()) {
						if (entrada.getValue().getLoc().equals(loc)) {
							RED_FLU.remove(entrada.getKey());
							mago.sendMessage("");
							mago.sendMessage(MagiaPlugin.header + "Has roto tu chimenea y ha salido de la "
									+ ChatColor.GREEN + "Red Flu" + MagiaPlugin.mainColor + ".");
						}
					}
				}
			}

		} catch (NullPointerException nullExc) {
			// If there are nullpointerexceptions, bro just do nothing ofc
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player mago = e.getPlayer();
		Block lego = e.getBlock();
		try {
			Block legoArriba = lego.getWorld().getBlockAt(lego.getLocation().clone().add(0, 1, 0));
			Block legoAbajo = lego.getWorld().getBlockAt(lego.getLocation().clone().add(0, -1, 0));
			if (lego != null) {
				boolean ok = false;
				Location loc = null;
				if (FUEGO.contains(lego.getType()) && COSA_VERDE.contains(legoArriba.getType())) {
					loc = lego.getLocation();
					ok = true;
				} else if (COSA_VERDE.contains(lego.getType()) && FUEGO.contains(legoAbajo.getType())) {
					loc = legoAbajo.getLocation();
					ok = true;
				}
				if (ok) {
					loc = loc.clone().add(0.5, 0, 0.5);
					String nombre = "Chimenea de " + mago.getName();
					String nombreOG = new String(nombre);
					for (int cont = 2; RED_FLU.containsKey(nombre); cont++) {
						nombre = nombreOG + " " + cont;
					}
					RED_FLU.put(nombre, new ChimeneaFlu(loc, nombre, mago.getName()));
					mago.sendMessage("");
					mago.sendMessage(MagiaPlugin.header + "Has registrado tu chimenea en la " + ChatColor.GREEN
							+ "Red Flu" + MagiaPlugin.mainColor + ".");
					mago.sendMessage(MagiaPlugin.header
							+ "Cambia el bloque de debajo de la hoguera para personalizar el icono para tu entrada.");
					mago.sendMessage(MagiaPlugin.header
							+ "Además, pon alfombra verde al lado de la hoguera (a la misma altura) para indicar por dónde sale la gente que venga a tu hoguera.");
				}
			}

		} catch (NullPointerException nullExc) {
			// If there are nullpointerexceptions, bro just do nothing ofc
		}
	}
}
