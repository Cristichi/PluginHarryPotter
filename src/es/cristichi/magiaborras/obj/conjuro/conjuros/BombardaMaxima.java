package es.cristichi.magiaborras.obj.conjuro.conjuros;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.plugin.Plugin;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.conjuro.EfectoVisual;
import es.cristichi.magiaborras.obj.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.conjuro.TiposLanzamiento;
import es.cristichi.magiaborras.obj.varita.Varita;

public class BombardaMaxima extends Conjuro {

	public BombardaMaxima(Plugin plugin) {
		super(plugin, "bombarda_maxima", "Bombarda Máxima", "Catapum.", new MaterialChoice(Material.CREEPER_HEAD),
				new TiposLanzamiento(TipoLanzamiento.DISTANCIA_BLOQUE, TipoLanzamiento.DISTANCIA_ENTIDAD),
				new EfectoVisual[] { EfectoVisual.PARTICULAS, EfectoVisual.RAYITO },
				ChatColor.DARK_PURPLE + "" + ChatColor.BOLD, Color.fromRGB(255, 0, 255), 240, "");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento) {
		if (victima != null) {
			mago.getWorld().createExplosion(victima.getLocation(), 8f * varita.getPotencia(mago), false, true, mago);
			return true;
		} else if (bloque != null) {
			mago.getWorld().createExplosion(bloque.getLocation(), 8f * varita.getPotencia(mago), false, true, mago);
			return true;
		}
		return false;
	}
}