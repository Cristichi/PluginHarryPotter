package es.cristichi.magiaborras.obj.varita.conjuro.conjuros;

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
import es.cristichi.magiaborras.obj.varita.Varita;
import es.cristichi.magiaborras.obj.varita.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.varita.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.varita.conjuro.TiposLanzamiento;

public class AvadaKedavra extends Conjuro {
	
	public AvadaKedavra(Plugin plugin) {
		super(plugin, "avada_kedavra", "Avada Kedavra", "La maldición asesina.",
				new MaterialChoice(Material.DRAGON_HEAD, Material.CREEPER_HEAD, Material.PLAYER_HEAD,
						Material.ZOMBIE_HEAD, Material.SKELETON_SKULL, Material.WITHER_SKELETON_SKULL),
				new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD, TipoLanzamiento.GOLPE),
				ChatColor.GREEN + "" + ChatColor.BOLD, Color.GREEN, 1200, "");
	}

	@Override
	public boolean puedeLanzar(MagiaPlugin plugin, Player mago, Entity victima, Varita varita, double cdr,
			boolean avisar) {
		return super.puedeLanzar(plugin, mago, victima, varita, varita.isHack() ? 0.5 : 1, avisar);
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento, float potencia) {
		if (victima instanceof LivingEntity) {
			LivingEntity victimaViva = (LivingEntity) victima;
			if (!victimaViva.isDead()) {
				System.out.println(victimaViva.getName());
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
