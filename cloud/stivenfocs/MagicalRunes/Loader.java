package cloud.stivenfocs.MagicalRunes;

import cloud.stivenfocs.MagicalRunes.Commands.magicalrunes;
import cloud.stivenfocs.MagicalRunes.Rune.Events;
import cloud.stivenfocs.MagicalRunes.Rune.Rune;
import cloud.stivenfocs.MagicalRunes.Rune.RunesHandler;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class Loader extends JavaPlugin {

    public void onEnable() {
        Vars.plugin = this;
        Vars vars = Vars.getVars();

        vars.reloadVars();

        getCommand("magicalrunes").setExecutor(new magicalrunes(this));
        getCommand("magicalrunes").setTabCompleter(new magicalrunes(this));

        Bukkit.getPluginManager().registerEvents(new Events(this), this);

        new Metrics(this, 15210);
    }

    public void onDisable() {
        for (Rune rune : RunesHandler.runes) {
            Bukkit.getServer().removeRecipe(new NamespacedKey(Vars.plugin, rune.getName()));
        }
    }

}
