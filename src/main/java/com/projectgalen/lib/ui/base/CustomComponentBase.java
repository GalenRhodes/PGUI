package com.projectgalen.lib.ui.base;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: CustomComponentBase.java
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

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

@SuppressWarnings({ "unused", "SameParameterValue" })
public class CustomComponentBase {

    public CustomComponentBase() { }

    public void populateGridData(@NotNull Container parent, @NotNull String panelName, int rowCount, @NotNull GridDataLambda lambda) {
        JPanel p = findComponentWithName(parent, panelName, JPanel.class);
        if(p != null) {
            p.removeAll();
            p.setLayout(new GridLayoutManager(Math.max(rowCount, 1), 2, new Insets(0, 0, 0, 0), -1, -1));

            Font pFont = p.getFont();

            for(int r = 0; r < rowCount; r++) {
                GridDataItem item = lambda.getGridDataItem(r);
                if(item != null) {
                    JLabel label = new JLabel(Objects.toString(item.label, ""));
                    JLabel data  = new JLabel(Objects.toString(item.data, ""));

                    label.setFont(Objects.requireNonNullElseGet(item.labelFont, () -> new Font(pFont.getFontName(), Font.BOLD, 10)));
                    data.setFont(Objects.requireNonNullElseGet(item.dataFont, () -> new Font(pFont.getFontName(), Font.PLAIN, 10)));

                    p.add(label, getConstraint(r, 0, item.labelAnchor));
                    p.add(data, getConstraint(r, 1, item.dataAnchor));
                }
            }
        }
    }

    protected <T extends JComponent> T findComponentWithName(@NotNull Container parent, @NotNull String name, @NotNull Class<T> cls) {
        synchronized(parent.getTreeLock()) {
            for(Component c : parent.getComponents()) {
                if(name.equals(c.getName()) && cls.isAssignableFrom(c.getClass())) return cls.cast(c);
                if(c instanceof Container) {
                    T o = findComponentWithName((Container)c, name, cls);
                    if(o != null) return o;
                }
            }
            return null;
        }
    }

    protected @NotNull GridConstraints getConstraint(int row, int column, int anchor) {
        return new GridConstraints(row, column, 1, 1, anchor, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false);
    }

    public interface GridDataLambda {
        GridDataItem getGridDataItem(int row);
    }

    public static class GridDataItem {
        private final String label;
        private final String data;
        private final int    labelAnchor;
        private final int    dataAnchor;
        private final Font   labelFont;
        private final Font   dataFont;

        public GridDataItem(String label, int labelAnchor, @NotNull Font font, String data, int dataAnchor) {
            this.label       = label;
            this.data        = data;
            this.labelAnchor = labelAnchor;
            this.dataAnchor  = dataAnchor;
            this.labelFont   = new Font(font.getFontName(), Font.BOLD, font.getSize());
            this.dataFont    = new Font(font.getFontName(), Font.PLAIN, font.getSize());
        }

        public GridDataItem(String label, int labelAnchor, Font labelFont, String data, int dataAnchor, Font dataFont) {
            this.label       = label;
            this.data        = data;
            this.labelAnchor = labelAnchor;
            this.dataAnchor  = dataAnchor;
            this.labelFont   = labelFont;
            this.dataFont    = dataFont;
        }

        public GridDataItem(String label, String data) {
            this.label       = label;
            this.data        = data;
            this.dataAnchor  = GridConstraints.ANCHOR_WEST;
            this.labelAnchor = GridConstraints.ANCHOR_EAST;
            this.labelFont   = null;
            this.dataFont    = null;
        }

        public GridDataItem(String label, String data, int dataAnchor) {
            this.label       = label;
            this.data        = data;
            this.dataAnchor  = dataAnchor;
            this.labelAnchor = GridConstraints.ANCHOR_EAST;
            this.labelFont   = null;
            this.dataFont    = null;
        }

        public GridDataItem(@NotNull String label, int labelAnchor, @Nullable String data, int dataAnchor) {
            this.label       = label;
            this.data        = data;
            this.labelAnchor = labelAnchor;
            this.dataAnchor  = dataAnchor;
            this.labelFont   = null;
            this.dataFont    = null;
        }

        public String getData() {
            return data;
        }

        public int getDataAnchor() {
            return dataAnchor;
        }

        public Font getDataFont() {
            return dataFont;
        }

        public String getLabel() {
            return label;
        }

        public int getLabelAnchor() {
            return labelAnchor;
        }

        public Font getLabelFont() {
            return labelFont;
        }
    }
}
