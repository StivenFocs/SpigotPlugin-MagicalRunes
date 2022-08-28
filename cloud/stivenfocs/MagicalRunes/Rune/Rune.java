package cloud.stivenfocs.MagicalRunes.Rune;

import cloud.stivenfocs.MagicalRunes.Vars;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Rune {

    public final String name;
    public final String displayname;
    public final ItemStack item;
    public final RuneType type;
    protected final List<String> commands;
    public final Integer delay;
    public final Boolean do_not_cancel;

    public Rune(String name, String displayname, ItemStack item, RuneType type, List<String> commands, Integer delay, Boolean do_not_cancel) {
        this.name = name;
        this.displayname = displayname;
        this.item = item;
        this.type = type;
        this.commands = commands;
        this.delay = delay;
        this.do_not_cancel = do_not_cancel;
    }

    /////////////////////////////

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayname;
    }

    public ItemStack getItem() {
        if (item != null) {
            return item.clone();
        }

        return null;
    }

    public RuneType getType() {
        return type;
    }

    public List<String> getCommands() {
        return new ArrayList<>(commands);
    }

    public Integer getDelay() {
        return delay;
    }

    public Boolean doNotCancel() {
        return do_not_cancel;
    }

    public void runCommands(Player p) {
        for (String command : getCommands()) {
            try {
                command = command.replaceAll("%player_name%", p.getName()).replace("%player_displayname%", p.getDisplayName()).replaceAll("%player_uuid%", p.getUniqueId().toString()).replaceAll("%world%", p.getWorld().getName()).replaceAll("%x%", String.valueOf(p.getLocation().getX())).replaceAll("%y%", String.valueOf(p.getLocation().getY())).replaceAll("%z%", String.valueOf(p.getLocation().getZ()));
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    command = PlaceholderAPI.setPlaceholders(p, command);
                }

                if (command.startsWith("tell:")) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', command.replace("tell:", "")));
                } else if (command.startsWith("broadcast:")) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', command.replace("broadcast:", "")));
                } else if (command.startsWith("title")) {
                    String[] title = command.replaceAll("title:", "").split(",");
                    p.sendTitle(ChatColor.translateAlternateColorCodes('&', title[0]),ChatColor.translateAlternateColorCodes('&', title[1]),Integer.valueOf(title[2]),Integer.valueOf(title[3]),Integer.valueOf(title[4]));
                } else if (command.startsWith("actionbar")) {
                    String actionbar = command.replaceAll("actionbar:", "");
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', actionbar)));
                } else if (command.startsWith("sudo:")) {
                    command = command.replaceAll("sudo:", "");
                    if (Vars.isBukkitCommand(command)) {
                        p.performCommand(command);
                    } else {
                        p.chat("/" + command);
                    }
                } else if (command.startsWith("permitted_sudo:")) {
                    command = command.replaceAll("permitted_sudo:", "");
                    boolean op = p.isOp();

                    p.setOp(true);
                    if (Vars.isBukkitCommand(command)) {
                        try {
                            p.performCommand(command);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        p.chat("/" + command);
                    }
                    p.setOp(op);
                } else if (command.startsWith("sound:")) {
                    command = command.replaceAll(" ","");
                    String[] sound = command.replaceAll("sound:", "").split(",");
                    p.playSound(p.getLocation(), Sound.valueOf(sound[0].toUpperCase()), Integer.parseInt(sound[1]), Integer.parseInt(sound[2]));
                } else if (command.startsWith("effect:")) {
                    command = command.replaceAll(" ","");
                    String[] effect = command.replaceAll("effect:", "").split(",");
                    p.addPotionEffect(new PotionEffect(PotionEffectType.getByName(effect[0]),Integer.parseInt(effect[1]),Integer.parseInt(effect[2]),Boolean.parseBoolean(effect[3]),Boolean.parseBoolean(effect[4]), Boolean.parseBoolean(effect[5])));
                } else if (command.startsWith("particle:")) {
                    command = command.replaceAll(" ","");
                    String[] particle = command.replaceAll("particle:", "").split(",");
                    Bukkit.getWorld(particle[1]).spawnParticle(Particle.valueOf(particle[0].toUpperCase()), Double.parseDouble(particle[2]), Double.parseDouble(particle[3]), Double.parseDouble(particle[4]), Integer.parseInt(particle[5]), Double.parseDouble(particle[6]), Double.parseDouble(particle[7]), Double.parseDouble(particle[8]), Double.parseDouble(particle[9]));
                } else if (command.startsWith("kill")) {
                    p.setHealth(0);
                }  else if (command.startsWith("teleport")) {
                    String teleport = command.replaceAll("teleport:", "");
                    p.teleport(Vars.stringToLocation(teleport));
                } else if (command.equalsIgnoreCase("fireball_cannon")) {
                    p.launchProjectile(Fireball.class);
                } else if (command.equalsIgnoreCase("egg_cannon")) {
                    p.launchProjectile(Egg.class);
                } else if (command.equalsIgnoreCase("snowball_cannon")) {
                    p.launchProjectile(Snowball.class);
                } else if (command.equalsIgnoreCase("lightning")) {
                    Block target_block = p.getTargetBlock(null, 50);
                    if (!target_block.getType().equals(Material.AIR)) {
                        p.getWorld().strikeLightning(target_block.getLocation());
                    }
                } else if (command.toLowerCase().startsWith("closeinventory")) {
                    p.closeInventory();
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            } catch (Exception ex) {
                Vars.plugin.getLogger().warning("Unable to run the command: " + command + " Reason: " + ex.getMessage());
            }
        }
    }

}
