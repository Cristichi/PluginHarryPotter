package main;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import obj.Varita;
import obj.Varita.Flexibilidad;
import obj.Varita.Madera;
import obj.Varita.Nucleo;

public class MagiaPlugin extends JavaPlugin implements Listener {
	private PluginDescriptionFile desc = getDescription();

	private final ChatColor mainColor = ChatColor.BLUE;
	private final ChatColor textColor = ChatColor.AQUA;
	private final ChatColor accentColor = ChatColor.DARK_AQUA;
//	private final ChatColor errorColor = ChatColor.DARK_RED;
	private final String header = mainColor + "[" + desc.getName() + "] " + textColor;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new Varita.VaritaListener(), this);
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("Enabled");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		label = label.toLowerCase();
		if (args.length==0) {
			return false;
		}
		boolean bueno = true;
		switch (args[0]) {
		case "help":
			sender.sendMessage(header+"Sin ayuda "+accentColor+":D");
			break;
		case "dame":
			if (sender instanceof Player) {
				Player p = (Player) sender;
				Varita varita = new Varita(this, Nucleo.PLUMA_DE_FENIX, Madera.ABEDUL, Flexibilidad.MUY_FLEXIBLE);
				varita.setType(Material.DIAMOND_BLOCK);
				p.getInventory().addItem(varita);
				p.sendMessage("Palito :D");
			}
			break;

		default:
			bueno = false;
			break;
		}
		return bueno;
	}
}
