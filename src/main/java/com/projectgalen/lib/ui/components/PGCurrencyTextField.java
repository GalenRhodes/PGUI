package com.projectgalen.lib.ui.components;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: PGCurrencyTextField.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 11, 2023
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
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Objects;

@SuppressWarnings("unused")
public class PGCurrencyTextField extends JFormattedTextField {

    private static final FocusListener focusListener = new MyFocusAdapter();

    public PGCurrencyTextField() {
        this(BigDecimal.ZERO);
    }

    public PGCurrencyTextField(double value) {
        this(BigDecimal.valueOf(value));
    }

    public PGCurrencyTextField(@NotNull BigDecimal value) {
        super(getCurrencyFormatter());
        setValue(value.doubleValue());
        setColumns(16);
        addFocusListener(focusListener);

        SwingUtilities.invokeLater(() -> {
            if(getGraphics() instanceof Graphics2D g) {
                try {
                    Dimension size = new Dimension((int)(getFontMetrics(getFont()).getStringBounds("$999,999,999.99", g).getWidth() + 0.5), getHeight());
                    setMinimumSize(size);
                    setMaximumSize(size);
                    setPreferredSize(size);
                }
                catch(Exception e) {
                    e.printStackTrace(System.err);
                }
                finally {
                    g.dispose();
                }
            }
        });
    }

    public @NotNull BigDecimal getNumber() {
        return ((getValue() instanceof Number n) ? BigDecimal.valueOf(n.doubleValue()) : BigDecimal.ZERO);
    }

    public void setNumber(double number) {
        setValue(number);
    }

    public void setNumber(BigDecimal number) {
        setValue(Objects.requireNonNullElse(number, BigDecimal.ZERO).doubleValue());
    }

    public static @NotNull NumberFormat getCurrencyFormatter() {
        NumberFormat fmt = NumberFormat.getCurrencyInstance();
        fmt.setMaximumIntegerDigits(9);
        fmt.setMinimumIntegerDigits(1);
        fmt.setMinimumFractionDigits(2);
        fmt.setMaximumFractionDigits(2);
        return fmt;
    }

    private static final class MyFocusAdapter extends FocusAdapter {
        public @Override void focusGained(@NotNull FocusEvent e) {
            if(e.getComponent() instanceof JFormattedTextField field) {
                SwingUtilities.invokeLater(() -> {
                    field.getCaret().setDot(1);
                    field.getCaret().moveDot(field.getText().length());
                });
            }
        }
    }
}
