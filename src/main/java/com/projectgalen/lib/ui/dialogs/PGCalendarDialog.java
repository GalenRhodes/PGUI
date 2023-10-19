package com.projectgalen.lib.ui.dialogs;

import com.projectgalen.lib.ui.Fonts;
import com.projectgalen.lib.ui.M;
import com.projectgalen.lib.ui.annotations.RootPanel;
import com.projectgalen.lib.ui.base.JDialogBase;
import com.projectgalen.lib.ui.components.PGDialogButtons;
import com.projectgalen.lib.ui.components.calendar.PGCalendarFace;
import com.projectgalen.lib.ui.components.combobox.PGJComboBox;
import com.projectgalen.lib.utils.PGCalendar;
import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.enums.Month;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Optional.ofNullable;

@SuppressWarnings("unused")
public class PGCalendarDialog extends JDialogBase {

    protected static final String[] months = M.msgs.getString("month.values").split("\\s*,\\s*");

    protected @RootPanel JPanel               contentPane;
    protected            PGDialogButtons      dialogButtons;
    protected            JButton              prevMonthButton;
    protected            JButton              nextMonthButton;
    protected            PGJComboBox<Integer> monthDropDown;
    protected            PGJComboBox<Integer> yearDropDown;
    protected            PGCalendarFace       calendarFace;
    protected            int                  startYear;
    protected            int                  endYear;
    protected            int                  minYear;
    protected            int                  maxYear;
    protected            PGCalendar           date;

    protected PGCalendarDialog(@NotNull String titleKey, @NotNull PGResourceBundle msgs) {
        super(titleKey, msgs);
    }

    protected PGCalendarDialog(@NotNull Frame owner, @NotNull String titleKey, @NotNull PGResourceBundle msgs) {
        super(owner, titleKey, msgs);
    }

    protected PGCalendarDialog(@NotNull Dialog owner, @NotNull String titleKey, @NotNull PGResourceBundle msgs) {
        super(owner, titleKey, msgs);
    }

    public Date getDate() {
        return date.getTime();
    }

    public @Override void setup(Object @NotNull ... args) {
        startYear = (Integer)args[0];
        endYear   = (Integer)args[1];
        minYear   = Math.min(startYear, endYear);
        maxYear   = Math.max(startYear, endYear);
        date      = ofNullable((Date)args[2]).map(PGCalendar::toCalendar).orElseGet(() -> new PGCalendar(startYear, Month.JANUARY, 1));

        ofNullable((Font)args[3]).ifPresent(f -> Fonts.setFont(this, f));
        updateYearDropdownList();
        prevMonthButton.addActionListener(this::onPrevMonth);
        nextMonthButton.addActionListener(this::onNextMonth);
        monthDropDown.addItemListener(this::onMonthSelected);
        yearDropDown.addItemListener(this::onYearSelected);
        calendarFace.addCalendarFaceListener(e -> date.setDate(e.selectedDate));
        updateUI();
    }

    protected @Override void createUIComponents() {
        minYear       = PGCalendar.getInstance().getYear();
        maxYear       = (minYear + 10);
        yearDropDown  = new PGJComboBox<>(getYearList(), false, String::valueOf);
        monthDropDown = new PGJComboBox<>(getMonthList(), false, i -> months[i]);
    }

    protected void onMonthSelected(@NotNull ItemEvent e) {
        if(e.getItem() instanceof Integer month) {
            date.setMonth(month);
            updateUI();
        }
    }

    protected void onNextMonth(@NotNull ActionEvent e) {
        date.addMonths(1);
        int _maxYear = Math.max(maxYear, date.getYear());
        if(maxYear != _maxYear) {
            maxYear = _maxYear;
            updateYearDropdownList();
        }
        updateUI();
    }

    protected void onPrevMonth(@NotNull ActionEvent e) {
        date.addMonths(-1);
        int _minYear = Math.min(minYear, date.getYear());
        if(minYear != _minYear) {
            minYear = _minYear;
            updateYearDropdownList();
        }
        updateUI();
    }

    protected void onYearSelected(@NotNull ItemEvent e) {
        if(e.getItem() instanceof Integer year) {
            date.setYear(year);
            updateUI();
        }
    }

    protected void updateUI() {
        SwingUtilities.invokeLater(() -> {
            int        month = date.getMonth();
            int        year  = date.getYear();
            int        dom   = date.getDate();
            PGCalendar c     = new PGCalendar(year, month, 1);

            monthDropDown.setSelectedItem(month);
            yearDropDown.setSelectedItem(year);
            calendarFace.setData(c.getDayOfWeek(), c.getActualMaximum(Calendar.DATE), dom);

            pack();
            repaint();
        });
    }

    protected void updateYearDropdownList() {
        yearDropDown.setData(getYearList());
    }

    private @NotNull List<Integer> getMonthList() {
        return IntStream.range(0, 12).boxed().toList();
    }

    private @NotNull List<Integer> getYearList() {
        return IntStream.range(minYear, (maxYear + 1)).boxed().toList();
    }

    public static @Nullable Date execute(@Nullable Component owner, int startYear, int endYear, @Nullable Date date) {
        return execute(owner, null, startYear, endYear, date);
    }

    public static @Nullable Date execute(@Nullable Component owner, @Nullable Font font, int startYear, int endYear, @Nullable Date date) {
        PGCalendarDialog dlg = create(PGCalendarDialog.class, owner, "dlg.title.calendar", M.msgs, startYear, endYear, date, font);
        return ((dlg.getExitCode() == OK_BUTTON) ? dlg.getDate() : null);
    }
}
