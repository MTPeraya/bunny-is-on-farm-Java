import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Random;

public class MazeState extends GameState {
    private int rows = 50;
    private int cols = 50;
    private int[][] grid;
    private int exitX, exitY;
    private int camX, camY;

    private long startTime;
    private final long TIME_LIMIT = 600 * 1000; // 600 seconds = 10 minutes

    public MazeState(Game game) {
        super(game);
        grid = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = 1; // Wall
            }
        }
        
        generateMaze(1, 1);
        addLoops(10);
        
        Random rand = new Random();
        while (true) {
            exitX = 1 + rand.nextInt(cols - 2);
            exitY = 1 + rand.nextInt(rows - 2);
            if (grid[exitY][exitX] == 0) break;
        }

        game.bunny.x = 1;
        game.bunny.y = 1;
        game.bunny.targetX = 1;
        game.bunny.targetY = 1;

        startTime = System.currentTimeMillis();
    }

    private void generateMaze(int r, int c) {
        int[] dr = {0, 0, -1, 1};
        int[] dc = {-1, 1, 0, 0};
        
        grid[r][c] = 0;
        
        // Shuffle directions
        Random rand = new Random();
        for (int i = 0; i < 4; i++) {
            int rId = rand.nextInt(4);
            int t1 = dr[i], t2 = dc[i];
            dr[i] = dr[rId]; dc[i] = dc[rId];
            dr[rId] = t1; dc[rId] = t2;
        }

        for (int i = 0; i < 4; i++) {
            int nr = r + dr[i] * 2;
            int nc = c + dc[i] * 2;
            if (nr > 0 && nr < rows - 1 && nc > 0 && nc < cols - 1 && grid[nr][nc] == 1) {
                grid[r + dr[i]][c + dc[i]] = 0;
                generateMaze(nr, nc);
            }
        }
    }

    private void addLoops(int num) {
        Random rand = new Random();
        for (int i = 0; i < num; i++) {
            int r = 1 + rand.nextInt(rows - 2);
            int c = 1 + rand.nextInt(cols - 2);
            grid[r][c] = 0;
        }
    }

    public boolean isWalkable(int x, int y) {
        if (x < 0 || x >= cols || y < 0 || y >= rows) return false;
        return grid[y][x] == 0;
    }

    @Override
    public void update(boolean[] keys) {
        handleBunnyMovement(keys);

        // Camera
        int targetCamX = (int)(game.bunny.x * Config.TILE_SIZE - Config.WINDOW_WIDTH / 2);
        int targetCamY = (int)(game.bunny.y * Config.TILE_SIZE - Config.WINDOW_HEIGHT / 2);
        camX += (targetCamX - camX) * 0.1;
        camY += (targetCamY - camY) * 0.1;

        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > TIME_LIMIT) {
            game.setState(new FarmState(game)); // Fail condition
        }
    }

    @Override
    public void keyPressed(int keyCode) {
        if (keyCode == KeyEvent.VK_E) {
            if ((int)game.bunny.x == exitX && (int)game.bunny.y == exitY) {
                // Win
                game.bunny.inventory.addItem("diamond", 5);
                game.bunny.money += 500;
                game.bunny.inventory.showNotification("Maze Completed! +Diamonds & Money");
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
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int px = c * Config.TILE_SIZE - camX;
                int py = r * Config.TILE_SIZE - camY;
                if (grid[r][c] == 1) {
                    if (Config.environ.get("bush_dun") != null) g.drawImage(Config.environ.get("bush_dun"), px, py, null);
                    else { g.setColor(Color.DARK_GRAY); g.fillRect(px, py, Config.TILE_SIZE, Config.TILE_SIZE); }
                } else {
                    if (Config.environ.get("dirt_dun") != null) g.drawImage(Config.environ.get("dirt_dun"), px, py, null);
                    else { g.setColor(Color.LIGHT_GRAY); g.fillRect(px, py, Config.TILE_SIZE, Config.TILE_SIZE); }
                }
            }
        }

        // Exit
        g.setColor(Color.MAGENTA);
        g.fillOval(exitX * Config.TILE_SIZE - camX, exitY * Config.TILE_SIZE - camY, Config.TILE_SIZE, Config.TILE_SIZE);

        game.bunny.draw(g, camX, camY);

        long timeRemaining = (TIME_LIMIT - (System.currentTimeMillis() - startTime)) / 1000;
        int min = (int)timeRemaining / 60;
        int sec = (int)timeRemaining % 60;

        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(Color.WHITE);
        g.drawString(String.format("Time Left: %d:%02d", min, sec), 10, 30);
        
        // Compass simple line
        int compassX = Config.WINDOW_WIDTH - 50, compassY = 85;
        g.drawOval(compassX - 40, compassY - 40, 80, 80);
        double angle = Math.atan2(exitY - game.bunny.y, exitX - game.bunny.x);
        int ax = compassX + (int)(30 * Math.cos(angle));
        int ay = compassY + (int)(30 * Math.sin(angle));
        g.setColor(Color.RED);
        g.drawLine(compassX, compassY, ax, ay);
    }
}
