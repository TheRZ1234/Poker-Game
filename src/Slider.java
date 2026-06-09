import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Slider
{
    final int HEIGHT = 7, RADIUS = 15;
    int x, y, width, cx, amt;

    public Slider(int x, int y, int width)
    {
        this.x = x; this.y = y;
        this.cx = x;
        this.width = width;
    }

    void update(int mn, int mx)
    {
        if (x - RADIUS <= Main.mouse_x && Main.mouse_x <= x + width + RADIUS && 
            y - RADIUS <= Main.mouse_y && Main.mouse_y <= y + RADIUS &&
            Main.mousePressed
        )
        {
            cx = Math.max(x, Math.min(Main.mouse_x, x+width));
        }
        amt = mn + (mx-mn)*(cx-x)/width;
    }

    void draw(Graphics2D g2d)
    {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x, y, width, HEIGHT);
        g2d.setColor(Color.GRAY);
        g2d.fillOval(cx-RADIUS, y+HEIGHT/2-RADIUS, 2*RADIUS, 2*RADIUS);


        Const.drawText(g2d, String.format("$%d", amt),
                        x+width/2, y+HEIGHT+20,
                        Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 15),
                        Color.WHITE);
    }
}
