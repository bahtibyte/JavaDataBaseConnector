package jdbc.helpers;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class IconHelper {

    public static Icon settingsIcon;
    public static Icon executeQueryIcon;
    public static Icon newQueryIcon;
    public static Icon openFileIcon;
    public static Icon saveQueryIcon;
    public static Icon connectIcon;
    public static Icon refreshIcon;

    public static Image magnifyingImage;

    public static void initialize() {
        settingsIcon = loadIcon("assets/icons8-settings-16.png");
        executeQueryIcon = loadIcon("assets/icons8-circled-play-16.png");
        newQueryIcon = loadIcon("assets/icons8-new-file-16.png");
        openFileIcon = loadIcon("assets/icons8-opened-folder-16.png");
        saveQueryIcon = loadIcon("assets/icons8-floppy-16.png");
        connectIcon = loadIcon("assets/icons8-connect-16.png");
        refreshIcon = loadIcon("assets/icons8-refresh-16.png");


        magnifyingImage = loadImage("assets/icons8-magnifying-128.png");
    }

    private static Image loadImage(String url){
        try {
            return ImageIO.read(new File(url));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static Icon loadIcon(String url) {
        return new ImageIcon(url);
    }
}
