package cloud.stivenfocs.MagicRunes.Rune;

import cloud.stivenfocs.MagicRunes.Vars;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Rune {

    public final String name;
    public final String displayname;
    public final ItemStack item;
    public final RuneType type;
    protected final List<String> commands;
    public final Integer delay;

    public Rune(String name, String displayname, ItemStack item, RuneType type, List<String> commands, Integer delay) {
        this.name = name;
        this.displayname = displayname;
        this.item = item;
        this.type = type;
        this.commands = commands;
        this.delay = delay;
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

    public void runCommands(Player p) {
        for (String command : getCommands()) {
            try {
                command = command.replaceAll("%player_name%", p.getName()).replaceAll("%player_displayname%", p.getDisplayName()).replaceAll("%player_uuid%", p.getUniqueId().toString()).replaceAll("%world%", p.getWorld().getName()).replaceAll("%x%", String.valueOf(p.getLocation().getX())).replaceAll("%y%", String.valueOf(p.getLocation().getY())).replaceAll("%z%", String.valueOf(p.getLocation().getZ()));
                if (command.startsWith("tell:")) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', command.replaceAll("tell:", "")));
                } else if (command.startsWith("sudo:")) {
                    command = command.replaceAll("sudo:", "");
                    p.chat("/" + command);
                } else if (command.startsWith("permitted_sudo:")) {
                    command = command.replaceAll("permitted_sudo:", "");
                    boolean op = p.isOp();

                    p.setOp(true);
                    p.chat("/" + command);
                    p.setOp(op);
                } else if (command.startsWith("sound:")) {
                    String[] sound = command.replaceAll("sound:", "").split(",");
                    p.playSound(p.getLocation(), Sound.valueOf(sound[0].toUpperCase()), Integer.parseInt(sound[1]), Integer.parseInt(sound[2]));
                } else if (command.startsWith("particle:")) {
                    String[] particle = command.replaceAll("particle:", "").split(",");
                    p.getWorld().spawnParticle(Particle.valueOf(particle[0].toUpperCase()), Double.parseDouble(particle[1]), Double.parseDouble(particle[2]), Double.parseDouble(particle[3]), Integer.parseInt(particle[4]), Double.parseDouble(particle[5]), Double.parseDouble(particle[6]), Double.parseDouble(particle[7]), Double.parseDouble(particle[8]));
                } else if (command.equalsIgnoreCase("fireball_cannon")) {
                    p.launchProjectile(Fireball.class);
                } else if (command.equalsIgnoreCase("egg_cannon")) {
                    p.launchProjectile(Egg.class);
                } else if (command.equalsIgnoreCase("snowball_cannon")) {
                    p.launchProjectile(Snowball.class);
                } else if (command.equalsIgnoreCase("lightning")) {
                    p.getWorld().strikeLightning(p.getTargetBlock(null, 50).getLocation());
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            } catch (Exception ex) {
                Vars.plugin.getLogger().warning("Unable to run the commnd: " + command);
                Vars.plugin.getLogger().warning(ex.getMessage());
            }
        }
    }

}
