package com.projectgalen.lib.ui.components.table;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: PGJTable.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 12, 2023
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

import com.projectgalen.lib.ui.base.NonGUIEditorCustomComponent;
import com.projectgalen.lib.ui.components.table.PGJTable.DummyRowModel.DummyDataModel;
import com.projectgalen.lib.ui.components.table.PGJTable.DummyRowModel.DummyDataModel.DummyData;
import com.projectgalen.lib.ui.components.table.misc.*;
import com.projectgalen.lib.ui.interfaces.PGDataModel;
import com.projectgalen.lib.utils.EventListeners;
import com.projectgalen.lib.utils.NullTools;
import com.projectgalen.lib.utils.refs.IntegerRef;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static javax.swing.SwingUtilities.invokeLater;

@SuppressWarnings("unused")
public class PGJTable<T> extends JScrollPane implements NonGUIEditorCustomComponent, NullTools {
    protected static final int DEFAULT_ROW_HEIGHT = 16;

    protected final EventListeners   listeners          = new EventListeners();
    protected final PGJTableModel<T> model;
    protected       PGJTableImpl<T>  table;
    protected       boolean          heightMatchesRows;
    protected       boolean          hideHeader;
    protected       int              maximumVisibleRows;
    protected       VSizePolicy      vSizePolicy;
    protected       double[]         colSizes;
    protected       int              rowHeightTune      = 4;
    protected       int              totalRowHeightTune = 5;

    public PGJTable() {
        this(new PGJTableModel<>(new DummyRowModel<>(), new DummyDataModel<>()), SelectionMode.Single, new double[0], Integer.MAX_VALUE, VSizePolicy.None, false);
    }

    public PGJTable(@NotNull PGJTableModel<T> model, @NotNull SelectionMode selectionMode) {
        this(model, selectionMode, new double[0], Integer.MAX_VALUE, VSizePolicy.None, false);
    }

    public PGJTable(@NotNull PGJTableRowModel<T> rowModel, @NotNull SelectionMode selectionMode) {
        this(rowModel, selectionMode, new double[0], Integer.MAX_VALUE, VSizePolicy.None, false);
    }

    public PGJTable(@NotNull PGJTableModel<T> model, @NotNull SelectionMode selectionMode, int maximumVisibleRows, @NotNull VSizePolicy vSizePolicy, boolean hideHeader) {
        this(model, selectionMode, new double[0], maximumVisibleRows, vSizePolicy, hideHeader);
    }

    public PGJTable(@NotNull PGJTableRowModel<T> rowModel, @NotNull SelectionMode selectionMode, int maximumVisibleRows, @NotNull VSizePolicy vSizePolicy, boolean hideHeader) {
        this(rowModel, selectionMode, new double[0], maximumVisibleRows, vSizePolicy, hideHeader);
    }

    public PGJTable(@NotNull PGJTableRowModel<T> rowModel,
                    @NotNull SelectionMode selectionMode,
                    double @NotNull [] columnSizeWeights,
                    int maximumVisibleRows,
                    @NotNull VSizePolicy vSizePolicy,
                    boolean hideHeader) {
        this(new PGJTableModel<>(rowModel), selectionMode, columnSizeWeights, maximumVisibleRows, vSizePolicy, hideHeader);
    }

    public PGJTable(@NotNull PGJTableModel<T> model,
                    @NotNull SelectionMode selectionMode,
                    double @NotNull [] columnSizeWeights,
                    int maximumVisibleRows,
                    @NotNull VSizePolicy vSizePolicy,
                    boolean hideHeader) {
        super();
        this.model              = model;
        this.hideHeader         = hideHeader;
        this.heightMatchesRows  = false;
        this.maximumVisibleRows = maximumVisibleRows;
        this.vSizePolicy        = vSizePolicy;
        this.colSizes           = columnSizeWeights;

        setViewportView(table = new PGJTableImpl<>(this.model, true, false, false, true));

        table.setSelectionMode(selectionMode.getValue());
        table.getSelectionModel().addListSelectionListener(this::onSelected);
        this.model.addTableModelListener(e -> revalidate());

        invokeLater(() -> {
            with(getColumnHeader(), ch -> ch.setVisible(!this.hideHeader));
            revalidate();
            invokeLater(this::updateColumnPreferredWidths);
        });
    }

