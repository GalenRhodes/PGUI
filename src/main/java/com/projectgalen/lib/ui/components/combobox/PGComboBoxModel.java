package com.projectgalen.lib.ui.components.combobox;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: PGComboBoxModel.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 10, 2023
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
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

@SuppressWarnings({ "unchecked", "unused" })
public class PGComboBoxModel<T> extends AbstractListModel<T> implements ComboBoxModel<T> {
    private final @NotNull Supplier<List<T>> dataSupplier;
    private final @NotNull Supplier<Boolean> isOptionalSupplier;
    private                T                 selectedItem = null;

    public PGComboBoxModel() {
        this(Collections::emptyList, () -> false);
    }

    public PGComboBoxModel(@NotNull Supplier<List<T>> dataSupplier, @NotNull Supplier<Boolean> isOptionalSupplier) {
        super();
        this.dataSupplier       = dataSupplier;
        this.isOptionalSupplier = isOptionalSupplier;
    }

    public void fireContentsChanged() {
        super.fireContentsChanged(this, 0, getSize());
    }

    public @Override @Nullable T getElementAt(int idx) {
        return (isOptional() ? getDataValue(idx - 1) : getDataValue(idx));
    }

    public @Override Object getSelectedItem() {
        return selectedItem;
    }

    public @Override int getSize() {
        return (isOptional() ? (getData().size() + 1) : getData().size());
    }

    public @Override void setSelectedItem(Object anItem) {
        selectedItem = (T)anItem;
    }

    private List<T> getData() {
        return ofNullable(dataSupplier.get()).orElseGet(Collections::emptyList);
    }

    private @Nullable T getDataValue(int idx) {
        List<T> data = getData();
        return (((idx >= 0) && (idx < data.size())) ? data.get(idx) : null);
    }

    private boolean isOptional() {
        return ofNullable(isOptionalSupplier.get()).orElse(false);
    }
}
