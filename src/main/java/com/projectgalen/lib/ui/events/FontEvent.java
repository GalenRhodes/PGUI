package com.projectgalen.lib.ui.events;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: FontEvent.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 17, 2023
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

import java.awt.*;
import java.util.EventObject;

public class FontEvent extends EventObject {
    private final Font oldFont;
    private final Font newFont;
    private final int  oldFontSizeAdjustment;
    private final int  newFontSizeAdjustment;

    public FontEvent(Object source, Font oldFont, Font newFont, int oldFontSizeAdjustment, int newFontSizeAdjustment) {
        super(source);
        this.oldFont               = oldFont;
        this.newFont               = newFont;
        this.oldFontSizeAdjustment = oldFontSizeAdjustment;
        this.newFontSizeAdjustment = newFontSizeAdjustment;
    }

    public Font getNewFont() {
        return newFont;
    }

    public int getNewFontSizeAdjustment() {
        return newFontSizeAdjustment;
    }

    public Font getOldFont() {
        return oldFont;
    }

    public int getOldFontSizeAdjustment() {
        return oldFontSizeAdjustment;
    }
}
