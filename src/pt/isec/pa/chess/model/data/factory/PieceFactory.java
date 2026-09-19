package pt.isec.pa.chess.model.data.factory;

import pt.isec.pa.chess.model.data.pieces.*;


/**
 * Factory class for creating chess pieces.
 * <p>
 * Provides static methods to instantiate specific {@link Piece} objects
 * based on a {@link PieceFactory.PieceType} enum or a compact string representation.
 * <p>
 * This class uses the Factory design pattern to encapsulate the creation logic
 * of chess pieces and decouple client code from direct instantiation of piece subclasses.
 *
 * @see Piece
 * @see Pawn
 * @see Queen
 * @see King
 * @see Rook
 * @see Bishop
 * @see Knight
 *
 * @version 1.0
 *
 * @author José Moreira 2022132718
 * @author Miguel Realinho 2022132718
 * @author Gonçalo Oliveira 2022143112
 */

public class PieceFactory {

    /**
     * Enumeration representing the different types of chess pieces.
     */
    public enum PieceType{
        BISHOP, KING, KNIGHT, PAWN, QUEEN, ROOK;
    }

    /**
     * Private constructor to prevent instantiation of the PieceFactory class.
     */
    private PieceFactory() {}

    /**
     * Creates a new piece of the specified type, at the given coordinates and color.
     *
     * @param type the type of piece to create
     * @param x the column of the piece (e.g., 'a'–'h')
     * @param y the row of the piece (1–8)
     * @param c the color of the piece (white or black)
     * @return the corresponding {@link Piece} instance
     */
    public static Piece createPiece(PieceType type, char x, int y, Piece.Color c){
        return switch (type) {
            case BISHOP -> new Bishop(x, y, c);
            case KING -> new King(x, y, c);
            case KNIGHT -> new Knight(x, y, c);
            case PAWN -> new Pawn(x, y, c);
            case QUEEN -> new Queen(x, y, c);
            case ROOK -> new Rook(x, y, c);
        };
    }
    /**
     * Creates a new piece based on a compact string representation.
     * <p>
     * The string should follow the format <code>"Pc3"</code> where:
     * <ul>
     *   <li>The first character indicates the piece and its color (uppercase for white, lowercase for black)</li>
     *   <li>The second character is the column (e.g., 'a'–'h')</li>
     *   <li>The third character is the row (e.g., '1'–'8')</li>
     * </ul>
     *
     * @param info the compact string representing the piece
     * @return the corresponding {@link Piece}, or <code>null</code> if the format is invalid
     */

    public static Piece createPiece(String info) {
        char[] pieceInfo = info.toCharArray();
        int row = Character.getNumericValue(pieceInfo[2]);
        return switch (pieceInfo[0]) {
            case 'p' -> new Pawn(pieceInfo[1], row, Piece.Color.BLACK);
            case 'P' -> new Pawn(pieceInfo[1], row, Piece.Color.WHITE);
            case 'n' -> new Knight(pieceInfo[1], row, Piece.Color.BLACK);
            case 'N' -> new Knight(pieceInfo[1], row, Piece.Color.WHITE);
            case 'b' -> new Bishop(pieceInfo[1], row, Piece.Color.BLACK);
            case 'B' -> new Bishop(pieceInfo[1], row, Piece.Color.WHITE);
            case 'r' -> new Rook(pieceInfo[1], row, Piece.Color.BLACK);
            case 'R' -> new Rook(pieceInfo[1], row, Piece.Color.WHITE);
            case 'k' -> new King(pieceInfo[1], row, Piece.Color.BLACK);
            case 'K' -> new King(pieceInfo[1], row, Piece.Color.WHITE);
            case 'q' -> new Queen(pieceInfo[1], row, Piece.Color.BLACK);
            case 'Q' -> new Queen(pieceInfo[1], row, Piece.Color.WHITE);
            default -> null;
        };
    }


}
