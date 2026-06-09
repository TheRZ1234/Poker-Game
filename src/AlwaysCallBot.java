public class AlwaysCallBot extends Bot
{
    AlwaysCallBot(String name, int money, int x, int y)
    {
        super(name, money, x, y);
    }

    @Override
    Move calculateTurn(int pot, int curBet, Card[] board, int curRound, int activeNumPlayers)
    {
        if (folded) return new Move(Const.FOLD, 0);
        if (allIned) return new Move(Const.ALL_IN, money);

        if (System.currentTimeMillis()-startWaitTime <= WAIT_TIME)
            return new Move(Const.WAITING, 0);

        if (bet == curBet) return new Move(Const.CHECK, 0);
        else if (money > curBet-bet) return new Move(Const.CALL, curBet-bet);
        else
        {
            allIned = true;
            return new Move(Const.ALL_IN, money);
        }
    }
}
