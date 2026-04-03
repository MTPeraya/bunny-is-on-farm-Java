import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

public class Bunny {
    public String name;
    public double x, y;
    public double targetX, targetY;
    public int health = 100;
    public double speed = 0.1;
    
    public Inventory inventory;
    public int money = 0;
    
    public String currentDirection = "front";
    private int currentFrame = 0;
    private long lastAnimationUpdate = 0;
    
    public String heldItem = null;

    public Bunny(double x, double y, String name) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.inventory = new Inventory();
    }

    public void move(boolean[] keys, Farm farm) {
        boolean moving = false;
        String newDirection = currentDirection;

        // If at target or close enough
        if (Math.abs(x - targetX) < 0.01 && Math.abs(y - targetY) < 0.01) {
            x = targetX;
            y = targetY;
            
            if (keys[37] || keys[65]) { // Left or A
                if (farm == null || farm.isWalkable((int)x - 1, (int)y)) {
                    targetX = x - 1;
                    moving = true;
                }
                newDirection = "left";
            } else if (keys[39] || keys[68]) { // Right or D
                if (farm == null || farm.isWalkable((int)x + 1, (int)y)) {
                    targetX = x + 1;
                    moving = true;
                }
                newDirection = "right";
            } else if (keys[38] || keys[87]) { // Up or W
                if (farm == null || farm.isWalkable((int)x, (int)y - 1)) {
                    targetY = y - 1;
                    moving = true;
                }
                newDirection = "back";
            } else if (keys[40] || keys[83]) { // Down or S
                if (farm == null || farm.isWalkable((int)x, (int)y + 1)) {
                    targetY = y + 1;
                    moving = true;
                }
                newDirection = "front";
            }
        }

        // Interpolate movement
        if (x < targetX) x = Math.min(x + speed, targetX);
        else if (x > targetX) x = Math.max(x - speed, targetX);
        
        if (y < targetY) y = Math.min(y + speed, targetY);
        else if (y > targetY) y = Math.max(y - speed, targetY);

        if (!newDirection.equals(currentDirection)) {
            currentDirection = newDirection;
            currentFrame = 0;
        }

        updateAnimation(moving);
    }

    private void updateAnimation(boolean moving) {
        long current = System.currentTimeMillis();
        int frameDelay = 1000 / Config.FPS;
        if (moving && current - lastAnimationUpdate > frameDelay * 4) { // Slow down animation a bit
            currentFrame = (currentFrame + 1) % 4; // Assume 4 frames per animation
            lastAnimationUpdate = current;
        }
    }

    public void selectHotbarItem(int index) {
        if (index >= 0 && index < 6) {
            Integer itemIndex = inventory.hotbarIndices[index];
            if (itemIndex != null) {
                ArrayList<String> keys = new ArrayList<>(inventory.items.keySet());
                if (itemIndex < keys.size()) {
                    heldItem = keys.get(itemIndex);
                    // System.out.println("Holding: " + heldItem);
                }
            }
        }
    }

    public int[] getFrontPosition() {
        if (currentDirection.equals("front")) return new int[]{(int)x, (int)y + 1};
        if (currentDirection.equals("back")) return new int[]{(int)x, (int)y - 1};
        if (currentDirection.equals("left")) return new int[]{(int)x - 1, (int)y};
        if (currentDirection.equals("right")) return new int[]{(int)x + 1, (int)y};
        return new int[]{(int)x, (int)y};
    }

    public void draw(Graphics g, int camX, int camY) {
        int screenX = (int)(x * Config.TILE_SIZE) - camX;
        int screenY = (int)(y * Config.TILE_SIZE) - camY;

        SpriteSheet sheet = Config.bunSheets.get(currentDirection + "_sheet");
        if (sheet != null) {
            // Determine max frames for direction
            int maxFrames = (currentDirection.equals("left") || currentDirection.equals("right")) ? 8 : 5;
            int frame = currentFrame % maxFrames;
            Image frameImg = sheet.getImage(frame, Config.BUN_EXACT, Config.BUN_EXACT, 2, Config.COLOR_BLACK);
            g.drawImage(frameImg, screenX, screenY, null);
        } else {
            // Fallback
            g.setColor(Config.COLOR_BUNNY);
            g.fillRect(screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE);
        }

        // Draw health bar
        g.setColor(Color.RED);
        g.fillRect(screenX, screenY - 10, Config.TILE_SIZE, 5);
        g.setColor(Color.GREEN);
        g.fillRect(screenX, screenY - 10, (int)(Config.TILE_SIZE * (health / 100.0)), 5);
    }
}