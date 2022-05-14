package cloud.stivenfocs.MagicRunes;

import cloud.stivenfocs.MagicRunes.Commands.magicalrunes;
import cloud.stivenfocs.MagicRunes.Rune.Events;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Loader extends JavaPlugin {

    public void onEnable() {
        Vars.plugin = this;
        Vars vars = Vars.getVars();

        vars.reloadVars();

        getCommand("magicalrunes").setExecutor(new magicalrunes(this));
        getCommand("magicalrunes").setTabCompleter(new magicalrunes(this));

        Bukkit.getPluginManager().registerEvents(new Events(this), this);
    }

}
