package jdbc.helpers;

import jdbc.DisplayResults;
import jdbc.Query;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

    public static void executeQuery(String sql, Class<?> sender) {
        new Thread(() -> {
            try {
                final long timeout = 1500;
                final long startTime = System.currentTimeMillis();

                Query query = new Query();
                query.runCustomQuery(sql);

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
                    DisplayResults display = new DisplayResults(query.getResults());
                }

            } catch (Exception e) {
                Messages.exception("Unable to open table.\n@"+sender.getSimpleName());
                e.printStackTrace();
            }
        }).start();
    }

}
