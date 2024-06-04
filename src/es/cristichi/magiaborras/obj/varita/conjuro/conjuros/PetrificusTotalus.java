package es.cristichi.magiaborras.obj.varita.conjuro.conjuros;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.varita.Varita;
import es.cristichi.magiaborras.obj.varita.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.varita.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.varita.conjuro.TiposLanzamiento;

public class PetrificusTotalus extends Conjuro {

	public PetrificusTotalus(Plugin plugin) {
		super(plugin, "petrificustotalus", "Petrificus Totalus",
				"Detiene una entidad donde está. Útil para traicionar amigos.", new MaterialChoice(Material.STONE),
				new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD), ChatColor.BOLD + "", Color.WHITE, 500, "");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento, float potencia) {
		if (victima instanceof LivingEntity) {
			int ticks = (int) (60 * potencia) + 10;
			((LivingEntity) victima).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks, 999));
			int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				@Override
				public void run() {
					// objetivo.teleport(loc);
					victima.setVelocity(new Vector());
				}
			}, 0, 1);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					Bukkit.getScheduler().cancelTask(id);
				}
			}, ticks);
			resetTiempoPalabras(mago);
			return true;
		}
		return false;
	}

}
