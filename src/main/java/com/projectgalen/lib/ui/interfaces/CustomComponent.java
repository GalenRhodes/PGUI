package com.projectgalen.lib.ui.interfaces;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: CustomComponent.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 31, 2023
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

import javax.swing.*;
import java.awt.*;

public interface CustomComponent {

    default Rectangle getBounds()                 { return getRootPanel().getBounds(); }

    default Point getLocation()                   { return getRootPanel().getLocation(); }

    default Dimension getMaximumSize()            { return getRootPanel().getMaximumSize(); }

    default Dimension getMinimumSize()            { return getRootPanel().getMinimumSize(); }

    default Dimension getPreferredSize()          { return getRootPanel().getPreferredSize(); }

    JPanel getRootPanel();

    default Dimension getSize()                   { return getRootPanel().getSize(); }

    default boolean isEnabled()                   { return getRootPanel().isEnabled(); }

    default boolean isVisible()                   { return getRootPanel().isVisible(); }

    default void repaint()                        { getRootPanel().repaint(); }

    default void revalidate()                     { getRootPanel().revalidate(); }

    default void setBounds(Rectangle bounds)      { getRootPanel().setBounds(bounds); }

    default void setEnabled(boolean enabled)      { getRootPanel().setEnabled(enabled); }

    default void setLocation(Point loc)           { getRootPanel().setLocation(loc); }

    default void setLocation(int x, int y)        { getRootPanel().setLocation(x, y); }

    default void setMaximumSize(Dimension size)   { getRootPanel().setMaximumSize(size); }

    default void setMinimumSize(Dimension size)   { getRootPanel().setMinimumSize(size); }

    default void setPreferredSize(Dimension size) { getRootPanel().setPreferredSize(size); }

    default void setSize(Dimension size)          { getRootPanel().setSize(size); }

    default void setVisible(boolean visible)      { getRootPanel().setVisible(visible); }
}
