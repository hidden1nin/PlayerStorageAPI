package com.hiddentech.playerstorage.listeners;

import com.hiddentech.playerstorage.PlayerStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final PlayerStorage plugin;

    public PlayerQuitListener(PlayerStorage plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event){
        plugin.getRegistry().savePlayer(event.getPlayer());
        plugin.getRegistry().removePlayer(event.getPlayer());
    }
}
