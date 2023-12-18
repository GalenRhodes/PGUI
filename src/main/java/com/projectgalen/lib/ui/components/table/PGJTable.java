package com.projectgalen.lib.ui.components.table;

import com.projectgalen.lib.ui.UI;
import com.projectgalen.lib.ui.base.NonGUIEditorCustomComponent;
import com.projectgalen.lib.ui.components.table.PGJTable.DummyRowModel.DummyDataSupplier;
import com.projectgalen.lib.ui.components.table.PGJTable.DummyRowModel.DummyDataSupplier.DummyData;
import com.projectgalen.lib.ui.components.table.misc.*;
import com.projectgalen.lib.ui.interfaces.PGDataSupplier;
import com.projectgalen.lib.utils.events.EventListeners;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.projectgalen.lib.ui.M.msgs;
import static com.projectgalen.lib.ui.components.table.VSizePolicy.None;
import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static java.util.Optional.ofNullable;
import static javax.swing.SwingUtilities.invokeLater;

@SuppressWarnings("unused")
public class PGJTable<T> extends JScrollPane implements NonGUIEditorCustomComponent {

    private static final int      DEFAULT_ROW_HEIGHT               = 20;
    private static final Class<?> DEFAULT_COLUMN_CLASS             = String.class;
    private static final String   UIKEY_TABLE_FONT                 = "Table.font";
    private static final String   UIKEY_TABLE_SELECTION_BACKGROUND = "Table[Enabled+Selected].textBackground";
    private static final String   UIKEY_TABLE_SELECTION_FOREGROUND = "Table[Enabled+Selected].textForeground";

