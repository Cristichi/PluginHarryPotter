package es.cristichi.magiaborras.obj.varita.conjuro.conjuros;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.varita.Varita;
import es.cristichi.magiaborras.obj.varita.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.varita.conjuro.EfectoVisual;
import es.cristichi.magiaborras.obj.varita.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.varita.conjuro.TiposLanzamiento;

public class Accio extends Conjuro {

	public Accio(Plugin plugin) {
		super(plugin, "accio", "Accio", "Trae una entidad a la que estés mirando directamente hacia ti.",
				new MaterialChoice(Material.COMPASS), new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD),
				new EfectoVisual[] { EfectoVisual.PARTICULAS }, ChatColor.AQUA + "", Color.AQUA, 120, "");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento) {
		victima.setGravity(false);
		int idDist1 = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (mago.getLocation().distance(victima.getLocation()) > 2) {
					Vector pos = victima.getLocation().toVector();
					Vector target = mago.getLocation().toVector();
					Vector velocity = target.subtract(pos);
					victima.setVelocity(velocity.normalize());
				}
			}
		}, 0, 5);
		int idDist2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				// Cancela el hechizo si la entidad se acerca mucho al mago
				if (mago.getLocation().distance(victima.getLocation()) < 2) {
					victima.setGravity(true);
					Bukkit.getScheduler().cancelTask(idDist1);
				}
			}
		}, 0, 5);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				// Cancela el hechizo si pasa un tiempo (10-70 depende de la potencia)
				Bukkit.getScheduler().cancelTask(idDist1);
				Bukkit.getScheduler().cancelTask(idDist2);
				victima.setGravity(true);
			}
		}, (long) (10 + 60 * varita.getPotencia(mago)));
		resetTiempoPalabras(mago);
		return true;
	}

}
