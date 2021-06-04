package com.hiddentech.playerstorage.listeners;

import com.hiddentech.playerstorage.PlayerStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final PlayerStorage plugin;

    public PlayerJoinListener(PlayerStorage plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event){
        plugin.getRegistry().loadPlayer(event.getPlayer());
    }
}
