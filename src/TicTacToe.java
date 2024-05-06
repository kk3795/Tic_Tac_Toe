import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Optional;
import java.util.Random;
import java.util.Stack;

public class TicTacToe extends Application {

    private Button[][] board;
    private char currentPlayer;
    private boolean singlePlayer;
    private char computerSymbol;
    private char playerSymbol;
    private Label turnLabel;

    private Stack<int[]> playerMoveStack = new Stack<>();
    private Stack<int[]> computerMoveStack = new Stack<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tic Tac Toe");

        Button singlePlayerButton = new Button("Single Player");
        singlePlayerButton.setStyle("-fx-font-size: 20px;");
        Button multiplayerButton = new Button("Multiplayer");
        multiplayerButton.setStyle("-fx-font-size: 20px;");

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20); // Set vertical space between nodes

        Label titleLabel = new Label("Tic Tac Toe");
        titleLabel.setStyle("-fx-font-size: 36px;"); // Set font size for the heading
        vbox.getChildren().add(titleLabel);

        vbox.getChildren().addAll(singlePlayerButton, multiplayerButton);

        singlePlayerButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Choose Difficulty");
            alert.setHeaderText("Select the difficulty level");
            alert.setContentText("Choose your option:");

            ButtonType beginnerButton = new ButtonType("Beginner");
            ButtonType advancedButton = new ButtonType("Advanced");
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(beginnerButton, advancedButton, cancelButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == beginnerButton) {
                startGame(true, 'X'); // Single player, player is X
            } else if (result.isPresent() && result.get() == advancedButton) {
                startGame(true, 'O'); // Single player, player is O
            }
        });

        multiplayerButton.setOnAction(event -> startGame(false, 'X')); // Multiplayer, player 1 is X

        Scene scene = new Scene(vbox, 400, 400); // Increased width and height
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startGame(boolean singlePlayer, char firstPlayer) {
        this.singlePlayer = singlePlayer;
        currentPlayer = firstPlayer;
        computerSymbol = (firstPlayer == 'X') ? 'O' : 'X';
        playerSymbol = (firstPlayer == 'X') ? 'O' : 'X'; // Set player symbol based on first player
        board = new Button[3][3];

        BorderPane borderPane = new BorderPane();

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button();
                button.setPrefSize(100, 100);
                button.setStyle("-fx-font-size: 20px;");
                int finalI = i;
                int finalJ = j;
                button.setOnAction(event -> {
                    makeMove(finalI, finalJ);
                    animateButton(button);
                });
                board[i][j] = button;
                gridPane.add(button, j, i);
            }
        }

        turnLabel = new Label("Turn: " + currentPlayer);
        turnLabel.setStyle("-fx-font-size: 20px;");
        turnLabel.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        Button undoButton = new Button("Undo");
        undoButton.setOnAction(event -> undoMove());
        undoButton.setStyle("-fx-font-size: 16px;");

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(event -> resetGame());
        resetButton.setStyle("-fx-font-size: 16px;");

        buttonBox.getChildren().addAll(undoButton, resetButton);

        borderPane.setTop(turnLabel);
        BorderPane.setAlignment(turnLabel, Pos.CENTER);
        borderPane.setCenter(gridPane);
        borderPane.setBottom(buttonBox);

        Scene scene = new Scene(borderPane, 400, 400);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Tic Tac Toe - Game");
        stage.show();

        if (singlePlayer && currentPlayer == computerSymbol) {
            makeComputerMove();
        }
    }

    private void makeMove(int row, int col) {
        if (board[row][col].getText().isEmpty()) {
            board[row][col].setText(String.valueOf(currentPlayer));
            if (currentPlayer == playerSymbol) {
                playerMoveStack.push(new int[]{row, col});
            } else {
                computerMoveStack.push(new int[]{row, col});
            }
            if (checkWin()) {
                endGame(currentPlayer + " wins!");
            } else if (isBoardFull()) {
                endGame("It's a tie!");
            } else {
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                turnLabel.setText("Turn: " + currentPlayer);
                if (singlePlayer && currentPlayer == computerSymbol) {
                    makeComputerMove();
                }
            }
        }
    }

    private void undoMove() {
        if (!playerMoveStack.isEmpty()) {
            int[] playerLastMove = playerMoveStack.pop();
            board[playerLastMove[0]][playerLastMove[1]].setText("");
        }
        if (!computerMoveStack.isEmpty()) {
            int[] computerLastMove = computerMoveStack.pop();
            board[computerLastMove[0]][computerLastMove[1]].setText("");
        }
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        turnLabel.setText("Turn: " + currentPlayer);
    }

    private void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j].setText("");
            }
        }
        currentPlayer = 'X'; // Reset to player X
        turnLabel.setText("Turn: " + currentPlayer);
    }

    private void makeComputerMove() {
        if (singlePlayer) {
            if (currentPlayer == computerSymbol) {
                // Beginner level: Random move
                Random random = new Random();
                int row, col;
                do {
                    row = random.nextInt(3);
                    col = random.nextInt(3);
                } while (!board[row][col].getText().isEmpty());
                board[row][col].setText(String.valueOf(currentPlayer));
            } else {
                // Advanced level: Minimax algorithm
                int[] move = minimax(board, computerSymbol);
                board[move[0]][move[1]].setText(String.valueOf(computerSymbol));
                currentPlayer = playerSymbol;
            }
            if (checkWin()) {
                endGame(currentPlayer + " wins!");
            } else if (isBoardFull()) {
                endGame("It's a tie!");
            } else {
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                turnLabel.setText("Turn: " + currentPlayer);
            }
        }
    }

    private int[] minimax(Button[][] board, char player) {
        int bestScore;
        int[] bestMove = new int[] { -1, -1 };

        if (checkWin()) {
            bestScore = (player == computerSymbol) ? 10 : -10;
        } else if (isBoardFull()) {
            bestScore = 0;
        } else {
            bestScore = (player == computerSymbol) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].getText().isEmpty()) {
                        board[i][j].setText(String.valueOf(player));
                        int score = minimax(board, (player == computerSymbol) ? playerSymbol : computerSymbol)[0];
                        board[i][j].setText("");

                        if ((player == computerSymbol && score > bestScore)
                                || (player == playerSymbol && score < bestScore)) {
                            bestScore = score;
                            bestMove = new int[] { i, j };
                        }
                    }
                }
            }
        }

        return new int[] { bestScore, bestMove[0], bestMove[1] };
    }

    private boolean checkWin() {
        // Check for win logic
        for (int i = 0; i < 3; i++) {
            if (board[i][0].getText().equals(board[i][1].getText())
                    && board[i][1].getText().equals(board[i][2].getText())
                    && !board[i][0].getText().isEmpty()) {
                return true; // Check rows
            }
            if (board[0][i].getText().equals(board[1][i].getText())
                    && board[1][i].getText().equals(board[2][i].getText())
                    && !board[0][i].getText().isEmpty()) {
                return true; // Check columns
            }
        }
        if (board[0][0].getText().equals(board[1][1].getText())
                && board[1][1].getText().equals(board[2][2].getText())
                && !board[0][0].getText().isEmpty()) {
            return true; // Check diagonal from top-left to bottom-right
        }
        if (board[0][2].getText().equals(board[1][1].getText())
                && board[1][1].getText().equals(board[2][0].getText())
                && !board[0][2].getText().isEmpty()) {
            return true; // Check diagonal from top-right to bottom-left
        }
        return false;
    }

    private boolean isBoardFull() {
        // Check if the board is full logic
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].getText().isEmpty()) {
                    return false; // If any button is empty, board is not full
                }
            }
        }
        return true; // All buttons are filled
    }

    private void endGame(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(message);
        alert.showAndWait();
        Stage stage = (Stage) turnLabel.getScene().getWindow(); // Get the stage from any node in the scene
        stage.close(); // Close the stage
    }

    private void animateButton(Button button) {
        button.setOpacity(0.5); // Set initial opacity
        button.setOnMousePressed(event -> button.setOpacity(0.5)); // Set opacity to full when pressed
        button.setOnMouseReleased(event -> button.setOpacity(1.0)); // Set opacity back to half when released
    }
    

    public static void main(String[] args) {
        launch(args);
    }
}
