package com.projectgalen.lib.ui;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: Fonts.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 27, 2023
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

import com.projectgalen.lib.utils.U;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Set;
import java.util.TreeSet;

import static com.projectgalen.lib.ui.M.msgs;

@SuppressWarnings("unused")
public final class Fonts {

    private Fonts() { }

    public static @NotNull Font addFontStyle(@NotNull Font font, int addStyle) {
        return changeFont(font, (font.getStyle() | addStyle), font.getSize());
    }

    public static @NotNull Font changeFace(@NotNull Font faceFont, @Nullable Font styleSizeFont) {
        return (styleSizeFont == null) ? faceFont : new Font(faceFont.getFamily(), styleSizeFont.getStyle(), styleSizeFont.getSize());
    }

    public static @NotNull Font changeFont(@NotNull Font font, int style, int size) {
        if((size == font.getSize()) && (style == font.getStyle())) return font;
        return new Font(font.getFamily(), style, size);
    }

    public static @NotNull Font changeFontSize(@NotNull Font font, int newSize) {
        return changeFont(font, font.getStyle(), newSize);
    }

    public static @NotNull Font changeFontStyle(@NotNull Font font, int newStyle) {
        return changeFont(font, newStyle, font.getSize());
    }

    public static @NotNull String fontStyleName(@NotNull Font font) {
        switch (font.getStyle()) {/*@f0*/
            case Font.PLAIN:  return msgs.getString("font.style.plain");
            case Font.BOLD:   return msgs.getString("font.style.bold");
            case Font.ITALIC: return msgs.getString("font.style.italic");
            default:          return msgs.getString("font.style.bold_italic");
        }/*@f1*/
    }

    public static @NotNull Set<String> getFontFamilyNames() {
        Font[]      fonts        = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        Set<String> fontFamilies = new TreeSet<>();
        for(Font f : fonts) fontFamilies.add(f.getFamily());
        return fontFamilies;
    }

    public static void setBorderFonts(@Nullable Border b, @NotNull Font font) {
        if(b != null) {
            if(b instanceof TitledBorder) {
                TitledBorder titledBorder = (TitledBorder)b;
                titledBorder.setTitleFont(changeFace(font, titledBorder.getTitleFont()));
            }
            else if(b instanceof CompoundBorder) {
                CompoundBorder cb = (CompoundBorder)b;
                setBorderFonts(cb.getInsideBorder(), font);
                setBorderFonts(cb.getOutsideBorder(), font);
            }
        }
    }

    public static void setFonts(@NotNull Component c, @NotNull Font font, String... menuItemsToIgnore) {
        if(c instanceof Container) {
            for(Component cc : ((Container)c).getComponents()) setFonts(cc, font, menuItemsToIgnore);
        }
        if(c instanceof JComponent) {
            setBorderFonts(((JComponent)c).getBorder(), font);
        }
        if(c instanceof JMenu) {
            setMenuFonts((JMenu)c, font, menuItemsToIgnore);
        }
        c.setFont(changeFace(font, c.getFont()));
    }

    public static void setFonts(@NotNull Component c, @NotNull Font font) {
        setFonts(c, font, msgs.getString("menu.font"));
    }

    public static void setMenuFonts(@NotNull JMenu m, @NotNull Font font) {
        setMenuFonts(m, font, msgs.getString("menu.font"));
    }

    public static void setMenuFonts(@NotNull JMenu m, @NotNull Font font, String... menuItemsToIgnore) {
        if(!U.isObjIn(m.getText(), menuItemsToIgnore)) for(Component mc : m.getMenuComponents()) setFonts(mc, font);
    }
}
