package testing;

import jdbc.QueryPanel;
import jdbc.helpers.Shared;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import java.awt.*;
import java.io.File;
import java.util.*;

public class UITest {

    public static void main(String arg[]) {
        main1(arg);
    }

    private static void createSQLView(){
        JFrame sqlView = new JFrame();
        sqlView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        sqlView.setPreferredSize(new Dimension(500, 300));
        sqlView.setResizable(true);

        sqlView.getContentPane().add(new QueryPanel(sqlView, "Sample code", null, QueryPanel.JFRAME_PARENT));

        sqlView.pack();
        sqlView.setLocationRelativeTo(null);
        sqlView.setVisible(true);
    }

    public static void main1(String args[]) {
        UIManager.getIcon("FileView.directoryIcon");
        UIManager.getIcon("FileView.fileIcon");
        UIManager.getIcon("FileView.computerIcon");
        UIManager.getIcon("FileView.hardDriveIcon");
        UIManager.getIcon("FileView.floppyDriveIcon");

        UIManager.getIcon("FileChooser.newFolderIcon");
        UIManager.getIcon("FileChooser.upFolderIcon");
        UIManager.getIcon("FileChooser.homeFolderIcon");
        UIManager.getIcon("FileChooser.detailsViewIcon");
        UIManager.getIcon("FileChooser.listViewIcon");

        //BasicFileChooserUI

        UIDefaults defaults = UIManager.getDefaults();
        Set<Map.Entry<Object, Object>> set = defaults.entrySet();

        for (Map.Entry<Object, Object> s : set) {
            String name = s.getKey().toString();
            if (name.indexOf("Icon") == name.length()-4){
                System.out.println(s.getKey());
            }
        }

        Enumeration<Object> keysEnum = defaults.keys();
        ArrayList<Object> keyList = Collections.list(keysEnum);
        for (Object key : keyList) {
            if (defaults.getString(key) != null) {
                //System.out.println(key+" - "+defaults.getString("FileChooser.detailsViewIcon"));
            }
        }
    }
}
