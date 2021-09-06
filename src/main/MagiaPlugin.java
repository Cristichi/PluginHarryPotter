package main;

import java.io.File;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import obj.Caldero;
import obj.Pocion;
import obj.RecetaPocion;
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
	public static String header;

	private ArrayList<Ayuda> help;

	public Caldero caldero;
	public ArrayList<RecetaPocion> recetas;

	@Override
	public void onEnable() {
		header = mainColor + "[" + desc.getName() + "] " + textColor;
		Pocion.Init(this);
		Varita.Init(this);
		try {
			Varita.cargarNumeros(archivoNumeros);
		} catch (FileSystemException e) {
			e.printStackTrace();
		}

		recetas = new ArrayList<>(1);

		{
			ArrayList<Material> ingredientes = new ArrayList<>(1);
			ingredientes.add(Material.APPLE);
			RecetaPocion receta = new RecetaPocion(Pocion.getInvisibilidad(), ingredientes);
			recetas.add(receta);
		}

		{
			ArrayList<Material> ingredientes = new ArrayList<>(4);
			ingredientes.add(Material.WHEAT);
			ingredientes.add(Material.SWEET_BERRIES);
			ingredientes.add(Material.EGG);
			ingredientes.add(Material.EGG);
			RecetaPocion receta = new RecetaPocion(Pocion.getCervezaDeMantequilla(), ingredientes);
			recetas.add(receta);
		}

		{
			ArrayList<Material> ingredientes = new ArrayList<>(4);
			ingredientes.add(Material.FERMENTED_SPIDER_EYE);
			ingredientes.add(Material.ROTTEN_FLESH);
			ingredientes.add(Material.ROTTEN_FLESH);
			ingredientes.add(Material.ROTTEN_FLESH);
			RecetaPocion receta = new RecetaPocion(Pocion.getParalizadora(), ingredientes);
			recetas.add(receta);
		}

		{
			ArrayList<Material> ingredientes = new ArrayList<>(7);
			ingredientes.add(Material.FEATHER);
			ingredientes.add(Material.FEATHER);
			ingredientes.add(Material.FEATHER);
			ingredientes.add(Material.FEATHER);
			ingredientes.add(Material.FEATHER);
			ingredientes.add(Material.LEATHER_BOOTS);
			ingredientes.add(Material.BLAZE_POWDER);
			RecetaPocion receta = new RecetaPocion(Pocion.getLevitadora(), ingredientes);
			recetas.add(receta);
		}

		{
			ArrayList<Material> ingredientes = new ArrayList<>(4);
			ingredientes.add(Material.GOLD_INGOT);
			ingredientes.add(Material.GOLD_INGOT);
			ingredientes.add(Material.GOLD_INGOT);
			ingredientes.add(Material.GOLD_INGOT);
			ingredientes.add(Material.GOLD_INGOT);
			ingredientes.add(Material.GOLD_INGOT);
			ingredientes.add(Material.GOLD_INGOT);
			ingredientes.add(Material.GOLD_INGOT);
			ingredientes.add(Material.GOLD_INGOT);
			ingredientes.add(Material.GOLD_INGOT);
			ingredientes.add(Material.GOLD_INGOT);
			ingredientes.add(Material.GOLD_INGOT);
			ingredientes.add(Material.PUMPKIN_PIE);
			RecetaPocion receta = new RecetaPocion(Pocion.getFelixFelicis(), ingredientes);
			recetas.add(receta);
		}

		caldero = new Caldero(this, recetas);

		help = new ArrayList<>();
		help.add(new Ayuda("conjuros", "Muestra una lista de conjuros"));
		help.add(new Ayuda("receta", "Te muestra el crafteo de la varita mágica"));
		help.add(new Ayuda("uso", "Te explica cómo puedes usar tu varita"));
//		help.add(new Ayuda("caldero", "Te explica cómo puedes usar el caldero para hacer pociones"));

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
		case "test":
			if (sender instanceof Player) {
				if (args.length == 1) {
					((Player) sender).getInventory().addItem(Pocion.get("solitario"));
				} else {
					Pocion pot = Pocion.get(args[1]);
					if (pot == null) {
						sender.sendMessage(header + "No existe esa poción.");
					} else
						((Player) sender).getInventory().addItem(pot);
				}
			}
			break;
		case "version":
			sender.sendMessage(header + desc.getFullName());
			break;
		case "help":
			sender.sendMessage(header + "Comandos:");
			for (Ayuda ayuda : help) {
				sender.sendMessage(ayuda.toString());
			}
			break;
		case "give":
			if (sender instanceof Player) {
				Player p = (Player) sender;
//				p.getInventory()
//						.addItem(new Varita(null, Varita.getOrGenerateNumero(p), Nucleo.FIBRA_DE_CORAZON_DE_DRAGON,
//								Madera.SAUCO, Flexibilidad.FLEXIBILIDAD_MEDIA, Longitud.MUY_LARGA, null, false));
				p.getInventory().addItem(new Varita());
				p.sendMessage("<Ollivanders> De nada, feo");
			}
			break;
		case "uso":
			if (sender instanceof Player) {
				sender.sendMessage(header + "Para lanzar un " + accentColor + "Conjuro" + textColor
						+ " primero debes preparar la varita con ese " + accentColor + "Conjuro" + textColor + ". "
						+ "Para ello, coloca tu varita en la mano principal y el ingrediente del " + accentColor
						+ "Conjuro" + textColor + " en la otra, " + "y después pulsa " + accentColor + "F" + textColor
						+ " para intercambiarlos. Una vez cargado el " + accentColor + "Conjuro" + textColor
						+ " en tu varita, " + "puedes lanzarlo tantas veces como quieras o volver a cambiarlo de "
						+ accentColor + "Conjuro" + textColor + ".");
				sender.sendMessage("\n" + header + "Para ver qué ingrediente necesitas para preparar un " + accentColor
						+ "Conjuro" + textColor + ", recuerda usar /" + accentColor + label + " conjuros" + textColor);
				sender.sendMessage("\n" + header + "Una vez preparado tu " + accentColor + "Conjuro" + textColor + ", "
						+ "para lanzarlo sólo debes hacer click derecho con tu varita en dirección a un mob o jugador. "
						+ "Recuerda que algunos " + accentColor + "Conjuros" + textColor
						+ " podrían lanzarse de una manera diferente.");
			} else {
				sender.sendMessage(header + "Lo siento, pero tú no puedes usar una varita");
			}
			break;
		case "conjuros":
		case "encantamientos":
		case "ingredientes":
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
//				System.out.println("forma: " + forma);
				System.out.println("mapa keys: " + mapa.keySet());
//				System.out.println("mapa vals: " + mapa.values());
				for (int i = 0; i < 9; i++) {
//					System.out.println(mapa.get(forma[i / 3].charAt(i % 3)));
					matrix[i + 1] = mapa.get(forma[i / 3].charAt(i % 3));
				}
				inv.setContents(matrix);
				p.openInventory(inv);
			}
			break;
		case "pociones":
			String msg = header + "Pociones disponibles:";
			Set<Entry<String, Pocion>> pociones = Pocion.getPociones().entrySet();
			for (Iterator<Entry<String, Pocion>> it = pociones.iterator(); it.hasNext();) {
				Entry<String, Pocion> entry = it.next();
				msg += "\n" + ChatColor.DARK_GREEN + entry.getKey() + ": " + entry.getValue().getNombre();
			}
			sender.sendMessage(msg);
			break;
		case "sinergia":
			if (sender instanceof Player) {
				Player p = (Player) sender;
				Varita varita = Varita.convertir(p.getInventory().getItemInMainHand());
				if (varita == null) {
					p.sendMessage(header + "Debe tener una varita en su mano para comprobar su sinergia con ella.");
				} else {
					float numP = varita.getPotencia(p);
					p.sendMessage(header + "Su varita y usted están compenetrados al "
							+ (int) (numP * 100) + "%");
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
				} else {
					p.sendMessage(header+"Eso no es una varita");
				}
			}
			break;
