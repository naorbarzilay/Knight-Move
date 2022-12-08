package com.example.knightmove.Model;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;
import java.lang.Math;
import java.util.EventListener;
import java.util.Random;


public class ChessBoard {

    GridPane chessBoard;
    String theme;
    public ArrayList<Square> squares = new ArrayList<>();

    public ChessBoard(GridPane chessBoard, String theme){
        this.chessBoard = chessBoard;
        this.theme = theme;
        makeBoard(this.chessBoard, theme);
    }

    private ArrayList<point> createBlockingSquaresLocations(){
        ArrayList<point> specialSquaresLocations = new ArrayList<point>();
        while(specialSquaresLocations.size() <Consts.NUMBER_OF_BLOCKING_SQUARES){
            Random rand = new Random();
            int randX = rand.nextInt(7); // random x value in range of (0,7)
            int randY = rand.nextInt(7);// random y value in range of (0,7)
            if(!checkIfPointExist(specialSquaresLocations, randX, randY)){
                point specialSquarePoint = new point(randX,randY);
                specialSquaresLocations.add(specialSquarePoint);
            }
            System.out.println("current list length: "+ specialSquaresLocations.size());
        }

        for(point p : specialSquaresLocations){
            System.out.println("point coords: " + p.x +", "+ p.y);
        }
    return specialSquaresLocations;
    }

    private void makeBoard(GridPane chessBoard, String theme){
        /**
         * Algo:
         * 1. Create random locations of blocking squares
         * 2. For each square set it's theme based on its type.
         *
         */
        ArrayList<point> BlockingSquaresLocations=createBlockingSquaresLocations();

        for(int i=0; i<Consts.SQUARES_IN_ROW; i++){
            for(int j=0; j<Consts.SQUARES_IN_COLUMN; j++){
                Square square = new Square(i,j);
                square.setName("Square" + i + j);
                square.setPrefHeight(Consts.SQUARE_SIZE);
                square.setPrefWidth(Consts.SQUARE_SIZE);

                // NOTE: BoardStroke args (colurOfLinesBetweenSquares, typeOfLineBetweenSquares - could be dotted or full line)
                square.setBorder(new Border(new BorderStroke(Color.BLACK,
                        BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                setTheme(square, theme, i, j,BlockingSquaresLocations);
                chessBoard.add(square, i, j, 1, 1);
                squares.add(square);
            }
        }
        addPieces();
    }

    private void setTheme(Square square, String theme, int i, int j,ArrayList<point> BlockingSquaresLocations){
        point currentPoint = new point(i,j);
        Color color1 = Color.web("#ffffff00");
        Color color2 = Color.web("#ffffff00");
        Color colorSpecialSquare = Color.web("#FF0000");
        Color randomJumpSquare = Color.web("#9ACD32");



        switch (theme) {
            case "Coral" -> {
                color1 = Color.web("#b1e4b9");
                color2 = Color.web("#70a2a3");
            }
            case "Dusk" -> {
                color1 = Color.web("#cbb7ae");
                color2 = Color.web("#716677");
            }
            case "Wheat" -> {
                color1 = Color.web("#eaefce");
                color2 = Color.web("#bbbe65");
            }
            case "Marine" -> {
                color1 = Color.web("#9dacff");
                color2 = Color.web("#6f74d2");
            }
            case "Emerald" -> {
                color1 = Color.web("#adbd90");
                color2 = Color.web("#6e8f72");
            }
            case "Sandcastle" -> {
                color1 = Color.web("#e4c16f");
                color2 = Color.web("#b88b4a");
            }
        }


        for(point p : BlockingSquaresLocations){
            if(currentPoint.x == p.x && currentPoint.y == p.y){
                square.setBackground(new Background(new BackgroundFill(colorSpecialSquare, CornerRadii.EMPTY, Insets.EMPTY)));
                return;
            }
        }
        if((i+j)%2==0){
            square.setBackground(new Background(new BackgroundFill(color1, CornerRadii.EMPTY, Insets.EMPTY)));

        }else{
            square.setBackground(new Background(new BackgroundFill(color2, CornerRadii.EMPTY, Insets.EMPTY)));
        }

    }

    private void addPiece(Square square, Piece piece){
        square.getChildren().add(piece);
        square.occupied = true;
    }

    private void addPieces(){
        /**
         * Add pieces to their init location: pre-defined. (check out Const.java for enumerates).
         */
        for(Square square : squares){
            if(square.occupied) continue;
            // set horse init location
            if(square.x == Consts.KNIGHT_INIT_LOCATION_X && square.y == Consts.KNIGHT_INIT_LOCATION_Y){
                addPiece(square, new Knight("black", square.x, square.y));
            }
            // set queen init location
            else if(square.x == Consts.QUEEN_INIT_LOCATION_X && square.y == Consts.QUEEN_INIT_LOCATION_Y){
                addPiece(square, new Queen("black", square.x, square.y));
            }
            // set king init location
            else if(square.x == Consts.KING_INIT_LOCATION_X && square.y == Consts.KING_INIT_LOCATION_Y){
                addPiece(square, new King("black", square.x, square.y,Consts.KING_INIT_SPEED));

            }

        }

    }

    private boolean checkIfPointExist(ArrayList<point> specialSquaresLocations,int randX,int randY){
        for(point p : specialSquaresLocations){
            if(p.x == randX && p.y == randY){
                return true;
            }
        }
        return false;
    }


    private void specialSquareMessege(point currentPosition , ArrayList<point> specialSquaresLocations){
        for(point p : specialSquaresLocations){
            if(currentPosition.equals(p)){
                Alert al = new Alert(Alert.AlertType.ERROR);
                al.setContentText("GAME ENDED!");
                al.setHeaderText("GAME ENDED!");
                al.setTitle("GAME ENDED!");
                al.setResizable(false);
                al.showAndWait();;
            }
        }

    }

}

