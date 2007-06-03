package org.openswing.swing.client;
import java.beans.*;
import java.util.*;

import java.awt.*;
import java.awt.event.KeyListener;
import javax.swing.*;

import java.awt.event.MouseListener;
import org.openswing.swing.table.columns.client.*;

import org.openswing.swing.table.model.client.*;



import org.openswing.swing.table.client.*;
import org.openswing.swing.util.client.*;
import org.openswing.swing.table.java.*;
import org.openswing.swing.util.java.*;
import org.openswing.swing.table.filter.client.FilterPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import org.openswing.swing.table.filter.client.FilterDialog;


/**
 * <p>Title: OpenSwing Framework</p>
 * <p>Description: Grid based on VOListTableModel.</p>
 * <p>Copyright: Copyright (C) 2006 Mauro Carniel</p>
 *
 * <p> This file is part of OpenSwing Framework.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the (LGPL) Lesser General Public
 * License as published by the Free Software Foundation;
 *
 *                GNU LESSER GENERAL PUBLIC LICENSE
 *                 Version 2.1, February 1999
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *       The author may be contacted at:
 *           maurocarniel@tin.it</p>
 *
 * @author Mauro Carniel
 * @version 1.0
 */
public class GridControl extends JPanel {

  /** grid contained in this panel; it will be created only on run-time */
  private Grids table = null;

  /** columns container; used only in design-time */
  private ColumnContainer itsColumnContainer = new ColumnContainer();

  /** scrollpane used to contain the columns container; used only in design-time */
  private JScrollPane designScrollPane = new JScrollPane();

  /** layout used by the columns container */
  private FlowLayout flowLayout1 = new FlowLayout();

  /** layout used by the scrollpane */
  private BorderLayout borderLayout1 = new BorderLayout(0,0);

  /** value object class name (all rows of the grid will have a value object of this type) */
  private String valueObjectClassName = null;

  /** flag used to specify if column reordering is allowed */
  private boolean reorderingAllowed = true;

  /** flag used to specify if column resizing is allowed */
  private boolean resizingAllowed = true;

  /** current grid mode; default value: READONLY */
  private int mode = Consts.READONLY;

  /** grid container */
  private GridController controller = new GridController();

  /* status panel */
  private GridStatusPanel labelPanel = new GridStatusPanel();

  /** panel used to contain the grid and the status panel */
  private JPanel gridStatusPanel = new JPanel();

  /** layout used by panel used to contain the grid and the status panel */
  private BorderLayout borderLayout2 = new BorderLayout(0,0);

  /** flag used to specify if the status panel is visible; default value: "true" */
  private boolean visibleStatusPanel = true;

  /** grid selection mode; default value: SINGLE_SELECTION */
  private int selectionMode = ListSelectionModel.SINGLE_SELECTION;

  /** insert button */
  private InsertButton insertButton = null;

  /** export button */
  private ExportButton exportButton = null;

  /** filter/sort button */
  private FilterButton filterButton = null;

  /** copy button */
  private CopyButton copyButton = null;

  /** edit button */
  private EditButton editButton = null;

  /** reload/cancel button */
  private ReloadButton reloadButton = null;

  /** delete button */
  private DeleteButton deleteButton = null;

  /** save button */
  private SaveButton saveButton = null;

  /** flag used to specify if data loading will be automatically performed when the grid is set to visible; default value: "true". */
  private boolean autoLoadData = true;

  /** identifier (functionId) associated to the container */
  private String functionId = null;

  /** other grid parameters */
  private Map otherGridParams = new HashMap();

  /** grid data locator; can be set to "ClientGridDataLocator" or to "ServerGridDataLocator" */
  private GridDataLocator gridDataLocator = null;

  /** navigation bar (optional) */
  private NavigatorBar navBar = null;

  /** maximum number of sorted columns */
  private int maxSortedColumns = 1;

