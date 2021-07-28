package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;

public abstract class ChessPiece extends Piece {

    private Colour colour;
    private int moveCount;
    

    public ChessPiece(Board board, Colour colour) {
        super(board);
        this.colour = colour;
    }

    public Colour getColour() {
        return this.colour;
    }

    public void increaseMoveCount() {
        moveCount++;
    }

    public void decreaseMoveCount() {
        moveCount --;
    }
    public int getMoveCount() {
        return this.moveCount;
    }

    protected boolean isThereOpponentPiece(Position position) {
        ChessPiece p = (ChessPiece) getBoard().piece(position);
        return p != null && p.getColour() != colour;
    }

    public ChessPosition getChessPosition() {
        return ChessPosition.fromPosition(position);
    }

}