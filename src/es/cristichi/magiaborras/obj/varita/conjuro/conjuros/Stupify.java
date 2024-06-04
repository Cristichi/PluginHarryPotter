package es.cristichi.magiaborras.obj.varita.conjuro.conjuros;

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
import es.cristichi.magiaborras.obj.varita.Varita;
import es.cristichi.magiaborras.obj.varita.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.varita.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.varita.conjuro.TiposLanzamiento;

public class Stupify extends Conjuro {

	public Stupify(Plugin plugin) {
		super(plugin, "stupify", "Stupify", "El conjuro de daño básico. Hace pupa y ya.",
				new MaterialChoice(Material.COBBLESTONE, Material.COBBLESTONE_SLAB, Material.COBBLESTONE_STAIRS,
						Material.COBBLESTONE_WALL),
				new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD), ChatColor.RED + "", Color.RED, 20, "");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento, float potencia) {
		if (victima instanceof LivingEntity) {
			((LivingEntity) victima).damage(5 * potencia, mago);
			return true;
		}
		return false;
	}

}
