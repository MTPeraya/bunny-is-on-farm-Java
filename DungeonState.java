import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class DungeonState extends GameState {
    public int width = 30;
    public int height = 30;
    public char[][] layout;
    private int camX, camY;

    // Entities
    public ArrayList<Enemy> enemies = new ArrayList<>();
    public ArrayList<Carrot> projectiles = new ArrayList<>();

    public DungeonState(Game game) {
        super(game);
        layout = new char[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                layout[y][x] = '#';
            }
        }
        
        generateDungeon();

        game.bunny.x = 1;
        game.bunny.y = 1;
        game.bunny.targetX = 1;
        game.bunny.targetY = 1;
    }

    private void generateDungeon() {
        createRoom(10, 5, 10, 10);
        createRoom(0, 0, 6, 6);
        createRoom(21, 3, 6, 6);
        createRoom(3, 15, 6, 6);
        createRoom(21, 15, 6, 6);

        createCorridor(15, 10, 3, 3);
        createCorridor(15, 10, 24, 6);
        createCorridor(15, 10, 6, 18);
        createCorridor(15, 10, 24, 18);

        // Spawn Boss
        enemies.add(new Enemy(15, 10, "boss"));
        
        // Normal Enemies
        int[][] positions = {{5, 5}, {7, 7}, {24, 5}, {22, 7}};
        for (int[] pos : positions) {
            enemies.add(new Enemy(pos[0], pos[1], "normal"));
        }
    }

    private void createRoom(int x, int y, int w, int h) {
        for (int i = y; i < y + h; i++) {
            for (int j = x; j < x + w; j++) {
                if (i >= 0 && i < height && j >= 0 && j < width) layout[i][j] = '.';
            }
        }
    }

    private void createCorridor(int x1, int y1, int x2, int y2) {
        int stepX = x2 > x1 ? 1 : -1;
        for (int x = x1; x != x2 + stepX; x += stepX) {
            if (x >= 0 && x < width) layout[y1][x] = '.';
        }
        int stepY = y2 > y1 ? 1 : -1;
        for (int y = y1; y != y2 + stepY; y += stepY) {
            if (y >= 0 && y < height) layout[y][x2] = '.';
        }
    }

    public boolean isWalkable(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return false;
        return layout[y][x] == '.';
    }

    @Override
    public void update(boolean[] keys) {
        // We pass null for Farm to bunny move, but bunny logic handles Farm
        // We will customize bunny movement check or use the interface approach. 
        // For simplicity, Bunny movement needs to check isWalkable. 
        // Let's modify Bunny temporarily in update or implement it via a checker.
        handleBunnyMovement(keys);

        // Camera
        int targetCamX = (int)(game.bunny.x * Config.TILE_SIZE - Config.WINDOW_WIDTH / 2);
        int targetCamY = (int)(game.bunny.y * Config.TILE_SIZE - Config.WINDOW_HEIGHT / 2);
        camX += (targetCamX - camX) * 0.1;
        camY += (targetCamY - camY) * 0.1;

        // Projectiles
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Carrot p = projectiles.get(i);
            p.update();
            if (p.isDead() || !isWalkable((int)(p.x/Config.TILE_SIZE), (int)(p.y/Config.TILE_SIZE))) {
                projectiles.remove(i);
                continue;
            }
            // Check collision with enemies
            for (int j = enemies.size() - 1; j >= 0; j--) {
                Enemy e = enemies.get(j);
                if (p.getBounds().intersects(e.getBounds())) {
                    e.takeDamage(20);
                    projectiles.remove(i);
                    break;
                }
            }
        }

        // Enemies
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);
            e.update(game.bunny, this);
            if (e.health <= 0) {
                if (e.type.equals("boss")) {
                    game.bunny.inventory.addItem("boss_key", 1);
                    game.bunny.inventory.showNotification("Boss Defeated! You got the Boss Key!");
                }
                enemies.remove(i);
            }
        }
    }

    @Override
    public void keyPressed(int keyCode) {
        // Space to attack
        if (keyCode == KeyEvent.VK_SPACE) {
            projectiles.add(new Carrot(game.bunny.x, game.bunny.y, game.bunny.currentDirection));
        }
        
        // Exit check
        if (keyCode == KeyEvent.VK_E) {
            if ((int)game.bunny.x == width - 2 && (int)game.bunny.y == height - 2) {
                game.setState(new FarmState(game));
            }
        }
    }


    private void handleBunnyMovement(boolean[] keys) {
        Bunny b = game.bunny;
        if (Math.abs(b.x - b.targetX) < 0.01 && Math.abs(b.y - b.targetY) < 0.01) {
            b.x = b.targetX;
            b.y = b.targetY;
            if (keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT]) {
                if (isWalkable((int)b.x - 1, (int)b.y)) b.targetX = b.x - 1;
                b.currentDirection = "left";
            } else if (keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT]) {
                if (isWalkable((int)b.x + 1, (int)b.y)) b.targetX = b.x + 1;
                b.currentDirection = "right";
            } else if (keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP]) {
                if (isWalkable((int)b.x, (int)b.y - 1)) b.targetY = b.y - 1;
                b.currentDirection = "back";
            } else if (keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN]) {
                if (isWalkable((int)b.x, (int)b.y + 1)) b.targetY = b.y + 1;
                b.currentDirection = "front";
            }
        }
        
        if (b.x < b.targetX) b.x = Math.min(b.x + b.speed, b.targetX);
        else if (b.x > b.targetX) b.x = Math.max(b.x - b.speed, b.targetX);
        
        if (b.y < b.targetY) b.y = Math.min(b.y + b.speed, b.targetY);
        else if (b.y > b.targetY) b.y = Math.max(b.y - b.speed, b.targetY);
    }

    @Override
    public void draw(Graphics g) {
        Color wallColor = new Color(100, 100, 100);
        Color floorColor = new Color(200, 200, 200);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int px = x * Config.TILE_SIZE - camX;
                int py = y * Config.TILE_SIZE - camY;
                if (layout[y][x] == '#') {
                    g.setColor(wallColor);
                    g.fillRect(px, py, Config.TILE_SIZE, Config.TILE_SIZE);
                } else {
                    g.setColor(floorColor);
                    g.fillRect(px, py, Config.TILE_SIZE, Config.TILE_SIZE);
                }
            }
        }

        // Draw exit logic (blue portal)
        int ex = (width - 2) * Config.TILE_SIZE - camX;
        int ey = (height - 2) * Config.TILE_SIZE - camY;
        g.setColor(Color.BLUE);
        g.fillOval(ex, ey, Config.TILE_SIZE, Config.TILE_SIZE);

        for (Enemy e : enemies) e.draw(g, camX, camY);
        for (Carrot c : projectiles) c.draw(g, camX, camY);

        game.bunny.draw(g, camX, camY);

        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(Color.WHITE);
        g.drawString("Dungeon Mode | Health: " + game.bunny.health, 10, 30);
        g.drawString("Keys (E) To Exit Portal at End", 10, 60);

        game.bunny.inventory.draw(g);
    }
}
