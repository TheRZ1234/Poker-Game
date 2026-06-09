import java.awt.Graphics2D;
import javax.swing.Timer;

public class Player
{
    static final int DEC_RATE = 100;
    static final int TURN_DEC_RATE = 500;

    String name;

    int x, y;
    static final int RADIUS = 90;

    Card[] pair = new Card[2];
    int money, bet;

    boolean folded = false, allIned = false, myTurn = false, showCards = false;
    String status = "";

    Timer decTimer;

    Player(String name, int money, int x, int y)
    {
        this.name = name;
        this.money = money;
        this.x = x; this.y = y;

        decTimer = new Timer(500, e -> {
            this.money = Math.max(0, this.money - (myTurn ? TURN_DEC_RATE : DEC_RATE));
        });
    }

    void reset()
    {
        bet = 0;
        folded = false;
        allIned = false;
        status = "";
        showCards = false;
        decTimer.stop();
    }

    void startTimer()
    {
        decTimer.start();
    }

    Hand bestHand(Card[] board)
    {
        Card[] all = new Card[7];
        System.arraycopy(board, 0, all, 0, 5);
        all[5] = pair[0]; all[6] = pair[1];

        Hand best = new Hand(board);
        int setMask = (1<<5)-1;
        while (setMask < (1<<7))
        {
            Card[] cards = new Card[5];
            for (int i=0, j=0; i < 7; i++)
            {
                if ((1&setMask>>i) == 1)
                    cards[j++] = all[i];
            }
            Hand hand = new Hand(cards);
            if (Const.compareHand(hand, best) == 1) best = hand;

            int c = setMask&-setMask;
            int r = setMask+c;
            setMask = (((r ^ setMask) >> 2) / c) | r;
        }

        return best;
    }

    void setPair(Card c1, Card c2)
    {
        pair[0] = c1;
        pair[1] = c2;
    }

    void startTurn()
    {
        myTurn = true;
    }

    void endTurn()
    {
        myTurn = false;
    }

    void startRound()
    {

    }

    Move calculateTurn(int pot, int curBet, Card[] board, int curRound, int activeNumPlayers)
    {
        return new Move(Const.FOLD, 0);
    }

    void draw(Graphics2D g2d)
    {
        
    }
}
