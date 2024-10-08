package es.cristichi.magiaborras.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import es.cristichi.magiaborras.obj.conjuro.Conjuro;
import es.cristichi.magiaborras.obj.conjuro.MenuConjuros;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Accio;
import es.cristichi.magiaborras.obj.conjuro.conjuros.ArrestoMomentum;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Ascendio;
import es.cristichi.magiaborras.obj.conjuro.conjuros.AvadaKedavra;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Bombarda;
import es.cristichi.magiaborras.obj.conjuro.conjuros.BombardaMaxima;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Crucio;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Depulso;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Diffindo;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Expelliarmus;
import es.cristichi.magiaborras.obj.conjuro.conjuros.FiniteIncantatem;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Imperio;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Incendio;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Morsmordre;
import es.cristichi.magiaborras.obj.conjuro.conjuros.PetrificusTotalus;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Sectumsempra;
import es.cristichi.magiaborras.obj.conjuro.conjuros.WingardiumLeviosa;
import es.cristichi.magiaborras.obj.flu.ChimeneaFlu;
import es.cristichi.magiaborras.obj.flu.MenuRedFlu;
import es.cristichi.magiaborras.obj.flu.RedFlu;
import es.cristichi.magiaborras.obj.pocion.Caldero;
import es.cristichi.magiaborras.obj.pocion.Pocion;
import es.cristichi.magiaborras.obj.pocion.RecetaPocion;
import es.cristichi.magiaborras.obj.varita.Varita;

public class MagiaPlugin extends JavaPlugin implements Listener {
	public final Permission PERM_MAGO = new Permission("magiaborras.use");
	public final Permission PERM_CRAFT = new Permission("magiaborras.craft");
	public final Permission PERM_ADMIN = new Permission("magiaborras.admin");

	private PluginDescriptionFile desc = getDescription();

	private File archivoNumeros = new File("plugins/" + desc.getName() + "/Números Mágicos.yml");
	private File archivoChimeneasFlu = new File("plugins/" + desc.getName() + "/chimeneas.flu");

	public final static ChatColor mainColor = ChatColor.BLUE;
	public final static ChatColor textColor = ChatColor.AQUA;
	public final static ChatColor accentColor = ChatColor.GOLD;
	public final static ChatColor errorColor = ChatColor.DARK_RED;
	public static String header;

	private ArrayList<Ayuda> help;

	public Caldero caldero;
	public ArrayList<RecetaPocion> recetas;

	public RedFlu flooNetwork;

	public String invMenuName = this.getName() + " conjuros";

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

		MenuConjuros.init(this);

		{
			new Accio(this);
			new ArrestoMomentum(this);
			new AvadaKedavra(this);
			new Crucio(this);
			new Depulso(this);
			new Expelliarmus(this);
			new FiniteIncantatem(this);
			new Sectumsempra(this);
			new Imperio(this);
			new Incendio(this);
			new Morsmordre(this);
			new PetrificusTotalus(this);
			new Diffindo(this);
			new WingardiumLeviosa(this);
			new Bombarda(this);
			new BombardaMaxima(this);
			new Ascendio(this);
		}

		flooNetwork = new RedFlu();
		getServer().getPluginManager().registerEvents(flooNetwork, this);

