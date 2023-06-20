package com.projectgalen.lib.ui.events;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: TableCellModelEvent.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 15, 2023
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

import java.util.EventObject;
import java.util.Objects;

@SuppressWarnings("unused")
public class TableCellModelEvent extends EventObject {
    private final int       rowIndex;
    private final int       columnIndex;
    private final Object    oldValue;
    private final Object    newValue;
    private final EventType eventType;

    public TableCellModelEvent(@NotNull Object source, @NotNull EventType eventType, int rowIndex, int columnIndex, @Nullable Object oldValue, @Nullable Object newValue) {
        super(source);
        this.eventType   = eventType;
        this.rowIndex    = rowIndex;
        this.columnIndex = columnIndex;
        this.oldValue    = oldValue;
        this.newValue    = newValue;
    }

    @Override
    public boolean equals(Object o) {
        return ((this == o) || ((o instanceof TableCellModelEvent) && _equals((TableCellModelEvent)o)));
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Object getNewValue() {
        return newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSource(), eventType, rowIndex, columnIndex, oldValue, newValue);
    }

    private boolean _equals(TableCellModelEvent that) {
        return (Objects.equals(getSource(), that.getSource())
                && (rowIndex == that.rowIndex)
                && (columnIndex == that.columnIndex)
                && Objects.equals(eventType, that.eventType)
                && Objects.equals(oldValue, that.oldValue)
                && Objects.equals(newValue, that.newValue));
    }

    public enum EventType {
        CellDataUpdated, TableDataUpdated
    }
}
