package com.hiddentech.playerstorage;

import com.hiddentech.playerstorage.types.PlayerData;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;

import java.util.Map;

public class GetDataCommand implements CommandExecutor {
    private PlayerStorage plugin;

    public GetDataCommand(PlayerStorage plugin) {
        this.plugin = plugin;
        plugin.getCommand("playerdata").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("No perms");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage("specify player");
            return true;
        }
        sender.sendMessage("Retrieving data for " + args[0]);
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage("They've never played!");
            return false;
        }

        sender.sendMessage("Cached :");
        if (plugin.getRegistry().getPlayers().containsKey(player.getUniqueId())) {
            PlayerData data = plugin.getRegistry().getPlayers().get(player.getUniqueId());
            if (data.getBooleans().isEmpty() && data.getInts().isEmpty() && data.getStrings().isEmpty()) {
                sender.sendMessage("No data cached!");
            } else {
                for (String key : data.getStrings().keySet()) {
                    sender.sendMessage(ChatColor.AQUA + "" + key + ChatColor.DARK_BLUE + " : " + ChatColor.BLUE + data.getStrings().get(key));
                }
                for (String key : data.getBooleans().keySet()) {
                    sender.sendMessage(ChatColor.AQUA + "" + key + ChatColor.DARK_BLUE + " : " + ChatColor.BLUE + data.getBooleans().get(key));
                }
                for (String key : data.getInts().keySet()) {
                    sender.sendMessage(ChatColor.AQUA + "" + key + ChatColor.DARK_BLUE + " : " + ChatColor.BLUE + data.getInts().get(key));
                }
            }
        } else {
            sender.sendMessage("No cached data found!");
        }
        new BukkitRunnable() {
            @Override
            public void run() {

                if (plugin.redisEnabled) {
                    sender.sendMessage("In Redis :");
                    try {
                        if (!plugin.getRegistry().getPlayers().containsKey(player.getUniqueId())) {
                            sender.sendMessage("No data found!");
                            return;
                        }
                        Jedis jedis = plugin.getPool().getResource();
                        if (!jedis.exists(player.getUniqueId().toString())) {
                            sender.sendMessage("No data stored in redis!");
                        } else {
                            Map<String, String> strings = jedis.hgetAll(player.getUniqueId().toString());
                            for (String key : strings.keySet()) {
                                sender.sendMessage(ChatColor.AQUA + "" + key + ChatColor.DARK_BLUE + " : " + ChatColor.BLUE + strings.get(key));
                            }
                        }
                        jedis.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!plugin.mongoEnabled) return;
                sender.sendMessage("In MongoDB :");
                try {
                    if (!plugin.getRegistry().getPlayers().containsKey(player.getUniqueId())) {
                        sender.sendMessage("No data found!");
                        return;
                    }
                    Document document = plugin.getMongo().getCollection().find(Filters.eq("uuid", player.getUniqueId().toString())).first();
                    if (document == null) {
                        sender.sendMessage("No data stored in mongo!");
                        return;
                    }
                    Map<String, String> storedStrings = (Map<String, String>) document.get("strings");
                    Map<String, Boolean> storedBooleans = (Map<String, Boolean>) document.get("bools");
                    Map<String, Integer> storedInts = (Map<String, Integer>) document.get("ints");

                    for (String key : storedStrings.keySet()) {
                        sender.sendMessage(ChatColor.AQUA + "" + key + ChatColor.DARK_BLUE + " : " + ChatColor.BLUE + storedStrings.get(key));
                    }
                    for (String key : storedBooleans.keySet()) {
                        sender.sendMessage(ChatColor.AQUA + "" + key + ChatColor.DARK_BLUE + " : " + ChatColor.BLUE + storedBooleans.get(key));
                    }
                    for (String key : storedInts.keySet()) {
                        sender.sendMessage(ChatColor.AQUA + "" + key + ChatColor.DARK_BLUE + " : " + ChatColor.BLUE + storedInts.get(key));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
        return false;
    }
}