    private final PGJTableImpl<T>  table;
    private final EventListeners   listeners             = new EventListeners();
    private       PGJTableModel<T> tableModel;
    private       boolean          hideHeader;
    private       int              aRowHeight;
    private       int              aHeaderRowHeight;
    private       int              maximumVisibleRows    = Integer.MAX_VALUE;
    private       double[]         columnSizePercentages = null;
    private       VSizePolicy      verticalSizePolicy    = None;
    private       Font             cellFont;
    private       Font             headerFont;
    private final JTableProxy      tableProxy            = new JTableProxy();

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
                    double @Nullable [] columnSizePercentages,
                    @Range(from = 0, to = Integer.MAX_VALUE) int maximumVisibleRows,
                    @Nullable VSizePolicy verticalSizePolicy,
                    boolean hideHeader) {
        this(model, selectionMode, columnSizePercentages, maximumVisibleRows, verticalSizePolicy, null, hideHeader);
    }

    public PGJTable(@NotNull PGJTableRowModel<T> rowModel,
                    @Nullable SelectionMode selectionMode,
                    double @Nullable [] columnSizePercentages,
                    @Range(from = 0, to = Integer.MAX_VALUE) int maximumVisibleRows,
                    @Nullable VSizePolicy verticalSizePolicy,
                    boolean hideHeader) {
        this(rowModel, selectionMode, columnSizePercentages, maximumVisibleRows, verticalSizePolicy, null, hideHeader);
    }

    public PGJTable(@NotNull PGJTableRowModel<T> rowModel,
                    @Nullable SelectionMode selectionMode,
                    double @Nullable [] columnSizePercentages,
                    @Range(from = 0, to = Integer.MAX_VALUE) int maximumVisibleRows,
                    @Nullable VSizePolicy verticalSizePolicy,
                    @Nullable Font font,
                    boolean hideHeader) {
        this(new PGJTableModel<>(rowModel), selectionMode, columnSizePercentages, maximumVisibleRows, verticalSizePolicy, font, hideHeader);
    }

    public PGJTable(@NotNull PGJTableModel<T> model,
                    @Nullable SelectionMode selectionMode,
                    double @Nullable [] columnSizePercentages,
                    @Range(from = 0, to = Integer.MAX_VALUE) int maximumVisibleRows,
                    @Nullable VSizePolicy verticalSizePolicy,
                    @Nullable Font font,
                    boolean hideHeader) {

        super();

        this.tableModel = model;
        this.table      = new PGJTableImpl<>(this.tableModel);

        setDoubleBuffered(true);
        setViewportView(this.table);

        this.maximumVisibleRows    = ((maximumVisibleRows > 0) ? maximumVisibleRows : this.maximumVisibleRows);
        this.verticalSizePolicy    = requireNonNullElse(verticalSizePolicy, this.verticalSizePolicy);
        this.columnSizePercentages = ofNullable(columnSizePercentages).orElse(this.columnSizePercentages);
        this.cellFont              = requireNonNullElseGet(font, this::getCurrentFont);
        this.hideHeader            = hideHeader;

        setCellFont(requireNonNullElseGet(font, this::getCurrentFont));
        setSelectionMode(requireNonNullElseGet(selectionMode, this::getSelectionMode));
        ofNullable(getSelectionModel()).ifPresent(m -> m.addListSelectionListener(this::onSelected));
        ofNullable(getModel()).ifPresent(m -> m.addTableModelListener(e -> resizeTable()));

        invokeLater(() -> {
            ofNullable(getColumnHeader()).ifPresent(header -> header.setVisible(!this.hideHeader));
            updateColumnPreferredWidths();
            resizeTable();
        });
    }

    public void addRowSelectionInterval(int index0, int index1) {
        ofNullable(getTable()).ifPresent(table -> table.addRowSelectionInterval(index0, index1));
    }

    public void addTableSelectionListener(@NotNull PGJTableSelectionListener listener) {
        ofNullable(listeners).ifPresent(l -> l.add(PGJTableSelectionListener.class, listener));
    }

    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
        ofNullable(getTable()).ifPresent(table -> table.changeSelection(rowIndex, columnIndex, toggle, extend));
    }

    public void clearSelection() {
        ofNullable(getTable()).ifPresent(JTable::clearSelection);
    }

    public void fireTableCellUpdated(int rowIndex, int columnIndex) {
        ofNullable(getModel()).ifPresent(model -> model.fireTableCellUpdated(rowIndex, columnIndex));
    }

    public void fireTableDataChanged() {
        ofNullable(getModel()).ifPresent(AbstractTableModel::fireTableDataChanged);
    }

    public void fireTableRowDeleted(int rowIndex) {
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        ofNullable(getModel()).ifPresent(model -> model.fireTableRowsDeleted(firstRow, lastRow));
    }

    public void forEachColumn(BiConsumer<Integer, TableColumn> biConsumer) {
        forEachColumn(0, Integer.MAX_VALUE, biConsumer);
    }

    public void forEachColumn(int startIndex, BiConsumer<Integer, TableColumn> biConsumer) {
        forEachColumn(startIndex, Integer.MAX_VALUE, biConsumer);
    }

    public void forEachColumn(int startIndex, int endIndex, BiConsumer<Integer, TableColumn> biConsumer) {
        ofNullable(getColumnModel()).ifPresent(columnModel -> {
            int cc    = columnModel.getColumnCount();
            int start = Math.max(0, Math.min(cc, Math.min(startIndex, endIndex)));
            int end   = Math.max(0, Math.min(cc, Math.max(startIndex, endIndex)));
            for(int i = start; i < end; i++) biConsumer.accept(i, columnModel.getColumn(i));
        });
    }

    public int getAutoResizeMode() {
        return ofNullable(getTable()).map(JTable::getAutoResizeMode).orElse(0);
    }

    public TableCellEditor getCellEditor() {
        return ofNullable(getTable()).map(JTable::getCellEditor).orElse(null);
    }

    public TableCellEditor getCellEditor(int row, int column) {
        return ofNullable(getTable()).map(table -> table.getCellEditor(row, column)).orElse(null);
    }

    public @NotNull Rectangle getCellRect(int row, int column, boolean includeSpacing) {
        return ofNullable(getTable()).map(table -> table.getCellRect(row, column, includeSpacing)).orElse(new Rectangle());
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        return ofNullable(getTable()).map(table -> table.getCellRenderer(row, column)).orElse(null);
    }

    public boolean getCellSelectionEnabled() {
        return ofNullable(getTable()).map(JTable::getCellSelectionEnabled).orElse(false);
    }

    public TableColumn getColumn(int columnIndex) {
        return ofNullable(getColumnModel()).map(m -> m.getColumn(columnIndex)).orElse(null);
    }

    public TableColumn getColumn(@NotNull Object identifier) {
        return ofNullable(getTable()).map(table -> table.getColumn(identifier)).orElse(null);
    }

    public Class<?> getColumnClass(int column) {
        JTable table = getTable();
        return ((table != null) ? table.getColumnClass(column) : DEFAULT_COLUMN_CLASS);
    }

    public int getColumnCount() {
        return ofNullable(getColumnModel()).map(TableColumnModel::getColumnCount).orElse(0);
    }

    public TableColumnModel getColumnModel() {
        return ofNullable(getTable()).map(JTable::getColumnModel).orElse(null);
    }

    public String getColumnName(int column) {
        return ofNullable(getTable()).map(table -> table.getColumnName(column)).orElse("");
    }

    public boolean getColumnSelectionAllowed() {
        return ofNullable(getTable()).map(JTable::getColumnSelectionAllowed).orElse(false);
    }

    public double[] getColumnSizePercentages() {
        return requireNonNullElseGet(columnSizePercentages, () -> ofNullable(getRowModel()).map(PGJTableRowModel::getColumnSizePercentages).orElse(UI.EMPTY_DOUBLE_ARRAY));
    }

    public PGDataSupplier<T> getDataSupplier() {
        return ofNullable(getModel()).map(PGJTableModel::getDataSupplier).orElse(null);
    }

    public TableCellEditor getDefaultEditor(Class<?> columnClass) {
        return ofNullable(getTable()).map(table -> table.getDefaultEditor(columnClass)).orElse(null);
    }

    public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
        return ofNullable(getTable()).map(table -> table.getDefaultRenderer(columnClass)).orElse(null);
    }

    public boolean getDragEnabled() {
        return ofNullable(getTable()).map(JTable::getDragEnabled).orElse(false);
    }

    public int getEditingColumn() {
        return ofNullable(getTable()).map(JTable::getEditingColumn).orElse(0);
    }

    public int getEditingRow() {
        return ofNullable(getTable()).map(JTable::getEditingRow).orElse(0);
    }

    public Component getEditorComponent() {
        return ofNullable(getTable()).map(JTable::getEditorComponent).orElse(null);
    }

    public @Override Font getFont() {
        return cellFont;
    }

    public Color getGridColor() {
        return ofNullable(getTable()).map(JTable::getGridColor).orElse(Color.WHITE);
    }

    public final @Range(from = 1, to = Integer.MAX_VALUE) int getMaximumVisibleRows() {
        return maximumVisibleRows;
    }

    public PGJTableModel<T> getModel() {
        return tableModel;
    }

    public Dimension getPreferredScrollableViewportSize() {
        return ofNullable(getTable()).map(JTable::getPreferredScrollableViewportSize).orElse(getPreferredSize());
    }

    public int getRowCount() {
        return ofNullable(getTable()).map(JTable::getRowCount).orElse(0);
    }

    public int getRowHeight() {
        return ofNullable(getTable()).map(JTable::getRowHeight).orElse(0);
    }

    public int getRowHeight(int row) {
        return ofNullable(getTable()).map(table -> table.getRowHeight(row)).orElse(0);
    }

    public int getRowMargin() {
        return ofNullable(getTable()).map(JTable::getRowMargin).orElse(0);
    }

    public PGJTableRowModel<T> getRowModel() {
        return ofNullable(getModel()).map(PGJTableModel::getRowModel).orElse(null);
    }

    public boolean getRowSelectionAllowed() {
        return ofNullable(getTable()).map(JTable::getRowSelectionAllowed).orElse(false);
    }

    public RowSorter<? extends TableModel> getRowSorter() {
        return ofNullable(getTable()).map(JTable::getRowSorter).orElse(null);
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return ofNullable(getTable()).map(table -> table.getScrollableBlockIncrement(visibleRect, orientation, direction)).orElse(0);
    }

    public boolean getScrollableTracksViewportHeight() {
        return ofNullable(getTable()).map(JTable::getScrollableTracksViewportHeight).orElse(false);
    }

    public boolean getScrollableTracksViewportWidth() {
        return ofNullable(getTable()).map(JTable::getScrollableTracksViewportWidth).orElse(false);
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return ofNullable(getTable()).map(table -> table.getScrollableUnitIncrement(visibleRect, orientation, direction)).orElse(0);
    }

    public int getSelectedColumn() {
        return ofNullable(getTable()).map(JTable::getSelectedColumn).orElse(0);
    }

    public int getSelectedColumnCount() {
        return ofNullable(getTable()).map(JTable::getSelectedColumnCount).orElse(0);
    }

    public int[] getSelectedColumns() {
        return ofNullable(getTable()).map(JTable::getSelectedColumns).orElse(UI.EMPTY_INT_ARRAY);
    }

    public List<T> getSelectedItems() {
        return ofNullable(getDataSupplier()).map(dataSupplier -> IntStream.of(getSelectedRows()).mapToObj(dataSupplier::get).toList()).orElseGet(Collections::emptyList);
    }

    public int getSelectedRow() {
        return ofNullable(getTable()).map(JTable::getSelectedRow).orElse(0);
    }

    public int getSelectedRowCount() {
        return ofNullable(getTable()).map(JTable::getSelectedRowCount).orElse(0);
    }

    public int[] getSelectedRows() {
        return ofNullable(getTable()).map(JTable::getSelectedRows).orElse(UI.EMPTY_INT_ARRAY);
    }

    public Color getSelectionBackground() {
        return ofNullable(getTable()).map(JTable::getSelectionBackground).orElse(requireNonNullElse(UIManager.getColor(UIKEY_TABLE_SELECTION_BACKGROUND), Color.BLUE));
    }

    public Color getSelectionForeground() {
        return ofNullable(getTable()).map(JTable::getSelectionForeground).orElse(requireNonNullElse(UIManager.getColor(UIKEY_TABLE_SELECTION_FOREGROUND), Color.WHITE));
    }

    public SelectionMode getSelectionMode() {
        return ofNullable(getSelectionModel()).map(m -> SelectionMode.valueOf(m.getSelectionMode())).orElse(SelectionMode.Single);
    }

    public ListSelectionModel getSelectionModel() {
        return ofNullable(getTable()).map(JTable::getSelectionModel).orElse(null);
    }

    public boolean getShowHorizontalLines() {
        return ofNullable(getTable()).map(JTable::getShowHorizontalLines).orElse(false);
    }

    public boolean getShowVerticalLines() {
        return ofNullable(getTable()).map(JTable::getShowVerticalLines).orElse(false);
    }

    public @NotNull JTableProxy getTableComponent() {
        return ofNullable(tableProxy).orElse(new JTableProxy());
    }

    public JTableHeader getTableHeader() {
        return ofNullable(getTable()).map(JTable::getTableHeader).orElse(null);
    }

    public boolean getUpdateSelectionOnSort() {
        return ofNullable(getTable()).map(JTable::getUpdateSelectionOnSort).orElse(false);
    }

    public Object getValueAt(int row, int column) {
        return ofNullable(getTable()).map(table -> table.getValueAt(row, column)).orElse(0);
    }

    public VSizePolicy getVerticalSizePolicy() {
        return requireNonNullElse(verticalSizePolicy, None);
    }

    public boolean isCellEditable(int row, int column) {
        return ofNullable(getTable()).map(table -> table.isCellEditable(row, column)).orElse(false);
    }

    public boolean isCellSelected(int row, int column) {
        return ofNullable(getTable()).map(table -> table.isCellSelected(row, column)).orElse(false);
    }

    public boolean isColumnSelected(int column) {
        return ofNullable(getTable()).map(table -> table.isColumnSelected(column)).orElse(false);
    }

    public boolean isEditing() {
        return ofNullable(getTable()).map(JTable::isEditing).orElse(false);
    }

    public boolean isHideHeader() {
        return hideHeader;
    }

    public boolean isRowSelected(int row) {
        return ofNullable(getTable()).map(table -> table.isRowSelected(row)).orElse(false);
    }

    public void moveColumn(int column, int targetColumn) {
        ofNullable(getTable()).ifPresent(table -> table.moveColumn(column, targetColumn));
    }

    public void removeEditor() {
        ofNullable(getTable()).ifPresent(JTable::removeEditor);
    }

    public void removeRowSelectionInterval(int index0, int index1) {
        ofNullable(getTable()).ifPresent(table -> table.removeRowSelectionInterval(index0, index1));
    }

    public void removeTableSelectionListener(@NotNull PGJTableSelectionListener listener) {
        ofNullable(listeners).ifPresent(l -> l.remove(PGJTableSelectionListener.class, listener));
    }

    public int rowAtPoint(@NotNull Point point) {
        return ofNullable(getTable()).map(table -> table.rowAtPoint(point)).orElse(0);
    }

    public void selectAll() {
        ofNullable(getTable()).ifPresent(JTable::selectAll);
    }

    public void setAutoResizeMode(int mode) {
        ofNullable(getTable()).ifPresent(table -> table.setAutoResizeMode(mode));
    }

    public void setCellEditor(int columnIndex, @NotNull TableCellEditor editor) {
        ofNullable(getColumn(columnIndex)).ifPresent(column -> column.setCellEditor(editor));
    }

    public void setCellEditor(TableCellEditor anEditor) {
        ofNullable(getTable()).ifPresent(table -> table.setCellEditor(anEditor));
    }

    public void setCellRenderer(int columnIndex, @NotNull TableCellRenderer renderer) {
        ofNullable(getColumn(columnIndex)).ifPresent(column -> column.setCellRenderer(renderer));
    }

    public void setCellSelectionEnabled(boolean cellSelectionEnabled) {
        ofNullable(getTable()).ifPresent(table -> table.setCellSelectionEnabled(cellSelectionEnabled));
    }

    public void setColumnModel(@NotNull TableColumnModel columnModel) {
        ofNullable(getTable()).ifPresent(table -> table.setColumnModel(columnModel));
    }

    public void setColumnSelectionAllowed(boolean columnSelectionAllowed) {
        ofNullable(getTable()).ifPresent(table -> table.setColumnSelectionAllowed(columnSelectionAllowed));
    }

    public void setColumnSelectionInterval(int index0, int index1) {
        ofNullable(getTable()).ifPresent(table -> table.setColumnSelectionInterval(index0, index1));
    }

    public void setColumnSizePercentages(double @NotNull [] columnSizePercentages) {
        this.columnSizePercentages = columnSizePercentages;
        invokeLater(this::updateColumnPreferredWidths);
    }

    public void setData(@NotNull List<T> aList) {
        setData(aList, false);
    }

    public void setData(@NotNull List<T> aList, boolean copy) {
        setDataSupplier(new PGListDataSupplier<>(aList, copy));
    }

    public void setDataSupplier(PGListDataSupplier<T> dataModel) {
        ofNullable(getModel()).ifPresent(model -> model.setDataSupplier(dataModel));
        resizeTable();
    }

    public void setDefaultEditor(Class<?> columnClass, TableCellEditor editor) {
        ofNullable(getTable()).ifPresent(table -> table.setDefaultEditor(columnClass, editor));
    }

    public void setDefaultRenderer(Class<?> columnClass, TableCellRenderer renderer) {
        ofNullable(getTable()).ifPresent(table -> table.setDefaultRenderer(columnClass, renderer));
    }

    public void setDragEnabled(boolean b) {
        ofNullable(getTable()).ifPresent(table -> table.setDragEnabled(b));
    }

    public void setEditingColumn(int aColumn) {
        ofNullable(getTable()).ifPresent(table -> table.setEditingColumn(aColumn));
    }

    public void setEditingRow(int aRow) {
        ofNullable(getTable()).ifPresent(table -> table.setEditingRow(aRow));
    }

    public @Override void setFont(@NotNull Font font) {
        setCellFont(font);
        resizeTable();
    }

    public void setGridColor(@NotNull Color gridColor) {
        ofNullable(getTable()).ifPresent(table -> table.setGridColor(gridColor));
    }

    public void setHideHeader(boolean hideHeader) {
        this.hideHeader = hideHeader;
        ofNullable(getColumnHeader()).ifPresent(header -> header.setVisible(!this.hideHeader));
        revalidate();
    }

    public void setMaximumVisibleRows(@Range(from = 1, to = Integer.MAX_VALUE) int rows) {
        maximumVisibleRows = rows;
        resizeTable();
    }

    public void setModel(@NotNull PGJTableModel<T> model) {
        this.tableModel = model;
        ofNullable(getTable()).ifPresent(t -> t.setModel(model));
    }

    public void setPreferredScrollableViewportSize(Dimension size) {
        ofNullable(getTable()).ifPresent(table -> table.setPreferredScrollableViewportSize(switch(getVerticalSizePolicy()) {/*@f0*/
            case None     -> size;
            case FitsRows -> new Dimension(size.width, (aRowHeight * Math.min(table.getRowCount(), maximumVisibleRows)));
            case Fixed    -> new Dimension(size.width, (aRowHeight * maximumVisibleRows));
        }));/*@f1*/
        revalidate();
    }

    public void setRowHeight(int rowHeight) {
        ofNullable(getTable()).ifPresent(table -> table.setRowHeight(rowHeight));
    }

    public void setRowHeight(int row, int rowHeight) {
        ofNullable(getTable()).ifPresent(table -> table.setRowHeight(row, rowHeight));
    }

    public void setRowMargin(int rowMargin) {
        ofNullable(getTable()).ifPresent(table -> table.setRowMargin(rowMargin));
    }

    public void setRowModel(@NotNull PGJTableRowModel<T> rowModel) {
        ofNullable(getModel()).ifPresent(model -> {
            if(!isDummyRowModel(rowModel) && isDummyRowModel(model.getRowModel())) setData(Collections.emptyList());
            model.setRowModel(rowModel);
            resizeTable();
            updateColumnPreferredWidths();
        });
    }

    private boolean isDummyRowModel(PGJTableRowModel<T> rowModel) {
        return (rowModel instanceof PGJTable.DummyRowModel<T>);
    }

    public void setRowSelectionAllowed(boolean rowSelectionAllowed) {
        ofNullable(getTable()).ifPresent(table -> table.setRowSelectionAllowed(rowSelectionAllowed));
    }

    public void setRowSelectionInterval(int index0, int index1) {
        ofNullable(getTable()).ifPresent(table -> table.setRowSelectionInterval(index0, index1));
    }

    public void setRowSorter(RowSorter<? extends TableModel> sorter) {
        ofNullable(getTable()).ifPresent(table -> table.setRowSorter(sorter));
    }

    public void setSelectionBackground(Color selectionBackground) {
        ofNullable(getTable()).ifPresent(table -> table.setSelectionBackground(selectionBackground));
    }

    public void setSelectionForeground(Color selectionForeground) {
        ofNullable(getTable()).ifPresent(table -> table.setSelectionForeground(selectionForeground));
    }

    public void setSelectionMode(@NotNull SelectionMode mode) {
        ofNullable(getTable()).ifPresent(table -> table.setSelectionMode(mode.getValue()));
    }

    public void setSelectionModel(@NotNull ListSelectionModel selectionModel) {
        ofNullable(getTable()).ifPresent(table -> table.setSelectionModel(selectionModel));
    }

    public void setShowGrid(boolean showGrid) {
        ofNullable(getTable()).ifPresent(table -> table.setShowGrid(showGrid));
    }

    public void setShowHorizontalLines(boolean showHorizontalLines) {
        ofNullable(getTable()).ifPresent(table -> table.setShowHorizontalLines(showHorizontalLines));
    }

    public void setShowVerticalLines(boolean showVerticalLines) {
        ofNullable(getTable()).ifPresent(table -> table.setShowVerticalLines(showVerticalLines));
    }

    public void setUpdateSelectionOnSort(boolean update) {
        ofNullable(getTable()).ifPresent(table -> table.setUpdateSelectionOnSort(update));
    }

    public void setValueAt(Object aValue, int row, int column) {
        ofNullable(getTable()).ifPresent(table -> table.setValueAt(aValue, row, column));
    }

    public void setVerticalSizePolicy(VSizePolicy verticalSizePolicy) {
        this.verticalSizePolicy = verticalSizePolicy;
        resizeTable();
    }

    public void sizeColumnsToFit(int resizingColumn) {
        ofNullable(getTable()).ifPresent(table -> table.sizeColumnsToFit(resizingColumn));
    }

    private @NotNull Font getCurrentFont() {
        return ofNullable(getTable()).map(Component::getFont).orElseGet(PGJTable::getDefaultUIFont);
    }

    private int getFontHeight(@NotNull Font font) {
        return ofNullable(getFontMetrics(font)).map(m -> (m.getMaxAscent() + m.getMaxDescent())).orElse(0);
    }

    private @Nullable PGJTableImpl<T> getTable() {
        return table;
    }

    private void onSelected(@NotNull ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) {
            PGDataSupplier<T>      dataSupplier = getDataSupplier();
            int[]                  rows         = getSelectedRows();
            int[]                  cols         = getSelectedColumns();
            List<T>                items        = IntStream.of(rows).mapToObj(dataSupplier::get).toList();
            PGJTableSelectionEvent event        = new PGJTableSelectionEvent(this, rows, cols, items);
            listeners.fireEvent(PGJTableSelectionListener.class, event, PGJTableSelectionListener::onSelection);
        }
    }

    private void resizeTable() {
        if(getVerticalSizePolicy() != None) setPreferredScrollableViewportSize(getPreferredScrollableViewportSize());
    }

    private void setCellFont(@NotNull Font font) {
        super.setFont(cellFont = font);
        ofNullable(getTable()).ifPresent(table -> {
            table.setFont(cellFont);
            table.setRowHeight(aRowHeight = (getFontHeight(cellFont) + 4 + table.getRowMargin()));
        });
        setHeaderFont(cellFont.deriveFont(Font.BOLD));
    }

    private void setHeaderFont(@NotNull Font font) {
        headerFont = font;
        ofNullable(getTableHeader()).ifPresent(header -> {
            header.setFont(headerFont);
            header.setPreferredSize(new Dimension(1, aHeaderRowHeight = (getFontHeight(headerFont) + 4)));
        });
    }

    private void updateColumnPreferredWidths() {
        double[]         cs = getColumnSizePercentages();
        TableColumnModel cm = getColumnModel();

        if((cm != null) && (cs.length > 0)) {
            int colCount   = cm.getColumnCount();
            int diff       = (colCount - Math.min(cs.length, colCount));
            int totalWidth = cm.getTotalColumnWidth();
            int widthAcc   = 0;

            for(int c = 0, j = Math.min(cs.length, colCount); c < j; c++) {
                int cw = Math.max(1, (int)(totalWidth * cs[c]));
                cm.getColumn(c).setPreferredWidth(cw);
                widthAcc += cw;
            }

            if(cs.length < colCount) for(int c = cs.length; c < colCount; c++) {
                cm.getColumn(c).setPreferredWidth((totalWidth - widthAcc) / diff);
            }

            invokeLater(() -> ofNullable(getTable()).ifPresent(JComponent::revalidate));
        }
    }

    private static @NotNull Font getDefaultUIFont() {
        return requireNonNullElseGet(UIManager.getFont(UIKEY_TABLE_FONT), () -> new Font(Font.SANS_SERIF, Font.PLAIN, 12));
    }

    public final class JTableProxy {

        public void addAncestorListener(AncestorListener listener) {
            ofNullable(getTable()).ifPresent(t -> t.addAncestorListener(listener));
        }

        public void addComponentListener(ComponentListener l) {
            ofNullable(getTable()).ifPresent(t -> t.addComponentListener(l));
        }

        public void addContainerListener(ContainerListener l) {
            ofNullable(getTable()).ifPresent(t -> t.addContainerListener(l));
        }

        public void addFocusListener(FocusListener l) {
            ofNullable(getTable()).ifPresent(t -> t.addFocusListener(l));
        }

        public void addHierarchyBoundsListener(HierarchyBoundsListener l) {
            ofNullable(getTable()).ifPresent(t -> t.addHierarchyBoundsListener(l));
        }

        public void addHierarchyListener(HierarchyListener l) {
            ofNullable(getTable()).ifPresent(t -> t.addHierarchyListener(l));
        }

        public void addInputMethodListener(InputMethodListener l) {
            ofNullable(getTable()).ifPresent(t -> t.addInputMethodListener(l));
        }

        public void addKeyListener(KeyListener l) {
            ofNullable(getTable()).ifPresent(t -> t.addKeyListener(l));
        }

        public void addMouseListener(MouseListener l) {
            ofNullable(getTable()).ifPresent(t -> t.addMouseListener(l));
        }

        public void addMouseMotionListener(MouseMotionListener l) {
            ofNullable(getTable()).ifPresent(t -> t.addMouseMotionListener(l));
        }

        public void addMouseWheelListener(MouseWheelListener l) {
            ofNullable(getTable()).ifPresent(t -> t.addMouseWheelListener(l));
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            ofNullable(getTable()).ifPresent(t -> t.addPropertyChangeListener(listener));
        }

        public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            ofNullable(getTable()).ifPresent(t -> t.addPropertyChangeListener(propertyName, listener));
        }

        public void addVetoableChangeListener(VetoableChangeListener listener) {
            ofNullable(getTable()).ifPresent(t -> t.addVetoableChangeListener(listener));
        }

        public boolean contains(Point p) {
            return ofNullable(getTable()).map(t -> t.contains(p)).orElse(false);
        }

        public void enableInputMethods(boolean enable) {
            ofNullable(getTable()).ifPresent(t -> t.enableInputMethods(enable));
        }

        public ActionListener getActionForKeyStroke(KeyStroke aKeyStroke) {
            return ofNullable(getTable()).map(t -> t.getActionForKeyStroke(aKeyStroke)).orElse(null);
        }

        public int getConditionForKeyStroke(KeyStroke aKeyStroke) {
            return ofNullable(getTable()).map(t -> t.getConditionForKeyStroke(aKeyStroke)).orElse(0);
        }

        public Point getMousePosition(boolean allowChildren) throws HeadlessException {
            return ofNullable(getTable()).map(t -> t.getMousePosition(allowChildren)).orElse(null);
        }

        public Point getMousePosition() throws HeadlessException {
            return ofNullable(getTable()).map(Component::getMousePosition).orElse(null);
        }

        public Point getPopupLocation(MouseEvent event) {
            return ofNullable(getTable()).map(t -> t.getPopupLocation(event)).orElse(null);
        }

        public KeyStroke[] getRegisteredKeyStrokes() {
            return ofNullable(getTable()).map(JComponent::getRegisteredKeyStrokes).orElseGet(() -> new KeyStroke[0]);
        }

        public boolean getSurrendersFocusOnKeystroke() {
            return ofNullable(getTable()).map(JTable::getSurrendersFocusOnKeystroke).orElse(false);
        }

        public Point getToolTipLocation(MouseEvent event) {
            return ofNullable(getTable()).map(t -> t.getToolTipLocation(event)).orElseGet(Point::new);
        }

        public String getToolTipText() {
            return ofNullable(getTable()).map(JComponent::getToolTipText).orElse(null);
        }

        public String getToolTipText(@NotNull MouseEvent event) {
            return ofNullable(getTable()).map(table -> table.getToolTipText(event)).orElse(null);
        }

        public void removeAncestorListener(AncestorListener listener) {
            ofNullable(getTable()).ifPresent(t -> t.removeAncestorListener(listener));
        }

        public void removeComponentListener(ComponentListener l) {
            ofNullable(getTable()).ifPresent(t -> t.removeComponentListener(l));
        }

        public void removeContainerListener(ContainerListener l) {
            ofNullable(getTable()).ifPresent(t -> t.removeContainerListener(l));
        }

        public void removeFocusListener(FocusListener l) {
            ofNullable(getTable()).ifPresent(t -> t.removeFocusListener(l));
        }

        public void removeHierarchyBoundsListener(HierarchyBoundsListener l) {
            ofNullable(getTable()).ifPresent(t -> t.removeHierarchyBoundsListener(l));
        }

        public void removeHierarchyListener(HierarchyListener l) {
            ofNullable(getTable()).ifPresent(t -> t.removeHierarchyListener(l));
        }

        public void removeInputMethodListener(InputMethodListener l) {
            ofNullable(getTable()).ifPresent(t -> t.removeInputMethodListener(l));
        }

        public void removeKeyListener(KeyListener l) {
            ofNullable(getTable()).ifPresent(t -> t.removeKeyListener(l));
        }

        public void removeMouseListener(MouseListener l) {
            ofNullable(getTable()).ifPresent(t -> t.removeMouseListener(l));
        }

        public void removeMouseMotionListener(MouseMotionListener l) {
            ofNullable(getTable()).ifPresent(t -> t.removeMouseMotionListener(l));
        }

        public void removeMouseWheelListener(MouseWheelListener l) {
            ofNullable(getTable()).ifPresent(t -> t.removeMouseWheelListener(l));
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            ofNullable(getTable()).ifPresent(t -> t.removePropertyChangeListener(listener));
        }

        public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            ofNullable(getTable()).ifPresent(t -> t.removePropertyChangeListener(propertyName, listener));
        }

        public void removeVetoableChangeListener(VetoableChangeListener listener) {
            ofNullable(getTable()).ifPresent(t -> t.removeVetoableChangeListener(listener));
        }

        public void resetKeyboardActions() {
            ofNullable(getTable()).ifPresent(JComponent::resetKeyboardActions);
        }

        public void setToolTipText(String text) {
            ofNullable(getTable()).ifPresent(table -> table.setToolTipText(text));
        }
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

    private static final class PGJTableImpl<T> extends JTable {

        public static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);

        public PGJTableImpl(@Nullable PGJTableModel<T> tableModel) {
            super(tableModel);
            setDoubleBuffered(true);
            setDefaultRenderer(BigDecimal.class, new CurrencyCellRenderer());
            setDefaultEditor(BigDecimal.class, new CurrencyCellEditor());
            setFillsViewportHeight(true);
            setCellSelectionEnabled(true);
            setColumnSelectionAllowed(true);
            setRowSelectionAllowed(true);
            getTableHeader().setReorderingAllowed(false);
        }

        @SuppressWarnings("unchecked") public @Override @Nullable PGJTableModel<T> getModel() {
            return (PGJTableModel<T>)super.getModel();
        }

        public @Override Component prepareEditor(TableCellEditor editor, int row, int column) {
            Component c = super.prepareEditor(editor, row, column);
            c.setFont(getFont());
            return c;
        }

        public @NotNull Component prepareRenderer(@NotNull PGJTableModel<T> model, @NotNull Component renderer, int row, int column, boolean isSelected) {
            renderer.setBackground(isSelected ? getSelectionBackground() : getBackground());
            renderer.setForeground(isSelected ? getSelectionForeground() : getForeground());
            if(renderer instanceof JLabel l) l.setHorizontalAlignment(model.getRowModel().getColumnAlignment(column));
            if(renderer instanceof JComponent c) c.setBorder(EMPTY_BORDER);
            return model.setRowAttributes(renderer, this, row, column, isSelected);
        }

        public @Override Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            PGJTableModel<T> model = getModel();
            return ((model != null) ? prepareRenderer(model, super.prepareRenderer(renderer, row, column), row, column, isCellSelected(row, column)) : super.prepareRenderer(renderer, row, column));
        }

        public @Override void setModel(@NotNull TableModel dataModel) {
            if(dataModel instanceof PGJTableModel<?>) super.setModel(dataModel);
            else throw new IllegalArgumentException(msgs.getString("msg.err.pgjtableimpl.invalid_instance_of_model"));
        }

        public void setModel(@NotNull PGJTableModel<T> model) {
            super.setModel(model);
        }
    }
}
