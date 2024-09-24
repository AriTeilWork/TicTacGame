import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerInputDialog extends JDialog {
    private JTextField player1Field; // Text field for Player 1's name
    private JTextField player2Field; // Text field for Player 2's name
    private JButton okButton; // Button to confirm the names
    private String player1Name; // Name of Player 1
    private String player2Name; // Name of Player 2

    public PlayerInputDialog(Frame owner) {
        super(owner, "Enter Player Names", true);
        setLayout(new GridLayout(3, 2));

        // Add fields for Player 1
        add(new JLabel("Player 1 (X):"));
        player1Field = new JTextField(20);
        add(player1Field);

        // Add fields for Player 2
        add(new JLabel("Player 2 (O):"));
        player2Field = new JTextField(20);
        add(player2Field);

        // OK button to confirm the names
        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player1Name = player1Field.getText(); // Get Player 1's name
                player2Name = player2Field.getText(); // Get Player 2's name
                dispose(); // Close the dialog
            }
        });
        add(okButton);

        setSize(300, 150);
        setLocationRelativeTo(owner); // Center the dialog on the owner
    }

    // Getters for player names
    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }
}
