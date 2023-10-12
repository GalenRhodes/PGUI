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

import com.intellij.uiDesigner.core.GridLayoutManager;
import com.projectgalen.lib.ui.Fonts;
import com.projectgalen.lib.ui.base.NonGUIEditorCustomComponent;
import com.projectgalen.lib.ui.components.table.misc.*;
import com.projectgalen.lib.utils.EventListeners;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.SwingUtilities.invokeLater;

@SuppressWarnings("unused")
public class PGJTable<T> extends JPanel implements NonGUIEditorCustomComponent {
    protected final EventListeners     listeners = new EventListeners();
    protected final DataModel<T>       dataModel;
    protected       JTableImpl<T>      dataTable;
    protected       JScrollPane        scrollPane;
    protected       boolean            heightMatchesRows;
    protected       boolean            hideHeader;
    protected       int                visibleRowCount;
    protected       int                fontSize;
    protected       VSizePolicy        vSizePolicy;
    protected       double @NotNull [] colSizes;

    public PGJTable() {
        this(new DataModel<>(new DummyRowModel<>()), SelectMode.Single, Integer.MAX_VALUE, VSizePolicy.None, false);
    }

    public PGJTable(@NotNull DataModel<T> model, @NotNull SelectMode selectMode, int visibleRowCount, @NotNull VSizePolicy vSizePolicy, boolean hideHeader) {
        this(model, selectMode, new double[0], visibleRowCount, vSizePolicy, hideHeader);
    }

    public PGJTable(@NotNull DataModel<T> model, @NotNull SelectMode selectMode, double @NotNull [] columnSizeWeights, int visibleRowCount, @NotNull VSizePolicy vSizePolicy, boolean hideHeader) {
        this(model, selectMode, columnSizeWeights, 11, visibleRowCount, vSizePolicy, hideHeader);
    }

    public PGJTable(@NotNull DataModel<T> dataModel, @NotNull SelectMode selectMode, int fontSize, int visibleRowCount, @NotNull VSizePolicy vSizePolicy, boolean hideHeader) {
        this(dataModel, selectMode, new double[0], fontSize, visibleRowCount, vSizePolicy, hideHeader);
    }

    public PGJTable(@NotNull DataModel<T> dataModel,
                    @NotNull SelectMode selectMode,
                    double @NotNull [] columnSizeWeights,
                    int fontSize,
                    int visibleRowCount,
                    @NotNull VSizePolicy vSizePolicy,
                    boolean hideHeader) {
        super(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1), true);

        this.dataModel         = dataModel;
        this.hideHeader        = hideHeader;
        this.heightMatchesRows = false;
        this.visibleRowCount   = visibleRowCount;
        this.fontSize          = fontSize;
        this.vSizePolicy       = vSizePolicy;
        this.colSizes          = columnSizeWeights;

        scrollPane = new JScrollPane(dataTable = new JTableImpl<>(this.dataModel), VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dataTable.setSelectionMode(selectMode.getValue());
        dataTable.getSelectionModel().addListSelectionListener(this::onSelected);
        this.dataModel.addTableModelListener(e -> setTableSize());

        add(scrollPane, createConstraint(0, 0, ANCHOR_CENTER, FILL_BOTH, SIZE_POLICY_ALL, SIZE_POLICY_ALL));

        invokeLater(() -> {
            scrollPane.getColumnHeader().setVisible(!this.hideHeader);
            setTableFont(getFont().deriveFont((float)this.fontSize));
            setTableSize();
            invokeLater(this::updateColumnPreferredWidths);
        });
    }

    public void addTableSelectionListener(@NotNull TableSelectionListener listener) {
        listeners.add(TableSelectionListener.class, listener);
    }

    public void clearSelection() {
        dataTable.clearSelection();
    }

    public void fireTableCellUpdated(int rowIndex, int columnIndex) {
        dataModel.fireTableCellUpdated(rowIndex, columnIndex);
    }

    public void fireTableDataChanged() {
        dataModel.fireTableDataChanged();
    }

    public void fireTableRowDeleted(int rowIndex) {
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        dataModel.fireTableRowsDeleted(firstRow, lastRow);
    }

    public double[] getColSizes() {
        return colSizes;
    }

    public TableColumn getColumn(int columnIndex) {
        TableColumnModel columnModel = getColumnModel();
        if(columnIndex < 0 || columnIndex >= columnModel.getColumnCount()) throw new IndexOutOfBoundsException(columnIndex);
        return columnModel.getColumn(columnIndex);
    }

    public int getColumnCount() {
        return getColumnModel().getColumnCount();
    }

    public TableColumnModel getColumnModel() {
        return dataTable.getColumnModel();
    }

    public List<T> getDataList() {
        return dataModel.getDataList();
    }

    public DataModel<T> getDataModel() {
        return dataModel;
    }

    public DataRowModel<T> getDataRowModel() {
        return dataModel.getRowModel();
    }

    public @Override @NotNull Dimension getMaximumSize() {
        return getViewPortSize(super.getMaximumSize());
    }

    public @Override @NotNull Dimension getPreferredSize() {
        return getViewPortSize(super.getPreferredSize());
    }

    public List<T> getSelectedItems() {
        List<T> data = getDataList();
        return IntStream.of(getSelectedRows()).mapToObj(data::get).collect(Collectors.toList());
    }

    public int[] getSelectedRows() {
        return dataModel.getSelectedRows(dataTable.getSelectionModel());
    }

