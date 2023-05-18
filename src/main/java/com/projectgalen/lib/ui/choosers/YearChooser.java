package com.projectgalen.lib.ui.choosers;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: YearChooser.java
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

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ItemEvent;

@SuppressWarnings("unused")
public class YearChooser extends DayInformer {

    private int startYear, endYear;

    public YearChooser() {
        this(2023, 2123, null, false);
    }

    public YearChooser(int startYear, int endYear, DayOfMonthChooser dayOfMonthChooser, boolean noChoiceOption) {
        super(dayOfMonthChooser, noChoiceOption);
        this.startYear = startYear;
        this.endYear = endYear;
    }

    public int getEndYear() {
        return endYear;
    }

    @Override
    public String[] getItemList() {
        return integerRangeList(startYear, endYear);
    }

    public int getStartYear() {
        return startYear;
    }

    public Integer getYear() {
        String s = getAdjSelectedItem();
        return ((s == null) ? null : Integer.parseInt(s));
    }

    @Override
    public void handleSelectedItem(@NotNull ItemEvent e) {
        super.handleSelectedItem(e);
        fireYearChangedEvent(getYear());
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
        fireReloadList();
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
        fireReloadList();
    }

    public void setYear(Integer i) {
        SwingUtilities.invokeLater(() -> chooser.setSelectedItem((i == null) ? "" : String.valueOf(i)));
    }
}
