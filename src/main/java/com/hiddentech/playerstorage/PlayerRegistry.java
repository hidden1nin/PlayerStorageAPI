package com.hiddentech.playerstorage;

import com.hiddentech.playerstorage.types.DataType;
import com.hiddentech.playerstorage.types.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRegistry {

    private final PlayerStorage plugin;

    public HashMap<UUID, PlayerData> getPlayers() {
        return players;
    }

    public HashMap<String, DataType> getTypes() {
        return types;
    }

    private HashMap<UUID, PlayerData> players = new HashMap<>();
    private HashMap<String, DataType> types= new HashMap<>();

    public HashMap<String, Boolean> getDefaultBools() {
        return defaultBools;
    }

    public HashMap<String, Integer> getDefaultInts() {
        return defaultInts;
    }

    public HashMap<String, String> getDefaultStrings() {
        return defaultStrings;
    }

    private HashMap<String, Boolean> defaultBools= new HashMap<>();
    private HashMap<String, Integer> defaultInts= new HashMap<>();
    private HashMap<String, String> defaultStrings= new HashMap<>();

    public PlayerRegistry(PlayerStorage plugin){
        this.plugin = plugin;
    }

    public void loadPlayer(Player player) {
        loadPlayer(player.getUniqueId());
    }
    public void loadPlayer(UUID uuid) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try{
                    if(players.containsKey(uuid))return;
                    Jedis jedis = plugin.getPool().getResource();

                    if(!jedis.exists(uuid.toString())){
                        players.put(uuid,new PlayerData(uuid));
                        return;
                    }

                    Map<String,String> stored =jedis.hgetAll(uuid.toString());
                    Map<String,String> storedStrings = (Map<String, String>) DataType.STRING.deSerialize(stored);
                    Map<String,Boolean> storedBooleans = (Map<String, Boolean>) DataType.BOOLEAN.deSerialize(stored);
                    Map<String,Integer> storedInts = (Map<String, Integer>) DataType.INTEGER.deSerialize(stored);
                    players.put(uuid,new PlayerData(uuid,storedStrings,storedBooleans,storedInts));

                    jedis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

    }

    public void savePlayer(Player player) {
        savePlayer(player.getUniqueId());
    }
    public void savePlayer(UUID uuid) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if(!players.containsKey(uuid))return;
                    Jedis jedis = plugin.getPool().getResource();
                    PlayerData data = players.get(uuid);
                    HashMap<String,String> strings = new HashMap<>();
                    encode(data.getBooleans(),strings);
                    encode(data.getInts(),strings);
                    encode(data.getStrings(),strings);

                    jedis.del(data.getUuid().toString());
                    if(!strings.isEmpty()){
                        jedis.hset(data.getUuid().toString(), strings);
                    }
                    jedis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }


    public void removePlayer(Player player) {
        this.getPlayers().remove(player.getUniqueId());
    }


    public void encode(Map<String,?> data,HashMap<String,String> strings){
        for(String key:data.keySet()){
            strings.put(key,String.valueOf(data.get(key)));
        }
    }
}
