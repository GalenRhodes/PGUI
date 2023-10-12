package com.projectgalen.lib.ui.components.table.misc;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: CurrencyCellEditor.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: September 06, 2023
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

import com.projectgalen.lib.ui.components.PGCurrencyTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.UUID;

@SuppressWarnings("unused")
public class CurrencyCellEditor extends DefaultCellEditor {

    private Font font;

    public CurrencyCellEditor() { this(-1); }

    public CurrencyCellEditor(int fontSize) {
        super(new PGCurrencyTextField(0.0));

        PGCurrencyTextField formattedField   = getComponent();
        String              keyPressActionId = UUID.randomUUID().toString();

        font = formattedField.getFont();
        formattedField.setHorizontalAlignment(JTextField.TRAILING);
        formattedField.addFocusListener(new FocusAdapter() {
            public @Override void focusGained(FocusEvent e) {
                PGCurrencyTextField formattedField = getComponent();
                formattedField.setSelectionStart(1);
                formattedField.setSelectionEnd(formattedField.getText().length());
            }
        });
        formattedField.setFocusLostBehavior(JFormattedTextField.PERSIST);
        formattedField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), keyPressActionId);
        formattedField.getActionMap().put(keyPressActionId, new AbstractAction() {
            public @Override void actionPerformed(ActionEvent e) {
                PGCurrencyTextField formattedField = getComponent();
                if(formattedField.isEditValid()) commitEdit(formattedField);
                formattedField.postActionEvent();
            }
        });
        if(fontSize > 0) formattedField.setFont(font.deriveFont((float)fontSize));
    }

    public @Override Object getCellEditorValue() {
        return getComponent().getNumber();
    }

    public @Override PGCurrencyTextField getComponent() {
        return (PGCurrencyTextField)super.getComponent();
    }

    public int getFontSize() {
        return font.getSize();
    }

    public @Override @NotNull Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        PGCurrencyTextField formattedField = (PGCurrencyTextField)super.getTableCellEditorComponent(table, value, isSelected, row, column);
        formattedField.setValue((value == null) ? 0.0 : ((BigDecimal)value).doubleValue());
        return formattedField;
    }

    public void setFontSize(int fontSize) {
        PGCurrencyTextField c = getComponent();
        c.setFont(font = c.getFont().deriveFont((float)fontSize));
    }

    public @Override boolean stopCellEditing() {
        PGCurrencyTextField formattedField = getComponent();
        if(!formattedField.isEditValid()) return false;
        commitEdit(formattedField);
        return super.stopCellEditing();
    }

    private static void commitEdit(PGCurrencyTextField formattedField) {
        try { formattedField.commitEdit(); } catch(Exception ignore) { }
    }
}
