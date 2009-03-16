package org.openswing.swing.table.editors.client;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;

import org.openswing.swing.util.client.*;
import org.openswing.swing.table.client.Grids;
import org.openswing.swing.util.java.Consts;


/**
 * <p>Title: OpenSwing Framework</p>
 * <p>Description: Column editor used to edit a check-box type column.</p>
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
public class CheckBoxCellEditor extends AbstractCellEditor implements TableCellEditor {

  /** flag used to store current check-box state */
  private Boolean selected = Boolean.FALSE;

  /** cell content (a check-box is drawed inside it) */
  private CheckLabel label = new CheckLabel();

  /** flag used to set mandatory property of the cell */
  private boolean required;

  /** ItemListener list associated to the check-box */
  private ArrayList itemListenerList = null;

  /** current row index */
  private int row = -1;

  /** current model column index */
  private int column = -1;

  /** table hook */
  private Grids grids = null;

  /** define if null value is alloed (i.e. distinct from Boolean.FALSE value); default value: <code>false</code> */
  private boolean allowNullValue;


  /**
   * Construtor.
   * @param required flag used to set mandatory property of the cell
   * @param itemListenerList ItemListener list associated to the check-box
   */
  public CheckBoxCellEditor(Grids grids,boolean required,ArrayList itemListenerList,boolean allowNullValue) {
    this.grids = grids;
    this.required = required;
    this.itemListenerList = itemListenerList;
    this.allowNullValue = allowNullValue;

    label.setFocusable(true);
    label.addMouseListener(new MouseAdapter() {

      public void mouseClicked(MouseEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            if (!CheckBoxCellEditor.this.grids.getGrid().hasFocus())
              CheckBoxCellEditor.this.grids.getGrid().requestFocus();

            changeSelectedValue();

            CheckBoxCellEditor.this.grids.getGrid().setValueAt(selected,CheckBoxCellEditor.this.grids.getGrid().getSelectedRow(),CheckBoxCellEditor.this.grids.getGrid().convertColumnIndexToView(column));
            CheckBoxCellEditor.this.grids.getGrid().editCellAt(CheckBoxCellEditor.this.grids.getGrid().getSelectedRow(),CheckBoxCellEditor.this.grids.getGrid().convertColumnIndexToView(column));
            for(int i=0;i<CheckBoxCellEditor.this.itemListenerList.size();i++)
              ((ItemListener)CheckBoxCellEditor.this.itemListenerList.get(i)).itemStateChanged(new ItemEvent(new JCheckBox(),column,CheckBoxCellEditor.this,-1));
            CheckBoxCellEditor.this.grids.getGrid().repaint();
          }
        });

      }

    });
  }


  public final boolean stopCellEditing() {
    return(validate());
  }


  /**
   * Perform the validation.
   */
  private final boolean validate() {
    fireEditingStopped();
    return true;
  }


  public final Object getCellEditorValue() {
    return selected;
  }


  /**
   * Prepare the editor for a value.
   */
  private final Component _prepareEditor(Object value) {
//    if (value!=null)
//      selected = value.equals(new Boolean(true));
//    else
//      selected = false;
//    label.repaint();
//    return label;
    if (allowNullValue) {
      selected = (Boolean)value;
    }
    else {
      if (value==null)
        value = Boolean.FALSE;
      selected = (Boolean)value;
    }

    if (column!=-1 &&
        grids.isEnableInReadOnlyMode(column)&&
        grids.getMode()==Consts.READONLY) {
      if (row!=grids.getSelectedRow()) {
        if (selected==null || !selected.booleanValue())
          selected = Boolean.TRUE;
        else
          selected = Boolean.FALSE;
      }
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          grids.setRowSelectionInterval(row,row);
        }
      });
    }

    label.repaint();