    public int getRowHeightTune() {
        return rowHeightTune;
    }

    public @NotNull JTable getTableComponent() {
        return table;
    }

    public int getTotalRowHeightTune() {
        return totalRowHeightTune;
    }

    public VSizePolicy getvSizePolicy() {
        return vSizePolicy;
    }

    public void setRowHeightTune(int rowHeightTune) {
        this.rowHeightTune = rowHeightTune;
    }

    public void addTableSelectionListener(@NotNull PGJTableSelectionListener listener) {
        with(listeners, l -> l.add(PGJTableSelectionListener.class, listener));
    }

    public void clearSelection() {
        with(table, JTable::clearSelection);
    }

    public void fireTableCellUpdated(int rowIndex, int columnIndex) {
        with(getTableModel(), o -> o.fireTableCellUpdated(rowIndex, columnIndex));
    }

    public void fireTableDataChanged() {
        with(getTableModel(), AbstractTableModel::fireTableDataChanged);
    }

    public void fireTableRowDeleted(int rowIndex) {
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        with(getTableModel(), o -> o.fireTableRowsDeleted(firstRow, lastRow));
    }

    public double[] getColSizes() {
        return Objects.requireNonNullElseGet(colSizes, () -> new double[0]);
    }

    public TableColumn getColumn(int columnIndex) {
        return fromV(getColumnModel(), m -> {
            if(columnIndex < 0 || columnIndex >= m.getColumnCount()) throw new IndexOutOfBoundsException(columnIndex);
            return m.getColumn(columnIndex);
        }, null);
    }

    public int getColumnCount() {
        return fromV(getColumnModel(), TableColumnModel::getColumnCount, 0);
    }

    public TableColumnModel getColumnModel() {
        return fromV(table, JTable::getColumnModel, null);
    }

    public PGDataModel<T> getDataModel() {
        return fromV(model, PGJTableModel::getDataModel, null);
    }

    public int getMaximumVisibleRows() {
        return maximumVisibleRows;
    }

    public Dimension getPreferredScrollableViewportSize() {
        return ((table == null) ? getPreferredSize() : getViewPortSize(table.getPreferredScrollableViewportSize()));
    }

    public PGJTableRowModel<T> getRowModel() {
        return fromV(model, PGJTableModel::getRowModel, null);
    }

    public List<T> getSelectedItems() {
        return from(getDataModel(), dm -> IntStream.of(getSelectedRows()).mapToObj(dm::get).toList(), ArrayList::new);
    }

    public int[] getSelectedRows() {
        return from(table, JTable::getSelectedRows, () -> new int[0]);
    }

    public SelectionMode getSelectionMode() {
        return fromV(getSelectionModel(), sm -> SelectionMode.valueOf(sm.getSelectionMode()), SelectionMode.Single);
    }

    public ListSelectionModel getSelectionModel() {
        return fromV(table, JTable::getSelectionModel, null);
    }

    public void setTotalRowHeightTune(int totalRowHeightTune) {
        this.totalRowHeightTune = totalRowHeightTune;
    }

    public PGJTableModel<T> getTableModel() {
        return model;
    }

    public boolean isHeightMatchesRows() {
        return heightMatchesRows;
    }

    public boolean isHideHeader() {
        return hideHeader;
    }

    public void removeTableSelectionListener(@NotNull PGJTableSelectionListener listener) {
        with(listeners, l -> l.remove(PGJTableSelectionListener.class, listener));
    }

