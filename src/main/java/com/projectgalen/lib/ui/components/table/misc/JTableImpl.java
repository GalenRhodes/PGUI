package com.projectgalen.lib.ui.components.table.misc;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: JTableImpl.java
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

import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.math.BigDecimal;

public class JTableImpl<T> extends JTable {

    public JTableImpl(@Nullable DataModel<T> dataModel) {
        super(dataModel);
        setDoubleBuffered(true);
        setDefaultRenderer(BigDecimal.class, new CurrencyCellRenderer());
        setDefaultEditor(BigDecimal.class, new CurrencyCellEditor());
        setFillsViewportHeight(true);
        setCellSelectionEnabled(false);
        setColumnSelectionAllowed(false);
        setRowSelectionAllowed(true);
    }

    public @SuppressWarnings("unchecked") DataModel<T> getDataModel() {
        TableModel model = super.getModel();
        return ((model instanceof DataModel<?>) ? ((DataModel<T>)model) : null);
    }

    public @Override int getSelectedRow() {
        int[] rows = getSelectedRows();
        return ((rows.length > 0) ? rows[0] : -1);
    }

    public @Override int getSelectedRowCount() {
        return getSelectedRows().length;
    }

    public @Override int[] getSelectedRows() {
        return getDataModel().getSelectedRows(this);
    }

    public @Override Component prepareEditor(TableCellEditor editor, int row, int column) {
        Component c = super.prepareEditor(editor, row, column);
        c.setFont(getFont());
        return c;
    }

    public @Override Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        DataModel<T> model = getDataModel();

        if((model == null) || !(renderer instanceof DefaultTableCellRenderer dRenderer)) return super.prepareRenderer(renderer, row, column);

        boolean   isSelected = isRowSelected(row);
        Component cRenderer  = super.prepareRenderer(dRenderer, row, column);

        cRenderer.setBackground(isSelected ? getSelectionBackground() : getBackground());
        cRenderer.setForeground(isSelected ? getSelectionForeground() : getForeground());
        if(cRenderer instanceof JLabel l) l.setHorizontalAlignment(model.getRowModel().getColumnAlignment(column));
        if(cRenderer instanceof JComponent c) c.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        return model.setRowAttributes(dRenderer, cRenderer, this, row, column, isSelected);
    }
}
