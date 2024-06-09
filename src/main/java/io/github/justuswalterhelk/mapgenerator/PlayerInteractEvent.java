package io.github.justuswalterhelk.mapgenerator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class PlayerInteractEvent implements Listener {
    private static final BlockFace[] ADJACENT_FACES = {
            BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
            BlockFace.UP, BlockFace.DOWN
    };

    public static boolean generated = false;

    private static MapScreen screen;

    @EventHandler
    public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEntityEvent event) {
        if(event.getRightClicked().getType() == EntityType.ITEM_FRAME) {
            ItemFrame frame = (ItemFrame)event.getRightClicked();

            List<ItemFrame> frames = findAdjacendItemFrames(frame);

            int width = determineWidth(frames);
            int height = determineHeight(frames);

            event.getPlayer().sendMessage("Found " + (frames.size()) + " adjacend item frames");
            event.getPlayer().sendMessage("Screen width: " + width + " frames");
            event.getPlayer().sendMessage("Screen height: " + height + " frames");

            if(generated) return;
            generated = true;
            screen = new MapScreen(width,height, MapGenerator.image);
        }
    }

    private List<ItemFrame> findAdjacendItemFrames(ItemFrame frame) {
        List<ItemFrame> adjacentFrames = new ArrayList<>();

        for(Entity entity : frame.getNearbyEntities(2, 2, 2))
        {
            if(!(entity instanceof ItemFrame item))
                continue;

            Block block = item.getLocation().getBlock().getRelative(item.getAttachedFace());
            if(block != null && block.getType() != Material.AIR)
                adjacentFrames.add(item);
        }

        return adjacentFrames;
    }

    private int determineWidth(List<ItemFrame> frames) {
        if (frames.isEmpty()) return 0;

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;

        for (ItemFrame frame : frames) {
            Location loc = frame.getLocation();
            int x = loc.getBlockX();

            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
        }

        return maxX - minX + 1;
    }

    private int determineHeight(List<ItemFrame> frames) {
        if (frames.isEmpty()) return 0;

        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (ItemFrame frame : frames) {
            Location loc = frame.getLocation();
            int y = loc.getBlockY();

            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }

        return maxY - minY + 1;
    }
}
