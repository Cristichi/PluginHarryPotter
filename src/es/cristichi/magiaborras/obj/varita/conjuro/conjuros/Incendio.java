package es.cristichi.magiaborras.obj.varita.conjuro.conjuros;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.plugin.Plugin;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.varita.Varita;
import es.cristichi.magiaborras.obj.varita.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.varita.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.varita.conjuro.TipoProyectil;
import es.cristichi.magiaborras.obj.varita.conjuro.TiposLanzamiento;

public class Incendio extends Conjuro {

	public Incendio(Plugin plugin) {
		super(plugin, "incendio", "Incendio", new MaterialChoice(Material.FLINT_AND_STEEL, Material.FIRE_CHARGE),
				new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD, TipoLanzamiento.DISTANCIA_BLOQUE),
				ChatColor.RED + "", Color.RED, 60, TipoProyectil.COHETE);
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento, float potencia) {
		Location centro = null;
		if (victima == null) {
			centro = bloque.getLocation().add(0, 1, 0);
		} else {
			centro = victima.getLocation();
		}
		int radio = (int) (2 * potencia) + 2;
		potencia *= 0.8;
		for (int i = -radio + 1; i < radio; i++) {
			for (int j = -radio + 1; j < radio; j++) {
				for (int k = -radio + 1; k < radio; k++) {
					Block lego = centro.getWorld().getBlockAt(centro.add(i, j, k));
					if (Math.random() < potencia && lego.getType().name().contains("AIR")) {
						lego.setType(Material.FIRE);
					}
				}
			}
		}
		return true;
	}

}
