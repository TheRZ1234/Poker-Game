import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;

public class MainMenu 
{
    BufferedImage background;

    Button play;

    MainMenu()
    {
        try {
            background = ImageIO.read(new File("assets\\main_menu_background.png"));
        }
        catch (Exception e) {
            
        }

        play = new Button(Main.WIDTH/2 - 175/2, 280, 175, 75, "Play", 30);
    
        Const.MAIN_MENU_MUSIC.loop(Clip.LOOP_CONTINUOUSLY);
        Const.MAIN_MENU_MUSIC.setFramePosition(0);
        Const.MAIN_MENU_MUSIC.start();
    }

    void stopMusic()
    {
        Const.MAIN_MENU_MUSIC.stop();
    }

    boolean update()
    {
        if (play.update())
        {
            Const.MAIN_MENU_MUSIC.stop();
            return true;
        }
        else return false;
    }

    void draw(Graphics2D g2d)
    {
        g2d.drawImage(background, 0, 0, Main.WIDTH, Main.HEIGHT, null);
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);

        Const.drawText(g2d, "Poker Blitz", Main.WIDTH/2, 150,
            Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 100),
            Color.WHITE);

        Const.drawText(g2d, "Just like normal Poker, but fast!", Main.WIDTH/2, 420,
            Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 30),
            Color.WHITE);

        Const.drawText(g2d, "Every second, everyone's money decreases by a certain amount.", Main.WIDTH/2, 450,
            Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 15),
            Color.WHITE);

        Const.drawText(g2d, "However, don't spend too much time on your turn, as your money will decrease even faster!", Main.WIDTH/2, 480,
            Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 15),
            Color.WHITE);
        
        Const.drawText(g2d, "Outsmart your opponents, manage your time, and be the last player standing!", Main.WIDTH/2, 510,
            Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 15),
            Color.WHITE);
        
        play.draw(g2d);
    }
}
