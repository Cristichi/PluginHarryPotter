package es.cristichi.magiaborras.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
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
import es.cristichi.magiaborras.obj.conjuro.conjuros.AvadaKedavra;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Crucio;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Depulso;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Expelliarmus;
import es.cristichi.magiaborras.obj.conjuro.conjuros.FiniteIncantatem;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Imperio;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Incendio;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Morsmordre;
import es.cristichi.magiaborras.obj.conjuro.conjuros.PetrificusTotalus;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Sectumsempra;
import es.cristichi.magiaborras.obj.conjuro.conjuros.Stupify;
import es.cristichi.magiaborras.obj.conjuro.conjuros.WingardiumLeviosa;
import es.cristichi.magiaborras.obj.flu.ChimeneaFlu;
import es.cristichi.magiaborras.obj.flu.MenuRedFlu;
import es.cristichi.magiaborras.obj.flu.RedFlu;
import es.cristichi.magiaborras.obj.pocion.Caldero;
import es.cristichi.magiaborras.obj.pocion.Pocion;
import es.cristichi.magiaborras.obj.pocion.RecetaPocion;
import es.cristichi.magiaborras.obj.varita.Varita;

public class MagiaPlugin extends JavaPlugin implements Listener {
	public Permission USE = new Permission("magiaborras.use");
	public Permission CREATE = new Permission("magiaborras.create");
	public Permission ADMIN = new Permission("magiaborras.admin");

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
			new Imperio(this);
			new Incendio(this);
			new Morsmordre(this);
			new PetrificusTotalus(this);
			new Sectumsempra(this);
			new Stupify(this);
			new WingardiumLeviosa(this);
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
			for (RecetaPocion r : recetas) {
				msg += textColor + "\nPara hacer " + mainColor + r.getResultado().getNombre() + textColor
						+ " necesitas:\n";
				for (int i = 0; i < r.getMateriales().size(); i++) {
					if (i != 0) {
						if (i == r.getMateriales().size() - 1) {
							msg += textColor + " y ";
						} else {
							msg += textColor + ", ";
						}
					}
					msg += accentColor + r.getMateriales().get(i).name().toLowerCase().replace("_", " ");
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