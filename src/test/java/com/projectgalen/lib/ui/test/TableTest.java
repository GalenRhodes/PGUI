package com.projectgalen.lib.ui.test;

import com.projectgalen.lib.ui.UI;
import com.projectgalen.lib.ui.components.table.PGJTable;
import com.projectgalen.lib.utils.refs.IntegerRef;
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
    public static final int TEST_RECORD_COUNT = 1000;

    protected JPanel  contentPane;
    protected JButton quitButton;
    protected JButton packButton;
    protected PGJTable<TestData> table;

    public TableTest(@NotNull List<TestData> testData) {
        setContentPane(contentPane);
        getRootPane().setDefaultButton(quitButton);
        quitButton.addActionListener(e -> onQuit());
        packButton.addActionListener(e -> onPack());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new MyWindowAdapter());
        contentPane.registerKeyboardAction(e -> onPack(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        table.setRowModel(new TestRowModel());
        table.setData(testData);
        SwingUtilities.invokeLater(() -> table.setColumnSizePercentages(new double[] { 0.5, 0.1, 0.2 }));
    }

    private void onPack() {
        pack();
    }

    private void onQuit() {
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

        SwingUtilities.invokeLater(() -> {
            TableTest window = new TableTest(TestData.createTestData(TEST_RECORD_COUNT));
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
        // SwingUtilities.invokeLater(TableTest::dumpAvailableLafs);
        // SwingUtilities.invokeLater(() -> dumpUIDefaults(true));
        // SwingUtilities.invokeLater(() -> dumpUIDefaults(false));
    }

    private static void dumpAvailableLafs() {
        for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) System.out.printf("%20s -> %s\n", info.getName(), info.getClassName());
        System.out.print('\n');
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
        new Thread(() -> {
            try {
                IntegerRef          maxA = IntegerRef.getReference(0);
                IntegerRef          maxB = IntegerRef.getReference(0);
                Map<String, String> map  = new TreeMap<>();

                UIManager.getDefaults().keySet().stream().map(Objects::toString).forEach(key -> {
                    String value = Objects.toString(UIManager.get(key)).replaceAll("(\\r\\n|\\r|\\n|\\t)", " ");
                    map.put(key, value);
                    maxA.setMax(key.length());
                    maxB.setMax(value.length());
                });
                String fmt = "| %%%ds | %%-%ds |\n".formatted(maxA.value, maxB.value);
                String bar = "+-%s-+-%s-+\n".formatted("-".repeat(maxA.value), "-".repeat(maxB.value));

                writer.print(bar);
                map.forEach((key, value) -> writer.printf(fmt, key, value));
                writer.print(bar);
            }
            finally {
                writer.flush();
            }
        }).start();
    }

    private final class MyWindowAdapter extends WindowAdapter {
        public @Override void windowClosing(WindowEvent e) {
            dispose();
        }
    }
}
