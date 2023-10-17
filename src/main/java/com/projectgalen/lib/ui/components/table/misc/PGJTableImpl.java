package com.projectgalen.lib.ui.components.table.misc;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: PGJTableImpl.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 12, 2023
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.math.BigDecimal;

import static com.projectgalen.lib.ui.M.msgs;

@SuppressWarnings("unchecked")
public class PGJTableImpl<T> extends JTable {

    public static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);

    public PGJTableImpl(@Nullable PGJTableModel<T> tableModel) {
        super(tableModel);
        setDoubleBuffered(true);
        setDefaultRenderer(BigDecimal.class, new CurrencyCellRenderer());
        setDefaultEditor(BigDecimal.class, new CurrencyCellEditor());
        setFillsViewportHeight(true);
        setCellSelectionEnabled(true);
        setColumnSelectionAllowed(true);
        setRowSelectionAllowed(true);
        getTableHeader().setReorderingAllowed(false);
    }

    public @Override Component prepareEditor(TableCellEditor editor, int row, int column) {
        Component c = super.prepareEditor(editor, row, column);
        c.setFont(getFont());
        return c;
    }

    public @Override @Nullable PGJTableModel<T> getModel() {
        return (PGJTableModel<T>)super.getModel();
    }

    public @NotNull Component prepareRenderer(@NotNull PGJTableModel<T> model, @NotNull Component renderer, int row, int column, boolean isSelected) {
        renderer.setBackground(isSelected ? getSelectionBackground() : getBackground());
        renderer.setForeground(isSelected ? getSelectionForeground() : getForeground());
        if(renderer instanceof JLabel l) l.setHorizontalAlignment(model.getRowModel().getColumnAlignment(column));
        if(renderer instanceof JComponent c) c.setBorder(EMPTY_BORDER);
        return model.setRowAttributes(renderer, this, row, column, isSelected);
    }

    public @Override Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        PGJTableModel<T> model = getModel();
        return ((model != null) ? prepareRenderer(model, super.prepareRenderer(renderer, row, column), row, column, isCellSelected(row, column)) : super.prepareRenderer(renderer, row, column));
    }

    public @Override void setModel(@NotNull TableModel dataModel) {
        if(dataModel instanceof PGJTableModel<?>) super.setModel(dataModel);
        else throw new IllegalArgumentException(msgs.getString("msg.err.pgjtableimpl.invalid_instance_of_model"));
    }

    public void setModel(@NotNull PGJTableModel<T> model) {
        super.setModel(model);
    }
}
