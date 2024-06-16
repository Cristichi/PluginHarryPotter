package es.cristichi.magiaborras.obj.conjuro.conjuros;

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
import es.cristichi.magiaborras.obj.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.conjuro.EfectoVisual;
import es.cristichi.magiaborras.obj.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.conjuro.TiposLanzamiento;
import es.cristichi.magiaborras.obj.varita.Varita;

public class Depulso extends Conjuro {

	public Depulso(Plugin plugin) {
		super(plugin, "depulso", "Depulso", "Lanza hacia atrás a la entidad a la que estés mirando.",
				new MaterialChoice(Material.IRON_DOOR), new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD),
				new EfectoVisual[] { EfectoVisual.PARTICULAS, EfectoVisual.RAYITO }, ChatColor.AQUA + "", Color.AQUA, 120, "");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento) {
		Vector pos = victima.getLocation().toVector();
		Vector target = mago.getLocation().toVector();
		Vector velocity = pos.subtract(target);
		victima.setVelocity(velocity.normalize().multiply(6 * varita.getPotencia(mago)));
		resetTiempoPalabras(mago);
		return true;
	}

}
