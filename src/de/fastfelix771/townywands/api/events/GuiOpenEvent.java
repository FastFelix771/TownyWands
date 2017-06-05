/*******************************************************************************
 * Copyright (C) 2017 Felix Drescher-Hackel
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.fastfelix771.townywands.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.fastfelix771.townywands.api.ModularGUI;
import de.fastfelix771.townywands.api.ModularInventory;
import de.fastfelix771.townywands.lang.Language;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class GuiOpenEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    
    
    @Getter @Setter
    private boolean cancelled = false;
    
    @Getter
    private final Player player;
    
    @Getter @NonNull
    private ModularGUI gui;
    
    @Getter @NonNull
    private ModularInventory inventory;
    
    @Getter @NonNull
    private Language itemLanguage;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
