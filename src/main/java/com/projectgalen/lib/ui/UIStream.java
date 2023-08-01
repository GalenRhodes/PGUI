package com.projectgalen.lib.ui;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: UIStream.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: July 31, 2023
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

import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public final class UIStream {
    private UIStream() { }

    public static <C extends Component, R> R fromChildStream(@Nullable Container container, @NotNull Class<C> cls, @Nullable @RegExp @NonNls @Language("RegExp") String pattern, @NotNull Function<Stream<C>, R> function) {
        if(container == null) return function.apply(Stream.empty());
        synchronized(container.getTreeLock()) {
            return function.apply(Stream.of(container.getComponents())
                                        .filter(cls::isInstance)
                                        .map(cls::cast)
                                        .filter(c -> ((pattern == null) || ((c.getName() != null) && c.getName().matches(pattern)))));
        }
    }

    public static <C extends Component, R> R fromChildStream(@Nullable Container container, @NotNull Class<C> cls, @NotNull Function<Stream<C>, R> function) {
        return fromChildStream(container, cls, null, function);
    }

    public static <C extends Component> void withChildStream(@Nullable Container container, @NotNull Class<C> cls, @NotNull Consumer<Stream<C>> consumer) {
        withChildStream(container, cls, null, consumer);
    }

    public static <C extends Component> void withChildStream(@Nullable Container container, @NotNull Class<C> cls, @Nullable @RegExp @NonNls @Language("RegExp") String pattern, @NotNull Consumer<Stream<C>> consumer) {
        fromChildStream(container, cls, pattern, s -> { consumer.accept(s); return null; });/*@f0*/
    }/*@f1*/
}
