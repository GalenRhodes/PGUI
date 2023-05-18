package com.projectgalen.lib.ui;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: NumberTextField.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 03, 2023
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
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Objects;

public class CurrencyTextField {
    protected JFormattedTextField textField;
    protected JPanel              numberPanel;

    public CurrencyTextField() {
        this(0.0);
    }

    public CurrencyTextField(BigDecimal value) {
        this((value == null) ? 0.0 : value.doubleValue());
    }

    public CurrencyTextField(double value) {
        super();
        _setNumber(value);
        SwingUtilities.invokeLater(() -> {
            Canvas   c = new Canvas(numberPanel.getGraphicsConfiguration());
            Graphics g = c.getGraphics();
            try {
                Font        font = textField.getFont();
                FontMetrics fm   = c.getFontMetrics(font);
                String      str  = "$999,999,999.99";
                Rectangle2D rect = fm.getStringBounds(str, g);
                Dimension   size = new Dimension((int)(rect.getWidth() + 0.5), textField.getHeight());
                textField.setMinimumSize(size);
                textField.setMaximumSize(size);
                textField.setPreferredSize(size);
            }
            finally {
                if(g != null) g.dispose();
            }
        });
    }

    public @NotNull BigDecimal getNumber() {
        return BigDecimal.valueOf(Objects.requireNonNullElse(((Number)textField.getValue()), 0.0).doubleValue());
    }

    public String getToolTipText() {
        return textField.getToolTipText();
    }

    public void setNumber(double value) {
        SwingUtilities.invokeLater(() -> {
            _setNumber(value);
        });
    }

    public void setNumber(BigDecimal value) {
        setNumber((value == null) ? 0.0 : value.doubleValue());
    }

    public void setToolTipText(String toolTipText) {
        SwingUtilities.invokeLater(() -> textField.setToolTipText(toolTipText));
    }

    private void _setNumber(double value) {
        textField.setValue(value);
        textField.setSelectionStart(1);
        textField.setSelectionEnd(textField.getText().length());
    }

    private void createUIComponents() {
        NumberFormat fmt = NumberFormat.getCurrencyInstance();
        fmt.setMaximumIntegerDigits(9);
        fmt.setMinimumIntegerDigits(1);
        fmt.setMinimumFractionDigits(2);
        fmt.setMaximumFractionDigits(2);
        textField = new JFormattedTextField(fmt);
        textField.setValue(0.0);
        textField.setColumns(16);
    }
}
