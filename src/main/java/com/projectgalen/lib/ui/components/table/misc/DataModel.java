package com.projectgalen.lib.ui.components.table.misc;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: DataModel.java
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

import com.projectgalen.lib.ui.components.table.DataRowModel;
import com.projectgalen.lib.ui.events.TableCellModelEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class DataModel<T> extends AbstractTableModel {
    protected DataRowModel<T> rowModel;
    protected List<T>         dataList;

    public DataModel(DataRowModel<T> rowModel, List<T> dataList) {
        super();
        this.dataList = dataList;
        this.rowModel = rowModel;
        this.rowModel.setModelProvider(() -> this);
        this.rowModel.addTableCellModelListener(this::onTableCellModelEvent);
    }

    public DataModel(DataRowModel<T> rowModel) {
        this(rowModel, new ArrayList<>());
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

    public List<T> getDataList() {
        return dataList;
    }

    public @Override int getRowCount() {
        return dataList.size();
    }

    public DataRowModel<T> getRowModel() {
        return rowModel;
    }

    public int[] getSelectedRows(@NotNull JTable table) {
        return getSelectedRows(table.getSelectionModel());
    }

    public int[] getSelectedRows(@NotNull ListSelectionModel selectionModel) {
        return selectionModel.getSelectedIndices();
    }

    public @Contract(pure = true) @Override @Nullable Object getValueAt(int rowIndex, int columnIndex) {
        return (isRowIndexValid(rowIndex) ? rowModel.getColumnValue(dataList.get(rowIndex), columnIndex) : null);
    }

    public @Override boolean isCellEditable(int rowIndex, int columnIndex) {
        return (isRowIndexValid(rowIndex) && rowModel.isColumnEditable(dataList.get(rowIndex), columnIndex));
    }

    public boolean isRowDeletable(int rowIndex) {
        return true;
    }

    public boolean isRowEditable(int rowIndex) {
        return true;
    }

    public boolean isRowIndexValid(int rowIndex) {
        return ((rowIndex >= 0) && (rowIndex < dataList.size()));
    }

    public boolean isRowSelectable(int rowIndex) {
        return true;
    }

    public void setDataList(@NotNull List<T> dataList) {
        this.dataList = dataList;
        fireTableDataChanged();
    }

    public @NotNull Component setRowAttributes(@NotNull DefaultTableCellRenderer cellRenderer,
                                               @NotNull Component rendererComponent,
                                               @NotNull JTable table,
                                               int rowIndex,
                                               int columnIndex,
                                               boolean isSelected) {
        rowModel.setColumnAttributes(cellRenderer, rendererComponent, table, dataList.get(rowIndex), columnIndex, isSelected);
        return rendererComponent;
    }

    public void setRowModel(DataRowModel<T> rowModel) {
        this.rowModel = rowModel;
        fireTableStructureChanged();
    }

    public @Override void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(isRowIndexValid(rowIndex)) {
            rowModel.setColumnValue(dataList.get(rowIndex), rowIndex, columnIndex, aValue);
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
