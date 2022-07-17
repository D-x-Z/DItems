package net.danh.ditems.Listeners;

import net.danh.dcore.Random.Number;
import net.danh.dcore.Resource.Files;
import net.danh.dcore.Utils.Chat;
import net.danh.ditems.API.Attack;
import net.danh.ditems.DItems;
import net.danh.ditems.Manager.NBTItem;
import net.danh.ditems.PlayerData.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DamageEvent implements Listener {
    public static Map<Entity, Integer> indicators = new HashMap<>();

    public static double getRandomOffset() {
        double random = Math.random();
        if (Math.random() > 0.5) random *= -1;
        return random;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity target = e.getEntity();
        Entity killer = e.getDamager();
        if (killer instanceof Player) {
            Player k = ((Player) killer).getPlayer();
            if (k == null) return;
            ItemStack item = k.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) return;
            if (new NBTItem(item).hasDoubleStats("REQUIRED_LEVEL")) {
                if ((int) new NBTItem(item).getDoubleStats("REQUIRED_LEVEL") > PlayerData.getLevel(k)) {
                    e.setDamage(0);
                    e.setCancelled(true);
                    return;
                }
            }
            int crit_chance = (int) new NBTItem(item).getDoubleStats("CRIT_CHANCE");
            int chance = Number.getRandomInt(1, 100);
            if (target instanceof Player) {
                Player t = ((Player) target).getPlayer();
                if (t != null) {
                    if (new NBTItem(item).hasData()) {
                        if (!new NBTItem(item).hasDoubleStats("PVP_DAMAGE")) {
                            if (chance > crit_chance) {
                                Attack.NormalAttack(e, k, t);
                            } else {
                                Attack.CritAttack(e, k, t);
                            }
                        } else {
                            if (chance > crit_chance) {
                                Attack.PvPNormalAttack(e, k, t);
                            } else {
                                Attack.PvPCritAttack(e, k, t);
                            }
                        }
                    }
                }
            } else if (target instanceof Monster || target instanceof Animals) {
                if (chance > crit_chance) {
                    Attack.NormalAttack(e, k, target);
                } else {
                    Attack.CritAttack(e, k, target);
                }
            } else {
                if (chance > crit_chance) {
                    Attack.PvPNormalAttack(e, k, target);
                } else {
                    Attack.PvPCritAttack(e, k, target);
                }
            }
            if (new Files(DItems.getInstance(), "config").getConfig().getBoolean("INDICATORS.ENABLE") && new Files(DItems.getInstance(), "config").getConfig().getBoolean("INDICATORS.SUPPORT_OTHER_PLUGIN")) {
                Location loc = target.getLocation().clone().add(getRandomOffset(), 1, getRandomOffset());
                int damage = (int) e.getDamage();
                k.getWorld().spawn(loc, ArmorStand.class, armorStand -> {
                    armorStand.setMarker(true);
                    armorStand.setVisible(false);
                    armorStand.setGravity(false);
                    armorStand.setSmall(true);
                    armorStand.setCustomNameVisible(true);
                    armorStand.setCustomName(Chat.colorize(Objects.requireNonNull(new Files(DItems.getInstance(), "config").getConfig().getString("INDICATORS.NORMAL_ATTACK")).replaceAll("#damage#", String.valueOf(damage))));
                    indicators.put(armorStand, 30);
                });
            }
        }
    }
}

