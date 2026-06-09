
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Bot extends Player
{
    static final int WAIT_TIME = 1000;
    long startWaitTime = 0;

    Card[] deck = new Card[Const.SUITS.length*Const.VALUES.length];

    Bot(String name, int money, int x, int y)
    {
        super(name, money, x, y);
        for (int i = 0; i < Const.SUITS.length; i++)
        {
            for (int j = 0; j < Const.VALUES.length; j++)
                deck[i*Const.VALUES.length+j] = new Card(i, j+1);
        }
    }

    @Override
    void startTurn()
    {
        myTurn = true;
        startWaitTime = System.currentTimeMillis();
    }

    @Override
    Move calculateTurn(int pot, int curBet, Card[] board, int curRound, int activeNumPlayers)
    {
        if (folded) return new Move(Const.FOLD, 0);
        if (System.currentTimeMillis()-startWaitTime <= WAIT_TIME)
            return new Move(Const.WAITING, 0);
        else return new Move(Const.CALL, curBet-bet);
    }

    @Override
    void draw(Graphics2D g2d)
    {
        g2d.setColor((myTurn) ? new Color(255, 125, 0) : new Color(100, 100, 100));
        g2d.fillOval(x - RADIUS/2, y - RADIUS/2, RADIUS, RADIUS);

        Const.drawText(g2d, name, x, y-10, 
                        Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 20),
                        Color.WHITE);
        
        Const.drawText(g2d, String.format("$%d", money), x, y+15,
                        Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 15),
                        Color.WHITE);
        
        if (!status.equals(""))
        {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(x - (RADIUS-10)/2, y + 47, RADIUS-10, 25);
        }
        
        Const.drawText(g2d, status, x, y+60,
                        Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 20),
                        Color.WHITE);
        
        if (showCards)
        {
            pair[0].draw(g2d, x - Card.WIDTH - 10, y - Card.HEIGHT/2 + 100, true);
            pair[1].draw(g2d, x + 10, y - Card.HEIGHT/2 + 100, true);
        }
    }
}
