package es.cristichi.magiaborras.obj.varita;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.util.Targeter;

public class VaritaListener implements Listener {

		@EventHandler
		private void onPlayerJoin(PlayerJoinEvent e) {
			Player p = e.getPlayer();
			p.discoverRecipe(Varita.keyReceta);
			if (!Varita.numerosMagicos.containsKey(p.getUniqueId())) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(Varita.plugin, new Runnable() {
					@Override
					public void run() {
						Varita nueva = null;

						Varita nueva1 = new Varita();
						nueva1.setJugador(p.getName());
						nueva1.recagarDatos();

						Varita nueva2 = new Varita();
						nueva2.setJugador(p.getName());
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
			Varita varita = Varita.esItemStackUnaVarita(p.getInventory().getItemInMainHand());
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
			Varita varita = Varita.esItemStackUnaVarita(e.getOffHandItem());
			if (varita != null) {
				ItemStack otro = e.getMainHandItem();
				for (Conjuro c : Conjuro.getConjuros()) {
					if (c.getIngredientes().test(otro)) {
						if (!c.equals(varita.getConjuro())) {
							PlayerInventory pi = e.getPlayer().getInventory();
							varita.cambiarConjuro(c);
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
				Varita v0 = Varita.esItemStackUnaVarita(ingrediente0);
				Varita v1 = Varita.esItemStackUnaVarita(ingrediente1);
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
					Varita varitaIngrediente = Varita.esItemStackUnaVarita(arrayList.get(0));
					if (varitaIngrediente != null && varitaIngrediente.getConjuro() != null) {
						Varita varita2 = new Varita(varitaIngrediente);
						varita2.cambiarConjuro(null);
						e.getInventory().setResult(varita2);
					}
				}
			} else if (e.getRecipe().getResult() != null) {
				Varita varita = Varita.esItemStackUnaVarita(e.getRecipe().getResult());
				if (varita != null) {
					if (!e.getView().getPlayer().hasPermission(Varita.plugin.PERM_CRAFT)) {
						ItemStack block = new ItemStack(Material.BARRIER);
						ItemMeta im = block.getItemMeta();
						im.setDisplayName("No puedes fabricar varitas");
						im.getPersistentDataContainer().set(new NamespacedKey(Varita.plugin, "blockcraft"),
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
		private void onInteractEntity(PlayerInteractEntityEvent e) {
			if (e.getHand().equals(EquipmentSlot.HAND)) {
				Player mago = e.getPlayer();
				Varita varita = Varita.esItemStackUnaVarita(mago.getInventory().getItemInMainHand());
				if (varita != null) {
					if (varita.getConjuro() != null) {
						Conjuro c = varita.getConjuro();
						if (c.isTipoLanzamiento(TipoLanzamiento.CERCA_ENTIDAD)) {
							c.Accionar(Varita.plugin, mago, e.getRightClicked(), null, varita, TipoLanzamiento.CERCA_ENTIDAD);
						}
					}
				}
			}

		}

		@EventHandler
		private void onInteract(PlayerInteractEvent e) {
			if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
					&& e.getItem() != null) {
				Varita varita = Varita.esItemStackUnaVarita(e.getItem());
				if (varita != null) {
					Player mago = e.getPlayer();
					if (varita.getConjuro() != null) {
						Conjuro c = varita.getConjuro();
						if (c.isTipoLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD)
								|| c.isTipoLanzamiento(TipoLanzamiento.DISTANCIA_BLOQUE)) {
							RayTraceResult target = Targeter.getTargetEntity(mago);
							if (target != null) {
								if (target.getHitEntity() != null) {
									c.Accionar(Varita.plugin, mago, target.getHitEntity(), null,
											Varita.esItemStackUnaVarita(mago.getInventory().getItemInMainHand()),
											TipoLanzamiento.DISTANCIA_ENTIDAD);
								} else if (target.getHitBlock() != null) {
									c.Accionar(Varita.plugin, mago, null, target.getHitBlock(),
											Varita.esItemStackUnaVarita(mago.getInventory().getItemInMainHand()),
											TipoLanzamiento.DISTANCIA_BLOQUE);
								}
							}
						} else if (c.isTipoLanzamiento(TipoLanzamiento.AREA_MAGO)) {
							c.Accionar(Varita.plugin, mago, null, null, varita, TipoLanzamiento.AREA_MAGO);
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
						c.Accionar(Varita.plugin, mago, atacado, null,
								Varita.esItemStackUnaVarita(mago.getInventory().getItemInMainHand()),
								TipoLanzamiento.DISTANCIA_ENTIDAD);
						break;
					}
			} else if (atacante instanceof Player) {
				Player mago = (Player) atacante;
				Varita varita = Varita.esItemStackUnaVarita(mago.getInventory().getItemInMainHand());
				if (varita != null && varita.getConjuro() != null) {
					Conjuro c = varita.getConjuro();
					if (c.isTipoLanzamiento(TipoLanzamiento.GOLPE) && varita.getConjuro().equals(c)) {
						if (c.Accionar(Varita.plugin, mago, atacado, null, varita, TipoLanzamiento.GOLPE)) {
							e.setCancelled(true);
						}
					}
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
						c.Accionar(Varita.plugin, mago, null, e.getHitBlock(),
								Varita.esItemStackUnaVarita(mago.getInventory().getItemInMainHand()),
								TipoLanzamiento.DISTANCIA_BLOQUE);
						break;
					}
				}
			}
		}

		@EventHandler
		private void onInventoryClick(InventoryClickEvent e) {
			Inventory inventory = e.getClickedInventory();

			// Nada de inventarios fantasma
			if (inventory == null) {
				return;
			}

			// Nada de robar la receta
			if (inventory.getType().equals(InventoryType.WORKBENCH)
					&& inventory.getClass().getName().equals("org.bukkit.craftbukkit.inventory.CraftInventoryCustom")) {
				if (Varita.esItemStackUnaVarita(inventory.getItem(0)) != null) {
					e.setCancelled(true);
					return;
				}
			}

			// When clicking to get the craft result
			if (e.getSlotType().equals(SlotType.RESULT)
					&& e.getClickedInventory().getType().equals(InventoryType.WORKBENCH)) {
				CraftingInventory inv = (CraftingInventory) e.getClickedInventory();
				ItemStack is = inv.getResult();
				if (is != null && is.hasItemMeta() && is.getItemMeta().getPersistentDataContainer()
						.has(new NamespacedKey(Varita.plugin, "blockcraft"), PersistentDataType.BYTE)) {
					e.setCancelled(true);
					return;
				}
				Varita varita = Varita.esItemStackUnaVarita(is);
				if (varita != null) {
					e.setCancelled(true);
					HumanEntity p = e.getView().getPlayer();
					PlayerInventory pi = p.getInventory();
					varita.setJugador(p.getName());
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
	}