package org.example.checkers;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javafx.scene.text.Text;

public class CheckersApp extends Application {

    int white_count=12;
    int red_count=12;
    boolean REDturn = false;
    boolean WHITEturn =true;
    public static final int TILE_SIZE = 100;

    public static final int WIDTH = 8;

    public static final int HEIGHT = 8;

    private Tile[][] board = new Tile[WIDTH][HEIGHT];

    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();
    Pane root = new Pane();

    private Parent createContent() {
        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        root.getChildren().addAll(tileGroup, pieceGroup);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;

                tileGroup.getChildren().addAll(tile);

                Piece piece = null;

                if (y <= 2 && (x + y) % 2 != 0)
                {
                    piece = makePiece(PieceType.RED, x, y);
                }

                if (y >= 5 && (x + y) % 2 != 0)
                {
                    piece = makePiece(PieceType.WHITE, x, y);
                }

                if (piece != null)
                {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().addAll(piece);
                }
            }
        }

        return root;
    }

    private MoveResult tryMove(Piece piece, int newX, int newY) {
        if (board[newX][newY].hasPiece() || (newX + newY) % 2 == 0) {
            return new MoveResult(MoveType.NONE);
        }
        if((piece.getType() == PieceType.RED && REDturn) || (piece.getType() == PieceType.WHITE && WHITEturn)) {
            MoveResult result = new MoveResult(MoveType.NONE);


            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());

            if(piece.isKing){
                if (Math.abs(newX - x0) == 1) {
                    result = new MoveResult(MoveType.NORMAL);
                } else if (Math.abs(newX - x0) == 2) {

                    int x1 = x0 + (newX - x0) / 2;
                    int y1 = y0 + (newY - y0) / 2;

                    if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
                        result = new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
                    }
                }
            }
            else {
                if (Math.abs(newX - x0) == 1 && newY - y0 == piece.getType().moveDir) {
                    result = new MoveResult(MoveType.NORMAL);
                } else if (Math.abs(newX - x0) == 2 && newY - y0 == piece.getType().moveDir * 2) {

                    int x1 = x0 + (newX - x0) / 2;
                    int y1 = y0 + (newY - y0) / 2;

                    if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
                        result = new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
                    }
                }
            }
            if(piece.getType() == PieceType.RED && REDturn){
                REDturn = false;
                WHITEturn = true;
            }
            else{
                WHITEturn = false;
                REDturn = true;
            }
            return result;
        }

        return new MoveResult(MoveType.NONE);
    }

    private int toBoard(double pixel) {
        return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("CheckersApp");
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    private Piece makePiece(PieceType type, int x, int y) {
        Piece piece = new Piece(type, x, y);


        piece.setOnMouseReleased(e -> {
            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());

            MoveResult result;

            if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
                result = new MoveResult(MoveType.NONE);
            } else {
                result = tryMove(piece, newX, newY);
            }

            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());

            switch (result.getType()) {
                case NONE:
                    piece.abortMove();
                    break;
                case NORMAL:
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);

                    if((piece.getType() == PieceType.RED && newY == 7) || (piece.getType() == PieceType.WHITE && newY == 0)) {
                        piece.isKing = true;
//                        board[newX][newY].setPiece(null);
//                        pieceGroup.getChildren().remove(piece);
//
//                        Piece piece1 = new Piece(type, newX, newY, piece.isKing);
//                        board[newX][newY].setPiece(piece1);
//                        pieceGroup.getChildren().addAll(piece1);
                    }


                    break;
                case KILL:
                    if(result.getPiece().getType()== PieceType.RED)
                    {
                        red_count--;
                    }
                    else
                    {
                        white_count--;
                    }
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);

                    Piece otherPiece = result.getPiece();
                    board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                    pieceGroup.getChildren().remove(otherPiece);

                    if((piece.getType() == PieceType.RED && newY == 7) || (piece.getType() == PieceType.WHITE && newY == 0)) {
                        piece.isKing = true;
//                        board[newX][newY].setPiece(null);
//                        pieceGroup.getChildren().remove(piece);
//
//                        Piece piece1 = new Piece(type, newX, newY, piece.isKing);
//                        board[newX][newY].setPiece(piece1);
//                        pieceGroup.getChildren().addAll(piece1);
                    }
                    break;
            }

            if(white_count==0)
            {
                Text text= new Text();
                text.setText("RED HAS WON!!");
                text.setX(10);
                text.setY(400);;
                text.setFont(Font.font("Verdana", 83));
                text.setFill(Color.RED);
                root.getChildren().add(text);
            }

            if(red_count==0)
            {
                Text text= new Text();
                text.setText("WHITE HAS WON!!");
                text.setX(10);
                text.setY(400);;
                text.setFont(Font.font("Verdana", 83));
                text.setFill(Color.RED);
                root.getChildren().add(text);
            }
        });

        return piece;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
