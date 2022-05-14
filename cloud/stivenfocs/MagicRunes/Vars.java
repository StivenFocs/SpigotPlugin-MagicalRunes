package cloud.stivenfocs.MagicRunes;

import cloud.stivenfocs.MagicRunes.Rune.RunesHandler;
import com.google.gson.JsonParser;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Vars {

    public static Loader plugin;
    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public File runesFile = new File(plugin.getDataFolder() + "/runes.yml");
    public FileConfiguration runesConfig;

    static Vars vars;
    public static Vars getVars() {
        if (vars == null) {
            vars = new Vars();
        }

        return vars;
    }

    /////////////////////////////

    public static Boolean debug = false;

    public static String prefix = "";
    public static String no_permission = "";
    public static String only_players = "";
    public static String player_not_found = "";
    public static String rune_not_found = "";
    public static String no_valid_rune = "";
    public static String inventory_full = "";
    public static String unknown_subcommand = "";
    public static String incomplete_command = "";
    public static String an_error_occurred = "";
    public static String configuration_reloaded = "";
    public static String you_given_rune = "";
    public static String you_got_rune = "";
    public static List<String> help_admin = new ArrayList<>();
    public static List<String> help_user = new ArrayList<>();

    /////////////////////////////

    public boolean reloadVars() {
        try {
            plugin.reloadConfig();

            getConfig().options().header("Developed with LOV by StivenFocs");
            getConfig().options().copyDefaults(true);

            getConfig().addDefault("options.debug", false);

            getConfig().addDefault("messages.prefix", "");
            getConfig().addDefault("messages.no_permission", "&cYou're not permitted to do this.");
            getConfig().addDefault("messages.only_players", "&cOnly players can do this.");
            getConfig().addDefault("messages.player_not_found", "&cNo players found with that name.");
            getConfig().addDefault("messages.rune_not_found", "&cRune not found!");
            getConfig().addDefault("messages.no_valid_rune", "&cThis rune haven't a valid item.");
            getConfig().addDefault("messages.inventory_full", "&cInventory full!!");
            getConfig().addDefault("messages.unknown_subcommand", "&dUnknown subcommand. Type &5/runes &dto see the commands list.");
            getConfig().addDefault("messages.incomplete_command", "&dIncomplete command. Type &5/runes &dto see the commands list.");
            getConfig().addDefault("messages.an_error_occurred", "&cAn error occurred while executing this task...");
            getConfig().addDefault("messages.configuration_reloaded", "&aConfiguration successfully reloaded");
            getConfig().addDefault("messages.you_given_rune", "&5You given x%amount% %rune%&5 to &f%player_displayname%");
            getConfig().addDefault("messages.you_given_rune", "&5You got x%amount% %rune%");
            List<String> new_help_admin = new ArrayList<>();
            new_help_admin.add("&8&m==========================");
            new_help_admin.add("&7* &5Magical&dRunes &7%version%");
            new_help_admin.add("");
            new_help_admin.add("&7* /runes reload &8&m|&7 Reload or fix the whole configuration");
            new_help_admin.add("&7* /runes give <player> <rune> <amount> &8&m|&7 Give an amount of rune to a player");
            new_help_admin.add("");
            new_help_admin.add("&8&m==========================");
            getConfig().addDefault("messages.help_admin", new_help_admin);
            List<String> new_help_user = new ArrayList<>();
            new_help_user.add("&8&m==========================");
            new_help_user.add("&7* &5Magical&dRunes");
            new_help_user.add("");
            new_help_user.add("&7* Get or find the Magical Runes");
            new_help_user.add("&7* that will give you powers.");
            new_help_user.add("");
            new_help_user.add("&8&m==========================");
            getConfig().addDefault("messages.help_user", new_help_user);

            plugin.saveConfig();
            plugin.reloadConfig();

            reloadRunesConfig();

            runesConfig.options().header("Developed with LOV by StivenFocs");
            runesConfig.options().copyDefaults(true);

            if (runesConfig.getKeys(false).size() < 1) {
                runesConfig.set("heal_rune.displayname", "&aHeal Rune");
                runesConfig.set("heal_rune.item.displayname", "&aHeal Rune");
                runesConfig.set("heal_rune.item.material", "STONE_BUTTON");
                runesConfig.set("heal_rune.item.durability", 0);
                List<String> new_heal_rune_item_lore = new ArrayList<>();
                new_heal_rune_item_lore.add("&7recovery all your");
                new_heal_rune_item_lore.add("&7HP in seconds.");
                runesConfig.set("heal_rune.item.lore", new_heal_rune_item_lore);
                runesConfig.set("heal_rune.item.glow", true);
                List<String> new_heal_rune_commands = new ArrayList<>();
                new_heal_rune_commands.add("sound:ENTITY_PLAYER_LEVELUP,1,3");
                new_heal_rune_commands.add("particle:SNEEZE,%x%,%y%,%z%,1000,1,1,1,0");
                new_heal_rune_commands.add("effect give %player_name% regeneration 2 250 true");
                runesConfig.set("heal_rune.type", "CONSUMABLE");
                runesConfig.set("heal_rune.commands", new_heal_rune_commands);
                runesConfig.set("heal_rune.delay", 0);
                runesConfig.set("heal_rune.recipe.row1", "AIR,SWEET_BERRIES,AIR");
                runesConfig.set("heal_rune.recipe.row2", "SWEET_BERRIES,STONE_BUTTON,SWEET_BERRIES");
                runesConfig.set("heal_rune.recipe.row3", "AIR,SWEET_BERRIES,AIR");

                runesConfig.set("strength_rune.displayname", "&dStrength Rune");
                runesConfig.set("strength_rune.item.displayname", "&dStrength Rune");
                runesConfig.set("strength_rune.item.material", "DARK_OAK_BUTTON");
                runesConfig.set("strength_rune.item.durability", 0);
                List<String> new_strength_rune_item_lore = new ArrayList<>();
                new_strength_rune_item_lore.add("&7Keep an effect of strength");
                new_strength_rune_item_lore.add("&7until you keep this rune");
                new_strength_rune_item_lore.add("&7on your inventory.");
                runesConfig.set("strength_rune.item.lore", new_strength_rune_item_lore);
                runesConfig.set("strength_rune.item.glow", true);
                List<String> new_strength_rune_commands = new ArrayList<>();
                new_strength_rune_commands.add("sound:BLOCK_LAVA_EXTINGUISH,1,2");
                new_strength_rune_commands.add("particle:FLAME,%x%,%y%,%z%,50,0.5,1,0.5,0");
                new_strength_rune_commands.add("effect give %player_name% strength 62 2 true");
                runesConfig.set("strength_rune.type", "CONSUMABLE");
                runesConfig.set("strength_rune.commands", new_strength_rune_commands);
                runesConfig.set("strength_rune.delay", 60);
                runesConfig.set("strength_rune.recipe.row1", "AIR,STONE_SWORD,AIR");
                runesConfig.set("strength_rune.recipe.row2", "STONE_SWORD,DARK_OAK_BUTTON,STONE_SWORD");
                runesConfig.set("strength_rune.recipe.row3", "AIR,STONE_SWORD,AIR");

                runesConfig.set("lightning_pointer.displayname", "&dStrength Rune");
                runesConfig.set("lightning_pointer.item.displayname", "&dStrength Rune");
                runesConfig.set("lightning_pointer.item.material", "DARK_OAK_BUTTON");
                runesConfig.set("lightning_pointer.item.durability", 0);
                List<String> new_lightning_pointer_item_lore = new ArrayList<>();
                runesConfig.set("lightning_pointer.item.lore", new_lightning_pointer_item_lore);
                runesConfig.set("lightning_pointer.item.glow", true);
                List<String> new_lightning_pointer_commands = new ArrayList<>();
                new_lightning_pointer_commands.add("lightning");
                runesConfig.set("lightning_pointer.type", "CONSUMABLE");
                runesConfig.set("lightning_pointer.commands", new_lightning_pointer_commands);
                runesConfig.set("lightning_pointer.delay", 0);
                runesConfig.set("lightning_pointer.recipe.row1", "AIR,LIGHTNING_ROD,AIR");
                runesConfig.set("lightning_pointer.recipe.row2", "AIR,LEVER,AIR");
                runesConfig.set("lightning_pointer.recipe.row3", "AIR,AIR,AIR");
            }

            saveRunesConfig();
            reloadRunesConfig();

            debug = getConfig().getBoolean("options.debug", false);

            prefix = getConfig().getString("messages.prefix", "");
            no_permission = getConfig().getString("messages.no_permission", "&cYou're not permitted to do this.");
            only_players = getConfig().getString("messages.only_players", "&cOnly players can do this.");
            player_not_found = getConfig().getString("messages.player_not_found", "&cNo players found with that name.");
            rune_not_found = getConfig().getString("messages.rune_not_found", "&cRune not found!");
            no_valid_rune = getConfig().getString("messages.no_valid_rune", "&cThis rune haven't a valid item.");
            inventory_full = getConfig().getString("messages.inventory_full", "&cInventory full!!");
            unknown_subcommand = getConfig().getString("messages.unknown_subcommand", "&dUnknown subcommand. Type &5/runes &dto see the commands list.");
            incomplete_command = getConfig().getString("messages.incomplete_command", "&dIncomplete command. Type &5/runes &dto see the commands list.");
            an_error_occurred = getConfig().getString("messages.an_error_occurred", "&cAn error occurred while executing this task...");
            configuration_reloaded = getConfig().getString("messages.configuration_reloaded", "&aConfiguration successfully reloaded");
            you_given_rune = getConfig().getString("messages.you_given_rune", "&5You given x%amount% %rune%&5 to &f%player_displayname%");
            you_got_rune = getConfig().getString("messages.you_given_rune", "&5You got x%amount% %rune%");
            help_admin = getConfig().getStringList("messages.help_admin");
            help_user = getConfig().getStringList("messages.help_user");

            RunesHandler.reload();

            plugin.getLogger().info("Configuration reloaded");
            return true;
        } catch (Exception ex) {
            plugin.getLogger().severe("An exception occurred while trying to load the whole configuration!! Plugin disabled.");
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(plugin);
            return false;
        }
    }

    public void saveRunesConfig() {
        if (!runesFile.exists()) {
            try {
                if (!runesFile.createNewFile()) {
                    plugin.getLogger().severe("Unable to generate the Runes configuration!!");
                }
            } catch (Exception ex) {
                plugin.getLogger().severe("Unable to generate the Runes configuration!!");
                ex.printStackTrace();
            }
            runesConfig = YamlConfiguration.loadConfiguration(runesFile);
        }

        try {
            runesConfig.save(runesFile);
        } catch (Exception ex) {
            plugin.getLogger().severe("Unable to save the Runes configuration!!");
            ex.printStackTrace();
        }
    }

    public void reloadRunesConfig() {
        if (!runesFile.exists()) {
            try {
                if (!runesFile.createNewFile()) {
                    plugin.getLogger().severe("Unable to generate the Runes configuration!!");
                }
            } catch (Exception ex) {
                plugin.getLogger().severe("Unable to generate the Runes configuration!!");
                ex.printStackTrace();
            }
        }

        runesConfig = YamlConfiguration.loadConfiguration(runesFile);
    }

    /////////////////////////////

    public static boolean hasUserPermission(String permission, CommandSender user) {
        return user.hasPermission("magicrunes.user." + permission);
    }

    public static boolean hasAdminPermission(String permission, CommandSender user) {
        return user.hasPermission("magicrunes.admin." + permission);
    }

    public static String setPlaceholders(String text, CommandSender user) {
        text = text.replaceAll("%author%", plugin.getDescription().getAuthors().get(0));
        text = text.replaceAll("%version%", plugin.getDescription().getVersion());
        text = text.replaceAll("%name%", user.getName());
        if (user instanceof Player) {
            Player p = (Player) user;

            text = text.replaceAll("%displayname%", p.getDisplayName());
        }

        return text;
    }

    /////////////////////////////

    public static void sendString(String text, CommandSender user) {
        if (text.length() > 0) {
            text = prefix + text;
            if (Vars.plugin.getConfig().getString(text) != null) text = Vars.plugin.getConfig().getString(text);
            for (String line : text.split("/n")) {
                line = setPlaceholders(line, user);
                if (user instanceof Player && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                    line = PlaceholderAPI.setPlaceholders((Player) user, line);

                if (isValidJson(line) && user instanceof Player) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + user.getName() + " " + line);
                }

                user.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
            }
        }
    }

    public static void sendStringList(List<String> string_list, CommandSender user) {
        for (String line_ : string_list) {
            line_ = prefix + line_;
            if (line_.length() > 0 && Vars.plugin.getConfig().getString(line_) != null) line_ = Vars.plugin.getConfig().getString(line_);
            for (String line : line_.split("/n")) {
                line = setPlaceholders(line, user);
                if (user instanceof Player && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                    line = PlaceholderAPI.setPlaceholders((Player) user, line);

                if (isValidJson(line) && user instanceof Player) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + user.getName() + " " + line);
                }

                user.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
            }
        }
    }

    public static List<String> colorList(List<String> uncolored_list) {
        List<String> colored_list = new ArrayList<>();
        for (String line : uncolored_list) {
            colored_list.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return colored_list;
    }

    public static boolean isValidJson(String json) {
        try {
            return new JsonParser().parse(json).getAsJsonObject() != null;
        } catch (Throwable ignored) {}

        try {
            return new JsonParser().parse(json).getAsJsonArray() != null;
        } catch (Throwable ignored) {}

        return false;
    }

    public static boolean isDigit(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

}
