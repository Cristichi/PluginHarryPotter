package es.cristichi.magiaborras.obj.varita.conjuro.conjuros;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.varita.Varita;
import es.cristichi.magiaborras.obj.varita.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.varita.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.varita.conjuro.TipoProyectil;
import es.cristichi.magiaborras.obj.varita.conjuro.TiposLanzamiento;

public class WingardiumLeviosa extends Conjuro {

	public WingardiumLeviosa(Plugin plugin) {
		super(plugin, "wingardiumleviosa", "Wingardium Leviosa", Material.FEATHER,
				new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD), ChatColor.GRAY + "", Color.GRAY, 0,
				TipoProyectil.INVISIBLE);
	}

	@Override
	public boolean puedeLanzar(MagiaPlugin plugin, Player mago, Entity victima, Varita varita, double cdr,
			boolean avisar, boolean palabrasMagicas) {
		// TODO Auto-generated method stub
		return super.puedeLanzar(plugin, mago, victima, varita, varita.isHack() ? cdr - 0.8 : cdr, avisar,
				palabrasMagicas);
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento, float potencia) {
		if (victima instanceof LivingEntity) {
			int ticks = (int) (8 * potencia) + 2;
			((LivingEntity) victima).addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, ticks, 1));
			return true;
		}
		return false;
	}

}
