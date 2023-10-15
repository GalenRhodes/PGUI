package com.projectgalen.lib.ui.components.table.test;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: TestRowModel.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 14, 2023
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

import com.projectgalen.lib.ui.components.table.misc.AbstractPGJTableRowModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.math.BigDecimal;

public class TestRowModel extends AbstractPGJTableRowModel<TestData> {

    public static final Class<?>[] columnClasses    = { String.class, int.class, String.class, BigDecimal.class };
    public static final int[]      columnAlignments = { SwingConstants.LEADING, SwingConstants.RIGHT, SwingConstants.CENTER, SwingConstants.RIGHT };
    public static final String[]   columnNames      = { "Name", "Age", "Gender", "Salary" };

    public TestRowModel() { }

    @Override public int getColumnAlignment(int columnIndex) {
        return columnAlignments[columnIndex];
    }

    @Override public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    @Override public int getColumnCount() {
        return 4;
    }

    @Override public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override public @Nullable Object getColumnValue(@NotNull TestData obj, int columnIndex) {
        return switch(columnIndex) {
            case 0 -> obj.getName();
            case 1 -> obj.getAge();
            case 2 -> obj.getGender();
            case 3 -> obj.getSalary();
            default -> throw new IndexOutOfBoundsException(columnIndex);
        };
    }
}
