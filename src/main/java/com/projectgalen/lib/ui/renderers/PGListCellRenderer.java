package com.projectgalen.lib.ui.renderers;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: PGListCellRenderer.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 10, 2023
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

import com.projectgalen.lib.ui.PGDefaultLookup;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

@SuppressWarnings({ "unused", "SameParameterValue", "UnusedAssignment" })
public class PGListCellRenderer<T> implements ListCellRenderer<T> {

    private static final String UI1                   = "List.cellNoFocusBorder";
    private static final String UI2                   = "List.focusSelectedCellHighlightBorder";
    private static final String UI3                   = "List.focusCellHighlightBorder";
    private static final String UI4                   = "List.dropCellForeground";
    private static final String UI5                   = "List.dropCellBackground";
    private static final String UI6                   = "List.noValueForground";
    private static final String UI7                   = "List.noValueBackground";
    private static final String UI9                   = "List.noValueText";
    private static final String DEFAULT_NULL_STRING   = "<none>";
    private static final Color  DEFAULT_NULL_FG_COLOR = new Color(193, 193, 193, 255);
    private static final Color  DEFAULT_NULL_BG_COLOR = new Color(255, 255, 255, 255);

    private final    PGDefaultListCellRenderer delegate       = new PGDefaultListCellRenderer();
    private          boolean                   allow          = false;
    private @NotNull Function<T, String>       stringFunction = o -> Objects.toString(o, DEFAULT_NULL_STRING);
    private          Color                     noValueFGColor;
    private          Color                     noValueBGColor;
    private          String                    noValueText;

    public PGListCellRenderer() {
        this(null, null, item -> Objects.toString(item, DEFAULT_NULL_STRING));
    }

    public PGListCellRenderer(@Nullable Color noValueFGColor, @Nullable Color noValueBGColor) {
        this(noValueFGColor, noValueBGColor, item -> Objects.toString(item, DEFAULT_NULL_STRING));
    }

    public PGListCellRenderer(@NotNull Function<T, String> stringFunction) {
        this(null, null, stringFunction);
    }

    public PGListCellRenderer(@Nullable Color noValueFGColor, @Nullable Color noValueBGColor, @NotNull Function<T, String> stringFunction) {
        super();
        this.stringFunction = stringFunction;
        this.noValueFGColor = noValueFGColor;
        this.noValueBGColor = noValueBGColor;
    }

    public final @Contract(pure = true) @Override @NotNull Component getListCellRendererComponent(@NotNull JList<? extends T> list,
                                                                                                  @Nullable T value,
                                                                                                  int index,
                                                                                                  boolean isSelected,
                                                                                                  boolean cellHasFocus) {
        synchronized(delegate.lock) {
            allow = true;
            try {
                String    displayString = stringFunction.apply(value);
                Component renderer      = delegate.getListCellRendererComponent(list, displayString, index, isSelected, cellHasFocus);
                return getUserListCellRendererComponent(renderer, list, value, displayString, index, isSelected, cellHasFocus);
            }
            finally {
                allow = false;
            }
        }
    }

    public Color getNoValueBGColor() {
        return noValueBGColor;
    }

    public Color getNoValueFGColor() {
        return noValueFGColor;
    }

    public @NotNull Function<T, String> getStringFunction() {
        return stringFunction;
    }

    public @NotNull Component getUserListCellRendererComponent(@NotNull Component renderer,
                                                               @NotNull JList<? extends T> list,
                                                               @Nullable T value,
                                                               @Nullable String displayString,
                                                               int index,
                                                               boolean isSelected,
                                                               boolean cellHasFocus) {
        return renderer;
    }

    public void setNoValueBGColor(Color noValueBGColor) {
        this.noValueBGColor = noValueBGColor;
    }

    public void setNoValueFGColor(Color noValueFGColor) {
        this.noValueFGColor = noValueFGColor;
    }

    public void setStringFunction(@NotNull Function<T, String> stringFunction) {
        this.stringFunction = stringFunction;
    }

    private @NotNull Border getUIBorder(@NotNull String key) {
        return getUIBorder(key, () -> new EmptyBorder(1, 1, 1, 1));
    }

    private Border getUIBorder(@NotNull String key, @NotNull Supplier<Border> defaultSupplier) {
        return ofNullable(PGDefaultLookup.getBorder(delegate, delegate.getUI(), key)).orElseGet(defaultSupplier);
    }

