package main;

import java.io.File;
import java.nio.file.FileSystemException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import obj.Varita;

public class MagiaPlugin extends JavaPlugin implements Listener {
	public final Permission USE = new Permission("magiaborras.use");
	public final Permission CREATE = new Permission("magiaborras.create");
	public final Permission NO_CD = new Permission("magiaborras.nocd");
	public final Permission ADMIN = new Permission("magiaborras.admin");
	private PluginDescriptionFile desc = getDescription();

	private File archivoNumeros = new File("plugins/"+desc.getName()+"/Números Mágicos.yml");
	
	public final ChatColor mainColor = ChatColor.BLUE;
	public final ChatColor textColor = ChatColor.AQUA;
	public final ChatColor accentColor = ChatColor.DARK_AQUA;
	public final ChatColor errorColor = ChatColor.DARK_RED;
	public final String header = mainColor + "[" + desc.getName() + "] " + textColor;

	@Override
	public void onEnable() {
		Varita.Init(this);
		try {
			Varita.cargarNumeros(archivoNumeros);
		} catch (FileSystemException e) {
			e.printStackTrace();
		}
		getServer().getPluginManager().registerEvents(new Varita.VaritaListener(), this);
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("Enabled");
	}
	
	@Override
	public void onDisable() {
		Varita.guardarNumeros(archivoNumeros);
		getLogger().info("Disabled");
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
//				p.getInventory().addItem(new Varita(Nucleo.PLUMA_DE_FENIX, Madera.ABEDUL, Flexibilidad.MUY_FLEXIBLE, Longitud.MUY_LARGA, Conjuro.AVADA_KEDAVRA));
				p.getInventory().addItem(new Varita());
//				p.getInventory().addItem(new Varita(new Random(115)));
				p.sendMessage("<Ollivanders> De nada, feo");
			}
			break;
		case "test":
			if (sender instanceof Player) {
				Player p = (Player) sender;
				Varita varita = Varita.convertir(this, p.getInventory().getItemInMainHand());
				if (varita==null) {
					p.sendMessage(header+"No es una varita: "+p.getInventory().getItemInMainHand());
				}else {
					p.sendMessage(header+"Es una varita de número: "+varita.getNumeroMagico());
					p.sendMessage(header+"Y tu número es: "+Varita.numerosMagicos.getOrDefault(p.getUniqueId(), -1F));
				}
			}
			break;
		case "recargarinfo":
			if (sender instanceof Player) {
				Player p = (Player) sender;
				Varita varita = Varita.convertir(this, p.getInventory().getItemInMainHand());
				if (varita!=null) {
					varita.recagarDatos();
					p.getInventory().setItemInMainHand(varita);
				}
			}
			break;

		default:
			bueno = false;
			break;
		}
		return bueno;
	}
}
