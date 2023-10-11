package com.projectgalen.lib.ui.components.combobox;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: PGJComboBox.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 10, 2023
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
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.beans.JavaBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

@SuppressWarnings({ "unused", "unchecked" })
@JavaBean(defaultProperty = "UI", description = "A combination of a text field and a drop-down list.")
@SwingContainer(false)
public class PGJComboBox<T> extends JComboBox<T> {

    protected @NotNull final List<T>             data;
    protected @NotNull       Function<T, String> stringFunction;
    protected                boolean             isOptional;

    public PGJComboBox() {
        this(Collections.emptyList(), false, null, Objects::toString);
    }

    public PGJComboBox(@NotNull List<T> data) {
        this(data, false, null, Objects::toString);
    }

    public PGJComboBox(@NotNull List<T> data, @NotNull Function<T, String> stringFuction) {
        this(data, false, null, stringFuction);
    }

    public PGJComboBox(@NotNull List<T> data, boolean isOptional, @NotNull Function<T, String> stringFuction) {
        this(data, isOptional, null, stringFuction);
    }

    public PGJComboBox(@NotNull List<T> data, boolean isOptional, @Nullable PGListCellRendererProxy<T> renderProxy, @NotNull Function<T, String> stringFuction) {
        super(new PGComboBoxModel<>());
        this.data           = new ArrayList<>(data);
        this.stringFunction = stringFuction;
        this.isOptional     = isOptional;
        super.setRenderer(new PGListCellRenderer<>(null, null, null, this::getDisplayStringImpl, renderProxy));
        super.setModel(new PGComboBoxModel<>(this::getDataImpl, this::isOptional));
    }

    public @Nullable @Override T getSelectedItem() {
        return getModelImpl().getSelectedItem();
    }

    public @NotNull List<T> getData() {
        return Collections.unmodifiableList(data);
    }

    public Color getNullItemBackground() {
        return getRendererImpl().getNullItemBackground();
    }

    public Color getNullItemForeground() {
        return getRendererImpl().getNullItemForeground();
    }

    public String getNullItemText() {
        return getRendererImpl().getNullItemText();
    }

    public @Nullable PGListCellRendererProxy<T> getRenderProxy() {
        return getRendererImpl().getRenderProxy();
    }

    public @NotNull Function<T, String> getStringFunction() {
        return stringFunction;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public void setData(@NotNull List<T> data) {
        setData(data, stringFunction);
    }

    public void setData(@NotNull List<T> data, @NotNull Function<T, String> stringFunction) {
        this.data.clear();
        this.data.addAll(data);
        this.stringFunction = stringFunction;
        fireContentsChanged();
    }

    public @Override void setModel(ComboBoxModel<T> aModel) {
        if(aModel instanceof PGComboBoxModel<T> model) super.setModel(model);
        fireContentsChanged();
    }

    public void setNullItemBackground(Color nullItemBackground) {
        getRendererImpl().setNullItemBackground(nullItemBackground);
        fireContentsChanged();
    }

    public void setNullItemForeground(Color nullItemForeground) {
        getRendererImpl().setNullItemForeground(nullItemForeground);
        fireContentsChanged();
    }

    public void setNullItemText(String nullItemText) {
        getRendererImpl().setNullItemText(nullItemText);
        fireContentsChanged();
    }

    public void setOptional(boolean optional) {
        isOptional = optional;
        fireContentsChanged();
    }

    public void setRenderProxy(@Nullable PGListCellRendererProxy<T> renderProxy) {
        getRendererImpl().setRenderProxy(renderProxy);
        fireContentsChanged();
    }

    public void setStringFunction(@NotNull Function<T, String> stringFunction) {
        this.stringFunction = stringFunction;
        fireContentsChanged();
    }

    private void fireContentsChanged() {
        if(dataModel instanceof PGComboBoxModel<T> model) model.fireContentsChanged();
    }

    private @NotNull List<T> getDataImpl() {
        return data;
    }

    private String getDisplayStringImpl(@Nullable T value) {
        return ofNullable(value).map(o -> stringFunction.apply(o)).orElse(null);
    }

    private PGComboBoxModel<T> getModelImpl() {
        return (PGComboBoxModel<T>)dataModel;
    }

    private @NotNull PGListCellRenderer<T> getRendererImpl() {
        return (PGListCellRenderer<T>)getRenderer();
    }
}
