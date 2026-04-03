import java.awt.Graphics;
import java.awt.Image;
import java.util.List;
import java.util.Arrays;

public class Plant {
    public String cropType;
    public int stage = 1;
    public int maxStage = 3; // Typically 3 stages for crops based on config
    public long plantedTime;
    public boolean harvestable = false;
    public long growTime = 3000; // ms
    public List<String> seasons;

    public Plant(String cropType) {
        this.cropType = cropType;
        this.plantedTime = System.currentTimeMillis();
        
        // Define maxStage/growTime based on cropType
        if (cropType.equals("carrot")) {
            growTime = 7 * 60 * 1000; // 7 minutes? In Python it was high
            seasons = Arrays.asList("Spring", "Summer", "Fall");
        } else {
            growTime = 3000; // Default fast growth 3 secs
            if (cropType.equals("potato")) seasons = Arrays.asList("Spring", "Summer", "Winter");
            else if (cropType.equals("radish")) seasons = Arrays.asList("Spring", "Fall");
            else if (cropType.equals("spinach")) seasons = Arrays.asList("Spring", "Fall");
            else if (cropType.equals("turnip")) seasons = Arrays.asList("Spring", "Fall", "Winter");
        }
    }

    public void update(String currentSeason) {
        if (!harvestable) {
            if (seasons != null && !seasons.contains(currentSeason)) {
                return; // Wrong season, don't grow
            }

            long now = System.currentTimeMillis();
            if (now - plantedTime > growTime) {
                stage++;
                plantedTime = now;
                if (stage >= maxStage) {
                    stage = maxStage;
                    harvestable = true;
                }
            }
        }
    }

    public void draw(Graphics g, int x, int y, int size) {
        String key = cropType + "_stage" + stage;
        Image img = Config.plants.get(key);
        if (img != null) {
            g.drawImage(img, x, y, size, size, null);
        }
    }
}