//    SwingUtilities.invokeLater(new Runnable() {
//      public void run() {
//        table.editingStopped(null);
//        table.setColumnSelectionInterval(column,column);
//      }
//    });
    return label;
  }


  public final Component getTableCellEditorComponent(JTable table, Object value,
                                               boolean isSelected, int row,
                                               int column) {
    this.row = row;
    this.column = table.convertColumnIndexToModel(column);
//    table.setRowSelectionInterval(row,row);
    label.setPreferredSize(new Dimension(table.getColumnModel().getColumn(column).getWidth(),table.getHeight()));
    label.setOpaque(false);
//    label.setBackground(table.getBackground());
    return _prepareEditor(value);
  }


  public final void changeSelectedValue() {
    if (allowNullValue) {
      if (selected==null)
        selected = Boolean.TRUE;
      else if (Boolean.FALSE.equals(selected))
        selected = null;
      else
        selected = Boolean.FALSE;
    }
    else {
      if (selected!=null && Boolean.TRUE.equals(selected))
        selected = Boolean.FALSE;
      else
        selected = Boolean.TRUE;
    }


    if (grids.getMode()!=Consts.READONLY) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          grids.getGrid().setValueAt(selected,grids.getGrid().getSelectedRow(),grids.getGrid().convertColumnIndexToView(column));
          grids.getGrid().editCellAt(grids.getGrid().getSelectedRow(),grids.getGrid().convertColumnIndexToView(column));
          for(int i=0;i<CheckBoxCellEditor.this.itemListenerList.size();i++)
            ((ItemListener)CheckBoxCellEditor.this.itemListenerList.get(i)).itemStateChanged(new ItemEvent(new JCheckBox(),column,CheckBoxCellEditor.this,-1));
          grids.getGrid().repaint();
        }
      });
    }
  }


  /**
   * <p>Title: OpenSwing Framework</p>
   * <p>Description: Inner class used to draw the check-box into the cell.</p>
   * <p>Copyright: Copyright (C) 2006 Mauro Carniel</p>
   * <p> </p>
   * @author Mauro Carniel
   * @version 1.0
   */
  class CheckLabel extends JPanel {

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (required) {
        g.setColor(ClientSettings.GRID_REQUIRED_CELL_BORDER);
        g.drawRect(0,0,this.getWidth()-1,this.getHeight()-1);
//          g.drawString("*",15,0);
      }
      else {
        g.setColor(Color.gray);
        g.drawRect(0,0,this.getWidth()-1,this.getHeight()-1);
//          g.drawString("*",15,0);
      }
      g.translate((int)this.getWidth()/2-6,this.getHeight()/2-5);
      BasicGraphicsUtils.drawLoweredBezel(g,0,0,12,12,Color.darkGray,Color.black,Color.white,Color.gray);
      if (allowNullValue && selected==null) {
        g.setColor(Color.lightGray);
        g.fillRect(1,1,10,10);
      }
      if (Boolean.TRUE.equals(selected)) {
        g.setColor(Color.black);
        g.drawLine(3,5,5,7);
        g.drawLine(3,6,5,8);
        g.drawLine(3,7,5,9);
        g.drawLine(6,6,9,3);
        g.drawLine(6,7,9,4);
        g.drawLine(6,8,9,5);
      }
    }


    public boolean processKeyBinding(KeyStroke ks, KeyEvent e,
                                        int condition, boolean pressed) {
      if (e.getSource()!=null &&
          (e.getSource() instanceof org.openswing.swing.table.client.Grid ||
           e.getSource().equals(this) )) {
        if (e.getKeyChar()==' ' && pressed)
        try {
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              if (!grids.getGrid().hasFocus())
                grids.getGrid().requestFocus();

                changeSelectedValue();

//                if (!table.hasFocus())
//                  table.requestFocus();
//                grids.getGrid().setValueAt(selected,grids.getGrid().getSelectedRow(),column);
//                grids.getGrid().editCellAt(grids.getGrid().getSelectedRow(),column);
//                for(int i=0;i<CheckBoxCellEditor.this.itemListenerList.size();i++)
//                  ((ItemListener)CheckBoxCellEditor.this.itemListenerList.get(i)).itemStateChanged(new ItemEvent(new JCheckBox(),column,CheckBoxCellEditor.this,-1));
            }
          });
        }
        catch (Exception ex) {
        }
      }
      return true;
    }


  }


  public final void finalize() {
    grids = null;
    itemListenerList.clear();
  }


}
