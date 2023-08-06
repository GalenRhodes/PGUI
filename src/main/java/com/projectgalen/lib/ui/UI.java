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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static com.projectgalen.lib.utils.errors.Errors.makeRuntimeException;
import static com.projectgalen.lib.utils.errors.Errors.propagate;

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
        try {
            return new ImageIcon(ImageIO.read(Objects.requireNonNull(referenceClass.getResourceAsStream(name))));
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
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

    public static void setFlatLaf() throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatLightLaf());
    }

    public static void setLookAndFeel(@NotNull BuiltInLookAndFeelProfiles profile) throws UnsupportedLookAndFeelException, ReflectiveOperationException {
        UIManager.setLookAndFeel(profile.getClassName());
    }
}
