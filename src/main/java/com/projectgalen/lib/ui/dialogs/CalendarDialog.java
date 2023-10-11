package com.projectgalen.lib.ui.dialogs;

import com.projectgalen.lib.ui.M;
import com.projectgalen.lib.ui.annotations.RootPanel;
import com.projectgalen.lib.ui.base.JDialogBase;
import com.projectgalen.lib.ui.components.CalendarFace;
import com.projectgalen.lib.ui.components.combobox.PGJComboBox;
import com.projectgalen.lib.ui.interfaces.DialogButtonsInterface;
import com.projectgalen.lib.utils.Dates;
import com.projectgalen.lib.utils.PGCalendar;
import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.Streams;
import com.projectgalen.lib.utils.enums.Month;
import com.projectgalen.lib.utils.refs.IntegerRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class CalendarDialog extends JDialogBase {

    private static final SimpleDateFormat DATE_FMT1 = new SimpleDateFormat("EEEE, MMMM d, yyyy");
    private static final SimpleDateFormat DATE_FMT2 = new SimpleDateFormat("MMMM yyyy");
    private static final List<String>     MONTHS    = M.msgs.getStringList("month.values");

    protected @RootPanel JPanel                 contentPane;
    protected            DialogButtonsInterface dialogButtons;
    protected            JButton                buttonPrev;
    protected            JButton                buttonNext;
    protected            PGJComboBox<Integer>   fieldMonths;
    protected            PGJComboBox<Integer>   fieldYears;
    protected            CalendarFace           calendarFace;
    protected            int                    storedMonth = 0;
    protected            int                    storedDate  = 1;
    protected            int                    storedYear  = PGCalendar.getInstance().getYear();
    protected            int                    minYear;
    protected            int                    maxYear;

    protected CalendarDialog(@NotNull String titleKey, @NotNull PGResourceBundle msgs) {
        super(titleKey, msgs);
    }

    protected CalendarDialog(@NotNull Frame owner, @NotNull String titleKey, @NotNull PGResourceBundle msgs) {
        super(owner, titleKey, msgs);
    }

    protected CalendarDialog(@NotNull Dialog owner, @NotNull String titleKey, @NotNull PGResourceBundle msgs) {
        super(owner, titleKey, msgs);
    }

    public Date getDate() {
        return new PGCalendar(storedYear, storedMonth, storedDate).getSqlDate();
    }

    public @Override void setup(Object @NotNull ... args) {
        int        startYear = (Integer)args[0];
        int        endYear   = (Integer)args[1];
        PGCalendar initDate  = ((args[2] == null) ? new PGCalendar(startYear, Month.JANUARY, 1) : PGCalendar.toCalendar((Date)args[2]));

        minYear = Math.min(startYear, endYear);
        maxYear = Math.max(startYear, endYear);
        if(initDate.getYear() < minYear || initDate.getYear() > maxYear) throw new IllegalArgumentException(msgs.format("msg.err.bad_year_for_initial_date", initDate, minYear, maxYear));

        storeDate(initDate);

        fieldYears.setData(createYearList(startYear, endYear), String::valueOf);
        fieldYears.setSelectedItem(storedYear);
        fieldMonths.setSelectedItem(storedMonth);
        buttonNext.addActionListener(e -> incMonth(1));
        buttonPrev.addActionListener(e -> incMonth(-1));
        calendarFace.addCalendarFaceListener(e -> storedDate = e.selectedDate);
        calendarFace.setToolTipFunc(dt -> DATE_FMT1.format(getDate()));
    }

    protected @Override void createUIComponents() {
        int year = PGCalendar.getInstance().getYear();
        fieldMonths = new PGJComboBox<>(IntStream.range(0, MONTHS.size()).boxed().toList(), false, MONTHS::get);
        fieldYears  = new PGJComboBox<>(createYearList(year, year + 10), false, String::valueOf);
        fieldMonths.addItemListener(e -> onMonthChanged());
        fieldYears.addItemListener(e -> onYearChanged());
    }

    @NotNull private static List<Integer> createYearList(int startYear, int endYear) {
        return Streams.closedIntRange(startYear, endYear).boxed().toList();
    }

    protected @Override void onCancel() {
        super.onCancel();
    }

    protected @Override void onOK() {
        super.onOK();
    }

    protected void updateUI() {
        SwingUtilities.invokeLater(() -> {
            fieldMonths.setSelectedItem(storedMonth);
            fieldYears.setSelectedItem(storedYear);
            PGCalendar c = new PGCalendar(storedYear, storedMonth, 1);
            calendarFace.setData(c.getDayOfWeek(), c.getActualMaximum(Calendar.DAY_OF_MONTH), storedDate);
            pack();
            repaint();
        });
    }

    private void incMonth(int inc) {
        PGCalendar c = new PGCalendar(storedYear, storedMonth, storedDate).addMonths(inc);
        if((c.getYear() >= minYear) && (c.getYear() <= maxYear)) {
            storeDate(c);
            updateUI();
        }
    }

    private void onMonthChanged() {
        storedMonth = Objects.requireNonNullElse(fieldMonths.getSelectedItem(), 0);
        int mx = Dates.daysInMonth(storedMonth + 1, storedYear);
        if(storedDate > mx) storedDate = mx;
        updateUI();
    }

    private void onYearChanged() {
        storedYear = Objects.requireNonNullElse(fieldYears.getSelectedItem(), fieldYears.getData().get(0));
        int mx = Dates.daysInMonth(storedMonth + 1, storedYear);
        if(storedDate > mx) storedDate = mx;
        updateUI();
    }

    private void storeDate(@NotNull PGCalendar date) {
        storedMonth = date.getMonth();
        storedYear  = date.getYear();
        storedDate  = date.getDate();
        updateUI();
    }

    public static @Nullable Date execute(@Nullable Component owner, int startYear, int endYear, @Nullable Date date) {
        return execute(owner, startYear, endYear, date, new IntegerRef(0));
    }

    public static @Nullable Date execute(@Nullable Component owner, int startYear, int endYear, @Nullable Date date, @NotNull IntegerRef buttonChoiceRef) {
        CalendarDialog dlg = CalendarDialog.create(CalendarDialog.class, owner, "dlg.title.calendar", M.msgs, startYear, endYear, date);
        buttonChoiceRef.value = dlg.getExitCode();
        return ((buttonChoiceRef.value == OK_BUTTON) ? dlg.getDate() : null);
    }
}