  /** number of locked columns, i.e. columns anchored to the left side of the grid; default value: 0 */
  private int lockedColumns = 0;

  /** grid identifier (optional); used in drag 'n drop events */
  private String gridId = null;

  /** cache used to temporally store buttons disabilitation policies (until Gris is set to visibile) */
  private ArrayList buttonsNotEnabled = new ArrayList();

  /** generic buttons */
  private ArrayList genericButtons = new ArrayList();

  /** maximum number of rows to insert by pressing "down" key; default value: 1 */
  private int maxNumberOfRowsOnInsert = 1;

  /** list of columns added to grid */
  private ArrayList components = new ArrayList();

  /** row height of the grid control; default value: ClientSettings.CELL_HEIGHT */
  private int rowHeight = ClientSettings.CELL_HEIGHT;

  /** flag used to define if row height can change for each row, according to image height included in a cell of grid; default value: <code>true</code> */
  private boolean rowHeightFixed = true;

  /** flag used to define if grid sorting operation must always invoke loadData method to retrieve a new list of v.o. or the grid must sort the current v.o. list without invoking loadData (only with the whole result set loaded); default value: <code>true</code> */
  private boolean orderWithLoadData = true;

  /** <code>true</code> to automatically show a filter panel when moving mouse at right of the grid; <code>false</code> to do not show it */
  private boolean showFilterPanelOnGrid = ClientSettings.FILTER_PANEL_ON_GRID;

  /** split pane used to split grid component from filter panel (optional) */
  private JSplitPane split = new JSplitPane();

  /** filter panel to show at the right of the grid (optional) */
  private FilterPanel filterPanel = null;


