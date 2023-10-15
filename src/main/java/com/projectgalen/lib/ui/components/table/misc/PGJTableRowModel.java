package com.projectgalen.lib.ui.components.table.misc;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: PGJTableRowModel.java
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

import com.projectgalen.lib.ui.listeners.TableCellModelListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public interface PGJTableRowModel<T> {

    void addTableCellModelListener(@NotNull TableCellModelListener listener);

    int getColumnAlignment(int columnIndex);

    Class<?> getColumnClass(int columnIndex);

    int getColumnCount();

    String getColumnName(int columnIndex);

    @Nullable Object getColumnValue(@NotNull T obj, int columnIndex);

    @NotNull PGJTableModel<T> getTableModel();

    boolean isColumnEditable(@NotNull T obj, int columnIndex);

    boolean isColumnIndexValid(int columnIndex);

    void removeTableCellModelListener(@NotNull TableCellModelListener listener);

    void setColumnAttributes(@NotNull Component renderer, @NotNull JTable table, @NotNull T object, int columnIndex, boolean isSelected);

    void setColumnValue(@NotNull T obj, int rowIndex, int columnIndex, @Nullable Object newValue);

    void setModelProvider(@NotNull Supplier<PGJTableModel<T>> modelProvider);
}
