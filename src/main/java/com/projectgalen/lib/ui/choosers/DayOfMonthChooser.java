package com.projectgalen.lib.ui.choosers;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: DayOfMonthChooser.java
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

import com.projectgalen.lib.ui.events.MonthChangedEvent;
import com.projectgalen.lib.ui.events.YearChangedEvent;
import com.projectgalen.lib.ui.listeners.MonthChangedListener;
import com.projectgalen.lib.ui.listeners.YearChangedListener;
import com.projectgalen.lib.utils.Dates;
import com.projectgalen.lib.utils.enums.Month;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

@SuppressWarnings("unused")
public class DayOfMonthChooser extends BasicChooser implements MonthChangedListener, YearChangedListener {

    private Month month;
    private int   year;

    public DayOfMonthChooser() {
        this(null, -1, false);
    }

    public DayOfMonthChooser(boolean noChoiceOption) {
        this(null, -1, noChoiceOption);
    }

    public DayOfMonthChooser(@Nullable Month month, int year, boolean noChoiceOption) {
        super(noChoiceOption);
        this.month = month;
        this.year  = year;
    }

    public Integer getDayOfMonth() {
        int idx = getAdjSelectedIndex();
        return ((idx < 0) ? null : (idx + 1));
    }

    @Override
    public String[] getItemList() {
        int d = Dates.daysInMonth(Objects.requireNonNullElse(month, Month.JANUARY).getId(), ((year < 1) ? 2020 : year));
        for(int i = 1; i <= d; i++) chooser.addItem(String.valueOf(i));
        return new String[0];
    }

    public Month getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    @Override
    public void monthChanged(@NotNull MonthChangedEvent e) {
        setMonth(e.getNewMonth());
    }

    public void setDayOfMonth(Integer dayOfMonth) {
        SwingUtilities.invokeLater(() -> {
            if(Objects.requireNonNullElse(dayOfMonth, 0) < 1) chooser.setSelectedIndex(-1);
            else chooser.setSelectedItem(dayOfMonth.toString());
            fireReloadList();
        });
    }

    public void setMonth(Month month) {
        setMonthAndYear(month, year);
    }

    public void setMonthAndYear(Month month, int year) {
        this.month = month;
        this.year  = year;
        fireReloadList();
    }

    public void setYear(int year) {
        setMonthAndYear(month, year);
    }

    @Override
    public void yearChanged(@NotNull YearChangedEvent e) {
        setYear(e.getNewYear());
    }
}
