import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;

public class Table
{
    static final int TABLE_WIDTH = 850;
    static final int TABLE_HEIGHT = 400;
    static final int TABLE_CX = Main.WIDTH/2;
    static final int TABLE_CY = Main.HEIGHT/2 - 20;

    static final int BOARD_SEP = 40;

    Card[] deck = new Card[Const.SUITS.length*Const.VALUES.length];
    Card[] board = new Card[5];

    static final int INIT_AMT = 100000;
    static final int SMALL_BLIND = 250;
    static final int BIG_BLIND = 500;

    int pot, smallBlindIdx, bigBlindIdx, turn, curBet, consecCnt, curRound;
    boolean roundEnd = false, gameEnd = false, playerWon = false;

    int activeNumPlayers;

    ArrayList<Player> players = new ArrayList<>();

    Hand winnerHand;
    ArrayList<Player> winners = new ArrayList<>();

    BufferedImage background;

    Table()
    {
        for (int i = 0; i < Const.SUITS.length; i++)
        {
            for (int j = 0; j < Const.VALUES.length; j++)
                deck[i*Const.VALUES.length+j] = new Card(i, j+1);
        }

        players.add(new User("You", INIT_AMT));
        players.add(new AlwaysCallBot("Alice", INIT_AMT, TABLE_CX + TABLE_WIDTH/2, TABLE_CY - TABLE_HEIGHT/2));
        players.add(new AlwaysCallBot("Daniel", INIT_AMT, TABLE_CX, TABLE_CY - TABLE_HEIGHT/2 - 50));
        players.add(new EulerBot("Bob", INIT_AMT, TABLE_CX - TABLE_WIDTH/2, TABLE_CY - TABLE_HEIGHT/2, 0.2, 0.5, 6.0));
        players.add(new EulerBot("Charlie", INIT_AMT, TABLE_CX - TABLE_WIDTH/2, TABLE_CY + TABLE_HEIGHT/2, 0.2, 0.3, 2.0));
    
        try {
            background = ImageIO.read(new File("assets\\background.jpg"));   
        } catch (Exception e) {
        }
    }

    void startMusic()
    {
        Const.GAMEPLAY_MUSIC.loop(Clip.LOOP_CONTINUOUSLY);
        Const.GAMEPLAY_MUSIC.setFramePosition(0);
        Const.GAMEPLAY_MUSIC.start();
    }

    void setup(int smallBlindIdx)
    {
        Collections.shuffle(Arrays.asList(deck));
        System.arraycopy(deck, 0, board, 0, 5);

        for (int i = 0; i < players.size(); i++)
        {
            Player p = players.get(i);
            p.setPair(deck[5 + 2*i], deck[6 + 2*i]);
            p.reset();
            p.startTimer();
        }

        activeNumPlayers = players.size();

        this.smallBlindIdx = smallBlindIdx; 
        bigBlindIdx = (smallBlindIdx+1)%players.size();
        turn = (bigBlindIdx+1)%players.size(); 

        consecCnt = 1; curRound = 1; roundEnd = false; gameEnd = false;

        Player smallBlind = players.get(smallBlindIdx);
        smallBlind.bet = Math.min(smallBlind.money, SMALL_BLIND);
        smallBlind.money -= smallBlind.bet;
        Player bigBlind = players.get(bigBlindIdx);
        bigBlind.bet = Math.min(bigBlind.money, BIG_BLIND);
        bigBlind.money -= bigBlind.bet;

        curBet = Math.max(smallBlind.bet, bigBlind.bet);
        pot = smallBlind.bet + bigBlind.bet;

        winners.clear();

        startRound();
    }

    void startRound()
    {
        for (Player p : players) p.startRound();
        players.get(turn).startTurn();
        if (players.get(turn).folded || players.get(turn).allIned) players.get(turn).endTurn();
    }

    void endGame()
    {
        for (Player p : players)
        {
            if (p.folded) continue;
            if (winners.isEmpty())
            {
                winnerHand = p.bestHand(board);
                winners.add(p);
                continue;
            }
            Hand cur = p.bestHand(board);
            int result = Const.compareHand(cur, winnerHand);
            if (result == 1)
            {
                winnerHand = cur;
                winners.clear();
            }
            if (result >= 1) winners.add(p);
        }
        for (Player p : winners) p.money += pot/winners.size();
        winners.get(0).money += pot%winners.size();
        pot = 0;

        for (Player p : players)
        {
            p.reset();
            p.showCards = true;
        }
        players.removeIf(item -> item.money == 0);
    }

