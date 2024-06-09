package io.github.justuswalterhelk.mapgenerator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MapScreen {
    public int width;
    public int height;

    public static MapScreen screen;

    public BufferedImage renderedImage;
    public static Map<Integer, CustomRenderer> renderes = new HashMap<>();

    public void setMapRenderes() {
        for(Integer mapId : renderes.keySet()) {
            mapId += 1;
            Bukkit.broadcastMessage("Getting map with id of "+mapId);
            MapView map = Bukkit.getMap(mapId);

            if(map == null) {
                Bukkit.broadcastMessage("Map with id " + mapId + " not found");
                map = Bukkit.createMap(Bukkit.getWorld("world"));
            }

            map.getRenderers().clear();
            Bukkit.broadcastMessage("Trying to add renderer with id of " + renderes.get(mapId - 1).mapId);
            map.addRenderer(renderes.get(mapId - 1));

            Bukkit.broadcastMessage("Added custom renderer to map " + mapId);
        }
        Bukkit.broadcastMessage("Done setting up renderes");
    }

    public MapScreen(int width, int height, BufferedImage renderedImage) {
        this.width = width;
        this.height = height;
        this.renderedImage = scaleImage(renderedImage, width*128, height*128);
        createRenderers();
        Bukkit.broadcastMessage("Created renderers");
        setMapRenderes();

        screen = this;
    }

    public void updateMapScreen(BufferedImage renderedImage) {
        this.renderedImage = scaleImage(renderedImage, width*128, height*128);

        for(CustomRenderer renderer : renderes.values()) {
            renderer.updateImage(this.renderedImage.getSubimage(renderer.x * 128, renderer.y * 128, 128,128));
        }
    }

    public void createRenderers() {
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                int mapId = y * width + x;
                BufferedImage segment = renderedImage.getSubimage(x*128,y*128,128,128);
                Bukkit.broadcastMessage("Created segment with id of " + (y*width+x));
                CustomRenderer renderer = new CustomRenderer(segment);
                renderer.mapId = mapId;
                renderer.x = x;
                renderer.y = y;

                renderes.put(mapId, renderer);
            }
        }
    }

    private BufferedImage scaleImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return scaledImage;
    }
}

