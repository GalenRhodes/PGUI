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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
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
    protected static final int DEFAULT_ROW_HEIGHT = 19;

    protected final EventListeners   listeners          = new EventListeners();
    protected final PGJTableModel<T> model;
    protected       PGJTableImpl<T>  table;
    protected       boolean          hideHeader;
    protected       int              maximumVisibleRows = Integer.MAX_VALUE;
    protected       VSizePolicy      verticalSizePolicy = VSizePolicy.None;
    protected       double[]         columnSizeWeights  = new double[0];
    protected       int              rowHeightTune      = 0;
    protected       int              totalRowHeightTune = 0;
    protected       Font             aFont;
    protected       Font             aHeaderFont;
    protected       int              aRowHeight;
    protected       int              aHeaderRowHeight;

    public PGJTable() {
        this(new PGJTableModel<>(new DummyRowModel<>(), new DummyDataModel<>()), null, null, -1, null, false);
    }

    public PGJTable(@NotNull PGJTableModel<T> model, @NotNull SelectionMode selectionMode) {
        this(model, selectionMode, null, -1, null, false);
    }

    public PGJTable(@NotNull PGJTableRowModel<T> rowModel, @NotNull SelectionMode selectionMode) {
        this(rowModel, selectionMode, null, -1, null, false);
    }

    public PGJTable(@NotNull PGJTableModel<T> model, @NotNull SelectionMode selectionMode, int maximumVisibleRows, @NotNull VSizePolicy verticalSizePolicy, boolean hideHeader) {
        this(model, selectionMode, null, maximumVisibleRows, verticalSizePolicy, hideHeader);
    }

    public PGJTable(@NotNull PGJTableRowModel<T> rowModel, @NotNull SelectionMode selectionMode, int maximumVisibleRows, @NotNull VSizePolicy verticalSizePolicy, boolean hideHeader) {
        this(rowModel, selectionMode, null, maximumVisibleRows, verticalSizePolicy, hideHeader);
    }

    public PGJTable(@NotNull PGJTableRowModel<T> rowModel,
                    @Nullable SelectionMode selectionMode,
                    double @Nullable [] columnSizeWeights,
                    int maximumVisibleRows,
                    @Nullable VSizePolicy verticalSizePolicy,
                    boolean hideHeader) {
        this(new PGJTableModel<>(rowModel), selectionMode, columnSizeWeights, maximumVisibleRows, verticalSizePolicy, hideHeader);
    }

    public PGJTable(@NotNull PGJTableModel<T> model,
                    @Nullable SelectionMode selectionMode,
                    double @Nullable [] columnSizeWeights,
                    int maximumVisibleRows,
                    @Nullable VSizePolicy verticalSizePolicy,
                    boolean hideHeader) {
        super();

        this.model      = model;
        this.hideHeader = hideHeader;

        setDoubleBuffered(true);
        setViewportView(table = new PGJTableImpl<>(this.model));

        if(maximumVisibleRows >= 0) this.maximumVisibleRows = maximumVisibleRows;
        this.verticalSizePolicy = Objects.requireNonNullElse(verticalSizePolicy, this.verticalSizePolicy);
        this.columnSizeWeights  = Objects.requireNonNullElse(columnSizeWeights, this.columnSizeWeights);

        if(selectionMode != null) table.setSelectionMode(selectionMode.getValue());
        table.getSelectionModel().addListSelectionListener(this::onSelected);
        this.model.addTableModelListener(e -> revalidate());
        setFont((aFont = table.getFont()));

        invokeLater(() -> {
            with(getColumnHeader(), ch -> ch.setVisible(!this.hideHeader));
            revalidate();
            updateColumnPreferredWidths();
        });
    }

    public void addRowSelectionInterval(int index0, int index1) {
        if(table != null) table.addRowSelectionInterval(index0, index1);
    }

    public void addTableSelectionListener(@NotNull PGJTableSelectionListener listener) {
        with(listeners, l -> l.add(PGJTableSelectionListener.class, listener));
    }

    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
        if(table != null) table.changeSelection(rowIndex, columnIndex, toggle, extend);
    }

    public void clearSelection() {
        with(table, JTable::clearSelection);
    }

    public int columnAtPoint(@NotNull Point point) {
        return ((table != null) ? table.columnAtPoint(point) : 0);
    }

    public void columnMarginChanged(ChangeEvent e) {
        if(table != null) table.columnMarginChanged(e);
    }

    public void columnMoved(TableColumnModelEvent e) {
        if(table != null) table.columnMoved(e);
    }

    public void columnRemoved(TableColumnModelEvent e) {
        if(table != null) table.columnRemoved(e);
    }

    public void columnSelectionChanged(ListSelectionEvent e) {
        if(table != null) table.columnSelectionChanged(e);
    }

    public boolean editCellAt(int row, int column) {
        return ((table != null) && table.editCellAt(row, column));
    }

    public boolean editCellAt(int row, int column, EventObject e) {
        return ((table != null) && table.editCellAt(row, column, e));
    }

    public void editingCanceled(ChangeEvent e) {
        if(table != null) table.editingCanceled(e);
    }

    public void editingStopped(ChangeEvent e) {
        if(table != null) table.editingStopped(e);
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

    public int getAutoResizeMode() {
        return ((table != null) ? table.getAutoResizeMode() : 0);
    }

    public TableCellEditor getCellEditor() {
        return ((table != null) ? table.getCellEditor() : null);
    }

    public TableCellEditor getCellEditor(int row, int column) {
        return ((table != null) ? table.getCellEditor(row, column) : null);
    }

    public @NotNull Rectangle getCellRect(int row, int column, boolean includeSpacing) {
        return ((table != null) ? table.getCellRect(row, column, includeSpacing) : new Rectangle());
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        return ((table != null) ? table.getCellRenderer(row, column) : null);
    }

    public boolean getCellSelectionEnabled() {
        return table.getCellSelectionEnabled();
    }

    public TableColumn getColumn(int columnIndex) {
        return fromV(getColumnModel(), m -> {
            if(columnIndex < 0 || columnIndex >= m.getColumnCount()) throw new IndexOutOfBoundsException(columnIndex);
            return m.getColumn(columnIndex);
        }, null);
    }

    public TableColumn getColumn(@NotNull Object identifier) {
        return ((table != null) ? table.getColumn(identifier) : null);
    }

    public Class<?> getColumnClass(int column) {
        return ((table != null) ? table.getColumnClass(column) : String.class);
    }

    public int getColumnCount() {
        return fromV(getColumnModel(), TableColumnModel::getColumnCount, 0);
    }

    public TableColumnModel getColumnModel() {
        return fromV(table, JTable::getColumnModel, null);
    }

    public String getColumnName(int column) {
        return ((table != null) ? table.getColumnName(column) : "");
    }

    public boolean getColumnSelectionAllowed() {
        return table.getColumnSelectionAllowed();
    }

    public double[] getColumnSizeWeights() {
        return Objects.requireNonNullElseGet(columnSizeWeights, () -> new double[0]);
    }

    public PGDataModel<T> getDataModel() {
        return fromV(model, PGJTableModel::getDataModel, null);
    }

    public TableCellEditor getDefaultEditor(Class<?> columnClass) {
        return ((table != null) ? table.getDefaultEditor(columnClass) : null);
    }

    public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
        return ((table != null) ? table.getDefaultRenderer(columnClass) : null);
    }

    public boolean getDragEnabled() {
        return ((table != null) && table.getDragEnabled());
    }

    public int getEditingColumn() {
        return ((table != null) ? table.getEditingColumn() : 0);
    }

    public int getEditingRow() {
        return ((table != null) ? table.getEditingRow() : 0);
    }

    public Component getEditorComponent() {
        return ((table != null) ? table.getEditorComponent() : null);
    }

    public Font getFont() {
        return aFont;
    }

    public Color getGridColor() {
        return ((table != null) ? table.getGridColor() : Color.WHITE);
    }

    public int getMaximumVisibleRows() {
        return maximumVisibleRows;
    }

    public Dimension getPreferredScrollableViewportSize() {
        return ((table == null) ? getPreferredSize() : table.getPreferredScrollableViewportSize());
    }

    public int getRowCount() {
        return ((table != null) ? table.getRowCount() : 0);
    }

    public int getRowHeight() {
        return ((table != null) ? table.getRowHeight() : 0);
    }

    public int getRowHeight(int row) {
        return ((table != null) ? table.getRowHeight(row) : 0);
    }

    public int getRowHeightTune() {
        return rowHeightTune;
    }

    public int getRowMargin() {
        return ((table != null) ? table.getRowMargin() : 0);
    }

    public PGJTableRowModel<T> getRowModel() {
        return fromV(model, PGJTableModel::getRowModel, null);
    }

    public boolean getRowSelectionAllowed() {
        return table.getRowSelectionAllowed();
    }

    public RowSorter<? extends TableModel> getRowSorter() {
        return ((table != null) ? table.getRowSorter() : null);
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return ((table != null) ? table.getScrollableBlockIncrement(visibleRect, orientation, direction) : 0);
    }

    public boolean getScrollableTracksViewportHeight() {
        return ((table != null) && table.getScrollableTracksViewportHeight());
    }

    public boolean getScrollableTracksViewportWidth() {
        return ((table != null) && table.getScrollableTracksViewportWidth());
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return ((table != null) ? table.getScrollableUnitIncrement(visibleRect, orientation, direction) : 0);
    }

    public int getSelectedColumn() {
        return ((table != null) ? table.getSelectedColumn() : 0);
    }

    public int getSelectedColumnCount() {
        return ((table != null) ? table.getSelectedColumnCount() : 0);
    }

    public int[] getSelectedColumns() {
        return ((table != null) ? table.getSelectedColumns() : new int[0]);
    }

    public List<T> getSelectedItems() {
        return from(getDataModel(), dm -> IntStream.of(getSelectedRows()).mapToObj(dm::get).toList(), ArrayList::new);
    }

    public int getSelectedRow() {
        return ((table != null) ? table.getSelectedRow() : 0);
    }

    public int getSelectedRowCount() {
        return ((table != null) ? table.getSelectedRowCount() : 0);
    }

    public int[] getSelectedRows() {
        return from(table, JTable::getSelectedRows, () -> new int[0]);
    }

    public Color getSelectionBackground() {
        return ((table != null) ? table.getSelectionBackground() : Objects.requireNonNullElseGet(UIManager.getColor("Table.selectionBackground"), () -> Color.BLUE));
    }

    public Color getSelectionForeground() {
        return ((table != null) ? table.getSelectionForeground() : Objects.requireNonNullElseGet(UIManager.getColor("Table.selectionForground"), () -> Color.WHITE));
    }

    public SelectionMode getSelectionMode() {
        return fromV(getSelectionModel(), sm -> SelectionMode.valueOf(sm.getSelectionMode()), SelectionMode.Single);
    }

    public ListSelectionModel getSelectionModel() {
        return fromV(table, JTable::getSelectionModel, null);
    }

    public boolean getShowHorizontalLines() {
        return ((table != null) && table.getShowHorizontalLines());
    }

    public boolean getShowVerticalLines() {
        return ((table != null) && table.getShowVerticalLines());
    }

    public boolean getSurrendersFocusOnKeystroke() {
        return ((table != null) && table.getSurrendersFocusOnKeystroke());
    }

    public @NotNull JTable getTableComponent() {
        return table;
    }

    public JTableHeader getTableHeader() {
        return ((table != null) ? table.getTableHeader() : null);
    }

    public PGJTableModel<T> getTableModel() {
        return model;
    }

    public String getToolTipText(@NotNull MouseEvent event) {
        return ((table != null) ? table.getToolTipText(event) : "");
    }

    public int getTotalRowHeightTune() {
        return totalRowHeightTune;
    }

    public boolean getUpdateSelectionOnSort() {
        return ((table != null) && table.getUpdateSelectionOnSort());
    }

    public Object getValueAt(int row, int column) {
        return ((table != null) ? table.getValueAt(row, column) : 0);
    }

    public VSizePolicy getVerticalSizePolicy() {
        return Objects.requireNonNullElse(verticalSizePolicy, VSizePolicy.None);
    }

    public boolean isCellEditable(int row, int column) {
        return ((table != null) && table.isCellEditable(row, column));
    }

    public boolean isCellSelected(int row, int column) {
        return ((table != null) && table.isCellSelected(row, column));
    }

    public boolean isColumnSelected(int column) {
        return ((table != null) && table.isColumnSelected(column));
    }

    public boolean isEditing() {
        return ((table != null) && table.isEditing());
    }

    public boolean isHideHeader() {
        return hideHeader;
    }

    public boolean isRowSelected(int row) {
        return ((table != null) && table.isRowSelected(row));
    }

    public void moveColumn(int column, int targetColumn) {
        if(table != null) table.moveColumn(column, targetColumn);
    }

    public void removeEditor() {
        if(table != null) table.removeEditor();
    }

    public void removeRowSelectionInterval(int index0, int index1) {
        if(table != null) table.removeRowSelectionInterval(index0, index1);
    }

    public void removeTableSelectionListener(@NotNull PGJTableSelectionListener listener) {
        with(listeners, l -> l.remove(PGJTableSelectionListener.class, listener));
    }

    public int rowAtPoint(@NotNull Point point) {
        return ((table != null) ? table.rowAtPoint(point) : 0);
    }

    public void selectAll() {
        if(table != null) table.selectAll();
    }

    public void setAutoResizeMode(int mode) {
        if(table != null) table.setAutoResizeMode(mode);
    }

    public void setCellEditor(int columnIndex, @NotNull TableCellEditor editor) {
        with(getColumn(columnIndex), c -> c.setCellEditor(editor));
    }

    public void setCellEditor(TableCellEditor anEditor) {
        if(table != null) table.setCellEditor(anEditor);
    }

    public void setCellRenderer(int columnIndex, @NotNull TableCellRenderer renderer) {
        with(getColumn(columnIndex), c -> c.setCellRenderer(renderer));
    }

    public void setCellSelectionEnabled(boolean cellSelectionEnabled) {
        table.setCellSelectionEnabled(cellSelectionEnabled);
    }

    public void setColumnModel(@NotNull TableColumnModel columnModel) {
        if(table != null) table.setColumnModel(columnModel);
    }

    public void setColumnSelectionAllowed(boolean columnSelectionAllowed) {
        table.setColumnSelectionAllowed(columnSelectionAllowed);
    }

    public void setColumnSelectionInterval(int index0, int index1) {
        if(table != null) table.setColumnSelectionInterval(index0, index1);
    }

    public void setColumnSizeWeights(double @NotNull [] columnSizeWeights) {
        this.columnSizeWeights = columnSizeWeights;
        invokeLater(this::updateColumnPreferredWidths);
    }

    public void setData(@NotNull List<T> aList) {
        setDataModel(new PGListDataModel<>(aList));
    }

    public void setDataModel(PGListDataModel<T> dataModel) {
        with(getTableModel(), o -> o.setDataModel(dataModel));
        resizeTable();
    }

    public void setDefaultEditor(Class<?> columnClass, TableCellEditor editor) {
        if(table != null) table.setDefaultEditor(columnClass, editor);
    }

    public void setDefaultRenderer(Class<?> columnClass, TableCellRenderer renderer) {
        if(table != null) table.setDefaultRenderer(columnClass, renderer);
    }

    public void setDragEnabled(boolean b) {
        if(table != null) table.setDragEnabled(b);
    }

    public void setEditingColumn(int aColumn) {
        if(table != null) table.setEditingColumn(aColumn);
    }

    public void setEditingRow(int aRow) {
        if(table != null) table.setEditingRow(aRow);
    }

    public @Override void setFont(@NotNull Font font) {
        if(SwingUtilities.isEventDispatchThread()) {
            aFont       = font;
            aHeaderFont = aFont.deriveFont(Font.BOLD);

            if(table != null) {
                super.setFont(aFont);
                table.setFont(aFont);
                table.setRowHeight(aRowHeight = (getFontHeight(aFont) + 4 + table.getRowMargin()));

                JTableHeader header = table.getTableHeader();
                header.setFont(aHeaderFont);
                header.setPreferredSize(new Dimension(1, aHeaderRowHeight = (getFontHeight(aHeaderFont) + 4)));

                if(verticalSizePolicy == VSizePolicy.FitsRows) revalidate();
                resizeTable();
            }
        }
        else invokeLater(() -> setFont(font));
    }

    public void setGridColor(@NotNull Color gridColor) {
        if(table != null) table.setGridColor(gridColor);
    }

    public void setHideHeader(boolean hideHeader) {
        this.hideHeader = hideHeader;
        getColumnHeader().setVisible(!this.hideHeader);
        revalidate();
    }

    public void setMaximumVisibleRows(int rows) {
        maximumVisibleRows = rows;
        resizeTable();
    }

    public void setPreferredScrollableViewportSize(Dimension size) {
        if(table != null) table.setPreferredScrollableViewportSize(getViewPortSize(size));
        revalidate();
    }

    public void setRowHeight(int rowHeight) {
        if(table != null) table.setRowHeight(rowHeight);
    }

    public void setRowHeight(int row, int rowHeight) {
        if(table != null) table.setRowHeight(row, rowHeight);
    }

    public void setRowHeightTune(int rowHeightTune) {
        this.rowHeightTune = rowHeightTune;
        resizeTable();
    }

    public void setRowMargin(int rowMargin) {
        if(table != null) table.setRowMargin(rowMargin);
    }

    public void setRowModel(@NotNull PGJTableRowModel<T> rowModel) {
        with(model, m -> m.setRowModel(rowModel));
        resizeTable();
    }

    public void setRowSelectionAllowed(boolean rowSelectionAllowed) {
        table.setRowSelectionAllowed(rowSelectionAllowed);
    }

    public void setRowSelectionInterval(int index0, int index1) {
        if(table != null) table.setRowSelectionInterval(index0, index1);
    }

    public void setRowSorter(RowSorter<? extends TableModel> sorter) {
        if(table != null) table.setRowSorter(sorter);
    }

    public void setSelectionBackground(Color selectionBackground) {
        if(table != null) table.setSelectionBackground(selectionBackground);
    }

    public void setSelectionForeground(Color selectionForeground) {
        if(table != null) table.setSelectionForeground(selectionForeground);
    }

    public void setSelectionMode(@NotNull SelectionMode mode) {
        with(table, o -> o.setSelectionMode(mode.getValue()));
    }

    public void setSelectionModel(@NotNull ListSelectionModel selectionModel) {
        if(table != null) table.setSelectionModel(selectionModel);
    }

    public void setShowGrid(boolean showGrid) {
        if(table != null) table.setShowGrid(showGrid);
    }

    public void setShowHorizontalLines(boolean showHorizontalLines) {
        if(table != null) table.setShowHorizontalLines(showHorizontalLines);
    }

    public void setShowVerticalLines(boolean showVerticalLines) {
        if(table != null) table.setShowVerticalLines(showVerticalLines);
    }

    public void setTotalRowHeightTune(int totalRowHeightTune) {
        this.totalRowHeightTune = totalRowHeightTune;
        resizeTable();
    }

    public void setUpdateSelectionOnSort(boolean update) {
        if(table != null) table.setUpdateSelectionOnSort(update);
    }

    public void setValueAt(Object aValue, int row, int column) {
        if(table != null) table.setValueAt(aValue, row, column);
    }

    public void setVerticalSizePolicy(VSizePolicy verticalSizePolicy) {
        System.out.printf("Setting VSizePolicy: %s\n", verticalSizePolicy);
        this.verticalSizePolicy = verticalSizePolicy;
        resizeTable();
    }

    public void sizeColumnsToFit(int resizingColumn) {
        if(table != null) table.sizeColumnsToFit(resizingColumn);
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

    private int _calculateHeight(int rows) {
        if(table == null) return ((DEFAULT_ROW_HEIGHT * rows) + totalRowHeightTune);
        return (((aRowHeight + rowHeightTune) * rows) + totalRowHeightTune);
    }

    private int calculateHeight(int rows) {
        int h = _calculateHeight(rows);
        System.out.printf("calculateHeight: rows(%,d) = %,d\n", rows, h);
        return h;
    }

    private int getFontHeight(@NotNull Font font) {
        return fromV(getFontMetrics(font), m -> (m.getMaxAscent() + m.getMaxDescent()), 0);
    }

    private @NotNull Dimension getViewPortSize(@NotNull Dimension sizeIn) {
        VSizePolicy policy  = getVerticalSizePolicy();
        int         maxRows = this.getMaximumVisibleRows();

        System.out.printf("maxRows: %,d; vSizePolicy: %s\n", maxRows, policy);

        Dimension d = new Dimension(sizeIn.width, switch(policy) {/*@f0*/
            case None     -> sizeIn.height;
            case FitsRows -> calculateHeight(Math.max(1, Math.min(ofNullable(table).map(JTable::getModel).map(TableModel::getRowCount).orElse(maxRows), maxRows)));
            case Fixed    -> calculateHeight(Math.max(1, maxRows));
        });/*@f1*/

        System.out.printf("New Preferred Viewport Size: %,d x %,d\n", d.width, d.height);

        return d;
    }

    private void onSelected(ListSelectionEvent event) {
        int[]                  idx  = ofNullable(table).map(JTable::getSelectionModel).map(ListSelectionModel::getSelectedIndices).orElseGet(() -> new int[0]);
        PGDataModel<T>         data = ofNullable(model).map(PGJTableModel::getDataModel).orElseGet(PGListDataModel::new);
        PGJTableSelectionEvent evt  = new PGJTableSelectionEvent(this, idx, IntStream.of(idx).mapToObj(data::get).toList());
        with(listeners, l -> invokeLater(() -> l.fireEvent(PGJTableSelectionListener.class, evt, PGJTableSelectionListener::onSelection)));
    }

    private void resizeTable() {
        setPreferredScrollableViewportSize(getViewPortSize(getPreferredScrollableViewportSize()));
    }

    private void updateColumnPreferredWidths() {
        ifDo(getColumnSizeWeights(), cs -> (cs.length > 0), cs -> with(getColumnModel(), cm -> {
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
