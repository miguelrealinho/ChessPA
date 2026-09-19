package pt.isec.pa.chess.model.data.saves;

import pt.isec.pa.chess.model.data.ChessGame;

import java.io.*;

/**
 * Utility class for saving and loading ChessGame objects to and from disk.
 * This class provides static methods to serialize a ChessGame object
 * to a file and to deserialize a ChessGame object from a file.
 *
 * The class has a private constructor to prevent instantiation.
 */
public class SaveLoadManager {

    /**
     * Private constructor to prevent instantiation.
     */
    private SaveLoadManager() {}

    /**
     * Saves the given ChessGame object to a file named "chess_save.dat".
     *
     * @param chessGame The ChessGame object to be saved. Must implement Serializable.
     * @return The file path where the game was saved ("chess_save.dat") if successful,
     *         or null if an error occurred during saving.
     */
    public static String saveGame(ChessGame chessGame) {
        String filePath = "chess_save.dat";
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(chessGame);
            System.out.println("Game saved to " + filePath);
            return filePath;
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
            return null;
        }
    }

    /**
     * Loads a ChessGame object from the specified file path.
     *
     * @param filePath The path to the file containing the saved ChessGame.
     * @return The loaded ChessGame object if successful, or null if an error occurred
     *         during loading or if the file does not contain a valid ChessGame object.
     */
    public static ChessGame loadGame(String filePath) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (ChessGame) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game: " + e.getMessage());
            return null;
        }
    }
}

