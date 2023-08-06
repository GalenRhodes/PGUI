package com.projectgalen.lib.ui.components;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: CalendarFace.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: August 03, 2023
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

import com.projectgalen.lib.ui.annotations.RootPanel;
import com.projectgalen.lib.ui.interfaces.CustomComponent;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.projectgalen.lib.ui.UI.execSafe;
import static com.projectgalen.lib.ui.UIStream.withChildStream;
import static java.lang.Integer.parseInt;

@SuppressWarnings("unused")
public class CalendarFace implements CustomComponent {

    public static final Color  COLOR_BG_MOUSEOVER = Color.LIGHT_GRAY;
    public static final Color  COLOR_FG_MOUSEOVER = Color.BLACK;
    public static final Color  COLOR_BG_NORMAL    = Color.WHITE;
    public static final Color  COLOR_FG_NORMAL    = Color.BLACK;
    public static final Color  COLOR_BG_MARKED    = Objects.requireNonNullElse(UIManager.getColor("Menu.selectionBackground"), Color.BLUE);
    public static final Color  COLOR_FG_MARKED    = Objects.requireNonNullElse(UIManager.getColor("Menu.selectionForeground"), Color.WHITE);
    public static final Cursor CURSOR_DEF         = new Cursor(Cursor.DEFAULT_CURSOR);
    public static final Cursor CURSOR_HAND        = new Cursor(Cursor.HAND_CURSOR);

    private static final @RegExp String RX2 = "c\\d+";
    private static final @RegExp String RX3 = "[wc]\\d+";
    private static final @RegExp String RX4 = "HRF8209";

    protected @RootPanel                 JPanel                    calendarPanel;
    protected @Range(from = 1, to = 7)   int                       firstWeekDay  = 1;
    protected @Range(from = 28, to = 31) int                       numberOfDays  = 31;
    protected @Range(from = 1, to = 31)  int                       selectedDate  = 1;
    protected @NotNull                   Function<Integer, String> toolTipFunc   = (d) -> null;
    protected final                      EventListenerList         listeners     = new EventListenerList();
    protected final                      MouseListener             mouseListener = new CalendarMouseListener();

    public CalendarFace() {
        SwingUtilities.invokeLater(() -> {
            Dimension maxSize = new Dimension();
            withChildStream(calendarPanel, JPanel.class, RX3, sp -> sp.forEach(p -> {
                maxSize.width  = Math.max(maxSize.width, p.getPreferredSize().width);
                maxSize.height = Math.max(maxSize.height, p.getPreferredSize().height);
            }));
            withChildStream(calendarPanel, JPanel.class, RX3, sp -> sp.forEach(p -> p.setMinimumSize(maxSize)));
        });
        updateUI();
    }

    public void addCalendarFaceListener(@NotNull CalendarFaceListener listener) {
        listeners.add(CalendarFaceListener.class, listener);
    }

