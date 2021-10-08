package jdbc.helpers;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.github.vertical_blank.sqlformatter.core.FormatConfig;
import com.github.vertical_blank.sqlformatter.languages.Dialect;
import jdbc.DisplayResults;
import jdbc.Query;
import jdbc.QueryPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Shared {

    public static void registerFrame(final JFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                Constants.activeFrames.remove(frame);
                e.getWindow().dispose();
            }
        });

        Constants.activeFrames.add(frame);
    }

    public static void executeQuery(String sql, String db, Class<?> sender) {
        new Thread(() -> {
            try {
                final long timeout = 30000;
                final long startTime = System.currentTimeMillis();

                String toRun = "USE " + db +"\n" + sql;
                Query query = new Query();
                query.runCustomQuery(toRun);

                long currentTime = System.currentTimeMillis();
                while (currentTime - startTime < timeout) {
                    Thread.sleep(100);
                    currentTime = System.currentTimeMillis();

                    if (query.isExceptionThrown() || query.isComplete())
                        break;
                }

                if (query.isExceptionThrown()) {
                    Messages.exception(query.getExceptionMessage());
                }
                else if (!query.isComplete()) {
                    Messages.error("Unable to complete Query.\n@"+sender.getSimpleName());
                }
                else {
                    QueryResults results = query.getResults();
                    results.setDB(db);

                    DisplayResults display = new DisplayResults(results);
                }

            } catch (Exception e) {
                Messages.exception("Unable to open table.\n@"+sender.getSimpleName());
                e.printStackTrace();
            }
        }).start();
    }

    public static void formatQuery(QueryPanel queryPanel) {
        int prev = queryPanel.codeArea.getCaretPosition();
        String full = queryPanel.codeArea.getText();
        String selected = queryPanel.codeArea.getSelectedText();

        if (selected == null || selected.length() == 0)
            selected = full.trim();

        String fixed = selected.replaceAll("\\[","\"").replaceAll("\\]","\"");
        System.out.println(fixed);

        String sql = SqlFormatter.of(Dialect.Redshift).format(fixed, Settings.config);

        int start = full.indexOf(selected);
        int pos = prev == start ? prev : start;

        queryPanel.codeArea.setText(full.replace(selected, sql));
        queryPanel.codeArea.setCaretPosition(pos);
        queryPanel.updateNumberLine();
    }

    public static void saveQuery(QueryPanel queryPanel) {
        File selectedFile = getSelectedFile();
        if (selectedFile == null)
            return;

        String name = selectedFile.getName().replace(" ", "_");
        if (name.indexOf(".sql") != name.length()-4) {
            name += ".sql";
        }

        File writeableFile = new File(selectedFile.getParentFile(), name);
        writeTo(writeableFile, "USE "+queryPanel.getSelectedDB()+"\n"+queryPanel.codeArea.getText());

        if (queryPanel.parent == QueryPanel.TABBED_PARENT)
            Constants.jdbc.tabbedPane.setTitleAt(Constants.jdbc.tabbedPane.getSelectedIndex(), name);
    }

    public static File getSelectedFile(){
        JFileChooser fileChooser = new JFileChooser(Settings.jdbcFolder);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.sql", "sql");
        fileChooser.setFileFilter(filter);
        fileChooser.showSaveDialog(null);

        return fileChooser.getSelectedFile();
    }

    private static void writeTo(File file, String sql) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(sql);
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
