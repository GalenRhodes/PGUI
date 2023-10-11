package com.projectgalen.lib.ui.base;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: NonGUIEditorCustomComponent.java
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

import com.intellij.uiDesigner.core.GridConstraints;
import com.projectgalen.lib.ui.UI;
import com.projectgalen.lib.ui.utils.Mnemonic;
import com.projectgalen.lib.utils.PGResourceBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Map;
import java.util.TreeMap;

import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK;
import static com.projectgalen.lib.ui.M.props;

@SuppressWarnings({ "DuplicatedCode", "unused" })
public interface NonGUIEditorCustomComponent {

    int                           SIZE_POLICY_ANY             = (SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW);
    Map<String, PGResourceBundle> __UI_BUNDLE_CACHE__         = new TreeMap<>();
    String                        __UI_ICONS_PATH__           = props.getProperty("icons.path");
    String                        __UI_MESSAGES_BUNDLE_PATH__ = props.getProperty("messages.bundle.path");

    default @NotNull JButton createButton(@NotNull String iconPath, @NotNull String iconName, @NotNull String textPath, @NotNull String textKey) {
        return loadButtonText(new JButton(UI.getIcon(iconPath, iconName, getClass())), textPath, textKey);
    }

    default @NotNull JButton createButton(@NotNull String iconName, @NotNull String textKey) {
        return createButton(__UI_ICONS_PATH__, iconName, __UI_MESSAGES_BUNDLE_PATH__, textKey);
    }

    default @NotNull GridConstraints createConstraint(
            final int row,
            final int column,
            final int anchor,
            final int fill,
            final int HSizePolicy,
            final int VSizePolicy) {
        return new GridConstraints(row, column, 1, 1, anchor, fill, HSizePolicy, VSizePolicy, null, null, null, 0, false);
    }

    default String getMessageFromBundle(String path, String key) {
        synchronized(__UI_BUNDLE_CACHE__) {
            return __UI_BUNDLE_CACHE__.computeIfAbsent(path, PGResourceBundle::getPGBundle).getString(key);
        }
    }

    default <T extends AbstractButton> T loadButtonText(@NotNull T button, @NotNull String text) {
        Mnemonic mnemonic = UI.getMnemonic(text);
        button.setText(mnemonic.text());

        if(mnemonic.hasMnemonic()) {
            button.setMnemonic(mnemonic.mnemonic());
            button.setDisplayedMnemonicIndex(mnemonic.index());
        }

        return button;
    }

    default <T extends AbstractButton> T loadButtonText(@NotNull T button, @NotNull String path, @NotNull String key) {
        return loadButtonText(button, getMessageFromBundle(path, key));
    }
}
