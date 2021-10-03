package jdbc.listeners;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.github.vertical_blank.sqlformatter.core.FormatConfig;
import com.github.vertical_blank.sqlformatter.languages.Dialect;
import jdbc.QueryPanel;
import jdbc.helpers.Constants;
import jdbc.helpers.IconHelper;
import jdbc.helpers.Settings;
import jdbc.helpers.Shared;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class CodeAreaMouseListener implements MouseListener, ActionListener {

    private QueryPanel queryPanel;
    private JFrame parentFrame;

    final JPopupMenu popup;

    final JMenuItem cutItem;
    final JMenuItem copyItem;
    final JMenuItem pasteItem;
    final JMenuItem formatItem;

    public CodeAreaMouseListener(JFrame parentFrame, QueryPanel queryPanel) {
        this.parentFrame = parentFrame;
        this.queryPanel = queryPanel;

        popup = new JPopupMenu("Edit");

        formatItem = new JMenuItem("Format");
        formatItem.addActionListener(this);
        formatItem.setIcon(IconHelper.formatIcon);
        popup.add(formatItem);

        popup.addSeparator();

        pasteItem = new JMenuItem("Paste");
        pasteItem.addActionListener(this);
        pasteItem.setIcon(IconHelper.pasteIcon);
        popup.add(pasteItem);

        copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(this);
        copyItem.setIcon(IconHelper.copyIcon);
        popup.add(copyItem);

        cutItem = new JMenuItem("Cut");
        cutItem.addActionListener(this);
        cutItem.setIcon(IconHelper.cutIcon);
        popup.add(cutItem);
    }

    private void singleRightClick() {
        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        SwingUtilities.convertPointFromScreen(b, parentFrame);

        popup.show(parentFrame, (int) b.getX(), (int) b.getY());
    }

    private void format() {
        Shared.formatQuery(this.queryPanel);
    }

    private void paste() {
        int prev = this.queryPanel.codeArea.getCaretPosition();
        String data = getClipboard();

        if (data == null || data.length() == 0)
            return;

        String fullText = this.queryPanel.codeArea.getText();
        String selected = this.queryPanel.codeArea.getSelectedText();

        if (selected == null || selected.length() == 0) {
            this.queryPanel.codeArea.insert(data, prev);
            this.queryPanel.codeArea.setCaretPosition(prev+data.length());
        }
        else {
            int start = fullText.indexOf(selected);
            int pos = prev == start ? prev : start;

            this.queryPanel.codeArea.setText(fullText.replace(selected, data));
            this.queryPanel.codeArea.setCaretPosition(pos+data.length());
        }

        this.queryPanel.updateNumberLine();
    }

    private void cut() {
        int prev = this.queryPanel.codeArea.getCaretPosition();
        String full = this.queryPanel.codeArea.getText();
        String selected = this.queryPanel.codeArea.getSelectedText();

        if (selected == null || selected.length() == 0)
            return;

        int start = full.indexOf(selected);
        int pos = prev == start ? prev : start;

        this.queryPanel.codeArea.setText(full.replace(selected, ""));
        this.queryPanel.codeArea.setCaretPosition(pos);
        this.queryPanel.updateNumberLine();

        this.copyToClipboard(selected);
    }

    private void copy() {
        String selected = this.queryPanel.codeArea.getSelectedText();
        this.copyToClipboard(selected);
    }

    private String getClipboard() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "[Unable to access clipboard]";
    }

    private void copyToClipboard(String selected) {
        StringSelection selection = new StringSelection(selected);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(formatItem))
            format();

        if (source.equals(pasteItem))
            paste();

        if (source.equals(copyItem))
            copy();

        if (source.equals(cutItem))
            cut();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 3 && e.getClickCount() == 1)
            this.singleRightClick();
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }
}
