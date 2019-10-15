package obj;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_14_R1.block.impl.CraftCauldron;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Caldero implements Listener {
	private static String metaCaldero = "caldero";
	private static String metaIngredientes = "ingredientesCaldero";

	private Plugin plugin;
	private ArrayList<RecetaPocion> recetas;
	private Integer idTaskParticulas;

	public Caldero(Plugin plugin, ArrayList<RecetaPocion> recetas) {
		this.plugin = plugin;
		this.recetas = recetas;

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	protected void onPlace(BlockPlaceEvent e) {
		Block lego = e.getBlock();
		Location loc = lego.getLocation();
		switch (lego.getType()) {
		case CAULDRON:
			Block debajo = lego.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());
			if (debajo.getType().equals(Material.CAMPFIRE)) {
				lego.setMetadata(metaCaldero, new FixedMetadataValue(plugin, true));
				CraftCauldron caldero = (CraftCauldron) lego.getBlockData();
				caldero.setLevel(caldero.getMaximumLevel());
				lego.setBlockData(caldero);
				e.getPlayer().sendMessage("¡Has construido un caldero! ");
			}
			break;
		case CAMPFIRE:
			Block encima = lego.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
			if (encima.getType().equals(Material.CAULDRON)) {
				encima.setMetadata(metaCaldero, new FixedMetadataValue(plugin, true));
				e.getPlayer().sendMessage("¡Has construido un caldero!");
			}
			break;
		default:
			break;
		}
	}

	@EventHandler
	protected void onBreak(BlockBreakEvent e) {
		Block lego = e.getBlock();
		Location loc = lego.getLocation();
		switch (lego.getType()) {
		case CAULDRON:
			Block debajo = lego.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());
			if (debajo.getType().equals(Material.CAMPFIRE) && lego.hasMetadata(metaCaldero)) {
				lego.removeMetadata(metaCaldero, plugin);
				lego.removeMetadata(metaIngredientes, plugin);
				if (idTaskParticulas != null)
					Bukkit.getScheduler().cancelTask(idTaskParticulas);
				e.getPlayer().sendMessage("Has destruido el caldero. ");
			}
			break;
		case CAMPFIRE:
			Block encima = lego.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
			if (encima.getType().equals(Material.CAULDRON) && encima.hasMetadata(metaCaldero)) {
				encima.removeMetadata(metaCaldero, plugin);
				encima.removeMetadata(metaIngredientes, plugin);
				if (idTaskParticulas != null)
					Bukkit.getScheduler().cancelTask(idTaskParticulas);
				e.getPlayer().sendMessage("Has destruido el caldero.");
			}
			break;
		default:
			break;
		}
	}

	@EventHandler
	protected void onItemDrop(PlayerDropItemEvent e) {
		BukkitScheduler hilos = Bukkit.getScheduler();
		Item item = e.getItemDrop();
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			@Override
			public void run() {
				if (idTaskParticulas != null) {
					hilos.cancelTask(idTaskParticulas);
				}
				Block lego = item.getWorld().getBlockAt(item.getLocation());
				if (lego.hasMetadata(metaCaldero)) {
					ArrayList<Material> mats = new ArrayList<>();
					String ing = "";
					if (lego.hasMetadata(metaIngredientes)) {
						ing = lego.getMetadata(metaIngredientes).get(0).asString();
						String[] matStrings = ing.split(":");
						for (String m : matStrings) {
							try {
								mats.add(Material.valueOf(m));
							} catch (Exception e1) {
							}
						}
					}
					ItemStack itemStack = item.getItemStack();
					for (int i = 0; i < itemStack.getAmount(); i++) {
						ing += ":" + itemStack.getType().name();
						mats.add(itemStack.getType());
					}
					item.remove();
					lego.setMetadata(metaIngredientes, new FixedMetadataValue(plugin, ing));
					e.getPlayer().playSound(lego.getLocation(), Sound.ENTITY_GENERIC_SPLASH, (float) 0.5, 1);

					RecetaPocion receta = getReceta(mats);
					if (receta != null) {
						idTaskParticulas = hilos.scheduleSyncRepeatingTask(plugin, new Runnable() {
							World world = lego.getWorld();
							Location loc = lego.getLocation().add(0.5, 1, 0.5);

							@Override
							public void run() {
								DustOptions dust = new DustOptions(receta.getResultado().getColor(),
										(int) (Math.random() * 5));
								world.spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 1, 0.1, 0.1, 0.1,
										dust);
							}
						}, 0, 20);
					}
				}
			}
		}, 15);
	}

	@EventHandler
	protected void onClick(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block lego = e.getClickedBlock();
			if (lego.hasMetadata(metaCaldero)) {
				Player p = e.getPlayer();
				if (lego.hasMetadata(metaIngredientes)) {
					if (p.isSneaking()) {
						String ingredientes = lego.getMetadata(metaIngredientes).get(0).asString();
						String[] ings = ingredientes.split(":");

						World w = p.getWorld();
						Location loc = p.getEyeLocation();
						for (String ing : ings) {
							try {
								Material mat = Material.valueOf(ing);
								w.dropItemNaturally(loc, new ItemStack(mat));
							} catch (Exception err) {
								err.printStackTrace();
							}
						}
						lego.removeMetadata(metaIngredientes, plugin);

						if (idTaskParticulas != null) {
							Bukkit.getScheduler().cancelTask(idTaskParticulas);
						}
					}
//					else {
//						String ingredientes = lego.getMetadata(metaIngredientes).get(0).asString();
//						String[] ings = ingredientes.split(":");
//
//						String msg = "Ingredientes:";
//						for (String ing : ings) {
//							if (!ing.isEmpty())
//								try {
//									Material mat = Material.valueOf(ing);
//									msg += "\n  -" + mat.toString();
//								} catch (Exception err) {
//									err.printStackTrace();
//								}
//						}
//						p.sendMessage(msg);
//					}
				}
			}
		}
	}

	@EventHandler
	protected void onCauldronLevelChange(CauldronLevelChangeEvent e) {
		if (e.getBlock().hasMetadata(metaCaldero) && e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			Block lego = e.getBlock();
			switch (e.getReason()) {
			case BOTTLE_FILL:
				if (lego.hasMetadata(metaIngredientes)) {
					String[] strs = lego.getMetadata(metaIngredientes).get(0).asString().split(":");
					ArrayList<Material> mats = new ArrayList<>(strs.length);
					for (int i = 0; i < strs.length; i++) {
						if (!strs[i].isEmpty()) {
							try {
								mats.add(Material.valueOf(strs[i]));
							} catch (Exception err) {
								System.err.println("Error trying to convert " + strs[i] + " to Material");
							}
						}
					}
					RecetaPocion receta = getReceta(mats);
					if (receta != null) {
						lego.removeMetadata(metaIngredientes, plugin);
						p.playSound(lego.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH, 1, 1);
						PlayerInventory pi = p.getInventory();
						ItemStack mano = pi.getItemInMainHand();
						if (mano.getAmount() > 1) {
							if (!p.getGameMode().equals(GameMode.CREATIVE))
								mano.setAmount(mano.getAmount() - 1);
							p.getWorld().dropItem(p.getEyeLocation(), mano);
						}
						pi.setItemInMainHand(new ItemStack(receta.getResultado()));
						e.setCancelled(true);

						CraftCauldron caldero = (CraftCauldron) lego.getBlockData();
						caldero.setLevel(e.getNewLevel());
						lego.setBlockData(caldero);
						if (idTaskParticulas != null) {
							Bukkit.getScheduler().cancelTask(idTaskParticulas);
						}
					}
				}
				break;
			default:
				break;
			}
		}
	}

	private RecetaPocion getReceta(ArrayList<Material> ingredientes) {
		for (RecetaPocion receta : recetas) {
			ArrayList<Material> clon = new ArrayList<>(receta.getMateriales());
			if (clon.size() == ingredientes.size()) {
				for (Material mat : ingredientes) {
					if (!clon.remove(mat)) {
						break;
					}
				}
				if (clon.size() == 0) {
					return receta;
				}
			}
		}
		return null;
	}
}
