package com.projectgalen.lib.ui.dialogs;

import com.projectgalen.lib.ui.M;
import com.projectgalen.lib.ui.UIStream;
import com.projectgalen.lib.ui.annotations.RootPanel;
import com.projectgalen.lib.ui.base.JDialogBase;
import com.projectgalen.lib.ui.components.PGUIComboBox;
import com.projectgalen.lib.ui.interfaces.DialogButtonsInterface;
import com.projectgalen.lib.utils.Dates;
import com.projectgalen.lib.utils.PGCalendar;
import com.projectgalen.lib.utils.PGResourceBundle;
import com.projectgalen.lib.utils.Streams;
import com.projectgalen.lib.utils.enums.Month;
import com.projectgalen.lib.utils.refs.IntegerRef;
import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class CalendarDialog extends JDialogBase {
    public static final Color COLOR_BG_MOUSEOVER = Color.LIGHT_GRAY;
    public static final Color COLOR_FG_MOUSEOVER = Color.BLACK;
    public static final Color COLOR_BG_NORMAL    = Color.WHITE;
    public static final Color COLOR_FG_NORMAL    = Color.BLACK;
    public static final Color COLOR_BG_MARKED    = Objects.requireNonNullElse(UIManager.getColor("Menu.selectionBackground"), Color.BLUE);
    public static final Color COLOR_FG_MARKED    = Objects.requireNonNullElse(UIManager.getColor("Menu.selectionForeground"), Color.WHITE);

    private static final         SimpleDateFormat DATE_FMT1   = new SimpleDateFormat("EEEE, MMMM d, yyyy");
    private static final         SimpleDateFormat DATE_FMT2   = new SimpleDateFormat("MMMM yyyy");
    private static final         Cursor           CURSOR_DEF  = new Cursor(Cursor.DEFAULT_CURSOR);
    private static final         Cursor           CURSOR_HAND = new Cursor(Cursor.HAND_CURSOR);
    private static final         List<String>     MONTHS      = M.msgs.getStringList("month.values");
    private static final @RegExp String           RX1         = "w\\d+";
    private static final @RegExp String           RX2         = "c\\d+";
    private static final @RegExp String           RX3         = "[wc]\\d+";

    protected @RootPanel JPanel                 contentPane;
    protected            DialogButtonsInterface dialogButtons;
    protected            JPanel                 calendarPanel;
    protected            JButton                buttonPrev;
    protected            JButton                buttonNext;
    protected            PGUIComboBox<Integer>  fieldMonths;
    protected            PGUIComboBox<Integer>  fieldYears;
    protected final      MouseListener          mouseListener = new CalendarMouseListener();
    protected            int                    storedMonth   = 0;
    protected            int                    storedDate    = 1;
    protected            int                    storedYear    = PGCalendar.getInstance().getYear();
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

        fieldMonths.setOptional(false);
        fieldMonths.setData(IntStream.range(0, MONTHS.size()).boxed().toList(), MONTHS::get);
        fieldMonths.setSelectedItem(storedMonth);
        fieldYears.setOptional(false);
        fieldYears.setData(Streams.closedIntRange(startYear, endYear).boxed().toList(), String::valueOf);
        fieldYears.setSelectedItem(storedYear);
        fieldMonths.addItemListener(e -> onMonthChanged());
        fieldYears.addItemListener(e -> onYearChanged());
        buttonNext.addActionListener(e -> incMonth(1));
        buttonPrev.addActionListener(e -> incMonth(-1));
        SwingUtilities.invokeLater(() -> {
            Dimension maxSize = new Dimension();
            withPanelLabel(RX1, (p, l) -> getMaxSize(maxSize, p.getPreferredSize()));
            withPanelLabel(RX3, (p, l) -> p.setMinimumSize(maxSize));
            pack();
        });
        updateUI();
    }

    protected @Override void onCancel() {
        super.onCancel();
    }

    protected @Override void onOK() {
        super.onOK();
    }

    private <R> @Nullable R fromLabel(@NotNull JPanel panel, @NotNull BiFunction<JPanel, JLabel, R> func) {
        return UIStream.fromChildStream(panel, JLabel.class, stream -> stream.map(l -> func.apply(panel, l)).filter(Objects::nonNull).findFirst().orElse(null));
    }

    private <R> @Nullable R fromPanelLabel(@NotNull @NonNls @RegExp @Language("RegExp") String pattern, @NotNull BiFunction<JPanel, JLabel, R> func) {
        return UIStream.fromChildStream(calendarPanel, JPanel.class, pattern, stream -> stream.map(panel -> fromLabel(panel, func)).filter(Objects::nonNull).findFirst().orElse(null));
    }

    private @NotNull Color getBgColor(boolean marked) {
        return (marked ? COLOR_BG_MARKED : COLOR_BG_NORMAL);
    }

    private @NotNull Color getFgColor(boolean marked) {
        return (marked ? COLOR_FG_MARKED : COLOR_FG_NORMAL);
    }

    private void getMaxSize(@NotNull Dimension maxSizeHolder, @NotNull Dimension currentSize) {
        maxSizeHolder.width  = Math.max(maxSizeHolder.width, currentSize.width);
        maxSizeHolder.height = Math.max(maxSizeHolder.height, currentSize.height);
    }

    private int getSelectedDayOfMonth() {
        return Objects.requireNonNullElse(fromPanelLabel(RX2, (p, l) -> parseLabelDate(l)), storedDate);
    }

    private int getSelectedMonth() {
        return Objects.requireNonNullElse(fieldMonths.getSelectedItem(), storedMonth);
    }

    private int getSelectedYear() {
        return Objects.requireNonNullElse(fieldYears.getSelectedItem(), storedYear);
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

    private int parseLabelDate(@NotNull JLabel l) {
        try { return Integer.parseInt(l.getText()); } catch(Throwable t) { return storedDate; }
    }

    private void setLabel(@Range(from = 0, to = 41) int index, @NotNull String text, @Nullable String toolTip, @NotNull Color bgColor, @NotNull Color fgColor, boolean addMouseListener) {
        withPanelLabel(Pattern.quote(String.format("c%d", index + 1)), (p, l) -> {
            p.removeMouseListener(mouseListener);
            p.setBackground(bgColor);
            p.setToolTipText(toolTip);
            if(addMouseListener) p.addMouseListener(mouseListener);
            l.setForeground(fgColor);
            l.setText(text);
        });
    }

    private void storeDate(@NotNull PGCalendar date) {
        storedMonth = date.getMonth();
        storedYear  = date.getYear();
        storedDate  = date.getDate();
    }

    private void updateUI() {
        SwingUtilities.invokeLater(() -> {
            PGCalendar cal       = new PGCalendar(storedYear, storedMonth, 1);
            PGCalendar nextMonth = new PGCalendar(storedYear, storedMonth, 1).addMonths(1);
            PGCalendar prevMonth = new PGCalendar(storedYear, storedMonth, 1).addMonths(-1);
            int        wd        = (cal.getDayOfWeek() - 1);
            int        dc        = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            int        ix        = 0;

            buttonNext.setToolTipText(DATE_FMT2.format(nextMonth.getTime()));
            buttonPrev.setToolTipText(DATE_FMT2.format(prevMonth.getTime()));
            buttonNext.setEnabled(nextMonth.getYear() <= maxYear);
            buttonPrev.setEnabled(prevMonth.getYear() >= minYear);

            fieldMonths.setSelectedItem(storedMonth);
            fieldYears.setSelectedItem(storedYear);

            while(ix < wd) setLabel(ix++, " ", null, COLOR_BG_NORMAL, COLOR_FG_NORMAL, false);
            for(int dt = 1; dt <= dc; dt++) setLabel(ix++, String.valueOf(dt), DATE_FMT1.format(cal.setDate(dt).toSQLDate()), getBgColor((dt == storedDate)), getFgColor((dt == storedDate)), true);
            while(ix < 42) setLabel(ix++, " ", null, COLOR_BG_NORMAL, COLOR_FG_NORMAL, false);
            calendarPanel.repaint();
        });
    }

    private void withLabel(@NotNull JPanel panel, @NotNull BiConsumer<JPanel, JLabel> consumer) {
        fromLabel(panel, (p, l) -> {
            consumer.accept(p, l);
            return null;
        });
    }

    private void withPanelLabel(@NotNull @NonNls @RegExp @Language("RegExp") String pattern, @NotNull BiConsumer<JPanel, JLabel> consumer) {
        fromPanelLabel(pattern, (panel, label) -> {
            consumer.accept(panel, label);
            return null;
        });
    }

    public static @Nullable Date execute(@Nullable Component owner, int startYear, int endYear, @Nullable Date date) {
        return execute(owner, startYear, endYear, date, new IntegerRef(0));
    }

    public static @Nullable Date execute(@Nullable Component owner, int startYear, int endYear, @Nullable Date date, @NotNull IntegerRef buttonChoiceRef) {
        CalendarDialog dlg = CalendarDialog.create(CalendarDialog.class, owner, "dlg.title.calendar", M.msgs, startYear, endYear, date);
        buttonChoiceRef.value = dlg.getExitCode();
        return ((buttonChoiceRef.value == OK_BUTTON) ? dlg.getDate() : null);
    }

    private final class CalendarMouseListener extends MouseAdapter {
        private Color savedBgColor = COLOR_BG_NORMAL;
        private Color savedFgColor = COLOR_FG_NORMAL;

        private CalendarMouseListener() { }

        public @Override void mouseClicked(@NotNull MouseEvent e) {
            if(e.getComponent() instanceof JPanel panel) withLabel(panel, (p, l) -> {
                storedDate   = parseLabelDate(l);
                storedYear   = getSelectedYear();
                storedMonth  = getSelectedMonth();
                savedBgColor = COLOR_BG_MARKED;
                savedFgColor = COLOR_FG_MARKED;
                updateUI();
            });
        }

        public @Override void mouseEntered(@NotNull MouseEvent e) {
            JPanel p = (JPanel)e.getComponent();
            savedBgColor = p.getBackground();
            savedFgColor = p.getForeground();
            p.setBackground(COLOR_BG_MOUSEOVER);
            p.setForeground(COLOR_FG_MOUSEOVER);
            p.setCursor(CURSOR_HAND);
        }

        public @Override void mouseExited(@NotNull MouseEvent e) {
            JPanel p = (JPanel)e.getComponent();
            p.setBackground(savedBgColor);
            p.setForeground(savedFgColor);
            p.setCursor(CURSOR_DEF);
        }
    }
}
