package es.cristichi.magiaborras.obj.flu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustTransition;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.joml.Math;

import es.cristichi.magiaborras.main.MagiaPlugin;

public class MenuRedFlu implements Listener {
	private static Plugin plugin;

	public static void init(Plugin plugin) {
		MenuRedFlu.plugin = plugin;
	}

	private final Inventory inv;

	public MenuRedFlu(List<ItemStack> items) {
		int num = Math.max(9, items.size());
		while (num % 9 != 0) {
			num++;
		}
		inv = Bukkit.createInventory(null, num, "Red Flu");

		addItems(items.toArray(new ItemStack[] {}));

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * ¡Usa {@link #createGuiItem(Material, String, String...)}!
	 * 
	 * @param items
	 */
	private void addItems(ItemStack... items) {
		for (ItemStack item : items) {
			inv.addItem(item);
		}
	}

	/**
	 * Simplemente crea un org.bukkit.inventory.ItemStack con los datos que pongas.
	 * 
	 * @param  material
	 * @param  name
	 * @param  lore
	 * @return
	 */
	public static ItemStack createGuiItem(Material material, String name, String... lore) {
		if (material.equals(Material.AIR)) {
			material = Material.BUCKET;
		}
		final ItemStack item = new ItemStack(material, 1);
		final ItemMeta meta = item.getItemMeta();

		// Set the name of the item
		meta.setDisplayName(name);

		// Set the lore of the item
		meta.setLore(Arrays.asList(lore));

		item.setItemMeta(meta);

		return item;
	}

	public void openInventory(final HumanEntity ent) {
		ent.openInventory(inv);
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent e) {
		if (!e.getInventory().equals(inv))
			return;

		e.setCancelled(true);

		final ItemStack clickedItem = e.getCurrentItem();

		// verify current item is not null
		if (clickedItem == null || clickedItem.getType().isAir() || !inv.contains(clickedItem))
			return;

		final Player mago = (Player) e.getWhoClicked();

		ItemStack mano = mago.getInventory().getItemInMainHand();
		if (RedFlu.POLVOS.contains(mano.getType())) {
			mago.closeInventory();
				mago.sendMessage(MagiaPlugin.header+"Buscando el nombre a "+clickedItem.getItemMeta().getDisplayName());
				Location tp = RedFlu.getChimeneaFlu(clickedItem.getItemMeta().getDisplayName()).getLoc();
				Location tp1x = tp.clone().add(1, 0, 0);
				Location tp1z = tp.clone().add(0, 0, 1);
				Location tpm1x = tp.clone().add(-1, 0, 0);
				Location tpm1z = tp.clone().add(0, 0, -1);
				if (RedFlu.ALFOMBRA.contains(tp1x.getBlock().getType())) {
					tp = tp1x;
					tp.setDirection(new Vector(1, 0, 0));
				} else if (RedFlu.ALFOMBRA.contains(tp1z.getBlock().getType())) {
					tp = tp1z;
					tp.setDirection(new Vector(0, 0, 1));
				} else if (RedFlu.ALFOMBRA.contains(tpm1x.getBlock().getType())) {
					tp = tpm1x;
					tp.setDirection(new Vector(-1, 0, 0));
				} else if (RedFlu.ALFOMBRA.contains(tpm1z.getBlock().getType())) {
					tp = tpm1z;
					tp.setDirection(new Vector(0, 0, -1));
				}
				
				final Location finalTp = tp.clone().add(0, 1, 0);
				
				final DustTransition dustTransition = new DustTransition(Color.fromRGB(0, 255, 0), Color.fromRGB(255, 255, 255),
						3F);
				mago.spawnParticle(Particle.DUST_COLOR_TRANSITION, finalTp, 5000, 1, 1, 1, dustTransition);
				
				mano.setAmount(mano.getAmount() - 1);
				mago.getInventory().setItemInMainHand(mano);
				
				mago.spawnParticle(Particle.DUST_COLOR_TRANSITION, mago.getLocation(), 5000, 1, 1, 1, dustTransition);

			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

				@Override
				public void run() {
					mago.spawnParticle(Particle.DUST_COLOR_TRANSITION, finalTp, 5000, 1, 1, 1, dustTransition);
					mago.teleport(finalTp, TeleportCause.PLUGIN);
					mago.playSound(finalTp, mago.getFallDamageSoundBig(), 1f, 1f);
				}
			}, 20);
		}

	}

	@EventHandler
	public void onInventoryClick(final InventoryDragEvent e) {
		if (e.getInventory().equals(inv)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryClick(final InventoryMoveItemEvent e) {
		if (e.getInitiator().equals(inv)) {
			e.setCancelled(true);
		}
	}
}