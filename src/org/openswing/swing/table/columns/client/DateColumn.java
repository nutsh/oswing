package org.openswing.swing.table.columns.client;

import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import org.openswing.swing.client.*;
import org.openswing.swing.internationalization.java.*;
import org.openswing.swing.table.client.*;
import org.openswing.swing.table.editors.client.*;
import org.openswing.swing.table.renderers.client.*;
import org.openswing.swing.util.client.*;


/**
 * <p>Title: OpenSwing Framework</p>
 * <p>Description: Column of type date:
 * it contains a date input field with a button to open a calendar.</p>
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
public class DateColumn extends Column {

  /** separator */
  private char separator = '/';

  /** date format; possible values:  Resources.YMD, Resources.DMY, Resources.MDY, Resources.YDM */
  private int dateFormat;

  /** date changed listeners */
  private ArrayList dateListeners = new ArrayList();

  /** maximum allowed date*/
  private Date upperLimit = null;

  /** minimum allowed date*/
  private Date lowerLimit = null;

  /** flag used to show century */
  private boolean showCentury = true;


  public DateColumn() {
    setTextAlignment(SwingConstants.CENTER);
    try {
      dateFormat = ClientSettings.getInstance().getResources().getDateFormat();
    }
    catch (Exception ex) {
    }
 }


  /**
   * @return column type
   */
  public int getColumnType() {
    return TYPE_DATE;
  }


  /**
   * Add a date changed listener.
   */
  public final void addDateChangedListener(DateChangedListener listener) {
    dateListeners.add(listener);
  }


  /**
   * Remove a date changed listener.
   */
  public final void removeDateChangedListener(DateChangedListener listener) {
    dateListeners.remove(listener);
  }


  /**
   * Set date format.
   * @param dateFormat; possible values:  Resources.YMD, Resources.DMY, Resources.MDY, Resources.YDM
   */
  public final void setFormat(int dateFormat) {
    if(dateFormat < 0 || dateFormat > 3)
      dateFormat = Resources.YMD;
    this.dateFormat = dateFormat;
  }


  /**
   * @return date format; possible values:  YMD, DMY, MDY, YDM
   */
  public final int getFormat() {
      return dateFormat;
  }


  /**
   * Set maximum allowed date.
   * @param upperLimit maximum allowed date
   */
  public final void setUpperLimit(Date upperLimit) {
    this.upperLimit = upperLimit;
  }


  /**
   * @return minimum allowed date
   */
  public final Date getUpperLimit() {
    return upperLimit;
  }


  /**
   * Set minimum allowed date.
   * @param lowerLimit minimum allowed date
   */
  public final void setLowerLimit(Date lowerLimit) {
    this.lowerLimit = lowerLimit;
  }


  /**
   * @return minimum allowed date
   */
  public final Date getLowerLimit() {
    return lowerLimit;
  }


  /**
   * Set separator.
   * @param separator separator character
   */
  public final void setSeparator(char separator) {
    if(Character.isLetterOrDigit(separator) || separator == ' ')
      return;
    this.separator = separator;
  }


  /**
   * @return separator
   */
  public final char getSeparator() {
    return separator;
  }


  /**
   * @return show century
   */
  public final boolean isShowCentury() {
    return showCentury;
  }


  /**
   * Used to show century.
   * @param showCentury show century
   */
  public final void setShowCentury(boolean showCentury) {
    this.showCentury = showCentury;
    setFormat(dateFormat);
  }


  public final ArrayList getDateListeners() {
    return dateListeners;
  }


  /**
   * @return TableCellRenderer for this column
   */
  public final TableCellRenderer getCellRenderer(GridController tableContainer,Grids grids) {
    return new DateTableCellRenderer(
      Column.TYPE_DATE,
      tableContainer,
      getTextAlignment(),
      getSeparator(),
      getFormat(),
      getDateListeners(),
      isShowCentury(),
      Resources.HH_MM
    );
  }


  /**
   * @return TableCellEditor for this column
   */
  public final TableCellEditor getCellEditor(GridController tableContainer,Grids grids) {
    return new DateCellEditor(isColumnRequired(),Column.TYPE_DATE);
  }


}
