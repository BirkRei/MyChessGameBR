package me.Birk.chess;

import me.Birk.chess.core.ChessGame;
import me.Birk.chess.swing.ChessWindow;

public class Main {
    public static ChessGame chessGame = new ChessGame();
    public static ChessGame chessGameOld = new ChessGame();
    public static void main(String[] args) {
        final ChessWindow chessWindow = new ChessWindow();


    }
    public static boolean isEven(int number) {
        return number % 2 == 0;
    }
}
