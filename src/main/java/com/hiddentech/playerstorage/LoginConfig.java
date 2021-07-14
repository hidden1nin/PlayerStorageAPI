package com.hiddentech.playerstorage;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LoginConfig {
    private final PlayerStorageAPI plugin;

    public LoginConfig(PlayerStorageAPI plugin){
        this.plugin = plugin;
        setConfig();
    }

    private void setConfig() {
        //make directory

        File file = new File(plugin.getPlugin().getDataFolder()+"/Database.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        //add config for connection
        if(!yml.isSet("Redis_Connection")){
            yml.set("Redis_Connection", "change this!");
            yml.set("Redis_Port", 12345);
            yml.set("Redis_Password", "and this too!");
            yml.set("Redis_Data_Expire_After", 86400);
        }
        if(!yml.isSet("Storage_Configuration")){
            yml.set("Storage_Configuration","both");
            yml.set("Mongo_DB_Name","PlayerStorageAPI");
            yml.set("Mongo_DB_ConnectionString","Change Me Too!");
        }
        try {
            yml.options().copyDefaults(true);
            yml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
