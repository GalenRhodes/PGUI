package com.projectgalen.lib.ui.components.table.test;

import com.projectgalen.lib.ui.components.table.PGJTable;
import com.projectgalen.lib.ui.components.table.VSizePolicy;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class TableTest extends JFrame {
    private   JPanel             contentPane;
    private   JButton            buttonOK;
    private   JButton            buttonCancel;
    protected PGJTable<TestData> table;

    public TableTest(@NotNull List<TestData> testData) {
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new MyWindowAdapter());
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        table.setRowModel(new TestRowModel());
        table.setData(testData);
        table.setMaximumVisibleRows(10);
        table.setvSizePolicy(VSizePolicy.FitsRows);
    }

    private void onCancel() {
        // add your code here if necessary
        pack();
    }

    private void onOK() {
        // add your code here
        dispose();
        System.exit(0);
    }

    public static void main(String[] args) {
        // try { UI.setFlatLaf(); } catch(UnsupportedLookAndFeelException e) { e.printStackTrace(System.err); }
        SwingUtilities.invokeLater(() -> {
            TableTest dialog = new TableTest(TestData.createTestData(100));
            dialog.pack();
            dialog.setVisible(true);
        });
    }

    private final class MyWindowAdapter extends WindowAdapter {
        public @Override void windowClosing(WindowEvent e) {
            onCancel();
        }
    }
}
