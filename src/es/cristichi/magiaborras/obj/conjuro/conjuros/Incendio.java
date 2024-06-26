package es.cristichi.magiaborras.obj.conjuro.conjuros;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.plugin.Plugin;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.conjuro.EfectoVisual;
import es.cristichi.magiaborras.obj.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.conjuro.TiposLanzamiento;
import es.cristichi.magiaborras.obj.varita.Varita;

public class Incendio extends Conjuro {

	public Incendio(Plugin plugin) {
		super(plugin, "incendio", "Incendio", "FUEGO FUEGOOOO MUAJAJAJAJAJAJAJA",
				new MaterialChoice(Material.FLINT_AND_STEEL, Material.FIRE_CHARGE),
				new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD, TipoLanzamiento.DISTANCIA_BLOQUE),
				new EfectoVisual[] { EfectoVisual.PARTICULAS, EfectoVisual.RAYITO }, ChatColor.RED + "", Color.RED, 200, "");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento) {
		float potencia = varita.getPotencia(mago);
		Location centro = null;
		if (victima == null) {
			centro = bloque.getLocation().add(0, 1, 0);
		} else {
			centro = victima.getLocation();
		}
		int radio = (int) (2 * potencia) + 2;
		double probPorBloque = potencia;
		for (int i = -radio + 1; i < radio; i++) {
			for (int j = -radio + 1; j < radio; j++) {
				for (int k = -radio + 1; k < radio; k++) {
					Block lego = centro.getWorld().getBlockAt(centro.clone().add(i, j, k));
					if (lego.getType().name().contains("AIR") && Math.random() < probPorBloque) {
						lego.setType(Material.FIRE);
					} else if (lego.getType().equals(Material.TNT)) {
						lego.getWorld().spawnEntity(lego.getLocation().add(0.5, 0.5, 0.5), EntityType.TNT);
						lego.setType(Material.AIR);
					}
				}
			}
		}
		return true;
	}

}
