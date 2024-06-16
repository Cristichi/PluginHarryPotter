package es.cristichi.magiaborras.obj.varita.conjuro.conjuros;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.varita.Varita;
import es.cristichi.magiaborras.obj.varita.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.varita.conjuro.EfectoVisual;
import es.cristichi.magiaborras.obj.varita.conjuro.TipoLanzamiento;
import es.cristichi.magiaborras.obj.varita.conjuro.TiposLanzamiento;

public class Morsmordre extends Conjuro {

	public Morsmordre(Plugin plugin) {
		super(plugin, "morsmordre", "Morsmordre", "¿Sos malvado? A ver la serpiente, a ver que la vea.",
				new MaterialChoice(Material.TOTEM_OF_UNDYING), new TiposLanzamiento(TipoLanzamiento.AREA_MAGO),
				new EfectoVisual[] { EfectoVisual.PARTICULAS }, ChatColor.DARK_GREEN + "", Color.GREEN, 60000, "");
	}

	@Override
	public boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento) {
		Location loc = mago.getLocation().add(0, 50, 0);
		World mundo = loc.getWorld();
		ArmorStand vfx = (ArmorStand) mundo.spawnEntity(loc, EntityType.ARMOR_STAND);
		vfx.setVisible(false);
		vfx.setCollidable(false);
		vfx.setInvulnerable(true);
		Collection<? extends Player> ps = Bukkit.getOnlinePlayers();
		for (Player player : ps) {
			if (player.getWorld().getName().equals(mundo.getName())) {
				player.sendMessage(MagiaPlugin.header + "¡Alguien ha invocado la Marca Tenebrosa!");
			}
		}

		int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			boolean alt = true;

			@Override
			public void run() {
				int lados = alt ? 2 : 4;
				int alto = alt ? 3 : -5;
				Location loc2 = loc.clone().add(Math.random() * lados * 2, Math.random() * alto * 2,
						Math.random() * lados * 2);
				Firework fw = (Firework) mundo.spawnEntity(loc2, EntityType.FIREWORK_ROCKET);
				FireworkMeta fwm = fw.getFireworkMeta();
				FireworkEffect effect = FireworkEffect.builder()
						.withColor(alt ? Color.GREEN : Math.random() > 0.5 ? Color.GREEN : Color.BLACK)
						.withFade(Color.BLACK).with(alt ? FireworkEffect.Type.CREEPER : FireworkEffect.Type.BURST)
						.trail(false).flicker(false).build();
				fwm.addEffect(effect);
				fw.setFireworkMeta(fwm);
				fw.setSilent(true);
				fw.detonate();
				alt = !alt;
				mundo.setTime(13500);
			}
		}, 0, 1);
		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				Bukkit.getScheduler().cancelTask(id);
				vfx.remove();
			}
		}, getCooldownTicks());

		vfx.getPersistentDataContainer().set(new NamespacedKey(plugin, "efectoMorsmordre"), PersistentDataType.INTEGER,
				id);
		return true;
	}

}