  /**
   * Costructor.
   */
  public GridControl() {
    super();
    try {
      jbInit();
      setVisibleStatusPanel(true);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  protected final void jbInit() throws Exception {
    gridStatusPanel.setLayout(borderLayout2);
    gridStatusPanel.add(designScrollPane,BorderLayout.CENTER);
    this.setLayout(borderLayout1);
    super.add(gridStatusPanel,BorderLayout.CENTER);
    designScrollPane.getViewport().add(itsColumnContainer);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    flowLayout1.setHgap(0);
    flowLayout1.setVgap(0);
    itsColumnContainer.setLayout(flowLayout1);
    java.lang.String stringa = "";
    try {
      labelPanel.setPreferredSize(new Dimension(this.getPreferredSize().width,22));
    }
    catch (Exception ex) {
    }
  }


  /**
   * Method called when the this panel is set to visible: it calls commitColumnContainer method.
   */
  public final void addNotify() {
    super.addNotify();
    commitColumnContainer();
  }


  /**
   * This method is called by addNotify method when this panel is set to visible:
   * it will create the grid and add it to this panel.
   */
  private final void commitColumnContainer() {
    try {
      long time = System.currentTimeMillis();

      if (Beans.isDesignTime())
        return;

      // the method terminates after the first execution...
      if (itsColumnContainer==null)
        return;

      gridStatusPanel.remove(designScrollPane);

      Component[] c = (Component[])components.toArray(new Component[components.size()]);
      Column[] columnProperties = new Column[c.length];
      for(int i=0;i<c.length;i++) {
        columnProperties[i] = (Column) c[i];
      }
      table = new Grids(
          this,
          lockedColumns,
          valueObjectClassName,
          columnProperties,
          controller,
          labelPanel,
          gridDataLocator,
          otherGridParams
      );
      for(int i=0;i<columnProperties.length;i++) {
        columnProperties[i].setTable(table);
      }
      table.setReorderingAllowed(reorderingAllowed);
      table.setResizingAllowed(resizingAllowed);
      table.setSelectionMode(selectionMode);
      table.setMaxSortedColumns(maxSortedColumns);
      if (insertButton!=null)
        table.setInsertButton(insertButton);
      if (exportButton!=null)
        table.setExportButton(exportButton);
      if (filterButton!=null)
        table.setFilterButton(filterButton);
      if (copyButton!=null)
        table.setCopyButton(copyButton);
      if (editButton!=null)
        table.setEditButton(editButton);
      if (reloadButton!=null)
        table.setReloadButton(reloadButton);
      if (deleteButton!=null)
        table.setDeleteButton(deleteButton);
      if (saveButton!=null)
        table.setSaveButton(saveButton);
      if (navBar!=null)
        table.setNavBar(navBar);

      GenericButton button = null;
      for(int i=0;i<genericButtons.size();i++) {
        button = (GenericButton)genericButtons.get(i);
        table.getGenericButtons().add(button);
        button.addDataController(this.table);
      }

      table.setFunctionId(functionId);

      // add buttons disabilitation policies...
      Object[] policy = null;
      for(int i=0;i<buttonsNotEnabled.size();i++) {
        policy = (Object[])buttonsNotEnabled.get(i);
        table.addButtonsNotEnabled((HashSet)policy[0],(GenericButtonController)policy[1]);
      }

      if (gridId!=null)
        table.enableDrag(gridId);

      gridStatusPanel.add(table,BorderLayout.CENTER);
      if (visibleStatusPanel)
        gridStatusPanel.add(labelPanel,BorderLayout.SOUTH);

      if (showFilterPanelOnGrid) {
        filterPanel = new FilterPanel(columnProperties,table);

        split.setOrientation(split.HORIZONTAL_SPLIT);
        split.setDividerSize(1);
        split.add(gridStatusPanel,split.LEFT);
        split.add(filterPanel,split.RIGHT);
        super.add(split, BorderLayout.CENTER);
        split.setDividerLocation(2048);
        table.getGrid().addMouseListener(new MouseAdapter() {

          public void mouseEntered(MouseEvent e) {
            split.setDividerLocation(split.getWidth()-10);
          }

        });
        if (table.getLockedGrid()!=null)
          table.getLockedGrid().addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent e) {
              split.setDividerLocation(split.getWidth()-10);
            }

          });

        filterPanel.addMouseListener(new MouseAdapter() {

          /**
           * Invoked when the mouse enters a component.
           */
          public void mouseEntered(MouseEvent e) {
            if (split.getDividerLocation()<=split.getWidth()-(int)filterPanel.getPreferredSize().getWidth()-20)
              return;

            filterPanel.init();
            if (split.getLastDividerLocation()>0 && split.getLastDividerLocation()<filterPanel.getPreferredSize().getWidth())
              split.setDividerLocation(split.getLastDividerLocation());
            else
              split.setDividerLocation(split.getWidth()-(int)filterPanel.getPreferredSize().getWidth()-20);
          }

          /**
           * Invoked when the mouse exits a component.
           */
          public void mouseExited(MouseEvent e) {
//            System.out.println(e.getX()+" "+e.getY()+" "+filterPanel.getWidth()+" "+filterPanel.getHeight());
            if (e.getX()<=0 || e.getY()<=0 || e.getX()>=filterPanel.getWidth() || e.getY()>=filterPanel.getHeight()) {
              split.setDividerLocation(split.getWidth()-10);
            }
          }

        });

      }

      gridStatusPanel.revalidate();
      gridStatusPanel.repaint();

      itsColumnContainer = null;

      table.getVOListTableModel().setMode(mode);
      table.setMaxNumberOfRowsOnInsert(maxNumberOfRowsOnInsert);

      if (mode==Consts.READONLY && autoLoadData)
        table.reload();

      table.getGrid().requestFocus();

      if (rowHeightFixed) {
        table.getGrid().setRowHeight(rowHeight);
        if (table.getLockedGrid()!=null)
          table.getLockedGrid().setRowHeight(rowHeight);
      }
      else {
        table.getGrid().setRowHeightFixed(false);
        if (table.getLockedGrid()!=null)
          table.getLockedGrid().setRowHeightFixed(false);
      }

      table.getGrid().setOrderWithLoadData(orderWithLoadData);
      if (table.getLockedGrid()!=null)
        table.getLockedGrid().setOrderWithLoadData(orderWithLoadData);

//        split.setDividerLocation((int)split.getPreferredSize().getWidth());


    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

  }


