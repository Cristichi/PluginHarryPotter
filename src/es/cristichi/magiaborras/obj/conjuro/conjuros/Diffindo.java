package es.cristichi.magiaborras.obj.conjuro.conjuros;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.plugin.Plugin;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.conjuro.EfectoVisual;
import es.cristichi.magiaborras.obj.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.conjuro.TiposLanzamiento;
import es.cristichi.magiaborras.obj.varita.Varita;

public class Diffindo extends Conjuro {

	public Diffindo(Plugin plugin) {
		super(plugin, "diffindo", "Diffindo", "El conjuro de daño básico. Hace pupa y ya.",
				new MaterialChoice(Material.REDSTONE_TORCH, Material.COBBLESTONE_SLAB, Material.COBBLESTONE_STAIRS,
						Material.COBBLESTONE_WALL),
				new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD),
				new EfectoVisual[] { EfectoVisual.PARTICULAS, EfectoVisual.RAYITO }, ChatColor.RED + "", Color.RED, 20,
				"");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento) {
		if (victima instanceof LivingEntity) {
			((LivingEntity) victima).damage(5 * varita.getPotencia(mago), mago);
			return true;
		}
		return false;
	}

}

