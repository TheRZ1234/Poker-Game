import java.util.Arrays;
import java.util.Collections;

public class EulerBot extends Bot
{
    static final int NUM_RANDOM_TESTS = 1000;

    double callCut, raiseCut, a, b;
    boolean raised = false;

    EulerBot(String name, int money, int x, int y, double callCut, double raiseCut, double maxMul)
    {
        super(name, money, x, y);

        this.callCut = callCut;
        this.raiseCut = raiseCut;
        b = Math.log(maxMul) / (1.0 - raiseCut);
        a = 1.0 / Math.pow(Math.E, b * raiseCut);
    }

    double f(double x)
    {
        return a * Math.pow(Math.E, b*x);
    }

    Move callMove(int curBet)
    {
        if (bet == curBet) return new Move(Const.CHECK, 0);
        else
        {
            if (money < curBet-bet) return new Move(Const.FOLD, 0);
            else
            {
                if (money == curBet-bet) allIned = true;
                return new Move((money > curBet-bet) ? Const.CALL : Const.ALL_IN, curBet-bet);
            }
        }
    }

    @Override
    void startRound()
    {
        raised = false;
    }

    int calculateNumWins(Card[] rem, int activeNumPlayers)
    {
        int numWins = 0;
        for (int t = 0; t < NUM_RANDOM_TESTS; t++)
        {
            Collections.shuffle(Arrays.asList(rem));
            Card[] testBoard = new Card[5];
            System.arraycopy(rem, 0, testBoard, 0, 5);

            Hand myHand = bestHand(testBoard);
            boolean win = true;
            for (int i = 0; i < activeNumPlayers-1; i++)
            {
                Player p = new Player("", 0, 0, 0);
                p.setPair(rem[5+2*i], rem[6+2*i]);
                if (Const.compareHand(myHand, p.bestHand(testBoard)) == 0)
                {
                    win = false;
                    break;
                }
            }
            numWins += (win) ? 1 : 0;
        }
        
        return numWins;
    }

    @Override
    Move calculateTurn(int pot, int curBet, Card[] board, int curRound, int activeNumPlayers)
    {
        if (folded) return new Move(Const.FOLD, 0);
        if (allIned) return new Move(Const.ALL_IN, money);
        
        if (System.currentTimeMillis()-startWaitTime <= WAIT_TIME)
            return new Move(Const.WAITING, 0);

        if (activeNumPlayers == 1) return new Move(Const.CHECK, 0);
        
        int index = (curRound == 1) ? -1 : curRound;
        Card[] rem = new Card[deck.length-(index+1)-2];
        for (int i=0, j=0; i < deck.length; i++)
        {
            boolean shown = false;
            for (int k = 0; k < 2; k++)
                shown |= deck[i].suitIdx == pair[k].suitIdx && deck[i].value == pair[k].value;
            for (int k = 0; k <= index; k++)
                shown |= deck[i].suitIdx == board[k].suitIdx && deck[i].value == board[k].value;

            if (!shown) rem[j++] = deck[i];
        }

        double chance = (double)calculateNumWins(rem, activeNumPlayers) / (double)NUM_RANDOM_TESTS;

        if (chance < callCut)
        {
            if (bet == curBet) return new Move(Const.CHECK, 0);
            else return new Move(Const.FOLD, 0);
        }
        else if (chance < raiseCut) return callMove(curBet);
        else
        {
            int amt = Math.max(Table.SMALL_BLIND, (int)(curBet * (1.0 + f(chance)))) - bet;
            if (amt <= money && !raised)
            {
                raised = true;
                if (amt == money) allIned = true;
                return new Move((amt < money) ? Const.RAISE : Const.ALL_IN, amt);
            }
            else return callMove(curBet);
        }
    }
}
