package net.danh.ditems.Commands;

import net.danh.dcore.Commands.CMDBase;
import net.danh.dcore.NMS.NMSAssistant;
import net.danh.dcore.Random.Number;
import net.danh.dcore.Resource.FileFolder;
import net.danh.dcore.Resource.Files;
import net.danh.ditems.API.Items;
import net.danh.ditems.Manager.Check;
import net.danh.ditems.Manager.NBTItem;
import net.danh.ditems.Resource.Resource;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.*;

import static net.danh.dcore.Utils.Player.sendConsoleMessage;
import static net.danh.dcore.Utils.Player.sendPlayerMessage;
import static net.danh.ditems.API.Items.saveItems;
import static net.danh.ditems.Resource.Resource.getMessage;

public class DItems extends CMDBase {
    public DItems(JavaPlugin core) {
        super(core, "ditems");
    }

    @Override
    public void playerexecute(Player p, String[] args) {
        if (p.hasPermission("ditems.admin")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    sendPlayerMessage(p, getMessage().getStringList("ADMIN.HELP"));
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    Resource.reloadFiles();
                    sendPlayerMessage(p, "&aReloaded");
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("remove_ability_command")) {
                    if (p.getInventory().getItemInMainHand().getType() != Material.AIR) {
                        new NBTItem(p.getInventory().getItemInMainHand()).removeAbilityCommand(args[1]);
                    }
                }
                if (args[0].equalsIgnoreCase("unbreakable")) {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    if (item.getType() == Material.AIR) {
                        return;
                    }
                    new NBTItem(item).setInfinityDurability(Boolean.parseBoolean(args[1]));
                }
                if (args[0].equalsIgnoreCase("setflag")) {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    if (item.getType() == Material.AIR) {
                        return;
                    }
                    new NBTItem(item).setFlag(ItemFlag.valueOf(args[1]));
                }
                if (args[0].equalsIgnoreCase("removeflag")) {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    if (item.getType() == Material.AIR) {
                        return;
                    }
                    new NBTItem(item).removeFlag(ItemFlag.valueOf(args[1]));
                }
                if (args[0].equalsIgnoreCase("custom_model_data")) {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    if (item.getType() == Material.AIR) {
                        return;
                    }
                    new NBTItem(item).setCustomModelData(args[1]);
                }
                if (args[0].equalsIgnoreCase("save")) {
                    saveItems(args[1], p.getInventory().getItemInMainHand());
                    sendPlayerMessage(p, "&aSaved item with key " + args[1]);
                }
                if (args[0].equalsIgnoreCase("show")) {
                    if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
                        return;
                    }
                    de.tr7zw.changeme.nbtapi.NBTItem nbtItem = new de.tr7zw.changeme.nbtapi.NBTItem(p.getInventory().getItemInMainHand());
                    sendPlayerMessage(p, String.valueOf(nbtItem.getDouble("DITEMS_STATS_" + args[1].toUpperCase())));
                }
                if (args[0].equalsIgnoreCase("removeenchant")) {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    if (item.getType() == Material.AIR) {
                        return;
                    }
                    String enchantments = args[1].toUpperCase();
                    Enchantment enchant;
                    NMSAssistant nms = new NMSAssistant();
                    if (nms.isVersionLessThanOrEqualTo(12)) {
                        enchant = Enchantment.getByName(enchantments.toUpperCase());
                    } else {
                        enchant = Enchantment.getByKey(new NamespacedKey(net.danh.ditems.DItems.getInstance(), enchantments.toUpperCase()));
                    }
                    if (enchant != null) {
                        new NBTItem(item).removeEnchants(enchantments);
                    } else {
                        sendPlayerMessage(p, "&c " + args[1] + " is null, Check: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/enchantments/Enchantment.html");
                    }
                }
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("load")) {
                    String key = args[1];
                    int amount = Number.getInt(args[2]);
                    Items.loadItems(p, key, amount);
                }
                if (args[0].equalsIgnoreCase("stats")) {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    if (item.getType() == Material.AIR) {
                        return;
                    }
                    String stats_name = args[1].toUpperCase();
                    if (Check.isDouble(args[2])) {
                        double amount = Double.parseDouble(args[2]);
                        for (String name : Objects.requireNonNull(new Files(net.danh.ditems.DItems.getInstance(), "stats").getConfig().getConfigurationSection("STATS")).getKeys(false)) {
                            if (name.equalsIgnoreCase(stats_name)) {
                                new NBTItem(item).setStats(stats_name, amount);
                                sendPlayerMessage(p, Objects.requireNonNull(getMessage().getString("ADMIN.SET_STATS")).replaceAll("#item#", p.getInventory().getItemInMainHand().getType().toString()).replaceAll("#stats_amount#", args[2]).replaceAll("#stats_name#", args[1]));
                            }
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("addenchant")) {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    if (item.getType() == Material.AIR) {
                        return;
                    }
                    String enchantments = args[1].toUpperCase();
                    Enchantment enchant;
                    NMSAssistant nms = new NMSAssistant();
                    if (nms.isVersionLessThanOrEqualTo(12)) {
                        enchant = Enchantment.getByName(enchantments.toUpperCase());
                    } else {
                        enchant = Enchantment.getByKey(new NamespacedKey(net.danh.ditems.DItems.getInstance(), enchantments.toUpperCase()));
                    }
                    if (enchant != null) {
                        new NBTItem(item).addEnchants(enchantments, Number.getInt(args[2]));
                    } else {
                        sendPlayerMessage(p, "&c " + args[1] + " is null, Check: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/enchantments/Enchantment.html");
                    }
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("removelore")) {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    if (item.getType() == Material.AIR) {
                        return;
                    }
                    int line = Number.getInt(args[1]);
                    new NBTItem(item).removeLore(line);
                }
            }
            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("ability_command")) {
                    String action = args[1];
                    String command = args[2];
                    Integer delay = Number.getInt(args[3]);
                    if (p.getInventory().getItemInMainHand().getType() != Material.AIR) {
                        new NBTItem(p.getInventory().getItemInMainHand()).addAbilityCommand(action, command, delay);
                    }
                }
            }
            if (args.length > 1) {
                if (args[0].equalsIgnoreCase("setname")) {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    if (item.getType() == Material.AIR) {
                        return;
                    }
                    String name = String.join(" ", Arrays.asList(args).subList(1, args.length));
                    new NBTItem(item).setName(name);
                }
                if (args[0].equalsIgnoreCase("addlore")) {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    if (item.getType() == Material.AIR) {
                        return;
                    }
                    String lore = String.join(" ", Arrays.asList(args).subList(1, args.length));
                    new NBTItem(item).addLore(lore);
                }
                if (args[0].equalsIgnoreCase("setlore")) {
                    int line = Number.getInt(args[1]);
                    ItemStack item = p.getInventory().getItemInMainHand();
                    if (item.getType() == Material.AIR) {
                        return;
                    }
                    String lore = String.join(" ", Arrays.asList(args).subList(2, args.length));
                    new NBTItem(item).setLore(line, lore);
                }
            }
        }
    }

    @Override
    public void consoleexecute(ConsoleCommandSender c, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sendConsoleMessage(c, getMessage().getStringList("ADMIN.HELP"));
            }
            if (args[0].equalsIgnoreCase("reload")) {
                Resource.reloadFiles();
                sendConsoleMessage(c, "&aReloaded");
            }
        }
        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("load")) {
                String key = args[1];
                int amount = Number.getInt(args[2]);
                Player p = Bukkit.getPlayer(args[3]);
                if (p == null) return;
                Items.loadItems(p, key, amount);
            }
        }
    }

    @Override
    public List<String> TabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        if (sender.hasPermission("ditems.admin")) {
            if (args.length == 1) {
                commands.add("setname");
                commands.add("setlore");
                commands.add("addlore");
                commands.add("removelore");
                commands.add("stats");
                commands.add("show");
                commands.add("help");
                commands.add("reload");
                commands.add("addenchant");
                commands.add("removeenchant");
                commands.add("save");
                commands.add("unbreakable");
                commands.add("setflag");
                commands.add("removeflag");
                commands.add("removeflag");
                commands.add("load");
                commands.add("custom_model_data");
                commands.add("ability_command");
                commands.add("remove_ability_command");
                StringUtil.copyPartialMatches(args[0], commands, completions);
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("ability_command") || args[0].equalsIgnoreCase("remove_ability_command")) {
                    StringUtil.copyPartialMatches(args[1], Resource.getConfig().getStringList("ACTION"), completions);
                }
                if (args[0].equalsIgnoreCase("stats") || args[0].equalsIgnoreCase("show")) {
                    StringUtil.copyPartialMatches(args[1], Objects.requireNonNull(new Files(net.danh.ditems.DItems.getInstance(), "stats").getConfig().getConfigurationSection("STATS")).getKeys(false), completions);
                }
                if (args[0].equalsIgnoreCase("load")) {
                    StringUtil.copyPartialMatches(args[1], new FileFolder(net.danh.ditems.DItems.getInstance(), "items", "ItemSaved").getConfig().getKeys(false), completions);
                }
                if (args[0].equalsIgnoreCase("addenchant") || args[0].equalsIgnoreCase("removeenchant")) {
                    for (Enchantment enchantment : Enchantment.values()) {
                        NMSAssistant nms = new NMSAssistant();
                        if (nms.isVersionGreaterThanOrEqualTo(13)) {
                            StringUtil.copyPartialMatches(args[1], Collections.singleton(enchantment.getKey().toString()), completions);
                        } else {
                            StringUtil.copyPartialMatches(args[1], Collections.singleton(enchantment.getName()), completions);
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("setflag") || args[0].equalsIgnoreCase("removeflag")) {
                    for (ItemFlag itemFlag : ItemFlag.values()) {
                        StringUtil.copyPartialMatches(args[1], Collections.singleton(itemFlag.name()), completions);
                    }
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("ability_command")) {
                    StringUtil.copyPartialMatches(args[2], Objects.requireNonNull(Resource.getCMD().getConfigurationSection("")).getKeys(false), completions);
                }
            }
        }
        Collections.sort(completions);
        return completions;
    }
}
