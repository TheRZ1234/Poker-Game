import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import static java.lang.String.format;

public class User extends Player
{
    boolean canCheck = false, canRaise = false, canCall = false, canFold = false;

    Button allIn = new Button(Main.WIDTH-150, Main.HEIGHT-525, 125, 75, "ALL IN", 20);
    Button fold = new Button(Main.WIDTH-150, Main.HEIGHT-425, 125, 75, "FOLD", 20);
    Button check = new Button(Main.WIDTH-150, Main.HEIGHT-325, 125, 75, "CHECK", 20);
    Button call = new Button(Main.WIDTH-150, Main.HEIGHT-225, 125, 75, "CALL", 20);
    Button raise = new Button(Main.WIDTH-150, Main.HEIGHT-125, 125, 75, "RAISE", 20);
    Slider raiseAmt = new Slider(Main.WIDTH-375, Main.HEIGHT-85, 200);

    User(String name, int money)
    {
        super(name, money, Main.WIDTH/2, Main.HEIGHT-110);
    }

    @Override
    Move calculateTurn(int pot, int curBet, Card[] board, int curRound, int activeNumPlayers)
    {
        if (folded) return new Move(Const.FOLD, 0);
        if (allIned) return new Move(Const.ALL_IN, money);
        if (allIn.update())
        {
            allIned = true;
            return new Move(Const.ALL_IN, money);
        }
        if (activeNumPlayers >= 2)
        {
            canFold = true;
            if (fold.update()) return new Move(Const.FOLD, 0);
        }
        else canFold = false;

        if (bet == curBet)
        {
            canCheck = true;
            if (check.update()) return new Move(Const.CHECK, 0);
        }
        else canCheck = false;

        if (curBet > bet && money > curBet-bet)
        {
            canCall = true;
            if (call.update()) return new Move(Const.CALL, curBet - bet);
        }
        else canCall = false;

        if (money > Math.max(Table.SMALL_BLIND, curBet) + curBet-bet)
        {
            canRaise = true;
            if (raise.update()) return new Move(Const.RAISE, raiseAmt.amt + curBet-bet);
            raiseAmt.update(Math.max(Table.SMALL_BLIND, curBet), 
                            Math.min(pot, money-(curBet - bet)));
        }
        else canRaise = false;

        return new Move(Const.WAITING, 0);
    }

    @Override
    void draw(Graphics2D g2d)
    {
        pair[0].draw(g2d, x - Card.WIDTH - 10, y - Card.HEIGHT/2, true);
        pair[1].draw(g2d, x + 10, y - Card.HEIGHT/2, true);

        Const.drawText(g2d, format("$%d", money),
                        x, y + Card.HEIGHT/2 + 15,
                        Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 15),
                        Color.WHITE);
        
        Const.drawText(g2d, format("Wager: $%d", bet),
                        x, y + Card.HEIGHT/2 + 30,
                        Const.BASE_PIXEL_FONT.deriveFont(Font.PLAIN, 15),
                        Color.WHITE);
        
        if (myTurn)
        {
            allIn.draw(g2d);
            if (canFold) fold.draw(g2d);
            if (canCheck) check.draw(g2d);
            if (canCall) call.draw(g2d);
            if (canRaise)
            {
                raise.draw(g2d);
                raiseAmt.draw(g2d);
            }
        }
    }
}
