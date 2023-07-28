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
import com.projectgalen.lib.utils.refs.BooleanRef;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static com.projectgalen.lib.utils.errors.Errors.makeRuntimeException;
import static com.projectgalen.lib.utils.errors.Errors.propagate;

@SuppressWarnings("unused")
public final class UI {
    public static final int CANGROW_WANTGROW  = (GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW);
    public static final int CANGROW_CANSHRINK = (GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK);

    private UI() { }

    public static @Nullable Component findChild(@Nullable Container parent, @NotNull String name) {
        return findChild(parent, Component.class, name);
    }

    public static <T extends Component> @Nullable T findChild(@Nullable Container parent, @NotNull Class<T> cls, @NotNull String name) {
        return ((parent == null) ? null : _findChild(parent, cls, name).orElse(null));
    }

    public static <T extends Component> void forEachChild(@Nullable Container parent, @NotNull Class<T> cls, @NotNull String name, boolean deep, @NotNull BiConsumer<T, BooleanRef> consumer) {
        if(parent != null) forEachChild(parent, cls, name, deep, new BooleanRef(false), consumer);
    }

    public static <T extends Component> void forEachChild(@Nullable Container parent, @NotNull Class<T> cls, @NotNull String name, @NotNull BiConsumer<T, BooleanRef> consumer) {
        if(parent != null) forEachChild(parent, cls, name, true, new BooleanRef(false), consumer);
    }

    public static void forEachChild(@Nullable Container parent, @NotNull String name, boolean deep, @NotNull BiConsumer<Component, BooleanRef> consumer) {
        if(parent != null) forEachChild(parent, Component.class, name, deep, new BooleanRef(false), consumer);
    }

    public static void forEachChild(@Nullable Container parent, @NotNull String name, @NotNull BiConsumer<Component, BooleanRef> consumer) {
        if(parent != null) forEachChild(parent, Component.class, name, true, new BooleanRef(false), consumer);
    }

    public static void forEachChild(@Nullable Container parent, boolean deep, @NotNull BiConsumer<Component, BooleanRef> consumer) {
        if(parent != null) forEachChild(parent, Component.class, null, deep, new BooleanRef(false), consumer);
    }

    public static void forEachChild(@Nullable Container parent, @NotNull BiConsumer<Component, BooleanRef> consumer) {
        if(parent != null) forEachChild(parent, Component.class, null, true, new BooleanRef(false), consumer);
    }

    public static <T extends Component> void forEachChild(@Nullable Container parent, @NotNull Class<T> cls, boolean deep, @NotNull BiConsumer<T, BooleanRef> consumer) {
        if(parent != null) forEachChild(parent, cls, null, deep, new BooleanRef(false), consumer);
    }

    public static <T extends Component> void forEachChild(@Nullable Container parent, @NotNull Class<T> cls, @NotNull BiConsumer<T, BooleanRef> consumer) {
        if(parent != null) forEachChild(parent, cls, null, true, new BooleanRef(false), consumer);
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

    public static <T extends Component> @NotNull Stream<T> streamChildren(@Nullable Container c, @NotNull Class<T> cls) {
        return ((c == null) ? Stream.empty() : Stream.of(c.getComponents()).filter(cls::isInstance).map(cls::cast));
    }

    private static <T extends Component> @NotNull Optional<T> _findChild(@NotNull Container parent, @NotNull Class<T> cls, @NotNull String name) {
        synchronized(parent.getTreeLock()) {
            Optional<T> child = streamChildren(parent, cls).filter(c -> name.equals(c.getName())).findFirst();
            return (child.isPresent() ? child : streamChildren(parent, Container.class).map(c -> _findChild(c, cls, name)).filter(Optional::isPresent).findFirst().orElse(Optional.empty()));
        }
    }

    private static <T extends Component> void forEachChild(@NotNull Container parent, @NotNull Class<T> cls, @Nullable String name, boolean deep, BooleanRef stop, @NotNull BiConsumer<T, BooleanRef> consumer) {
        synchronized(parent.getTreeLock()) {
            for(Component child : parent.getComponents()) {
                if(cls.isInstance(child) && ((name == null) || name.equals(child.getName()))) {
                    consumer.accept(cls.cast(child), stop);
                    if(stop.value) break;
                }
            }
            if(deep && !stop.value) {
                for(Component child : parent.getComponents()) {
                    if(child instanceof Container c) {
                        forEachChild(c, cls, name, true, stop, consumer);
                        if(stop.value) break;
                    }
                }
            }
        }
    }
}
