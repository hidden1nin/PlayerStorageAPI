package com.hiddentech.playerstorage.types;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    public Map<String, String> getStrings() {
        return strings;
    }

    public Map<String, Boolean> getBooleans() {
        return booleans;
    }

    public Map<String, Integer> getInts() {
        return ints;
    }

    public UUID getUuid() {
        return uuid;
    }

    private final Map<String,String> strings;
    private final Map<String,Boolean> booleans;
    private final Map<String,Integer> ints;
    private final UUID uuid;

    public PlayerData(UUID uniqueId, Map<String, String> storedStrings, Map<String, Boolean> storedBooleans, Map<String, Integer> storedInts) {
    this.uuid = uniqueId;
    this.strings = storedStrings;
    this.booleans = storedBooleans;
    this.ints = storedInts;
//    Bukkit.broadcastMessage("loaded from cloud");
    }

    public PlayerData(UUID uniqueId) {
        this.strings = new HashMap<>();
        this.booleans = new HashMap<>();
        this.ints = new HashMap<>();
        this.uuid = uniqueId;
//        Bukkit.broadcastMessage("loaded locally");
    }
}
