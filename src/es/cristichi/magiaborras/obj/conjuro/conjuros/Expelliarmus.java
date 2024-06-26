package es.cristichi.magiaborras.obj.conjuro.conjuros;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.plugin.Plugin;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.conjuro.EfectoVisual;
import es.cristichi.magiaborras.obj.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.conjuro.TiposLanzamiento;
import es.cristichi.magiaborras.obj.varita.Varita;

public class Expelliarmus extends Conjuro {

	public Expelliarmus(Plugin plugin) {
		super(plugin, "expelliarmus", "Expelliarmus", "Obliga a otro jugador a soltar lo que tenga en la mano.",
				new MaterialChoice(Material.RED_DYE), new TiposLanzamiento(TipoLanzamiento.DISTANCIA_ENTIDAD),
				new EfectoVisual[] { EfectoVisual.PARTICULAS, EfectoVisual.RAYITO }, ChatColor.RED + "", Color.RED, 200,
				"");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento) {
		if (victima instanceof HumanEntity) {
			HumanEntity victimaHumana = (HumanEntity) victima;
			ItemStack mano = victimaHumana.getInventory().getItemInMainHand();
			Item dropeado = victimaHumana.getWorld().dropItemNaturally(victimaHumana.getEyeLocation(), mano);
			if (dropeado != null) {
				dropeado.setGlowing(true);
				victimaHumana.getInventory().setItemInMainHand(null);
			}
			return true;
		} else if (victima instanceof LivingEntity) {
			LivingEntity vivito = (LivingEntity) victima;
			ItemStack mano = vivito.getEquipment().getItemInMainHand();
			Item dropeado = vivito.getWorld().dropItemNaturally(vivito.getEyeLocation(), mano);
			if (dropeado != null) {
				dropeado.setGlowing(true);
				vivito.getEquipment().setItemInMainHand(null);
				return true;
			}
		}
		return false;
	}

}
