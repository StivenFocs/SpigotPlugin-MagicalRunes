package cloud.stivenfocs.MagicRunes.Rune;

import cloud.stivenfocs.MagicRunes.Loader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class Events implements Listener {

    private final Loader plugin;
    public int BukkitTask;

    public HashMap<UUID, Integer> players_timers = new HashMap<>();

    public Events(Loader plugin) {
        this.plugin = plugin;

        BukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                Boolean do_have_runes = false;

                ItemStack item = p.getInventory().getItemInMainHand();
                if (item == null) item = p.getInventory().getItemInOffHand();

                if (item != null) {
                    Rune rune = RunesHandler.isRuneItem(item);
                    if (rune != null) {
                        do_have_runes = true;
                        if (rune.getType().equals(RuneType.HELD_EFFECT)) {
                            if (!players_timers.containsKey(p.getUniqueId())) {
                                players_timers.put(p.getUniqueId(), 0);
                            } else {
                                if (players_timers.get(p.getUniqueId()) >= (rune.getDelay() - 1)) {
                                    players_timers.put(p.getUniqueId(), 0);
                                    rune.runCommands(p);
                                } else {
                                    players_timers.put(p.getUniqueId(), players_timers.get(p.getUniqueId()) + 1);
                                }
                            }
                        }
                    }
                }

                for (ItemStack item_ : p.getInventory().getContents()) {
                    if (item_ != null) {
                        Rune rune = RunesHandler.isRuneItem(item_);
                        if (rune != null) {
                            do_have_runes = true;
                            if (rune.getType().equals(RuneType.KEEP_EFFECT)) {
                                if (!players_timers.containsKey(p.getUniqueId())) {
                                    players_timers.put(p.getUniqueId(), 0);
                                } else {
                                    if (players_timers.get(p.getUniqueId()) >= rune.getDelay()) {
                                        players_timers.put(p.getUniqueId(), 0);
                                        rune.runCommands(p);
                                    } else {
                                        players_timers.put(p.getUniqueId(), players_timers.get(p.getUniqueId()) + 1);
                                    }
                                }
                            }
                        }
                    }
                }

                if (!do_have_runes) {
                    players_timers.remove(p.getUniqueId());
                }

            }
        }, 0L, 20L).getTaskId();
    }

    /////////////////////////////

    @EventHandler
    public void itemInteractEvent(PlayerInteractEvent event) {
        Player p = event.getPlayer();

        if (!event.getAction().equals(Action.PHYSICAL)) {
            ItemStack item = event.getItem();

            if (p.getTargetBlock(null, 4).getType().isInteractable() && !p.isSneaking()) return;
            if (item != null) {
                Rune rune = RunesHandler.isRuneItem(item);
                if (rune != null) {
                    if (rune.getItem().getType().isEdible()) return;
                    event.setCancelled(true);

                    if (rune.getType().equals(RuneType.CONSUMABLE) || rune.getType().equals(RuneType.INFINITE)) {
                        if (rune.getType().equals(RuneType.CONSUMABLE)) {
                            if (item.getAmount() > 1) {
                                item.setAmount(item.getAmount() - 1);
                            } else {
                                p.getInventory().remove(item);
                            }
                        }
                        rune.runCommands(p);
                    }
                }
            }
        }
    }

    @EventHandler
    public void itemEatEvent(PlayerItemConsumeEvent event) {
        Player p = event.getPlayer();

        ItemStack item = event.getItem();
        Rune rune = RunesHandler.isRuneItem(item);
        if (rune != null) {
            if (rune.getType().equals(RuneType.CONSUMABLE)) {
                rune.runCommands(p);
            } else {
                event.setCancelled(true);
            }
        }
    }

}
