package es.cristichi.magiaborras.obj.conjuro.conjuros;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.conjuro.EfectoVisual;
import es.cristichi.magiaborras.obj.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.conjuro.TiposLanzamiento;
import es.cristichi.magiaborras.obj.varita.Varita;

public class Imperio extends Conjuro {

	public Imperio(Plugin plugin) {
		super(plugin, "imperio", "Imperio", "La maldición imperius. Aquí, de momento solo sirve con Aldeanos. Les hace venderte todo más barato.", new MaterialChoice(Material.PLAYER_HEAD),
				new TiposLanzamiento(TipoLanzamiento.CERCA_ENTIDAD, TipoLanzamiento.GOLPE),
				new EfectoVisual[] { EfectoVisual.PARTICULAS, EfectoVisual.RAYITO },
				ChatColor.DARK_RED + "" + ChatColor.BOLD, Color.fromRGB(255, 50, 0), 18000, "");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento) {
		if (victima instanceof Villager) {
			Villager aldeano = (Villager) victima;
			ZombieVillager zAldeano = aldeano.zombify();
			zAldeano.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 9999, 1));
			zAldeano.setConversionTime(5);
			zAldeano.setConversionPlayer(mago);
			return true;
		}
		return false;
	}
}
