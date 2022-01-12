package util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Reflection {
	public static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception ex) {
		}
	}

	public static Class<?> getNMSClass(String name) {
		try {
			return Class.forName("net.minecraft.server"
					+ Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
//			System.out.println("net.minecraft.server"
//					+ Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
//			return Class.forName("net.minecraft.server.v1_18_R1." + name);
		} catch (ClassNotFoundException ex) {
		}
		return null;
	}
}
