package io.github.justuswalterhelk.mapgenerator;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class CustomRenderer extends MapRenderer {

    private BufferedImage image;
    public int mapId;

    public int x;
    public int y;

    public CustomRenderer(BufferedImage image) {
        this.image = image;
    }

    public void updateImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        mapCanvas.drawImage(0,0,image);
        mapView.setScale(MapView.Scale.FAR);
    }
}
