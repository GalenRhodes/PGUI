package com.projectgalen.lib.ui;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: PGDefaultLookup.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 06, 2023
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

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class PGDefaultLookup {

    public PGDefaultLookup() { }

    public Object getDefault(@NotNull JComponent c, ComponentUI ui, String key) {
        return PGDefaultLookup.get(c, ui, key);
    }

    public static Object get(@NotNull JComponent c, ComponentUI ui, String key) {
        return UIManager.get(key, c.getLocale());
    }

    public static boolean getBoolean(JComponent c, ComponentUI ui, String key, boolean defaultValue) {
        return ((get(c, ui, key) instanceof Boolean b) ? b : defaultValue);
    }

    public static boolean getBoolean(JComponent c, ComponentUI ui, String key) {
        return getBoolean(c, ui, key, false);
    }

    public static Border getBorder(JComponent c, ComponentUI ui, String key, Border defaultValue) {
        return ((get(c, ui, key) instanceof Border v) ? v : defaultValue);
    }

    public static Border getBorder(JComponent c, ComponentUI ui, String key) {
        return getBorder(c, ui, key, null);
    }

    public static Color getColor(JComponent c, ComponentUI ui, String key, Color defaultValue) {
        return ((get(c, ui, key) instanceof Color v) ? v : defaultValue);
    }

    public static Color getColor(JComponent c, ComponentUI ui, String key) {
        return getColor(c, ui, key, null);
    }

    public static Icon getIcon(JComponent c, ComponentUI ui, String key, Icon defaultValue) {
        return ((get(c, ui, key) instanceof Icon v) ? v : defaultValue);
    }

    public static Icon getIcon(JComponent c, ComponentUI ui, String key) {
        return getIcon(c, ui, key, null);
    }

    public static Insets getInsets(JComponent c, ComponentUI ui, String key, Insets defaultValue) {
        return ((get(c, ui, key) instanceof Insets v) ? v : defaultValue);
    }

    public static Insets getInsets(JComponent c, ComponentUI ui, String key) {
        return getInsets(c, ui, key, null);
    }

    public static int getInt(JComponent c, ComponentUI ui, String key, int defaultValue) {
        return ((get(c, ui, key) instanceof Number v) ? v.intValue() : defaultValue);
    }

    public static int getInt(JComponent c, ComponentUI ui, String key) {
        return getInt(c, ui, key, -1);
    }
}
