package com.hiddentech.playerstorage;

import com.hiddentech.playerstorage.types.DataType;
import com.hiddentech.playerstorage.types.PlayerData;
import org.bukkit.Bukkit;

import java.util.UUID;

public class PlayerStorageAPI {
    
    public static void registerValue(String key,Boolean defaultValue){
        PlayerStorage.getPlugin().getRegistry().getTypes().put(key, DataType.BOOLEAN);
        PlayerStorage.getPlugin().getRegistry().getDefaultBools().put(key,defaultValue);
    }
    public static void registerValue(String key,Integer defaultValue){
        PlayerStorage.getPlugin().getRegistry().getTypes().put(key, DataType.INTEGER);
        PlayerStorage.getPlugin().getRegistry().getDefaultInts().put(key,defaultValue);
    }
    public static void registerValue(String key,String defaultValue){
        PlayerStorage.getPlugin().getRegistry().getTypes().put(key, DataType.BOOLEAN);
        PlayerStorage.getPlugin().getRegistry().getDefaultStrings().put(key,defaultValue);
    }

    public static Boolean getBool(UUID uuid,String key){
        if(!PlayerStorage.getPlugin().getRegistry().getDefaultBools().containsKey(key))return false;
        if(!PlayerStorage.getPlugin().getRegistry().getPlayers().containsKey(uuid))return PlayerStorage.getPlugin().getRegistry().getDefaultBools().get(key);
        if(!PlayerStorage.getPlugin().getRegistry().getPlayers().get(uuid).getBooleans().containsKey(key))return PlayerStorage.getPlugin().getRegistry().getDefaultBools().get(key);
        return PlayerStorage.getPlugin().getRegistry().getPlayers().get(uuid).getBooleans().get(key);
    }
    public static Integer getInt(UUID uuid,String key){
        if(!PlayerStorage.getPlugin().getRegistry().getDefaultInts().containsKey(key))return 0;
        if(!PlayerStorage.getPlugin().getRegistry().getPlayers().containsKey(uuid))return PlayerStorage.getPlugin().getRegistry().getDefaultInts().get(key);
        if(!PlayerStorage.getPlugin().getRegistry().getPlayers().get(uuid).getInts().containsKey(key))return PlayerStorage.getPlugin().getRegistry().getDefaultInts().get(key);
        return PlayerStorage.getPlugin().getRegistry().getPlayers().get(uuid).getInts().get(key);
    }
    public static String getString(UUID uuid,String key){
        if(!PlayerStorage.getPlugin().getRegistry().getDefaultStrings().containsKey(key))return null;
        if(!PlayerStorage.getPlugin().getRegistry().getPlayers().containsKey(uuid))return PlayerStorage.getPlugin().getRegistry().getDefaultStrings().get(key);
        if(!PlayerStorage.getPlugin().getRegistry().getPlayers().get(uuid).getStrings().containsKey(key))return PlayerStorage.getPlugin().getRegistry().getDefaultStrings().get(key);
        return PlayerStorage.getPlugin().getRegistry().getPlayers().get(uuid).getStrings().get(key);
    }

    public static void set(UUID uuid,String key,Boolean value){
        if(!PlayerStorage.getPlugin().getRegistry().getDefaultBools().containsKey(key))return;
        if(!PlayerStorage.getPlugin().getRegistry().getPlayers().containsKey(uuid))return;
        PlayerData data = PlayerStorage.getPlugin().getRegistry().getPlayers().get(uuid);
        Boolean defaultValue = PlayerStorage.getPlugin().getRegistry().getDefaultBools().get(key);
        if(value.equals(defaultValue)){
            data.getBooleans().remove(key);
            PlayerStorage.getPlugin().getRegistry().savePlayer(uuid);
            return;
        }
        data.getBooleans().put(key,value);
        PlayerStorage.getPlugin().getRegistry().savePlayer(uuid);
    }
    public static void set(UUID uuid,String key,Integer value){
        if(!PlayerStorage.getPlugin().getRegistry().getDefaultInts().containsKey(key))return;
        if(!PlayerStorage.getPlugin().getRegistry().getPlayers().containsKey(uuid))return;
        PlayerData data = PlayerStorage.getPlugin().getRegistry().getPlayers().get(uuid);
        Integer defaultValue = PlayerStorage.getPlugin().getRegistry().getDefaultInts().get(key);
        if(value.equals(defaultValue)){
            data.getInts().remove(key);
            PlayerStorage.getPlugin().getRegistry().savePlayer(uuid);
            return;
        }
        data.getInts().put(key,value);
        PlayerStorage.getPlugin().getRegistry().savePlayer(uuid);
    }
    public static void set(UUID uuid,String key,String value){
        if(!PlayerStorage.getPlugin().getRegistry().getDefaultStrings().containsKey(key))return;
        if(!PlayerStorage.getPlugin().getRegistry().getPlayers().containsKey(uuid))return;
        PlayerData data = PlayerStorage.getPlugin().getRegistry().getPlayers().get(uuid);
        String defaultValue = PlayerStorage.getPlugin().getRegistry().getDefaultStrings().get(key);
        if(value.equals(defaultValue)){
            data.getStrings().remove(key);
            PlayerStorage.getPlugin().getRegistry().savePlayer(uuid);
            return;
        }
        data.getStrings().put(key,value);
        PlayerStorage.getPlugin().getRegistry().savePlayer(uuid);
    }
}
