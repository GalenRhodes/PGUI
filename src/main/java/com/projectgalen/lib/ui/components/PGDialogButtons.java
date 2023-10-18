package com.projectgalen.lib.ui.components;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: PGDialogButtons.java
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

import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.projectgalen.lib.ui.UI;
import com.projectgalen.lib.ui.base.NonGUIEditorCustomComponent;
import com.projectgalen.lib.ui.interfaces.DialogButtonsInterface;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.JavaBean;

import static com.intellij.uiDesigner.core.GridConstraints.*;

@SuppressWarnings({ "unused" })
@JavaBean(defaultProperty = "UI", description = "A panel with generic 'OK' and 'Cancel' buttons.")
public class PGDialogButtons extends JPanel implements NonGUIEditorCustomComponent, DialogButtonsInterface {

    protected final JButton buttonOK;
    protected final JButton buttonCancel;

    public PGDialogButtons() {
        super(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1), true);
        add(new Spacer(), createConstraint(0, 0, ANCHOR_CENTER, FILL_HORIZONTAL, SIZEPOLICY_WANT_GROW, SIZEPOLICY_CAN_SHRINK));
        add(buttonOK = createButton("accept.png", "button.ok"), createConstraint(0, 1, ANCHOR_CENTER, FILL_HORIZONTAL, UI.SIZEPOLICY_BOTH, SIZEPOLICY_FIXED));
        add(buttonCancel = createButton("cancel.png", "button.cancel"), createConstraint(0, 2, ANCHOR_CENTER, FILL_HORIZONTAL, UI.SIZEPOLICY_BOTH, SIZEPOLICY_FIXED));
    }

    public @Override void addCancelButtonListener(@NotNull ActionListener e) {
        buttonCancel.addActionListener(e);
    }

    public @Override void addOKButtonListener(@NotNull ActionListener e) {
        buttonOK.addActionListener(e);
    }

    public @Override JButton getButtonCancel() {
        return buttonCancel;
    }

    public @Override JButton getButtonOK() {
        return buttonOK;
    }

    public @Override JPanel getDialogButtonsPanel() {
        return this;
    }

    public @Override boolean isCancelButtonEnabled() {
        return buttonCancel.isEnabled();
    }

    public @Override boolean isOKButtonEnabled() {
        return buttonOK.isEnabled();
    }

    public @Override void removeCancelButtonListener(@NotNull ActionListener listener) {
        buttonCancel.removeActionListener(listener);
    }

    public @Override void removeOKButtonListener(@NotNull ActionListener listener) {
        buttonOK.removeActionListener(listener);
    }

    public @Override void setCancelButtonEnabled(boolean enabled) {
        buttonCancel.setEnabled(enabled);
    }

    public @Override void setOKButtonEnabled(boolean enabled) {
        buttonOK.setEnabled(enabled);
    }
}
