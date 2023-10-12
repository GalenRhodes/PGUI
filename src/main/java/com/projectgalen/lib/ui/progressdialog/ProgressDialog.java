package com.projectgalen.lib.ui.progressdialog;

import com.projectgalen.lib.ui.UIButtonChoice;
import com.projectgalen.lib.ui.errors.ProgressDialogException;
import com.projectgalen.lib.utils.ProgressReporter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings("unused")
public class ProgressDialog extends JDialog implements ProgressReporter {

    protected JPanel         contentPane;
    protected JButton        buttonOK;
    protected JProgressBar   progressBar;
    protected JLabel         message;
    protected UIButtonChoice buttonChoice = UIButtonChoice.None;
    protected JLabel         finalMessageLabel;
    private static Method    $$$cachedGetBundleMethod$$$ = null;

    public ProgressDialog(@NotNull String title,
                          @NotNull String message,
                          @Range(from = 0, to = Integer.MAX_VALUE) int minValue,
                          @Range(from = 0, to = Integer.MAX_VALUE) int maxValue,
                          @Range(from = 0, to = Integer.MAX_VALUE) int initialValue) {
        super();
        setTitle(title);
        setModal(true);
        setup(message, minValue, maxValue, initialValue);
    }

    public ProgressDialog(@NotNull JFrame frame,
                          @NotNull String title,
                          @NotNull String message,
                          @Range(from = 0, to = Integer.MAX_VALUE) int minValue,
                          @Range(from = 0, to = Integer.MAX_VALUE) int maxValue,
                          @Range(from = 0, to = Integer.MAX_VALUE) int initialValue) {
        super(frame, title, true);
        setup(message, minValue, maxValue, initialValue);
    }

    public ProgressDialog(@NotNull JDialog dialog,
                          @NotNull String title,
                          @NotNull String message,
                          @Range(from = 0, to = Integer.MAX_VALUE) int minValue,
                          @Range(from = 0, to = Integer.MAX_VALUE) int maxValue,
                          @Range(from = 0, to = Integer.MAX_VALUE) int initialValue) {
        super(dialog, title, true);
        setup(message, minValue, maxValue, initialValue);
    }

    public UIButtonChoice getButtonChoice() {
        return buttonChoice;
    }

    public @NotNull String getMessage() {
        return message.getText();
    }

    public int getProgress() {
        return progressBar.getValue();
    }

    public int getProgressMax() {
        return progressBar.getMaximum();
    }

    public int getProgressMin() {
        return progressBar.getMinimum();
    }

    public String getProgressText() {
        return progressBar.getString();
    }

    public boolean isIndeterminate() {
        return progressBar.isIndeterminate();
    }

    public void setIndeterminate(boolean isIndeterminate) {
        SwingUtilities.invokeLater(() -> progressBar.setIndeterminate(isIndeterminate));
    }

    public void setMessage(@NotNull String msg) {
        SwingUtilities.invokeLater(() -> message.setText(msg));
    }

    public void setProgress(int value) {
        SwingUtilities.invokeLater(() -> progressBar.setValue(value));
    }

    public void setProgressMax(int maxValue) {
        SwingUtilities.invokeLater(() -> progressBar.setMaximum(maxValue));
    }

    public void setProgressMin(int minValue) {
        SwingUtilities.invokeLater(() -> progressBar.setMinimum(minValue));
    }

    public void setProgressText(@NotNull String text) {
        SwingUtilities.invokeLater(() -> progressBar.setString(text));
    }

    private String $$$getMessageFromBundle$$$(String path, String key) {
        ResourceBundle bundle;
        try {
            Class<?> thisClass = this.getClass();
            if($$$cachedGetBundleMethod$$$ == null) {
                Class<?> dynamicBundleClass = thisClass.getClassLoader().loadClass("com.intellij.DynamicBundle");
                $$$cachedGetBundleMethod$$$ = dynamicBundleClass.getMethod("getBundle", String.class, Class.class);
            }
            bundle = (ResourceBundle)$$$cachedGetBundleMethod$$$.invoke(null, path, thisClass);
        }
        catch(Exception e) {
            bundle = ResourceBundle.getBundle(path);
        }
        return bundle.getString(key);
    }

