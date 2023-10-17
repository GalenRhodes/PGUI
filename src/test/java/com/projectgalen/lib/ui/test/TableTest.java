package com.projectgalen.lib.ui.test;

import com.projectgalen.lib.ui.UI;
import com.projectgalen.lib.ui.components.table.PGJTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

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
    }

    private void onCancel() {
        pack();
    }

    private void onOK() {
        dispose();
        System.exit(0);
    }

    public static void main(String[] args) {
        try { UI.setFlatLaf(); } catch(UnsupportedLookAndFeelException e) { e.printStackTrace(System.err); }
        // try { UI.setLookAndFeel(BuiltInLookAndFeelProfiles.Nimbus); } catch(Exception e) { e.printStackTrace(System.err); }
        // try { UI.setLookAndFeel(BuiltInLookAndFeelProfiles.Motif); } catch(Exception e) { e.printStackTrace(System.err); }
        // try { UI.setLookAndFeel(BuiltInLookAndFeelProfiles.Aqual); } catch(Exception e) { e.printStackTrace(System.err); }
        // try { UI.setLookAndFeel(BuiltInLookAndFeelProfiles.Metal); } catch(Exception e) { e.printStackTrace(System.err); }
        // try { UI.setLookAndFeel(BuiltInLookAndFeelProfiles.Windows); } catch(Exception e) { e.printStackTrace(System.err); }
        // try { UI.setLookAndFeel(BuiltInLookAndFeelProfiles.WindowsClassic); } catch(Exception e) { e.printStackTrace(System.err); }

        for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            System.out.printf("%20s -> %s\n", info.getName(), info.getClassName());
        }
        System.out.print('\n');
        SwingUtilities.invokeLater(() -> {
            TableTest window = new TableTest(TestData.createTestData(7));
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
            //dumpUIDefaults(true);
            dumpUIDefaults(false);
        });
    }

    private static void dumpUIDefaults(@SuppressWarnings("SameParameterValue") boolean toConsole) {
        if(toConsole) dumpUIDefaults(new PrintWriter(System.out));
        else {
            String fileName = "UIManager_keys_%s.txt".formatted(UIManager.getLookAndFeel().getName().replaceAll("/", "_").replaceAll("\\\\", "_"));
            try(PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("documentation/%s".formatted(fileName)), StandardCharsets.UTF_8))) {
                dumpUIDefaults(writer);
            }
            catch(IOException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    private static void dumpUIDefaults(PrintWriter writer) {
        int          maxA    = 0;
        int          maxB    = 0;
        List<String> allKeys = UIManager.getDefaults().keySet().stream().map(o -> Objects.toString(o, "")).sorted().toList();
        // List<String>        allKeys = UIManager.getDefaults().keySet().stream().map(o -> Objects.toString(o, "")).filter(k -> (k.startsWith("Table.") || k.startsWith("Label."))).sorted().toList();
        Map<String, String> map = new TreeMap<>();

        for(String key : allKeys) {
            String value = Objects.toString(UIManager.get(key)).replaceAll("(\\r\\n|\\r|\\n)", " ").replaceAll("\\t", " ");
            map.put(key, value);
            maxA = Math.max(maxA, key.length());
            maxB = Math.max(maxB, value.length());
        }
        String fmt = "| %%%ds | %%-%ds |\n".formatted(maxA, maxB);

        writer.printf("+-%s-+-%s-+\n", "-".repeat(maxA), "-".repeat(maxB));
        for(String key : allKeys) writer.printf(fmt, key, map.get(key));
        writer.printf("+-%s-+-%s-+\n", "-".repeat(maxA), "-".repeat(maxB));
    }

    private final class MyWindowAdapter extends WindowAdapter {
        public @Override void windowClosing(WindowEvent e) {
            dispose();
        }
    }
}
