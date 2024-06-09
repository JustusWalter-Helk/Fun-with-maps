package io.github.justuswalterhelk.mapgenerator;

import com.google.common.graph.Graph;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public final class MapGenerator extends JavaPlugin implements Listener, CommandExecutor {

    static BufferedImage image;
    {
        try {
            image = ImageIO.read(new File(getDataFolder(), "map_image.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    BufferedImage scaledImage = new BufferedImage(128,128,BufferedImage.TYPE_INT_RGB);
    Graphics2D g = scaledImage.createGraphics();

    @Override
    public void onEnable() {
        // Plugin startup logic
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        pm.registerEvents(new PlayerInteractEvent(), this);
        getCommand("loadImage").setExecutor(this);
        getCommand("resetgenerator").setExecutor(new ResetCommand());

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                try {
                    updateImage("latestframe.png", true);
                    if(MapScreen.screen != null) {
                        MapScreen.screen.updateMapScreen(ImageIO.read(new File(getDataFolder(), "latestframe.png")));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        },0, 5);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        //g.dispose();
    }

    /**
     *
     * @param urlOrFilePath
     * @param filePath
     * If filepath it needs to be relative to plugins/MapGenerator
     */
    void updateImage(String urlOrFilePath, boolean filePath) throws IOException {
        if(filePath) {
            File file = new File(getDataFolder(), urlOrFilePath);
            image = ImageIO.read(file);
            return;
        }
        image = ImageIO.read(new URL(urlOrFilePath));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player p = (Player) sender;
        if(args.length == 0 || args.length == 1) {
            p.sendMessage("Usage: /loadImage <url/filePath> <isFilePath>");
            return false;
        }

        BufferedImage backupImage = image;
        try {
            updateImage(args[0], args[1].contains("true"));
        } catch (IOException e) {
            p.sendMessage("Could not update image. Invalid URL or FilePath?");
            image = backupImage;
        }

        return true;
    }
}
