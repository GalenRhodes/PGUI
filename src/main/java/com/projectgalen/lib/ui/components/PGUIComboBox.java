package com.projectgalen.lib.ui.components;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: PGUIComboBox.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: August 01, 2023
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
import com.projectgalen.lib.ui.interfaces.AbstractListCellRenderer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings({ "unchecked", "FieldCanBeLocal", "unused" })
public class PGUIComboBox<T> {
    private final EventListenerList listenerList = new EventListenerList();

    protected @RootPanel JPanel              panel;
    private              Function<T, String> stringFunction;
    private              List<T>             data                 = new ArrayList<>();
    private              JComboBox<T>        comboBox;
    private              PGUIComboBoxModel   model;
    private              boolean             isQuietOnSetSelected = false;
    private              boolean             isSettingSelected    = false;
    private              boolean             isOptional           = false;

    public PGUIComboBox() { }

    public void addItemListener(ItemListener listener) {
        listenerList.add(ItemListener.class, listener);
    }

    public @NotNull List<T> getData() {
        return Collections.unmodifiableList(data);
    }

    public @Nullable T getSelectedItem() {
        return (T)comboBox.getSelectedItem();
    }

    public Function<T, String> getStringFunction() {
        return stringFunction;
    }

    public boolean getVisible() {
        return comboBox.isVisible();
    }

    public boolean isEnabled() {
        return comboBox.isEnabled();
    }

    public boolean isOptional() {
        return isOptional;
    }

    public void removeItemListener(ItemListener listener) {
        listenerList.remove(ItemListener.class, listener);
    }

    public void setData(@NotNull List<T> data, @NotNull Function<T, String> stringFunction) {
        this.data.clear();
        this.data.addAll(data);
        this.stringFunction = stringFunction;
        model.fireContentsChanged();
    }

    public void setData(@NotNull List<T> data) {
        setData(data, stringFunction);
    }

    public void setEnabled(boolean flag) {
        comboBox.setEnabled(flag);
    }

    public void setOptional(boolean optional) {
        isOptional = optional;
    }

    public void setQuietOnSetSelected(boolean quietOnSetSelected) {
        isQuietOnSetSelected = quietOnSetSelected;
    }

    public void setSelectedIndex(int index) {
        isSettingSelected = true;
        try { comboBox.setSelectedIndex(index); } finally { isSettingSelected = false; }
    }

    public void setSelectedItem(T item) {
        isSettingSelected = true;
        try { comboBox.setSelectedItem(item); } finally { isSettingSelected = false; }
    }

    public void setStringFunction(Function<T, String> stringFunction) {
        this.stringFunction = stringFunction;
        model.fireContentsChanged();
    }

    public void setVisible(boolean flag) {
        comboBox.setVisible(flag);
    }

    private void createUIComponents() {
        stringFunction = Objects::toString;
        data           = new ArrayList<>();
        comboBox       = new PGUIComboBoxImpl(model = new PGUIComboBoxModel());
        comboBox.addItemListener(this::fireItemEvent);
    }

    private void fireItemEvent(ItemEvent e) {
        if(!(isQuietOnSetSelected && isSettingSelected)) for(ItemListener listener : listenerList.getListeners(ItemListener.class)) listener.itemStateChanged(e);
    }

    private final class PGUIComboBoxImpl extends JComboBox<T> {
        public PGUIComboBoxImpl(ComboBoxModel<T> m) {
            super(m);
            setRenderer(new PGUIComboBoxRenderer());
        }
    }

    private final class PGUIComboBoxModel extends AbstractListModel<T> implements ComboBoxModel<T> {
        private T selectedItem = null;

        public PGUIComboBoxModel() { }

        public void fireContentsChanged() {
            super.fireContentsChanged(this, 0, getSize());
        }

        @Override
        public @Nullable T getElementAt(int idx) {
            return (isOptional ? _get(idx - 1) : _get(idx));
        }

        @Override
        public Object getSelectedItem() {
            return selectedItem;
        }

        @Override
        public int getSize() {
            return (isOptional ? (data.size() + 1) : data.size());
        }

        @Override
        public void setSelectedItem(Object anItem) {
            selectedItem = (T)anItem;
        }

        private @Nullable T _get(int idx) {
            return (((idx >= 0) && (idx < data.size())) ? data.get(idx) : null);
        }
    }

    private final class PGUIComboBoxRenderer extends AbstractListCellRenderer<T> {
        public PGUIComboBoxRenderer() { }

        @Contract(pure = true)
        @Override
        public @NotNull String getDisplayString(@Nullable T obj) { return ((obj == null) ? "  " : stringFunction.apply(obj)); }
    }
}
