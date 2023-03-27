import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_HEIGHT * SCREEN_WIDTH) / (UNIT_SIZE * UNIT_SIZE);

    static final int NUM_OF_UNITS_HORIZONTALLY = SCREEN_WIDTH / UNIT_SIZE;

    static final int DELAY = 75;
    int[] x;
    int[] y;

    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean keyPressed = false;
    boolean firstGame = true;
    Timer timer;
    Random random;
    JButton restartButton;
    LinkedList<KeyEvent> keyInputList = new LinkedList<KeyEvent>();


    GamePanel() {
        restartButtonSetup();
        this.add(restartButton);

        random = new Random();
        this.setLayout(null);
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

    }

    private void restartButtonSetup() {
        restartButton = new JButton();
        restartButton.setBounds(SCREEN_WIDTH / 2 - 50, 400, 100, 50);
        restartButton.setText("Start");
        restartButton.setFocusable(false);
        restartButton.addActionListener(this);
        restartButton.setVisible(true);
    }


    public void startGame() {
        x = new int[GAME_UNITS];
        y = new int[GAME_UNITS];
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateFrame(g);
    }

    public void updateFrame(Graphics g) {

        if (running && !firstGame) {
            drawApple(g);
            drawGrid(g);
            drawSnake(g);
            drawScore(g);
        } else {
            drawScore(g);
            gameOver(g);
        }
    }

    public void drawSnake(Graphics g) {
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                g.setColor(Color.green);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            } else {
                g.setColor(new Color((40 + 7 * i) % 255, ((150 - 5 * i) % 255 + 255) % 255, (20 + 11 * i) % 255));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }
    }

    public void drawGrid(Graphics g) {
        g.setColor(Color.darkGray);
        for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
            g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
        }
        for (int i = 0; i < SCREEN_WIDTH / UNIT_SIZE; i++) {
            g.drawLine(0, i * UNIT_SIZE, SCREEN_HEIGHT, i * UNIT_SIZE);
        }
    }

    public void drawApple(Graphics g) {
        g.setColor(Color.red);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
    }

    public void drawScore(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 35));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score " + applesEaten)) / 2,
                50);
    }

    public void newApple() {
        if (bodyParts < GAME_UNITS) {
            int[] gridBodyParts = new int[bodyParts];
            for (int i = bodyParts; i > 0; i--) {
                gridBodyParts[i - 1] = pixels2Grid(x[i], y[i]);
            }
            int rnd = getRandomWithExclusion(random, GAME_UNITS, gridBodyParts);

            Dimension appleCoord = Grid2Pixels(rnd);

            appleX = (int) appleCoord.getWidth();
            appleY = (int) appleCoord.getHeight();
        }
    }

    public int getRandomWithExclusion(Random rnd, int gameSize, int... exclude) {
        Arrays.sort(exclude);
        int random = rnd.nextInt(gameSize - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;

        }

    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            newApple();
            bodyParts++;
            applesEaten++;
        }
    }

    public void checkCollisions() {
        checkCollisionsWithBody();
        checkCollisionsWithWalls();
        if (!running) {
            timer.stop();
        }
    }

    public void checkCollisionsWithBody() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
    }

    public void checkCollisionsWithWalls() {
        if ((x[0] >= SCREEN_WIDTH) || (x[0] < 0)) {
            running = false;
        } else if ((y[0] >= SCREEN_HEIGHT) || (y[0] < 0)) {
            running = false;
        }
    }

    public void gameOver(Graphics g) {

        if (!firstGame) {
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 75));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("GameOver")) / 2,
                    SCREEN_HEIGHT / 2);
            restartButton.setVisible(true);
            restartButton.setText("Restart");
        } else {
            restartButton.setVisible(true);
            restartButton.setText("Start");
        }
    }


    public int pixels2Grid(int xCoord, int yCoord) {
        int gridNumber = (yCoord / UNIT_SIZE) * NUM_OF_UNITS_HORIZONTALLY + xCoord / UNIT_SIZE;
        return gridNumber;
    }

    public Dimension Grid2Pixels(int gridCoord) {
        int xCoord = (gridCoord % NUM_OF_UNITS_HORIZONTALLY) * UNIT_SIZE;
        int yCoord = (gridCoord / NUM_OF_UNITS_HORIZONTALLY) * UNIT_SIZE;


        return new Dimension(xCoord, yCoord);
    }


    /*Well add some methods here*/
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == restartButton) {
            running = true;
            restartButton.setVisible(false);
            restartButton.setText("Start");
            direction = 'R';
            applesEaten = 0;
            bodyParts = 6;
            firstGame = false;
            startGame();
        }
        if (running) {
            keyPressed = false;
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public void changeDirection() {

    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            keyInputList.add(e);
            if () {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') {
                            direction = 'L';
                            keyPressed = true;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') {
                            direction = 'D';
                            keyPressed = true;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') {
                            direction = 'R';
                            keyPressed = true;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D') {
                            direction = 'U';
                            keyPressed = true;
                        }
                        break;

                }
            }
        }

    }
}
