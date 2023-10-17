package com.projectgalen.lib.ui.components.table;

import com.projectgalen.lib.ui.base.NonGUIEditorCustomComponent;
import com.projectgalen.lib.ui.components.table.PGJTable.DummyRowModel.DummyDataSupplier;
import com.projectgalen.lib.ui.components.table.PGJTable.DummyRowModel.DummyDataSupplier.DummyData;
import com.projectgalen.lib.ui.components.table.misc.*;
import com.projectgalen.lib.ui.interfaces.PGDataSupplier;
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

import static com.projectgalen.lib.ui.components.table.VSizePolicy.None;
import static java.util.Optional.ofNullable;
import static javax.swing.SwingUtilities.invokeLater;

@SuppressWarnings("unused")
public class PGJTable<T> extends JScrollPane implements NonGUIEditorCustomComponent, NullTools {

    protected static final int DEFAULT_ROW_HEIGHT = 20;

    protected final EventListeners listeners          = new EventListeners();
    protected       boolean        hideHeader;
    protected       int            aRowHeight;
    protected       int            aHeaderRowHeight;
    protected       int            maximumVisibleRows = Integer.MAX_VALUE;
    protected       int            rowHeightTune      = 0;
    protected       int            totalRowHeightTune = 0;
    protected       double[]       columnSizeWeights  = new double[0];
    protected       VSizePolicy    verticalSizePolicy = None;
    protected       Font           aFont;
    protected       Font           aHeaderFont;

    public PGJTable() {
        this(new PGJTableModel<>(new DummyRowModel<>(), new DummyDataSupplier<>()), null, null, 0, null, null, false);
    }

    public PGJTable(@NotNull PGJTableModel<T> model, @NotNull SelectionMode selectionMode) {
        this(model, selectionMode, null, 0, null, null, false);
    }

    public PGJTable(@NotNull PGJTableRowModel<T> rowModel, @NotNull SelectionMode selectionMode) {
        this(rowModel, selectionMode, null, 0, null, null, false);
    }

    public PGJTable(@NotNull PGJTableModel<T> model,
                    @NotNull SelectionMode selectionMode,
                    @Range(from = 0, to = Integer.MAX_VALUE) int maximumVisibleRows,
                    @NotNull VSizePolicy verticalSizePolicy,
                    boolean hideHeader) {
        this(model, selectionMode, null, maximumVisibleRows, verticalSizePolicy, null, hideHeader);
    }

    public PGJTable(@NotNull PGJTableRowModel<T> rowModel,
                    @NotNull SelectionMode selectionMode,
                    @Range(from = 0, to = Integer.MAX_VALUE) int maximumVisibleRows,
                    @NotNull VSizePolicy verticalSizePolicy,
                    boolean hideHeader) {
        this(rowModel, selectionMode, null, maximumVisibleRows, verticalSizePolicy, null, hideHeader);
    }

    public PGJTable(@NotNull PGJTableModel<T> model,
                    @Nullable SelectionMode selectionMode,
                    double @Nullable [] columnSizeWeights,
                    @Range(from = 0, to = Integer.MAX_VALUE) int maximumVisibleRows,
                    @Nullable VSizePolicy verticalSizePolicy,
                    boolean hideHeader) {
        this(model, selectionMode, columnSizeWeights, maximumVisibleRows, verticalSizePolicy, null, hideHeader);
    }

    public PGJTable(@NotNull PGJTableRowModel<T> rowModel,
                    @Nullable SelectionMode selectionMode,
                    double @Nullable [] columnSizeWeights,
                    @Range(from = 0, to = Integer.MAX_VALUE) int maximumVisibleRows,
                    @Nullable VSizePolicy verticalSizePolicy,
                    boolean hideHeader) {
        this(rowModel, selectionMode, columnSizeWeights, maximumVisibleRows, verticalSizePolicy, null, hideHeader);
    }