  /**
   * @return column container
   */
  public final ColumnContainer getColumnContainer() {
    return itsColumnContainer;
  }


  /**
   * @return value object class name
   */
  public final String getValueObjectClassName() {
    try {
      AttributeNameEditor.setDesignVOClass(Class.forName(valueObjectClassName));
    }
    catch (Throwable ex) {
    }
    return valueObjectClassName;
  }


  /**
   * Set the value object class name.
   * @param valueObjectClassName value object class name
   */
  public final void setValueObjectClassName(String valueObjectClassName) {
    this.valueObjectClassName = valueObjectClassName;
    try {
      AttributeNameEditor.setDesignVOClass(Class.forName(valueObjectClassName));
    }
    catch (Throwable ex) {
    }
  }


  /**
   * @return allows column reordering
   */
  public final boolean isReorderingAllowed() {
    return reorderingAllowed;
  }


  /**
   * Define if column reordering is allowed.
   * @param reorderingAllowed allows column reordering
   */
  public final void setReorderingAllowed(boolean reorderingAllowed) {
    this.reorderingAllowed = reorderingAllowed;
  }


  /**
   * @return allows column resizing
   */
  public final boolean isResizingAllowed() {
    return resizingAllowed;
  }


  /**
   * Define if column resizing is allowed.
   * @param resizingAllowed allows column resizing
   */
  public final void setResizingAllowed(boolean resizingAllowed) {
    this.resizingAllowed = resizingAllowed;
  }


  /**
   * @return current grid mode
   */
  public final int getMode() {
    if (table==null)
      return mode;
    else
      return table.getMode();
  }


  /**
   * Set grid mode.
   * @param mode grid mode; possibile values: READONLY, INSERT, EDIT
   */
  public final void setMode(int mode) {
    this.mode = mode;
    if (table!=null)
      table.getVOListTableModel().setMode(mode);
  }


  /**
   * @return grid container
   */
  public final GridController getController() {
    return controller;
  }


  /**
   * Set grid container. This method is callable ONLY before the grid is set to visible.
   * @param container grid container
   */
  public final void setController(GridController controller) {
    this.controller = controller;
  }


  /**
   * Set status panel visibility.
   * @param visibleStatusPanel <code>true</code> to show the status panel, <code>false</code> to hide the status panel
   */
  public final void setVisibleStatusPanel(boolean visibleStatusPanel) {
    this.visibleStatusPanel = visibleStatusPanel;
    if (visibleStatusPanel)
      gridStatusPanel.add(labelPanel,BorderLayout.SOUTH);
    else
      gridStatusPanel.remove(labelPanel);
  }


  /**
   * @return status panel visibility
   */
  public final boolean isVisibleStatusPanel() {
    return visibleStatusPanel;
  }


  /**
   * @return grid data model
   */
  public final VOListTableModel getVOListTableModel() {
    if (table!=null)
      return table.getVOListTableModel();
    else
      return null;
  }


  /**
   * @return grid selection mode
   */
  public final int getSelectionMode() {
    return selectionMode;
  }


  /**
   * Set grid selection mode.
   * @param selectionMode grid selection mode
   */
  public final void setSelectionMode(int selectionMode) {
    this.selectionMode = selectionMode;
  }


  /**
   * @return insert button
   */
  public final InsertButton getInsertButton() {
    return insertButton;
  }


  /**
   * @return export button
   */
  public final ExportButton getExportButton() {
    return exportButton;
  }


  /**
   * @return filter button
   */
  public final FilterButton getFilterButton() {
    return filterButton;
  }


  /**
   * @return copy button
   */
  public final CopyButton getCopyButton() {
    return copyButton;
  }


