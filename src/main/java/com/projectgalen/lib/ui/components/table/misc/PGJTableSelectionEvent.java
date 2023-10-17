package com.projectgalen.lib.ui.components.table.misc;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: PGJTableSelectionEvent.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: September 13, 2023
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

import java.util.EventObject;
import java.util.List;

public class PGJTableSelectionEvent extends EventObject {
    private final int[] selectedRows;
    private final int[] selectedColumns;
    private final List<?> selectedItems;

    public PGJTableSelectionEvent(@NotNull Object source, int @NotNull [] selectedRows, int @NotNull [] selectedColumns, @NotNull List<?> selectedItems) {
        super(source);
        this.selectedRows    = selectedRows;
        this.selectedColumns = selectedColumns;
        this.selectedItems   = selectedItems;
    }

    public int[] getSelectedColumns() {
        return selectedColumns;
    }

    public List<?> getSelectedItems() {
        return selectedItems;
    }

    public int[] getSelectedRows() {
        return selectedRows;
    }
}