    public void setCellEditor(int columnIndex, @NotNull TableCellEditor editor) {
        with(getColumn(columnIndex), c -> c.setCellEditor(editor));
    }

    public void setCellRenderer(int columnIndex, @NotNull TableCellRenderer renderer) {
        with(getColumn(columnIndex), c -> c.setCellRenderer(renderer));
    }

    public void setColSizes(double @NotNull [] colSizes) {
        this.colSizes = colSizes;
        invokeLater(this::updateColumnPreferredWidths);
    }

    public void setData(@NotNull List<T> aList) {
        setDataModel(new PGListDataModel<>(aList));
    }

    public void setDataModel(PGListDataModel<T> dataModel) {
        with(getTableModel(), o -> o.setDataModel(dataModel));
        revalidate();
        with(table, JComponent::revalidate);
    }

    public @Override void setFont(@NotNull Font font) {
        invokeLater(() -> {
            with(table, t -> {
                super.setFont(font);
                t.setFont(font);
                t.setRowHeight(getFontHeight(font) + 4);

                Font hf = font.deriveFont(Font.BOLD);
                t.getTableHeader().setFont(hf);
                t.getTableHeader().setPreferredSize(new Dimension(1, getFontHeight(hf) + 6));

                if(heightMatchesRows) revalidate();
            });
        });
    }

    public void setHeightMatchesRows(boolean heightMatchesRows) {
        this.heightMatchesRows = heightMatchesRows;
        setPreferredScrollableViewportSize(getPreferredScrollableViewportSize());
    }

    public void setHideHeader(boolean hideHeader) {
        this.hideHeader = hideHeader;
        getColumnHeader().setVisible(!this.hideHeader);
        revalidate();
    }

    public void setMaximumVisibleRows(int rows) {
        maximumVisibleRows = rows;
        setPreferredScrollableViewportSize(getPreferredScrollableViewportSize());
        revalidate();
    }

    public void setPreferredScrollableViewportSize(Dimension size) {
        System.out.printf("%s: table: %s\n", "setPreferredScrollableViewportSize", foo01(table));
        if(table != null) {
            table.setPreferredScrollableViewportSize(getViewPortSize(size));
        }
    }

    public void setRowModel(@NotNull PGJTableRowModel<T> rowModel) {
        with(model, m -> m.setRowModel(rowModel));
    }

    public void setSelectionMode(@NotNull SelectionMode mode) {
        with(table, o -> o.setSelectionMode(mode.getValue()));
    }

    public void setvSizePolicy(VSizePolicy vSizePolicy) {
        this.vSizePolicy = vSizePolicy;
        setPreferredScrollableViewportSize(getPreferredScrollableViewportSize());
    }

    public void withColumnModels(BiConsumer<Integer, TableColumn> biConsumer) {
        withColumnModels(0, Integer.MAX_VALUE, biConsumer);
    }

    public void withColumnModels(int endIndex, BiConsumer<Integer, TableColumn> biConsumer) {
        withColumnModels(0, endIndex, biConsumer);
    }

    public void withColumnModels(int startIndex, int endIndex, BiConsumer<Integer, TableColumn> biConsumer) {
        with2(table, JTable::getColumnModel, model -> {
            int cc    = model.getColumnCount();
            int start = Math.max(0, Math.min(cc, Math.min(startIndex, endIndex)));
            int end   = Math.max(0, Math.min(cc, Math.max(startIndex, endIndex)));
            for(int i = start; i < end; i++) biConsumer.accept(i, model.getColumn(i));
        });
    }

    private int calculateHeight(int rows) {
        return (((table == null) ? (DEFAULT_ROW_HEIGHT * rows) : ((table.getRowHeight() + rowHeightTune) * rows)) + totalRowHeightTune);
    }

    @Contract(pure = true) private @NotNull String foo01(Object arg) {
        return ((arg == null) ? "null" : "not null");
    }

