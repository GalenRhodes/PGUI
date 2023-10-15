package com.projectgalen.lib.ui.components.table.misc;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: AbstractPGJTableRowModel.java
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

import com.projectgalen.lib.ui.M;
import com.projectgalen.lib.ui.events.TableCellModelEvent;
import com.projectgalen.lib.ui.events.TableCellModelEvent.EventType;
import com.projectgalen.lib.ui.listeners.TableCellModelListener;
import com.projectgalen.lib.utils.EventListeners;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public abstract class AbstractPGJTableRowModel<T> implements PGJTableRowModel<T> {/*@f0*/

    private final EventListeners             eventListeners = new EventListeners();
    private       Supplier<PGJTableModel<T>> modelProvider;

    public AbstractPGJTableRowModel()                                                                                     { }

    public void addTableCellModelListener(@NotNull TableCellModelListener listener)                                       { eventListeners.add(TableCellModelListener.class, listener); }

    public void fireTableCellChanged(int rowIndex, int columnIndex, @Nullable Object oldValue, @Nullable Object newValue) { fireTableCellModelEvent(new TableCellModelEvent(this, EventType.CellDataUpdated, rowIndex, columnIndex, oldValue, newValue)); }

    public void fireTableCellModelEvent(@NotNull TableCellModelEvent event)                                               { eventListeners.fireEvent(TableCellModelListener.class, event, TableCellModelListener::tableCellChanged); }

    public void fireTableDataChanged()                                                                                    { fireTableCellModelEvent(new TableCellModelEvent(this, EventType.TableDataUpdated, -1, -1, null, null)); }

    public @Override @NotNull PGJTableModel<T> getTableModel()                                                            { return modelProvider.get(); }

    public @Override boolean isColumnEditable(@NotNull T obj, int columnIndex)                                            { return false; }

    public @Override boolean isColumnIndexValid(int columnIndex)                                                          { return ((columnIndex >= 0) && (columnIndex < getColumnCount())); }

    public void removeTableCellModelListener(@NotNull TableCellModelListener listener)                                    { eventListeners.remove(TableCellModelListener.class, listener); }

    public @Override void setColumnAttributes(@NotNull Component renderer, @NotNull JTable table, @NotNull T object, int columnIndex, boolean isSelected) { }

    public @Override void setColumnValue(@NotNull T obj, int rowIndex, int columnIndex, @Nullable Object newValue)        { }

    public @Override void setModelProvider(@NotNull Supplier<PGJTableModel<T>> modelProvider)                             { this.modelProvider = modelProvider; }

    protected @NotNull String getInvalidColumnIndexMessage(int columnIndex)                                               { return M.msgs.format("msg.err.invalid_column_index", columnIndex); }
}
