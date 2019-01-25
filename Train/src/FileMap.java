import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;

/**
 * Used to access the file names within resource folder.
 */
public class FileMap extends HashMap<String, String> {

    // There is a possibility that an object may modify this HasMap using put()

    private static FileMap instance;
    private static String resource = "";

    private FileMap() {
        initMap();
    }

    public static FileMap getInstance() {
        if (instance == null) {
            instance = new FileMap();
        }
        return instance;
    }

    /**
     * creates proper file names for certain types of tracks.
     */
    private void initMap() {
        put("trainTrack", makeFileName("trainTrack.png"));
        put("lockTrack", makeFileName("lockTrack.png"));
        put("findTrack", makeFileName("findTrack.png"));
        put("switchTrack", makeFileName("switchTrack.png"));
        put("station", makeFileName("station.png"));
        put("train", makeFileName("train.png"));
        put("greenLight", makeFileName("greenLight.png"));
        put("redLight", makeFileName("redLight.png"));
    }

    /**
     * creates complete relative path according to given file.
     *
     * @param file name of the file.
     * @return
     */
    private String makeFileName(String file) {
        String fs = System.getProperty("file.separator");
        return resource + file;
    }

    private String getFileName(String str) {
        return this.get(str);
    }

    public ImageView getView(String str) {
        return new ImageView(new Image(getClass().getClassLoader().getResourceAsStream((get(str)))));
    }

}
