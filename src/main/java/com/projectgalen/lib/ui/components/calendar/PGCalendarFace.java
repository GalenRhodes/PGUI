package com.projectgalen.lib.ui.components.calendar;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: PGCalendarFace.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 11, 2023
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

import com.intellij.uiDesigner.core.GridLayoutManager;
import com.projectgalen.lib.ui.base.NonGUIEditorCustomComponent;
import com.projectgalen.lib.utils.delegates.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.intellij.uiDesigner.core.GridConstraints.*;
import static com.projectgalen.lib.ui.M.msgs;
import static com.projectgalen.lib.ui.UI.execSafe;

@SuppressWarnings("unused")
public class PGCalendarFace extends JPanel implements NonGUIEditorCustomComponent, DateListener {

    public static final int CELL_COUNT = 42;

    private @Range(from = 1, to = 7)   int                       firstWeekDay        = 1;
    private @Range(from = 28, to = 31) int                       numberOfDays        = 31;
    private @Range(from = 1, to = 31)  int                       selectedDate        = 1;
    private @NotNull                   Function<Integer, String> toolTipFunc         = (d) -> null;
    private final                      EventListenerList         listeners           = new EventListenerList();
    private                            Color                     dateForeground      = new Color(0, 0, 0, 255);
    private                            Color                     dateBackground      = new Color(255, 255, 255, 255);
    private                            Color                     weekdayForeground   = new Color(0, 0, 0, 255);
    private                            Color                     weekdayBackground   = new Color(236, 236, 236, 255);
    private                            Color                     mouseOverForeground = new Color(0, 0, 0, 255);
    private                            Color                     mouseOverBackground = new Color(236, 236, 236, 255);
    private                            Color                     selectedForeground  = new Color(255, 255, 255, 255);
    private                            Color                     selectedBackground  = new Color(109, 193, 179, 255);
    private                            Color                     borderColor         = new Color(0, 0, 0, 255);
    private final                      DatePanel[]               dateCells           = new DatePanel[CELL_COUNT];
    private final                      JPanel[]                  weekDayCells        = new JPanel[7];

    public PGCalendarFace() {
        super();
        setupComponent();
    }

