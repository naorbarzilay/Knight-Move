package com.example.knightmove.controllers;

import com.example.knightmove.HelloApplication;
import com.example.knightmove.Model.*;
import javafx.event.EventTarget;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
// import the required classes
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import static com.example.knightmove.Model.Json.readFromJSON;
import static java.lang.System.exit;

public class GamePageController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    private Timeline timeline = new Timeline();

    private int startTimeSec;

    public GamePageController() throws IOException, ParseException {
    }

    public void initialize() {

        // Themes are Coral, Dusk, Wheat, Marine, Emerald, Sandcastle
        cb = new ChessBoard(chessBoard, "Sandcastle");
        currentPiece = null;
        currentPlayer = "black";
        this.game = true;
        score=0;
        visitedSquares = new ArrayList<>();
        addEventHandlers(cb.chessBoard);

        startTimeSec = 60; // Change to 60!
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startTimeSec--;
                boolean isSecondsZero = startTimeSec == 0;
                boolean timeToChangeLevel = startTimeSec == 0;

                if (timeToChangeLevel) {
                    timeline.stop();
                    startTimeSec = 60;
                    if (currentLevelText.getText().equals("1")) {
                        currentScore.setText(Integer.toString(GamePageController.score));
                        currentLevelText.setText("2");
                    } else if (currentLevelText.getText().equals("2")) {
                        currentLevelText.setText("3");
                    } else if (currentLevelText.getText().equals("3")) {
                        GamePageController.score+=105;
                        currentScore.setText(Integer.toString(GamePageController.score));
                        currentLevelText.setText("4");
                    } else if (currentLevelText.getText().equals("4")) {
                        currentLevelText.setText("End");
                        timeline.stop();
                        try {
                            root = FXMLLoader.load(HelloApplication.class.getResource("EndGamePage.fxml"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        stage = (Stage) mainPane.getScene().getWindow();
                        stage.setTitle("Game Over");
                        scene = new Scene(root);
                        stage.setScene(scene);
                        stage.setUserData(currentScore);
                        stage.show();
                    }
                }
                currentTimeText.setText(String.format("%02d sec", startTimeSec));
                if (!currentLevelText.getText().equals("End")) {
                    timeline.playFromStart();
                }
            }
        }));
    }

    public void returnToAppIntroPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(HelloApplication.class.getResource("AppIntroPage.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void updateScore()
    {
        this.currentScore.setText(Integer.toString(GamePageController.score));
    }
    public void newLevel(ActionEvent event) {
        startTimeSec = 60; // Change to 60!
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startTimeSec--;
                boolean isSecondsZero = startTimeSec == 0;
                boolean timeToChangeLevel = startTimeSec == 0;

                currentTimeText.setText(String.format("%02d sec", startTimeSec));
            }
        }));
        timeline.playFromStart();
    }

    @FXML
    private Text CurrentTurnText;

    @FXML
    private Text CurrentScoreText;

    @FXML
    public Label currentScore;
    @FXML
    private Text timeText;

    @FXML
    GridPane chessBoard;

    @FXML
    private ImageView boardCurrentStateImage;

    @FXML
    private Pane mainPane;

    @FXML
    private Label currentLevelText;

    @FXML
    private Label currentTimeText;

    public static void createQuestionPopUp(){
        HashSet<Question> allQuestionsInJSON= readFromJSON();
        // convert the HashSet to an array
        Object[] array = allQuestionsInJSON.toArray();
        // get a random questions from the array
        Object randomQuestion = array[ThreadLocalRandom.current().nextInt(array.length)];
        Question questionObject = (Question) randomQuestion;
        String questionNameToPopUp = questionObject.getQuestion();
        Integer questionLevel = questionObject.getLevel();
        ArrayList<String> answers = questionObject.getAnswers();
        ArrayList<ButtonType> answersOptions = new ArrayList<>();
        for(String answer : answers){
            answersOptions.add(new ButtonType(answer));
        }
        Integer correctAnswerNumber = questionObject.getCorrectAnswer();
        String correctAnswerStringByIndex = answers.get(correctAnswerNumber-1);
        
        // create an Alert object
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"",answersOptions.get(0),answersOptions.get(1),answersOptions.get(2),answersOptions.get(3));
        alert.setHeaderText(questionNameToPopUp);
        // set the alert's message to the first question
        alert.setContentText("Select your answer:");
        // show the alert and get the user's response
        ButtonType response = alert.showAndWait().orElse(null);
        String playerSelectedAnswer = response.getText();

        Alert correctAnswer = new Alert(Alert.AlertType.INFORMATION);
        correctAnswer.setTitle("Correct Answer");
        correctAnswer.setHeaderText("Correct Answer");
        correctAnswer.setContentText("Congratulations, that is the correct answer.");

        Alert wrongAnswer = new Alert(Alert.AlertType.ERROR);
        wrongAnswer.setTitle("Wrong Answer");
        wrongAnswer.setHeaderText("Wrong Answer");
        wrongAnswer.setContentText("Sorry, that is the wrong answer. Please try again.");

        // check the user's response
        if (playerSelectedAnswer.equals(correctAnswerStringByIndex)) {
            correctAnswer.showAndWait();
            GamePageController.score += questionLevel;
            System.out.println("Game.score " + GamePageController.score);
        }else {
            wrongAnswer.showAndWait();
            GamePageController.score -= (questionLevel+1);
            System.out.println("Game.score " + GamePageController.score);
        }
    }
    // Game Class
    public static Piece currentPiece;
    public static String currentPlayer;
    public static ChessBoard cb;
    private boolean game;

    public static int score;

    ArrayList<Square> visitedSquares; // squares they already visited at.

    public ArrayList<Square> getVisitedSquares() {
        return visitedSquares;
    }

    public void resetVisitedSquares() {
        for(Square square : visitedSquares){
            if((square.getY()+square.getX())%2==0){
                square.setBackground(new Background(new BackgroundFill(Consts.color1, CornerRadii.EMPTY, Insets.EMPTY)));
            }else{
                square.setBackground(new Background(new BackgroundFill(Consts.color2, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
        this.visitedSquares = new ArrayList<>();
    }

    public void addToVisitedSquares(Square sq){
        if(sq != null){
            if(!visitedSquares.contains(sq))
                this.visitedSquares.add(sq);
        }
    }
    private void addEventHandlers(GridPane chessBoard){
        chessBoard.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                EventTarget target = event.getTarget();

                //Clicked on the Knight
                if (target.toString().equals("Knight")) {
                    Piece newPiece = (Piece) target;
                    Square square = (Square) newPiece.getParent();
                    square.setBackground(new Background(new BackgroundFill(Consts.colorVisitedSquare, CornerRadii.EMPTY, Insets.EMPTY)));
                    //addToVisitedSquares(square);
                    //System.out.println("Knight possible moves:\n" + newPiece.possibleMoves);
                    // Selecting a new piece
                    if (currentPiece == null) {
                        currentPiece = newPiece;
                        if (!currentPiece.getColor().equals(currentPlayer)) {
                            currentPiece = null;
                            return;
                        }
                        selectPiece(game);
                    }
                    // Selecting other piece of same color || Killing a piece
                    else {
                        if (currentPiece.getColor().equals(newPiece.getColor())) {
                          // System.out.println("inside internal if of knight");
                            deselectPiece(false);
                            currentPiece = newPiece;
                            selectPiece(game);
                        } else {
                            killPiece(square);
                        }
                    }

                }
                //Clicked on the queen - DELETED!
                // Clicked on square
                if (target.toString().equals("Square")) {
                    Square square = (Square) target;
                    if (square.occupied) {
                        Piece newPiece = (Piece) square.getChildren().get(0);
                        // Selecting a new piece
                        if (currentPiece == null) {
                            currentPiece = newPiece;
                            if (!currentPiece.getColor().equals(currentPlayer)) {
                                currentPiece = null;
                                return;
                            }
                            selectPiece(game);
                        }
                        // Selecting other piece of same color || Killing a piece
                        else {
//                            System.out.println("inside else of square");
                            if (currentPiece.getColor().equals(newPiece.getColor())) {
                                System.out.println("inside internal if of square");
                                deselectPiece(false);
                                currentPiece = newPiece;
                                selectPiece(game);
                            } else {
                                System.out.println("inside internal else of square");
                                killPiece(square);
                            }
                        }

                    }
                    // Dropping a piece on blank square
                    else {
                        //removing the blockingSquares from possibleMoves
                        ArrayList<point> blockingSquares = new ArrayList<point>(cb.blockingSquaresLocations);
                        //removing the blockingSquares from possibleMoves
                        for (point p : blockingSquares) {
                            String squareString = "Square" + p.getX() + p.getY();
                            if (currentPiece.possibleMoves.contains(squareString)) {
                                currentPiece.possibleMoves.remove(squareString);
                            }
                        }
                        //System.out.println("currentPiece moves after drop:\n " + currentPiece.possibleMoves);
                        if (currentPiece.toString().equals("Knight")) {
                            square.setBackground(new Background(new BackgroundFill(Consts.colorVisitedSquare, CornerRadii.EMPTY, Insets.EMPTY)));
                            //addToVisitedSquares(square);
                        }
                        dropPiece(square);
                        if (visitedSquares.contains(square)) {
                            GamePageController.score--;
                        } else {
                            GamePageController.score++;
                        }
                        addToVisitedSquares(square);
                        for(Square s : getVisitedSquares()){
                            System.out.println("Visited Square:\n " + s.getX()+","+s.getY());
                        }

                        /**
                         * The knight clicked on empty square, afterwards move the queen
                         */

                        int queenNextPositionX = -1;
                        int queenNextPositionY = -1;
                        int[] knightPositions = new int[2];
                        knightPositions[0] = square.getX();
                        knightPositions[1] = square.getY();
                        Piece foundQueen = null;
                        for (Square sq : cb.getSquares()) {

                            if (sq.getChildren().size() > 0) {
                                String pieceName = String.valueOf(sq.getChildren().get(0));
                                if (pieceName.equals("Queen")) {
                                    Piece queen = (Piece) sq.getChildren().get(0);
                                    Square queenSquare = (Square) queen.getParent();
                                    Queen newQueen = (Queen) queen;
                                    currentPiece = newQueen;
                                    foundQueen = newQueen;
                                    ArrayList<String> possibleMoves = newQueen.getAllPossibleMoves();
                                    ArrayList<ArrayList<Integer>> possibleMovesInArrayOfTwo = newQueen.convertMovesToIntArrays(newQueen.getAllPossibleMoves());
//                                    ArrayList<Integer> movesSelector = newQueen.selectQueenMovements("random", possibleMovesInArrayOfTwo, knightPositions);
                                    ArrayList<Integer> movesSelector = newQueen.selectQueenMovements("smart", possibleMovesInArrayOfTwo, knightPositions);
                                    killPiece(queenSquare);
                                    //Doing Random/Smart movement (with Manhattan Distance) for Queen
                                    queenNextPositionX = movesSelector.get(0);
                                    queenNextPositionY = movesSelector.get(1);

                                }
                            }

                        }
                        if (foundQueen != null) {
                            currentPiece = foundQueen;
                        }
                        for (Square sq : cb.getSquares()) {
                            if (sq.getX() == queenNextPositionX && sq.getY() == queenNextPositionY && foundQueen != null) {
                                currentPiece = foundQueen;
                                dropPiece(sq);
                            }
                        }

                    }
                }
            }
        });
    }
    private void selectPiece(boolean game){
        if(!game){
            currentPiece = null;
            return;
        }

        DropShadow borderGlow = new DropShadow();
        borderGlow.setColor(Color.BLACK);
        borderGlow.setOffsetX(0f);
        borderGlow.setOffsetY(0f);
        currentPiece.setEffect(borderGlow);
        currentPiece.getAllPossibleMoves();
        currentPiece.showAllPossibleMoves(true);
    }

    private void deselectPiece(boolean changePlayer){
        currentPiece.setEffect(null);
        currentPiece.showAllPossibleMoves(false);
        currentPiece = null;
    }

    private void dropPiece(Square square){
        if(!currentPiece.possibleMoves.contains(square.name)) return;
        System.out.println("move to square " + square.name);
        Square initialSquare = (Square) currentPiece.getParent();
        square.getChildren().add(currentPiece);
        square.occupied = true;
        initialSquare.getChildren().removeAll();
        initialSquare.occupied = false;
        currentPiece.setPosX(square.getX());
        currentPiece.setPosY(square.getY());
        deselectPiece(true);
 //       GamePageController.createQuestionPopUp();
        updateScore();

    }

    private void killPiece(Square square){
        if(!currentPiece.possibleMoves.contains(square.name)) return;

        Piece killedPiece = (Piece) square.getChildren().get(0);
        if(killedPiece.type.equals("King")) this.game = false;
       // System.out.println("move from square " + square.name);
        Square initialSquare = (Square) currentPiece.getParent();
        square.getChildren().remove(0);
        square.getChildren().add(currentPiece);
        square.occupied = true;
        initialSquare.getChildren().removeAll();
        initialSquare.occupied = false;
        currentPiece.setPosX(square.getX());
        currentPiece.setPosY(square.getY());
        deselectPiece(true);
    }
}
