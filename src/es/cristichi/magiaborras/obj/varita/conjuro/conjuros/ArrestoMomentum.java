package es.cristichi.magiaborras.obj.varita.conjuro.conjuros;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.varita.Varita;
import es.cristichi.magiaborras.obj.varita.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.varita.conjuro.EfectoVisual;
import es.cristichi.magiaborras.obj.varita.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.varita.conjuro.TiposLanzamiento;

public class ArrestoMomentum extends Conjuro {

	public ArrestoMomentum(Plugin plugin) {
		super(plugin, "arresto_momentum", "Arresto Momentum",
				"Te detiene a ti y a todas las entidades de tu alrededor. ¡Útil para saltar por precipicios!",
				new MaterialChoice(Material.SPONGE, Material.WET_SPONGE),
				new TiposLanzamiento(TipoLanzamiento.AREA_MAGO),
				new EfectoVisual[] { EfectoVisual.PARTICULAS, EfectoVisual.ONDA }, ChatColor.AQUA + "", Color.AQUA, 120,
				"");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento) {
		float potencia = varita.getPotencia(mago);
		Location centro = mago.getLocation();
		double radio = 6 * potencia;
		Collection<Entity> entidades = centro.getWorld().getNearbyEntities(centro, radio, radio, radio, null);
		for (Entity ent : entidades) {
			if (ent instanceof LivingEntity) {
				((LivingEntity) ent).addPotionEffect(
						new PotionEffect(PotionEffectType.SLOW_FALLING, (int) (20 * potencia), (int) (3 * potencia)));
			}
			ent.setVelocity(ent.getVelocity().setY(0));
			ent.setFallDistance(0);
		}
		return true;
	}

}
