import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Inventory {
    public int capacity = 20;
    public Map<String, Integer> items = new HashMap<>();
    public Integer[] hotbarIndices = new Integer[6];
    public boolean fullView = false;

    private String notification = "";
    private long notificationTime = 0;

    public Inventory() {
        for (int i = 0; i < 6; i++) hotbarIndices[i] = null;
    }

    public boolean isFull() {
        int total = 0;
        for (int v : items.values()) total += v;
        return total >= capacity;
    }

    public boolean addItem(String itemName, int amount) {
        if (!isFull()) {
            if (!items.containsKey(itemName)) {
                // Find empty hotbar slot
                for (int i = 0; i < 6; i++) {
                    if (hotbarIndices[i] == null) {
                        hotbarIndices[i] = items.size();
                        break;
                    }
                }
            }
            items.put(itemName, items.getOrDefault(itemName, 0) + amount);
            return true;
        }
        return false;
    }

    public boolean useItem(String itemName) {
        if (items.getOrDefault(itemName, 0) > 0) {
            if (!itemName.equals("carrot_weapon")) { // carrot weapons are infinite typically in this game
                int count = items.get(itemName);
                items.put(itemName, count - 1);
            }
            return true;
        }
        return false;
    }

    public void showNotification(String text) {
        this.notification = text;
        this.notificationTime = System.currentTimeMillis();
    }

    public void draw(Graphics g) {
        if (fullView) {
            drawFullInventory(g);
        } else {
            drawQuickBar(g);
        }

        // Draw notification
        if (System.currentTimeMillis() - notificationTime < 3000) {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.setColor(Color.GREEN);
            g.drawString(notification, Config.WINDOW_WIDTH / 2 - 50, 50);
        }
    }

    private void drawQuickBar(Graphics g) {
        int slotSize = 64;
        int padding = 5;
        int startX = (Config.WINDOW_WIDTH - (slotSize + padding) * 6) / 2;
        int y = Config.WINDOW_HEIGHT - slotSize - 10;

        ArrayList<Map.Entry<String, Integer>> itemList = new ArrayList<>(items.entrySet());

        for (int i = 0; i < 6; i++) {
            g.setColor(new Color(200, 200, 200, 150));
            g.fillRect(startX + i * (slotSize + padding), y, slotSize, slotSize);
            g.setColor(Color.WHITE);
            g.drawRect(startX + i * (slotSize + padding), y, slotSize, slotSize);

            Integer idx = hotbarIndices[i];
            if (idx != null && idx < itemList.size()) {
                Map.Entry<String, Integer> entry = itemList.get(idx);
                if (entry.getValue() > 0) {
                    Image img = Config.items.get(entry.getKey());
                    if (img != null) {
                        g.drawImage(img, startX + i * (slotSize + padding) + 5, y + 5, slotSize - 10, slotSize - 10, null);
                    }
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 12));
                    g.drawString("" + entry.getValue(), startX + i * (slotSize + padding) + slotSize - 15, y + slotSize - 5);
                }
            }
            
            // Draw hotkey number
            g.setColor(Color.YELLOW);
            g.drawString(String.valueOf(i + 1), startX + i * (slotSize + padding) + 5, y + 15);
        }
    }

    private void drawFullInventory(Graphics g) {
        // Simplified full inventory drawing for this phase
        int width = 600;
        int height = 300;
        int boxX = (Config.WINDOW_WIDTH - width) / 2;
        int boxY = (Config.WINDOW_HEIGHT - height) / 2;

        g.setColor(new Color(50, 50, 50, 200));
        g.fillRect(boxX, boxY, width, height);
        g.setColor(Color.WHITE);
        g.drawRect(boxX, boxY, width, height);

        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Inventory", boxX + 20, boxY + 30);

        int slotSize = 50;
        int padding = 10;
        int cols = 6;
        int startX = boxX + 20;
        int startY = boxY + 50;

        ArrayList<Map.Entry<String, Integer>> itemList = new ArrayList<>(items.entrySet());

        for (int i = 0; i < itemList.size(); i++) {
            Map.Entry<String, Integer> entry = itemList.get(i);
            if (entry.getValue() > 0) {
                int row = i / cols;
                int col = i % cols;
                int x = startX + col * (slotSize + padding);
                int y = startY + row * (slotSize + padding);

                g.setColor(new Color(180, 180, 180));
                g.drawRect(x, y, slotSize, slotSize);

                Image img = Config.items.get(entry.getKey());
                if (img != null) {
                    g.drawImage(img, x + 5, y + 5, slotSize - 10, slotSize - 10, null);
                }

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString(entry.getValue() + "", x + slotSize - 15, y + slotSize - 5);
            }
        }
    }
}
