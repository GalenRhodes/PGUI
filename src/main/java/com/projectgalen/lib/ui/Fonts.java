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
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.projectgalen.lib.ui.M.msgs;
import static java.util.Optional.ofNullable;

@SuppressWarnings("unused")
public final class Fonts {

    private static final List<TextAttribute> NEG = List.of(TextAttribute.FONT, TextAttribute.FAMILY);

    private Fonts() { }

    public static @NotNull Font addFontStyle(@NotNull Font font, @MagicConstant(flags = { Font.BOLD, Font.ITALIC }) int addStyle) {
        return font.deriveFont(font.getStyle() | addStyle);
    }

    public static @NotNull Font changeFontFamily(@NotNull Font font, @NotNull String fontFamilyName) {
        @SuppressWarnings("unchecked") Map<TextAttribute, ? super Object> attrs = (Map<TextAttribute, ? super Object>)getFilteredAttributes(font, true);
        attrs.put(TextAttribute.FAMILY, fontFamilyName);
        return new Font(attrs);
    }

    public static @NotNull Font copyFontAttributes(@NotNull Font dest, @Nullable Font src) {
        return ofNullable(src).map(f -> dest.deriveFont(f.getStyle(), (float)f.getSize())).orElse(dest);
    }

    public static @NotNull Map<TextAttribute, ?> createAttributes(FontStyles @NotNull ... styles) {
        Map<TextAttribute, ? super Object> attr = new HashMap<>();
        for(FontStyles style : styles) attr.put(style.getAttribute(), style.getValue());
        return attr;
    }

    public static @NotNull Map<TextAttribute, ?> createAttributes(float size, FontStyles @NotNull ... styles) {
        @SuppressWarnings("unchecked") Map<TextAttribute, ? super Object> attr = (Map<TextAttribute, ? super Object>)createAttributes(styles);
        if(size >= 0.0f) attr.put(TextAttribute.SIZE, size);
        return attr;
    }

    public static @NotNull Map<TextAttribute, ?> createAttributes(@NotNull String familyName, float size, FontStyles @NotNull ... styles) {
        @SuppressWarnings("unchecked") Map<TextAttribute, ? super Object> attrs = (Map<TextAttribute, ? super Object>)createAttributes(familyName, styles);
        if(size >= 0.0f) attrs.put(TextAttribute.SIZE, size);
        return attrs;
    }

    public static @NotNull Map<TextAttribute, ?> createAttributes(@NotNull String familyName, FontStyles @NotNull ... styles) {
        @SuppressWarnings("unchecked") Map<TextAttribute, ? super Object> attr = (Map<TextAttribute, ? super Object>)createAttributes(styles);
        attr.put(TextAttribute.FAMILY, familyName);
        return attr;
    }

    public static @NotNull String fontStyleName(@NotNull Font font) {
        return switch(font.getStyle()) {/*@f0*/
            case Font.PLAIN  -> msgs.getString("font.style.plain");
            case Font.BOLD   -> msgs.getString("font.style.bold");
            case Font.ITALIC -> msgs.getString("font.style.italic");
            default          -> msgs.getString("font.style.bold_italic");
        };/*@f1*/
    }

    public static @NotNull Stream<Font> getAllFonts() {
        return ofNullable(GraphicsEnvironment.getLocalGraphicsEnvironment()).map(e -> Stream.of(e.getAllFonts()).sorted(Comparator.comparing(Font::getFontName))).orElseGet(Stream::empty);
    }

    /**
     * Get the attributes of the given font without the {@link TextAttribute#FONT} and {@link TextAttribute#FAMILY} entries.  If the {@link TextAttribute#SIZE} attribute is not included in the entries
     * then it will be added by calling the font's {@link Font#getSize()} method and adding it to the resulting map.
     *
     * @param font The font.
     *
     * @return The font's remaining attributes after {@link TextAttribute#FONT} and {@link TextAttribute#FAMILY} are removed.
     */
    public static @NotNull Map<TextAttribute, ?> getFilteredAttributes(@NotNull Font font) {
        return getFilteredAttributes(font, true);
    }

    /**
     * Get the attributes of the given font without the {@link TextAttribute#FONT} and {@link TextAttribute#FAMILY} entries.
     *
     * @param font                  The font.
     * @param copySizeIfNotIncluded If <code>true</code> and the {@link TextAttribute#SIZE} attribute is not included in the entries then it will be added by calling the font's {@link Font#getSize()}
     *                              method and adding it to the resulting map.
     *
     * @return The font's remaining attributes after {@link TextAttribute#FONT} and {@link TextAttribute#FAMILY} are removed.
     */
    public static @NotNull Map<TextAttribute, ?> getFilteredAttributes(@NotNull Font font, boolean copySizeIfNotIncluded) {
        return getFilteredAttributes(font, NEG, copySizeIfNotIncluded);
    }

