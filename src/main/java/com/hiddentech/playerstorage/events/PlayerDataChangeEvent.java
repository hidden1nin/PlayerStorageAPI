package com.hiddentech.playerstorage.events;

import com.hiddentech.playerstorage.types.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerDataChangeEvent extends Event {
    private final Player player;
    private final PlayerData data;
    private static final HandlerList handlers = new HandlerList();

    public String getLocation() {
        return location;
    }

    private final String location;

    public PlayerDataChangeEvent(Player player, PlayerData data,String location){
        this.player = player;
        this.data = data;
        this.location = location;
    }

    public PlayerData getPlayerData() {
        return data;
    }

    public Player getPlayer() {
        return player;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
