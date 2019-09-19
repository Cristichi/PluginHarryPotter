package main;

import java.io.File;
import java.nio.file.FileSystemException;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import obj.Varita;
import obj.Varita.Conjuro;

public class MagiaPlugin extends JavaPlugin implements Listener {
	public Permission USE = new Permission("magiaborras.use");
	public Permission CREATE = new Permission("magiaborras.create");
	public Permission ADMIN = new Permission("magiaborras.admin");
	private PluginDescriptionFile desc = getDescription();

	private File archivoNumeros = new File("plugins/" + desc.getName() + "/Números Mágicos.yml");

	public final ChatColor mainColor = ChatColor.BLUE;
	public final ChatColor textColor = ChatColor.AQUA;
	public final ChatColor accentColor = ChatColor.GOLD;
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
		if (args.length == 0) {
			return false;
		}
		boolean bueno = true;
		switch (args[0]) {
		case "help":
			sender.sendMessage(header + "Sin ayuda " + accentColor + ":D");
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
		case "conjuros":
		case "encantamientos":
		case "hechizos":
			String conjuros = header + "Conjuros y sus ingredientes:";
			for (Conjuro c : Conjuro.values()) {
				String ing = "";
				List<Material> mats = c.getIngredientes().getChoices();
				for (int i = 0; i < mats.size(); i++) {
					ing += accentColor + mats.get(i).toString().toLowerCase().replace("_", " ");
					if (i == mats.size() - 2) {
						ing += textColor + " o " + textColor;
					} else if (i != mats.size() - 1) {
						ing += textColor + ", " + textColor;
					}
				}
				conjuros += "\n " + textColor + "[" + c.getChatColor() + c.getNombre() + textColor + "]: " + ing;
			}
			sender.sendMessage(conjuros);
			break;
		case "varita":
		case "receta":
			if (sender instanceof Player) {
				Player p = (Player) sender;
				CraftInventoryCustom inv = (CraftInventoryCustom) Bukkit.createInventory(p, InventoryType.WORKBENCH);
				String[] forma = Varita.getReceta().getShape();
				Map<Character, ItemStack> mapa = Varita.getReceta().getIngredientMap();
				ItemStack[] matrix = inv.getContents();
				matrix[0] = new Varita();
				System.out.println("forma: " + forma);
				System.out.println("mapa keys: " + mapa.keySet());
				System.out.println("mapa vals: " + mapa.values());
				for (int i = 0; i < 9; i++) {
					System.out.println(mapa.get(forma[i / 3].charAt(i % 3)));
					matrix[i + 1] = mapa.get(forma[i / 3].charAt(i % 3));
				}
				inv.setContents(matrix);
				p.openInventory(inv);
			}
			break;
		case "test":
			if (sender instanceof Player) {
				Player p = (Player) sender;
				Varita varita = Varita.convertir(p.getInventory().getItemInMainHand());
				if (varita == null) {
					p.sendMessage(header + "Debe tener una varita en su mano para comprobar su sinergia con ella.");
				} else {
					float numP = Varita.getOrGenerateNumero(p);
					p.sendMessage(header + "Su varita y usted están compenetrados al "
							+ (int) ((1 - Math.abs(numP - varita.getNumeroMagico())) * 100) + "%");
				}
			}
			break;
		case "recargarinfo":
			if (sender instanceof Player) {
				Player p = (Player) sender;
				Varita varita = Varita.convertir(p.getInventory().getItemInMainHand());
				if (varita != null) {
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

	@EventHandler
	private void onCrafteo(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if (inv instanceof CraftInventoryCustom) {
			if (Varita.convertir(inv.getItem(0)) != null) {
				e.setCancelled(true);
			}
		}
	}
}
