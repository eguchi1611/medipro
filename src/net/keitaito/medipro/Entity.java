package net.keitaito.medipro;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.keitaito.medipro.gameover.GameOverModel;
import net.keitaito.medipro.save.SaveData;
import net.keitaito.medipro.save.SaveManager;
import net.keitaito.medipro.save.WorldSaveData;
import net.keitaito.medipro.stage.StageModel;
import net.keitaito.medipro.stage.StageView;
import net.keitaito.medipro.tiles.Tile;

public class Entity {

    private final StageModel stageModel;

    private double posX = 0;
    private double posY = 0;
    private double velX = 0;
    private double velY = 0;
    private double accX = 0;
    private double accY = 0;

    private Image image = null;

    private int width = 0;
    private int height = 0;

    private boolean isOnGround = false;
    private boolean isAlive = true;
    private boolean isGoal = false;

    private int direction = 1;

    private int elapsedSinceStop = 0;

    public Entity(StageModel stageModel) {
        this.stageModel = stageModel;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getVelX() {
        return velX;
    }

    public void setVelX(double velX) {
        this.velX = velX;
        boolean isCollided = false;

        if (this.posX + this.velX < 0) {
            this.velX = 0;
        }

        if (this.posX + this.velX + this.width > StageView.WIDTH) {
            this.velX = 0;
        }

        Tile[] leftTiles = getCollisionTileOnLeft(this.posX + this.velX);
        if (leftTiles != null) {
            for (Tile leftTile : leftTiles) {
                if (leftTile.onCollide(this)) {
                    isCollided = true;
                }
            }
            if (isCollided) {
                this.velX = 0;
            }
        }

        Tile[] rightTiles = getCollisionOnRight(this.posX + this.velX);
        if (rightTiles != null) {
            for (Tile right : rightTiles) {
                if (right.onCollide(this)) {
                    isCollided = true;
                }
            }
            if (isCollided) {
                this.velX = 0;
            }
        }

        // 絶対値が0.05以下の場合は0にする
        if (Math.abs(this.velX) < 0.05) {
            this.velX = 0;
        }
    }

    public double getVelY() {
        return velY;
    }

    public void setVelY(double velY) {
        this.velY = velY;
        boolean isCollided = false;

        if (this.posY + this.velY < 0) {
            this.velY = 0;
            this.posY = 0;
        }

        if (this.posY + this.velY + this.height > StageView.HEIGHT) {
            this.velY = 0;
            setAlive(false);
            targetDeathAction();
        }

        Tile[] topTiles = getCollisionOnTop(this.posY + this.velY);
        if (topTiles != null) {
            for (Tile topTile : topTiles) {
                if (topTile.onCollide(this)) {
                    isCollided = true;
                }
            }
            if (isCollided) {
                this.velY = 0;
            }
        }

        Tile[] bottomTiles = getCollisionOnBottom(this.posY + this.velY);
        if (bottomTiles != null) {
            for (Tile bottomTile : bottomTiles) {
                if (bottomTile.onCollide(this)) {
                    isCollided = true;
                }
            }
            if (isCollided) {
                this.velY = 0;
            }
            this.isOnGround = true;
        } else {
            this.isOnGround = false;
        }

        // 絶対値が0.03以下の場合は0にする
        if (Math.abs(this.velY) < 0.03) {
            this.velY = 0;
        }
    }

    public double getAccX() {
        return accX;
    }

    public void setAccX(double accX) {
        this.accX = accX + (-0.025 * this.velX);

        if (this.isOnGround && Math.abs(this.velX) > 0.05) {
            if (this.velX > 0) {
                this.accX -= 0.1;
            } else if (this.velX < 0) {
                this.accX += 0.1;
            }
        }

        // 絶対値が0.03以下の場合は0にする
        if (Math.abs(this.accX) < 0.03) {
            this.accX = 0;
        }
    }

    public double getAccY() {
        return accY;
    }

    public void setAccY(double accY) {
        this.accY = accY;

        // 絶対値が0.03以下の場合は0にする
        if (Math.abs(this.accY) < 0.03) {
            this.accY = 0;
        }
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        if (direction <= -1) {
            this.direction = -1;
        } else if (direction >= 1) {
            this.direction = 1;
        } else {
            this.direction = 0;
        }
    }

    public int getElapsedSinceStop() {
        return elapsedSinceStop;
    }

    public void setElapsedSinceStop(int elapsedSinceStop) {
        this.elapsedSinceStop = elapsedSinceStop;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isOnGround() {
        return this.isOnGround;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public boolean isGoal() {
        return this.isGoal;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public void setGoal(boolean isGoal) {
        this.isGoal = isGoal;
    }

    public void reset() {
        this.posX = stageModel.getWorld().getStartPosX();
        this.posY = stageModel.getWorld().getStartPosY();
        this.velX = 0;
        this.velY = 0;
        this.accX = 0;
        this.accY = 0;
        this.isOnGround = false;
        this.isAlive = true;
        App.getStageModel().reset();
        App.getStageModel().getWorld().resetState();
    }

    public void targetDeathAction() {
        // TODO: ターゲットが死んだときのアクションを追加する。
        this.velX = 0;
        this.velY = 0;
        this.accX = 0;
        this.accY = 0;
        GameOverModel gameOverModel = App.getGameOverModel();
        gameOverModel.setOpen(true);

    }

    public void targetGoalAction() {
        this.velX = 0;
        this.velY = 0;
        this.accX = 0;
        this.accY = 0;
        App.getGameClearModel().setOpen(true);

        // save処理
        int stageLevel = App.getLevelModel().getSelectedLevel();
        WorldSaveData worldSaveData = new WorldSaveData();
        worldSaveData.setChecked(true);
        worldSaveData.setInput(App.getInputModel().getText());
        SaveData saveData = SaveManager.load();
        if (stageLevel == 1) {
            saveData.setWorldSaveData1(worldSaveData);
        } else if (stageLevel == 2) {
            saveData.setWorldSaveData2(worldSaveData);
        } else if (stageLevel == 3) {
            saveData.setWorldSaveData3(worldSaveData);
        } else if (stageLevel == 4) {
            saveData.setWorldSaveData4(worldSaveData);
        } else if (stageLevel == 5) {
            saveData.setWorldSaveData5(worldSaveData);
        } else if (stageLevel == 6) {
            saveData.setWorldSaveData6(worldSaveData);
        } else if (stageLevel == 7) {
            saveData.setWorldSaveData7(worldSaveData);
        } else if (stageLevel == 8) {
            saveData.setWorldSaveData8(worldSaveData);
        }
        try {
            SaveManager.save(saveData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        App.getAchievementModel().checkAll();
    }

    @Override
    public String toString() {
        return "Entity [stageModel=" + stageModel + ", posX=" + posX + ", posY=" + posY + ", velX=" + velX + ", velY="
                + velY + ", accX=" + accX + ", accY=" + accY + ", width=" + width + ", height=" + height + "]";
    }

    public Tile[] getCollisionTileOnLeft(double newPosX) {
        List<Tile> collisionTiles = new ArrayList<>();
        double[] checkPointsY = {
                this.posY, // 上端
                this.posY + this.height / 3, // 高さの1/3位置
                this.posY + this.height / 3 * 2, // 高さの2/3位置
                this.posY + this.height // 下端
        };
        for (double checkPointY : checkPointsY) {
            Tile tile = stageModel.getWorld().getTileAt(newPosX, checkPointY);
            if (tile != null && tile.isSolid()) {
                collisionTiles.add(tile);
            }
        }
        if (collisionTiles.size() > 0) {
            return collisionTiles.toArray(new Tile[0]);
        }
        return null;
    }

    public Tile[] getCollisionOnRight(double newPosX) {
        List<Tile> collisionTiles = new ArrayList<>();
        double[] checkPointsY = {
                this.posY, // 上端
                this.posY + this.height / 3, // 高さの1/3位置
                this.posY + this.height / 3 * 2, // 高さの2/3位置
                this.posY + this.height // 下端
        };
        for (double checkPointY : checkPointsY) {
            Tile tile = stageModel.getWorld().getTileAt(newPosX + this.width, checkPointY);
            if (tile != null && tile.isSolid()) {
                collisionTiles.add(tile);
            }
        }
        if (collisionTiles.size() > 0) {
            return collisionTiles.toArray(new Tile[0]);
        }
        return null;
    }

    public Tile[] getCollisionOnTop(double newPosY) {
        List<Tile> collisionTiles = new ArrayList<>();
        double[] checkPointsX = {
                this.posX, // 左端
                this.posX + this.width / 3, // 幅の1/3位置
                this.posX + this.width / 3 * 2, // 幅の2/3位置
                this.posX + this.width // 右端
        };
        for (double checkPointX : checkPointsX) {
            Tile tile = stageModel.getWorld().getTileAt(checkPointX, newPosY);
            if (tile != null && tile.isSolid()) {
                collisionTiles.add(tile);
            }
        }
        if (collisionTiles.size() > 0) {
            return collisionTiles.toArray(new Tile[0]);
        }
        return null;
    }

    public Tile[] getCollisionOnBottom(double newPosY) {
        List<Tile> collisionTiles = new ArrayList<>();
        double[] checkPointsX = {
                this.posX, // 左端
                this.posX + this.width / 3, // 幅の1/3位置
                this.posX + this.width / 3 * 2, // 幅の2/3位置
                this.posX + this.width // 右端
        };
        for (double checkPointX : checkPointsX) {
            Tile tile = stageModel.getWorld().getTileAt(checkPointX, newPosY + this.height);
            if (tile != null && tile.isSolid()) {
                collisionTiles.add(tile);
            }
        }
        if (collisionTiles.size() > 0) {
            return collisionTiles.toArray(new Tile[0]);
        }
        return null;
    }

}
