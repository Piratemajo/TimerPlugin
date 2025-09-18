package es.javier.timerPlugin;

import es.javier.timerPlugin.configs.TimerConfig;
import es.javier.timerPlugin.data.TimerData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * @author Piratemajo
 * @version 1.1
 *
 * Plugin de Timer con Bloques
 *
 * **/
public class TimerPlugin extends JavaPlugin implements Listener {

    private final Map<Location, TimerData> activeTimers = new ConcurrentHashMap<>();
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, TimerConfig> playerSelections = new ConcurrentHashMap<>();
    private static final Map<Integer, boolean[][]> numberPatterns = new HashMap<>();
    private final Set<Location> pendingUpdates = ConcurrentHashMap.newKeySet();
    private BukkitRunnable globalTimerTask;

    private static FileConfiguration config;
    private File configFile;
    private static Material numberMaterial;
    private static Material separatorMaterial;

    @Override
    public void onEnable() {
        // Cargar configuración
        setupConfig();

        getServer().getPluginManager().registerEvents(this, this);
        initializeNumberPatterns();
        startGlobalTimer();
        getLogger().info("TimerPlugin activado correctamente!");
        getLogger().info("Version del servidor " + getServer().getBukkitVersion());
        getLogger().info("Creador: Piratemajo");
    }

    @Override
    public void onDisable() {
        if (globalTimerTask != null) {
            globalTimerTask.cancel();
        }

        // Limpiar todos los temporizadores al desactivar el plugin
        for (Location location : activeTimers.keySet()) {
            clearDisplay(location);
        }
        activeTimers.clear();
    }

    private void setupConfig() {
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        loadMaterialsFromConfig();
    }

