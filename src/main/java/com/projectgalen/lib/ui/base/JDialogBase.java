package com.projectgalen.lib.ui.base;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: JDialogBase.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 26, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
// SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
// IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ===========================================================================

import com.projectgalen.lib.ui.annotations.RootPanel;
import com.projectgalen.lib.ui.interfaces.DialogButtonsInterface;
import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.errors.Errors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.util.Objects;

import static com.projectgalen.lib.utils.reflection.Reflection.callMethod;
import static com.projectgalen.lib.utils.reflection.Reflection.getFieldValue;
import static com.projectgalen.lib.utils.reflection.Reflection2.*;

@SuppressWarnings({ "unused", "RedundantCast" })
public abstract class JDialogBase extends JDialog {

    public static final String CONTENT_PANE_FIELD_NAME = "contentPane";

    protected final PGResourceBundle msgs;
    protected       int              exitCode = 0;

    protected JDialogBase(@NotNull String titleKey, @NotNull PGResourceBundle msgs) {
        super();
        setTitle(msgs.getString(titleKey));
        setModal(true);
        this.msgs = msgs;
    }

    protected JDialogBase(@NotNull Frame owner, @NotNull String titleKey, @NotNull PGResourceBundle msgs) {
        super(owner, msgs.getString(titleKey), true);
        this.msgs = msgs;
    }

    protected JDialogBase(@NotNull Dialog owner, @NotNull String titleKey, @NotNull PGResourceBundle msgs) {
        super(owner, msgs.getString(titleKey), true);
        this.msgs = msgs;
    }

    public int getExitCode() {
        return exitCode;
    }

    public abstract void setup(Object @NotNull ... args);

    protected void createUIComponents() { }

    protected @UnknownNullability JPanel getBaseContentPane() {
        Class<? extends JDialogBase> c = getClass();
        Class<RootPanel>             a = RootPanel.class;
        return (JPanel)getAnnotatedFields(c, a).findFirst().map(f -> getFieldValue(f, this)).orElseGet(() -> getAnnotatedMethods(c, a).findFirst().map(m -> callMethod(m, this)).orElse(null));
    }

    protected void onCancel() {
        setCodeAndExit(-1);
    }

    protected void onOK() {
        setCodeAndExit(1);
    }

    protected void postSetup() { }

    protected void preSetup()  { }

    protected void setCodeAndExit(int exitCode) {
        this.exitCode = exitCode;
        dispose();
    }

    protected void showErrorMessage(@NotNull String titleKey, @NotNull String messageKey) {
        JOptionPane.showMessageDialog(this, msgs.getString(messageKey), msgs.getString(titleKey), JOptionPane.ERROR_MESSAGE);
    }

    public static <T extends JDialogBase> @NotNull T create(@NotNull Class<T> dialogClass, @Nullable Component owner, @NotNull String titleKey, @NotNull PGResourceBundle msgs, Object @NotNull ... args) {
        Class<DialogButtonsInterface> dlgBtnCls = DialogButtonsInterface.class;
        T                             dlg       = create(dialogClass, owner, titleKey, msgs);
        JPanel                        cp        = dlg.getBaseContentPane();

        cp.registerKeyboardAction(e -> dlg.onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        dlg.setContentPane(cp);
        dlg.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        dlg.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dlg.onCancel(); }
        });
        getFields(dialogClass).filter(f -> dlgBtnCls.isAssignableFrom(f.getType())).map(f -> (DialogButtonsInterface)getFieldValue(f, dlg)).filter(Objects::nonNull).forEach(btns -> {
            dlg.getRootPane().setDefaultButton(btns.getButtonOK());
            btns.addCancelButtonListener(e -> dlg.onCancel());
            btns.addOKButtonListener(e -> dlg.onOK());
        });
        dlg.preSetup();
        dlg.setup(args);
        dlg.postSetup();
        dlg.pack();
        dlg.setResizable(false);
        dlg.setLocationRelativeTo(owner);
        dlg.setVisible(true);
        return dlg;
    }

    private static <T extends JDialogBase> @NotNull T create(@NotNull Class<T> dialogClass, @Nullable Component owner, @NotNull String titleKey, @NotNull PGResourceBundle msgs) {
        try {
            if(owner instanceof Dialog) return newInstance(dialogClass.getDeclaredConstructor(Dialog.class, String.class), (Dialog)owner, titleKey, msgs);
            if(owner instanceof Frame) return newInstance(dialogClass.getDeclaredConstructor(Frame.class, String.class), (Frame)owner, titleKey, msgs);
            return newInstance(dialogClass.getDeclaredConstructor(String.class), titleKey, msgs);
        }
        catch(Exception e) { throw Errors.makeRuntimeException(e); }
    }

    private static <T extends JDialogBase> @NotNull T newInstance(@NotNull Constructor<T> constructor, Object... args) throws Exception {
        constructor.setAccessible(true);
        return constructor.newInstance(args);
    }
}