//
//		case "oscura":
//		case "oscuro":
//		case "malo":
//		case "maloso":
//			if (sender instanceof Player) {
//				Player p = (Player) sender;
//				Varita varita = Varita.convertir(p.getInventory().getItemInMainHand());
//				if (varita == null) {
//					p.sendMessage(
//							header + "Debe usted tener una varita en su mano para comprobar su sinergia con ella.");
//				} else if (varita.isHack()) {
//					p.sendMessage(header + "La varita ya está tornada a las artes oscuras.");
//				} else {
//					varita.setHack(true);
//					p.sendMessage(header + "La varita está ahora " + ChatColor.BLACK + "a merced de tu oscuridad"
//							+ textColor + ".");
//					p.getInventory().setItemInMainHand(varita);
//				}
//			}
//			break;

		default:
			bueno = false;
			break;
		}
		return bueno;
	}

	class Ayuda {
		private String comando;
		private String descripcion;

		public Ayuda(String comando, String descripcion) {
			this.comando = comando;
			this.descripcion = descripcion;
		}

		public String getComando() {
			return comando;
		}

		public void setComando(String comando) {
			this.comando = comando;
		}

		public String getDescripcion() {
			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}

		@Override
		public String toString() {
			return textColor + " /" + accentColor + "magia " + comando + textColor + ": " + descripcion + ".";
		}
	}
}