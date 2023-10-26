package com.projectgalen.lib.ui;
// ================================================================================================================================
//     PROJECT: PGUI
//    FILENAME: PGJMenu.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 26, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any purpose with or without fee is hereby granted, provided
// that the above copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR
// CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
// NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ================================================================================================================================

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionListener;

@SuppressWarnings("unused")
public class PGJMenu extends JMenu {

    public PGJMenu(@NotNull String text, JComponent... items) {
        this(text, null, items);
    }

    public PGJMenu(@NotNull String text, @Nullable ActionListener listener, JComponent... items) {
        super(text);
        if(listener != null) addActionListener(listener);
        for(JComponent item : items) {
            if(item instanceof JMenuItem menuItem) add(menuItem);
            else if(item instanceof JPopupMenu.Separator separator) add(separator);
        }
    }
}
