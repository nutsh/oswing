package demo18.client;

import javax.swing.*;
import org.openswing.swing.client.*;
import java.awt.*;
import org.openswing.swing.table.columns.client.*;
import org.openswing.swing.lookup.client.LookupController;
import java.sql.*;
import java.awt.event.*;
import org.openswing.swing.table.java.*;
import org.openswing.swing.mdi.client.InternalFrame;
import org.openswing.swing.mdi.client.MDIFrame;
import org.openswing.swing.util.client.ClientSettings;


/**
 * <p>Title: OpenSwing Framework</p>
 * <p>Description: Grid Frame for departments.</p>
 * <p>Copyright: Copyright (C) 2006 Mauro Carniel</p>
 * <p> </p>
 * @author Mauro Carniel
 * @version 1.0
 */
public class DeptGridFrame extends InternalFrame {
  GridControl grid = new GridControl();
  JPanel buttonsPanel = new JPanel();
  ReloadButton reloadButton = new ReloadButton();
  DeleteButton deleteButton = new DeleteButton();
  FlowLayout flowLayout1 = new FlowLayout();
  TextColumn colDeptCode = new TextColumn();
  TextColumn colDescription = new TextColumn();
  InsertButton insertButton = new InsertButton();
  EditButton editButton = new EditButton();
  SaveButton saveButton = new SaveButton();
  CopyButton copyButton = new CopyButton();
  ExportButton exportButton = new ExportButton();
  TextColumn colAddress = new TextColumn();

  private ServerGridDataLocator serverDataLocator = new ServerGridDataLocator();


  public DeptGridFrame(DeptFrameController controller) {
    try {
      jbInit();
      setSize(750,300);
      grid.setController(controller);
      grid.setGridDataLocator(serverDataLocator);
      serverDataLocator.setServerMethodName("loadDepts");

      setTitle(ClientSettings.getInstance().getResources().getResource("departments"));
      MDIFrame.add(this);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  public void reloadData() {
    grid.reloadData();
  }


  private void jbInit() throws Exception {
    buttonsPanel.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    grid.setCopyButton(copyButton);
    grid.setDeleteButton(deleteButton);
    grid.setEditButton(editButton);
    grid.setExportButton(exportButton);
    grid.setMaxNumberOfRowsOnInsert(10);
    grid.setInsertButton(insertButton);
    grid.setReloadButton(reloadButton);
    grid.setSaveButton(saveButton);
    grid.setValueObjectClassName("demo18.java.DeptVO");
    colDeptCode.setColumnFilterable(true);
    colDeptCode.setColumnName("deptCode");
    colDeptCode.setColumnSortable(true);
    colDeptCode.setEditableOnInsert(true);
    colDeptCode.setMaxCharacters(5);
    colDeptCode.setTrimText(true);
    colDeptCode.setUpperCase(true);
    colDescription.setColumnDuplicable(true);
    colDescription.setColumnName("description");
    colDescription.setEditableOnEdit(true);
    colDescription.setEditableOnInsert(true);
    colDescription.setPreferredWidth(200);
    insertButton.setText("insertButton1");
    editButton.setText("editButton1");
    saveButton.setText("saveButton1");
    colAddress.setColumnDuplicable(true);
    colAddress.setColumnName("address");
    colAddress.setColumnRequired(false);
    colAddress.setEditableOnEdit(true);
    colAddress.setEditableOnInsert(true);
    colAddress.setMaxWidth(200);
    colAddress.setPreferredWidth(400);
    this.getContentPane().add(grid, BorderLayout.CENTER);
    this.getContentPane().add(buttonsPanel, BorderLayout.NORTH);
    buttonsPanel.add(insertButton, null);
    buttonsPanel.add(copyButton, null);
    buttonsPanel.add(editButton, null);
    buttonsPanel.add(reloadButton, null);
    buttonsPanel.add(saveButton, null);
    buttonsPanel.add(exportButton, null);
    buttonsPanel.add(deleteButton, null);
    grid.getColumnContainer().add(colDeptCode, null);
    grid.getColumnContainer().add(colDescription, null);
    grid.getColumnContainer().add(colAddress, null);


  }


}