    private Color getUIColor(@NotNull String key, Color defaultValue) {
        return getUIColor(key, () -> defaultValue);
    }

    private Color getUIColor(@NotNull String key, @NotNull Supplier<Color> defaultSupplier) {
        return ofNullable(PGDefaultLookup.getColor(delegate, delegate.getUI(), key)).orElseGet(defaultSupplier);
    }

    private String getUIString(@NotNull String key, String defaultValue) {
        return Objects.toString(PGDefaultLookup.get(delegate, delegate.getUI(), key), defaultValue);
    }

    private class PGDefaultListCellRenderer extends DefaultListCellRenderer {

        private final Set<String> propertySet = new TreeSet<>();
        private final String      lock        = UUID.randomUUID().toString();

        public PGDefaultListCellRenderer() { super(); }

        public @Override Component getListCellRendererComponent(@NotNull JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            synchronized(lock) {
                boolean isNull = (value == null);
                boolean isDrop = ofNullable(list.getDropLocation()).filter(l -> (!l.isInsert() && (l.getIndex() == index))).isPresent();
                boolean isSel  = (isSelected || isDrop);

                propertySet.clear();
                setComponentOrientation(list.getComponentOrientation());

                if(value instanceof Icon icon) {
                    setIcon(icon);
                    setText("");
                }
                else {
                    setIcon(null);
                    setText(Objects.toString(value, getUIString(UI9, DEFAULT_NULL_STRING)));
                }

                setFont(isNull ? list.getFont().deriveFont(Font.ITALIC) : list.getFont());
                setForeground(isSel ? getSelectionColor(isDrop, UI4, list::getSelectionForeground) : (isNull ? getUIColor(UI6, this::getNoValueFGColor) : list.getForeground()));
                setBackground(isSel ? getSelectionColor(isDrop, UI5, list::getSelectionBackground) : (isNull ? getUIColor(UI7, this::getNoValueBGColor) : list.getBackground()));
                setEnabled(list.isEnabled());
                setBorder(cellHasFocus ? getFocusBorder(isSel, UI2, () -> getUIBorder(UI3)) : getUIBorder(UI1));

                return this;
            }
        }

        public @Override void setBackground(Color bg)     { condSet("Background", () -> super.setBackground(bg)); }

        public @Override void setBorder(Border border)    { condSet("Border", () -> super.setBorder(border)); }

        public @Override void setCursor(Cursor cursor)    { condSet("Cursor", () -> super.setCursor(cursor)); }

        public @Override void setFont(Font font)          { condSet("Font", () -> super.setFont(font)); }

        public @Override void setForeground(Color fg)     { condSet("Foreground", () -> super.setForeground(fg)); }

        public @Override void setIcon(Icon icon)          { condSet("Icon", () -> super.setIcon(icon)); }

        public @Override void setOpaque(boolean isOpaque) { condSet("Opaque", () -> super.setOpaque(isOpaque)); }

        public @Override void setText(String text)        { condSet("Text", () -> super.setText(text)); }

        private void condSet(@NotNull String key, @NotNull Runnable runnable) {
            if(lock == null) {
                runnable.run();
            }
            else synchronized(lock) {
                if(allow) {
                    runnable.run();
                    propertySet.add(key);
                }
                else if(!propertySet.contains(key)) {
                    runnable.run();
                }
            }
        }

        private @NotNull Border getFocusBorder(boolean isSelected, String selectedFocusBorderUIKey, @NotNull Supplier<Border> borderSupplier) {
            return (isSelected ? getUIBorder(selectedFocusBorderUIKey, borderSupplier) : borderSupplier.get());
        }

        private @NotNull Color getNoValueBGColor() {
            return ofNullable(PGListCellRenderer.this.getNoValueBGColor()).orElse(DEFAULT_NULL_BG_COLOR);
        }

        private @NotNull Color getNoValueFGColor() {
            return ofNullable(PGListCellRenderer.this.getNoValueFGColor()).orElse(DEFAULT_NULL_FG_COLOR);
        }

        private Color getSelectionColor(boolean isDropTarget, @NotNull String dropTargetValueKey, @NotNull Supplier<Color> defaultSupplier) {
            return (isDropTarget ? getUIColor(dropTargetValueKey, defaultSupplier) : defaultSupplier.get());
        }
    }
}
