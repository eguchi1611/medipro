package medipro.stage;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;

import medipro.App;
import medipro.Entity;
import medipro.HangWire;
import medipro.IKeyAction;
import medipro.Vector2;
import medipro.World;
import medipro.WorldTemplate;

public class StageModel implements IKeyAction {

    private World world = null;

    /**
     * 有効キー: a, d, スペース
     */
    private final List<Integer> availableKeys = List.of(65, 68, 32, 72, 74, 75);
    private Set<Integer> keys = new HashSet<>();
    private Entity entity;
    private int tickCount = 0;

    private boolean isDebug = true;

    private HangWire hangWire;

    // 重力の考慮
    private double gravity = 0.2;

    // ジャンプ力
    private double jumpPower = -6.5;

    // ハングアクションの速さ
    private double hangSpeed = 20.0;
    private double hangTensionCoef = 0.5;

    private final Image characterLeftWalkHat0 = loadImage("L_walk_hat_0.png");
    private final Image characterLeftWalkHat1 = loadImage("L_walk_hat_1.png");
    private final Image characterLeftWalkHat2 = loadImage("L_walk_hat_2.png");
    private final Image characterLeftJump = loadImage("L_jump_hat.png");
    private final Image characterRightWalkHat0 = loadImage("R_walk_hat_0.png");
    private final Image characterRightWalkHat1 = loadImage("R_walk_hat_1.png");
    private final Image characterRightWalkHat2 = loadImage("R_walk_hat_2.png");
    private final Image characterStop = loadImage("risaju.png");
    private final Image characterRightJump = loadImage("R_jump_hat.png");

    public StageModel() {
        entity = new Entity(this);
        Image image = characterRightWalkHat0;
        entity.setImage(image);
        entity.setWidth(50);
        entity.setHeight(50);
        App.getWorldSubject().addObserver((world) -> {
            setWorld(world);
            reset();
        });
    }

    public void reset() {
        entity.setPosX(world.getStartPosX());
        entity.setPosY(world.getStartPosY());
        entity.setVelX(0);
        entity.setVelY(0);
        entity.setAccX(0);
        entity.setAccY(0);
        entity.setAlive(true);
        hangWire = null;
        clearKeys();
        world.resetState();
        tickCount = 0;
    }

    @Override
    public void addKey(int key) {
        if (availableKeys.contains(key)) {
            keys.add(key);
        } else if (key == KeyEvent.VK_F3) {
            isDebug = !isDebug;
        }
    }

    @Override
    public void removeKey(int key) {
        keys.remove(key);
    }

    @Override
    public boolean hasKey(int key) {
        return keys.contains(key);
    }

    @Override
    public void clearKeys() {
        keys.clear();
    }

    @Override
    public Set<Integer> getKeys() {
        return keys;
    }

    @Override
    public List<Integer> getAvailableKeys() {
        return availableKeys;
    }

