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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.varita.Varita;
import es.cristichi.magiaborras.obj.varita.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.varita.conjuro.EfectoVisual;
import es.cristichi.magiaborras.obj.varita.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.varita.conjuro.TiposLanzamiento;

public class WingardiumLeviosa extends Conjuro {

	public WingardiumLeviosa(Plugin plugin) {
		super(plugin, "wingardiumleviosa", "Wingardium Leviosa",
				"Mantén el ratón mientras miras fijamente a una persona y verás qué risa.",
				new MaterialChoice(Material.FEATHER), new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD),
				new EfectoVisual[] { EfectoVisual.PARTICULAS }, ChatColor.GRAY + "", Color.GRAY, 0, "");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento, float potencia) {
		if (victima != null) {
			if (victima instanceof LivingEntity) {
				int ticks = (int) (8 * potencia) + 2;
				((LivingEntity) victima).addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, ticks, 1));
				victima.setVelocity(victima.getVelocity().add(new Vector(0, .1, 0)));
			} else {
				victima.setVelocity(victima.getVelocity().add(new Vector(0, .3, 0)));
			}
			return true;
		}
		return false;
	}

}
