import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;

public class Farm {
    public Tile[][] tiles;
    public int width = 50;
    public int height = 30;
    
    public Calendar calendar;

    public Farm() {
        calendar = new Calendar();
        tiles = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x] = new Tile("dirt", x, y);
            }
        }
        generateTerrain();
    }

    private void generateTerrain() {
        Random rand = new Random();
        // Trees
        for(int i=0; i<40; i++) {
            int x = 3 + rand.nextInt(width - 7);
            int y = 3 + rand.nextInt(height - 7);
            tiles[y][x] = new Tile("tree", x, y);
        }
        // Stones
        for(int i=0; i<25; i++) {
            int x = 3 + rand.nextInt(width - 7);
            int y = 3 + rand.nextInt(height - 7);
            tiles[y][x] = new Tile("stone", x, y);
        }
        
        // House
        for (int x = 10; x < 16; x++) {
            for (int y = 10; y < 14; y++) {
                tiles[y][x] = new Tile("house", x, y);
            }
        }
        
        // Mailbox placeholder (will represent as tile since logic is simplified)
        tiles[14][15] = new Tile("dirt", 15, 14); 

        // Wall edge test
        tiles[28][48] = new Tile("wall", 48, 28);
    }

    public void update() {
        calendar.update();
        String season = calendar.getCurrentSeason();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x].update(season);
            }
        }
    }

    public void draw(Graphics g, int camX, int camY) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x].draw(g, camX, camY);
            }
        }
        
        // Draw House Image overlay
        Image houseImg = Config.environ.get("house");
        if (houseImg != null) {
            int screenX = 8 * Config.TILE_SIZE - camX;
            int screenY = 8 * Config.TILE_SIZE - camY;
            g.drawImage(houseImg, screenX, screenY, Config.TILE_SIZE * 10, Config.TILE_SIZE * 8, null);
        }
        
        // Draw mailbox
        Image mailbox = Config.environ.get("mailbox");
        if (mailbox != null) {
            int screenX = 15 * Config.TILE_SIZE - camX;
            int screenY = 14 * Config.TILE_SIZE - camY;
            int scaled = (int)(Config.TILE_SIZE * 1.5);
            g.drawImage(mailbox, screenX - scaled/4, screenY - scaled/2, scaled, scaled, null);
        }
    }

    public boolean isWalkable(int x, int y) {
        if(x < 0 || x >= width || y < 0 || y >= height) return false;
        String type = tiles[y][x].type;
        return !type.equals("tree") && !type.equals("stone") && !type.equals("house") && !type.equals("wall");
    }
}