		{
			try {
				Scanner lector = new Scanner(archivoChimeneasFlu);
				while (lector.hasNextLine()) {
					lector.nextLine(); // String lineaChimenea, simplemente es para ver más clara cada chimenea en el archivo
					String lineaNombre = lector.nextLine();
					String lineaOwner = lector.nextLine();
					String lineaLoc = lector.nextLine();
					StringTokenizer tokLoc = new StringTokenizer(lineaLoc, " ");
					Location loc = new Location(Bukkit.getWorld(tokLoc.nextToken()),
							Double.parseDouble(tokLoc.nextToken()), Double.parseDouble(tokLoc.nextToken()),
							Double.parseDouble(tokLoc.nextToken()));
					RedFlu.RED_FLU.put(lineaNombre, new ChimeneaFlu(loc, lineaNombre, lineaOwner));
				}
				lector.close();
			} catch (FileNotFoundException e) {
				try {
					archivoChimeneasFlu.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		MenuRedFlu.init(this);

		recetas = new ArrayList<>(5);

		{
			HashMap<Material, Integer> ingredientes = new HashMap<Material, Integer>(1);
			ingredientes.put(Material.APPLE, 1);
			RecetaPocion receta = new RecetaPocion(Pocion.getInvisibilidad(), ingredientes);
			recetas.add(receta);
		}

		{
			HashMap<Material, Integer> ingredientes = new HashMap<Material, Integer>(4);
			ingredientes.put(Material.WHEAT, 1);
			ingredientes.put(Material.SWEET_BERRIES, 1);
			ingredientes.put(Material.EGG, 2);
			RecetaPocion receta = new RecetaPocion(Pocion.getCervezaDeMantequilla(), ingredientes);
			recetas.add(receta);
		}

		{
			HashMap<Material, Integer> ingredientes = new HashMap<Material, Integer>(4);
			ingredientes.put(Material.FERMENTED_SPIDER_EYE, 1);
			ingredientes.put(Material.ROTTEN_FLESH, 3);
			RecetaPocion receta = new RecetaPocion(Pocion.getParalizadora(), ingredientes);
			recetas.add(receta);
		}

		{
			HashMap<Material, Integer> ingredientes = new HashMap<Material, Integer>(7);
			ingredientes.put(Material.FEATHER, 5);
			ingredientes.put(Material.LEATHER_BOOTS, 1);
			ingredientes.put(Material.BLAZE_POWDER, 1);
			RecetaPocion receta = new RecetaPocion(Pocion.getLevitadora(), ingredientes);
			recetas.add(receta);
		}

		{
			HashMap<Material, Integer> ingredientes = new HashMap<Material, Integer>(4);
			ingredientes.put(Material.GOLD_INGOT, 12);
			ingredientes.put(Material.PUMPKIN_PIE, 1);
			RecetaPocion receta = new RecetaPocion(Pocion.getFelixFelicis(), ingredientes);
			recetas.add(receta);
		}

		caldero = new Caldero(this, recetas);

		help = new ArrayList<>();
		help.add(new Ayuda("hechizos", "Muestra una lista de conjuros"));
		help.add(new Ayuda("receta", "Te muestra el crafteo de la varita mágica"));
		help.add(new Ayuda("uso", "Te explica cómo puedes usar tu varita"));
		// help.add(new Ayuda("caldero", "Te explica cómo puedes usar el caldero para hacer pociones"));
		help.add(new Ayuda("pociones", "¿Qué pociones puedo hacer y qué necesito?"));
		help.add(new Ayuda("sinergia", "Con varita en mano, te dice cuánto le gustas a la varita!"));

		getServer().getPluginManager().registerEvents(this, this);

		getLogger().info("Enabled");
	}

	@Override
	public void onDisable() {
		Varita.guardarNumeros(archivoNumeros);

		{
			try {
				archivoChimeneasFlu.delete();
				archivoChimeneasFlu.createNewFile();
				FileWriter myWriter = new FileWriter(archivoChimeneasFlu);
				for (ChimeneaFlu chi : RedFlu.RED_FLU.values()) {
					myWriter.write("---- Chimenea:");
					myWriter.write("\n");
					myWriter.write(chi.getNombre());
					myWriter.write("\n");
					myWriter.write(chi.getOwner());
					myWriter.write("\n");
					myWriter.write(chi.getLoc().getWorld().getName() + " " + chi.getLoc().getX() + " "
							+ chi.getLoc().getY() + " " + chi.getLoc().getZ() + " ");
					myWriter.write("\n");
				}
				myWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		getLogger().info("Disabled");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		label = label.toLowerCase();
		if (args.length == 0) {
			args = new String[] { "help" };
		}
		boolean bueno = true;
		switch (args[0]) {
		case "testpotion":
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
		case "obtener":
			if (sender instanceof Player && sender.hasPermission(PERM_ADMIN)) {
				Player p = (Player) sender;
				// p.getInventory()
				// .addItem(new Varita(null, Varita.getOrGenerateNumero(p), Nucleo.FIBRA_DE_CORAZON_DE_DRAGON,
				// Madera.SAUCO, Flexibilidad.FLEXIBILIDAD_MEDIA, Longitud.MUY_LARGA, null, false));
				p.getInventory().addItem(new Varita());
				p.sendMessage("<Ollivanders> De nada, feo");
			}
			break;
		case "uso":
			if (sender instanceof Player) {
				sender.sendMessage(header + "Para lanzar un " + accentColor + "Conjuro" + textColor
						+ " primero debes preparar la varita con ese " + accentColor + "Conjuro" + textColor + ". "
						+ "Para ello, simplemente usa / " + accentColor + label + "conjuros" + textColor
						+ " y selecciona el " + accentColor + "Conjuro" + textColor + " deseado. Una vez cargado el "
						+ accentColor + "Conjuro" + textColor + " en tu varita, "
						+ "puedes lanzarlo tantas veces como quieras o volver a cambiarlo de " + accentColor + "Conjuro"
						+ textColor + ".");
				sender.sendMessage("\n" + header + "Para lanzar el " + accentColor + "Conjuro" + textColor
						+ " cargado, simplemente debes hacer click derecho con tu varita en dirección al aire o apuntando a un objetivo.");
			} else {
				sender.sendMessage(header + "Lo siento, pero tú no puedes usar una varita");
			}
			break;
		case "conjuros":
		case "encantamientos":
		case "ingredientes":
		case "hechizos":
			if (sender instanceof Player) {
				Player mago = (Player) sender;
				Varita varita = Varita.esItemStackUnaVarita(mago.getInventory().getItemInMainHand());
				if (varita == null) {
					mago.sendMessage(MagiaPlugin.header
							+ "Ponte la varita en la mano anda. Si ya lo decía tu madre, que no vales pa na.");
				} else {
					int size = Conjuro.getConjuros().size();
					while (size % 9 != 0) {
						size++;
					}
					ArrayList<ItemStack> menuItems = new ArrayList<>(Conjuro.getConjuros().size());
					for (Conjuro c : Conjuro.getConjuros()) {
						List<Material> mats = c.getIngredientes().getChoices();
						ItemStack is = new ItemStack(mats.get(0));
						ItemMeta im = is.getItemMeta();
						im.setDisplayName(textColor + c.getChatColor() + c.getNombre());

						StringTokenizer st = new StringTokenizer(c.getDesc(), " ");
						ArrayList<String> lore = new ArrayList<>(st.countTokens());
						String str = "";
						while (st.hasMoreTokens()) {
							str += st.nextToken() + " ";
							if (str.length() >= 30) {
								lore.add(str.trim());
								str = "";
							}
						}
						lore.add(str.trim());
						im.setLore(lore);
						is.setItemMeta(im);
						menuItems.add(is);
					}
					ItemStack is = new ItemStack(Material.BARRIER);
					ItemMeta im = is.getItemMeta();
					im.setDisplayName(textColor + "Ninguno");
					is.setItemMeta(im);
					menuItems.add(is);
					MenuConjuros menu = new MenuConjuros(menuItems);

					menu.openInventory(mago);
				}
			} else {
				String conjuros = header + "Conjuros y sus ingredientes:";
				for (Conjuro c : Conjuro.getConjuros()) {
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
			}
			break;
		case "varita":
		case "receta":
			if (sender instanceof Player) {
				Player p = (Player) sender;
				Inventory inv = (Inventory) Bukkit.createInventory(p, InventoryType.WORKBENCH);
				String[] forma = Varita.getReceta().getShape();
				Map<Character, ItemStack> mapa = Varita.getReceta().getIngredientMap();
				ItemStack[] matrix = inv.getContents();
				matrix[0] = new Varita();
				for (int i = 0; i < 9; i++) {
					matrix[i + 1] = mapa.get(forma[i / 3].charAt(i % 3));
				}
				inv.setContents(matrix);
				p.openInventory(inv);
			}
			break;
		case "pociones":
			String msg = header + "Pociones disponibles:";
			// TODO hacer que los items repetidos salgan como x5 en vez de el item 5 veces
			for (RecetaPocion receta : recetas) {
				msg += textColor + "\nPara hacer " + mainColor + receta.getResultado().getNombre() + textColor
						+ " necesitas:\n";

				int cont = 0;
				int size = receta.getMateriales().size();
				for (Map.Entry<Material, Integer> ingrediente : receta.getMateriales().entrySet()) {
					if (cont != 0) {
						if (cont == size - 1) {
							msg += textColor + " y ";
						} else {
							msg += textColor + ", ";
						}
					}
					cont++;

					msg += accentColor + ingrediente.getKey().name().toLowerCase().replace("_", " ")
							+ (ingrediente.getValue() > 1 ? " x" + ingrediente.getValue() : "");
				}

			}
			sender.sendMessage(msg);
			break;
		case "sinergia":
			if (sender instanceof Player) {
				Player p = (Player) sender;
				Varita varita = Varita.esItemStackUnaVarita(p.getInventory().getItemInMainHand());
				if (varita == null) {
					p.sendMessage(header + "Debe tener una varita en su mano para comprobar su sinergia con ella.");
				} else {
					float numP = varita.getPotencia(p);
					p.sendMessage(header + "Su varita y usted están compenetrados al " + (int) (numP * 100) + "%");
				}
			}
			break;
		case "cheat":
		case "hack935jejenummagico":
			if (sender instanceof Player) {
				Player mago = (Player) sender;
				Varita varita = Varita.esItemStackUnaVarita(mago.getInventory().getItemInMainHand());
				if (varita == null) {
					mago.sendMessage(header + "Debe tener una varita en su mano para comprobar su sinergia con ella.");
				} else {
					// float numV = varita.getNumeroMagico();
					// mago.sendMessage(header + "Su varita tenía el número mágico " + numV);
					float numP = Varita.getOrGenerateNumero(mago);
					if (args.length == 1 || args[1] == "100") {
						varita.setNumeroMagico(numP);
					} else {
						varita.setNumeroMagico(Float.parseFloat(args[1]));
					}
					// mago.sendMessage(header + "Y ahora tiene el número mágico " + varita.getNumeroMagico() + " - "+varita.getPotencia(mago)+"% potencia.");
					varita.cambiarConjuro(null);
					varita.setHack(true);
					mago.getInventory().setItemInMainHand(varita);
				}
			}
			break;
		case "recargarinfo":
			if (sender instanceof Player) {
				Player p = (Player) sender;
				Varita varita = Varita.esItemStackUnaVarita(p.getInventory().getItemInMainHand());
				if (varita != null) {
					varita.recagarDatos();
					p.getInventory().setItemInMainHand(varita);
				} else {
					p.sendMessage(header + "Eso no es una varita");
				}
			}
			break;
		//
		// case "oscura":
		// case "oscuro":
		// case "malo":
		// case "maloso":
		// if (sender instanceof Player) {
		// Player p = (Player) sender;
		// Varita varita = Varita.convertir(p.getInventory().getItemInMainHand());
		// if (varita == null) {
		// p.sendMessage(
		// header + "Debe usted tener una varita en su mano para comprobar su sinergia con ella.");
		// } else if (varita.isHack()) {
		// p.sendMessage(header + "La varita ya está tornada a las artes oscuras.");
		// } else {
		// varita.setHack(true);
		// p.sendMessage(header + "La varita está ahora " + ChatColor.BLACK + "a merced de tu oscuridad"
		// + textColor + ".");
		// p.getInventory().setItemInMainHand(varita);
		// }
		// }
		// break;

		default:
			bueno = false;
			break;
		}
		return bueno;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		switch (args.length) {
		case 1:
			if ("help".startsWith(args[0]))
				list.add("help");
			if ("uso".startsWith(args[0]))
				list.add("uso");
			if ("hechizos".startsWith(args[0]))
				list.add("hechizos");
			if ("pociones".startsWith(args[0]))
				list.add("pociones");
			if ("sinergia".startsWith(args[0]))
				list.add("sinergia");
			if (sender.hasPermission(PERM_CRAFT)) {
				if ("receta".startsWith(args[0]))
					list.add("receta");
			}
			if (sender.hasPermission(PERM_ADMIN)) {
				if ("recargarinfo".startsWith(args[0]))
					list.add("recargarinfo");
				if ("cheat".startsWith(args[0]))
					list.add("cheat");
				if ("obtener".startsWith(args[0]))
					list.add("obtener");
			}
			break;
		default:
			break;
		}
		return list;
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