package de.fastfelix771.townywands.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

import de.fastfelix771.townywands.api.ModularGUI;
import de.fastfelix771.townywands.inventory.ItemWrapper;

@RequiredArgsConstructor
public class GuiClickEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    @Getter private final ItemWrapper itemWrapper;
    @Getter private final Player player;
    @Getter @Setter private boolean cancelled = false;
    @Getter private final ModularGUI gui;
    @Getter private final Inventory inventory;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}