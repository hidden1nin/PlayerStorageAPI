package com.hiddentech.playerstorage.listeners;

import com.hiddentech.playerstorage.PlayerStorageAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final PlayerStorageAPI plugin;

    public PlayerJoinListener(PlayerStorageAPI plugin){
        this.plugin = plugin;
        plugin.getPlugin().getServer().getPluginManager().registerEvents(this,plugin.getPlugin());
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event){
        plugin.getRegistry().loadPlayer(event.getPlayer());
    }
}
