package es.cristichi.magiaborras.obj.varita.conjuro;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import es.cristichi.magiaborras.main.MagiaPlugin;
import es.cristichi.magiaborras.obj.varita.Varita;

public abstract class Conjuro {

	private static HashMap<String, Conjuro> CONJUROS = new HashMap<>(20);
	
	@Nullable
	public static Conjuro getConjuro(String id) {
		return CONJUROS.get(id);
	}
	
	public static Collection<Conjuro> getConjuros() {
		return CONJUROS.values();
	}
	
	protected String id;
	protected String nombre;
	protected MaterialChoice ingredientes;
	protected TiposLanzamiento tiposLanzamiento;
	protected TipoProyectil tipoProyectil;
	protected String chatColor;
	protected Color color;
	protected int cooldownTicks;
	private String palabras;
	private String metaFlechaNombre;
	private FixedMetadataValue metaFlecha;
	protected HashMap<UUID, Integer> cds = new HashMap<>();
	private HashMap<UUID, Integer> mensajes = new HashMap<>();
	private HashMap<UUID, Integer> mensajesPalabrasMagicas = new HashMap<>();
	protected static int cdMensajeCd = 20;
	protected static int cdMensajePalabrasMagicas = 40;

	protected Conjuro(Plugin plugin, String id, String nombre, MaterialChoice ingredientes, TiposLanzamiento tiposLanzamiento,
			String chatColor, Color color, int cooldownTicks, TipoProyectil tipoProyectil) {
		
		this(plugin, id, nombre, ingredientes, tiposLanzamiento, chatColor, color, cooldownTicks,
				ChatColor.RESET + "¡{chatcolor}{nombre}" + ChatColor.RESET + "!", tipoProyectil);
	}

	protected Conjuro(Plugin plugin, String id, String nombre, Material ingrediente, TiposLanzamiento tiposLanzamiento,
			String chatColor, Color color, int cooldownTicks, TipoProyectil tipoProyectil) {
		
		this(plugin, id, nombre, new MaterialChoice(ingrediente), tiposLanzamiento, chatColor, color, cooldownTicks,
				tipoProyectil);
	}

	protected Conjuro(Plugin plugin, String id, String nombre, Material ingrediente, TiposLanzamiento tiposLanzamiento,
			String chatColor, Color color, int cooldownTicks, String palabrasMagicas, TipoProyectil tipoProyectil) {
		
		this(plugin, id, nombre, new MaterialChoice(ingrediente), tiposLanzamiento, chatColor, color, cooldownTicks,
				palabrasMagicas, tipoProyectil);
	}

	/**
	 * Para las palabras mágicas se puede usar:<br>
	 * {nombre} Para el nombre del Conjuro<br>
	 * {chatcolor} Para el color del Conjuro<br>
	 * {atacante} Para el nombre del mago atacante<br>
	 * 
	 * @param ingredientes
	 * @param tiposLanzamiento
	 * @param chatColor
	 * @param color
	 * @param cooldownTicks
	 * @param palabrasMagicas
	 * @param tipoProyectil    El tipo de proyectil que usa el hechizo
	 */
	protected Conjuro(Plugin plugin, String id, String nombre, MaterialChoice ingredientes, TiposLanzamiento tiposLanzamiento,
			String chatColor, Color color, int cooldownTicks, String palabrasMagicas, TipoProyectil tipoProyectil) {
		this.id = id;
		this.nombre = nombre;
		char[] cs = nombre.toCharArray();
		boolean nextMayus = true;
		for (int i = 0; i < cs.length; i++) {
			char c = cs[i];
			if (c == ' ') {
				nextMayus = true;
			} else if (nextMayus) {
				cs[i] = Character.toUpperCase(c);
				nextMayus = false;
			}
		}
		nombre = new String(cs);
		this.chatColor = chatColor;
		this.tiposLanzamiento = tiposLanzamiento;
		this.tipoProyectil = tipoProyectil;
		this.color = color;
		this.cooldownTicks = cooldownTicks;
		this.ingredientes = ingredientes;
		this.palabras = palabrasMagicas;
		metaFlechaNombre = id;
		metaFlecha = new FixedMetadataValue(plugin, new FixedMetadataValue(plugin, true));
		
		CONJUROS.put(id, this);
	}
	
	public String getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public boolean isTipoLanzamiento(TipoLanzamiento tipo) {
		return tiposLanzamiento.contains(tipo);
	}

