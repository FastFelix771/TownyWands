package de.fastfelix771.townywands.packets;

import org.bukkit.entity.Player;
import de.fastfelix771.townywands.utils.Invoker;

public abstract interface VirtualSign {

    public void show(Player player, Invoker<String[]> callback);
    public void setup(Player player);
    public void unsetup(Player player);
    
}