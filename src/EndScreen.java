import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class EndScreen {

    BufferedImage background;

    boolean playerWon = false;

    EndScreen()
    {
        try {
            background = ImageIO.read(new File("assets\\end_screen_background.png"));
        }
        catch (Exception e) {
            
        }
    }

    void update(boolean playerWon)
    {
        this.playerWon = playerWon;
    }

    void draw(Graphics2D g2d)
    {
        g2d.drawImage(background, 0, 0, Main.WIDTH, Main.HEIGHT, null);
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);

        Const.drawText(g2d, (playerWon) ? "You won :D" : "You went bankrupt :(", Main.WIDTH/2, Main.HEIGHT/2,
            Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 80),
            Color.WHITE);
    }
}
