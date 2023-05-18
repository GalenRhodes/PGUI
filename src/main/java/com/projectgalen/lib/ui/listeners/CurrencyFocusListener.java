package com.projectgalen.lib.ui.listeners;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: CurrencyFocusListener.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: April 22, 2023
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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;

import static com.projectgalen.lib.utils.U.cleanNumberString;

public class CurrencyFocusListener implements FocusListener {

    private final NumberFormat fmt = NumberFormat.getCurrencyInstance();

    public CurrencyFocusListener() { }

    @Override
    public void focusGained(@NotNull FocusEvent e) {
        if(e.getSource() instanceof JTextField) {
            JTextField textField    = (JTextField)e.getComponent();
            String     numberString = textField.getText();
            SwingUtilities.invokeLater(() -> textField.setText(cleanNumberString(numberString)));
        }
    }

    @Override
    public void focusLost(@NotNull FocusEvent e) {
        if(e.getSource() instanceof JTextField) {
            JTextField textField = (JTextField)e.getComponent();
            SwingUtilities.invokeLater(() -> textField.setText(fmt.format(Double.parseDouble(cleanNumberString(textField.getText())))));
        }
    }
}
