package es.cristichi.magiaborras.obj.varita.conjuro.conjuros;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.varita.Varita;
import es.cristichi.magiaborras.obj.varita.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.varita.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.varita.conjuro.TipoProyectil;
import es.cristichi.magiaborras.obj.varita.conjuro.TiposLanzamiento;

public class Expelliarmus extends Conjuro {

	public Expelliarmus(Plugin plugin) {
		super(plugin, "expelliarmus", "Expelliarmus", Material.RED_DYE,
				new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD), ChatColor.RED + "", Color.RED, 300,
				TipoProyectil.COHETE);
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
		if (victima instanceof HumanEntity) {
			Random rng = new Random();
			HumanEntity victimaHumana = (HumanEntity) victima;
			ItemStack mano = victimaHumana.getInventory().getItemInMainHand();
			Item dropeado = victimaHumana.getWorld().dropItemNaturally(
					victimaHumana.getEyeLocation().add(rng.nextDouble() * (rng.nextBoolean() ? -3 : 3), 1,
							rng.nextDouble() * (rng.nextBoolean() ? -3 : 3)),
					mano);
			dropeado.setGlowing(true);
			victimaHumana.getInventory().setItemInMainHand(null);
			resetTiempoPalabras(mago);
			return true;
		}
		return false;
	}

}
