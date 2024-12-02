// Import necessary classes from Swing, AWT, and event libraries
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

// GamePanel class handles the main game mechanics and display
public class GamePanel extends JPanel implements ActionListener {
    // Constants for screen dimensions and game settings
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 5;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75;

    // Arrays to hold x and y coordinates for the plant's parts
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];

    // Initial plant parts count and score
    int plantParts = 40;
    int waterTaken;

    // Coordinates for the water's position
    int waterX;
    int waterY;

    // Initial direction of the snake (Right)
    char direction = 'R';

    // Game status
    boolean running = false;

    // Timer for game updates and random generator for water position
    Timer timer;
    Random random;

    // Constructor for GamePanel
    GamePanel() {
        // Initialize random object
        random = new Random();

        // Set panel properties
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);

        // Add a KeyListener for controlling the snake
        this.addKeyListener(new MyKeyAdapter());

        // Start the game
        startGame();
    }

    // Starts the game by creating a new water and setting game state
    public void startGame() {
        newWater();
        running = true;

        // Start the timer with a delay
        timer = new Timer(DELAY, this);
        timer.start();
    }

    // Override paintComponent to draw elements on the panel
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // Draws game elements: water, plant, and score
    //graphics for body with curves like vineyard
    public void draw(Graphics g) {
        if (running) {
            // Draw water drop with a larger, oval shape for a more droplet-like appearance
            int dropletWidth = UNIT_SIZE * 2;  // Make the droplet wider
            int dropletHeight = UNIT_SIZE * 3; // Make the droplet taller for a teardrop effect
            g.setColor(Color.blue);
            g.fillOval(waterX - dropletWidth / 4, waterY - dropletHeight / 4, dropletWidth, dropletHeight);

            // Draw the plant's head based on waterTaken value
            g.setColor(Color.green);
            g.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE);

            if (waterTaken > 2) {
                // Draw full flower with petals
                int flowerSize = UNIT_SIZE * 3; // Flower size
                g.setColor(new Color(255, 105, 180)); // Color for the flower (pink)

                // Draw petals around the flower center
                for (int i = 0; i < 8; i++) { // Create 8 petals
                    double angle = Math.PI / 4 * i; // Angle for each petal
                    int petalX = (int) (x[0] - flowerSize / 2 + flowerSize / 1.5 * Math.cos(angle));
                    int petalY = (int) (y[0] - flowerSize / 2 + flowerSize / 1.5 * Math.sin(angle));
                    g.fillOval(petalX, petalY, flowerSize / 2, flowerSize / 2); // Petal size
                }
                // Draw the center of the flower
                g.setColor(Color.yellow);
                g.fillOval(x[0] - flowerSize / 6, y[0] - flowerSize / 6, flowerSize / 3, flowerSize / 3); // Center of the flower
            } else if (waterTaken > 1) {
                // Draw bud with green petals
                int budSize = UNIT_SIZE * 2; // Bud size
                g.setColor(new Color(0, 128, 0)); // Petal color (green)

                // Draw petals around the bud center
                for (int i = 0; i < 5; i++) { // Create 5 petals
                    double angle = Math.PI / 2.5 * i; // Angle for each petal
                    int petalX = (int) (x[0] - budSize / 2 + budSize / 1.5 * Math.cos(angle));
                    int petalY = (int) (y[0] - budSize / 2 + budSize / 1.5 * Math.sin(angle));
                    g.fillOval(petalX, petalY, budSize / 2, budSize / 2); // Bud petals
                }
                // Draw the center of the bud
                g.setColor(Color.green); // Color for the bud itself
                g.fillOval(x[0] - budSize / 4, y[0] - budSize / 4, budSize / 2, budSize / 2); // Bud center
            } else {
                // Draw seed
                int seedSize = UNIT_SIZE*2; // Seed size
                g.setColor(new Color(139, 69, 19)); // Seed color (brown)
                g.fillOval(x[0] - seedSize / 2, y[0] - seedSize / 2, seedSize, seedSize); // Draw seed
            }

            // Draw the plant's body with a wiggling effect and leaves
            for (int i = 1; i < plantParts; i++) {
                // Create a sine wave offset for the body parts
                double angle = i * 0.5;  // Adjust the frequency of the wiggle here
                int offsetX = (int) (Math.sin(angle) * UNIT_SIZE / 2); // Adjust amplitude if needed
                int offsetY = (int) (Math.cos(angle) * UNIT_SIZE / 2);

                // Draw each segment with the oscillating offset
                g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i] + offsetX, y[i] + offsetY, UNIT_SIZE, UNIT_SIZE);

                // Draw leaves on alternating segments
                if (i % 2 == 0) {
                    g.setColor(Color.green);
                    // Draw leaf shapes (more elongated and leaf-like)
                    g.fillOval(x[i] + UNIT_SIZE / 2 + offsetX, y[i] + offsetY, UNIT_SIZE / 3, UNIT_SIZE / 5); // Left leaf
                    g.fillOval(x[i] + offsetX, y[i] + UNIT_SIZE / 2 + offsetY, UNIT_SIZE / 3, UNIT_SIZE / 5); // Right leaf
                }
            }

            // Display score
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + waterTaken, (SCREEN_WIDTH - metrics.stringWidth("Score: " + waterTaken)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    /* //flower is nice and funny idc anymore
    public void draw(Graphics g) {
        if (running) {
            // Draw water drop with a larger, oval shape for a more droplet-like appearance
            int dropletWidth = UNIT_SIZE * 2;  // Make the droplet wider
            int dropletHeight = UNIT_SIZE * 3; // Make the droplet taller for a teardrop effect
            g.setColor(Color.blue);
            g.fillOval(waterX - dropletWidth / 4, waterY - dropletHeight / 4, dropletWidth, dropletHeight);

            // Draw the plant's head with a flower/bud if waterTaken > 10
            g.setColor(Color.green);
            g.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE);
            if (waterTaken > 2) {
                int flowerSize = UNIT_SIZE * 3; // Flower size
                g.setColor(new Color(255, 105, 180)); // Color for the flower (pink)

                // Draw petals around the flower center
                for (int i = 0; i < 8; i++) { // Create 8 petals
                    double angle = Math.PI / 4 * i; // Angle for each petal
                    int petalX = (int) (x[0] - flowerSize / 2 + flowerSize / 1.5 * Math.cos(angle));
                    int petalY = (int) (y[0] - flowerSize / 2 + flowerSize / 1.5 * Math.sin(angle));
                    g.fillOval(petalX, petalY, flowerSize / 2, flowerSize / 2); // Petal size
                }
                // Draw the center of the flower
                g.setColor(Color.yellow);
                g.fillOval(x[0] - flowerSize / 6, y[0] - flowerSize / 6, flowerSize / 3, flowerSize / 3); // Center of the flower
            }

            // Draw the plant's body with a wiggling effect and leaves
            for (int i = 1; i < plantParts; i++) {
                // Create a sine wave offset for the body parts
                double angle = i * 0.5;  // Adjust the frequency of the wiggle here
                int offsetX = (int) (Math.sin(angle) * UNIT_SIZE / 2); // Adjust amplitude if needed
                int offsetY = (int) (Math.cos(angle) * UNIT_SIZE / 2);

                // Draw each segment with the oscillating offset
                g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i] + offsetX, y[i] + offsetY, UNIT_SIZE, UNIT_SIZE);

                // Draw leaves on alternating segments
                if (i % 2 == 0) {
                    g.setColor(Color.green);
                    // Draw leaf shapes (more elongated and leaf-like)
                    g.fillOval(x[i] + UNIT_SIZE / 2 + offsetX, y[i] + offsetY, UNIT_SIZE / 3, UNIT_SIZE / 5); // Left leaf
                    g.fillOval(x[i] + offsetX, y[i] + UNIT_SIZE / 2 + offsetY, UNIT_SIZE / 3, UNIT_SIZE / 5); // Right leaf
                }
            }

            // Display score
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + waterTaken, (SCREEN_WIDTH - metrics.stringWidth("Score: " + waterTaken)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }
*/
  /*  //deformed flower its so funny =it looks like a round dotted flower
    public void draw(Graphics g) {
        if (running) {
            // Draw water drop with a larger, oval shape for a more droplet-like appearance
            int dropletWidth = UNIT_SIZE * 2;  // Make the droplet wider
            int dropletHeight = UNIT_SIZE * 3; // Make the droplet taller for a teardrop effect
            g.setColor(Color.blue);
            g.fillOval(waterX - dropletWidth / 4, waterY - dropletHeight / 4, dropletWidth, dropletHeight);

            // Draw the plant's head with a flower/bud if waterTaken > 10
            g.setColor(Color.green);
            g.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE);
            if (waterTaken > 2) {
                int flowerSize = UNIT_SIZE * 3; // Flower size
                g.setColor(new Color(255, 105, 180)); // Color for the flower (pink)

                // Draw petals around the flower center
                for (int i = 0; i < 8; i++) { // Create 8 petals
                    double angle = Math.PI / 4 * i; // Angle for each petal
                    int petalX = (int) (x[0] - flowerSize / 2 + flowerSize / 1.5 * Math.cos(angle));
                    int petalY = (int) (y[0] - flowerSize / 2 + flowerSize / 1.5 * Math.sin(angle));
                    g.fillOval(petalX, petalY, flowerSize / 2, flowerSize / 2); // Petal size
                }
                // Draw the center of the flower
                g.setColor(Color.yellow);
                g.fillOval(x[0] - flowerSize / 6, y[0] - flowerSize / 6, flowerSize / 3, flowerSize / 3); // Center of the flower
            }

            // Draw the plant's body with a wiggling effect and leaves
            for (int i = 1; i < plantParts; i++) {
                // Create a sine wave offset for the body parts
                double angle = i * 0.5;  // Adjust the frequency of the wiggle here
                int offsetX = (int) (Math.sin(angle) * UNIT_SIZE / 2); // Adjust amplitude if needed
                int offsetY = (int) (Math.cos(angle) * UNIT_SIZE / 2);

                // Draw each segment with the oscillating offset
                g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i] + offsetX, y[i] + offsetY, UNIT_SIZE, UNIT_SIZE);

                // Draw leaves on alternating segments
                if (i % 2 == 0) {
                    g.setColor(Color.green);
                    // Draw leaf shapes (more elongated and leaf-like)
                    g.fillOval(x[i] + UNIT_SIZE / 2 + offsetX, y[i] + offsetY, UNIT_SIZE / 3, UNIT_SIZE / 5); // Left leaf
                    g.fillOval(x[i] + offsetX, y[i] + UNIT_SIZE / 2 + offsetY, UNIT_SIZE / 3, UNIT_SIZE / 5); // Right leaf
                }
            }

            // Display score
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + waterTaken, (SCREEN_WIDTH - metrics.stringWidth("Score: " + waterTaken)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }
*/
    /*
    //flower looks like a round helmet attached to its head
    public void draw(Graphics g) {
        if (running) {
            // Draw water drop with a larger, oval shape for a more droplet-like appearance
            int dropletWidth = UNIT_SIZE * 2;  // Make the droplet wider
            int dropletHeight = UNIT_SIZE * 3; // Make the droplet taller for a teardrop effect
            g.setColor(Color.blue);
            g.fillOval(waterX - dropletWidth / 4, waterY - dropletHeight / 4, dropletWidth, dropletHeight);

            // Draw the plant's head with a flower/bud if waterTaken > 10
            g.setColor(Color.green);
            g.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE);
            if (waterTaken > 2) {
                int flowerSize = UNIT_SIZE * 3; // Make the flower larger than the body segments
                g.setColor(new Color(255, 105, 180)); // Color for the flower (pink)
                g.fillOval(x[0] - flowerSize / 4, y[0] - flowerSize / 4, flowerSize, flowerSize);
            }

            // Draw the plant's body with a wiggling effect and leaves
            for (int i = 1; i < plantParts; i++) {
                // Create a sine wave offset for the body parts
                double angle = i * 0.5;  // Adjust the frequency of the wiggle here
                int offsetX = (int) (Math.sin(angle) * UNIT_SIZE / 2); // Adjust amplitude if needed
                int offsetY = (int) (Math.cos(angle) * UNIT_SIZE / 2);

                // Draw each segment with the oscillating offset
                g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i] + offsetX, y[i] + offsetY, UNIT_SIZE, UNIT_SIZE);

                // Draw leaves on alternating segments
                if (i % 2 == 0) {
                    g.setColor(Color.green);
                    g.fillOval(x[i] + UNIT_SIZE / 2 + offsetX, y[i] + offsetY, UNIT_SIZE / 2, UNIT_SIZE / 4); // Left leaf
                    g.fillOval(x[i] + offsetX, y[i] + UNIT_SIZE / 2 + offsetY, UNIT_SIZE / 2, UNIT_SIZE / 4); // Right leaf
                }
            }

            // Display score
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + waterTaken, (SCREEN_WIDTH - metrics.stringWidth("Score: " + waterTaken)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }
*/

/*
//small water dot
    public void draw(Graphics g) {
        if (running) {
            // Draw water drop
            g.setColor(Color.blue);
            g.fillOval(waterX, waterY, UNIT_SIZE, UNIT_SIZE);

            // Draw the plant's head with a flower/bud if waterTaken > 10
            g.setColor(Color.green);
            g.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE);
            if (waterTaken > 2) {
                int flowerSize = UNIT_SIZE * 3; // Make the flower larger than the body segments
                g.setColor(new Color(255, 105, 180)); // Color for the flower (pink)
                g.fillOval(x[0] - flowerSize / 4, y[0] - flowerSize / 4, flowerSize, flowerSize);

            }


            // Draw the plant's body with a wiggling effect and leaves
            for (int i = 1; i < plantParts; i++) {
                // Create a sine wave offset for the body parts
                double angle = i * 0.5;  // Adjust the frequency of the wiggle here
                int offsetX = (int) (Math.sin(angle) * UNIT_SIZE / 2); // Adjust amplitude if needed
                int offsetY = (int) (Math.cos(angle) * UNIT_SIZE / 2);

                // Draw each segment with the oscillating offset
                g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i] + offsetX, y[i] + offsetY, UNIT_SIZE, UNIT_SIZE);

                // Draw leaves on alternating segments
                if (i % 2 == 0) {
                    g.setColor(Color.green);
                    g.fillOval(x[i] + UNIT_SIZE / 2 + offsetX, y[i] + offsetY, UNIT_SIZE / 2, UNIT_SIZE / 4); // Left leaf
                    g.fillOval(x[i] + offsetX, y[i] + UNIT_SIZE / 2 + offsetY, UNIT_SIZE / 2, UNIT_SIZE / 4); // Right leaf
                }
            }

            // Display score
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + waterTaken, (SCREEN_WIDTH - metrics.stringWidth("Score: " + waterTaken)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }
*/

    /*
    //graphics for body which looks like a dot or increasing straight line
     public void draw(Graphics g) {
         if (running) {
             // Draw the apple
             g.setColor(Color.blue);
             g.fillOval(waterX, waterY, UNIT_SIZE, UNIT_SIZE);

             // Draw the plant's parts
             for (int i = 0; i < plantParts; i++) {
                 if (i == 0) {
                     g.setColor(Color.green); // Head
                     g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                 } else {
                     // Random color for each segment of the body
                     g.setColor(new Color(45, 180, 0));
                     //g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                     g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                 }
             }

             // Display the score on the screen
             g.setColor(Color.red);
             g.setFont(new Font("Ink Free", Font.BOLD, 40));
             FontMetrics metrics = getFontMetrics(g.getFont());
             g.drawString("Score: " + waterTaken, (SCREEN_WIDTH - metrics.stringWidth("Score: " + waterTaken)) / 2, g.getFont().getSize());
         } else {
             // Show Game Over screen if the game is not running
             gameOver(g);
         }
     }
 */
    // Generates a new water at a random location
    public void newWater() {
        waterX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        waterY = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
    }

    // Moves the plant in the current direction
    public void move() {
        for (int i = plantParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        // Change head position based on direction
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

    // Checks if the plant has taken an water
    public void checkWater() {
        if ((x[0] == waterX) && (y[0] == waterY)) {
            plantParts++;
            waterTaken++;
            newWater();
        }
    }

    // Checks if the plnat has collided with itself or the borders
    public void checkCollisions() {
        // Check collision with body
        for (int i = plantParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        // Check collision with left, right, top, and bottom borders
        if (x[0] < 0 || x[0] > SCREEN_WIDTH || y[0] < 0 || y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        // Stop the timer if the game is not running
        if (!running) {
            timer.stop();
        }
    }

    // Displays Game Over screen
    public void gameOver(Graphics g) {
        // Display score
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + waterTaken, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + waterTaken)) / 2, g.getFont().getSize());

        // Display Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }

    // Action performed with each timer tick (move, check water, check collisions)
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkWater();
            checkCollisions();
        }
        repaint();
    }

    // Inner class for handling key events
    public class MyKeyAdapter extends KeyAdapter {
        // Change direction based on arrow key input
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') direction = 'D';
                    break;
            }
        }
    }
}