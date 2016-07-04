package de.fastfelix771.townywands.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.fastfelix771.townywands.api.ModularGUI;
import de.fastfelix771.townywands.api.ModularInventory;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class GuiCloseEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();


	@Getter @Setter
	private boolean cancelled = false;
	
    @Getter
    private final Player player;

	@Getter @NonNull
	private ModularGUI gui;

	@Getter @NonNull
	private ModularInventory inventory;

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}