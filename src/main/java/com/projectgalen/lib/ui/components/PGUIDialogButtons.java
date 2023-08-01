package com.projectgalen.lib.ui.components;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: PGUIDialogButtons.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 09, 2023
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

import com.projectgalen.lib.ui.interfaces.DialogButtonsInterface;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionListener;

@SuppressWarnings("unused")
public class PGUIDialogButtons implements DialogButtonsInterface {
    protected JButton buttonOK;
    protected JButton buttonCancel;
    protected JPanel  dialogButtonsPanel;

    public PGUIDialogButtons() { }

    public void addCancelButtonListener(@NotNull ActionListener listener) {
        buttonCancel.addActionListener(listener);
    }

    public void addOKButtonListener(@NotNull ActionListener listener) {
        buttonOK.addActionListener(listener);
    }

    @Override public JButton getButtonCancel() {
        return buttonCancel;
    }

    public JButton getButtonOK() {
        return buttonOK;
    }

    public JPanel getDialogButtonsPanel() {
        return dialogButtonsPanel;
    }

    @Override public boolean isCancelButtonEnabled() {
        return buttonCancel.isEnabled();
    }

    @Override public boolean isOKButtonEnabled() {
        return buttonOK.isEnabled();
    }

    public void removeCancelButtonListener(@NotNull ActionListener listener) {
        buttonCancel.removeActionListener(listener);
    }

    public void removeOKButtonListener(@NotNull ActionListener listener) {
        buttonOK.removeActionListener(listener);
    }

    public void setCancelButtonEnabled(boolean enabled) {
        buttonCancel.setEnabled(enabled);
    }

    public void setOKButtonEnabled(boolean enabled) {
        buttonOK.setEnabled(enabled);
    }
}
