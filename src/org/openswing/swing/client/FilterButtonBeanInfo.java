package org.openswing.swing.client;

import java.beans.*;

import org.openswing.swing.client.*;

/**
 * <p>Title: OpenSwing Framework</p>
 * <p>Description: </p>
 *
 *
 * @author Mauro Carniel & MC
 * @version 1.0
 */

public class FilterButtonBeanInfo extends SimpleBeanInfo {
  Class beanClass = FilterButton.class;
  String iconColor16x16Filename = "FilterButton16.gif";
  String iconColor32x32Filename = "FilterButton.gif";
  String iconMono16x16Filename = "FilterButton16.gif";
  String iconMono32x32Filename = "FilterButton.gif";

  public FilterButtonBeanInfo() {
  }
  public PropertyDescriptor[] getPropertyDescriptors() {
    PropertyDescriptor[] pds = new PropertyDescriptor[] { };
    return pds;
  }
  public java.awt.Image getIcon(int iconKind) {
    switch (iconKind) {
      case BeanInfo.ICON_COLOR_16x16:
        return iconColor16x16Filename != null ? loadImage(iconColor16x16Filename) : null;
      case BeanInfo.ICON_COLOR_32x32:
        return iconColor32x32Filename != null ? loadImage(iconColor32x32Filename) : null;
      case BeanInfo.ICON_MONO_16x16:
        return iconMono16x16Filename != null ? loadImage(iconMono16x16Filename) : null;
      case BeanInfo.ICON_MONO_32x32:
        return iconMono32x32Filename != null ? loadImage(iconMono32x32Filename) : null;
    }
    return null;
  }
}