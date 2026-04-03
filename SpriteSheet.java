import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Color;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;

public class SpriteSheet {
    private BufferedImage sheet;

    public SpriteSheet(BufferedImage sheet) {
        this.sheet = sheet;
    }

    public Image getImage(int frameIndex, int frameWidth, int frameHeight, int scale, Color colorKey) {
        int x = frameIndex * frameWidth;
        int y = 0;
        BufferedImage subImg = sheet.getSubimage(x, y, frameWidth, frameHeight);

        // Make colorKey transparent if needed (black in the original python logic)
        Image transparentImg = makeColorTransparent(subImg, colorKey);
        
        // Scale the image
        return transparentImg.getScaledInstance(frameWidth * scale, frameHeight * scale, Image.SCALE_SMOOTH);
    }

    public static Image makeColorTransparent(BufferedImage im, Color color) {
        if (color == null) {
            return im;
        }
        ImageFilter filter = new RGBImageFilter() {
            public int markerRGB = color.getRGB() | 0xFF000000;

            public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    return 0x00FFFFFF & rgb;
                } else {
                    return rgb;
                }
            }
        };
        return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(im.getSource(), filter));
    }
}
