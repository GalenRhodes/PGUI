package com.projectgalen.lib.ui.components;

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
import com.projectgalen.lib.ui.components.calendar.CalendarFaceEvent;
import com.projectgalen.lib.ui.components.calendar.CalendarFaceListener;
import com.projectgalen.lib.utils.delegates.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.intellij.uiDesigner.core.GridConstraints.*;
import static com.projectgalen.lib.ui.M.msgs;
import static com.projectgalen.lib.ui.UI.execSafe;
import static java.lang.Integer.parseInt;

@SuppressWarnings("unused")
public class PGCalendarFace extends JPanel implements NonGUIEditorCustomComponent {
    public static final Color  COLOR_GRID         = new Color(0, 0, 0, 255);
    public static final Color  COLOR_BG_WEEKDAY   = new Color(236, 236, 236, 255);
    public static final Color  COLOR_FG_WEEKDAY   = new Color(0, 0, 0, 255);
    public static final Color  COLOR_BG_MOUSEOVER = new Color(236, 236, 236, 255);
    public static final Color  COLOR_FG_MOUSEOVER = new Color(0, 0, 0, 255);
    public static final Color  COLOR_BG_NORMAL    = new Color(255, 255, 255, 255);
    public static final Color  COLOR_FG_NORMAL    = new Color(0, 0, 0, 255);
    public static final Color  COLOR_BG_MARKED    = Objects.requireNonNullElse(UIManager.getColor("Menu.selectionBackground"), new Color(109, 193, 179, 255));
    public static final Color  COLOR_FG_MARKED    = Objects.requireNonNullElse(UIManager.getColor("Menu.selectionForeground"), new Color(255, 255, 255, 255));
    public static final Cursor CURSOR_DEF         = new Cursor(Cursor.DEFAULT_CURSOR);
    public static final Cursor CURSOR_HAND        = new Cursor(Cursor.HAND_CURSOR);

    private static final String   LABEL_NAME = "HRF8209";
    private static final String[] dayKeys    = { "txt.weekday.short.sunday",
                                                 "txt.weekday.short.monday",
                                                 "txt.weekday.short.tuesday",
                                                 "txt.weekday.short.wednesday",
                                                 "txt.weekday.short.thursday",
                                                 "txt.weekday.short.friday",
                                                 "txt.weekday.short.saturday" };

    protected @Range(from = 1, to = 7)   int                       firstWeekDay  = 1;
    protected @Range(from = 28, to = 31) int                       numberOfDays  = 31;
    protected @Range(from = 1, to = 31)  int                       selectedDate  = 1;
    protected @NotNull                   Function<Integer, String> toolTipFunc   = (d) -> null;
    protected final                      EventListenerList         listeners     = new EventListenerList();
    protected final                      MouseListener             mouseListener = new CalendarMouseListener();
    protected final                      JPanel[]                  cells         = new JPanel[42];
    protected final                      JLabel[]                  labels        = new JLabel[42];

    public PGCalendarFace() {
        super(new GridLayoutManager(7, 7, new Insets(0, 0, 0, 0), 0, 0));
        setDoubleBuffered(true);
        setBackground(COLOR_GRID);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_GRID), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        Font              font   = getFont().deriveFont(Font.BOLD);
        Insets            margin = new Insets(3, 3, 3, 3);
        GridLayoutManager layout = new GridLayoutManager(1, 1, margin, 1, 1, true, true);

        for(int i = 0; i < 7; i++) {
            JPanel p = new JPanel(layout, true);
            JLabel l = new JLabel(msgs.getString(dayKeys[i]));
            l.setFont(font);
            add(p, createConstraint(0, i, ANCHOR_CENTER, FILL_BOTH, SIZEPOLICY_FIXED, SIZEPOLICY_FIXED));
            p.add(l, createConstraint(0, 0, ANCHOR_CENTER, FILL_NONE, SIZEPOLICY_FIXED, SIZEPOLICY_FIXED));
            p.setForeground(COLOR_FG_WEEKDAY);
            p.setBackground(COLOR_BG_WEEKDAY);
        }