    /**
     * Get the attributes of the given font without the entries specified by the keys given in the parameter excludedKeys.
     *
     * @param font                  The font.
     * @param excludedKeys          A list containing the attribute keys to filter out of the attributes map.
     * @param copySizeIfNotIncluded If <code>true</code> and the {@link TextAttribute#SIZE} attribute is not included in the entries then it will be added by calling the font's {@link Font#getSize()}
     *                              method and adding it to the resulting map.
     *
     * @return The font's remaining attributes after the entries specified by the keys are removed.
     */
    public static @NotNull Map<TextAttribute, ? super Object> getFilteredAttributes(@NotNull Font font, @NotNull List<TextAttribute> excludedKeys, boolean copySizeIfNotIncluded) {
        Map<TextAttribute, ? super Object> attrs = streamFilteredAttributes(font, excludedKeys).collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b));
        if(copySizeIfNotIncluded) attrs.putIfAbsent(TextAttribute.SIZE, (float)font.getSize());
        return attrs;
    }

    /**
     * Get the attributes of the given font without the entries specified by the keys given in the parameter excludedKeys.  If the {@link TextAttribute#SIZE} attribute is not included in the entries
     * then it will be added by calling the font's {@link Font#getSize()} method and adding it to the resulting map.
     *
     * @param font         The font.
     * @param excludedKeys A list containing the attribute keys to filter out of the attributes map.
     *
     * @return The font's remaining attributes after the entries specified by the keys are removed.
     */
    public static @NotNull Map<TextAttribute, ? super Object> getFilteredAttributes(@NotNull Font font, @NotNull List<TextAttribute> excludedKeys) {
        return getFilteredAttributes(font, excludedKeys, true);
    }

    public static @NotNull Set<String> getFontFamilyNames() {
        return getAllFonts().map(Font::getFamily).collect(Collectors.toCollection(TreeSet::new));
    }

    public static @NotNull List<Font> getFontList() {
        return getAllFonts().toList();
    }

    public static @NotNull Set<String> getFontNames() {
        return getAllFonts().map(Font::getFontName).collect(Collectors.toCollection(TreeSet::new));
    }

    public static void setFont(@NotNull Component c, float size, FontStyles @NotNull ... styles) {
        Map<TextAttribute, ?> attrs = createAttributes(size, styles);
        setFont(c, f -> f.deriveFont(attrs));
    }

    public static void setFont(@NotNull Component c, FontStyles @NotNull ... styles) {
        Map<TextAttribute, ?> attrs = createAttributes(styles);
        setFont(c, f -> f.deriveFont(attrs));
    }

    public static void setFont(@NotNull Component c, @NotNull Font font, String @NotNull ... menuItemsToIgnore) {
        if(menuItemsToIgnore.length > 0) setFont(c, f -> copyFontAttributes(font, f), menuItemsToIgnore);
        else setFont(c, f -> copyFontAttributes(font, f), msgs.getString("menu.font"));
    }

    private static @NotNull Stream<? extends Entry<TextAttribute, ?>> getNonNullAttributes(@NotNull Font font) {
        return font.getAttributes().entrySet().stream().filter(e -> Objects.nonNull(e.getValue()));
    }

    private static void setBorderFont(Border border, @NotNull Function<Font, Font> function) {
        if(border instanceof TitledBorder b) {
            ofNullable(b.getTitleFont()).ifPresent(f -> b.setTitleFont(function.apply(f)));
        }
        else if(border instanceof CompoundBorder b) {
            setBorderFont(b.getInsideBorder(), function);
            setBorderFont(b.getOutsideBorder(), function);
        }
    }

    private static void setFont(@NotNull Component c, @NotNull Function<Font, Font> function, String @NotNull ... menuItemsToIgnore) {
        if(c instanceof Container cx) Stream.of(cx.getComponents()).forEach(cc -> setFont(cc, function, menuItemsToIgnore));
        if(c instanceof JComponent cx) setBorderFont(cx.getBorder(), function);
        if((c instanceof JMenu menu) && !U.isObjIn(menu.getText(), menuItemsToIgnore)) Stream.of(menu.getMenuComponents()).forEach(cc -> setFont(cc, function, menuItemsToIgnore));
        ofNullable(c.getFont()).ifPresent(f -> c.setFont(function.apply(f)));
    }

    private static @NotNull Stream<? extends Entry<TextAttribute, ?>> streamFilteredAttributes(@NotNull Font font, List<TextAttribute> excludedKeys) {
        return getNonNullAttributes(font).filter(e -> !excludedKeys.contains(e.getKey()));
    }
}
