package es.cristichi.magiaborras.obj.conjuro.conjuros;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
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

public class AvadaKedavra extends Conjuro {

	public AvadaKedavra(Plugin plugin) {
		super(plugin, "avada_kedavra", "Avada Kedavra", "La maldición asesina.",
				new MaterialChoice(Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, Material.DRAGON_HEAD, Material.CREEPER_HEAD,
						Material.PLAYER_HEAD, Material.ZOMBIE_HEAD, Material.SKELETON_SKULL,
						Material.WITHER_SKELETON_SKULL),
				new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD, TipoLanzamiento.GOLPE),
				new EfectoVisual[] { EfectoVisual.PARTICULAS, EfectoVisual.RAYITO },
				ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH, Color.GREEN, 18000, "");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento) {
		if (victima instanceof LivingEntity) {
			LivingEntity victimaViva = (LivingEntity) victima;
			if (!victimaViva.isDead()) {
				victimaViva.setHealth(1);
				victimaViva.damage(9999);
				victimaViva.playEffect(EntityEffect.HURT_DROWN);
				victimaViva.getWorld().strikeLightningEffect(victimaViva.getLocation());
				victimaViva.getWorld().playSound(victimaViva.getLocation(), Sound.ENTITY_WITCH_CELEBRATE, 1, 0.1F);
				victimaViva.getWorld().playSound(victimaViva.getLocation(), Sound.ENCHANT_THORNS_HIT, 1, 0.1F);
				resetTiempoPalabras(mago);
				return true;
			}
		}
		return false;
	}
}
