package com.projectgalen.lib.ui.choosers;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: BasicChooser.java
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

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.projectgalen.lib.ui.UI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Objects;

@SuppressWarnings("unused")
public abstract class BasicChooser extends JPanel implements ItemSelectable {
    protected final EventListenerList eventListeners = new EventListenerList();
    protected final JComboBox<String> chooser;
    protected       boolean           noChoiceOption;

    public BasicChooser(boolean noChoiceOption) {
        super(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
        setBorder(null);
        chooser = new JComboBox<>();
        add(chooser, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, UI.CANGROW_CANSHRINK, UI.CANGROW_CANSHRINK, null, null, null, 0, false));
        this.noChoiceOption = noChoiceOption;
        chooser.addItemListener(this::handleSelectedItem);
        fireReloadList();
    }

    @Override
    public void addItemListener(@NotNull ItemListener listener) {
        eventListeners.add(ItemListener.class, listener);
    }

    public void fireReloadList() {
        SwingUtilities.invokeLater(() -> {
            Object obj = chooser.getSelectedItem();
            chooser.removeAllItems();
            if(noChoiceOption) chooser.addItem("");
            for(String s : getItemList()) chooser.addItem(s);
            chooser.setSelectedItem(obj);
        });
    }

    public void fireSelectedItemEvent(@NotNull ItemEvent e) {
        SwingUtilities.invokeLater(() -> { for(ItemListener listener : eventListeners.getListeners(ItemListener.class)) listener.itemStateChanged(e); });
    }

    public abstract String[] getItemList();

    @Override
    public Object[] getSelectedObjects() {
        return chooser.getSelectedObjects();
    }

    public void handleSelectedItem(@NotNull ItemEvent e) {
        fireSelectedItemEvent(new ItemEvent(this, e.getID(), this, e.getStateChange()));
    }

    public boolean isNoChoiceOption() {
        return noChoiceOption;
    }

    @Override
    public void removeItemListener(@NotNull ItemListener listener) {
        eventListeners.remove(ItemListener.class, listener);
    }

    public void setNoChoiceOption(boolean noChoiceOption) {
        SwingUtilities.invokeLater(() -> {
            if(this.noChoiceOption && !noChoiceOption) {
                chooser.removeItemAt(0);
            }
            else if(noChoiceOption && !this.noChoiceOption) {
                chooser.insertItemAt("", 0);
            }
            this.noChoiceOption = noChoiceOption;
        });
    }

    protected <E extends Enum<E>> String[] enumValuesList(E @NotNull [] e) {
        String[] out = new String[e.length];
        for(int i = 0; i < out.length; i++) out[i] = e[i].toString();
        return out;
    }

    protected int getAdjSelectedIndex() {
        int idx = chooser.getSelectedIndex();
        return ((idx < 0) ? -1 : (noChoiceOption ? (idx - 1) : idx));
    }

    protected String getAdjSelectedItem() {
        String str = Objects.toString(chooser.getSelectedItem(), "").trim();
        return ((str.length() > 0) ? str : null);
    }

    @NotNull
    protected String[] integerRangeList(int start, int end) {
        if(start == end) return new String[] { String.valueOf(start) };

        int     sz  = ((Math.max(start, end) - Math.min(start, end)) + 1);
        int     num = start;
        boolean f   = (start < end);

        String[] out = new String[sz];

        for(int i = 0; i < sz; i++) {
            out[i] = String.valueOf(num);
            num    = (f ? (num + 1) : (num - 1));
        }

        return out;
    }
}