        for(int i = 0; i < 42; i++) {
            JPanel p = new JPanel(layout, true);
            JLabel l = new JLabel(String.valueOf(i + 1));
            add(p, createConstraint(((i / 7) + 1), (i % 7), ANCHOR_CENTER, FILL_BOTH, SIZEPOLICY_FIXED, SIZEPOLICY_FIXED));
            p.add(l, createConstraint(0, 0, ANCHOR_CENTER, FILL_NONE, SIZEPOLICY_FIXED, SIZEPOLICY_FIXED));
            p.setBackground(COLOR_BG_NORMAL);
            p.setForeground(COLOR_FG_NORMAL);
            l.setName(LABEL_NAME);
            cells[i]  = p;
            labels[i] = l;
        }

        SwingUtilities.invokeLater(this::updateMinimumSizes);
        SwingUtilities.invokeLater(this::refreshUI);
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
        refreshUI();
    }

    public void setFirstWeekDay(int firstWeekDay) {
        setData(firstWeekDay, numberOfDays, selectedDate);
    }

    public void setNumberOfDays(int numberOfDays) {
        setData(firstWeekDay, numberOfDays, selectedDate);
    }

    public void setSelectedDate(int selectedDate) {
        setData(firstWeekDay, numberOfDays, selectedDate);
    }

    public void setToolTipFunc(@NotNull Function<Integer, String> toolTipFunc) {
        this.toolTipFunc = toolTipFunc;
        refreshUI();
    }

    protected void fireCalendarFaceEvent() {
        if(SwingUtilities.isEventDispatchThread()) {
            CalendarFaceEvent event = new CalendarFaceEvent(this, this.selectedDate);
            Stream.of(listeners.getListeners(CalendarFaceListener.class)).forEach(l -> execSafe(() -> l.dateSelected(event)));
        }
        else {
            SwingUtilities.invokeLater(this::fireCalendarFaceEvent);
        }
    }

    protected void refreshUI() {
        if(SwingUtilities.isEventDispatchThread()) {
            withCells((p, l, dt) -> {
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
                    p.setToolTipText(toolTipFunc.apply(dt));
                    p.addMouseListener(mouseListener);
                }
            });
            repaint();
        }
        else {
            SwingUtilities.invokeLater(this::refreshUI);
        }
    }

    protected void withCells(@NotNull TriConsumer<JPanel, JLabel, Integer> triConsumer) {
        for(int i = 0; i < 42; i++) triConsumer.accept(cells[i], labels[i], ((i + 1) - (firstWeekDay - 1)));
    }

    private void updateMinimumSizes() {
        Dimension maxSize = new Dimension();
        for(JPanel p : cells) {
            Dimension preferredSize = p.getPreferredSize();
            maxSize.width  = Math.max(maxSize.width, preferredSize.width);
            maxSize.height = Math.max(maxSize.width, preferredSize.height);
        }
        for(JPanel p : cells) p.setMinimumSize(maxSize);
    }

    private final class CalendarMouseListener extends MouseAdapter {
        private Color  savedBgColor = COLOR_BG_NORMAL;
        private Color  savedFgColor = COLOR_FG_NORMAL;
        private Cursor savedCursor  = CURSOR_DEF;

        private CalendarMouseListener() { }

        public @Override void mouseClicked(@NotNull MouseEvent e) {
            if(e.getComponent() instanceof JPanel panel) foo(panel, (p, l) -> {
                selectedDate = parseInt(l.getText());
                fireCalendarFaceEvent();
                savedFgColor = COLOR_FG_MARKED;
                savedBgColor = COLOR_BG_MARKED;
                refreshUI();
            });
        }

        public @Override void mouseEntered(@NotNull MouseEvent e) {
            if(e.getComponent() instanceof JPanel panel) foo(panel, (p, l) -> {
                savedFgColor = l.getForeground();
                savedBgColor = p.getBackground();
                savedCursor  = p.getCursor();
                l.setForeground(COLOR_FG_MOUSEOVER);
                p.setBackground(COLOR_BG_MOUSEOVER);
                p.setCursor(CURSOR_HAND);
            });
        }

        public @Override void mouseExited(@NotNull MouseEvent e) {
            if(e.getComponent() instanceof JPanel panel) foo(panel, (p, l) -> {
                l.setForeground(savedFgColor);
                p.setBackground(savedBgColor);
                p.setCursor(savedCursor);
            });
        }

        private void foo(@NotNull JPanel p, @NotNull BiConsumer<JPanel, JLabel> biConsumer) {
            for(Component c : p.getComponents()) if(c instanceof JLabel l && LABEL_NAME.equals(l.getName())) biConsumer.accept(p, l);
        }
    }
}
