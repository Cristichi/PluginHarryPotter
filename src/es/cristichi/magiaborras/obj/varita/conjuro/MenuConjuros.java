package es.cristichi.magiaborras.obj.varita.conjuro;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.joml.Math;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.varita.Varita;

public class MenuConjuros implements Listener {
	private static MagiaPlugin plugin;

	public static void init(MagiaPlugin plugin) {
		MenuConjuros.plugin = plugin;
	}

	private final Inventory inv;

	public MenuConjuros(List<ItemStack> items) {
		int num = Math.max(9, items.size());
		while (num % 9 != 0) {
			num++;
		}
		inv = Bukkit.createInventory(null, num, "Conjuros");

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

	public void openInventory(final HumanEntity ent) {
		ent.openInventory(inv);
	}

	@EventHandler
	public void onInventoryClick(final InventoryDragEvent e) {
		if (e.getInventory().equals(inv)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	private void onInventoryClick(InventoryClickEvent e) {
		Inventory inventory = e.getClickedInventory();

		// Nada de inventarios fantasma
		if (inventory == null || !e.getInventory().equals(inv)) {
			return;
		}

		// When menu conjuros
		if (inventory.getType().equals(InventoryType.CHEST) && e.getCurrentItem() != null) {
			e.setCancelled(true);
			e.getView().getTitle().equals(plugin.invMenuName);

			HumanEntity mago = e.getWhoClicked();
			Varita varita = Varita.esItemStackUnaVarita(mago.getInventory().getItemInMainHand());
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
	}
}