    public PGJTable(@NotNull PGJTableRowModel<T> rowModel,
                    @Nullable SelectionMode selectionMode,
                    double @Nullable [] columnSizeWeights,
                    @Range(from = 0, to = Integer.MAX_VALUE) int maximumVisibleRows,
                    @Nullable VSizePolicy verticalSizePolicy,
                    @Nullable Font font,
                    boolean hideHeader) {
        this(new PGJTableModel<>(rowModel), selectionMode, columnSizeWeights, maximumVisibleRows, verticalSizePolicy, font, hideHeader);
    }

    public PGJTable(@NotNull PGJTableModel<T> model,
                    @Nullable SelectionMode selectionMode,
                    double @Nullable [] columnSizeWeights,
                    @Range(from = 0, to = Integer.MAX_VALUE) int maximumVisibleRows,
                    @Nullable VSizePolicy verticalSizePolicy,
                    @Nullable Font font,
                    boolean hideHeader) {

        super(new PGJTableImpl<>(model));
        setDoubleBuffered(true);

        this.hideHeader = hideHeader;

        if(maximumVisibleRows > 0) this.maximumVisibleRows = maximumVisibleRows;
        this.verticalSizePolicy = Objects.requireNonNullElse(verticalSizePolicy, this.verticalSizePolicy);
        this.columnSizeWeights  = Objects.requireNonNullElse(columnSizeWeights, this.columnSizeWeights);

        setFont(aFont = Objects.requireNonNullElseGet(font, () -> getTable().getFont()));
        setSelectionMode(Objects.requireNonNullElseGet(selectionMode, this::getSelectionMode));
        with(getSelectionModel(), m -> m.addListSelectionListener(this::onSelected));

        invokeLater(() -> {
            getModel().addTableModelListener(e -> revalidate());
            getColumnHeader().setVisible(!hideHeader);
            updateColumnPreferredWidths();
            revalidate();
        });
    }

    public void addRowSelectionInterval(int index0, int index1) {
        with(getTable(), table -> table.addRowSelectionInterval(index0, index1));
    }

