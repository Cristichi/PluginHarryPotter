package es.cristichi.magiaborras.obj.conjuro.conjuros;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.conjuro.EfectoVisual;
import es.cristichi.magiaborras.obj.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.conjuro.TiposLanzamiento;
import es.cristichi.magiaborras.obj.varita.Varita;

public class Imperio extends Conjuro {

	public Imperio(Plugin plugin) {
		super(plugin, "imperio", "Imperio",
				"La maldición imperius. Hace que los aldeanos te vendan barato, que los animales te sigan, que los mobs te ignoren, o putea jugadores un poco.",
				new MaterialChoice(Material.COMMAND_BLOCK_MINECART),
				new TiposLanzamiento(TipoLanzamiento.CERCA_ENTIDAD, TipoLanzamiento.GOLPE),
				new EfectoVisual[] { EfectoVisual.PARTICULAS, EfectoVisual.RAYITO },
				ChatColor.GRAY + "" + ChatColor.BOLD, Color.fromRGB(200, 200, 200), 18000, "");
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
		} else if (victima instanceof Player) {
			Player magoVictima = (Player) victima;
			if (magoVictima.getGameMode().equals(GameMode.SURVIVAL)) {
				int duracion = (int) (600 * varita.getPotencia(mago));
				magoVictima.setGameMode(GameMode.ADVENTURE);
				magoVictima.eject();
				magoVictima.closeInventory();
				magoVictima.dropItem(true);
				magoVictima.leaveVehicle();
				magoVictima.setCanPickupItems(false);
				magoVictima.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duracion, 255, true, false, false));
				float velocidad = magoVictima.getWalkSpeed();
				magoVictima.setWalkSpeed(-1);

				Bukkit.getScheduler().runTaskLater(plugin, new Consumer<BukkitTask>() {
					@Override
					public void accept(BukkitTask t) {
						magoVictima.setCanPickupItems(true);
						magoVictima.setGameMode(GameMode.SURVIVAL);
						magoVictima.setWalkSpeed(velocidad);

					}
				}, duracion);
				return true;
			}
		} else if (victima instanceof Animals) {
			Animals animalico = (Animals) victima;
			animalico.setTarget(mago);
			return true;
		} else if (victima instanceof Monster) {
			Monster monstro = (Monster) victima;
			monstro.setTarget(null);
			return true;
		}
		return false;
	}
}
