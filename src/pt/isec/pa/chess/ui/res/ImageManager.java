package pt.isec.pa.chess.ui.res;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Utility class responsible for loading and caching images used in the chess application.
 *
 * <p>This class provides static methods to retrieve images from the application's resources
 * or from external sources, ensuring that each image is loaded only once and cached for
 * efficient reuse throughout the application's lifecycle.
 *
 * <p>The internal cache is implemented as a {@link HashMap} mapping filenames to
 * {@link Image} instances.
 *
 * <p>Features include:
 * <ul>
 *   <li>Loading images bundled within the application’s resources under "images/pieces/".</li>
 *   <li>Loading images from external paths or URLs.</li>
 *   <li>Caching loaded images to avoid repeated disk or network access.</li>
 *   <li>Ability to purge cached images when no longer needed.</li>
 * </ul>
 *
 * <p>This class is designed as a singleton utility class with a private constructor to
 * prevent instantiation.
 *
 * @version 1.0
 *
 *  @author José Moreira 2022132718
 *  @author Miguel Realinho 2022132718
 *  @author Gonçalo Oliveira 2022143112
 */
public class ImageManager {
    private ImageManager() { }

    private static final HashMap<String, Image> images = new HashMap<>();

    /**
     * Retrieves an image from the application's internal resources, loading and caching it if necessary.
     *
     * @param filename The filename of the image resource (expected under "images/pieces/").
     * @return The loaded {@link Image}, or {@code null} if loading fails.
     */
    public static Image getImage(String filename) {
        Image image = images.get(filename);
        if (image == null)
            try (InputStream is = ImageManager.class.getResourceAsStream("images/pieces/" + filename)) {
                image = new Image(is);
                images.put(filename, image);
            } catch (Exception e) {
                return null;
            }
        return image;
    }

    /**
     * Retrieves an image from an external file path or URL, loading and caching it if necessary.
     *
     * @param filename The external file path or URL of the image.
     * @return The loaded {@link Image}, or {@code null} if loading fails.
     */
    public static Image getExternalImage(String filename) {
        Image image = images.get(filename);
        if (image == null)
            try {
                image = new Image(filename);
                images.put(filename, image);
            } catch (Exception e) {
                return null;
            }
        return image;
    }

    /**
     * Removes an image from the internal cache, allowing it to be garbage collected if no longer referenced.
     *
     * @param filename The filename or key of the cached image to remove.
     */
    public static void purgeImage(String filename) {
        images.remove(filename);
    }
}