    private void onOK() {
        buttonChoice = UIButtonChoice.OK;
        dispose();
    }

    private void setFailedMessage(@NotNull String err) {
        SwingUtilities.invokeLater(() -> {
            finalMessageLabel.setForeground(Color.red);
            finalMessageLabel.setText(String.format("An Error Occurred: %s", err));
        });
    }

    private void setSuccessMessage() {
        SwingUtilities.invokeLater(() -> {
            finalMessageLabel.setForeground(new Color(0, 128, 0));
            finalMessageLabel.setText("Success!");
        });
    }

    private void setup(@NotNull String message, int minValue, int maxValue, int initialValue) {
        if(minValue > maxValue) {
            int i = minValue;
            minValue = maxValue;
            maxValue = i;
        }
        if(initialValue < minValue) initialValue = minValue;
        if(initialValue > maxValue) initialValue = maxValue;

        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        buttonOK.addActionListener(e -> onOK());
        progressBar.setMinimum(minValue);
        progressBar.setMaximum(maxValue);
        progressBar.setValue(initialValue);
        boolean indeterminate = ((minValue == 0) && (maxValue == 0));
        progressBar.setIndeterminate(indeterminate);
        progressBar.setString(indeterminate ? "..." : null);
        this.message.setText(message);
        progressBar.setStringPainted(true);
        progressBar.setMinimumSize(new Dimension(progressBar.getMinimumSize().width, (int)(progressBar.getMinimumSize().height * 1.5)));
        finalMessageLabel.setText(" ");
        pack();
        setMinimumSize(new Dimension(400, getSize().height));
        pack();
    }

    public static void execute(Component owner, @NotNull String title, @NotNull ProgressDialog.ProgressExecuteDialogLambda lambda) {
        execute(owner, title, "", 0, 100, 0, false, lambda);
    }

    public static void execute(Component owner, @NotNull String title, boolean autoOK, @NotNull ProgressDialog.ProgressExecuteDialogLambda lambda) {
        execute(owner, title, "", 0, 100, 0, autoOK, lambda);
    }

    public static void execute(Component owner, @NotNull String title, @NotNull String message, @NotNull ProgressExecuteDialogLambda lambda) {
        execute(owner, title, message, 0, 100, 0, false, lambda);
    }

    public static void execute(Component owner, @NotNull String title, @NotNull String message, boolean autoOK, @NotNull ProgressExecuteDialogLambda lambda) {
        execute(owner, title, message, 0, 100, 0, autoOK, lambda);
    }

    public static void execute(Component owner, @NotNull String title, int minValue, int maxValue, int initialValue, @NotNull ProgressDialog.ProgressExecuteDialogLambda lambda) {
        execute(owner, title, "", minValue, maxValue, initialValue, false, lambda);
    }

    public static void execute(Component owner, @NotNull String title, int minValue, int maxValue, int initialValue, boolean autoOK, @NotNull ProgressDialog.ProgressExecuteDialogLambda lambda) {
        execute(owner, title, "", minValue, maxValue, initialValue, autoOK, lambda);
    }

    public static void execute(Component owner,
                               @NotNull String title,
                               @NotNull String message,
                               int minValue,
                               int maxValue,
                               int initialValue,
                               @NotNull ProgressDialog.ProgressExecuteDialogLambda lambda) {
        execute(owner, title, message, minValue, maxValue, initialValue, false, lambda);
    }

    public static void execute(Component owner,
                               @NotNull String title,
                               @NotNull String message,
                               int minValue,
                               int maxValue,
                               int initialValue,
                               boolean autoOK,
                               @NotNull ProgressDialog.ProgressExecuteDialogLambda lambda) {
        fetch(owner, title, message, minValue, maxValue, initialValue, autoOK, dlg -> {
            lambda.action(dlg);
            return null;
        });
    }

