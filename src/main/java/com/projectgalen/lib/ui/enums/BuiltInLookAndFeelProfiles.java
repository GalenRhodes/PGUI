package com.projectgalen.lib.ui.enums;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: BuiltInLookAndFeelProfiles.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 18, 2023
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

public enum BuiltInLookAndFeelProfiles {
    Nimbus("javax.swing.plaf.nimbus.NimbusLookAndFeel"),
    Metal("javax.swing.plaf.metal.MetalLookAndFeel"),
    Motif("com.sun.java.swing.plaf.motif.MotifLookAndFeel"),
    Aqual("com.apple.laf.AquaLookAndFeel");

    private final @NotNull String className;

    BuiltInLookAndFeelProfiles(@NotNull String className) {
        this.className = className;
    }

    public @NotNull String getClassName() {
        return className;
    }
}
