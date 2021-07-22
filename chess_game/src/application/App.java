package application;

import java.util.Locale;
import java.util.Scanner;

import boardgame.Position;
import chess.ChessMatch;
import boardgame.Piece;
import boardgame.Board;

public class App {
    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);

        ChessMatch chessMatch = new ChessMatch();
        UI.printBoard(chessMatch.getPieces());

        sc.close();
    }
}