  /**
   * @return edit button
   */
  public final EditButton getEditButton() {
    return editButton;
  }


  /**
   * Set the edit button.
   * @param editButton edit button
   */
  public final void setEditButton(EditButton editButton) {
    this.editButton = editButton;
    if (this.table!=null)
      this.table.setEditButton(editButton);
  }


  /**
   * Set the insert button.
   * @param insertButton insert button
   */
  public final void setInsertButton(InsertButton insertButton) {
    this.insertButton = insertButton;
    if (this.table!=null)
      this.table.setInsertButton(insertButton);
  }


  /**
   * Set the export button.
   * @param insertButton export button
   */
  public final void setExportButton(ExportButton exportButton) {
    this.exportButton = exportButton;
    if (this.table!=null)
      this.table.setExportButton(exportButton);
  }


  /**
   * Set the copy button.
   * @param copyButton insert button
   */
  public final void setCopyButton(CopyButton copyButton) {
    this.copyButton = copyButton;
    if (this.table!=null)
      this.table.setCopyButton(copyButton);
  }


  /**
   * @return reload/cancel button
   */
  public final ReloadButton getReloadButton() {
    return reloadButton;
  }


  /**
   * Set reload/cancel button.
   * @param reloadButton reload/cancel button
   */
  public final void setReloadButton(ReloadButton reloadButton) {
    this.reloadButton = reloadButton;
    if (this.table!=null)
      this.table.setReloadButton(reloadButton);
  }


  /**
   * @return delete button
   */
  public final DeleteButton getDeleteButton() {
    return deleteButton;
  }


  /**
   * Set the delete button.
   * @param deleteButton delete button
   */
  public final void setDeleteButton(DeleteButton deleteButton) {
    this.deleteButton = deleteButton;
    if (this.table!=null)
      this.table.setDeleteButton(deleteButton);
  }


  /**
   * Set the filter button.
   * @param filterButton filter button
   */
  public final void setFilterButton(FilterButton filterButton) {
    this.filterButton = filterButton;
    if (this.table!=null)
      this.table.setFilterButton(filterButton);
  }


  /**
   * @return save button
   */
  public final SaveButton getSaveButton() {
    return saveButton;
  }


  /**
   * Set the save button.
   * @param saveButton save button
   */
  public final void setSaveButton(SaveButton saveButton) {
    this.saveButton = saveButton;
    if (this.table!=null)
      this.table.setSaveButton(saveButton);
  }


  /**
   * Add an optional button.
   * @param button generic button
   */
  public final void addGenericButton(GenericButton button) {
    if (button!=null) {
      if (table==null)
        genericButtons.add(button);
      else {
        this.table.getGenericButtons().add(button);
        button.addDataController(this.table);
      }
    }
  }


  /**
   * Remove an optional button.
   * @param button generic button
   */
  public final void removeGenericButton(GenericButton button) {
    if (button!=null) {
      if (table==null)
        genericButtons.remove(button);
      else
        this.table.getGenericButtons().remove(button);
    }
  }



  /**
   * @return <code>true</code> to automatically load data when the grid is showed, <code>false</code> otherwise
   */
  public final boolean isAutoLoadData() {
    return this.autoLoadData;
  }


  /**
   * Define if grid must be automatically loaded when it is showed.
   * @param autoLoadData <code>true</code> to automatically load data when the grid is showed, <code>false</code> otherwise
   */
  public final void setAutoLoadData(boolean autoLoadData) {
    this.autoLoadData = autoLoadData;
  }


  /**
   * This method can be called ONLY when the grid is already visibile.
   */
  public void reloadData() {
    if (this.table!=null)
      this.table.reload();
  }


  /**
   * Set the functionId identifier, associated to the container
   * @param functionId identifier associated to the container
   */
  public final void setFunctionId(String functionId) {
    this.functionId = functionId;
  }


