package es.cristichi.magiaborras.obj.conjuro.conjuros;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.conjuro.EfectoVisual;
import es.cristichi.magiaborras.obj.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.conjuro.TiposLanzamiento;
import es.cristichi.magiaborras.obj.varita.Varita;

public class Sectumsempra extends Conjuro {

	public Sectumsempra(Plugin plugin) {
		super(plugin, "sectumsempra", "Sectumsempra",
				"¿Sos cirujano? A ver, nombra todos los cortes que se puede hacer a los interiores de la persona humana.",
				new MaterialChoice(Material.REDSTONE), new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD),
				new EfectoVisual[] { EfectoVisual.PARTICULAS, EfectoVisual.RAYITO }, ChatColor.DARK_RED + "",
				Color.fromRGB(115, 0, 0), 1000, "");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento) {
		if (victima instanceof LivingEntity) {
			LivingEntity victimaViva = (LivingEntity) victima;
			float potencia = varita.getPotencia(mago);
			int delay = 10;
			int ticks = (int) (5300 * potencia + 100);
			int wait = 20;
			final int repes = ticks / wait;
			double damage = Math.max(1, victimaViva.getHealth() * potencia / repes);
			victima.getLocation().getWorld().spawnParticle(Particle.CRIT, ((LivingEntity) victima).getEyeLocation(), 15,
					0.1, 0.1, 0.1);
			
			Bukkit.getScheduler().runTaskTimer(plugin, new Consumer<BukkitTask>() {

				int repe = 0;
				@Override
				public void accept(BukkitTask t) {
					if (++repe > repes || victimaViva.isDead()) {
						t.cancel();
					} else {
						victimaViva.damage(damage, mago);
						victimaViva.getLocation().getWorld().spawnParticle(Particle.SWEEP_ATTACK,
								((LivingEntity) victima).getEyeLocation(), 15, 0.1, 0.1, 0.1);
						victimaViva.getWorld().playSound(victimaViva.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1,
								0.01f);
						if (victimaViva.getHealth() < 0) {
							victimaViva.setHealth(0);
						}
					}
				}
			}, delay, wait);

//			int idDamage = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
//				@Override
//				public void run() {
//					if (!victimaViva.isDead()) {
//
//						victimaViva.getLocation().getWorld().spawnParticle(Particle.SWEEP_ATTACK,
//								((LivingEntity) victima).getEyeLocation(), 15, 0.1, 0.1, 0.1);
//						victimaViva.getWorld().playSound(victimaViva.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1,
//								0.01f);
//						victimaViva.damage(damage, mago);
//						if (victimaViva.getHealth() < 0) {
//							victimaViva.setHealth(0);
//						}
//					}
//				}
//			}, delay, wait);
//
//			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//				@Override
//				public void run() {
//					Bukkit.getScheduler().cancelTask(idDamage);
//				}
//			}, delay / 2 + ticks);
			resetTiempoPalabras(mago);
			return true;
		}
		return false;
	}

}