    public static <T> T fetch(Component owner, @NotNull String title, int minValue, int maxValue, int initialValue, boolean autoOK, @NotNull ProgressDialog.ProgressFetchDialogLambda<T> lambda) {
        return fetch(owner, title, "", minValue, maxValue, initialValue, autoOK, lambda);
    }

    public static <T> T fetch(Component owner, @NotNull String title, @NotNull String message, boolean autoOK, @NotNull ProgressDialog.ProgressFetchDialogLambda<T> lambda) {
        return fetch(owner, title, message, 0, 100, 0, autoOK, lambda);
    }

    public static <T> T fetch(Component owner, @NotNull String title, boolean autoOK, @NotNull ProgressDialog.ProgressFetchDialogLambda<T> lambda) {
        return fetch(owner, title, "", 0, 100, 0, autoOK, lambda);
    }

    public static <T> T fetch(Component owner, @NotNull String title, int minValue, int maxValue, int initialValue, @NotNull ProgressDialog.ProgressFetchDialogLambda<T> lambda) {
        return fetch(owner, title, "", minValue, maxValue, initialValue, false, lambda);
    }

    public static <T> T fetch(Component owner, @NotNull String title, @NotNull String message, @NotNull ProgressDialog.ProgressFetchDialogLambda<T> lambda) {
        return fetch(owner, title, message, 0, 100, 0, false, lambda);
    }

    public static <T> T fetch(Component owner, @NotNull String title, @NotNull ProgressDialog.ProgressFetchDialogLambda<T> lambda) {
        return fetch(owner, title, "", 0, 100, 0, false, lambda);
    }

    public static <T> T fetch(Component owner,
                              @NotNull String title,
                              @NotNull String message,
                              int minValue,
                              int maxValue,
                              int initialValue,
                              @NotNull ProgressDialog.ProgressFetchDialogLambda<T> lambda) {
        return fetch(owner, title, message, minValue, maxValue, initialValue, false, lambda);
    }

    public static <T> T fetch(Component owner,
                              @NotNull String title,
                              @NotNull String message,
                              int minValue,
                              int maxValue,
                              int initialValue,
                              boolean autoOK,
                              @NotNull ProgressDialog.ProgressFetchDialogLambda<T> lambda) {
        ProgressDialog dlg = create(owner, title, message, minValue, maxValue, initialValue);
        dlg.message.setText(message);
        dlg.pack();
        dlg.setResizable(true);
        dlg.setLocationRelativeTo(owner);

        Future<T> future = Executors.newSingleThreadExecutor().submit(() -> {
            SwingUtilities.invokeLater(() -> dlg.buttonOK.setEnabled(false));
            try {
                T results = lambda.action(dlg);
                dlg.setSuccessMessage();
                return results;
            }
            catch(Exception e) {
                dlg.setFailedMessage(e.toString());
                throw e;
            }
            finally {
                SwingUtilities.invokeLater(() -> dlg.buttonOK.setEnabled(true));
                if(autoOK) SwingUtilities.invokeLater(dlg::onOK);
            }
        });

        dlg.setVisible(true);
        try { return future.get(); } catch(Exception e) { throw new ProgressDialogException(e); }
    }

    @Contract("null, _, _, _, _, _ -> new")
    private static @NotNull ProgressDialog create(Component owner, @NotNull String title, @NotNull String message, int minValue, int maxValue, int initialValue) {
        if(owner instanceof JDialog) return new ProgressDialog((JDialog)owner, title, message, minValue, maxValue, initialValue);
        if(owner instanceof JFrame) return new ProgressDialog((JFrame)owner, title, message, minValue, maxValue, initialValue);
        return new ProgressDialog(title, message, minValue, maxValue, initialValue);
    }

    public interface ProgressExecuteDialogLambda {
        void action(@NotNull ProgressDialog progressDialog) throws Exception;
    }

    public interface ProgressFetchDialogLambda<T> {
        @Nullable T action(@NotNull ProgressDialog progressDialog) throws Exception;
    }
}
