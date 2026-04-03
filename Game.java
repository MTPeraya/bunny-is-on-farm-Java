import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Game extends JPanel implements Runnable, KeyListener, MouseListener {
    private boolean running = true;
    private boolean[] keys = new boolean[256];
    
    // State machine
    private GameState currentState;
    
    // Double buffering / camera
    private BufferedImage buffer;
    
    // Shared core objects that persist across states
    public Bunny bunny;

    public Game() {
        setPreferredSize(new Dimension(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT));
        setBackground(Config.COLOR_BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        
        buffer = new BufferedImage(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);

        // Load config images
        Config.loadImages();
        
        // Initialize core entities
        bunny = new Bunny(15, 15, "Player");
        
        // Init initial state (Farm)
        currentState = new FarmState(this);
    }

    public void setState(GameState state) {
        this.currentState = state;
    }

    @Override
    public void run() {
        // Simple game loop
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000D / Config.FPS;
        double delta = 0;
        
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;
            
            boolean shouldRender = false;

            while (delta >= 1) {
                update();
                delta--;
                shouldRender = true;
            }
            
            if (shouldRender) {
                render();
            } else {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void update() {
        if (currentState != null) {
            currentState.update(keys);
        }
    }

    private void render() {
        Graphics2D g2d = (Graphics2D) buffer.getGraphics();
        
        // Clear background
        g2d.setColor(Config.COLOR_BLACK);
        g2d.fillRect(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        
        if (currentState != null) {
            currentState.draw(g2d);
        }
        
        g2d.dispose();
        
        Graphics g = getGraphics();
        if (g != null) {
            g.drawImage(buffer, 0, 0, null);
            g.dispose();
        }
    }

    // Input Handling
    @Override
    public void keyPressed(KeyEvent e) { 
        if(e.getKeyCode() < 256) keys[e.getKeyCode()] = true; 
        if(currentState != null) currentState.keyPressed(e.getKeyCode());
    }
    
    @Override
    public void keyReleased(KeyEvent e) { 
        if(e.getKeyCode() < 256) keys[e.getKeyCode()] = false; 
        if(currentState != null) currentState.keyReleased(e.getKeyCode());
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}

    // Mouse Handling (For clicking UI like mailbox logic)
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

}