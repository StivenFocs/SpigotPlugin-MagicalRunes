package cloud.stivenfocs.MagicRunes.Rune;

import cloud.stivenfocs.MagicRunes.Vars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;

import java.util.ArrayList;
import java.util.List;

public class RunesHandler {

    public static List<Rune> runes = new ArrayList<>();
    protected static List<RunesPluginEvents> events_listeners = new ArrayList<>();

    public static void reload() {
        for (Rune rune : runes) {
            Bukkit.getServer().removeRecipe(new NamespacedKey(Vars.plugin, rune.getName()));
        }
        runes = new ArrayList<>();
        FileConfiguration runesConfig = Vars.getVars().runesConfig;

        for (String rune_name : runesConfig.getKeys(false)) {
            String rune_displayname = runesConfig.getString(rune_name + ".displayname", rune_name);

            ItemStack i = new ItemStack(Material.STONE_BUTTON);
            if (Vars.debug) Vars.plugin.getLogger().info("Getting the rune " + rune_name + " item");
            String path = rune_name + ".item.";
            if (Vars.debug) Vars.plugin.getLogger().info("path: " + path);
            if (runesConfig.get(rune_name + ".item") == null) {
                throw new NullPointerException("Item not found for the rune " + rune_name);
            }

            try {
                try {
                    if (Vars.debug) Vars.plugin.getLogger().info("Creating ItemStack");
                    i = new ItemStack(Material.valueOf(runesConfig.getString(path + "material").toUpperCase()));
                } catch (Exception ex) {
                    Vars.plugin.getLogger().warning("Couldn't find the material for the rune " + rune_name + " item...");
                }
                if (Vars.debug) Vars.plugin.getLogger().info("Setting durability");
                i.setDurability((short) runesConfig.getInt(path + "durability"));
                if (Vars.debug) Vars.plugin.getLogger().info("Getting itemMeta");
                ItemMeta iMeta = i.getItemMeta();
                if (Vars.debug) Vars.plugin.getLogger().info("Applying displayname");
                iMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', runesConfig.getString(path + "displayname")));
                if (Vars.debug) Vars.plugin.getLogger().info("Applying lore");
                iMeta.setLore(Vars.colorList(runesConfig.getStringList(path + "lore")));
                if (runesConfig.getBoolean(path + "glow")) {
                    if (Vars.debug) Vars.plugin.getLogger().info("Applying glow");
                    iMeta.addEnchant(Enchantment.PROTECTION_FALL, 1, true);
                    iMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }

                iMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                iMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                iMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                iMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                iMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
                iMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                if (Vars.debug) Vars.plugin.getLogger().info("Applying new item meta: " + iMeta);

                iMeta.getCustomTagContainer().setCustomTag(new NamespacedKey(Vars.plugin, "rune_name"), ItemTagType.STRING, rune_name);

                i.setItemMeta(iMeta);

            } catch (Exception ex) {
                Vars.plugin.getLogger().warning("An exception occurred while trying to generate the rune " + rune_name + " item, check the configuration...");
                ex.printStackTrace();
                throw new NullPointerException("Couldn't get a valid rune " + rune_name + " item...");
            }

            if (runesConfig.get(rune_name + ".recipe") != null) {
                try {
                    ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Vars.plugin, rune_name), i);

                    List<String> rows = new ArrayList<>();
                    rows.add(runesConfig.getString(rune_name + ".recipe.row1", "").toUpperCase());
                    rows.add(runesConfig.getString(rune_name + ".recipe.row2", "").toUpperCase());
                    rows.add(runesConfig.getString(rune_name + ".recipe.row3", "").toUpperCase());
                    List<Material> ingredients = new ArrayList<>();
                    for (String row : rows) {
                        for (String material_string : row.split(",")) {
                            ingredients.add(Material.valueOf(material_string.toUpperCase()));
                        }
                    }

                    recipe.shape("ABC", "DEF", "GHI");

                    recipe.setIngredient('A', ingredients.get(0));
                    recipe.setIngredient('B', ingredients.get(1));
                    recipe.setIngredient('C', ingredients.get(2));
                    recipe.setIngredient('D', ingredients.get(3));
                    recipe.setIngredient('E', ingredients.get(4));
                    recipe.setIngredient('F', ingredients.get(5));
                    recipe.setIngredient('G', ingredients.get(6));
                    recipe.setIngredient('H', ingredients.get(7));
                    recipe.setIngredient('I', ingredients.get(8));

                    Bukkit.getServer().addRecipe(recipe);
                } catch (Exception ex) {
                    Vars.plugin.getLogger().warning("Unable to add the rune " + rune_name + " recipe. Reason:" + ex.getMessage());
                }
            }

            RuneType type = RuneType.INFINITE;
            try {
                type = RuneType.valueOf(runesConfig.getString(rune_name + ".type").toUpperCase());
            } catch (Exception ex) {
                Vars.plugin.getLogger().warning("unknown rune " + rune_name + " type...");
            }

            List<String> commands = new ArrayList<>();
            try {
                commands = runesConfig.getStringList(rune_name + ".commands");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            Integer delay = runesConfig.getInt(rune_name + ".delay");

            runes.add(new Rune(rune_name, rune_displayname, i, type, commands, delay));
        }

        for (RunesPluginEvents listener : events_listeners) {
            try {
                listener.RunesReloadEvent();
            } catch (Exception ex) {
                Vars.plugin.getLogger().warning("An exception occurred while trying to call a listener for the event 'RunesReloadEvent'");
                ex.printStackTrace();
            }
        }
    }

    public static void addEventsListener(RunesPluginEvents listener) {
        if (!events_listeners.contains(listener)) {
            events_listeners.add(listener);
        }
    }
    public static void removeEventsListener(RunesPluginEvents listener) {
        events_listeners.remove(listener);
    }

    public static Rune getRuneByName(String rune_name) {
        for (Rune rune : runes) {
            if (rune_name.equals(rune.getName())) {
                return rune;
            }
        }

        return null;
    }

    public static Rune isRuneItem(ItemStack item) {
        String rune_name = null;
        try {
            rune_name =  item.getItemMeta().getCustomTagContainer().getCustomTag(new NamespacedKey(Vars.plugin, "rune_name"), ItemTagType.STRING);
        } catch (Exception ignored) {}
        if (rune_name == null) return null;
        return getRuneByName(rune_name);
    }

}
