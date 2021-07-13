package com.hiddentech.playerstorage.listeners;

import com.hiddentech.playerstorage.PlayerStorage;
import com.hiddentech.playerstorage.events.PlayerDataLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerDataLoadListener implements Listener {

    private final PlayerStorage plugin;

    public PlayerDataLoadListener(PlayerStorage plugin){
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void dataLoad(PlayerDataLoadEvent event){
//        event.getPlayer().sendMessage("loaded");
    }
}