  /**
   * @return identifier (functionId) associated to the container
   */
  public final String getFunctionId() {
    return functionId;
  }


  /**
   * @return grid data locator; can be set to "ClientGridDataLocator" or to "ServerGridDataLocator"
   */
  public final GridDataLocator getGridDataLocator() {
    return gridDataLocator;
  }


  /**
   * Set the grid data locator.
   * @param gridDataLocator grid data locator; can be set to "ClientGridDataLocator" or to "ServerGridDataLocator"
   */
  public final void setGridDataLocator(GridDataLocator gridDataLocator) {
    this.gridDataLocator = gridDataLocator;
  }


  /**
   * @return navigation bar (optional)
   */
  public final NavigatorBar getNavBar() {
    return navBar;
  }


  /**
   * Set navigation bar.
   * @param navBar navigation bar
   */
  public final void setNavBar(NavigatorBar navBar) {
    this.navBar = navBar;
    if (this.table!=null)
     this.table.setNavBar(navBar);
  }


  /**
   * @return other grid parameters
   */
  public final Map getOtherGridParams() {
    if (this.table!=null)
      return this.table.getOtherGridParams();
    return otherGridParams;
  }


  /**
   * Set other grid parameters.
   * @param otherGridParams other grid parameters
   */
  public final void setOtherGridParams(Map otherGridParams) {
    this.otherGridParams = otherGridParams;
    if (this.table!=null)
      this.table.setOtherGridParams(otherGridParams);
  }


  /**
   * Set maximum number of sorted columns.
   * @param maxSortedColumns maximum number of sorted columns
   */
  public final void setMaxSortedColumns(int maxSortedColumns) {
    this.maxSortedColumns = maxSortedColumns;
  }


  /**
   * Set maximum number of sorted columns.
   * @param maxSortedColumns maximum number of sorted columns
   */
  public final int getMaxSortedColumns() {
    return this.maxSortedColumns;
  }


  /**
   * Clear grid content.
   */
  public final void clearData() {
    if (this.table!=null)
      this.table.clearData();
  }


  /**
   * @return number of locked columns, i.e. columns anchored to the left side of the grid
   */
  public final int getLockedColumns() {
    return lockedColumns;
  }


  /**
   * Set the number of locked columns, i.e. columns anchored to the left side of the grid.
   * @param lockedColumns number of locked columns, i.e. columns anchored to the left side of the grid
   */
  public final void setLockedColumns(int lockedColumns) {
    this.lockedColumns = lockedColumns;
  }


  /**
   * @param rowNumber TableModel row index
   * @param attributeName attribute name that identifies a grid column
   * @return <code>true</code> means that the cell having the specified row and column index is editable, <code>false</code> otherwise
   */
  public final boolean isFieldEditable(int rowNumber,String attributeName) {
    return table.isFieldEditable(rowNumber,attributeName);
  }


  public int[] getSelectedRows() {
    if (table==null)
      return new int[0];
    else
      return table.getSelectedRows();
  }


  public int getSelectedRow() {
    if (table==null)
      return -1;
    else
      return table.getSelectedRow();
  }


  /**
   * Enable drag onto the grid.
   * @param gridId grid identifier
   */
  public final void enableDrag(String gridId) {
    if (table!=null)
      table.enableDrag(gridId);
    else
      this.gridId = gridId;
  }


  /**
   * Define GenericButton objects linked to this that will be disabled (independently from the grid mode)
   * when the specified attribute will be set to the specified value.
   * @param buttons collections GenericButton objects linked to this that have to be disabled
   * @param buttonController interface that defines button disabilitation, according to some custom policy
   */
  public final void addButtonsNotEnabled(HashSet buttons,GenericButtonController buttonController) {
    if (table!=null)
      table.addButtonsNotEnabled(buttons,buttonController);
    else
      buttonsNotEnabled.add(new Object[]{buttons,buttonController});
  }


