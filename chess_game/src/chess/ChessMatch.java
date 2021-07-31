package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import boardpieces.Bishop;
import boardpieces.King;
import boardpieces.Knight;
import boardpieces.Pawn;
import boardpieces.Queen;
import boardpieces.Rook;
import chess.chessExceptions.ChessException;

public class ChessMatch {

    private Board board;
    private int turn;
    private Colour currentPlayer;
    private boolean check;
    private boolean checkMate;

    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();


    public ChessMatch() {
        board = new Board(8, 8);
        turn = 1;
        currentPlayer = Colour.WHITE;
        initialSetup();
    }

    public int getTurn() {
        return this.turn;
    }

    public boolean getCheck() {
        return this.check;
    }

    public boolean getCheckMate() {
        return this.checkMate;
    }

    public Colour getCurrentPlayer() {
        return this.currentPlayer;
    }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i=0; i<board.getRows(); i++) {
            for (int j=0; j<board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat; 
    }

    private Colour opponent(Colour colour) {
        return (colour == Colour.WHITE) ? Colour.BLACK : Colour.WHITE;
    }

    private ChessPiece king(Colour colour) {
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColour() == colour).collect(Collectors.toList());
        for (Piece p : list) {
            if (p instanceof King) {
                return (ChessPiece) p;
            }
        }
        throw new IllegalStateException("There is no " + colour + " king on the board.");
    }

    private boolean testCheck(Colour colour) {
        Position kingPosition = king(colour).getChessPosition().toPosition();
        List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColour() == opponent(colour)).collect(Collectors.toList());
        for (Piece p : opponentPieces) {
            boolean[][] mat = p.possibleMoves();
            if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
                return true;
            }
        }
        return false;
    }

    private boolean testCheckMate(Colour colour) {
        if (!testCheck(colour)) {
            return false;
        }
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColour() == colour).collect(Collectors.toList());
        for (Piece p : list) {
            boolean[][] mat = p.possibleMoves();
            for (int i=0; i<board.getRows(); i++) {
                for (int j=0; j<board.getColumns(); j++) {
                    if (mat[i][j]) {
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturedPiece = makeMove(source, target);
                        boolean testCheck = testCheck(colour);
                        undoMove(source, target, capturedPiece);
                        if(!testCheck) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }

    private void initialSetup() {
        // rook instantiation
        placeNewPiece('a', 1, new Rook(board, Colour.WHITE));
        placeNewPiece('h', 1, new Rook(board, Colour.WHITE));
        // knight instantiation
        placeNewPiece('b', 1, new Knight(board, Colour.WHITE));
        placeNewPiece('g', 1, new Knight(board, Colour.WHITE));
        // bishop instantiation
        placeNewPiece('c', 1, new Bishop(board, Colour.WHITE));
        placeNewPiece('f', 1, new Bishop(board, Colour.WHITE));
        // king instantiation
        placeNewPiece('e', 1, new King(board, Colour.WHITE, this));
        // queen instantiation
        placeNewPiece('d', 1, new Queen(board, Colour.WHITE));
        // pawn instantiation
        placeNewPiece('a', 2, new Pawn(board, Colour.WHITE));
        placeNewPiece('b', 2, new Pawn(board, Colour.WHITE));
        placeNewPiece('c', 2, new Pawn(board, Colour.WHITE));
        placeNewPiece('d', 2, new Pawn(board, Colour.WHITE));
        placeNewPiece('e', 2, new Pawn(board, Colour.WHITE));
        placeNewPiece('f', 2, new Pawn(board, Colour.WHITE));
        placeNewPiece('g', 2, new Pawn(board, Colour.WHITE));
        placeNewPiece('h', 2, new Pawn(board, Colour.WHITE));

        // rook instantiation
        placeNewPiece('a', 8, new Rook(board, Colour.BLACK));
        placeNewPiece('h', 8, new Rook(board, Colour.BLACK));
        // knight instantiation
        placeNewPiece('b', 8, new Knight(board, Colour.BLACK));
        placeNewPiece('g', 8, new Knight(board, Colour.BLACK));
        // bishop instantiation
        placeNewPiece('c', 8, new Bishop(board, Colour.BLACK));
        placeNewPiece('f', 8, new Bishop(board, Colour.BLACK));
        // king instantiation
        placeNewPiece('e', 8, new King(board, Colour.BLACK, this));
        // queen instantiation
        placeNewPiece('d', 8, new Queen(board, Colour.BLACK));
        // pawn instantiation
        placeNewPiece('a', 7, new Pawn(board, Colour.BLACK));
        placeNewPiece('b', 7, new Pawn(board, Colour.BLACK));
        placeNewPiece('c', 7, new Pawn(board, Colour.BLACK));
        placeNewPiece('d', 7, new Pawn(board, Colour.BLACK));
        placeNewPiece('e', 7, new Pawn(board, Colour.BLACK));
        placeNewPiece('f', 7, new Pawn(board, Colour.BLACK));
        placeNewPiece('g', 7, new Pawn(board, Colour.BLACK));
        placeNewPiece('h', 7, new Pawn(board, Colour.BLACK));
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);

        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece);
            throw new ChessException("You cannot put/leave yourself in check!");
        }

        check = (testCheck(opponent(currentPlayer))) ? true : false;

        if (testCheckMate(opponent(currentPlayer))) {
            checkMate = true;
        }
        else {
            nextTurn();
        }
        return (ChessPiece) capturedPiece;
    }

    private void validateSourcePosition(Position position) {
        if (!board.thereIsAPiece(position)) {
            throw new ChessException("There is no piece on source position!");
        }
        if (currentPlayer != ((ChessPiece)board.piece(position)).getColour()) {
            throw new ChessException("The chosen piece is not yours!");
        }
        if (!board.piece(position).isThereAnyPossibleMove()) {
            throw new ChessException("There are no possible moves for the chosen piece!");
        }
    }

    private void validateTargetPosition(Position source, Position target) {
        if (!board.piece(source).possibleMove(target)) {
            throw new ChessException("The chosen piece cannot be moved to target position!");
        }
    }

    private Piece makeMove(Position source, Position target) {
        ChessPiece p = (ChessPiece) board.removePiece(source);
        p.increaseMoveCount();
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p, target);

        if (capturedPiece != null) {
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }
        // special move kingside castling
        if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
            Position sourceR = new Position(source.getRow(), source.getColumn() + 3);
            Position targetR = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceR);
            board.placePiece(rook, targetR);
            rook.increaseMoveCount();
        }
        // special move queenside castling
        if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
            Position sourceR = new Position(source.getRow(), source.getColumn() - 4);
            Position targetR = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceR);
            board.placePiece(rook, targetR);
            rook.increaseMoveCount();
        }
        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece) {
        ChessPiece p = (ChessPiece) board.removePiece(target);
        p.decreaseMoveCount();
        board.placePiece(p, source);
        
        if (capturedPiece != null) {
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }
        // special move kingside castling
        if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
            Position sourceR = new Position(source.getRow(), source.getColumn() + 3);
            Position targetR = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetR);
            board.placePiece(rook, sourceR);
            rook.decreaseMoveCount();
        }
        // special move queenside castling
        if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
            Position sourceR = new Position(source.getRow(), source.getColumn() - 4);
            Position targetR = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetR);
            board.placePiece(rook, sourceR);
            rook.decreaseMoveCount();
        }
    }

    private void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Colour.WHITE) ? Colour.BLACK : Colour.WHITE;
    }
    
}
