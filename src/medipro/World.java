package medipro;

import medipro.stage.StageModel;
import medipro.tiles.AirTile;
import medipro.tiles.GoalTile;
import medipro.tiles.RockTile;
import medipro.tiles.StartTile;
import medipro.tiles.ThornTile;
import medipro.tiles.Tile;
import medipro.tiles.WallTile;
import medipro.tiles.WarpTile;

public class World {

    public static final int TILE_SIZE = 20;

    private final StageModel stageModel;

    private final String exampleCommand;

    private final int width;
    private final int height;

    private int startPosX;
    private int startPosY;

    private int goalPosX;
    private int goalPosY;

    private final Tile[][] tiles;

    private final WarpTile[] warpTiles = new WarpTile[2];

    public World(StageModel stageModel, WorldTemplate worldTemplate) {
        this.stageModel = stageModel;
        this.width = worldTemplate.getWidth();
        this.height = worldTemplate.getHeight();
        this.exampleCommand = worldTemplate.getInitialCommand();
        short[][] tileIDs = worldTemplate.getTileIDs();

        tiles = new Tile[tileIDs.length][tileIDs[0].length];
        for (int y = 0; y < tileIDs[0].length; y++) {
            for (int x = 0; x < tileIDs.length; x++) {
                switch (tileIDs[x][y]) {
                    case 0:
                        tiles[x][y] = new AirTile(x * TILE_SIZE, y * TILE_SIZE);
                        break;
                    case 1:
                        tiles[x][y] = new WallTile(x * TILE_SIZE, y * TILE_SIZE);
                        break;
                    case 2:
                        tiles[x][y] = new RockTile(x * TILE_SIZE, y * TILE_SIZE);
                        break;
                    case 3:
                        tiles[x][y] = new ThornTile(x * TILE_SIZE, y * TILE_SIZE);
                        break;
                    case 4:
                        tiles[x][y] = new StartTile(x * TILE_SIZE, y * TILE_SIZE);
                        startPosX = x * TILE_SIZE;
                        startPosY = y * TILE_SIZE;
                        break;
                    case 5:
                        tiles[x][y] = new GoalTile(x * TILE_SIZE, y * TILE_SIZE);
                        goalPosX = x * TILE_SIZE;
                        goalPosY = y * TILE_SIZE;
                        break;
                    case 6:
                        tiles[x][y] = new WarpTile(x * TILE_SIZE, y * TILE_SIZE);
                        if (warpTiles[0] == null) {
                            warpTiles[0] = (WarpTile) tiles[x][y];
                        } else {
                            warpTiles[1] = (WarpTile) tiles[x][y];
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid tile ID: " + tileIDs[x][y]);
                }
            }
        }
        if (warpTiles[0] != null || warpTiles[1] != null) {
            for (int i = 0; i < warpTiles.length; i++) {
                WarpTile warpTile = warpTiles[i];
                warpTile.setWarpPoint(warpTiles[(i + 1) % 2]);
            }
        }
    }

    public StageModel getStageModel() {
        return stageModel;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getStartPosX() {
        return startPosX;
    }

    public int getStartPosY() {
        return startPosY;
    }

    public int getGoalPosX() {
        return goalPosX;
    }

    public int getGoalPosY() {
        return goalPosY;
    }

    public WarpTile[] getWarpTiles() {
        return warpTiles;
    }

    public String getExampleCommand() {
        return exampleCommand;
    }

    public void resetState() {
        for (WarpTile warpTile : warpTiles) {
            if (warpTile != null) {
                warpTile.setIsCollided(false);
            }
        }
    }

    /**
     * 指定した座標にあるタイルを取得
     * 
     * @param posX
     * @param posY
     * @return
     */
    public Tile getTileAt(double posX, double posY) {
        int x = (int) (posX / World.TILE_SIZE);
        int y = (int) (posY / World.TILE_SIZE);
        x = Math.min(x, getTiles().length - 1);
        y = Math.min(y, getTiles()[0].length - 1);
        return getTiles()[x][y];
    }

    /**
     * 指定した座標に壁があるかどうか
     * 
     * @param posX
     * @param posY
     * @return 壁がある場合はtrue
     */
    public boolean isSolidTileAt(double posX, double posY) {
        return getTileAt(posX, posY).isSolid();
    }

}
