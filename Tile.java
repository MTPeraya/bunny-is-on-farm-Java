import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Color;
import java.util.Random;

public class Tile {
    public String type; // dirt, tree, stone, house, wall
    public boolean dug = false;
    public int x, y;
    
    // Resource properties
    public int health = 0;
    public int maxHealth = 0;
    
    // Farming
    public Plant plant = null;
    public boolean watered = false;
    public long lastWatered = 0;
    
    private Random random = new Random();
    private float treeScale = 1.0f;
    private float stoneScale = 0.3f + random.nextFloat() * 0.2f;
    private int imageOffsetX = (int)((random.nextDouble() - 0.5) * 8); // -4 to 4

    public Tile(String type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        if (type.equals("tree") || type.equals("stone")) {
            health = 10;
            maxHealth = 10;
        }
    }

    public void update(String currentSeason) {
        if (plant != null) {
            plant.update(currentSeason);
        }
        
        if (watered && System.currentTimeMillis() - lastWatered > 10000) { // 10 seconds watered
            watered = false;
            // Optionally could kill un-harvested plants, but for now just dries 
        }
    }

    public boolean dig() {
        if (type.equals("dirt") && !dug) {
            dug = true;
            return true;
        }
        return false;
    }

    public void water() {
        if (dug) {
            watered = true;
            lastWatered = System.currentTimeMillis();
        }
    }

    public boolean takeDamage(int amount) {
        if (type.equals("tree") || type.equals("stone")) {
            health = Math.max(0, health - amount);
            return health <= 0;
        }
        return false;
    }

    public void draw(Graphics g, int camX, int camY) {
        Graphics2D g2d = (Graphics2D) g;
        int screenX = x * Config.TILE_SIZE - camX;
        int screenY = y * Config.TILE_SIZE - camY;
        int size = Config.TILE_SIZE;

        if (type.equals("house")) {
            g.setColor(new Color(150, 75, 0));
            g.fillRect(screenX, screenY, size, size);
        } else if (type.equals("wall")) {
            Image wallImg = Config.environ.get("wall");
            if (wallImg != null) g.drawImage(wallImg, screenX, screenY, size, size, null);
        } else {
            // Draw base dirt
            Image dirtImg = Config.environ.get("dirt");
            if (dirtImg != null) g.drawImage(dirtImg, screenX, screenY, size, size, null);
            
            // Draw dug overlay
            if (dug && type.equals("dirt")) {
                Image soilImg = Config.environ.get("soil_overlay");
                if (soilImg != null) g.drawImage(soilImg, screenX, screenY, size, size, null);
            }
            
            // Draw tree or stone
            if (type.equals("tree") || type.equals("stone")) {
                Image overlayImg = Config.environ.get(type);
                if (overlayImg != null) {
                    float scale = type.equals("tree") ? treeScale : stoneScale;
                    int scaledW = (int)(size * scale);
                    int scaledH = (int)(size * scale);
                    int imgX = screenX + (size - scaledW) / 2 + imageOffsetX;
                    int imgY = screenY + (size - scaledH); // Align to bottom
                    g.drawImage(overlayImg, imgX, imgY, scaledW, scaledH, null);
                }
            }
        }
        
        // Draw plant
        if (plant != null) {
            plant.draw(g, screenX, screenY, size);
        }
        
        // Draw water overlay
        if (watered) {
            Color color = new Color(0, 100, 255, 60); // Transparent blue
            g.setColor(color);
            g.fillRect(screenX, screenY, size, size);
        }
    }
}