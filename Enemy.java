import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

public class Enemy {
    public double x, y;
    public String type; // normal, rare, boss
    public int health;
    public int maxHealth;
    public boolean isAwake = false;
    
    // Movement AI
    private String direction = "up";
    private int directionTimer = 0;
    private double speed;
    public int attackPower = 15;
    public int size = Config.TILE_SIZE;

    private Color color;
    private Random random = new Random();

    public Enemy(double x, double y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;

        if (type.equals("boss")) {
            health = maxHealth = 500;
            speed = 0.02;
            attackPower = 40;
            color = Color.RED;
            size = (int)(Config.TILE_SIZE * 1.5);
        } else if (type.equals("rare")) {
            health = maxHealth = 200;
            speed = 0.03;
            attackPower = 25;
            color = Color.BLUE;
        } else {
            health = maxHealth = 100;
            speed = 0.05;
            attackPower = 15;
            color = Color.GRAY;
        }
    }

    public void update(Bunny bunny, DungeonState dungeon) {
        if (!isAwake) {
            double dist = Math.hypot(bunny.x - x, bunny.y - y);
            if (dist < 5) isAwake = true;
            return;
        }

        directionTimer--;
        if (directionTimer <= 0) {
            String[] dirs = {"left", "right", "up", "down"};
            direction = dirs[random.nextInt(4)];
            directionTimer = 30 + random.nextInt(90);
        }

        double nx = x, ny = y;
        if (direction.equals("left")) nx -= speed;
        if (direction.equals("right")) nx += speed;
        if (direction.equals("up")) ny -= speed;
        if (direction.equals("down")) ny += speed;

        if (dungeon.isWalkable((int)nx, (int)ny)) {
            x = nx;
            y = ny;
        } else {
            directionTimer = 0;
        }

        if (getBounds().intersects(getBunnyBounds(bunny))) {
            bunny.health -= attackPower;
            isAwake = false; // Add small cooldown?
        }
    }

    public void takeDamage(int amount) {
        this.health -= amount;
    }

    public Rectangle getBounds() {
        return new Rectangle((int)(x * Config.TILE_SIZE), (int)(y * Config.TILE_SIZE), size, size);
    }

    private Rectangle getBunnyBounds(Bunny bunny) {
        return new Rectangle((int)(bunny.x * Config.TILE_SIZE), (int)(bunny.y * Config.TILE_SIZE), Config.TILE_SIZE, Config.TILE_SIZE);
    }

    public void draw(Graphics g, int camX, int camY) {
        int screenX = (int)(x * Config.TILE_SIZE) - camX;
        int screenY = (int)(y * Config.TILE_SIZE) - camY;

        g.setColor(color);
        g.fillRect(screenX, screenY, size, size);

        // Health bar
        g.setColor(Color.RED);
        g.fillRect(screenX, screenY - 10, size, 5);
        g.setColor(Color.GREEN);
        g.fillRect(screenX, screenY - 10, (int)(size * (health / (double)maxHealth)), 5);
    }
}