    void endRound()
    {
        curRound++;
        if (curRound > 4)
        {
            endGame();
            gameEnd = true;
            return;
        }
        roundEnd = false;
        for (Player player : players) player.bet = 0;
        curBet = 0;
        turn = (bigBlindIdx+1)%players.size();
        consecCnt = 0;
        startRound();
    }

    void computeTurn()
    {
        Player p = players.get(turn);
        Move res = p.calculateTurn(pot, curBet, board, curRound, activeNumPlayers);
        p.status = switch(res.type)
        {
            case Const.WAITING -> "Waiting";
            case Const.FOLD -> "Fold";
            case Const.CALL -> "Call";
            case Const.CHECK -> "Check";
            case Const.ALL_IN -> "ALL IN";
            case Const.RAISE -> "Raise";
            default -> "";
        };
        if (res.type == Const.WAITING) return;
        
        if (res.type == Const.FOLD)
        {
            activeNumPlayers -= (p.folded) ? 0 : 1;
            p.folded = true;
        }
        else
        {
            if (res.amt > 0)
            {
                Const.POKER_CHIPS.setFramePosition(0);
                Const.POKER_CHIPS.start();
            }

            pot += res.amt;
            p.money -= res.amt;
            p.bet += res.amt;
            if (p.bet > curBet)
            {
                curBet = p.bet;
                consecCnt = 0;
            }
        }
        
        p.endTurn();
        consecCnt++;

        if (consecCnt == players.size()) roundEnd = true;
        else nextTurn();
    }

    void nextTurn()
    {
        turn = (turn+1)%players.size();
        players.get(turn).startTurn();
        if (players.get(turn).folded || players.get(turn).allIned) players.get(turn).endTurn();
    }

    boolean update()
    {
        if (gameEnd)
        {
            if (Main.enterPressed)
            {
                boolean playerExists = false;
                for (Player p : players) playerExists |= p.name.equals("You");
                if (!playerExists || players.size() == 1)
                {
                    playerWon = playerExists;
                    return true;
                }
                setup((smallBlindIdx+1)%players.size());
            }
            return false;
        }

        if (roundEnd)
        {
            endRound();
            return false;
        }

        computeTurn();

        return false;
    }

    void drawTable(Graphics2D g2d)
    {
        for (int i = 0; i < 5; i++)
        {
            int startX = TABLE_CX - (5*Card.WIDTH + 4*BOARD_SEP)/2;
            int xOffset = i*(Card.WIDTH + BOARD_SEP);
            boolean shown = (curRound == 1) ? false : (i <= curRound);
            board[i].draw(g2d, startX+xOffset, TABLE_CY - 70, shown);
        }

        Const.drawText(g2d, String.format("Pot: %d", pot), 
                        TABLE_CX, TABLE_CY - 110,
                        Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 25),
                        Color.WHITE);
        
        String info;
        if (gameEnd)
        {
            info = "Winner(s): ";
            for (int i = 0; i < winners.size(); i++)
            {
                info += winners.get(i).name;
                if (i < winners.size()-1) info += ", ";
            }
            info += " --> Press Enter to continue";
        }
        else info = String.format("Turn: %s", players.get(turn).name);
        Const.drawText(g2d, info,
                            TABLE_CX, TABLE_CY + 90,
                            Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 25),
                            new Color(216, 187, 93));
        
        Const.drawText(g2d, String.format("Bet: %d", curBet),
                            TABLE_CX, TABLE_CY+120,
                            Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 20),
                            Color.WHITE);
    }

    void drawSBBB(Graphics2D g2d)
    {
        g2d.setColor(Color.BLACK);
        g2d.fillOval(players.get(smallBlindIdx).x - 130, players.get(smallBlindIdx).y - 70,
                    40, 40);

        Const.drawText(g2d, "SB", players.get(smallBlindIdx).x - 110, players.get(smallBlindIdx).y - 50,
                        Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 20),
                        Color.WHITE);
        
        g2d.setColor(Color.BLACK);
        g2d.fillOval(players.get(bigBlindIdx).x - 130, players.get(bigBlindIdx).y - 70,
                    40, 40);

        Const.drawText(g2d, "BB", players.get(bigBlindIdx).x - 110, players.get(bigBlindIdx).y - 50,
                        Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 20),
                        Color.WHITE);
    }

    void draw(Graphics2D g2d)
    {
        g2d.drawImage(background, 0, 0, Main.WIDTH, Main.HEIGHT, null);

        drawTable(g2d);
        
        if (!gameEnd) drawSBBB(g2d);
        
        for (Player player : players) player.draw(g2d);
    }
}