    public int getSelectionMode() {
        return dataTable.getSelectionModel().getSelectionMode();
    }

    public @NotNull JTableImpl<T> getTable() {
        return dataTable;
    }

    public Font getTableFont() {
        return dataTable.getFont();
    }

    public Dimension getTablePreferredScrollableViewportSize() {
        return dataTable.getPreferredScrollableViewportSize();
    }

    public Dimension getTableSize() {
        return dataTable.getSize();
    }

    public int getVisibleRowCount() {
        return visibleRowCount;
    }

    public boolean isHeightMatchesRows() {
        return heightMatchesRows;
    }

    public boolean isHideHeader() {
        return hideHeader;
    }

    public void removeTableSelectionListener(@NotNull TableSelectionListener listener) {
        listeners.remove(TableSelectionListener.class, listener);
    }

    public void setCellEditor(int columnIndex, @NotNull TableCellEditor editor) {
        getColumn(columnIndex).setCellEditor(editor);
    }

    public void setCellRenderer(int columnIndex, @NotNull TableCellRenderer renderer) {
        getColumn(columnIndex).setCellRenderer(renderer);
    }

    public void setColSizes(double @NotNull [] colSizes) {
        this.colSizes = colSizes;
        invokeLater(this::updateColumnPreferredWidths);
    }

    public void setDataList(@NotNull List<T> aList) {
        dataModel.setDataList(aList);
        setTableSize();
        dataTable.revalidate();
    }

    public void setDataRowModel(@NotNull DataRowModel<T> dataRowModel) {
        dataModel.setRowModel(dataRowModel);
    }

    public void setSelectionMode(int mode) {
        dataTable.setSelectionMode(mode);
    }

    public void setTableFont(@NotNull Font font) {
        invokeLater(() -> {
            Font hf = new Font(font.getFamily(), (font.getStyle() | Font.BOLD), font.getSize());
            dataTable.setFont(font);
            dataTable.setRowHeight(getFontHeight(font) + 4);
            dataTable.getTableHeader().setFont(hf);
            dataTable.getTableHeader().setPreferredSize(new Dimension(1, getFontHeight(hf) + 6));
            if(heightMatchesRows) setTableSize();
        });
    }

    public void setTableFontSize(int size) {
        setTableFont(Fonts.changeFontSize(getTableFont(), size));
    }

    public void setTableFontStyle(@MagicConstant(flags = { Font.PLAIN, Font.BOLD, Font.ITALIC }) int style) {
        setTableFont(Fonts.changeFontStyle(getTableFont(), style));
    }

    public void setTableSize() {
        revalidate();
    }

    public void setVisibleRowCount(int rows) {
        visibleRowCount = rows;
        setTableSize();
    }

    private int foo(int rows) {
        return ((((dataTable.getRowHeight() + dataTable.getRowMargin()) * rows)) + scrollPane.getColumnHeader().getHeight());
    }

    private int getFontHeight(@NotNull Font font) {
        FontMetrics m = getFontMetrics(font);
        return (m.getMaxAscent() + m.getMaxDescent());
    }

    private @NotNull Dimension getViewPortSize(@NotNull Dimension defSize) {
        return new Dimension(defSize.width, switch(vSizePolicy) {/*@f0*/
            case None     -> defSize.height;
            case FitsRows -> foo(Math.max(1, Math.min(visibleRowCount, dataTable.getModel().getRowCount())));
            case Fixed    -> foo(Math.max(1, visibleRowCount));
        });/*@f1*/
    }

    private void onSelected(ListSelectionEvent event) {
        int[]               idx  = dataTable.getSelectionModel().getSelectedIndices();
        List<T>             data = dataModel.getDataList();
        TableSelectionEvent evt  = new TableSelectionEvent(this, idx, IntStream.of(idx).mapToObj(data::get).toList());
        invokeLater(() -> listeners.fireEvent(TableSelectionListener.class, evt, TableSelectionListener::onSelection));
    }

    private void updateColumnPreferredWidths() {
        if(colSizes.length > 0) {
            TableColumnModel model        = dataTable.getColumnModel();
            int              realColCount = model.getColumnCount();
            int              workColCount = Math.min(colSizes.length, realColCount);
            int              totalWidth   = model.getTotalColumnWidth();
            int              widthAcc     = 0;

            for(int c = 0; c < workColCount; c++) {
                int cw = Math.max(1, (int)(totalWidth * colSizes[c]));
                model.getColumn(c).setPreferredWidth(cw);
                widthAcc += cw;
            }
            for(int c = workColCount; c < realColCount; c++) model.getColumn(c).setPreferredWidth((totalWidth - widthAcc) / (realColCount - workColCount));
            invokeLater(() -> dataTable.revalidate());
        }
    }

    private static class DummyRowModel<T> extends AbstractDataRowModel<T> {

        public DummyRowModel()                                                                  { super(); }

        public @Override int getColumnAlignment(int columnIndex)                                { return SwingConstants.CENTER; }

        public @Override Class<?> getColumnClass(int columnIndex)                               { return String.class; }

        public @Override int getColumnCount()                                                   { return 1; }

        public @Contract(pure = true) @Override @Nullable String getColumnName(int columnIndex) { return ""; }

        public @Override @Nullable Object getColumnValue(@NotNull T obj, int columnIndex)       { return ""; }
    }
}
