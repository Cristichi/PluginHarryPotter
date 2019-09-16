package obj;

import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMagico extends JavaPlugin{
	public Permission USE;
	public Permission CREATE;
	public Permission NO_CD;
	public Permission ADMIN;

	public ChatColor mainColor;
	public ChatColor textColor;
	public ChatColor accentColor;
	public ChatColor errorColor;
	public String header;
}