    public void addTableSelectionListener(@NotNull PGJTableSelectionListener listener) {
        with(listeners, l -> l.add(PGJTableSelectionListener.class, listener));
    }

    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
        with(getTable(), table -> table.changeSelection(rowIndex, columnIndex, toggle, extend));
    }

    public void clearSelection() {
        with(getTable(), JTable::clearSelection);
    }

    public int columnAtPoint(@NotNull Point point) {
        return fromV(getTable(), table -> table.columnAtPoint(point), 0);
    }

    public void columnMarginChanged(ChangeEvent e) {
        with(getTable(), table -> table.columnMarginChanged(e));
    }

    public void columnMoved(TableColumnModelEvent e) {
        with(getTable(), table -> table.columnMoved(e));
    }

    public void columnRemoved(TableColumnModelEvent e) {
        with(getTable(), table -> table.columnRemoved(e));
    }

    public void columnSelectionChanged(ListSelectionEvent e) {
        with(getTable(), table -> table.columnSelectionChanged(e));
    }

    public boolean editCellAt(int row, int column) {
        return fromV(getTable(), table -> table.editCellAt(row, column), false);
    }

    public boolean editCellAt(int row, int column, EventObject e) {
        return fromV(getTable(), table -> table.editCellAt(row, column, e), false);
    }

    public void editingCanceled(ChangeEvent e) {
        with(getTable(), table -> table.editingCanceled(e));
    }

    public void editingStopped(ChangeEvent e) {
        with(getTable(), table -> table.editingStopped(e));
    }

    public void fireTableCellUpdated(int rowIndex, int columnIndex) {
        with(getModel(), o -> o.fireTableCellUpdated(rowIndex, columnIndex));
    }

    public void fireTableDataChanged() {
        with(getModel(), AbstractTableModel::fireTableDataChanged);
    }

    public void fireTableRowDeleted(int rowIndex) {
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        with(getModel(), o -> o.fireTableRowsDeleted(firstRow, lastRow));
    }

    public void forEachColumn(BiConsumer<Integer, TableColumn> biConsumer) {
        forEachColumn(0, Integer.MAX_VALUE, biConsumer);
    }

    public void forEachColumn(int endIndex, BiConsumer<Integer, TableColumn> biConsumer) {
        forEachColumn(0, endIndex, biConsumer);
    }

    public void forEachColumn(int startIndex, int endIndex, BiConsumer<Integer, TableColumn> biConsumer) {
        with2(getTable(), JTable::getColumnModel, model -> {
            int cc    = model.getColumnCount();
            int start = Math.max(0, Math.min(cc, Math.min(startIndex, endIndex)));
            int end   = Math.max(0, Math.min(cc, Math.max(startIndex, endIndex)));
            for(int i = start; i < end; i++) biConsumer.accept(i, model.getColumn(i));
        });
    }

    public int getAutoResizeMode() {
        return fromV(getTable(), JTable::getAutoResizeMode, 0);
    }

    public TableCellEditor getCellEditor() {
        return fromV(getTable(), JTable::getCellEditor, null);
    }

    public TableCellEditor getCellEditor(int row, int column) {
        return fromV(getTable(), table -> table.getCellEditor(row, column), null);
    }

    public @NotNull Rectangle getCellRect(int row, int column, boolean includeSpacing) {
        return fromV(getTable(), table -> table.getCellRect(row, column, includeSpacing), new Rectangle());
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        return fromV(getTable(), table -> table.getCellRenderer(row, column), null);
    }

    public boolean getCellSelectionEnabled() {
        return getTable().getCellSelectionEnabled();
    }

    public TableColumn getColumn(int columnIndex) {
        return fromV(getColumnModel(), m -> m.getColumn(columnIndex), null);
    }

    public TableColumn getColumn(@NotNull Object identifier) {
        return fromV(getTable(), table -> table.getColumn(identifier), null);
    }

    public Class<?> getColumnClass(int column) {
        return fromV(getTable(), table -> table.getColumnClass(column), String.class);
    }

    public int getColumnCount() {
        return fromV(getColumnModel(), TableColumnModel::getColumnCount, 0);
    }

    public TableColumnModel getColumnModel() {
        return fromV(getTable(), JTable::getColumnModel, null);
    }

    public String getColumnName(int column) {
        return fromV(getTable(), table -> table.getColumnName(column), "");
    }

    public boolean getColumnSelectionAllowed() {
        return fromV(getTable(), JTable::getColumnSelectionAllowed, false);
    }

    public double[] getColumnSizeWeights() {
        return Objects.requireNonNullElseGet(columnSizeWeights, () -> new double[0]);
    }

    public PGDataSupplier<T> getDataSupplier() {
        return fromV(getModel(), PGJTableModel::getDataSupplier, null);
    }

    public TableCellEditor getDefaultEditor(Class<?> columnClass) {
        return fromV(getTable(), table -> table.getDefaultEditor(columnClass), null);
    }

    public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
        return fromV(getTable(), table -> table.getDefaultRenderer(columnClass), null);
    }

    public boolean getDragEnabled() {
        return fromV(getTable(), JTable::getDragEnabled, false);
    }

    public int getEditingColumn() {
        return fromV(getTable(), JTable::getEditingColumn, 0);
    }

    public int getEditingRow() {
        return fromV(getTable(), JTable::getEditingRow, 0);
    }

    public Component getEditorComponent() {
        return fromV(getTable(), JTable::getEditorComponent, null);
    }

    public @Override Font getFont() {
        return aFont;
    }

    public Color getGridColor() {
        return fromV(getTable(), JTable::getGridColor, Color.WHITE);
    }

    public final @Range(from = 1, to = Integer.MAX_VALUE) int getMaximumVisibleRows() {
        return maximumVisibleRows;
    }

    public PGJTableModel<T> getModel() {
        return getTable().getModel();
    }

    public Dimension getPreferredScrollableViewportSize() {
        return from(getTable(), JTable::getPreferredScrollableViewportSize, this::getPreferredSize);
    }

    public int getRowCount() {
        return fromV(getTable(), JTable::getRowCount, 0);
    }

    public int getRowHeight() {
        return fromV(getTable(), JTable::getRowHeight, 0);
    }

    public int getRowHeight(int row) {
        return fromV(getTable(), table -> table.getRowHeight(row), 0);
    }

    public int getRowHeightTune() {
        return rowHeightTune;
    }

    public int getRowMargin() {
        return fromV(getTable(), JTable::getRowMargin, 0);
    }

    public PGJTableRowModel<T> getRowModel() {
        return fromV(getModel(), PGJTableModel::getRowModel, null);
    }

    public boolean getRowSelectionAllowed() {
        return getTable().getRowSelectionAllowed();
    }

    public RowSorter<? extends TableModel> getRowSorter() {
        return fromV(getTable(), JTable::getRowSorter, null);
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return fromV(getTable(), table -> table.getScrollableBlockIncrement(visibleRect, orientation, direction), 0);
    }

    public boolean getScrollableTracksViewportHeight() {
        return fromV(getTable(), JTable::getScrollableTracksViewportHeight, false);
    }

    public boolean getScrollableTracksViewportWidth() {
        return fromV(getTable(), JTable::getScrollableTracksViewportWidth, false);
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return fromV(getTable(), table -> table.getScrollableUnitIncrement(visibleRect, orientation, direction), 0);
    }

    public int getSelectedColumn() {
        return fromV(getTable(), JTable::getSelectedColumn, 0);
    }

    public int getSelectedColumnCount() {
        return fromV(getTable(), JTable::getSelectedColumnCount, 0);
    }

    public int[] getSelectedColumns() {
        return fromV(getTable(), JTable::getSelectedColumns, new int[0]);
    }

    public List<T> getSelectedItems() {
        return from(getDataSupplier(), dm -> IntStream.of(getSelectedRows()).mapToObj(dm::get).toList(), ArrayList::new);
    }

    public int getSelectedRow() {
        return fromV(getTable(), JTable::getSelectedRow, 0);
    }

    public int getSelectedRowCount() {
        return fromV(getTable(), JTable::getSelectedRowCount, 0);
    }

    public int[] getSelectedRows() {
        return from(getTable(), JTable::getSelectedRows, () -> new int[0]);
    }

    public Color getSelectionBackground() {
        return from(getTable(), JTable::getSelectionBackground, () -> Objects.requireNonNullElse(UIManager.getColor("Table.selectionBackground"), Color.BLUE));
    }

    public Color getSelectionForeground() {
        return from(getTable(), JTable::getSelectionForeground, () -> Objects.requireNonNullElse(UIManager.getColor("Table.selectionForground"), Color.WHITE));
    }

    public SelectionMode getSelectionMode() {
        return fromV(getSelectionModel(), sm -> SelectionMode.valueOf(sm.getSelectionMode()), SelectionMode.Single);
    }

    public ListSelectionModel getSelectionModel() {
        return fromV(getTable(), JTable::getSelectionModel, null);
    }

    public boolean getShowHorizontalLines() {
        return fromV(getTable(), JTable::getShowHorizontalLines, false);
    }

    public boolean getShowVerticalLines() {
        return fromV(getTable(), JTable::getShowVerticalLines, false);
    }

    public boolean getSurrendersFocusOnKeystroke() {
        return fromV(getTable(), JTable::getSurrendersFocusOnKeystroke, false);
    }

    public JTableHeader getTableHeader() {
        return fromV(getTable(), JTable::getTableHeader, null);
    }

    public @Override Point getToolTipLocation(MouseEvent event) {
        return super.getToolTipLocation(event);
    }

    public @Override String getToolTipText() {
        return super.getToolTipText();
    }

    public @Override String getToolTipText(@NotNull MouseEvent event) {
        return fromV(getTable(), table -> table.getToolTipText(event), null);
    }

    public int getTotalRowHeightTune() {
        return totalRowHeightTune;
    }

    public boolean getUpdateSelectionOnSort() {
        return fromV(getTable(), JTable::getUpdateSelectionOnSort, false);
    }

    public Object getValueAt(int row, int column) {
        return fromV(getTable(), table -> table.getValueAt(row, column), 0);
    }

    public VSizePolicy getVerticalSizePolicy() {
        return Objects.requireNonNullElse(verticalSizePolicy, None);
    }

    public boolean isCellEditable(int row, int column) {
        return fromV(getTable(), table -> table.isCellEditable(row, column), false);
    }

    public boolean isCellSelected(int row, int column) {
        return fromV(getTable(), table -> table.isCellSelected(row, column), false);
    }

    public boolean isColumnSelected(int column) {
        return fromV(getTable(), table -> table.isColumnSelected(column), false);
    }

    public boolean isEditing() {
        return fromV(getTable(), JTable::isEditing, false);
    }

    public boolean isHideHeader() {
        return hideHeader;
    }

    public boolean isRowSelected(int row) {
        return fromV(getTable(), table -> table.isRowSelected(row), false);
    }

    public void moveColumn(int column, int targetColumn) {
        with(getTable(), table -> table.moveColumn(column, targetColumn));
    }

    public void removeEditor() {
        with(getTable(), JTable::removeEditor);
    }

    public void removeRowSelectionInterval(int index0, int index1) {
        with(getTable(), table -> table.removeRowSelectionInterval(index0, index1));
    }

    public void removeTableSelectionListener(@NotNull PGJTableSelectionListener listener) {
        with(listeners, l -> l.remove(PGJTableSelectionListener.class, listener));
    }

    public int rowAtPoint(@NotNull Point point) {
        return fromV(getTable(), table -> table.rowAtPoint(point), 0);
    }

    public void selectAll() {
        with(getTable(), JTable::selectAll);
    }

    public void setAutoResizeMode(int mode) {
        with(getTable(), table -> table.setAutoResizeMode(mode));
    }

    public void setCellEditor(int columnIndex, @NotNull TableCellEditor editor) {
        with(getColumn(columnIndex), c -> c.setCellEditor(editor));
    }

    public void setCellEditor(TableCellEditor anEditor) {
        with(getTable(), table -> table.setCellEditor(anEditor));
    }

    public void setCellRenderer(int columnIndex, @NotNull TableCellRenderer renderer) {
        with(getColumn(columnIndex), c -> c.setCellRenderer(renderer));
    }

    public void setCellSelectionEnabled(boolean cellSelectionEnabled) {
        getTable().setCellSelectionEnabled(cellSelectionEnabled);
    }

    public void setColumnModel(@NotNull TableColumnModel columnModel) {
        with(getTable(), table -> table.setColumnModel(columnModel));
    }

    public void setColumnSelectionAllowed(boolean columnSelectionAllowed) {
        getTable().setColumnSelectionAllowed(columnSelectionAllowed);
    }

    public void setColumnSelectionInterval(int index0, int index1) {
        with(getTable(), table -> table.setColumnSelectionInterval(index0, index1));
    }

    public void setColumnSizeWeights(double @NotNull [] columnSizeWeights) {
        this.columnSizeWeights = columnSizeWeights;
        invokeLater(this::updateColumnPreferredWidths);
    }

    public void setData(@NotNull List<T> aList) {
        setData(aList, false);
    }

    public void setData(@NotNull List<T> aList, boolean copy) {
        setDataSupplier(new PGListDataSupplier<>(aList, copy));
    }

    public void setDataSupplier(PGListDataSupplier<T> dataModel) {
        with(getModel(), o -> o.setDataSupplier(dataModel));
        resizeTable();
    }

    public void setDefaultEditor(Class<?> columnClass, TableCellEditor editor) {
        with(getTable(), table -> table.setDefaultEditor(columnClass, editor));
    }

    public void setDefaultRenderer(Class<?> columnClass, TableCellRenderer renderer) {
        with(getTable(), table -> table.setDefaultRenderer(columnClass, renderer));
    }

    public void setDragEnabled(boolean b) {
        with(getTable(), table -> table.setDragEnabled(b));
    }

    public void setEditingColumn(int aColumn) {
        with(getTable(), table -> table.setEditingColumn(aColumn));
    }

    public void setEditingRow(int aRow) {
        with(getTable(), table -> table.setEditingRow(aRow));
    }

    public @Override void setFont(@NotNull Font font) {
        if(SwingUtilities.isEventDispatchThread()) {
            aFont       = font;
            aHeaderFont = aFont.deriveFont(Font.BOLD);

            with(getTable(), table -> {
                super.setFont(aFont);
                table.setFont(aFont);
                table.setRowHeight(aRowHeight = (getFontHeight(aFont) + 4 + table.getRowMargin()));

                with(table.getTableHeader(), header -> {
                    header.setFont(aHeaderFont);
                    header.setPreferredSize(new Dimension(1, aHeaderRowHeight = (getFontHeight(aHeaderFont) + 4)));
                });

                resizeTable();
            });
        }
        else invokeLater(() -> setFont(font));
    }

    public void setGridColor(@NotNull Color gridColor) {
        with(getTable(), table -> table.setGridColor(gridColor));
    }

    public void setHideHeader(boolean hideHeader) {
        this.hideHeader = hideHeader;
        getColumnHeader().setVisible(!this.hideHeader);
        revalidate();
    }

    public void setMaximumVisibleRows(@Range(from = 1, to = Integer.MAX_VALUE) int rows) {
        maximumVisibleRows = rows;
        resizeTable();
    }

    public void setModel(@NotNull PGJTableModel<T> model) {
        with(getTable(), table -> table.setModel(model));
    }

    public void setPreferredScrollableViewportSize(Dimension size) {
        with(getTable(), table -> table.setPreferredScrollableViewportSize(switch(getVerticalSizePolicy()) {/*@f0*/
            case None     -> size;
            case FitsRows -> new Dimension(size.width, (aRowHeight * Math.min(table.getRowCount(), maximumVisibleRows)));
            case Fixed    -> new Dimension(size.width, (aRowHeight * maximumVisibleRows));
        }));/*@f1*/
        revalidate();
    }

    public void setRowHeight(int rowHeight) {
        with(getTable(), table -> table.setRowHeight(rowHeight));
    }

    public void setRowHeight(int row, int rowHeight) {
        with(getTable(), table -> table.setRowHeight(row, rowHeight));
    }

    public void setRowHeightTune(int rowHeightTune) {
        this.rowHeightTune = rowHeightTune;
        resizeTable();
    }

    public void setRowMargin(int rowMargin) {
        with(getTable(), table -> table.setRowMargin(rowMargin));
    }

    public void setRowModel(@NotNull PGJTableRowModel<T> rowModel) {
        with(getModel(), m -> m.setRowModel(rowModel));
        resizeTable();
    }

    public void setRowSelectionAllowed(boolean rowSelectionAllowed) {
        getTable().setRowSelectionAllowed(rowSelectionAllowed);
    }

    public void setRowSelectionInterval(int index0, int index1) {
        with(getTable(), table -> table.setRowSelectionInterval(index0, index1));
    }

    public void setRowSorter(RowSorter<? extends TableModel> sorter) {
        with(getTable(), table -> table.setRowSorter(sorter));
    }

    public void setSelectionBackground(Color selectionBackground) {
        with(getTable(), table -> table.setSelectionBackground(selectionBackground));
    }

    public void setSelectionForeground(Color selectionForeground) {
        with(getTable(), table -> table.setSelectionForeground(selectionForeground));
    }

    public void setSelectionMode(@NotNull SelectionMode mode) {
        with(getTable(), table -> table.setSelectionMode(mode.getValue()));
    }

    public void setSelectionModel(@NotNull ListSelectionModel selectionModel) {
        with(getTable(), table -> table.setSelectionModel(selectionModel));
    }

    public void setShowGrid(boolean showGrid) {
        with(getTable(), table -> table.setShowGrid(showGrid));
    }

    public void setShowHorizontalLines(boolean showHorizontalLines) {
        with(getTable(), table -> table.setShowHorizontalLines(showHorizontalLines));
    }

    public void setShowVerticalLines(boolean showVerticalLines) {
        with(getTable(), table -> table.setShowVerticalLines(showVerticalLines));
    }

    public @Override void setToolTipText(String text) {
        with(getTable(), table -> table.setToolTipText(text));
    }

    public void setTotalRowHeightTune(int totalRowHeightTune) {
        this.totalRowHeightTune = totalRowHeightTune;
        resizeTable();
    }

    public void setUpdateSelectionOnSort(boolean update) {
        with(getTable(), table -> table.setUpdateSelectionOnSort(update));
    }

    public void setValueAt(Object aValue, int row, int column) {
        with(getTable(), table -> table.setValueAt(aValue, row, column));
    }

    public void setVerticalSizePolicy(VSizePolicy verticalSizePolicy) {
        this.verticalSizePolicy = verticalSizePolicy;
        resizeTable();
    }

    public void sizeColumnsToFit(int resizingColumn) {
        with(getTable(), table -> table.sizeColumnsToFit(resizingColumn));
    }

    private int getFontHeight(@NotNull Font font) {
        return fromV(getFontMetrics(font), m -> (m.getMaxAscent() + m.getMaxDescent()), 0);
    }

    private @SuppressWarnings("unchecked") PGJTableImpl<T> getTable() {
        return (PGJTableImpl<T>)fromV(getViewport(), JViewport::getView, null);
    }

    private void onSelected(ListSelectionEvent event) {
        int[]             idx  = ofNullable(getTable()).map(JTable::getSelectionModel).map(ListSelectionModel::getSelectedIndices).orElseGet(() -> new int[0]);
        PGDataSupplier<T> data = ofNullable(getModel()).map(PGJTableModel::getDataSupplier).orElseGet(PGListDataSupplier::new);
        PGJTableSelectionEvent evt  = new PGJTableSelectionEvent(this, idx, IntStream.of(idx).mapToObj(data::get).toList());
        with(listeners, l -> invokeLater(() -> l.fireEvent(PGJTableSelectionListener.class, evt, PGJTableSelectionListener::onSelection)));
    }

    private void resizeTable() {
        ifDo(getVerticalSizePolicy(), p -> (p != None), p -> setPreferredScrollableViewportSize(getPreferredScrollableViewportSize()));
    }

    private void updateColumnPreferredWidths() {
        ifDo(getColumnSizeWeights(), cs -> (cs.length > 0), cs -> with(getColumnModel(), cm -> {
            int        diff       = (cm.getColumnCount() - Math.min(cs.length, cm.getColumnCount()));
            int        totalWidth = cm.getTotalColumnWidth();
            IntegerRef widthAcc   = new IntegerRef(0);

            forEachColumn(cs.length, (c, column) -> {
                int cw = Math.max(1, (int)(totalWidth * cs[c]));
                column.setPreferredWidth(cw);
                widthAcc.value += cw;
            });
            forEachColumn(cs.length, (c, column) -> column.setPreferredWidth((totalWidth - widthAcc.value) / diff));
            with(getTable(), table -> invokeLater(table::revalidate));
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

        static final class DummyDataSupplier<T> implements PGDataSupplier<T> {

            private final List<T> data = DummyData.getDummyData();

            public DummyDataSupplier()                                                 { }

            public @Override void forEach(@NotNull Consumer<? super T> consumer)       { data.forEach(consumer); }

            public @Override T get(@Range(from = 0, to = Integer.MAX_VALUE) int index) { return data.get(index); }

            public @Override int size()                                                { return data.size(); }

            public @Override @NotNull Stream<T> stream()                               { return data.stream(); }

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
