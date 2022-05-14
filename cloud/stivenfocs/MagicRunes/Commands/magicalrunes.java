package cloud.stivenfocs.MagicRunes.Commands;

import cloud.stivenfocs.MagicRunes.Loader;
import cloud.stivenfocs.MagicRunes.Rune.Rune;
import cloud.stivenfocs.MagicRunes.Rune.RunesHandler;
import cloud.stivenfocs.MagicRunes.Vars;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class magicalrunes implements CommandExecutor, TabCompleter {

    private Loader plugin;

    public magicalrunes(Loader plugin) {
        this.plugin = plugin;
    }

    /////////////////////////////

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 0) {
            if (Vars.hasAdminPermission("help", sender)) {
                Vars.sendStringList(Vars.help_admin, sender);
            } else {
                Vars.sendStringList(Vars.help_user, sender);
            }
        } else {
            if (args[0].equalsIgnoreCase("reload")) {
                if (Vars.hasAdminPermission("reload", sender)) {
                    if (!(sender instanceof ConsoleCommandSender)) {
                        if (Vars.getVars().reloadVars()) {
                            Vars.sendString(Vars.configuration_reloaded, sender);
                        } else {
                            Vars.sendString(Vars.an_error_occurred, sender);
                        }
                    } else {
                        Vars.getVars().reloadVars();
                    }
                } else {
                    Vars.sendString(Vars.no_permission, sender);
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("give")) {
                if (Vars.hasAdminPermission("give", sender)) {
                    if (args.length > 2) {
                        Player player = Bukkit.getPlayerExact(args[1]);
                        if (player != null) {
                            String rune_name = args[2];
                            if (Vars.getVars().runesConfig.get(rune_name) != null) {
                                Rune rune = RunesHandler.getRuneByName(rune_name);
                                if (rune.getItem() != null) {
                                    int emptySlots = 0;
                                    for (ItemStack i : player.getInventory().getContents()) {
                                        if (i == null) {
                                            emptySlots++;
                                        } else if (i.getType().equals(Material.AIR)) {
                                            emptySlots++;
                                        } else if (i.getType().equals(rune.getItem().getType()) && i.getAmount() < i.getMaxStackSize()) {
                                            emptySlots++;
                                        }
                                    }

                                    if (emptySlots > 0) {
                                        ItemStack rune_item = rune.getItem();
                                        if (args.length > 3 && Vars.isDigit(args[3])) {
                                            rune_item.setAmount(Integer.parseInt(args[3]));
                                        }

                                        player.getInventory().addItem(rune_item);

                                        String sender_displayname = sender.getName();
                                        if (sender instanceof Player) sender_displayname = ((Player) sender).getDisplayName();
                                        Vars.sendString(Vars.you_got_rune.replaceAll("%amount%", String.valueOf(rune_item.getAmount())).replaceAll("%player_name%", sender.getName()).replaceAll("%player_displayname%", sender_displayname).replaceAll("%rune%", rune.getDisplayName()), player);
                                        if (!player.getName().equals(sender.getName())) {
                                            Vars.sendString(Vars.you_given_rune.replaceAll("%amount%", String.valueOf(rune_item.getAmount())).replaceAll("%player_name%", player.getName()).replaceAll("%player_displayname%", player.getDisplayName()).replaceAll("%rune%", rune.getDisplayName()), sender);
                                        }
                                    } else {
                                        Vars.sendString(Vars.inventory_full, sender);
                                    }
                                } else {
                                    Vars.sendString(Vars.no_valid_rune, sender);
                                }
                            } else {
                                Vars.sendString(Vars.rune_not_found, sender);
                            }
                        } else {
                            Vars.sendString(Vars.player_not_found, sender);
                        }
                    } else {
                        Vars.sendString(Vars.incomplete_command, sender);
                    }
                } else {
                    Vars.sendString(Vars.no_permission, sender);
                }
                return true;
            }

            Vars.sendString(Vars.unknown_subcommand, sender);
        }
        return false;
    }

    /////////////////////////////

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> su = new ArrayList<>();

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("")) {
                if (Vars.hasAdminPermission("reload", sender)) {
                    su.add("reload");
                }
                if (Vars.hasAdminPermission("give", sender)) {
                    su.add("give");
                }
            } else {
                if (Vars.hasAdminPermission("reload", sender)) {
                    if ("reload".startsWith(args[0].toLowerCase())) {
                        su.add("reload");
                    }
                }
                if (Vars.hasAdminPermission("give", sender)) {
                    if ("give".startsWith(args[0].toLowerCase())) {
                        su.add("give");
                    }
                }
            }
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("")) {
                if (args[0].equalsIgnoreCase("give")) {
                    if (Vars.hasAdminPermission("give", sender)) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            su.add(player.getName());
                        }
                    }
                }
            } else {
                if (args[0].equalsIgnoreCase("give")) {
                    if (Vars.hasAdminPermission("give", sender)) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                                su.add(player.getName());
                            }
                        }
                    }
                }
            }
        } else if (args.length == 3) {
            if (args[2].equalsIgnoreCase("")) {
                if (args[0].equalsIgnoreCase("give")) {
                    if (Vars.hasAdminPermission("give", sender)) {
                        for (String rune_name : Vars.getVars().runesConfig.getKeys(false)) {
                            su.add(rune_name);
                        }
                    }
                }
            } else {
                if (args[0].equalsIgnoreCase("give")) {
                    if (Vars.hasAdminPermission("give", sender)) {
                        for (String rune_name : Vars.getVars().runesConfig.getKeys(false)) {
                            if (rune_name.startsWith(args[2].toLowerCase())) {
                                su.add(rune_name);
                            }
                        }
                    }
                }
            }
        }

        return su;
    }
}
