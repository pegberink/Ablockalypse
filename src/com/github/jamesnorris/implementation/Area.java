package com.github.jamesnorris.implementation;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.github.jamesnorris.DataManipulator;
import com.github.jamesnorris.enumerated.GameObjectType;
import com.github.jamesnorris.enumerated.Setting;
import com.github.jamesnorris.enumerated.ZAColor;
import com.github.jamesnorris.enumerated.ZAEffect;
import com.github.jamesnorris.enumerated.ZASound;
import com.github.jamesnorris.inter.Blinkable;
import com.github.jamesnorris.inter.GameObject;
import com.github.jamesnorris.threading.BlinkerThread;
import com.github.jamesnorris.util.Rectangle;

public class Area extends DataManipulator implements GameObject, Blinkable {
    private BlinkerThread bt;
    private Location loc1, loc2;
    private HashMap<Location, Byte> locdata = new HashMap<Location, Byte>();
    private HashMap<Location, Material> locs = new HashMap<Location, Material>();
    private boolean opened, blinkers;
    private Rectangle rectangle;
    private Game zag;

    /**
     * Creates a new GameArea instance that is represented by a rectangular prism.
     * 
     * @param zag The game that should use this area
     * @param loc1 The first corner of the rectangular prism
     * @param loc2 The second corner of the rectangular prism
     */
    public Area(Game zag, Location loc1, Location loc2) {
        data.gameObjects.add(this);
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.zag = zag;
        opened = false;
        data.areas.add(this);
        rectangle = new Rectangle(loc1, loc2);
        for (Location l : rectangle.getLocations()) {
            locs.put(l, l.getBlock().getType());
            locdata.put(l, l.getBlock().getData());
        }
        zag.addArea(this);
        initBlinker();
    }
    
    public Area(String gameName, String world1name, String x1str, String y1str, String z1str, String world2name, String x2str, String y2str, String z2str) {
        this(data.getGame(gameName, true), new Location(Bukkit.getWorld(world1name), Double.parseDouble(x1str), Double.parseDouble(y1str), Double.parseDouble(z1str)), new Location(Bukkit.getWorld(world2name), Double.parseDouble(x2str), Double.parseDouble(y2str), Double.parseDouble(z2str)));
    }

    @SuppressWarnings("deprecation") private void initBlinker() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        for (Location l : rectangle.get3DBorder())
            blocks.add(l.getBlock());
        this.blinkers = (Boolean) Setting.BLINKERS.getSetting();
        bt = new BlinkerThread(blocks, ZAColor.BLUE, blinkers, 30, this);
    }

    /**
     * Replaces the area.
     */
    public void close() {
        for (Location l : locs.keySet()) {
            Block b = l.getBlock();
            b.setType(locs.get(l));
            b.setData(locdata.get(l));
            ZAEffect.SMOKE.play(l);
        }
        opened = false;
    }

    /**
     * Gets the BlinkerThread attached to this instance.
     * 
     * @return The BlinkerThread attached to this instance
     */
    @Override public BlinkerThread getBlinkerThread() {
        return bt;
    }

    /**
     * Gets a list of blocks for this area.
     * 
     * @return A list of blocks for this area
     */
    public ArrayList<Block> getBlocks() {
        ArrayList<Block> bls = new ArrayList<Block>();
        for (Location l : locs.keySet())
            bls.add(l.getBlock());
        return bls;
    }

    /**
     * Gets the blocks around the border of the Area.
     * 
     * @return The blocks around the border
     */
    @SuppressWarnings("deprecation") public ArrayList<Location> getBorderBlocks() {
        return rectangle.get3DBorder();
    }

    /**
     * Gets the blocks that defines this object as an object.
     * 
     * @return The blocks assigned to this object
     */
    @Override public ArrayList<Block> getDefiningBlocks() {
        ArrayList<Block> bs = new ArrayList<Block>();
        for (Location l : locs.keySet())
            bs.add(l.getBlock());
        return bs;
    }

    /**
     * Gets the game this area is assigned to.
     * 
     * @return The game this area is assigned to
     */
    @Override public Game getGame() {
        return zag;
    }

    /**
     * Gets a point from the area. This must be between 1 and 2.
     * 
     * @param i The point to get
     * @return The location of the point
     */
    public Location getPoint(int i) {
        Location loc = (i == 1) ? loc1 : loc2;
        return loc;
    }

    /**
     * Returns if the area is opened or not.
     * 
     * @return Whether or not the area has been opened
     */
    public boolean isOpened() {
        return opened;
    }

    /**
     * Removes the area.
     */
    public void open() {
        for (Location l : locs.keySet()) {
            l.getBlock().setType(Material.AIR);
            ZAEffect.SMOKE.play(l);
        }
        opened = true;
        ZASound.AREA_BUY.play(loc1);
    }

    /**
     * Removes the area.
     */
    @Override public void remove() {
        close();
        zag.removeArea(this);
        setBlinking(false);
        data.areas.remove(this);
        data.gameObjects.remove(this);
        data.threads.remove(bt);
        zag = null;
    }

    /**
     * Stops/Starts the blinker for this area.
     * 
     * @param tf Whether or not this area should blink
     */
    @Override public void setBlinking(boolean tf) {
        if (bt.isRunning())
            bt.remove();
        if (tf) {
            if (!data.threads.contains(bt))
                initBlinker();
            bt.setRunThrough(true);
        }
    }

    /**
     * Sets the first or second location of the area.
     * 
     * @param loc The location to set
     * @param n A number between 1 and 2
     */
    public void setLocation(Location loc, int n) {
        if (n == 1)
            loc1 = loc;
        else if (n == 2)
            loc2 = loc;
        if (n == 1 || n == 2)
            rectangle = new Rectangle(loc1, loc2);
    }

    @Override public Block getDefiningBlock() {
        return loc1.getBlock();
    }

    @Override public GameObjectType getObjectType() {
        return GameObjectType.AREA;
    }
}