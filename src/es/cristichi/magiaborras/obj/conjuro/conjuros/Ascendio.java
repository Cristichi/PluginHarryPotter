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

public class Ascendio extends Conjuro {

	public Ascendio(Plugin plugin) {
		super(plugin, "ascendio", "Ascendio", "Parriba lol.", new MaterialChoice(Material.LADDER),
				new TiposLanzamiento(TipoLanzamiento.AREA_MAGO), new EfectoVisual[] { EfectoVisual.PARTICULAS },
				ChatColor.AQUA + "", Color.AQUA, 0, "");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento) {
		mago.setVelocity(mago.getVelocity().add(new Vector(0, 0.3, 0)));
		return true;
	}

}
