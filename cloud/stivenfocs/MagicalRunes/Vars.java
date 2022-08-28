package cloud.stivenfocs.MagicalRunes;

import cloud.stivenfocs.MagicalRunes.Rune.RunesHandler;
import com.google.gson.JsonParser;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

    private static Field bukkitCommandMap = null;

    static {
        try {
            bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
        } catch (NoSuchFieldException noSuchFieldException) {
            noSuchFieldException.printStackTrace();
        }
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
            getConfig().addDefault("messages.you_got_rune", "&5You got x%amount% %rune%");
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

            runesConfig.options().header("Developed with LOV by StivenFocs\n" +
                    "\n" +
                    "For the Materials list: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html\n" +
                    "For the Enchantments list: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/enchantments/Enchantment.html\n" +
                    "For the ItemFlags list: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/inventory/ItemFlag.html\n" +
                    "For the Sounds list: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html\n" +
                    "For the Particles list: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html\n" +
                    "For the PotionEffects list: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html\n" +
                    "\n" +
                    "To create a magical item you have to choose a \"code name\" for it such like \"heal_rune\"\n" +
                    "it will be used as reference for the rune its configuration and item.\n" +
                    "(this means that if you change the item appaerance, the old item still works. to block this just rename the rune code name)\n" +
                    "\n" +
                    "Every rune does have all these fields:\n" +
                    "- displayname: this is not the item displayname, is the reference name of the rune\n" +
                    "- item: a section that contains all the appaerance data of the rune item\n" +
                    "- commands: the list of commands or actions that the item will execute\n" +
                    "The commands lists can contain these special actions: (every command/action does have the placeholders: %player_name% %player_displayname% %player_uuid% %world% %x% %y% %z%, and you can also use PlaceholderAPI's placeholders)\n" +
                    "  - \"tell:TEXT\" to directly send a message to the player (you can color the message with the \"&\" character) | ex. \"tell:&eHello!\"\n" +
                    "  - \"broadcast:TEXT\" to broadcast a message (you can color the message with the \"&\" character) | ex. \"broadcast:&eHello!\"\n" +
                    "  - \"title:TITLE TEXT,SUBTITLE TEXT,FADE IN,DURATION,FADE OUT\" to broadcast a message (you can color the message with the \"&\" character) | ex. \"title: ,&d...Something just happened...,5,25,5\"\n" +
                    "  - \"actionbar:TEXT\" to send an actionbar message to the player (you can color the message with the \"&\" character) | ex. \"actionbar:&eHello!\"\n" +
                    "  - \"sudo:COMMAND\" send a command as the player | ex. \"sudo:spawn\"\n" +
                    "  - \"permitted_sudo:COMMAND\" send a command as the player with permissions skip | ex. \"permitted_sudo:gamemode creative\" \n" +
                    "  - \"sound:SOUND_NAME,VOLUME,PITCH\" play a custom sound to the player | ex. \"sound:ENTITY_PLAYER_LEVELUP,1,3\"\n" +
                    "  - \"particle:PARTICLE_NAME,LOCATION_WORLD,LOCATION_X,LOCATION_Y,LOCATION_Z,AMOUNT,OFFSET_X,OFFSET_Y,OFFSET_Z,SPEED\" spawn a particle in a location with custom data | ex. \"particle:SNEEZE,%world%,%x%,%y%,%z%,1000,1,1,1,0\"\n" +
                    "  - \"effect:EFFECT_NAME,DURATION IN TICKS,AMPLIFIER,AMBIENT,SHOW POTION PARTICLES,SHOW ICON\" add an effect to the player | ex. \"effect:REGENERATION,250,2,false,false,true\"\n" +
                    "  - \"teleport:WORLD,X,Y,Z\" Teleport the player to a location (you can use \"Double\" values and pitch/yaw also) | ex. \"TELEPORT:world,0,80,80,90,90\"\n" +
                    "  - \"fireball_cannon\"\n" +
                    "  - \"snowball_cannon\"\n" +
                    "  - \"egg_cannon\"\n" +
                    "  - \"lightning\" (the target block is limited to the farthest in a distance of 50 blocks)\n" +
                    "  - \"openinventory\" force the player to open his inventory\n" +
                    "  - \"closeinventory\" force the player to close the inventory\n" +
                    "  - \"kill\" kill the player\n" +
                    "  if the command is not one of these, the plugin will sudo the command at the console\n" +
                    "- type: the interaction type of the item\n" +
                    "CONSUMABLE: at every click (left or right) it will run all the commands and the item amount will descend by 1 or destroy.\n" +
                    "INFINITE: like CONSUMABLE but infinite\n" +
                    "HELD_EFFECT: everytime the delay resets, the commands will be run if the player is holding the item\n" +
                    "KEEP_EFFECT: everytime the delay resets, the commands will be run if the player is kepping the item in its inventory\n" +
                    "- delay: delay in ticks of the commands execution (20 ticks = 1 second)\n" +
                    "- recipe: custom recipe of the item\n" +
                    "TO DISABLE THE CUSTOM RECIPE JUST REMOVE THE \"recipe\" FIELD\n" +
                    "To configure a shaped recipe, set three fields: \"row1\",\"row2\",\"row3\" with three material names separated by a \",\"\n" +
                    "at example:\n" +
                    "  recipe:\n" +
                    "    row1: \"AIR,GOLD_INGOT,AIR\"\n" +
                    "    row2: \"GOLD_INGOT,NETHER_STAR,GOLD_INGOT\"\n" +
                    "    row3: \"AIR,GOLD_INGOT,AIR\"\n" +
                    "YOU WILL GET A CONSOLE ERROR IF THERE AREN'T THREE MATERIALS PER ROW OR IF ONE OF THESE MATERIALS DOES NOT EXIST.\n" +
                    "To configure a shapeless recipe, set a list field: \"shapeless\" with all the crafting ingredients materials separated by a \",\"\n" +
                    "NOTE THAT: if you set a shapeless field, the plugin will ignore the rows system.\n" +
                    "at example:\n" +
                    "  recipe:\n" +
                    "    shapeless:\n" +
                    "    - GOLD_INGOT\n" +
                    "    - NETHER_STAR\n" +
                    "this means that the ingredients combination the player will use in the crafting table is not relevant, just\n" +
                    "put at least one of these per slot\n" +
                    "- do_not_cancel: if set to true, all the interaction events triggered by clicking on a rune item will not be canceled (useful to allow a player to place a block rune or to wear a magical armor with the right click)\n" +
                    "\n" +
                    "Every rune item configuration can have all these fields:\n" +
                    "- displayname: the name of the item (you can color the text by using the \"&\" character)\n" +
                    "- material: the item material type\n" +
                    "- durability: if it's a weapon or armor, the damage to apply to it\n" +
                    "- lore: a list field that is the description/lore of the item (you can color the text by using the \"&\" character)\n" +
                    "- enchantments: a list field for the item enchantments, for example:\n" +
                    "    enchantments:\n" +
                    "    - LUCK,1\n" +
                    "    - DAMAGE_ALL,2 (DAMAGE_ALL is Sharpness)\n" +
                    "- flags: a list field for the item flags, for example:\n" +
                    "    flags:\n" +
                    "    - HIDE_ENCHANTS\n" +
                    "    - HIDE_ATTRIBUTES\n" +
                    "- unbreakable: set to true if you want the item to be unbreakable\n" +
                    "- model_data: set a model data id to use with a custom ResourcesPack");
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
                List<String> new_heal_rune_item_enchantments = new ArrayList<>();
                new_heal_rune_item_enchantments.add("LUCK,1");
                runesConfig.set("heal_rune.item.enchantments", new_heal_rune_item_enchantments);
                List<String> new_heal_rune_item_flags = new ArrayList<>();
                new_heal_rune_item_flags.add("HIDE_ENCHANTS");
                runesConfig.set("heal_rune.item.flags", new_heal_rune_item_flags);
                runesConfig.set("heal_rune.item.model_data", 1234567);
                List<String> new_heal_rune_commands = new ArrayList<>();
                new_heal_rune_commands.add("sound:ENTITY_PLAYER_LEVELUP,1,3");
                new_heal_rune_commands.add("particle:SNEEZE,%world%,%x%,%y%,%z%,1000,1,1,1,0");
                new_heal_rune_commands.add("effect:REGENERATION,90,4,false,false,true");
                runesConfig.set("heal_rune.commands", new_heal_rune_commands);
                runesConfig.set("heal_rune.type", "CONSUMABLE");
                runesConfig.set("heal_rune.delay", 0);
                runesConfig.set("heal_rune.recipe.row1", "AIR,SWEET_BERRIES,AIR");
                runesConfig.set("heal_rune.recipe.row2", "SWEET_BERRIES,STONE_BUTTON,SWEET_BERRIES");
                runesConfig.set("heal_rune.recipe.row3", "AIR,SWEET_BERRIES,AIR");

                runesConfig.set("strength_chestplate.displayname", "&9Magic Chestplate");
                runesConfig.set("strength_chestplate.item.displayname", "&9Magic Chestplate");
                runesConfig.set("strength_chestplate.item.material", "IRON_CHESTPLATE");
                runesConfig.set("strength_chestplate.item.durability", 0);
                List<String> new_strength_chestplate_item_lore = new ArrayList<>();
                new_strength_chestplate_item_lore.add("&7Wear this &9Magic Chestplate");
                new_strength_chestplate_item_lore.add("&7to keep an effect of strength II");
                runesConfig.set("strength_chestplate.item.lore", new_strength_chestplate_item_lore);
                List<String> new_strength_chestplate_item_enchantment = new ArrayList<>();
                new_strength_chestplate_item_enchantment.add("LUCK,1");
                runesConfig.set("strength_chestplate.item.enchantments", new_strength_chestplate_item_enchantment);
                List<String> new_strength_chestplate_item_flags = new ArrayList<>();
                new_strength_chestplate_item_flags.add("HIDE_ENCHANTS");
                runesConfig.set("strength_chestplate.item.flags", new_strength_chestplate_item_flags);
                List<String> new_strength_chestplate_commands = new ArrayList<>();
                new_strength_chestplate_commands.add("sound:BLOCK_LAVA_EXTINGUISH,1,2");
                new_strength_chestplate_commands.add("particle:FLAME,%world%,%x%,%y%,%z%,50,0.5,1,0.5,0");
                new_strength_chestplate_commands.add("effect:INCREASE_DAMAGE,1200,1,false,false,true");
                runesConfig.set("strength_chestplate.commands", new_strength_chestplate_commands);
                runesConfig.set("strength_chestplate.type", "KEEP_EFFECT");
                runesConfig.set("strength_chestplate.delay", 1200);
                List<String> new_strength_chestplate_shapeless_recipe = new ArrayList<>();
                new_strength_chestplate_shapeless_recipe.add("IRON_CHESTPLATE");
                new_strength_chestplate_shapeless_recipe.add("BLAZE_ROD");
                runesConfig.set("strength_chestplate.recipe.shapeless", new_strength_chestplate_shapeless_recipe);
                runesConfig.set("strength_chestplate.do_not_cancel", true);

                runesConfig.set("lightning_pointer.displayname", "&eLightning Pointer");
                runesConfig.set("lightning_pointer.item.displayname", "&eLightning Pointer");
                runesConfig.set("lightning_pointer.item.material", "LEVER");
                runesConfig.set("lightning_pointer.item.durability", 0);
                List<String> new_lightning_pointer_item_lore = new ArrayList<>();
                runesConfig.set("lightning_pointer.item.lore", new_lightning_pointer_item_lore);
                List<String> new_lightning_pointer_item_enchantment = new ArrayList<>();
                new_lightning_pointer_item_enchantment.add("LUCK,1");
                runesConfig.set("lightning_pointer.item.enchantments", new_lightning_pointer_item_enchantment);
                List<String> new_lightning_pointer_item_flags = new ArrayList<>();
                new_lightning_pointer_item_flags.add("HIDE_ENCHANTS");
                runesConfig.set("lightning_pointer.item.flags", new_lightning_pointer_item_flags);
                List<String> new_lightning_pointer_commands = new ArrayList<>();
                new_lightning_pointer_commands.add("lightning");
                runesConfig.set("lightning_pointer.commands", new_lightning_pointer_commands);
                runesConfig.set("lightning_pointer.type", "INFINITE");
                runesConfig.set("lightning_pointer.delay", 0);
                runesConfig.set("lightning_pointer.recipe.row1", "AIR,LIGHTNING_ROD,AIR");
                runesConfig.set("lightning_pointer.recipe.row2", "AIR,LEVER,AIR");
                runesConfig.set("lightning_pointer.recipe.row3", "AIR,AIR,AIR");

                runesConfig.set("useless_sword.displayname", "Useless Wooden Sword");
                runesConfig.set("useless_sword.item.displayname", "Useless Wooden Sword");
                runesConfig.set("useless_sword.item.material", "WOODEN_SWORD");
                runesConfig.set("useless_sword.item.durability", 0);
                List<String> new_useless_sword_item_lore = new ArrayList<>();
                new_useless_sword_item_lore.add("&7An useless unbreakable sword..");
                runesConfig.set("useless_sword.item.lore", new_useless_sword_item_lore);
                List<String> new_useless_sword_item_enchantment = new ArrayList<>();
                runesConfig.set("useless_sword.item.enchantments", new_useless_sword_item_enchantment);
                List<String> new_useless_sword_item_flags = new ArrayList<>();
                runesConfig.set("useless_sword.item.flags", new_useless_sword_item_flags);
                runesConfig.set("useless_sword.item.unbreakable", true);
                List<String> new_useless_sword_commands = new ArrayList<>();
                runesConfig.set("useless_sword.commands", new_useless_sword_commands);
                runesConfig.set("useless_sword.type", "INFINITE");
                runesConfig.set("useless_sword.delay", 0);
                List<String> new_useless_sword_shapeless_recipe = new ArrayList<>();
                new_useless_sword_shapeless_recipe.add("WOODEN_SWORD");
                new_useless_sword_shapeless_recipe.add("WOODEN_SWORD");
                runesConfig.set("useless_sword.recipe.shapeless", new_useless_sword_shapeless_recipe);
                runesConfig.set("useless_sword.do_not_cancel", true);
            }
            runesConfig.addDefault("config_version", 1);

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
            you_got_rune = getConfig().getString("messages.you_got_rune", "&5You got x%amount% %rune%");
            help_admin = getConfig().getStringList("messages.help_admin");
            help_user = getConfig().getStringList("messages.help_user");

            RunesHandler.reload();

            try {
                bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                bukkitCommandMap.setAccessible(true);
            } catch (NoSuchFieldException noSuchFieldException) {
                noSuchFieldException.printStackTrace();
            }

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

    public static boolean isBukkitCommand(String paramString) {
        paramString = paramString.split(" ")[0];
        try {
            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) bukkitCommandMap.get(Bukkit.getServer());
            for (Command command : simpleCommandMap.getCommands()) {
                if (command.getName().equalsIgnoreCase(paramString) || command.getAliases().contains(paramString))
                    return true;
            }
        } catch (IllegalAccessException ex) {
            Vars.plugin.getLogger().warning("An exception occurred while trying to retrieve and/or use the commandMap");
            ex.printStackTrace();
        }
        return false;
    }

    public static String locationToString(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
    }
    public static String locationBlockToString(Location loc, Boolean include_world) {
        if (include_world) {
            return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
        }
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    public static Location stringToLocation(String strloc) {
        try {
            String[] strloc_ = strloc.split(",");
            if (strloc_.length > 5) {
                return new Location(Bukkit.getWorld(strloc_[0]), Double.parseDouble(strloc_[1]), Double.parseDouble(strloc_[2]), Double.parseDouble(strloc_[3]), Float.parseFloat(strloc_[4]), Float.parseFloat(strloc_[5]));
            } else {
                return new Location(Bukkit.getWorld(strloc_[0]), Double.parseDouble(strloc_[1]), Double.parseDouble(strloc_[2]), Double.parseDouble(strloc_[3]));
            }
        } catch (Exception ex) {
            Vars.plugin.getLogger().info("An exception occurred while trying to load a location from string: " + strloc);
            ex.printStackTrace();
            return null;
        }
    }

}
