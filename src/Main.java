import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.*;
import javax.swing.*;

public class Main extends Canvas implements MouseListener, MouseMotionListener, KeyListener
{
    static final int WIDTH = 1280;
    static final int HEIGHT = 720;
    static final int FRAME_DELAY = 16;

    static Table table = null;
    static MainMenu main_menu = null;
    static EndScreen end_screen = null;

    static int gameState = Const.MAIN_MENU;

    static int mouse_x, mouse_y;
    static boolean mousePressed = false, mouseClicked = false;

    static boolean enterPressed = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Poker Blitz");
        Main game = new Main();
        game.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        frame.setVisible(true);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                Const.closeAudio();
                System.exit(0);
            }
        });

        game.addMouseListener(game);
        game.addMouseMotionListener(game);
        game.addKeyListener(game);
        game.requestFocus();

        game.createBufferStrategy(3);
        BufferStrategy bs = game.getBufferStrategy();


        Const.initFonts();
        Const.initAudio();
        Const.initCardImages();
        table = new Table();

        main_menu = new MainMenu();
        end_screen = new EndScreen();

        while (true)
        {
            update();

            Graphics g = bs.getDrawGraphics();
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            draw(g2d);

            g.dispose();
            bs.show();

            try {
                Thread.sleep(FRAME_DELAY);
            }
            catch (Exception e) {
            }
        }
    }

    public static void update()
    {
        if (gameState == Const.MAIN_MENU)
        {
            if (main_menu.update())
            {
                gameState = Const.GAMEPLAY;
                main_menu.stopMusic();
                table.startMusic();
                table.setup(2);
            }
        }
        else if (gameState == Const.GAMEPLAY)
        {
            if (table.update())
                gameState = Const.ENDSCREEN;
        }
        else end_screen.update(table.playerWon);
        mouseClicked = false;
    }

    public static void draw(Graphics2D g2d)
    {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        if (gameState == Const.MAIN_MENU) main_menu.draw(g2d);
        else if (gameState == Const.GAMEPLAY) table.draw(g2d);
        else end_screen.draw(g2d);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1)
            mouseClicked = true;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1)
            mousePressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1)
            mousePressed = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            enterPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            enterPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}