package com.projectgalen.lib.ui.choosers;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: DayInformer.java
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
import com.projectgalen.lib.utils.enums.Month;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class DayInformer extends BasicChooser {

    public DayInformer(DayOfMonthChooser dayOfMonthChooser, boolean noChoiceOption) {
        super(noChoiceOption);
        if(dayOfMonthChooser != null) {
            addMonthChangeListener(dayOfMonthChooser);
            addYearChangeListener(dayOfMonthChooser);
        }
    }

    public void addMonthChangeListener(@NotNull MonthChangedListener listener) {
        eventListeners.add(MonthChangedListener.class, listener);
    }

    public void addYearChangeListener(@NotNull YearChangedListener listener) {
        eventListeners.add(YearChangedListener.class, listener);
    }

    public void removeMonthChangeListener(@NotNull MonthChangedListener listener) {
        eventListeners.remove(MonthChangedListener.class, listener);
    }

    public void removeYearChangeListener(@NotNull YearChangedListener listener) {
        eventListeners.remove(YearChangedListener.class, listener);
    }

    protected void fireMonthChangedEvent(Month newMonth) {
        MonthChangedEvent      e         = new MonthChangedEvent(this, newMonth);
        MonthChangedListener[] listeners = eventListeners.getListeners(MonthChangedListener.class);
        for(MonthChangedListener listener : listeners) listener.monthChanged(e);
    }

    protected void fireYearChangedEvent(int newYear) {
        YearChangedEvent      e         = new YearChangedEvent(this, newYear);
        YearChangedListener[] listeners = eventListeners.getListeners(YearChangedListener.class);
        for(YearChangedListener listener : listeners) listener.yearChanged(e);
    }
}
