
// Import necessary classes from the Swing library
import javax.swing.JFrame;

// GameFrame class that sets up the game window
public class GameFrame extends JFrame {
    // Constructor for GameFrame
    GameFrame() {
        // Create a new GamePanel instance
        GamePanel panel = new GamePanel();

        // Add the GamePanel to the frame
        this.add(panel);

        // Set the title of the window
        this.setTitle("PlantPal");

        // Ensure the application closes when the window is closed
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Prevent the window from being resized
        this.setResizable(false);

        // Automatically size the frame based on its components
        this.pack();

        // Make the window visible
        this.setVisible(true);

        // Center the frame on the screen
        this.setLocationRelativeTo(null);
    }
}