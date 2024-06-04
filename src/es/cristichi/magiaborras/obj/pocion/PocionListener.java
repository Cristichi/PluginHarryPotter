package es.cristichi.magiaborras.obj.pocion;

import java.util.function.BiConsumer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.persistence.PersistentDataType;

class PocionListener implements Listener {
	
	protected PocionListener() {
	}

	@EventHandler
	private static void alBeber(PlayerItemConsumeEvent e) {
		if (e.getItem().hasItemMeta()) {
			String nombre = e.getItem().getItemMeta().getPersistentDataContainer().get(Pocion.keyPocion,
					PersistentDataType.STRING);
			Pocion.pociones.forEach(new BiConsumer<String, Pocion>() {
				@Override
				public void accept(String t, Pocion u) {
					if (u.getNombre().equals(nombre)) {
						u.Accion(e);
					}
				}
			});
		}
	}
}