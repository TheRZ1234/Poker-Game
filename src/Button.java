
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Button
{
    int x, y, width, height, fontSize;
    String label;
    boolean hold = false, hovering = false;

    BufferedImage normal, clicked, hover;

    Button(int x, int y, int width, int height, String label, int fontSize)
    {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
        this.label = label;
        try {
            normal = ImageIO.read(new File("assets\\gui\\[1] Normal.png"));
            clicked = ImageIO.read(new File("assets\\gui\\[2] Clicked.png"));
            hover = ImageIO.read(new File("assets\\gui\\[3] Hover.png"));
        } catch (Exception e) {
        }
        this.fontSize = fontSize;
    }

    boolean update()
    {
        boolean inside = x <= Main.mouse_x && Main.mouse_x <= x+width &&
                         y <= Main.mouse_y && Main.mouse_y <= y+height;
        hold = inside && Main.mousePressed;
        hovering = inside;

        return inside && Main.mouseClicked;
    }

    void draw(Graphics2D g2d)
    {
        g2d.setColor(Color.LIGHT_GRAY);

        if (hold) g2d.drawImage(clicked, x, y, width, height, null);
        else if (hovering) g2d.drawImage(hover, x, y, width, height, null);
        else g2d.drawImage(normal, x, y, width, height, null);

        Const.drawText(g2d, label, x+width/2, y+height/2-5,
                        Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, fontSize),
                        Color.BLACK);
    }
}