    public Entity getEntity() {
        return entity;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public HangWire getHangWire() {
        return hangWire;
    }

    public int getTickCount() {
        return tickCount;
    }

    public World loadWorld(InputStream file, InputStream exampleCommandFile) {
        byte[] buffer = new byte[1024];
        String text = "";
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedInputStream bis = new BufferedInputStream(file)) {
            int len;
            while ((len = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            text = new String(baos.toByteArray());
        } catch (Exception e) {
        }

        String exampleCommand = "";
        buffer = new byte[1024];
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedInputStream bis = new BufferedInputStream(exampleCommandFile)) {
            int len;
            while ((len = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            exampleCommand = new String(baos.toByteArray());
        } catch (Exception e) {
        }

        WorldTemplate worldTemplate = new WorldTemplate(text, 800, 600, exampleCommand);

        World world = new World(this, worldTemplate);
        return world;
    }

    public World loadBinaryWorld(InputStream file) {
        try (ObjectInputStream ois = new ObjectInputStream(file)) {
            WorldTemplate worldTemplate = (WorldTemplate) ois.readObject();
            World world = new World(this, worldTemplate);
            return world;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to load world");
        }
    }

    public boolean hasHangWire() {
        return hangWire != null;
    }

    public boolean getIsDebug() {
        return isDebug;
    }

    public void setIsDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    public void tick() {
        tickCount++;
        // 横方向の移動
        double speed = 0.2;
        double accX = 0;
        if (entity.isOnGround()) {
            if (hasKey(65)) {
                accX -= 1;
                entity.setDirection(-1);
            }
            if (hasKey(68)) {
                accX += 1;
                entity.setDirection(1);
            }
        } else {
            if (hasKey(65)) {
                accX -= 0.5;
            }
            if (hasKey(68)) {
                accX += 0.5;
            }
        }

        entity.setAccX(accX * speed);

        // 重力とジャンプ
        double accY = gravity; // 最終的な加速度
        if (hasKey(32)) {
            // 下がタイルに接している場合ジャンプ
            if (entity.isOnGround()) {
                accY = jumpPower;
            }
        }
        entity.setAccY(accY);

        // 伸びないようにする
        // if (hasHangWire() && entity.getVelY() > 0) {
        // entity.setVelY(0);
        // }

        // ハングアクション
        Vector2 entitySize = new Vector2(entity.getWidth(), entity.getHeight());
        Vector2 entityPosition = new Vector2(entity.getPosX(), entity.getPosY()).add(entitySize.mul(0.5));

        if (hangWire == null) {
            if (hasKey(72)) {
                hangWire = new HangWire(entityPosition, new Vector2(-1, -1));
            }
            if (hasKey(75)) {
                hangWire = new HangWire(entityPosition, new Vector2(1, -1));
            }
        } else if (hasKey(74)) {
            hangWire = null;
        }

        if (hangWire != null) {
            hangWire.setStart(entityPosition);
            if (hangWire.isHanged()) {
                Vector2 hangDirection = hangWire.getEnd().sub(entityPosition).normalize();
                Vector2 tension = hangDirection.mul(hangTensionCoef);
                entity.setAccX(entity.getAccX() + tension.getX());
                entity.setAccY(entity.getAccY() + tension.getY());
            } else {
                double nextLength = hangWire.getProgress() + hangSpeed;
                Vector2 nextPosition = hangWire.getEnd(nextLength);

                if (nextPosition.getY() > world.getHeight() || nextPosition.getY() < 0
                        || nextPosition.getX() > world.getWidth() || nextPosition.getX() < 0) {
                    hangWire = null;
                    return;
                }

                if (world.getTileAt(nextPosition.getX(), nextPosition.getY()).isSolid()) {
                    double left = hangWire.getProgress();
                    double right = nextLength;
                    while (right - left > 0.005) {
                        double middle = (left + right) / 2;
                        if (world.getTileAt(hangWire.getEnd(middle).getX(), hangWire.getEnd(middle).getY()).isSolid()) {
                            right = middle;
                        } else {
                            left = middle;
                        }
                    }
                    hangWire.setProgress(left);
                    hangWire.setHanged(true);
                } else {
                    hangWire.setProgress(nextLength);
                }
            }
        }

        // 速度を加速度に加算
        entity.setVelX(entity.getVelX() + entity.getAccX());
        entity.setVelY(entity.getVelY() + entity.getAccY());

        // 位置を速度に加算
        entity.setPosX(entity.getPosX() + entity.getVelX());
        entity.setPosY(entity.getPosY() + entity.getVelY());

        // 速度を見て、キャラクターの画像を変更する
        if (entity.getVelX() > 0) {
            entity.setElapsedSinceStop(0);
        }
        // 速度や中空かを見て、キャラクターの画像を変更する
        if (!entity.isOnGround()) {
            if (entity.getVelX() > 0) {
                entity.setImage(characterRightJump);
            } else {
                entity.setImage(characterLeftJump);
            }
        } else if (entity.getVelX() > 0) {
            if (!entity.isOnGround() || (entity.getVelX() < 0.5)) {
                entity.setImage(characterRightWalkHat0);
            } else {
                if (entity.getVelX() % 2 < 1) {
                    entity.setImage(characterRightWalkHat1);
                } else {
                    entity.setImage(characterRightWalkHat2);
                }
            }
        } else if (entity.getVelX() < 0) {
            entity.setElapsedSinceStop(0);
            if (!entity.isOnGround() || (entity.getVelX() > -0.5)) {
                entity.setImage(characterLeftWalkHat0);
            } else {
                if (entity.getVelX() % 2 > -1) {
                    entity.setImage(characterLeftWalkHat1);
                } else {
                    entity.setImage(characterLeftWalkHat2);
                }
            }
        } else {
            entity.setElapsedSinceStop(entity.getElapsedSinceStop() + 1);
            if (entity.getDirection() == 1) {
                entity.setImage(characterRightWalkHat0);
            } else if (entity.getDirection() == -1) {
                entity.setImage(characterLeftWalkHat0);
            } else {
                entity.setImage(characterStop);
            }

            if (entity.getElapsedSinceStop() > 100) {
                entity.setImage(characterStop);
            }
        }
    }

    private Image loadImage(String name) {
        return new ImageIcon(getClass().getResource(
                "/medipro/images/" + name)).getImage();
    }
}