    public void addCalendarFaceListener(@NotNull CalendarFaceListener listener) {
        listeners.add(CalendarFaceListener.class, listener);
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public Color getDateBackground() {
        return dateBackground;
    }

    public Color getDateForeground() {
        return dateForeground;
    }

    public int getFirstWeekDay() {
        return firstWeekDay;
    }

    public Color getMouseOverBackground() {
        return mouseOverBackground;
    }

    public Color getMouseOverForeground() {
        return mouseOverForeground;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public Color getSelectedBackground() {
        return selectedBackground;
    }

    public int getSelectedDate() {
        return selectedDate;
    }

    public Color getSelectedForeground() {
        return selectedForeground;
    }

    public @NotNull Function<Integer, String> getToolTipFunc() {
        return toolTipFunc;
    }

    public Color getWeekdayBackground() {
        return weekdayBackground;
    }

    public Color getWeekdayForeground() {
        return weekdayForeground;
    }

    public @Override void onDateEvent(@NotNull DateEvent e) {
        if(selectedDate != e.date) {
            selectedDate = e.date;
            withCells((p, d, v) -> p.setSelected(selectedDate == d));
            SwingUtilities.invokeLater(this::fireCalendarFaceEvent);
            repaint();
        }
    }

    public void removeCalendarFaceListener(@NotNull CalendarFaceListener listener) {
        listeners.remove(CalendarFaceListener.class, listener);
    }

    public @Override void setBackground(Color bg) {
        setBorderColor(bg);
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        super.setBackground(borderColor);
        setBorder(BorderFactory.createLineBorder(borderColor));
    }

    public void setData(@Range(from = 1, to = 7) int firstWeekDay, @Range(from = 28, to = 31) int numberOfDays, @Range(from = 1, to = 31) int selectedDate) {
        int dt = Math.min(selectedDate, numberOfDays);

        if((this.firstWeekDay != firstWeekDay) || (this.numberOfDays != numberOfDays)) {
            this.firstWeekDay = firstWeekDay;
            this.numberOfDays = numberOfDays;
            refreshUI();
        }

        if(this.selectedDate != dt) {
            this.selectedDate = dt;
            withCells((p, d, v) -> p.setSelected(p.getDate() == this.selectedDate));
            repaint();
            SwingUtilities.invokeLater(this::fireCalendarFaceEvent);
        }
    }

    public void setDateBackground(Color dateBackground) {
        this.dateBackground = dateBackground;
        withCells((p, d, v) -> p.setBackground(dateBackground));
    }

    public void setDateForeground(Color dateForeground) {
        this.dateForeground = dateForeground;
        withCells((p, d, v) -> p.setForeground(dateForeground));
    }

    public void setFirstWeekDay(int firstWeekDay) {
        setData(firstWeekDay, numberOfDays, selectedDate);
    }

    public @Override void setFont(@NotNull Font font) {
        int  style       = (font.getStyle() & Font.ITALIC);
        Font dateFont    = font.deriveFont(style);
        Font weekDayFont = font.deriveFont(style | Font.BOLD);
        super.setFont(dateFont);
        withWeekDays((p, d) -> p.setFont(weekDayFont));
        withCells((p, d, v) -> p.setFont(dateFont));
    }

    public void setMouseOverBackground(Color mouseOverBackground) {
        this.mouseOverBackground = mouseOverBackground;
        withCells((p, d, v) -> p.setMouseOverBackground(mouseOverBackground));
    }

    public void setMouseOverForeground(Color mouseOverForeground) {
        this.mouseOverForeground = mouseOverForeground;
        withCells((p, d, v) -> p.setMouseOverForeground(mouseOverForeground));
    }

    public void setNumberOfDays(int numberOfDays) {
        setData(firstWeekDay, numberOfDays, selectedDate);
    }

    public void setSelectedBackground(Color selectedBackground) {
        this.selectedBackground = selectedBackground;
        withCells((p, d, v) -> p.setSelectedBackground(selectedBackground));
    }

    public void setSelectedDate(int selectedDate) {
        setData(firstWeekDay, numberOfDays, selectedDate);
    }

    public void setSelectedForeground(Color selectedForeground) {
        this.selectedForeground = selectedForeground;
        withCells((p, d, v) -> p.setSelectedForeground(selectedForeground));
    }

    public void setToolTipFunc(@NotNull Function<Integer, String> toolTipFunc) {
        this.toolTipFunc = toolTipFunc;
        refreshUI();
    }

    public void setWeekdayBackground(Color weekdayBackground) {
        this.weekdayBackground = weekdayBackground;
        withWeekDays((p, d) -> p.setBackground(weekdayBackground));
    }

    public void setWeekdayForeground(Color weekdayForeground) {
        this.weekdayForeground = weekdayForeground;
        withWeekDays((p, d) -> p.setForeground(weekdayForeground));
    }

    protected void fireCalendarFaceEvent() {
        if(SwingUtilities.isEventDispatchThread()) {
            CalendarFaceEvent event = new CalendarFaceEvent(this, selectedDate);
            Stream.of(listeners.getListeners(CalendarFaceListener.class)).forEach(l -> execSafe(() -> l.dateSelected(event)));
        }
        else {
            SwingUtilities.invokeLater(this::fireCalendarFaceEvent);
        }
    }

    protected void refreshUI() {
        if(SwingUtilities.isEventDispatchThread()) {
            withCells((p, d, v) -> p.updateInfo(d, (v ? toolTipFunc.apply(d) : null)));
            repaint();
        }
        else {
            SwingUtilities.invokeLater(this::refreshUI);
        }
    }

    protected void setupComponent() {
        setLayout(new GridLayoutManager(7, 7, new Insets(0, 0, 0, 0), 0, 0));
        setDoubleBuffered(true);
        setBackground(borderColor);
        setBorder(BorderFactory.createLineBorder(borderColor));

        Font     dateFont    = getFont().deriveFont(Font.PLAIN);
        Font     weekDayFont = getFont().deriveFont(Font.BOLD);
        String[] dayNames    = msgs.getString("txt.weekdays.short").split("\\s*,\\s*");

        for(int i = 0; i < 7; i++) {
            JPanel p = new JPanel(DatePanel.LAYOUT, true);
            JLabel l = new JLabel(dayNames[i]);
            l.setFont(weekDayFont);
            add(p, createConstraint(0, i, ANCHOR_CENTER, FILL_BOTH, SIZEPOLICY_FIXED, SIZEPOLICY_FIXED));
            p.add(l, createConstraint(0, 0, ANCHOR_CENTER, FILL_NONE, SIZEPOLICY_FIXED, SIZEPOLICY_FIXED));
            p.setForeground(weekdayForeground);
            p.setBackground(weekdayBackground);
            weekDayCells[i] = p;
        }

        for(int i = 0; i < CELL_COUNT; i++) {
            DatePanel dp = new DatePanel(i + 1, dateForeground, dateBackground, mouseOverForeground, mouseOverBackground, selectedForeground, selectedBackground);
            dp.setFont(dateFont);
            add(dp, createConstraint(((i / 7) + 1), (i % 7), ANCHOR_CENTER, FILL_BOTH, SIZEPOLICY_FIXED, SIZEPOLICY_FIXED));
            dp.addDateListener(this);
            dateCells[i] = dp;
        }

        SwingUtilities.invokeLater(this::updateMinimumSizes);
        SwingUtilities.invokeLater(this::refreshUI);
    }

    protected void updateMinimumSizes() {
        Dimension maxSize = new Dimension();
        withCells((p, d, v) -> {
            Dimension preferredSize = p.getPreferredSize();
            maxSize.width  = Math.max(maxSize.width, preferredSize.width);
            maxSize.height = Math.max(maxSize.width, preferredSize.height);
        });
        withCells((p, d, v) -> p.setMinimumSize(maxSize));
    }

    protected void withCells(@NotNull TriConsumer<DatePanel, Integer, Boolean> triConsumer) {
        if(dateCells != null) for(int i = 0; i < dateCells.length; i++) {
            DatePanel cell = dateCells[i];
            if(cell != null) {
                int     d = ((i + 1) - (firstWeekDay - 1));
                boolean v = ((d >= 1) && (d <= numberOfDays));
                triConsumer.accept(cell, (v ? d : -1), v);
            }
        }
    }

    protected void withWeekDays(@NotNull BiConsumer<JPanel, Integer> biConsumer) {
        if(weekDayCells != null) for(int i = 0; i < weekDayCells.length; i++) {
            JPanel cell = weekDayCells[i];
            if(cell != null) biConsumer.accept(cell, i);
        }
    }
}