	public boolean isTipoProyectil(TipoProyectil tipo) {
		return tipoProyectil.equals(tipo);
	}

	public String getChatColor() {
		return chatColor;
	}

	public Color getColor() {
		return color;
	}

	public int getCooldownTicks() {
		return cooldownTicks;
	}

	public MaterialChoice getIngredientes() {
		return ingredientes;
	}

	public String getPalabrasMagicas(String atacante) {
		if (palabras == null) {
			return null;
		}
		return palabras.replace("{nombre}", nombre)
				.replace("{chatcolor}", chatColor)
				.replace("{atacante}", atacante)
				;
	}

	public String getMetaNombre() {
		return metaFlechaNombre;
	}

	public FixedMetadataValue getMetaFlecha() {
		return metaFlecha;
	}

	public void resetTiempoPalabras(Player p) {
		mensajesPalabrasMagicas.remove(p.getUniqueId());
	}

	public void ponerEnCD(Player p) {
		cds.put(p.getUniqueId(), p.getTicksLived());
	}

	public boolean puedeLanzar(MagiaPlugin plugin, Player mago, Entity victima, Varita varita, double cdr,
			boolean avisar, boolean palabrasMagicas) {
		boolean puede = true;
		int ticks = mago.getTicksLived();
		if (mago.hasPermission(plugin.USE)) {
			if (cooldownTicks > 0) {
				if (cds.containsKey(mago.getUniqueId())) {
					int ticksObj = cds.get(mago.getUniqueId()) + cooldownTicks - (int) (cooldownTicks * cdr);
					if (ticksObj > ticks) {
						puede = false;
						if (avisar) {
							int espera = (int) ((ticksObj - ticks) / 20);
							if (!mensajes.containsKey(mago.getUniqueId())
									|| mensajes.get(mago.getUniqueId()) + cdMensajeCd <= ticks) {
								mago.sendMessage(MagiaPlugin.header + "Debes esperar " + plugin.accentColor + espera
										+ plugin.textColor + " segundos para volver a lanzar " + chatColor + toString()
										+ plugin.textColor + ".");
								mensajes.put(mago.getUniqueId(), ticks);
							}
						}
					}
				}
			}
		} else {
			puede = false;
			if (avisar)
				mago.sendMessage(MagiaPlugin.header + plugin.errorColor + "No puedes usar Magia.");
		}
		if (palabrasMagicas && puede && palabras != null) {
			if (!mensajesPalabrasMagicas.containsKey(mago.getUniqueId())
					|| mensajesPalabrasMagicas.get(mago.getUniqueId()) + cdMensajePalabrasMagicas <= ticks) {
				String nombre = mago.getCustomName() == null ? mago.getName() : mago.getCustomName();
				AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, mago, getPalabrasMagicas(nombre), new HashSet<Player>(plugin.getServer().getOnlinePlayers()));
				Bukkit.getPluginManager().callEvent(event);
				plugin.getLogger().info("XDDD" + event.getFormat());
				plugin.getServer().broadcastMessage(event.getFormat().replace("%1$s", nombre).replace("%2$s", getPalabrasMagicas(nombre)));
//				mago.chat(getPalabrasMagicas(nombre));
			}
			mensajesPalabrasMagicas.put(mago.getUniqueId(), ticks);
		}
		return puede;
	}

	public void Accionar(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento, boolean ignorarPuede) {
		if (varita == null) {
			return;
		}
		if (ignorarPuede || puedeLanzar(plugin, mago, victima, varita, 0, !ignorarPuede, !ignorarPuede)) {
			int ticks = mago.getTicksLived();
			cds.put(mago.getUniqueId(), ticks);
			mensajes.put(mago.getUniqueId(), ticks);
			Accion(plugin, mago, victima, bloque, varita, tipoLanzamiento, varita.getPotencia(mago));
		}
	}

	/**
	 * Ésta es la acción que hace el conjuro. Puedes comprobar qué entidad o bloque mira el jugador, al igual que datos como la varita usada, el tipo de lanzamiento que triggerea esto, etc.
	 * @param plugin
	 * @param mago
	 * @param victima
	 * @param bloque
	 * @param varita
	 * @param tipoLanzamiento
	 * @param potencia
	 * @return "true" si se lanzó y actuó para que se ponga en CD, "false" si no. 
	 */
	public abstract boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento, float potencia);

	@Override
	public String toString() {
		return nombre;
	}

}