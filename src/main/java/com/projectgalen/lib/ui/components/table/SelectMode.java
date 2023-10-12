package com.projectgalen.lib.ui.components.table;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: SelectMode.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: September 29, 2023
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

import org.intellij.lang.annotations.MagicConstant;

import static javax.swing.ListSelectionModel.*;

public enum SelectMode {
    Single(SINGLE_SELECTION), Interval(SINGLE_INTERVAL_SELECTION), MultiInterval(MULTIPLE_INTERVAL_SELECTION);

    private final int value;

    SelectMode(@MagicConstant(intValues = { SINGLE_SELECTION,
                                            MULTIPLE_INTERVAL_SELECTION,
                                            SINGLE_INTERVAL_SELECTION }) int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
