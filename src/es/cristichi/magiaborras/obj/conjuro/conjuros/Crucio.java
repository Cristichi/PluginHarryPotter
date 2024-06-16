package es.cristichi.magiaborras.obj.conjuro.conjuros;

import java.util.ArrayList;

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

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.conjuro.EfectoVisual;
import es.cristichi.magiaborras.obj.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.conjuro.TiposLanzamiento;
import es.cristichi.magiaborras.obj.varita.Varita;

public class Crucio extends Conjuro {

	public Crucio(Plugin plugin) {
		super(plugin, "crucio", "Crucio",
				"La maldición cruciatus. Hace que cualquier cosa viva se quede quieta sufriendo un rato.",
				new MaterialChoice(Material.PLAYER_WALL_HEAD),
				new TiposLanzamiento(TipoLanzamiento.CERCA_ENTIDAD, TipoLanzamiento.GOLPE),
				new EfectoVisual[] { EfectoVisual.PARTICULAS, EfectoVisual.RAYITO },
				ChatColor.DARK_RED + "" + ChatColor.BOLD, Color.fromRGB(255, 50, 0), 18000, "");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento) {
		if (victima instanceof LivingEntity) {
			LivingEntity victimaViva = (LivingEntity) victima;
			int duracion = (int) (3600 * varita.getPotencia(mago));
			ArrayList<PotionEffect> efectos = new ArrayList<>(10);
			efectos.add(new PotionEffect(PotionEffectType.DARKNESS, duracion, 255));
			efectos.add(new PotionEffect(PotionEffectType.NAUSEA, duracion, 255));
			efectos.add(new PotionEffect(PotionEffectType.MINING_FATIGUE, duracion, 255));
			efectos.add(new PotionEffect(PotionEffectType.HUNGER, duracion, 255));
			efectos.add(new PotionEffect(PotionEffectType.POISON, duracion, 255));
			efectos.add(new PotionEffect(PotionEffectType.WEAKNESS, duracion, 255));
			efectos.add(new PotionEffect(PotionEffectType.SLOWNESS, duracion, 255));
			efectos.add(new PotionEffect(PotionEffectType.GLOWING, duracion, 1));
			victimaViva.addPotionEffects(efectos);
			victimaViva.setHealth(1);
			return true;
		}
		return false;
	}
}
