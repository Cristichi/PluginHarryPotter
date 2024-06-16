package es.cristichi.magiaborras.obj.conjuro.conjuros;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustTransition;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Stray;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.conjuro.EfectoVisual;
import es.cristichi.magiaborras.obj.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.conjuro.TiposLanzamiento;
import es.cristichi.magiaborras.obj.varita.Varita;

public class Crucio extends Conjuro implements Listener {

	private final String META_KEY = "crucio-no-att";
	private final MetadataValue META_VALUE;

	public Crucio(Plugin plugin) {
		super(plugin, "crucio", "Crucio",
				"La maldición cruciatus. Hace que cualquier cosa viva se quede quieta sufriendo un rato.",
				new MaterialChoice(Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE),
				new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD, TipoLanzamiento.GOLPE),
				new EfectoVisual[] { EfectoVisual.PARTICULAS, EfectoVisual.RAYITO },
				ChatColor.DARK_RED + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH, Color.fromRGB(255, 50, 0), 1, "");

		META_VALUE = new FixedMetadataValue(plugin, true);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento) {
		if (victima instanceof LivingEntity) {
			LivingEntity victimaViva = (LivingEntity) victima;
			int duracion = (int) (cooldownTicks + 10);
			ArrayList<PotionEffect> efectos = new ArrayList<>(10);
			efectos.add(new PotionEffect(PotionEffectType.DARKNESS, duracion, 255));
			efectos.add(new PotionEffect(PotionEffectType.NAUSEA, duracion, 255));
			efectos.add(new PotionEffect(PotionEffectType.MINING_FATIGUE, duracion, 255));
			efectos.add(new PotionEffect(PotionEffectType.HUNGER, duracion, 255));
			efectos.add(new PotionEffect(PotionEffectType.WEAKNESS, duracion, 255));
			efectos.add(new PotionEffect(PotionEffectType.SLOWNESS, duracion, 255));
			efectos.add(new PotionEffect(PotionEffectType.POISON, duracion, 255));
			if (victima instanceof Zombie || victima instanceof Skeleton || victima instanceof CaveSpider
					|| victima instanceof Spider || victima instanceof Phantom || victima instanceof Stray
					|| victima instanceof Wither || victima instanceof WitherSkeleton) {
				victimaViva.setFireTicks(duracion * 2);
			}
			victimaViva.addPotionEffects(efectos);
			victimaViva.setHealth(victimaViva.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			if (!victimaViva.hasMetadata(META_KEY)) {
				victimaViva.setMetadata(META_KEY, META_VALUE);
			}
			victimaViva.setGravity(false);
			Vector movimiento = new Vector();

			final int periodo = 1;
			Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Consumer<BukkitTask>() {
				int ticks = 0;

				@Override
				public void accept(BukkitTask t) {
					if (ticks >= duracion || victimaViva.isDead()) {
						if (victimaViva.hasMetadata(META_KEY)) {
							victimaViva.removeMetadata(META_KEY, plugin);
						}
						victimaViva.setGravity(true);
						t.cancel();
					} else {
						if (!victimaViva.hasMetadata(META_KEY)) {
							victimaViva.setMetadata(META_KEY, META_VALUE);
						}
						victimaViva.setVelocity(movimiento);
						final DustTransition dustTransitionOnda = new DustTransition(getColor(), getColor(), 0.5f);
						mago.spawnParticle(Particle.DUST_COLOR_TRANSITION, victima.getLocation().clone().add(0, 0.5, 0),
								periodo, 0.5, 0.5, 0.5, dustTransitionOnda);
						mago.spawnParticle(Particle.SWEEP_ATTACK, victima.getLocation().clone().add(0, 0.5, 0), 1, 0.5,
								0.5, 0.5);

						ticks += periodo;
					}
				}
			}, 0, periodo);

			return true;
		}
		return false;
	}

	@EventHandler
	private void onShoot(EntityShootBowEvent e) {
		if (e.getEntity().hasMetadata(META_KEY)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	private void onEntDamageEnt(EntityDamageByEntityEvent e) {
		if (e.getDamager().hasMetadata(META_KEY)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	private void onEntTeleport(EntityTeleportEvent e) {
		if (e.getEntity().hasMetadata(META_KEY)) {
			e.setCancelled(true);
		}
	}
}
