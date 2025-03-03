package net.keitaito.medipro.tiles;

import java.awt.Graphics;
import java.awt.Image;

import net.keitaito.medipro.worlds.World;
import net.keitaito.medipro.Entity;

public class WallTile extends Tile {
    private Image wallImage;

    public WallTile(int x, int y) {
        super(x, y);
    }

    @Override
    public void draw(Graphics g, int stageLevel) {
        wallImage = loadImage("WallTile.png", stageLevel);
        g.drawImage(wallImage, getX(), getY(), World.TILE_SIZE, World.TILE_SIZE, null);
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public boolean onCollide(Entity target) {
        return true;
    }
}
