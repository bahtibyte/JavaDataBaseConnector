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

    public static Icon serverIcon16;
    public static Icon dbIcon16;
    public static Icon schemaIcon;
    public static Icon tableIcon;


    public static Icon formatIcon;
    public static Icon pasteIcon;
    public static Icon copyIcon;
    public static Icon cutIcon;

    public static Icon viewIcon;
    public static Icon filterIcon;

    public static Icon openExcelIcon;
    public static Icon exportAllIcon;
    public static Icon exportSelIcon;

    public static Image magnifyingImage;
    public static Image tableImage;
    public static Image queryEditorImage;

    public static void initialize() {
        settingsIcon = loadIcon("assets/icons8-settings-16.png");
        executeQueryIcon = loadIcon("assets/icons8-circled-play-16.png");
        newQueryIcon = loadIcon("assets/icons8-new-file-16.png");
        openFileIcon = loadIcon("assets/icons8-opened-folder-16.png");
        saveQueryIcon = loadIcon("assets/icons8-floppy-16.png");
        connectIcon = loadIcon("assets/icons8-connect-16.png");
        refreshIcon = loadIcon("assets/icons8-refresh-16.png");

        serverIcon16 = loadIcon("assets/icons8-server-16.png");
        dbIcon16 = loadIcon("assets/icons8-database-16.png");

        schemaIcon = loadIcon("assets/icons8-schema-16.png");
        tableIcon = loadIcon("assets/icons8-table-16.png");

        formatIcon = loadIcon("assets/icons8-format-16.png");
        pasteIcon = loadIcon("assets/icons8-paste-16.png");
        copyIcon = loadIcon("assets/icons8-copy-16.png");
        cutIcon = loadIcon("assets/icons8-cut-16.png");

        viewIcon = loadIcon("assets/icons8-view-16.png");
        filterIcon = loadIcon("assets/icons8-filter-16.png");

        openExcelIcon = loadIcon("assets/icons8-excel-16.png");
        exportSelIcon = loadIcon("assets/icons8-export-sel-16.png");
        exportAllIcon = loadIcon("assets/icons8-export-all-16.png");

        magnifyingImage = loadImage("assets/icons8-magnifying-128.png");
        tableImage = loadImage("assets/icons8-data-sheet-128.png");
        queryEditorImage = loadImage("assets/icons8-editor-128.png");
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
