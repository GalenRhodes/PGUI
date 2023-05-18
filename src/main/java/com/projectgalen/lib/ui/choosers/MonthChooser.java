package com.projectgalen.lib.ui.choosers;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: MonthChooser.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 03, 2023
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

import com.projectgalen.lib.utils.enums.Month;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.Objects;

@SuppressWarnings("unused")
public class MonthChooser extends DayInformer {

    public MonthChooser() {
        this(null, false);
    }

    public MonthChooser(DayOfMonthChooser dayOfMonthChooser) {
        this(dayOfMonthChooser, false);
    }

    public MonthChooser(boolean allowNoChoice) {
        this(null, allowNoChoice);
    }

    public MonthChooser(DayOfMonthChooser dayOfMonthChooser, boolean allowNoChoice) {
        super(dayOfMonthChooser, allowNoChoice);
    }

    @Override
    public String[] getItemList() {
        return enumValuesList(Month.values());
    }

    public Month getMonth() {
        int idx = (noChoiceOption ? (chooser.getSelectedIndex() - 1) : chooser.getSelectedIndex());
        return ((idx < 0) ? null : Month.getMonth(idx + 1));
    }

    @Override
    public void handleSelectedItem(@NotNull ItemEvent e) {
        super.handleSelectedItem(e);
        fireMonthChangedEvent(getMonth());
    }

    public void setMonth(Month month) {
        SwingUtilities.invokeLater(() -> chooser.setSelectedItem(Objects.toString(month, "")));
    }
}
