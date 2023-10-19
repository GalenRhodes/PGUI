package com.projectgalen.lib.ui;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: FontStyles.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 19, 2023
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

import java.awt.font.TextAttribute;

public enum FontStyles {
    WeightExtraLight(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRA_LIGHT),
    WeightLight(TextAttribute.WEIGHT, TextAttribute.WEIGHT_LIGHT),
    WeightDemilight(TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMILIGHT),
    WeightRegular(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR),
    WeightSemibold(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD),
    WeightMedium(TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM),
    WeightDemibold(TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMIBOLD),
    WeightBold(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD),
    WeightHeavy(TextAttribute.WEIGHT, TextAttribute.WEIGHT_HEAVY),
    WeightExtrabold(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRABOLD),
    WeightUltrabold(TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD),

    WidthCondensed(TextAttribute.WIDTH, TextAttribute.WIDTH_CONDENSED),
    WidthSemiCondensed(TextAttribute.WIDTH, TextAttribute.WIDTH_SEMI_CONDENSED),
    WidthRegular(TextAttribute.WIDTH, TextAttribute.WIDTH_REGULAR),
    WidthSemiExtended(TextAttribute.WIDTH, TextAttribute.WIDTH_SEMI_EXTENDED),
    WidthExtended(TextAttribute.WIDTH, TextAttribute.WIDTH_EXTENDED),

    ItalicsOn(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR),
    ItalicsOff(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE),

    Superscript(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER),
    Subscript(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB),

    UnderlineOn(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON),
    UndrelineOff(TextAttribute.UNDERLINE, -1),

    StrikethroughOn(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON),
    StrikethroughOff(TextAttribute.STRIKETHROUGH, Boolean.FALSE),

    RunDirectionLTR(TextAttribute.RUN_DIRECTION, TextAttribute.RUN_DIRECTION_LTR),
    RunDirectionRTL(TextAttribute.RUN_DIRECTION, TextAttribute.RUN_DIRECTION_RTL),

    JustificationFull(TextAttribute.JUSTIFICATION, TextAttribute.JUSTIFICATION_FULL),
    JustificationNone(TextAttribute.JUSTIFICATION, TextAttribute.JUSTIFICATION_NONE),

    KerningOn(TextAttribute.KERNING, TextAttribute.KERNING_ON),
    KerningOff(TextAttribute.KERNING, 0),

    LigaturesOn(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON),
    LigaturesOff(TextAttribute.LIGATURES, 0),

    TrackingTight(TextAttribute.TRACKING, TextAttribute.TRACKING_TIGHT),
    TrackingLoose(TextAttribute.TRACKING, TextAttribute.TRACKING_LOOSE);

    private final TextAttribute attribute;
    private final Object        value;

    FontStyles(TextAttribute attribute, Object value) {
        this.attribute = attribute;
        this.value     = value;
    }

    public TextAttribute getAttribute() {
        return attribute;
    }

    public Object getValue() {
        return value;
    }
}
