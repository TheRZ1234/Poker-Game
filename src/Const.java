import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Const
{
    static final int WAITING = -1;
    static final int FOLD = -2;
    static final int CHECK = -3;
    static final int CALL = -4;
    static final int RAISE = -5;
    static final int ALL_IN = -6; 

    static final int ROYAL_FLUSH = 1;
    static final int STRAIGHT_FLUSH = 2;
    static final int FOUR_OF_KIND = 3;
    static final int FULL_HOUSE = 4;
    static final int FLUSH = 5;
    static final int STRAIGHT = 6;
    static final int THREE_OF_KIND = 7;
    static final int TWO_PAIR = 8;
    static final int PAIR = 9;
    static final int HIGH_CARD = 10;

    static final int MAIN_MENU = 0;
    static final int GAMEPLAY = 1;
    static final int ENDSCREEN = 2;

    static final String[] SUITS = { "spades", "hearts", "diamonds", "clubs" };
    static final String[] VALUES = { "A", "02", "03", "04", "05", "06", "07", "08", "09", "10", "J", "Q", "K" };

    static BufferedImage[][] CARD_IMG = new BufferedImage[4][13];

    static Font BASE_PIXEL_FONT = new Font("Monospaced", Font.PLAIN, 67);

    static Clip POKER_CHIPS = null;
    static Clip MAIN_MENU_MUSIC = null;
    static Clip GAMEPLAY_MUSIC = null;

    static void initAudio()
    {
        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("assets\\audio\\poker_chips.wav"))) {
            POKER_CHIPS = AudioSystem.getClip();
            POKER_CHIPS.open(audioStream);
            
            FloatControl gainControl = (FloatControl)POKER_CHIPS.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-7.95f);
        }
        catch (Exception e) {
            System.out.println("67bad");
        }

        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("assets\\audio\\main_menu.wav"))) {
            MAIN_MENU_MUSIC = AudioSystem.getClip();
            MAIN_MENU_MUSIC.open(audioStream);
            
            FloatControl gainControl = (FloatControl)MAIN_MENU_MUSIC.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-7.95f);
        }
        catch (Exception e) {
            System.out.println("67bad");
        }

        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("assets\\audio\\gameplay.wav"))) {
            GAMEPLAY_MUSIC = AudioSystem.getClip();
            GAMEPLAY_MUSIC.open(audioStream);
            
            FloatControl gainControl = (FloatControl)GAMEPLAY_MUSIC.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-7.95f);
        }
        catch (Exception e) {
            System.out.println("67bad");
        }
    }

    static void closeAudio()
    {
        POKER_CHIPS.close();
        MAIN_MENU_MUSIC.close();
        GAMEPLAY_MUSIC.close();
    }

    static void initFonts()
    {
        try {
            BASE_PIXEL_FONT = Font.createFont(Font.TRUETYPE_FONT, new File("assets\\GridHunter-gw8D5.ttf"));
        }
        catch (Exception e) {
            System.out.println("67bad");
        }
    }

    static void initCardImages()
    {
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 13; j++)
            {
                String fileName = "card_" + SUITS[i] + "_" + VALUES[j] + ".png";
                try {
                    CARD_IMG[i][j] = ImageIO.read(new File("assets\\cards\\" + fileName));
                }
                catch (Exception e) {
                    System.out.println("67bad");
                }
            }
        }
    }

    static void drawText(Graphics2D g2d, String s, int centerX, int centerY, Font font, Color color)
    {
        g2d.setFont(font);
        g2d.setColor(color);
        
        FontMetrics fm = g2d.getFontMetrics();
        
        int x = centerX - fm.stringWidth(s)/2;
        int y = centerY - fm.getHeight()/2 + fm.getAscent();
        
        g2d.drawString(s, x, y);
    }

    static boolean[] compareHandPairThreeFour(Hand a, Hand b)
    {
        boolean result=false, tie=false;

        int pairValA=-1, pairValB=-1;
        int[] sortedA = new int[5];
        int[] sortedB = new int[5];
        for (int i=2, ja=0, jb=0; i <= 14; i++)
        {
            if (a.cnt[i] >= 2) pairValA = i;
            else if (a.cnt[i] == 1) sortedA[ja++] = i;
            if (b.cnt[i] >= 2) pairValB = i;
            else if (b.cnt[i] == 1) sortedB[jb++] = i;
        }
        if (pairValA != pairValB) result = pairValA > pairValB;
        else
        {
            tie = true;
            for (int i = 4; i >= 0; i--)
            {
                if (sortedA[i] != sortedB[i])
                {
                    result = sortedA[i] > sortedB[i];
                    tie = false;
                    break;
                }
            }
        }

        boolean[] ret = new boolean[2];
        ret[0] = result; ret[1] = tie;
        return ret;
    }

    static boolean[] compareHandTwoPair(Hand a, Hand b)
    {
        boolean result=false, tie=false;

        int pairValA1=-1, pairValA2=-1;
        int pairValB1=-1, pairValB2=-1;
        int maxA=-1, maxB=-1;
        for (int i = 2; i <= 14; i++)
        {
            if (a.cnt[i] == 2)
            {
                if (pairValA2 == -1) pairValA2 = i;
                else pairValA1 = i;
            }
            else if (a.cnt[i] == 1) maxA = i;
            if (b.cnt[i] == 2)
            {
                if (pairValB2 == -1) pairValB2 = i;
                else pairValB1 = i;
            }
            else if (b.cnt[i] == 1) maxB = i;
        }
        if (pairValA1 != pairValB1) result = pairValA1 > pairValB1;
        else if (pairValA2 != pairValB2) result = pairValA2 > pairValB2;
        else if (maxA != maxB) result = maxA > maxB;
        else tie = true;

        boolean[] ret = new boolean[2];
        ret[0] = result; ret[1] = tie;
        return ret;
    }

    static boolean[] compareHandFlushHigh(Hand a, Hand b)
    {
        boolean result=false, tie=true;

        int[] sortedA = new int[5];
        int[] sortedB = new int[5];
        for (int i=2, ja=0, jb=0; i <= 14; i++)
        {
            if (a.cnt[i] >= 1) sortedA[ja++] = i;
            if (b.cnt[i] >= 1) sortedB[jb++] = i;
        }
        for (int i = 4; i >= 0; i--)
        {
            if (sortedA[i] != sortedB[i])
            {
                result = sortedA[i] > sortedB[i];
                tie = false;
                break;
            }
        }

        boolean[] ret = new boolean[2];
        ret[0] = result; ret[1] = tie;
        return ret;
    }

    static boolean[] compareHandFullHouse(Hand a, Hand b)
    {
        boolean result=false, tie=false;

        int threeAVal=-1, pairAVal=-1, threeBVal=-1, pairBVal=-1;
        for (int i = 2; i <= 14; i++)
        {
            if (a.cnt[i] == 3) threeAVal = i;
            else if (a.cnt[i] == 2) pairAVal = i;
            if (b.cnt[i] == 3) threeBVal = i;
            else if (b.cnt[i] == 2) pairBVal = i;
        }
        if (threeAVal != threeBVal) result = threeAVal > threeBVal;
        else if (pairAVal != pairBVal) result = pairAVal > pairBVal;
        else tie = true;

        boolean[] ret = new boolean[2];
        ret[0] = result; ret[1] = tie;
        return ret;
    }

    static int compareHand(Hand a, Hand b)
    {
        if (a.type != b.type) return (a.type < b.type) ? 1 : 0;
        else
        {
            boolean result=false, tie=false;
            switch (a.type)
            {
                case Const.PAIR, Const.THREE_OF_KIND, Const.FOUR_OF_KIND -> 
                {
                    boolean[] ret = compareHandPairThreeFour(a, b);
                    result = ret[0]; tie = ret[1];
                }
                case Const.TWO_PAIR -> 
                {
                    boolean[] ret = compareHandTwoPair(a, b);
                    result = ret[0]; tie = ret[1];
                }
                case Const.STRAIGHT, Const.STRAIGHT_FLUSH, Const.ROYAL_FLUSH -> 
                {
                    result = a.straight > b.straight;
                    tie = a.straight == b.straight;
                }
                case Const.FLUSH, Const.HIGH_CARD ->
                {
                    boolean[] ret = compareHandFlushHigh(a, b);
                    result = ret[0]; tie = ret[1];
                }
                case Const.FULL_HOUSE -> 
                {
                    boolean[] ret = compareHandFullHouse(a, b);
                    result = ret[0]; tie = ret[1];
                }
            }
            return (tie) ? 2 : ((result) ? 1 : 0);
        }
    }
}