    private int getFontHeight(@NotNull Font font) {
        return fromV(getFontMetrics(font), m -> (m.getMaxAscent() + m.getMaxDescent()), 0);
    }

    private @NotNull Dimension getViewPortSize(@NotNull Dimension sizeIn) {
        return new Dimension(sizeIn.width, switch(Objects.requireNonNullElse(vSizePolicy, VSizePolicy.None)) {
            case None -> sizeIn.height;
            case FitsRows -> calculateHeight(Math.max(1, Math.min(ofNullable(table).map(JTable::getModel).map(TableModel::getRowCount).orElse(maximumVisibleRows), maximumVisibleRows)));
            case Fixed -> calculateHeight(Math.max(1, maximumVisibleRows));
        });
    }

    private void onSelected(ListSelectionEvent event) {
        int[]                  idx  = ofNullable(table).map(JTable::getSelectionModel).map(ListSelectionModel::getSelectedIndices).orElseGet(() -> new int[0]);
        PGDataModel<T>         data = ofNullable(model).map(PGJTableModel::getDataModel).orElseGet(PGListDataModel::new);
        PGJTableSelectionEvent evt  = new PGJTableSelectionEvent(this, idx, IntStream.of(idx).mapToObj(data::get).toList());
        with(listeners, l -> invokeLater(() -> l.fireEvent(PGJTableSelectionListener.class, evt, PGJTableSelectionListener::onSelection)));
    }

    private void updateColumnPreferredWidths() {
        ifDo(getColSizes(), cs -> (cs.length > 0), cs -> with(getColumnModel(), cm -> {
            int        diff       = (cm.getColumnCount() - Math.min(cs.length, cm.getColumnCount()));
            int        totalWidth = cm.getTotalColumnWidth();
            IntegerRef widthAcc   = new IntegerRef(0);

            withColumnModels(cs.length, (c, column) -> {
                int cw = Math.max(1, (int)(totalWidth * cs[c]));
                column.setPreferredWidth(cw);
                widthAcc.value += cw;
            });
            withColumnModels(cs.length, (c, column) -> column.setPreferredWidth((totalWidth - widthAcc.value) / diff));
            with(table, t -> invokeLater(t::revalidate));
        }));
    }

    static final class DummyRowModel<T> extends AbstractPGJTableRowModel<T> {

        public static final String[] columnNames = { "Alpha", "Beta" };

        public DummyRowModel()                                                                  { super(); }

        public @Override int getColumnAlignment(int columnIndex)                                { return SwingConstants.CENTER; }

        public @Override Class<?> getColumnClass(int columnIndex)                               { return String.class; }

        public @Override int getColumnCount()                                                   { return columnNames.length; }

        public @Contract(pure = true) @Override @Nullable String getColumnName(int columnIndex) { return columnNames[columnIndex]; }

        public @Override @Nullable Object getColumnValue(@NotNull T obj, int columnIndex)       { return ((DummyData)obj).data[columnIndex]; }

        static final class DummyDataModel<T> implements PGDataModel<T> {

            private final List<T> data = DummyData.getDummyData();

            public DummyDataModel()                                                    { }

            @Override public void forEach(@NotNull Consumer<? super T> consumer)       { data.forEach(consumer); }

            @Override public T get(@Range(from = 0, to = Integer.MAX_VALUE) int index) { return data.get(index); }

            @Override public int size()                                                { return data.size(); }

            @Override public @NotNull Stream<T> stream()                               { return data.stream(); }

            public static final class DummyData {
                public final String[] data;

                public DummyData(int row) {
                    String[] names = DummyRowModel.columnNames;
                    data = new String[names.length];
                    for(int c = 0; c < names.length; c++) data[c] = "%s Row %d".formatted(names[c], row);
                }

                public static <T> List<T> getDummyData() {
                    //noinspection unchecked
                    return (List<T>)IntStream.range(0, 4).mapToObj(DummyData::new).toList();
                }
            }
        }
    }
}
