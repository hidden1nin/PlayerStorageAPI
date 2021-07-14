package com.hiddentech.playerstorage;

import com.hiddentech.playerstorage.events.PlayerDataLoadEvent;
import com.hiddentech.playerstorage.types.DataType;
import com.hiddentech.playerstorage.types.PlayerData;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRegistry {

    private final PlayerStorageAPI plugin;

    public HashMap<UUID, PlayerData> getPlayers() {
        return players;
    }

    public HashMap<String, DataType> getTypes() {
        return types;
    }

    private HashMap<UUID, PlayerData> players = new HashMap<>();
    private HashMap<String, DataType> types = new HashMap<>();

    public HashMap<String, Boolean> getDefaultBools() {
        return defaultBools;
    }

    public HashMap<String, Integer> getDefaultInts() {
        return defaultInts;
    }

    public HashMap<String, String> getDefaultStrings() {
        return defaultStrings;
    }

    private HashMap<String, Boolean> defaultBools = new HashMap<>();
    private HashMap<String, Integer> defaultInts = new HashMap<>();
    private HashMap<String, String> defaultStrings = new HashMap<>();

    public PlayerRegistry(PlayerStorageAPI plugin) {
        this.plugin = plugin;
    }

    public void loadPlayer(Player player) {
        loadPlayer(player.getUniqueId());
    }

    public void loadPlayer(UUID uuid) {
        new BukkitRunnable() {
            @Override
            public void run() {

                if (plugin.redisEnabled && plugin.mongoEnabled) {
                    try {
                        if (players.containsKey(uuid)) return;
                        Jedis jedis = plugin.getPool().getResource();

                        if (!jedis.exists(uuid.toString())) {

                            if (plugin.mongoEnabled) {
                                PlayerData data = loadPlayerFromMongo(uuid);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        plugin.getPlugin().getServer().getPluginManager().callEvent(new PlayerDataLoadEvent(plugin.getPlugin().getServer().getPlayer(uuid), data));
                                    }
                                }.runTask(plugin.getPlugin());
                                players.put(uuid, data);
                                savePlayer(uuid);
                            } else {
                                PlayerData data = new PlayerData(uuid);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        plugin.getPlugin().getServer().getPluginManager().callEvent(new PlayerDataLoadEvent(plugin.getPlugin().getServer().getPlayer(uuid), data));
                                    }
                                }.runTask(plugin.getPlugin());
                                players.put(uuid, data);
                            }
                            return;
                        }

                        JedisLoad(jedis, uuid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    savePlayer(uuid);
                    return;
                }

                if (plugin.mongoEnabled) {
                    PlayerData data = loadPlayerFromMongo(uuid);
                    players.put(uuid, data);
                    savePlayer(uuid);
                    return;
                }

                if (plugin.redisEnabled) {
                    try {
                        if (players.containsKey(uuid)) return;
                        Jedis jedis = plugin.getPool().getResource();

                        if (!jedis.exists(uuid.toString())) {
                            PlayerData data = new PlayerData(uuid);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    plugin.getPlugin().getServer().getPluginManager().callEvent(new PlayerDataLoadEvent(plugin.getPlugin().getServer().getPlayer(uuid), data));
                                }
                            }.runTask(plugin.getPlugin());
                            players.put(uuid, data);
                            return;
                        }
                        JedisLoad(jedis, uuid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    savePlayer(uuid);
                }

            }
        }.runTaskAsynchronously(plugin.getPlugin());

    }

    private void JedisLoad(Jedis jedis, UUID uuid) {
        Map<String, String> stored = jedis.hgetAll(uuid.toString());
        Map<String, String> storedStrings = (Map<String, String>) DataType.STRING.deSerialize(stored, plugin);
        Map<String, Boolean> storedBooleans = (Map<String, Boolean>) DataType.BOOLEAN.deSerialize(stored, plugin);
        Map<String, Integer> storedInts = (Map<String, Integer>) DataType.INTEGER.deSerialize(stored, plugin);
        PlayerData data = new PlayerData(uuid, storedStrings, storedBooleans, storedInts);
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getPlugin().getServer().getPluginManager().callEvent(new PlayerDataLoadEvent(plugin.getPlugin().getServer().getPlayer(uuid), data));
            }
        }.runTask(plugin.getPlugin());
        players.put(uuid, data);

        jedis.close();
    }

    private PlayerData loadPlayerFromMongo(UUID uuid) {
        Document document = plugin.getMongo().getCollection().find(Filters.eq("uuid", uuid.toString())).first();
        if (document == null) return new PlayerData(uuid);
        Map<String, String> storedStrings = (Map<String, String>) document.get("strings");
        Map<String, Boolean> storedBooleans = (Map<String, Boolean>) document.get("bools");
        Map<String, Integer> storedInts = (Map<String, Integer>) document.get("ints");
        PlayerData data = new PlayerData(uuid, storedStrings, storedBooleans, storedInts);
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getPlugin().getServer().getPluginManager().callEvent(new PlayerDataLoadEvent(plugin.getPlugin().getServer().getPlayer(uuid), data));
            }
        }.runTask(plugin.getPlugin());
        return data;
    }

    public void savePlayer(Player player) {
        savePlayer(player.getUniqueId());
    }

    public void savePlayer(UUID uuid) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!players.containsKey(uuid)) return;
                    PlayerData data = players.get(uuid);

                    if (plugin.redisEnabled) {
                        Jedis jedis = plugin.getPool().getResource();
                        HashMap<String, String> strings = new HashMap<>();
                        encode(data.getBooleans(), strings);
                        encode(data.getInts(), strings);
                        encode(data.getStrings(), strings);
                        jedis.del(data.getUuid().toString());
                        if (!strings.isEmpty()) {
                            jedis.hset(data.getUuid().toString(), strings);
                            //auto remove old data after 1 day
                            if (plugin.mongoEnabled) {
                                jedis.expire(data.getUuid().toString(), plugin.expireTime);
                            }
                        }

                        jedis.close();
                    }
                    if (plugin.mongoEnabled) {
                        MongoPlayerData mongoPlayerData = new MongoPlayerData(uuid, data, plugin.getMongo(), plugin);
                        mongoPlayerData.save();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin.getPlugin());
    }


    public void removePlayer(Player player) {
        this.getPlayers().remove(player.getUniqueId());
    }


    public void encode(Map<String, ?> data, HashMap<String, String> strings) {
        for (String key : data.keySet()) {
            strings.put(key, String.valueOf(data.get(key)));
        }
    }
}
