package org.openswing.swing.table.columns.client;

import org.openswing.swing.domains.java.Domain;
import java.util.ArrayList;
import java.awt.event.ItemListener;
import org.openswing.swing.util.client.ClientSettings;
import org.openswing.swing.items.client.ItemsMapper;
import org.openswing.swing.items.client.ItemsDataLocator;
import org.openswing.swing.message.receive.java.ValueObject;
import javax.swing.DefaultComboBoxModel;
import java.util.Hashtable;
import org.openswing.swing.logger.client.Logger;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;


/**
 * <p>Title: OpenSwing Framework</p>
 * <p>Description: Column of type combo-box: it contains a combo box showing a list of value objects.
 * Its items are retrieved through the combo box controller, that returns a list of value object;
 * for each value object there exists a row in the combo box: v.o. attributes can be mapped as columns in an item.</p>
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
public class ComboVOColumn extends Column {

  /** combo item listeners */
  private ArrayList itemListeners = new ArrayList();

  /** define if in insert mode combo box has no item selected; default value: <code>false</code> i.e. the first item is pre-selected */
  private boolean nullAsDefaultValue = false;

  /** mapping between items v.o. attributes and items container v.o. attributes */
  private ItemsMapper itemsMapper = new ItemsMapper();

  /** items data source */
  private ItemsDataLocator itemsDataLocator = null;

  /** items value object */
  private ValueObject itemsVO = null;

  /** columns associated to lookup grid */
  private Column[] colProperties = new Column[0];

  /** flag used to set visibility on all columns of lookup grid; default "false"  */
  private boolean allColumnVisible = false;

  /** default preferredWidth for all columns of lookup grid; default 100 pixels */
  private int allColumnPreferredWidth = 100;

  /** collection of pairs <v.o. attribute name,Method object, related to the attribute getter method> */
  private Hashtable getters = new Hashtable();


  public ComboVOColumn() { }


  /**
   * @return column type
   */
  public int getColumnType() {
    return TYPE_COMBO_VO;
  }


  /**
   * Add an ItemListener to the combo.
   * @param listener ItemListener to add
   */
  public final void addItemListener(ItemListener listener) {
    itemListeners.add(listener);
  }


  /**
   * Remove an ItemListener from the combo.
   * @param listener ItemListener to remove
   */
  public final void removeItemListener(ItemListener listener) {
    itemListeners.remove(listener);
  }


  /**
   * @return ItemListener objects
   */
  public final ArrayList getItemListeners() {
    return itemListeners;
  }


  /**
   * @return define if in insert mode combo box has no item selected
   */
  public final boolean isNullAsDefaultValue() {
    return nullAsDefaultValue;
  }


  /**
   * Define if in insert mode combo box has no item selected.
   * @param nullAsDefaultValue define if in insert mode combo box has no item selected
   */
  public final void setNullAsDefaultValue(boolean nullAsDefaultValue) {
    this.nullAsDefaultValue = nullAsDefaultValue;
  }



  /**
   * Set column visibility in the combo box grid frame.
   * @param comboAttributeName attribute name that identifies the item column
   * @param visible column visibility state
   */
  public final void setVisibleColumn(String comboAttributeName, boolean visible) {
    try {
      Column infoTemp;
      int visibleIndex = -1;
      int index = -1;
      for (int i = 0; i < colProperties.length; i++) {
        if (colProperties[i].isVisible())
          visibleIndex = i;
        if (colProperties[i].getColumnName().equals(comboAttributeName)) {
          colProperties[i].setColumnVisible(visible);
          colProperties[i].setColumnSelectable(visible);
          index = i;
          break;
        }
      }
      if (visible) {
        if (visibleIndex==-1)
          visibleIndex=0;
        else if ((visibleIndex-1) < colProperties.length)
          visibleIndex++;
        if ( (index > -1) && (index != visibleIndex)) {
          infoTemp = colProperties[index];
          colProperties[index] = colProperties[visibleIndex];
          colProperties[visibleIndex] = infoTemp;
        }
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  /**
   * Add a link from an attribute of the combo box v.o. to an attribute of the combo box container v.o.
   * @param comboAttributeName attribute of the combo box v.o.
   * @param parentAttributeName attribute of the combo box container v.o.
   */
  public final void addCombo2ParentLink(String comboAttributeName,String parentAttributeName) {
    itemsMapper.addItem2ParentLink(comboAttributeName,parentAttributeName);
  }


  /**
   * Add a link from the whole combo box value object to an equivalent inner v.o. included in the container v.o.
   * @param parentAttributeName attribute of the combo box container v.o., related to an inner v.o. having the same type of the combo box v.o.
   */
  public final void addCombo2ParentLink(String parentAttributeName) {
    itemsMapper.addItem2ParentLink("",parentAttributeName);
  }


  /**
   * @return combo box data locator
   */
  public final ItemsDataLocator getComboDataLocator() {
    return itemsDataLocator;
  }


  /**
   * Set combo box data locator.
   * @param comboDataLocator combo box data locator
   */
  public final void setComboDataLocator(ItemsDataLocator comboDataLocator) {
    this.itemsDataLocator = comboDataLocator;
  }



  /**
   * Set value object class name associated to the combo box: this method calls initItemsVO method.
   * @param itemsValeuObjectClassName value object class name associated to the combo box
   */
  public final void setComboValueObjectClassName(String comboValueObjectClassName) {
    initItemsVO(comboValueObjectClassName);
  }


  /**
   * Method called by setComboValueObjectClassName:
   * - it creates an empty combo v.o
   * - it initializes combo column properties.
   * @param itemsValueObjectClassName combo value object class name
   */
  private void initItemsVO(String itemsValueObjectClassName) {
    try {
      this.itemsVO = (ValueObject) Class.forName(itemsValueObjectClassName).getConstructor(new Class[0]).newInstance(new Object[0]);

      Method[] methods = itemsVO.getClass().getMethods();
      int count = 0;
      for(int i=0;i<methods.length;i++) {
        if (methods[i].getName().startsWith("get") &&
            methods[i].getParameterTypes().length==0 &&
            ( methods[i].getReturnType().equals(String.class) ||
              methods[i].getReturnType().equals(java.math.BigDecimal.class) ||
              methods[i].getReturnType().equals(java.util.Date.class) ||
              methods[i].getReturnType().equals(java.sql.Date.class) ||
              methods[i].getReturnType().equals(java.sql.Timestamp.class) ||
              methods[i].getReturnType().equals(Integer.class) ||
              methods[i].getReturnType().equals(Long.class) ||
              methods[i].getReturnType().equals(Double.class) ||
              methods[i].getReturnType().equals(Float.class) ||
              methods[i].getReturnType().equals(Short.class) ||
              methods[i].getReturnType().equals(Boolean.class))
        )
          count++;
      }
      String[] attributeNames = new String[count];
      this.colProperties = new Column[count];
      count = 0;
      Class colType = null;
      for(int i=0;i<methods.length;i++) {
        if (methods[i].getName().startsWith("get") &&
            methods[i].getParameterTypes().length==0 &&
            ( methods[i].getReturnType().equals(String.class) ||
              methods[i].getReturnType().equals(java.math.BigDecimal.class) ||
              methods[i].getReturnType().equals(java.util.Date.class) ||
              methods[i].getReturnType().equals(java.sql.Date.class) ||
              methods[i].getReturnType().equals(java.sql.Timestamp.class) ||
              methods[i].getReturnType().equals(Integer.class) ||
              methods[i].getReturnType().equals(Long.class) ||
              methods[i].getReturnType().equals(Double.class) ||
              methods[i].getReturnType().equals(Float.class) ||
              methods[i].getReturnType().equals(Short.class) ||
              methods[i].getReturnType().equals(Boolean.class))
        ) {
          attributeNames[count] = methods[i].getName().substring(3);
          if (attributeNames[count].length()>1)
            attributeNames[count] = attributeNames[count].substring(0,1).toLowerCase()+attributeNames[count].substring(1);
          colType = methods[i].getReturnType();
          if (colType.equals(String.class))
            colProperties[count] = new TextColumn();
          else if (colType.equals(Integer.class) || colType.equals(Long.class))
            colProperties[count] = new IntegerColumn();
          else if (colType.equals(BigDecimal.class) || colType.equals(Double.class) || colType.equals(Float.class))
            colProperties[count] = new DecimalColumn();
          else if (colType.equals(Boolean.class))
            colProperties[count] = new CheckBoxColumn();
          else if (colType.equals(Date.class))
            colProperties[count] = new DateColumn();
          else if (colType.equals(java.sql.Date.class))
            colProperties[count] = new DateColumn();
          else if (colType.equals(Timestamp.class))
            colProperties[count] = new DateColumn();

          colProperties[count].setColumnName(attributeNames[count]);
          if (colProperties[count].getHeaderColumnName().equals("columnname"))
            colProperties[count].setHeaderColumnName(String.valueOf(attributeNames[count].charAt(0)).toUpperCase()+attributeNames[count].substring(1));
          colProperties[count].setColumnVisible(this.allColumnVisible);
          colProperties[count].setPreferredWidth(this.allColumnPreferredWidth);
          getters.put(
            colProperties[count].getColumnName(),
            methods[i]
          );
          count++;
        }
      }

    }
    catch (Exception ex) {
      ex.printStackTrace();
      this.itemsVO = null;
    }
    catch (Error er) {
      er.printStackTrace();
      this.itemsVO = null;
    }
  }


  /**
   * @return columns visibility
   */
  public final boolean isAllColumnVisible() {
    return this.allColumnVisible;
  }


  /**
   * Set column visibility for the whole columns of the items grid frame.
   * @param visible columns visibility
   */
  public final void setAllColumnVisible(boolean visible) {
    this.allColumnVisible = visible;
    for(int i=0; i<colProperties.length; i++) {
      colProperties[i].setColumnVisible(visible);
      colProperties[i].setColumnSelectable(visible);
    }
  }


  /**
   * @return columns width
   */
  public final int getAllColumnPreferredWidth() {
    return this.allColumnPreferredWidth;
  }


  /**
   * Set columns width for the whole columns of the items grid frame.
   * @param preferredWidth columns width
   */
  public final void setAllColumnPreferredWidth(int preferredWidth) {
    this.allColumnPreferredWidth = preferredWidth;
    for(int i=0; i<colProperties.length; i++)
      colProperties[i].setPreferredWidth(preferredWidth);
  }


  /**
   * Set column width in the items grid frame.
   * @param itemsAttributeName attribute name that identifies the grid column
   * @param preferredWidth column width
   */
  public final void setPreferredWidthColumn(String itemsAttributeName,int preferredWidth) {
    for(int i=0;i<colProperties.length;i++)
      if (colProperties[i].getColumnName().equals(itemsAttributeName)) {
        colProperties[i].setPreferredWidth(preferredWidth);
        return;
      }
    Logger.error(this.getClass().getName(),"setPreferredWidthColumn","The attribute '"+(itemsAttributeName==null?"null":"'"+itemsAttributeName+"'")+"' does not exist.",null);
  }
  public Column[] getColProperties() {
    return colProperties;
  }
  public Hashtable getGetters() {
    return getters;
  }
  public ValueObject getItemsVO() {
    return itemsVO;
  }
  public ItemsMapper getItemsMapper() {
    return itemsMapper;
  }


}