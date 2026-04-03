import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class Config {
    // Window settings
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final int WINDOW_X = 800;
    public static final int WINDOW_Y = 600;
    public static final int FPS = 60;
    
    // Grid settings
    public static final int TILE_SIZE = 64; // Equivalent to bun_size
    public static final int BUN_EXACT = 32;
    public static final int GRID_SIZE = 50; 

    // Colors
    public static final Color COLOR_BLACK = new Color(0, 0, 0);
    public static final Color COLOR_WHITE = new Color(255, 255, 255);
    public static final Color COLOR_GRAY = new Color(70, 70, 70);
    public static final Color COLOR_RED = new Color(255, 0, 0);
    public static final Color COLOR_GREEN = new Color(0, 255, 0);
    public static final Color COLOR_PEACH = new Color(255, 200, 200);
    public static final Color COLOR_SKY = new Color(200, 255, 255);
    public static final Color COLOR_PURPLE = new Color(200, 0, 210);
    public static final Color COLOR_DARK_PURPLE = new Color(52, 0, 112);
    public static final Color COLOR_BROWN = new Color(60, 5, 25);
    public static final Color COLOR_DIRT = new Color(139, 69, 19);
    public static final Color COLOR_BUNNY = Color.WHITE;
    public static final Color COLOR_YELLOW = new Color(200, 200, 0);

    // Dictionaries for loaded images
    public static Map<String, Image> environ = new HashMap<>();
    public static Map<String, Image> items = new HashMap<>();
    public static Map<String, Image> plants = new HashMap<>(); // To store stages
    public static Map<String, Image> ui = new HashMap<>();
    
    public static Map<String, SpriteSheet> bunSheets = new HashMap<>();

    public static final String FONT_PATH = "assets/fonts/pixel.ttf";

    public static void loadImages() {
        System.out.println("Loading images...");
        try {
            environ.put("dirt", loadScaledAndTransparent("assets/picture/grass1.png", TILE_SIZE, TILE_SIZE));
            environ.put("soil_overlay", loadScaledAndTransparent("assets/picture/soil_overlay.png", TILE_SIZE, TILE_SIZE));
            environ.put("tree", loadScaledAndTransparent("assets/picture/tree.png", TILE_SIZE, TILE_SIZE));
            environ.put("stone", loadScaledAndTransparent("assets/picture/stone.png", TILE_SIZE, TILE_SIZE));
            environ.put("house", loadScaledAndTransparent("assets/bgimages/home.png", TILE_SIZE*10, TILE_SIZE*8));
            environ.put("mailbox", loadScaledAndTransparent("assets/picture/mailbox.png", TILE_SIZE, TILE_SIZE));
            environ.put("noti", loadScaledAndTransparent("assets/picture/noti.png", TILE_SIZE/2, TILE_SIZE/2));
            environ.put("wall", loadScaledAndTransparent("assets/picture/wall.png", TILE_SIZE, TILE_SIZE));
            
            // Dungeon / Maze
            environ.put("bush_dun", loadScaledAndTransparent("assets/picture/bush_dun1.png", TILE_SIZE, TILE_SIZE));
            environ.put("dirt_dun", loadScaledAndTransparent("assets/picture/dirt_dun.png", TILE_SIZE, TILE_SIZE));
            
            // Backgrounds
            ui.put("login_bg", loadImageRaw("assets/bgimages/login_bg.png"));

            // Items
            items.put("wood", loadScaledAndTransparent("assets/items/wood.png", TILE_SIZE, TILE_SIZE));
            items.put("stone", loadScaledAndTransparent("assets/items/stone.png", TILE_SIZE, TILE_SIZE));
            items.put("carrot", loadScaledAndTransparent("assets/items/carrot.png", TILE_SIZE, TILE_SIZE));
            items.put("potato", loadScaledAndTransparent("assets/items/potato.png", TILE_SIZE, TILE_SIZE));
            items.put("radish", loadScaledAndTransparent("assets/items/radish.png", TILE_SIZE, TILE_SIZE));
            items.put("spinach", loadScaledAndTransparent("assets/items/spinach.png", TILE_SIZE, TILE_SIZE));
            items.put("turnip", loadScaledAndTransparent("assets/items/turnip.png", TILE_SIZE, TILE_SIZE));
            items.put("pickaxe", loadScaledAndTransparent("assets/items/pickaxe.png", TILE_SIZE, TILE_SIZE));
            items.put("carrot_weapon", loadScaledAndTransparent("assets/items/carrot_weapon.png", TILE_SIZE, TILE_SIZE));
            items.put("boss_key", loadScaledAndTransparent("assets/items/boss_key.png", TILE_SIZE, TILE_SIZE));
            items.put("diamond", loadScaledAndTransparent("assets/items/diamond.png", TILE_SIZE, TILE_SIZE));

            // Seeds
            items.put("carrot_seed", loadScaledAndTransparent("assets/plants/carrot_seed.png", TILE_SIZE, TILE_SIZE));
            items.put("potato_seed", loadScaledAndTransparent("assets/plants/potato_seed.png", TILE_SIZE, TILE_SIZE));
            items.put("radish_seed", loadScaledAndTransparent("assets/plants/radish_seed.png", TILE_SIZE, TILE_SIZE));
            items.put("spinach_seed", loadScaledAndTransparent("assets/plants/spinach_seed.png", TILE_SIZE, TILE_SIZE));
            items.put("turnip_seed", loadScaledAndTransparent("assets/plants/turnip_seed.png", TILE_SIZE, TILE_SIZE));

            // Sprite sheets
            bunSheets.put("front_sheet", new SpriteSheet(loadImageRaw("assets/picture/BunnyWalk-Sheet.png")));
            bunSheets.put("back_sheet", new SpriteSheet(loadImageRaw("assets/picture/BunnyWalkBack-Sheet.png")));
            bunSheets.put("right_sheet", new SpriteSheet(loadImageRaw("assets/picture/BunnyWalkright-Sheet.png")));
            bunSheets.put("left_sheet", new SpriteSheet(loadImageRaw("assets/picture/BunnyWalkleft-Sheet.png")));
            bunSheets.put("front_damage_sheet", new SpriteSheet(loadImageRaw("assets/picture/bunny_front_damage.png")));
            bunSheets.put("back_damage_sheet", new SpriteSheet(loadImageRaw("assets/picture/bunny_back_damage.png")));
            bunSheets.put("left_damage_sheet", new SpriteSheet(loadImageRaw("assets/picture/bunny_left_damage.png")));
            bunSheets.put("right_damage_sheet", new SpriteSheet(loadImageRaw("assets/picture/bunny_right_damage.png")));

            // Plant stages
            String[] crops = {"carrot", "potato", "radish", "spinach", "turnip"};
            for (String crop : crops) {
                for (int i = 1; i <= 3; i++) {
                    String path = "assets/plants/" + crop + "_stage" + i + ".png";
                    Image img = loadScaledAndTransparent(path, TILE_SIZE, TILE_SIZE);
                    if (img != null) {
                        plants.put(crop + "_stage" + i, img);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static BufferedImage loadImageRaw(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("File not found: " + path);
                // Return dummy surface instead
                return new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            }
            return ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            return new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        }
    }

    private static Image loadScaledAndTransparent(String path, int width, int height) {
        BufferedImage raw = loadImageRaw(path);
        // We will just scale it directly. If user really needed color replacement, we can add it later.
        return raw.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}