  /**
   * Method called by GenericButton.setEnabled method to check if the button must be disabled.
   * @param button button whose abilitation must be checked
   * @return <code>true</code> if no policy is defined in the grid for the specified button, <code>false</code> if there exists a disabilitation policy for the specified button (through addButtonsNotEnabledOnState grid method)
   */
  public final boolean isButtonDisabled(GenericButton button) {
    if (table!=null)
      return table.isButtonDisabled(button);
    else
      return false;
  }


  /**
   * Method automatically called by the Grid to check buttons disabilitation.
   */
  public final void resetButtonsState() {
    if (table!=null)
      table.resetButtonsState();
  }


  /**
   * Remove cell editing, if needed.
   */
  public final void transferFocus() {
    try {
      if (table!=null)
        table.transferFocus();
    }
    catch (Exception ex) {
    }
  }


  /**
   * @return maximum number of rows to insert by pressing "down" key; default value: 1
   */
  public final int getMaxNumberOfRowsOnInsert() {
    if (table==null)
      return maxNumberOfRowsOnInsert;
    else
     return table.getMaxNumberOfRowsOnInsert();
  }


  /**
   * Set the maximum number of rows to insert by pressing "down" key; default value: 1.
   * @param maxNumberOfRowsOnInsert maximum number of rows to insert by pressing "down" key
   */
  public final void setMaxNumberOfRowsOnInsert(int maxNumberOfRowsOnInsert) {
    if (table==null)
      this.maxNumberOfRowsOnInsert = maxNumberOfRowsOnInsert;
    else
      table.setMaxNumberOfRowsOnInsert(maxNumberOfRowsOnInsert);
  }


  /**
   * @return collection of pairs < attribute name,new FilterWhereClause[2] > related to filters currently applied to grid
   */
  public final Map getQuickFilterValues() {
    if (table==null)
      return new HashMap();
    return table.getQuickFilterValues();
  }


  /**
   * Method invoked by UI designers (e.g. JDeveloper) that does not correctly use the "containerDelegate" property defined in GridControlBeanInfo
   * @param comp Component to add
   * @param constraint contraint to use for the component to add
   */
  public final void add(Component comp,Object constraint) {
    itsColumnContainer.add(comp,constraint);
  }


  public final void paint(Graphics g) {
    if (Beans.isDesignTime()) {
      // show "itsColumnContainer" children components...
      if (getColumnContainer().getComponents().length>0)
        // JBuilder/NetBeans...
        super.paint(g);
      else {
        // JDeveloper UI designer...
        Component[] comps = itsColumnContainer.getComponents();
        ArrayList tmp = new ArrayList(Arrays.asList(comps));
        tmp.addAll(components);
        g.setColor(getBackground());
        g.fillRect(0,0,getWidth(),getHeight());
        g.setColor(Color.GRAY);
        Column comp = null;
        int width = 0;
        boolean b = false;
        for(int i=0;i<tmp.size();i++) {
          comp = (Column)tmp.get(i);
          b = true;
          if (width<getWidth()) {
            g.setColor(Color.GRAY);
            g.drawRect(width,0,comp.getPreferredSize().width,19);

            g.setColor(Color.WHITE);
            g.drawLine(width+1,1,width+comp.getPreferredSize().width-1,1);
            g.drawLine(width+1,1,width+1,19);
            g.setColor(new Color(100,100,100));
            g.drawLine(width+1,19,width+comp.getPreferredSize().width-1,19);
            g.drawLine(width+comp.getPreferredSize().width-1,1,width+comp.getPreferredSize().width-1,18);
            g.setColor(Color.BLACK);
            g.drawString(comp.getHeaderColumnName(),width+3,15);
            g.setColor(Color.WHITE);
            g.fillRect(width+1,21,comp.getPreferredSize().width-1,18);
          }
          width += comp.getPreferredSize().width;
        }
        if (b) {
          g.drawRect(0,getHeight()-20,getWidth()-1,19);
          g.setColor(new Color(100,100,100));
          g.drawLine(1,getHeight()-19,getWidth()-2,getHeight()-19);
          g.drawLine(1,getHeight()-19,1,getHeight()-1);
          g.setColor(Color.WHITE);
          g.drawLine(1,getHeight()-1,getWidth()-1,getHeight()-1);
          g.drawLine(getWidth()-1,getHeight()-19,getWidth()-1,getHeight()-1);
        }

      }
    }
    else
      super.paint(g);
  }


