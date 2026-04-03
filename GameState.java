import java.awt.Graphics;

public abstract class GameState {
    protected Game game;

    public GameState(Game game) {
        this.game = game;
    }

    public abstract void update(boolean[] keys);
    public abstract void draw(Graphics g);
    
    // Optional methods that states can override
    public void keyPressed(int keyCode) {}
    public void keyReleased(int keyCode) {}
}
