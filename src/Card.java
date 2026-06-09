import java.awt.Color;
import java.awt.Graphics2D;

public class Card
{
    static final int WIDTH = 80;
    static final int HEIGHT = 100;

    static final int OFFSET = 15;

    int suitIdx, value;

    Card(int suitIdx, int value)
    {
        this.suitIdx = suitIdx;
        this.value = value;
    }

    void draw(Graphics2D g2d, int x, int y, boolean shown)
    {
        if (shown)
        {
            g2d.setColor(new Color(160, 160, 160));
            g2d.drawImage(Const.CARD_IMG[suitIdx][value-1], x-OFFSET, y, WIDTH+2*OFFSET, HEIGHT, null);
        }
    }
}
