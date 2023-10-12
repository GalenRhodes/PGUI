package com.projectgalen.lib.ui.components.table.misc;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: DropDownCellItem.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: September 19, 2023
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

import com.projectgalen.lib.ui.components.combobox.PGJComboBox;
import com.projectgalen.lib.ui.components.combobox.PGJListCellRendererProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")
public class DropDownCellItem<T> extends PGJComboBox<T> {
    public DropDownCellItem() {
        super();
    }

    public DropDownCellItem(@NotNull List<T> data) {
        super(data);
    }

    public DropDownCellItem(@NotNull List<T> data, @NotNull Function<T, String> stringFuction) {
        super(data, stringFuction);
    }

    public DropDownCellItem(@NotNull List<T> data, boolean isOptional, @NotNull Function<T, String> stringFuction) {
        super(data, isOptional, stringFuction);
    }

    public DropDownCellItem(@NotNull List<T> data, boolean isOptional, @Nullable PGJListCellRendererProxy<T> renderProxy, @NotNull Function<T, String> stringFuction) {
        super(data, isOptional, renderProxy, stringFuction);
    }
}
