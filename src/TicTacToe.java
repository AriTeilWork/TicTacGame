import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TicTacToe extends JFrame {
    // Declare variables for buttons, board state, players, and game status
    private JButton[] buttons;
    private char[] board;
    private char currentPlayer;
    private boolean gameOver;
    private String player1Name;
    private String player2Name;
    private char player1Symbol;
    private char player2Symbol;
    private JLabel statusLabel;
    private JButton saveButton;

    public TicTacToe() {
        // Set window title and basic configuration
        setTitle("Tic Tac Toe");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 3)); // Layout to arrange buttons and labels

        // Initialize buttons and board state
        buttons = new JButton[9];
        board = new char[9];
        currentPlayer = 'X';
        gameOver = false;

        // Create 9 buttons for the Tic Tac Toe grid
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 60));
            buttons[i].setFocusPainted(false);
            buttons[i].addActionListener(new ButtonClickListener(i)); // Attach event listener to each button
            add(buttons[i]); // Add buttons to the window
        }

        // Add status label to display messages to the user
        statusLabel = new JLabel("Welcome to Tic Tac Toe!");
        add(statusLabel);

        // Add "Save Game" button to allow saving game state
        saveButton = new JButton("Save Game");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGameState(); // Save the current game state to a file
                JOptionPane.showMessageDialog(TicTacToe.this, "Game saved successfully!"); // Show confirmation
            }
        });
        add(saveButton);

        // Display the initial menu to the user
        displayMenu();
    }

    // Method to display a menu for starting or loading a game
    private void displayMenu() {
        String[] options = {"New Game", "Load Game", "Exit"};
        int choice = JOptionPane.showOptionDialog(this,
                "Choose an option:",
                "Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        // Handle user's menu choice
        if (choice == 0) {
            inputPlayerNames(); // Ask for player names
            chooseSymbols();    // Ask for player symbols
            resetGame();        // Start a new game
        } else if (choice == 1) {
            loadGameFromFile(); // Load saved game from file
            if (player1Name == null || player2Name == null) {
                JOptionPane.showMessageDialog(this, "No saved game found. Starting a new game.");
                inputPlayerNames(); // If no saved game, start fresh
                chooseSymbols();
                resetGame();
            } else {
                statusLabel.setText(player1Name + " (" + currentPlayer + ")'s turn.");
                updateBoardUI(); // Update the board with the loaded state
            }
        } else {
            System.exit(0); // Exit the game
        }
    }

    // Listener for button clicks in the Tic Tac Toe grid
    private class ButtonClickListener implements ActionListener {
        private int index; // The index of the button clicked

        public ButtonClickListener(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // If the game is not over and the selected cell is empty
            if (!gameOver && board[index] == '\u0000') {
                board[index] = currentPlayer; // Update the board
                buttons[index].setText(String.valueOf(currentPlayer)); // Update button text with current player's symbol

                // Check if the current player has won
                if (checkWinner()) {
                    statusLabel.setText("Player " + currentPlayer + " wins!"); // Show winner
                    gameOver = true;
                    saveGameResult(); // Save the game result to a file
                    // Ask if the player wants to play again
                    int response = JOptionPane.showConfirmDialog(TicTacToe.this,
                            "Game Over. Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        resetGame(); // Reset for a new game
                    } else {
                        System.exit(0); // Exit the game
                    }
                    // Check if the board is full (i.e., a draw)
                } else if (isBoardFull()) {
                    statusLabel.setText("It's a draw!"); // Show draw message
                    gameOver = true;
                    saveGameResult(); // Save the game result to a file
                    // Ask if the player wants to play again
                    int response = JOptionPane.showConfirmDialog(TicTacToe.this,
                            "Game Over. Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        resetGame(); // Reset for a new game
                    } else {
                        System.exit(0); // Exit the game
                    }
                } else {
                    // Switch to the next player and update the status label
                    currentPlayer = (currentPlayer == player1Symbol) ? player2Symbol : player1Symbol;
                    statusLabel.setText((currentPlayer == player1Symbol ? player1Name : player2Name) + " (" + currentPlayer + ")'s turn.");
                }
            }
        }
    }

    // Method to check if the current player has won the game
    private boolean checkWinner() {
        int[][] winConditions = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6} // Possible winning combinations
        };

        // Check each win condition to see if current player has won
        for (int[] condition : winConditions) {
            if (board[condition[0]] == currentPlayer &&
                    board[condition[1]] == currentPlayer &&
                    board[condition[2]] == currentPlayer) {
                return true;
            }
        }
        return false;
    }

    // Check if the board is full (i.e., all cells are filled)
    private boolean isBoardFull() {
        for (char cell : board) {
            if (cell == '\u0000') { // Empty cell found
                return false;
            }
        }
        return true;
    }

    // Method to reset the game state for a new round
    private void resetGame() {
        for (int i = 0; i < 9; i++) {
            board[i] = '\u0000'; // Reset board cells to empty
            buttons[i].setText(""); // Clear button text
        }
        currentPlayer = player1Symbol; // Set current player to player 1
        gameOver = false; // Reset game over flag
        statusLabel.setText(player1Name + " (" + currentPlayer + ")'s turn."); // Update status
    }

    // Method to input player names via a custom dialog
    private void inputPlayerNames() {
        PlayerInputDialog inputDialog = new PlayerInputDialog(this);
        inputDialog.setVisible(true);
        player1Name = inputDialog.getPlayer1Name(); // Get player 1's name
        player2Name = inputDialog.getPlayer2Name(); // Get player 2's name
        savePlayersToFile(); // Save player names to a file
    }

    // Method to allow player 1 to choose their symbol (X or O)
    private void chooseSymbols() {
        Object[] options = {"X", "O"};
        int choice = JOptionPane.showOptionDialog(this,
                player1Name + ", choose your symbol:",
                "Choose Symbol",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        // Assign symbols based on choice
        if (choice == 0) {
            player1Symbol = 'X';
            player2Symbol = 'O';
        } else {
            player1Symbol = 'O';
            player2Symbol = 'X';
        }

        currentPlayer = player1Symbol; // Set the current player to player 1
        statusLabel.setText(player1Name + " (" + currentPlayer + ")'s turn."); // Update status label
    }

    // Method to save the game result to a file
    private void saveGameResult() {
        String result;
        if (checkWinner()) {
            result = (currentPlayer == player1Symbol) ? player1Name + " wins!" : player2Name + " wins!";
        } else {
            result = "It's a draw!";
        }
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try (FileWriter writer = new FileWriter("game_results.txt", true)) {
            writer.write("Date and Time: " + dateTime + "\n");
            writer.write("Player 1: " + player1Name + " (" + player1Symbol + ")\n");
            writer.write("Player 2: " + player2Name + " (" + player2Symbol + ")\n");
            writer.write("Result: " + result + "\n");
            writer.write("----------------------------------------------------\n");
        } catch (IOException e) {
            e.printStackTrace(); // Handle file write errors
        }
    }

    // Method to save the current game state to a file
    private void saveGameState() {
        try (FileWriter writer = new FileWriter("saved_game.txt")) {
            writer.write("Player 1: " + player1Name + " (" + player1Symbol + ")\n");
            writer.write("Player 2: " + player2Name + " (" + player2Symbol + ")\n");
            writer.write("Current Player: " + currentPlayer + "\n");

            // Save the board state
            for (int i = 0; i < board.length; i++) {
                writer.write(board[i] == '\u0000' ? '.' : board[i]);
                if ((i + 1) % 3 == 0) writer.write("\n"); // New line after every 3 cells
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle file write errors
        }
    }

    // Save player names and symbols to a file
    private void savePlayersToFile() {
        try (FileWriter writer = new FileWriter("players.txt")) {
            writer.write("Player 1: " + player1Name + " (" + player1Symbol + ")\n");
            writer.write("Player 2: " + player2Name + " (" + player2Symbol + ")\n");
        } catch (IOException e) {
            e.printStackTrace(); // Handle file write errors
        }
    }

    // Load a saved game state from a file
    private void loadGameFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("saved_game.txt"))) {
            String player1Line = reader.readLine();
            String player2Line = reader.readLine();
            String currentPlayerLine = reader.readLine();

            if (player1Line != null && player2Line != null && currentPlayerLine != null) {
                String[] player1Data = player1Line.split(" ");
                String[] player2Data = player2Line.split(" ");
                String[] currentPlayerData = currentPlayerLine.split(" ");

                player1Name = player1Data[1];
                player1Symbol = player1Data[2].charAt(1);
                player2Name = player2Data[1];
                player2Symbol = player2Data[2].charAt(1);
                currentPlayer = currentPlayerData[2].charAt(0);

                board = new char[9];
                for (int i = 0; i < board.length; i++) {
                    int c = reader.read();
                    board[i] = (c == '.') ? '\u0000' : (char) c;
                    if (c == '\n') reader.read(); // Skip new lines
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle file read errors
        }
    }

    // Update the UI with the current board state
    private void updateBoardUI() {
        for (int i = 0; i < board.length; i++) {
            buttons[i].setText(board[i] == '\u0000' ? "" : String.valueOf(board[i]));
        }
        statusLabel.setText((currentPlayer == player1Symbol ? player1Name : player2Name) + " (" + currentPlayer + ")'s turn.");
    }

    // Main method to start the game
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TicTacToe game = new TicTacToe();
            game.setVisible(true); // Show the game window
        });
    }
}
