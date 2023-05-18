package com.projectgalen.lib.ui.events;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: KeyValueInfoPanelButtonEvent.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 01, 2023
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

import com.projectgalen.lib.ui.ButtonChoice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EventObject;

@SuppressWarnings("unused")
public class InfoPanelButtonEvent extends EventObject {
    private final ButtonChoice button;
    private final int          selectedIndex;
    private final Object       selectedObject;
    private final Object[]     selectedObjects;
    private final int[]        selectedIndicies;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     *
     * @throws IllegalArgumentException if source is null
     */
    public InfoPanelButtonEvent(@NotNull Object source, @NotNull ButtonChoice button, int index, @Nullable Object selected) {
        super(source);
        this.button = button;
        this.selectedIndex = index;
        this.selectedObject = selected;
        this.selectedIndicies = new int[]{ index };
        this.selectedObjects = new Object[]{ selected };
    }

    public InfoPanelButtonEvent(@NotNull Object source, @NotNull ButtonChoice button, int @NotNull [] indicies, Object @NotNull [] selected) {
        super(source);
        this.button = button;
        this.selectedObjects = selected;
        this.selectedIndicies = indicies;
        this.selectedObject = ((selected.length == 0) ? null : selected[0]);
        this.selectedIndex = ((indicies.length == 0) ? -1 : indicies[0]);
    }

    public ButtonChoice getButton() {
        return button;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public int[] getSelectedIndicies() {
        return selectedIndicies;
    }

    public Object getSelectedObject() {
        return selectedObject;
    }

    public Object[] getSelectedObjects() {
        return selectedObjects;
    }
}
