package com.projectgalen.lib.ui.components.calendar;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: DatePanel.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 12, 2023
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
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static com.intellij.uiDesigner.core.GridConstraints.*;

@SuppressWarnings("unused")
public final class DatePanel extends JPanel implements NonGUIEditorCustomComponent, MouseListener {

    public static final GridLayoutManager LAYOUT      = new GridLayoutManager(1, 1, new Insets(3, 3, 3, 3), 1, 1, true, true);
    public static final Cursor            CURSOR_DEF  = new Cursor(Cursor.DEFAULT_CURSOR);
    public static final Cursor            CURSOR_HAND = new Cursor(Cursor.HAND_CURSOR);

    private final EventListenerList listeners   = new EventListenerList();
    private final JLabel            dateLabel;
    private       Color             selectedForeground;
    private       Color             selectedBackground;
    private       Color             mouseOverForeground;
    private       Color             mouseOverBackground;
    private       Color             normalForeground;
    private       Color             normalBackground;
    private       boolean           isSelected;
    private       boolean           isMouseOver = false;
    private       int               date;

    public DatePanel(int date, @NotNull Color fg, @NotNull Color bg, @NotNull Color mouseOverFg, @NotNull Color mouseOverBg, @NotNull Color selectedFg, @NotNull Color selectedBg) {
        super(LAYOUT, true);
        this.date                = date;
        this.isSelected          = false;
        this.normalForeground    = fg;
        this.normalBackground    = bg;
        this.mouseOverForeground = mouseOverFg;
        this.mouseOverBackground = mouseOverBg;
        this.selectedForeground  = selectedFg;
        this.selectedBackground  = selectedBg;

        add(this.dateLabel = new JLabel(getLabelText()), createConstraint(0, 0, ANCHOR_CENTER, FILL_NONE, SIZEPOLICY_FIXED, SIZEPOLICY_FIXED));
        addMouseListener(this);
        setColors();
    }

    public void addDateListener(@NotNull DateListener listener)    { listeners.add(DateListener.class, listener); }

    public int getDate()                                           { return date; }

    public Color getMouseOverBackground()                          { return mouseOverBackground; }

    public Color getMouseOverForeground()                          { return mouseOverForeground; }

    public Color getSelectedBackground()                           { return selectedBackground; }

    public Color getSelectedForeground()                           { return selectedForeground; }

    public boolean isSelected()                                    { return isSelected; }

    public @Override void mouseClicked(@NotNull MouseEvent e)      { if(e.getComponent() instanceof DatePanel p) p.onMouseClicked(); }

    public @Override void mouseEntered(@NotNull MouseEvent e)      { if(e.getComponent() instanceof DatePanel p) p.onMouseEntered(); }

    public @Override void mouseExited(@NotNull MouseEvent e)       { if(e.getComponent() instanceof DatePanel p) p.onMouseExited(); }

    public @Override void mousePressed(MouseEvent e)               { }

    public @Override void mouseReleased(MouseEvent e)              { }

    public void removeDateListener(@NotNull DateListener listener) { listeners.remove(DateListener.class, listener); }

    public @Override void setBackground(Color background) {
        normalBackground = background;
        setColors();
    }

    public void setDate(int date) {
        this.date = date;
        dateLabel.setText(getLabelText());
        isSelected = (this.date >= 1 && isSelected);
    }

    public @Override void setForeground(Color foreground) {
        normalForeground = foreground;
        setColors();
    }

    public void setMouseOverBackground(Color mouseOverBackground) {
        this.mouseOverBackground = mouseOverBackground;
        setColors();
    }

    public void setMouseOverForeground(Color mouseOverForeground) {
        this.mouseOverForeground = mouseOverForeground;
        setColors();
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        setColors();
    }

    public void setSelectedBackground(Color selectedBackground) {
        this.selectedBackground = selectedBackground;
        setColors();
    }

    public void setSelectedForeground(Color selectedForeground) {
        this.selectedForeground = selectedForeground;
        setColors();
    }

    public void updateInfo(int date, String toolTipText) {
        setDate(date);
        setToolTipText((date < 1) ? null : toolTipText);
    }

    private @NotNull String getLabelText() {
        return (this.date < 1) ? " " : String.valueOf(this.date);
    }

    private void onMouseClicked() {
        if((date > 0) && isMouseOver) {
            DateEvent event = new DateEvent(this, date);
            for(DateListener l : listeners.getListeners(DateListener.class)) l.onDateEvent(event);
        }
    }

    private void onMouseEntered() {
        if(date > 0) {
            isMouseOver = true;
            setCursor(CURSOR_HAND);
            setColors();
        }
    }

    private void onMouseExited() {
        if(date > 0) {
            isMouseOver = false;
            setCursor(CURSOR_DEF);
            setColors();
        }
    }

    private void setColors() {
        super.setBackground((isSelected ? selectedBackground : (isMouseOver ? mouseOverBackground : normalBackground)));
        super.setForeground((isSelected ? selectedForeground : (isMouseOver ? mouseOverForeground : normalForeground)));
    }
}
