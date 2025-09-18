package es.javier.timerPlugin.commands;

import es.javier.timerPlugin.TimerPlugin;
import es.javier.timerPlugin.data.TimerData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import static java.lang.Math.log;

public class TimerMaterialCommand implements CommandExecutor {

    private final Map<Location, TimerData> activeTimers = new ConcurrentHashMap<>();

    private FileConfiguration config;
    private File configFile;
    private Material numberMaterial;
    private Material separatorMaterial;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (command.getName().equalsIgnoreCase("timersetmaterial")) {
            if (!sender.hasPermission("timerplugin.setmaterial")) {
                sender.sendMessage(ChatColor.RED + "No tienes permiso para cambiar los materiales.");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Uso: /timersetmaterial <número|separador> <material>");
                sender.sendMessage(ChatColor.RED + "Ejemplo: /timersetmaterial número GOLD_BLOCK");
                return true;
            }

            String type = args[0].toLowerCase();
            String materialName = args[1].toUpperCase();
            Material material = Material.matchMaterial(materialName);

            if (material == null || !material.isBlock()) {
                sender.sendMessage(ChatColor.RED + "Material '" + materialName + "' no válido o no es un bloque.");
                return true;
            }

            if (type.equals("número") || type.equals("numero")) {
                config.set("number-material", materialName);
                numberMaterial = material;
                sender.sendMessage(ChatColor.GREEN + "Material de números cambiado a: " + materialName);
            } else if (type.equals("separador")) {
                config.set("separator-material", materialName);
                separatorMaterial = material;
                sender.sendMessage(ChatColor.GREEN + "Material de separadores cambiado a: " + materialName);
            } else {
                sender.sendMessage(ChatColor.RED + "Tipo no válido. Usa 'número' o 'separador'.");
                return true;
            }

            saveConfig();

            // Actualizar displays existentes
            for (Location location : activeTimers.keySet()) {
                if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
                    TimerData timerData = activeTimers.get(location);
                    TimerPlugin.updateDisplay(location, timerData.getTimeLeft());
                }
            }

            return true;
        }
        return false;
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {

        }
    }
}
