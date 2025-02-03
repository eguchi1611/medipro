package medipro.tiles;

import java.awt.Graphics;
import java.awt.Image;

import medipro.App;
import medipro.Entity;
import medipro.World;

public class GoalTile extends AirTile {

    private Image image;

    public GoalTile(int x, int y) {
        super(x, y);
    }

    @Override
    public void draw(Graphics g, int stageLevel) {
        image = loadImage("GoalTile.png");
        g.drawImage(image, getX(), getY(), World.TILE_SIZE * 3, World.TILE_SIZE * 3, null);
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public void onCollide(Entity target) {
        // TODO: ゴールした際の処理
        System.out.println("Goal!");
        App.getStageModel().reset();
    }
}
