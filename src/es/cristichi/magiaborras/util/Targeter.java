package es.cristichi.magiaborras.util;

import java.util.function.Predicate;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class Targeter {
	private static double RANGO_MAX = 50000;

	public static RayTraceResult getTargetEntity(final Player mago) {
		return getTarget(mago, RANGO_MAX);
	}

	private static RayTraceResult getTarget(final Player mago, double rango) {
		final Location ojo = mago.getEyeLocation();
		return mago.getWorld().rayTrace(ojo, ojo.getDirection(), rango, FluidCollisionMode.NEVER, true, 0.2,
				new Predicate<Entity>() {

					@Override
					public boolean test(Entity t) {
						return t != mago;
					}
				});
	}

}