package cloud.stivenfocs.MagicalRunes.Rune;

import cloud.stivenfocs.MagicalRunes.Vars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
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

        List<String> new_runes = new ArrayList<>(runesConfig.getKeys(false));
        new_runes.remove("config_version");
        for (String rune_name : new_runes) {
            try {
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
                    for (String enchantment_string : runesConfig.getStringList(path + "enchantments")) {
                        try {
                            String[] enchantment = enchantment_string.replaceAll(" ", "").split(",");
                            i.addUnsafeEnchantment(Enchantment.getByName(enchantment[0]), Integer.parseInt(enchantment[1]));
                        } catch (Exception ex) {
                            Vars.plugin.getLogger().warning("Unable to parse the enchantment " + enchantment_string + " for the rune " + rune_name + ". Reason: " + ex.getMessage());
                        }
                    }
                    ItemMeta iMeta = i.getItemMeta();
                    if (Vars.debug) Vars.plugin.getLogger().info("Applying displayname");
                    iMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', runesConfig.getString(path + "displayname")));
                    if (Vars.debug) Vars.plugin.getLogger().info("Applying lore");
                    iMeta.setLore(Vars.colorList(runesConfig.getStringList(path + "lore")));
                    for (String flag_string : runesConfig.getStringList(path + "flags")) {
                        try {
                            iMeta.addItemFlags(ItemFlag.valueOf(flag_string));
                        } catch (Exception ex) {
                            Vars.plugin.getLogger().warning("Unable to parse the enchantment " + flag_string + " for the rune " + rune_name + ". Reason: " + ex.getMessage());
                        }
                    }
                    if (runesConfig.getBoolean(path + "unbreakable")) iMeta.setUnbreakable(true);
                    iMeta.getCustomTagContainer().setCustomTag(new NamespacedKey(Vars.plugin, "rune_name"), ItemTagType.STRING, rune_name);
                    try {
                        if (runesConfig.get(path + "model_data") != null) {
                            iMeta.setCustomModelData(runesConfig.getInt(path + "model_data"));
                        }
                    } catch (Exception ex) {
                        Vars.plugin.getLogger().warning("Unable to set model data to the rune " + rune_name + " Reason: " + ex.getMessage());
                    }
                    if (Vars.debug) Vars.plugin.getLogger().info("Applying new item meta: " + iMeta);
                    i.setItemMeta(iMeta);

                } catch (Exception ex) {
                    Vars.plugin.getLogger().warning("An exception occurred while trying to generate the rune " + rune_name + " item, check the configuration...");
                    ex.printStackTrace();
                    throw new NullPointerException("Couldn't get a valid rune " + rune_name + " item...");
                }

                if (runesConfig.get(rune_name + ".recipe") != null) {
                    if (runesConfig.get(rune_name + ".recipe.shapeless") != null) {
                        try {
                            ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(Vars.plugin, rune_name), i);

                            for (String material_string : runesConfig.getStringList(rune_name + ".recipe.shapeless")) {
                                recipe.addIngredient(Material.valueOf(material_string.toUpperCase()));
                            }

                            Bukkit.getServer().addRecipe(recipe);
                        } catch (Exception ex) {
                            Vars.plugin.getLogger().warning("Unable to register the shapeless recipe of the rune " + rune_name + " Reason: " + ex.getMessage());
                        }
                    } else {
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
                            Vars.plugin.getLogger().warning("Unable to add the rune " + rune_name + " recipe. Reason: " + ex.getMessage());
                        }
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
                Boolean do_not_cancel = runesConfig.getBoolean(rune_name + ".do_not_cancel");

                runes.add(new Rune(rune_name, rune_displayname, i, type, commands, delay, do_not_cancel));

                Vars.plugin.getLogger().info("Loaded item: " + rune_name);
            } catch (Exception ex) {
                Vars.plugin.getLogger().warning("Unable to load the rune " + rune_name + " Reason: internal error");
                ex.printStackTrace();
            }
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
