package com.github.utility.ranged;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class Hit {
    public Location getEntrance() {
        return entrance;
    }

    public Location getExit() {
        return exit;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getInitialPenetration() {
        return initialPenetration;
    }

    public double getPenetration() {
        return penetration;
    }

    public void setPenetration(double penetration) {
        this.penetration = penetration;
    }

    public HitBox getHitBox() {
        return hitBox;
    }

    public List<DataZone> getZones() {
        return zones;
    }
    
    public ShotData getShotData() {
        return shotData;
    }
    
    public Location getFrom() {
        return from;
    }

    private Location from, entrance, exit;
    private ShotData shotData;
    private HitBox hitBox;
    private double damage = 0, initialPenetration, penetration = 0;
    private List<DataZone> zones = new ArrayList<DataZone>();

    public Hit(Location from, Location entrance, Location exit, HitBox hitBox, ShotData data) {
        this.from = from;
        this.entrance = entrance;
        this.exit = exit;
        this.shotData = data;
        this.hitBox = hitBox;
        this.initialPenetration = data.getStartingPenetration();
        this.penetration = initialPenetration;
        double mXY = (exit.getX() - entrance.getX()) / (exit.getY() - entrance.getY());
        double mXZ = (exit.getX() - entrance.getX()) / (exit.getZ() - entrance.getZ());
        double bY = entrance.getY() - hitBox.getOrigin().getY();
        double bZ = entrance.getZ() - hitBox.getOrigin().getZ();
        
        for (DataZone zone : hitBox.getDataZones()) {
            double y = zone.heightFrom;
            double x = (bY - y) / mXY;
            double z = (bZ - y) / mXZ;
            
            if (zone.widthFrom <= x && zone.widthTo >= x && zone.heightFrom <= y && zone.heightTo >= y && zone.lengthFrom <= z && zone.lengthTo >= z) {
                zones.add(zone);
            }
        }
    }
}
