package com.projectgalen.lib.ui;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: UI.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 18, 2023
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

import com.formdev.flatlaf.FlatLightLaf;
import com.intellij.uiDesigner.core.GridConstraints;
import com.projectgalen.lib.ui.enums.BuiltInLookAndFeelProfiles;
import com.projectgalen.lib.ui.utils.Mnemonic;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static com.projectgalen.lib.utils.errors.Errors.makeRuntimeException;
import static com.projectgalen.lib.utils.errors.Errors.propagate;
import static java.awt.event.KeyEvent.VK_UNDEFINED;

@SuppressWarnings("unused")
public final class UI {
    public static final int CANGROW_WANTGROW  = (GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW);
    public static final int CANGROW_CANSHRINK = (GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK);

    private UI() { }

    public static void execSafe(@NotNull com.projectgalen.lib.utils.delegates.ThrowingRunnable throwingRunnable) {
        try {
            throwingRunnable.run();
        }
        catch(Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull GridConstraints getGridConstraints(int row, int column) {
        return getGridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull GridConstraints getGridConstraints(int row, int column, int anchor) {
        return getGridConstraints(row, column, 1, 1, anchor);
    }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    public static @NotNull GridConstraints getGridConstraints(int row, int column, int rowSpan, int colSpan) {
        return getGridConstraints(row, column, rowSpan, colSpan, GridConstraints.ANCHOR_CENTER);
    }

    @Contract(value = "_, _, _, _, _ -> new", pure = true)
    public static @NotNull GridConstraints getGridConstraints(int row, int column, int rowSpan, int colSpan, int anchor) {
        return getGridConstraints(row, column, rowSpan, colSpan, anchor, GridConstraints.FILL_BOTH);
    }

    @Contract(value = "_, _, _, _, _, _ -> new", pure = true)
    public static @NotNull GridConstraints getGridConstraints(int row, int column, int rowSpan, int colSpan, int anchor, int fill) {
        return new GridConstraints(row, column, rowSpan, colSpan, anchor, fill, CANGROW_CANSHRINK, CANGROW_CANSHRINK, null, null, null, 0);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull GridConstraints getHSpacerConstraints(int row, int column) { return getHSpacerConstraints(row, column, 1, 1); }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    public static @NotNull GridConstraints getHSpacerConstraints(int row, int column, int rowSpan, int colSpan) {
        return new GridConstraints(row,
                                   column,
                                   rowSpan,
                                   colSpan,
                                   GridConstraints.ANCHOR_CENTER,
                                   GridConstraints.FILL_HORIZONTAL,
                                   CANGROW_WANTGROW,
                                   GridConstraints.SIZEPOLICY_FIXED,
                                   null,
                                   null,
                                   null,
                                   0,
                                   false);
    }

    public static @NotNull Icon getIcon(@NotNull String name, @NotNull Class<?> referenceClass) {
        return new ImageIcon(Objects.requireNonNull(referenceClass.getResource(name)));
    }

    public static @NotNull Icon getIcon(@NotNull String path, @NotNull String name, @NotNull Class<?> referenceClass) {
        StringBuilder sb = new StringBuilder();
        sb.append(path = path.replace('\\', '/'));
        if(!path.endsWith("/")) sb.append('/');
        if((name = name.replace('\\', '/')).startsWith("/")) name = name.substring(1);
        return getIcon(sb.append(name).toString(), referenceClass);
    }

    public static @NotNull Mnemonic getMnemonic(@NotNull String text) {
        int len = text.length();
        int idx = text.indexOf('&');

        while(idx >= 0) {
            int i = (idx + 1);
            if(i == len) break;
            int cp = text.codePointAt(i);
            if(cp != '&') return new Mnemonic(text.substring(0, idx) + text.substring(i), true, KeyEvent.getExtendedKeyCodeForChar(cp), idx);
            idx = text.indexOf('&', (i + 1));
        }

        return new Mnemonic(text, false, VK_UNDEFINED, -1);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull GridConstraints getVSpacerConstraints(int row, int column) { return getVSpacerConstraints(row, column, 1, 1); }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    public static @NotNull GridConstraints getVSpacerConstraints(int row, int column, int rowSpan, int colSpan) {
        return new GridConstraints(row,
                                   column,
                                   rowSpan,
                                   colSpan,
                                   GridConstraints.ANCHOR_CENTER,
                                   GridConstraints.FILL_VERTICAL,
                                   GridConstraints.SIZEPOLICY_FIXED,
                                   CANGROW_WANTGROW,
                                   null,
                                   null,
                                   null,
                                   0,
                                   false);
    }

    public static <T> @Nullable T invokeAndGet(@NotNull Callable<T> callable) {
        return invokeAndGet(false, callable);
    }

    public static <T> @Nullable T invokeAndGet(boolean propagateExceptions, @NotNull Callable<T> callable) {
        if(SwingUtilities.isEventDispatchThread()) return propagate(propagateExceptions, callable);
        try {
            return new UIFuture<>(callable).get();
        }
        catch(Exception e) {
            if(propagateExceptions) throw makeRuntimeException(e);
            return null;
        }
    }

    @Contract("_ -> new")
    public static <T> @NotNull Future<T> invokeAndGetLater(@NotNull Callable<T> callable) {
        if(SwingUtilities.isEventDispatchThread()) throw new RuntimeException("Cannot be called from the event dispatch thread.");
        return new UIFuture<>(callable);
    }

    public static void invokeAndWait(@NotNull ThrowingRunnable runnable) {
        AtomicReference<Exception> ex = new AtomicReference<>(null);
        try {
            if(SwingUtilities.isEventDispatchThread()) runnable.run();
            else SwingUtilities.invokeAndWait(() -> { try { runnable.run(); } catch(Exception e) { ex.set(e); } });
        }
        catch(Exception e) { throw makeRuntimeException(e); }
        if(ex.get() != null) throw makeRuntimeException(ex.get());
    }

    public static void invokeLater(@NotNull Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

    public static void invokeLater2(@NotNull Runnable runnable) {
        if(SwingUtilities.isEventDispatchThread()) runnable.run();
        else SwingUtilities.invokeLater(runnable);
    }

    @Contract("_, _ -> param1") public static <T extends AbstractButton> @NotNull T setButtonText(@NotNull T button, @NotNull String text) {
        Mnemonic mnemonic = getMnemonic(text);
        button.setText(mnemonic.text());

        if(mnemonic.hasMnemonic()) {
            button.setMnemonic(mnemonic.mnemonic());
            button.setDisplayedMnemonicIndex(mnemonic.index());
        }

        return button;
    }

    public static KeyStroke setButtonText(@NotNull JButton button, @NotNull String text, @Nullable KeyStroke oldAccel, @Nullable KeyStroke newAccel, @NotNull ActionListener actionListener) {
        Mnemonic mnemonic = getMnemonic(text);

        if(newAccel == null) {
            int idx = (text.indexOf('&') + 1);
            if((idx > 0) && (idx < text.length())) {
                char ch = text.charAt(idx);
                newAccel = KeyStroke.getKeyStroke(Character.toLowerCase(ch), ((Character.isUpperCase(ch) ? InputEvent.SHIFT_DOWN_MASK : 0) | InputEvent.ALT_DOWN_MASK));
                text     = text.substring(0, idx - 1) + text.substring(idx);
            }
        }

        button.setText(text);

        if((newAccel != null) && !Objects.equals(newAccel, oldAccel)) {
            if(oldAccel != null) button.unregisterKeyboardAction(oldAccel);
            button.registerKeyboardAction(actionListener, newAccel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            return newAccel;
        }

        return oldAccel;
    }

    public static void setFlatLaf() throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatLightLaf());
    }

    public static void setLookAndFeel(@NotNull BuiltInLookAndFeelProfiles profile) throws UnsupportedLookAndFeelException, ReflectiveOperationException {
        UIManager.setLookAndFeel(profile.getClassName());
    }
}
