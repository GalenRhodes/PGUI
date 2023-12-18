package com.projectgalen.lib.ui.components.calendar;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: PGCalendarButton.java
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

import com.projectgalen.lib.ui.UI;
import com.projectgalen.lib.ui.dialogs.PGCalendarDialog;
import com.projectgalen.lib.ui.utils.Mnemonic;
import com.projectgalen.lib.utils.dates.Dates;
import com.projectgalen.lib.utils.dates.PGCalendar;
import com.projectgalen.lib.utils.math.Range;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.projectgalen.lib.ui.M.msgs;
import static com.projectgalen.lib.ui.M.props;
import static com.projectgalen.lib.utils.dates.PGCalendar.getInstance;
import static java.util.Optional.ofNullable;

@SuppressWarnings("unused")
public class PGCalendarButton extends JButton {
    private static final String DEF_BUTTON_TEXT  = msgs.getString("button.date.no_date_text");
    private static final String DEF_TOOLTIP_TEXT = msgs.getString("tooltip.date.no_date_text");
    private static final Icon   DEF_ICON         = UI.getIcon(props.getProperty("icons.path"), props.getProperty("calendar.button.icon"), PGCalendarButton.class);
    private static final String DEF_DATE_FORMAT  = props.getProperty("calendar.button.default.date.format");

    private Component popupDialogOwner;
    private int       startYear;
    private int       endYear;
    private String    buttonText;
    private Date      date;
    private String    dateFormat;

    public PGCalendarButton() {
        this(null, getInstance().getYear(), (getInstance().getYear() + props.getInt("calendar.button.default.year.range")), null, DEF_BUTTON_TEXT, DEF_TOOLTIP_TEXT, null, null);
    }

    public PGCalendarButton(@Nullable Component popupDialogOwner,
                            @org.jetbrains.annotations.Range(from = 1583, to = 3000) int startYear,
                            @org.jetbrains.annotations.Range(from = 1583, to = 3000) int endYear,
                            @Nullable String dateFormat,
                            @Nullable String buttonText,
                            @Nullable String toolTipText,
                            @Nullable Icon icon,
                            @Nullable Date date) {
        super(DEF_BUTTON_TEXT, ofNullable(icon).orElse(DEF_ICON));
        setAll(popupDialogOwner, startYear, endYear, dateFormat, buttonText, toolTipText, icon, date);
        addActionListener(e -> editDate());
    }

    public Date getDate() {
        return date;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public int getEndYear() {
        return endYear;
    }

    public Component getPopupDialogOwner() {
        return popupDialogOwner;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setAll(@Nullable Component popupDialogOwner,
                       @org.jetbrains.annotations.Range(from = 1583, to = 3000) int startYear,
                       @org.jetbrains.annotations.Range(from = 1583, to = 3000) int endYear,
                       @Nullable String dateFormat,
                       @Nullable String buttonText,
                       @Nullable String toolTipText,
                       @Nullable Icon icon,
                       @Nullable Date date) {
        this.popupDialogOwner = popupDialogOwner;
        this.startYear        = startYear;
        this.endYear          = endYear;
        this.date             = Dates.toSQLDate(date);
        this.buttonText       = buttonText;
        this.dateFormat       = dateFormat;

        setIcon(icon);
        setToolTipText(toolTipText);
        setButtonText();
    }

    public void setDate(Date date) {
        this.date = Dates.toSQLDate(date);
        setButtonText();
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        setButtonText();
    }

    public void setEndYear(@org.jetbrains.annotations.Range(from = 1583, to = 3000) int endYear) {
        this.endYear = endYear;
    }

    public @Override void setIcon(Icon defaultIcon) {
        super.setIcon(ofNullable(defaultIcon).orElse(DEF_ICON));
    }

    public void setPopupDialogOwner(Component popupDialogOwner) {
        this.popupDialogOwner = popupDialogOwner;
    }

    public void setStartYear(@org.jetbrains.annotations.Range(from = 1583, to = 3000) int startYear) {
        this.startYear = startYear;
    }

    public @Override void setText(String buttonText) {
        this.buttonText = buttonText;
        setButtonText();
    }

    public @Override void setToolTipText(String text) {
        super.setToolTipText(Objects.toString(text, DEF_TOOLTIP_TEXT));
    }

    private void editDate() {
        Date _date = date;

        if(_date == null) {
            PGCalendar now = getInstance();
            _date = Range.isInClosedRange(now.getYear(), startYear, endYear) ? now.getSqlDate() : Dates.createSQLDate(startYear, Calendar.JANUARY, 1);
        }

        setDate(ofNullable(PGCalendarDialog.execute(popupDialogOwner, getFont(), startYear, endYear, _date)).orElse(date));
    }

    private void setButtonText() {
        Mnemonic mnemonic = UI.getMnemonic(Objects.toString(buttonText, DEF_BUTTON_TEXT));
        super.setText(ofNullable(getDate()).map(d -> Dates.format(Objects.toString(getDateFormat(), DEF_DATE_FORMAT), d)).orElse(mnemonic.text()));
        setMnemonic(KeyEvent.getExtendedKeyCodeForChar(mnemonic.mnemonic()));
        setDisplayedMnemonicIndex((getDate() == null) ? mnemonic.index() : -1);
    }
}
