import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class FarmState extends GameState {
    private Farm farm;
    private int camX, camY;

    public FarmState(Game game) {
        super(game);
        this.farm = new Farm();
        // Starter Kit
        game.bunny.inventory.addItem("carrot_weapon", 1);
        game.bunny.inventory.addItem("carrot_seed", 5);
        game.bunny.inventory.addItem("potato_seed", 5);
        game.bunny.money += 200;
        game.bunny.inventory.showNotification("Received starter kit!");
    }

    @Override
    public void update(boolean[] keys) {
        // Physics and camera
        game.bunny.move(keys, farm);
        farm.update();
        
        // Smooth camera
        int targetCamX = (int)(game.bunny.x * Config.TILE_SIZE - Config.WINDOW_WIDTH / 2);
        int targetCamY = (int)(game.bunny.y * Config.TILE_SIZE - Config.WINDOW_HEIGHT / 2);
        camX += (targetCamX - camX) * 0.1;
        camY += (targetCamY - camY) * 0.1;
    }

    @Override
    public void keyPressed(int keyCode) {
        // Inventory toggle
        if (keyCode == KeyEvent.VK_I) {
            game.bunny.inventory.fullView = !game.bunny.inventory.fullView;
        }
        
        // Hotbar shortcuts
        if (keyCode >= KeyEvent.VK_1 && keyCode <= KeyEvent.VK_6) {
            game.bunny.selectHotbarItem(keyCode - KeyEvent.VK_1);
        }
        
        // Portals
        if (keyCode == KeyEvent.VK_P) {
            int cx = (int)game.bunny.x;
            int cy = (int)game.bunny.y;
            if (cx >= farm.width - 4 && cy >= farm.height - 4) {
                if (Math.random() > 0.5) game.setState(new DungeonState(game));
                else game.setState(new MazeState(game));
            }
        }
        
        // Interaction
        if (keyCode == KeyEvent.VK_SPACE) {
            handleInteraction();
        }
    }

    private void handleInteraction() {
        int[] front = game.bunny.getFrontPosition();
        int fx = front[0];
        int fy = front[1];

        if (fx >= 0 && fx < farm.width && fy >= 0 && fy < farm.height) {
            Tile tile = farm.tiles[fy][fx];

            if (tile.type.equals("tree") || tile.type.equals("stone")) {
                boolean destroyed = tile.takeDamage(4); // Improved: Damage faster (4 per hit instead of 1 out of 10)
                if (destroyed) {
                    int yield = 2 + (int)(Math.random() * 3); // Improved: Drops multiple resources!
                    game.bunny.inventory.addItem(tile.type.equals("tree") ? "wood" : "stone", yield);
                    tile.type = "dirt";
                    tile.health = 0;
                    game.bunny.inventory.showNotification("Looted " + yield + " resources!");
                } else {
                    game.bunny.inventory.showNotification(tile.type.equals("tree") ? "Chopping..." : "Mining...");
                }
            } else if (tile.type.equals("dirt")) {
                if (!tile.dug) {
                    tile.dig();
                    game.bunny.inventory.showNotification("Dug the ground!");
                } else if (tile.plant != null && tile.plant.harvestable) {
                    int cropYield = 1 + (int)(Math.random() * 2); // Improved: Chance to yield 2 crops
                    game.bunny.inventory.addItem(tile.plant.cropType, cropYield); 
                    game.bunny.inventory.showNotification("Harvested " + cropYield + " " + tile.plant.cropType + "!");
                    tile.plant = null;
                } else if (tile.plant == null && game.bunny.heldItem != null && game.bunny.heldItem.endsWith("_seed")) {
                    String cropType = game.bunny.heldItem.replace("_seed", "");
                    if (game.bunny.inventory.useItem(game.bunny.heldItem)) {
                        tile.plant = new Plant(cropType);
                        game.bunny.inventory.showNotification("Planted " + cropType);
                    }
                } else if (tile.dug && tile.plant == null) {
                    tile.water();
                    game.bunny.inventory.showNotification("Watered tile!");
                }
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        farm.draw(g, camX, camY);
        game.bunny.draw(g, camX, camY);

        // Draw UI
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(Color.WHITE);
        g.drawString("Health: " + game.bunny.health, 10, 30);
        g.drawString("Money: " + game.bunny.money, 10, 60);
        g.drawString("Holding: " + (game.bunny.heldItem != null ? game.bunny.heldItem : "None"), 10, 90);
        
        // Date
        g.setColor(Config.COLOR_BROWN);
        g.fillRoundRect(490, 10, 300, 30, 10, 10);
        g.setColor(Config.COLOR_SKY);
        g.drawString(farm.calendar.getDateString(), 500, 32);

        // Portal rendering & hint
        g.setColor(new Color(150, 0, 200, 150));
        int portalX = (farm.width - 3) * Config.TILE_SIZE - camX;
        int portalY = (farm.height - 2) * Config.TILE_SIZE - camY;
        g.fillOval(portalX, portalY, Config.TILE_SIZE, Config.TILE_SIZE);
        
        int[] front = game.bunny.getFrontPosition();
        int bx = (int)game.bunny.x;
        int by = (int)game.bunny.y;
        if (bx >= farm.width - 4 && by >= farm.height - 4) {
            g.setColor(Color.WHITE);
            g.drawString("Press P to Enter Portal", 10, 120);
        } else if (front[0] >= 0 && front[0] < farm.width && front[1] >= 0 && front[1] < farm.height) {
            Tile tile = farm.tiles[front[1]][front[0]];
            String text = "Interact (SPACE)";
            if (tile.type.equals("tree")) text = "Chop (SPACE)";
            else if (tile.type.equals("stone")) text = "Mine (SPACE)";
            else if (tile.type.equals("dirt") && !tile.dug) text = "Dig (SPACE)";
            else if (tile.plant != null && tile.plant.harvestable) text = "Harvest (SPACE)";
            g.setColor(Color.WHITE);
            g.drawString(text, 10, 120);
        }

        game.bunny.inventory.draw(g);
    }
}
