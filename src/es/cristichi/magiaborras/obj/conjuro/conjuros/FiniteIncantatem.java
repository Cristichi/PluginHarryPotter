package es.cristichi.magiaborras.obj.conjuro.conjuros;

import java.util.Collection;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.conjuro.EfectoVisual;
import es.cristichi.magiaborras.obj.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.conjuro.TiposLanzamiento;
import es.cristichi.magiaborras.obj.varita.Varita;

public class FiniteIncantatem extends Conjuro {
	private NamespacedKey key;

	public FiniteIncantatem(Plugin plugin) {
		super(plugin, "finite_incantatem", "Finite Incantatem", "Detiene el Morsmorde. No tiene más usos la verdad.",
				new MaterialChoice(Material.WATER_BUCKET), new TiposLanzamiento(TipoLanzamiento.AREA_MAGO),
				new EfectoVisual[] { EfectoVisual.PARTICULAS, EfectoVisual.ONDA }, ChatColor.DARK_GRAY + "", Color.WHITE, 0, "");
		key = new NamespacedKey(plugin, "efectoMorsmordre");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento) {
		Collection<Entity> armorStands = mago.getWorld().getNearbyEntities(mago.getLocation(), 50, 15, 50,
				new Predicate<Entity>() {
					@Override
					public boolean test(Entity t) {
						return t instanceof ArmorStand
								&& t.getPersistentDataContainer().has(key, PersistentDataType.INTEGER);
					}
				});
		for (Entity as : armorStands) {
			Bukkit.getScheduler().cancelTask(as.getPersistentDataContainer().get(key, PersistentDataType.INTEGER));
			as.remove();
		}
		return true;
	}

}