    public int getFirstWeekDay() {
        return firstWeekDay;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public @Override JPanel getRootPanel() {
        return calendarPanel;
    }

    public int getSelectedDate() {
        return selectedDate;
    }

    public @NotNull Function<Integer, String> getToolTipFunc() {
        return toolTipFunc;
    }

    public void removeCalendarFaceListener(@NotNull CalendarFaceListener listener) {
        listeners.remove(CalendarFaceListener.class, listener);
    }

    public void setData(@Range(from = 1, to = 7) int firstWeekDay, @Range(from = 28, to = 31) int numberOfDays, @Range(from = 1, to = 31) int selectedDate) {
        this.firstWeekDay = firstWeekDay;
        this.numberOfDays = numberOfDays;
        this.selectedDate = Math.min(selectedDate, numberOfDays);
        if(this.selectedDate != selectedDate) SwingUtilities.invokeLater(this::fireCalendarFaceEvent);
        updateUI();
    }

    public void setFirstWeekDay(@Range(from = 1, to = 7) int firstWeekDay) {
        setData(firstWeekDay, numberOfDays, selectedDate);
    }

    public void setNumberOfDays(@Range(from = 28, to = 31) int numberOfDays) {
        setData(firstWeekDay, numberOfDays, selectedDate);
    }

    public void setSelectedDate(@Range(from = 1, to = 31) int selectedDate) {
        setData(firstWeekDay, numberOfDays, selectedDate);
    }

    public void setToolTipFunc(@NotNull Function<Integer, String> toolTipFunc) {
        this.toolTipFunc = toolTipFunc;
    }

    protected void fireCalendarFaceEvent() {
        if(SwingUtilities.isEventDispatchThread()) _fireCalendarEvent();
        else SwingUtilities.invokeLater(this::_fireCalendarEvent);
    }

    protected void updateUI() {
        if(SwingUtilities.isEventDispatchThread()) _updateUI();
        else SwingUtilities.invokeLater(this::_updateUI);
    }

    private void _fireCalendarEvent() {
        CalendarFaceEvent event = new CalendarFaceEvent(this, this.selectedDate);
        Stream.of(listeners.getListeners(CalendarFaceListener.class)).forEach(l -> execSafe(() -> l.dateSelected(event)));
    }

    private void _updateUI() {
        withChildStream(calendarPanel, JPanel.class, RX2, sp -> sp.forEach(p -> withChildStream(p, JLabel.class, RX4, sl -> sl.findFirst().ifPresent(l -> {
            int dt = (parseInt(p.getName().substring(1)) - (firstWeekDay - 1));
            p.removeMouseListener(mouseListener);
            if((dt < 1) || (dt > numberOfDays)) {
                l.setText(" ");
                l.setForeground(COLOR_FG_NORMAL);
                p.setBackground(COLOR_BG_NORMAL);
                p.setToolTipText(null);
            }
            else {
                l.setText(String.valueOf(dt));
                l.setForeground((dt == selectedDate) ? COLOR_FG_MARKED : COLOR_FG_NORMAL);
                p.setBackground((dt == selectedDate) ? COLOR_BG_MARKED : COLOR_BG_NORMAL);
                p.addMouseListener(mouseListener);
                p.setToolTipText(toolTipFunc.apply(dt));
            }
        }))));
        calendarPanel.repaint();
    }

    public interface CalendarFaceListener extends EventListener {
        void dateSelected(@NotNull CalendarFaceEvent event);
    }

    private final class CalendarMouseListener extends MouseAdapter {
        private Color  savedBgColor = COLOR_BG_NORMAL;
        private Color  savedFgColor = COLOR_FG_NORMAL;
        private Cursor savedCursor  = CURSOR_DEF;

        private CalendarMouseListener() { }

        public @Override void mouseClicked(@NotNull MouseEvent e) {
            if(e.getComponent() instanceof JPanel p) withChildStream(p, JLabel.class, RX4, s -> s.findFirst().ifPresent(l -> execSafe(() -> {
                selectedDate = parseInt(l.getText());
                fireCalendarFaceEvent();
                savedFgColor = COLOR_FG_MARKED;
                savedBgColor = COLOR_BG_MARKED;
                updateUI();
            })));
        }

        public @Override void mouseEntered(@NotNull MouseEvent e) {
            if(e.getComponent() instanceof JPanel p) withChildStream(p, JLabel.class, RX4, s -> s.findFirst().ifPresent(l -> {
                savedFgColor = l.getForeground();
                savedBgColor = p.getBackground();
                savedCursor  = p.getCursor();
                l.setForeground(COLOR_FG_MOUSEOVER);
                p.setBackground(COLOR_BG_MOUSEOVER);
                p.setCursor(CURSOR_HAND);
            }));
        }

        public @Override void mouseExited(@NotNull MouseEvent e) {
            if(e.getComponent() instanceof JPanel p) withChildStream(p, JLabel.class, RX4, s -> s.findFirst().ifPresent(l -> {
                l.setForeground(savedFgColor);
                p.setBackground(savedBgColor);
                p.setCursor(savedCursor);
            }));
        }
    }

    public final static class CalendarFaceEvent extends EventObject {

        public final int selectedDate;

        public CalendarFaceEvent(CalendarFace source, int selectedDate) {
            super(source);
            this.selectedDate = selectedDate;
        }

        public @Override CalendarFace getSource() {
            return (CalendarFace)super.getSource();
        }
    }
}
