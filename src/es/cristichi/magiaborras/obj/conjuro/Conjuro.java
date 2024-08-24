package es.cristichi.magiaborras.obj.conjuro;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustTransition;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

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
	protected String nombre, desc;
	protected MaterialChoice ingredientes;
	protected TiposLanzamiento tiposLanzamiento;
	protected EfectoVisual[] efectosVisuales;
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
	 * @param palabrasMagicas  String vacío "" si quieres que sean las por defecto. null si quieres que no se usen.
	 * @param tipoProyectil    El tipo de proyectil que usa el hechizo
	 */
	protected Conjuro(Plugin plugin, String id, String nombre, String desc, MaterialChoice ingredientes,
			TiposLanzamiento tiposLanzamiento, EfectoVisual[] efectosVisuales, String chatColor, Color color,
			int cooldownTicks, String palabrasMagicas) {
		this.id = id;
		this.nombre = nombre;
		this.desc = desc;
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
		this.efectosVisuales = efectosVisuales;
		this.color = color;
		this.cooldownTicks = cooldownTicks;
		this.ingredientes = ingredientes;
		this.palabras = palabrasMagicas == "" ? ChatColor.RESET + "¡{chatcolor}{nombre}" + ChatColor.RESET + "!"
				: palabrasMagicas;
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

	public String getDesc() {
		return desc;
	}

	public boolean isTipoLanzamiento(TipoLanzamiento tipo) {
		return tiposLanzamiento.contains(tipo);
	}

	public EfectoVisual[] getEfectosVisuales() {
		return efectosVisuales;
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
		return palabras.replace("{nombre}", nombre).replace("{chatcolor}", chatColor).replace("{atacante}", atacante);
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

	/**
	 * 
	 * @param  plugin
	 * @param  mago
	 * @param  victima
	 * @param  varita
	 * @param  multCD  valor por el que se multiplica el CD. Por defecto es 1.
	 * @param  avisoCD Normalmente "true". Si "false", no dirá a quien castea esto que tiene CD, útil si el CD es bajito.
	 * @return
	 */
	private boolean puedeLanzar(MagiaPlugin plugin, Player mago, Entity victima, Varita varita, boolean avisoCD) {
		boolean puede = true;
		int ticks = mago.getTicksLived();
		if (mago.hasPermission(plugin.PERM_MAGO)) {
			if (cooldownTicks > 0) {
				if (cds.containsKey(mago.getUniqueId())) {
					int ticksObj = cds.get(mago.getUniqueId()) + cooldownTicks;
					if (ticksObj > ticks) {
						puede = false;
						if (avisoCD) {
							int espera = (int) ((ticksObj - ticks) / 20);
							if (!mensajes.containsKey(mago.getUniqueId())
									|| mensajes.get(mago.getUniqueId()) + cdMensajeCd <= ticks) {
								mago.sendMessage(MagiaPlugin.header + "Debes esperar " + MagiaPlugin.accentColor
										+ espera + MagiaPlugin.textColor + " segundos para volver a lanzar " + chatColor
										+ toString() + MagiaPlugin.textColor + ".");
								mensajes.put(mago.getUniqueId(), ticks);
							}
						}
					}
				}
			}
		} else {
			puede = false;
			if (avisoCD)
				mago.sendMessage(MagiaPlugin.header + MagiaPlugin.errorColor + "No puedes usar Magia.");
		}
		return puede;
	}

	public boolean Accionar(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento) {
		if (varita == null) {
			return false;
		}
		if (puedeLanzar(plugin, mago, victima, varita, true)) {
			int ticks = mago.getTicksLived();
			mensajes.put(mago.getUniqueId(), ticks);

			// Lanzamos el conjuro
			if (Accion(plugin, mago, victima, bloque, varita, tipoLanzamiento)) {
				// Si el conjuro se lanzó con éxito, hacemos lo siguiente.

				// Palabras mágicas
				if (palabras != null) {
					if (!mensajesPalabrasMagicas.containsKey(mago.getUniqueId())
							|| mensajesPalabrasMagicas.get(mago.getUniqueId()) + cdMensajePalabrasMagicas <= ticks) {
						String nombre = mago.getCustomName() == null ? mago.getName() : mago.getCustomName();
						AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, mago, getPalabrasMagicas(nombre),
								new HashSet<Player>(plugin.getServer().getOnlinePlayers()));
						Bukkit.getPluginManager().callEvent(event);
						plugin.getServer().broadcastMessage(String.format(event.getFormat(), nombre, getPalabrasMagicas(nombre)));
					}
					mensajesPalabrasMagicas.put(mago.getUniqueId(), ticks);
				}

				// Efectos mágicos
				final int totalTicksParticulas = cooldownTicks;
				final int periodoParticulas = 3;
				final int totalVecesParticulas = totalTicksParticulas / periodoParticulas;
				final DustTransition dustTransitionParticulas = new DustTransition(getColor(), getColor(), 1F);

				final int radioOnda = 1;

				for (EfectoVisual efectoVisual : efectosVisuales) {
					switch (efectoVisual) {
					case PARTICULAS: {
						Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Consumer<BukkitTask>() {
							int cont = 0;

							@Override
							public void accept(BukkitTask task) {
								if (cont > totalVecesParticulas) {
									task.cancel();
								} else {
									mago.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION,
											mago.getEyeLocation().clone().add(0, 0.5, 0), 1, 0.1, 0.2, 0.1,
											dustTransitionParticulas);
								}
								cont++;
							}
						}, 0, periodoParticulas);
						break;
					}
					case RAYITO: {
						Location objetivo = null;
						if (tipoLanzamiento.equals(TipoLanzamiento.DISTANCIA_ENTIDAD) && victima != null) {
							objetivo = victima.getLocation().clone().add(0, victima.getHeight() * 2 / 4, 0);
						} else if (tipoLanzamiento.equals(TipoLanzamiento.DISTANCIA_BLOQUE) && bloque != null) {
							objetivo = bloque.getLocation().clone().add(0.5, 0.5, 0.5);
						}

						if (objetivo != null) {
							final DustTransition dustTransitionRayito = new DustTransition(getColor(), getColor(), 1F);
							Location actual = mago.getEyeLocation().clone().add(0, -0.2, 0);
							Vector paso = objetivo.toVector().clone().subtract(actual.toVector()).normalize()
									.multiply(0.5);

							while (actual.distance(objetivo) > 1) {
								mago.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, actual, 1, 0, 0, 0,
										dustTransitionRayito);
								actual.add(paso);
							}
						}
						break;
					}
					case ONDA: {
						final DustTransition dustTransitionOnda = new DustTransition(getColor(), getColor(), 1F);
						Location centro = mago.getLocation().clone().add(0, -0.1, 0);
						mago.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, centro, 5555, radioOnda, 0.1, radioOnda,
								dustTransitionOnda);
						break;
					}

					default: {
						plugin.getLogger().log(Level.SEVERE, "Error al intentar cargar los efectos visuales.",
								new IllegalArgumentException("Efecto visual no existe: " + efectoVisual.name() + "."));
						break;
					}
					}
				}

				// Ponemos en CD marcando el tiempo que el mago terminó este conjuro.
				cds.put(mago.getUniqueId(), mago.getTicksLived());
				return true;
			}
		}
		return false;
	}

	/**
	 * Ésta es la acción que hace el conjuro. Puedes comprobar qué entidad o bloque mira el jugador, al igual que datos como la varita usada, el tipo de lanzamiento que triggerea esto, etc.
	 * 
	 * @param  plugin
	 * @param  mago
	 * @param  victima
	 * @param  bloque
	 * @param  varita
	 * @param  tipoLanzamiento
	 * @param  potencia
	 * @return                 "true" si se lanzó y actuó para que se ponga en CD, "false" si no.
	 */
	public abstract boolean Accion(MagiaPlugin plugin, Player mago, Entity victima, Block bloque, Varita varita,
			TipoLanzamiento tipoLanzamiento);

	@Override
	public String toString() {
		return nombre;
	}

}