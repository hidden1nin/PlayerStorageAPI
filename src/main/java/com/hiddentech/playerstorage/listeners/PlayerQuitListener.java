package com.hiddentech.playerstorage.listeners;

import com.hiddentech.playerstorage.PlayerStorageAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final PlayerStorageAPI plugin;

    public PlayerQuitListener(PlayerStorageAPI plugin){
        this.plugin = plugin;
        plugin.getPlugin().getServer().getPluginManager().registerEvents(this,plugin.getPlugin());
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event){
        plugin.getRegistry().removePlayer(event.getPlayer());
    }
}
