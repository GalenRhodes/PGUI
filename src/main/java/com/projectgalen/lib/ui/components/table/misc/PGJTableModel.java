package com.projectgalen.lib.ui.components.table.misc;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: PGJTableModel.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: September 06, 2023
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

import com.projectgalen.lib.ui.events.TableCellModelEvent;
import com.projectgalen.lib.ui.interfaces.PGDataSupplier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

@SuppressWarnings("unused")
public class PGJTableModel<T> extends AbstractTableModel {
    protected PGJTableRowModel<T> rowModel;
    protected PGDataSupplier<T>   dataSupplier;

    public PGJTableModel(PGJTableRowModel<T> rowModel, PGDataSupplier<T> dataSupplier) {
        super();
        this.dataSupplier = dataSupplier;
        this.rowModel     = rowModel;
        this.rowModel.setModelProvider(() -> this);
        this.rowModel.addTableCellModelListener(this::onTableCellModelEvent);
    }

    public PGJTableModel(PGJTableRowModel<T> rowModel) {
        this(rowModel, new PGListDataSupplier<>());
    }

    public @Override @Nullable Class<?> getColumnClass(int columnIndex) {
        return rowModel.getColumnClass(columnIndex);
    }

    public @Override int getColumnCount() {
        return rowModel.getColumnCount();
    }

    public @Override @Nullable String getColumnName(int columnIndex) {
        return rowModel.getColumnName(columnIndex);
    }

    public PGDataSupplier<T> getDataSupplier() {
        return dataSupplier;
    }

    public @Override int getRowCount() {
        return dataSupplier.size();
    }

    public PGJTableRowModel<T> getRowModel() {
        return rowModel;
    }

    public @Contract(pure = true) @Override @Nullable Object getValueAt(int rowIndex, int columnIndex) {
        return (isRowIndexValid(rowIndex) ? rowModel.getColumnValue(dataSupplier.get(rowIndex), columnIndex) : null);
    }

    public @Override boolean isCellEditable(int rowIndex, int columnIndex) {
        return (isRowIndexValid(rowIndex) && rowModel.isColumnEditable(dataSupplier.get(rowIndex), columnIndex));
    }

    public boolean isRowDeletable(int rowIndex) {
        return true;
    }

    public boolean isRowEditable(int rowIndex) {
        return true;
    }

    public boolean isRowIndexValid(int rowIndex) {
        return ((rowIndex >= 0) && (rowIndex < dataSupplier.size()));
    }

    public boolean isRowSelectable(int rowIndex) {
        return true;
    }

    public void setDataSupplier(@NotNull PGDataSupplier<T> dataSupplier) {
        this.dataSupplier = dataSupplier;
        fireTableDataChanged();
    }

    public @NotNull Component setRowAttributes(@NotNull Component renderer, @NotNull JTable table, int rowIndex, int columnIndex, boolean isSelected) {
        rowModel.setColumnAttributes(renderer, table, dataSupplier.get(rowIndex), columnIndex, isSelected);
        return renderer;
    }

    public void setRowModel(PGJTableRowModel<T> rowModel) {
        this.rowModel = rowModel;
        fireTableStructureChanged();
    }

    public @Override void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(isRowIndexValid(rowIndex)) {
            rowModel.setColumnValue(dataSupplier.get(rowIndex), rowIndex, columnIndex, aValue);
            fireTableDataChanged();
        }
    }

    private void onTableCellModelEvent(@NotNull TableCellModelEvent e) {
        switch(e.getEventType()) {
            case CellDataUpdated -> fireTableCellUpdated(e.getRowIndex(), e.getColumnIndex());
            case TableDataUpdated -> fireTableDataChanged();
        }
    }
}
