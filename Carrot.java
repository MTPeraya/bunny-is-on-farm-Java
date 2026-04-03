import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

public class Carrot {
    public double x, y;
    public double dx, dy;
    public double distance = 0;
    public final double MAX_DISTANCE = 5 * Config.TILE_SIZE;
    public final double SPEED = 5.0;

    public Carrot(double startX, double startY, String direction) {
        this.x = startX * Config.TILE_SIZE;
        this.y = startY * Config.TILE_SIZE;
        
        switch(direction) {
            case "front": this.dy = SPEED; this.dx = 0; break;
            case "back": this.dy = -SPEED; this.dx = 0; break;
            case "left": this.dx = -SPEED; this.dy = 0; break;
            case "right": this.dx = SPEED; this.dy = 0; break;
            default: this.dy = SPEED; this.dx = 0; break;
        }
    }

    public void update() {
        x += dx;
        y += dy;
        distance += SPEED;
    }

    public boolean isDead() {
        return distance > MAX_DISTANCE;
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, Config.TILE_SIZE/2, Config.TILE_SIZE/2);
    }

    public void draw(Graphics g, int camX, int camY) {
        Image img = Config.items.get("carrot_weapon");
        if (img != null) {
            g.drawImage(img, (int)x - camX, (int)y - camY, Config.TILE_SIZE/2, Config.TILE_SIZE/2, null);
        } else {
            g.setColor(java.awt.Color.ORANGE);
            g.fillOval((int)x - camX, (int)y - camY, Config.TILE_SIZE/2, Config.TILE_SIZE/2);
        }
    }
}