    public void loadMaterialsFromConfig() {
        try {
            String numberMaterialName = config.getString("number-material", "IRON_BLOCK");
            String separatorMaterialName = config.getString("separator-material", "REDSTONE_BLOCK");

            numberMaterial = Material.matchMaterial(numberMaterialName);
            separatorMaterial = Material.matchMaterial(separatorMaterialName);

            // Validar materiales
            if (numberMaterial == null || !numberMaterial.isBlock()) {
                getLogger().warning("Material '" + numberMaterialName + "' no válido para números. Usando IRON_BLOCK por defecto.");
                numberMaterial = Material.IRON_BLOCK;
            }

            if (separatorMaterial == null || !separatorMaterial.isBlock()) {
                getLogger().warning("Material '" + separatorMaterialName + "' no válido para separadores. Usando REDSTONE_BLOCK por defecto.");
                separatorMaterial = Material.REDSTONE_BLOCK;
            }

            getLogger().info("Materiales cargados: Números=" + numberMaterial + ", Separadores=" + separatorMaterial);

        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Error cargando configuración, usando valores por defecto", e);
            numberMaterial = Material.IRON_BLOCK;
            separatorMaterial = Material.REDSTONE_BLOCK;
        }
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "No se pudo guardar la configuración", e);
        }
    }

    private void reloadConfiguration() {
        config = YamlConfiguration.loadConfiguration(configFile);
        loadMaterialsFromConfig();

        // Actualizar displays existentes
        for (Location location : activeTimers.keySet()) {
            if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
                TimerData timerData = activeTimers.get(location);
                updateDisplay(location, timerData.getTimeLeft());
            }
        }
    }

    private void initializeNumberPatterns() {
        // Patrones de números 0-9 (3x5) optimizados
        numberPatterns.put(0, new boolean[][]{
                {true, true, true},
                {true, false, true},
                {true, false, true},
                {true, false, true},
                {true, true, true}
        });

        numberPatterns.put(1, new boolean[][]{
                {false, true, false},
                {true, true, false},
                {false, true, false},
                {false, true, false},
                {true, true, true}
        });

        numberPatterns.put(2, new boolean[][]{
                {true, true, true},
                {false, false, true},
                {true, true, true},
                {true, false, false},
                {true, true, true}
        });

        numberPatterns.put(3, new boolean[][]{
                {true, true, true},
                {false, false, true},
                {true, true, true},
                {false, false, true},
                {true, true, true}
        });

        numberPatterns.put(4, new boolean[][]{
                {true, false, true},
                {true, false, true},
                {true, true, true},
                {false, false, true},
                {false, false, true}
        });

        numberPatterns.put(5, new boolean[][]{
                {true, true, true},
                {true, false, false},
                {true, true, true},
                {false, false, true},
                {true, true, true}
        });

        numberPatterns.put(6, new boolean[][]{
                {true, true, true},
                {true, false, false},
                {true, true, true},
                {true, false, true},
                {true, true, true}
        });

        numberPatterns.put(7, new boolean[][]{
                {true, true, true},
                {false, false, true},
                {false, false, true},
                {false, false, true},
                {false, false, true}
        });

        numberPatterns.put(8, new boolean[][]{
                {true, true, true},
                {true, false, true},
                {true, true, true},
                {true, false, true},
                {true, true, true}
        });

        numberPatterns.put(9, new boolean[][]{
                {true, true, true},
                {true, false, true},
                {true, true, true},
                {false, false, true},
                {true, true, true}
        });
    }

    private void startGlobalTimer() {
        globalTimerTask = new BukkitRunnable() {
            @Override
            public void run() {
                processPendingUpdates();
                updateAllTimers();
            }
        };
        globalTimerTask.runTaskTimer(this, 0L, 20L);
    }

    private void processPendingUpdates() {
        for (Location location : pendingUpdates) {
            if (activeTimers.containsKey(location)) {
                TimerData timerData = activeTimers.get(location);

                // Verificar si el chunk está cargado antes de actualizar el display
                if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
                    updateDisplay(location, timerData.getTimeLeft());

                    if (timerData.getTimeLeft() % 10 == 0 || timerData.getTimeLeft() <= 5) {
                        spawnParticles(location);
                        // Solo reproducir sonido si hay jugadores cerca
                        if (!location.getWorld().getPlayers().isEmpty()) {
                            location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_HAT, 0.3f, 1.0f);
                        }
                    }
                }

                if (timerData.getTimeLeft() <= 0) {
                    triggerTimer(location);
                    activeTimers.remove(location);
                }
            }
        }
        pendingUpdates.clear();
    }

    private void updateAllTimers() {
        Iterator<Map.Entry<Location, TimerData>> iterator = activeTimers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Location, TimerData> entry = iterator.next();
            Location location = entry.getKey();
            TimerData timerData = entry.getValue();

            // El temporizador debe seguir funcionando incluso si el chunk no está cargado
            // Solo verificar el bloque si el chunk está cargado
            if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
                Block block = location.getBlock();
                if (block.getType().isAir()) {
                    clearDisplay(location);
                    iterator.remove();
                    continue;
                }
            }

            timerData.decrement();
            pendingUpdates.add(location);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.CLOCK && isTimerItem(item)) {
            event.setCancelled(true);

            if (cooldowns.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis()) {
                player.sendMessage(ChatColor.RED + "Espera un momento antes de colocar otro temporizador.");
                return;
            }
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + 2000);

            if (activeTimers.containsKey(block.getLocation())) {
                player.sendMessage(ChatColor.RED + "Este bloque ya tiene un temporizador activo!");
                return;
            }

            TimerConfig config = playerSelections.get(player.getUniqueId());
            if (config == null) {
                config = new TimerConfig(60);
            }

            createTimer(block.getLocation(), player, config);
            playerSelections.remove(player.getUniqueId());
        }
    }

    private void createTimer(Location location, Player player, TimerConfig config) {
        TimerData timerData = new TimerData(config.getTotalSeconds());
        activeTimers.put(location, timerData);
        pendingUpdates.add(location);

        player.sendMessage(ChatColor.GREEN + "Temporizador colocado! Se activará en " + formatTime(config.getTotalSeconds()) + ".");
    }

    public static void updateDisplay(Location location, int totalSeconds) {
        int days = totalSeconds / 86400;
        int hours = (totalSeconds % 86400) / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        int[] daysDigits = getDigits(days, 2);
        int[] hoursDigits = getDigits(hours, 2);
        int[] minutesDigits = getDigits(minutes, 2);
        int[] secondsDigits = getDigits(seconds, 2);

        displayTimeWithBlocks(location, daysDigits, hoursDigits, minutesDigits, secondsDigits);
    }

    private static void displayTimeWithBlocks(Location location, int[] days, int[] hours, int[] minutes, int[] seconds) {
        World world = location.getWorld();
        int baseX = location.getBlockX();
        int baseY = location.getBlockY() + 2;
        int baseZ = location.getBlockZ();

        clearDisplay(location);

        // Display días con espacio entre números
        displayDigitWithBlocks(world, baseX - 8, baseY, baseZ, days[0]);
        displayDigitWithBlocks(world, baseX - 4, baseY, baseZ, days[1]);

        // Separador
        displaySeparatorWithBlocks(world, baseX, baseY, baseZ);

        // Display horas con espacio entre números
        displayDigitWithBlocks(world, baseX + 2, baseY, baseZ, hours[0]);
        displayDigitWithBlocks(world, baseX + 6, baseY, baseZ, hours[1]);

        // Separador
        displaySeparatorWithBlocks(world, baseX + 10, baseY, baseZ);

        // Display minutos con espacio entre números
        displayDigitWithBlocks(world, baseX + 12, baseY, baseZ, minutes[0]);
        displayDigitWithBlocks(world, baseX + 16, baseY, baseZ, minutes[1]);

        // Separador
        displaySeparatorWithBlocks(world, baseX + 20, baseY, baseZ);

        // Display segundos con espacio entre números
        displayDigitWithBlocks(world, baseX + 22, baseY, baseZ, seconds[0]);
        displayDigitWithBlocks(world, baseX + 26, baseY, baseZ, seconds[1]);
    }

    private static void displayDigitWithBlocks(World world, int x, int y, int z, int digit) {
        boolean[][] pattern = numberPatterns.get(digit);
        if (pattern == null) return;

        for (int dy = 0; dy < 5; dy++) {
            for (int dx = 0; dx < 3; dx++) {
                Location blockLoc = new Location(world, x + dx, y + (4 - dy), z);
                if (pattern[dy][dx]) {
                    blockLoc.getBlock().setType(numberMaterial, false);
                } else {
                    blockLoc.getBlock().setType(Material.AIR, false);
                }
            }
        }
    }

    private static void displaySeparatorWithBlocks(World world, int x, int y, int z) {
        for (int dy = 0; dy < 5; dy++) {
            Location blockLoc1 = new Location(world, x, y + (4 - dy), z);
            Location blockLoc2 = new Location(world, x + 2, y + (4 - dy), z);

            if (dy == 1 || dy == 3) {
                blockLoc1.getBlock().setType(separatorMaterial, false);
                blockLoc2.getBlock().setType(separatorMaterial, false);
            } else {
                blockLoc1.getBlock().setType(Material.AIR, false);
                blockLoc2.getBlock().setType(Material.AIR, false);
            }
        }
    }

    private static void clearDisplay(Location location) {
        World world = location.getWorld();
        int baseX = location.getBlockX();
        int baseY = location.getBlockY() + 2;
        int baseZ = location.getBlockZ();

        // Limpiar un área más grande para acomodar los espacios
        for (int x = -8; x <= 29; x++) {
            for (int y = 0; y < 5; y++) {
                Location blockLoc = new Location(world, baseX + x, baseY + y, baseZ);
                // Solo intentar limpiar si el chunk está cargado
                if (world.isChunkLoaded(blockLoc.getBlockX() >> 4, blockLoc.getBlockZ() >> 4)) {
                    if (!blockLoc.getBlock().getType().isAir()) {
                        blockLoc.getBlock().setType(Material.AIR, false);
                    }
                }
            }
        }
    }

    private static int[] getDigits(int number, int digits) {
        int[] result = new int[digits];
        String numStr = String.format("%0" + digits + "d", Math.min((int)Math.pow(10, digits) - 1, Math.max(0, number)));
        for (int i = 0; i < digits; i++) {
            result[i] = Character.getNumericValue(numStr.charAt(i));
        }
        return result;
    }

    private void triggerTimer(Location loc) {
        World world = loc.getWorld();

        if (world.isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            // Solo reproducir efectos si el chunk está cargado
            world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.0f);
            world.spawnParticle(Particle.EXPLOSION, loc.clone().add(0.5, 0.5, 0.5), 5);
        }

        clearDisplay(loc);

        // Notificar solo a jugadores que estén en el mundo y cerca
        int notifyRadius = 20;
        for (Player player : world.getPlayers()) {
            if (player.getLocation().distance(loc) <= notifyRadius) {
                player.sendMessage(ChatColor.YELLOW + "¡Temporizador activado cerca de tu ubicación!");
            }
        }
    }

    private void spawnParticles(Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            location.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, location.clone().add(0.5, 1.2, 0.5), 3);
        }
    }

    private boolean isTimerItem(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            return meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.GOLD + "Temporizador Avanzado");
        }
        return false;
    }




    public static ItemStack createTimerItem() {
        ItemStack timerItem = new ItemStack(Material.CLOCK);
        ItemMeta meta = timerItem.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Temporizador Avanzado");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Clic derecho en un bloque");
        lore.add(ChatColor.GRAY + "para colocar un temporizador");
        lore.add(ChatColor.GRAY + "Usa /settimer para configurar");
        meta.setLore(lore);

        timerItem.setItemMeta(meta);
        return timerItem;
    }



    private String formatTime(int seconds) {
        if (seconds < 60) return seconds + " segundos";
        if (seconds < 3600) return (seconds / 60) + " minutos y " + (seconds % 60) + " segundos";
        if (seconds < 86400) return (seconds / 3600) + " horas, " + ((seconds % 3600) / 60) + " minutos";
        return (seconds / 86400) + " días, " + ((seconds % 86400) / 3600) + " horas";
    }




}