  public LayoutManager getLayout() {
    return flowLayout1;
  }


  /**
   * @return row height of the grid control
   */
  public final int getRowHeight() {
    return rowHeight;
  }


  /**
   * Set the row height of the grid control.
   * @param rowHeight row height of the grid control
   */
  public final void setRowHeight(int rowHeight) {
    this.rowHeight = rowHeight;
  }


  /**
   * @return define if row height can change for each row, according to image height included in a cell of grid; default value: <code>true</code>
   */
  public final boolean isRowHeightFixed() {
    return rowHeightFixed;
  }


  /**
   * @param rowHeightFixed define if row height can change for each row, according to image height included in a cell of grid; default value: <code>true</code>
   */
  public final void setRowHeightFixed(boolean rowHeightFixed) {
    this.rowHeightFixed = rowHeightFixed;
  }


  /**
   * @return flag used to define if grid sorting operation must always invoke loadData method to retrieve a new list of v.o. or the grid must sort the current v.o. list without invoking loadData (only with the whole result set loaded)
   */
  public final boolean isOrderWithLoadData() {
    return orderWithLoadData;
  }


  /**
   * Used to define if grid sorting operation must always invoke loadData method to retrieve a new list of v.o. or
   * the grid must sort the current v.o. list without invoking loadData (only with the whole result set loaded).
   * @param orderWithLoadData flag used to define if grid sorting operation must always invoke loadData method to retrieve a new list of v.o. or the grid must sort the current v.o. list without invoking loadData (only with the whole result set loaded)
   */
  public final void setOrderWithLoadData(boolean orderWithLoadData) {
    this.orderWithLoadData = orderWithLoadData;
  }


  /**
   * @return <code>true</code> to automatically show a filter panel when moving mouse at right of the grid; <code>false</code> to do not show it
   */
  public final boolean isShowFilterPanelOnGrid() {
    return showFilterPanelOnGrid;
  }


  /**
   * Set a <code>true</code> value to automatically show a filter panel when moving mouse at right of the grid; <code>false</code> to do not show it
   * @param showFilterPanelOnGrid <code>true</code> to automatically show a filter panel when moving mouse at right of the grid; <code>false</code> to do not show it
   */
  public final void setShowFilterPanelOnGrid(boolean showFilterPanelOnGrid) {
    this.showFilterPanelOnGrid = showFilterPanelOnGrid;
  }


  /**
   * <p>Title: OpenSwing Framework</p>
   * <p>Description: Inner class used inside GridControl to add column to the columns container.
   * Moreover, it add the column to components list (used by JDeveloper UI Designer).</p>
   * <p>Copyright: Copyright (C) 2006 Mauro Carniel</p>
   * @version 1.0
   */
  public class ColumnContainer extends JPanel { /** columns container; used only in design-time */

    /**
     * Method invoked by UI designers (e.g. NetBeans) that adds a column via "containerDelegate" property defined in GridControlBeanInfo
     * @param comp Component to add, WITHOUT contraint
     */
    public final Component add(Component comp) {
      super.add(comp,null);
      components.add(comp);
      return comp;
    }


      /**
       * Method invoked by UI designers (e.g. JDeveloper) that does not correctly use the "containerDelegate" property defined in GridControlBeanInfo
       * @param comp Component to add
       * @param constraint contraint to use for the component to add
       */
      public final void add(Component comp,Object constraint) {
        super.add(comp,constraint);
        components.add(comp);
      }

  }


}

