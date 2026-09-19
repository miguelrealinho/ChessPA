package pt.isec.pa.chess.ui.res;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import pt.isec.pa.chess.model.ModelLog;


/**
 * Utility class for managing sound playback in the chess application.
 *
 * <p>This class provides static methods to play single sound effects or sequences of
 * sounds. It handles loading sound files from the resources and manages the media
 * player instance to play sounds without overlapping.
 *
 * <p>Designed as a singleton utility class with a private constructor to prevent
 * instantiation.
 *
 * @version 1.0
 *
 *  @author José Moreira 2022132718
 *  @author Miguel Realinho 2022132718
 *  @author Gonçalo Oliveira 2022143112
 */


public class SoundManager {

    // Prevent instantiation
    private SoundManager() {}

    private static MediaPlayer mp;


    /**
     * Plays a single sound effect from the resource path "sounds/en/" with the specified filename.
     *
     * <p>If a sound is currently playing, it will be stopped before the new sound plays.
     *
     * @param filename The name of the sound file (without extension) to play.
     * @return {@code true} if the sound started playing successfully; {@code false} otherwise
     *         (e.g., if the file was not found or an error occurred).
     */

    public static boolean play(String filename) {
        try {
            var url = SoundManager.class.getResource("sounds/en/" + filename + ".mp3");
            if (url == null) return false;
            String path = url.toExternalForm();
            Media music = new Media(path);

            if (mp != null && mp.getStatus() == MediaPlayer.Status.PLAYING)
                mp.stop();

            mp = new MediaPlayer(music);
            mp.setStartTime(Duration.ZERO);
            mp.setStopTime(music.getDuration());
            mp.setAutoPlay(true);
        } catch (Exception e) {
            ModelLog.getInstance().addLog("Error playing sound file.");
            return false;
        }
        return true;
    }

    /**
     * Plays a sequence of sounds in order, each starting after the previous one finishes.
     *
     * @param filenames The array of sound filenames (without extension) to play sequentially.
     */

    public static void playSequence(String... filenames) {
        playSequenceRecursive(0, filenames);
    }


    /**
     * Helper method that recursively plays each sound in the sequence.
     *
     * @param index The current index in the filenames array.
     * @param filenames The array of sound filenames.
     */

    private static void playSequenceRecursive(int index, String[] filenames) {
        if (index >= filenames.length) return;

        try {
            var url = SoundManager.class.getResource("sounds/en/" + filenames[index] + ".mp3");
            if (url == null) return;

            String path = url.toExternalForm();
            Media media = new Media(path);
            MediaPlayer player = new MediaPlayer(media);

            player.setOnEndOfMedia(() -> {
                playSequenceRecursive(index + 1, filenames); // play next after this one ends
            });

            player.play();
        } catch (Exception e) {
            ModelLog.getInstance().addLog("Error playing sound file: " + filenames[index]);
        }
    }
}
