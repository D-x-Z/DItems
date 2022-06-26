package net.danh.ditems;

import net.danh.dcore.DCore;
import net.danh.dcore.NMS.NMSAssistant;
import net.danh.dcore.Utils.File;
import net.danh.ditems.Listeners.ArmorEquip;
import net.danh.ditems.Listeners.BlockDispenseArmor;
import net.danh.ditems.Listeners.DamageEvent;
import net.danh.ditems.Resource.Files;
import org.bukkit.plugin.java.JavaPlugin;

public final class DItems extends JavaPlugin {

    private static DItems INSTANCE;

    public static DItems getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        DCore.RegisterDCore(this);
        getServer().getPluginManager().registerEvents(new DamageEvent(), this);
        getServer().getPluginManager().registerEvents(new ArmorEquip(), this);
        NMSAssistant nms = new NMSAssistant();
        if (nms.isVersionGreaterThanOrEqualTo(13)) {
            getServer().getPluginManager().registerEvents(new BlockDispenseArmor(), this);
        }
        new Files("message").load();
        new Files("message").save();
        new Files("stats").load();
        new Files("stats").save();
        File.updateFile(DItems.getInstance(), new Files("stats").getConfig(), "stats.yml");
        File.updateFile(DItems.getInstance(), new Files("message").getConfig(), "message.yml");
        new net.danh.ditems.Commands.DItems(this);
    }

    @Override
    public void onDisable() {
        new Files("stats").save();
        new Files("message").save();
    }
}
