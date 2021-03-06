package demo20;

import javax.swing.*;
import org.openswing.swing.client.*;
import java.awt.*;
import org.openswing.swing.table.columns.client.*;
import org.openswing.swing.lookup.client.LookupController;
import java.sql.*;
import java.awt.event.*;
import org.openswing.swing.table.java.*;
import org.openswing.swing.message.receive.java.Response;
import java.util.Map;
import java.util.ArrayList;
import org.openswing.swing.message.receive.java.VOListResponse;
import java.math.BigDecimal;
import org.openswing.swing.table.client.GridController;
import javax.swing.text.MaskFormatter;


/**
 * <p>Title: OpenSwing Framework</p>
 * <p>Description: Grid Frame</p>
 * <p>Copyright: Copyright (C) 2006 Mauro Carniel</p>
 * <p> </p>
 * @author Mauro Carniel
 * @version 1.0
 */
public class GridFrame extends JFrame {
  GridControl grid = new GridControl();
  JPanel buttonsPanel = new JPanel();
  ReloadButton reloadButton = new ReloadButton();
  DeleteButton deleteButton = new DeleteButton();
  FlowLayout flowLayout1 = new FlowLayout();
  TextColumn colText = new TextColumn();
  DecimalColumn colDecimal = new DecimalColumn();
  CurrencyColumn colCurrency = new CurrencyColumn();
  DateColumn colDate = new DateColumn();
  ComboColumn colCombo = new ComboColumn();
  CodLookupColumn colLookup = new CodLookupColumn();
  TextColumn textColumn1 = new TextColumn();
  CheckBoxColumn colCheck = new CheckBoxColumn();
  InsertButton insertButton = new InsertButton();
  private Connection conn = null;
  EditButton editButton = new EditButton();
  SaveButton saveButton = new SaveButton();
  CopyButton copyButton = new CopyButton();
  ExportButton exportButton = new ExportButton();
  ButtonColumn colButton = new ButtonColumn();
  NavigatorBar navigatorBar1 = new NavigatorBar();
  FormattedTextColumn colFormattedText = new FormattedTextColumn();
  IntegerColumn colInt = new IntegerColumn();


  public GridFrame(Connection conn,GridFrameController controller) {
    super.setDefaultCloseOperation(super.EXIT_ON_CLOSE);
    this.conn = conn;
    try {
      jbInit();
      setSize(750,300);
      grid.setController(controller);
      grid.setGridDataLocator(controller);

      LookupController lookupController = new DemoLookupController(conn);
      colLookup.setLookupController(lookupController);
/*
      // set top grid content, i.e. the first row...
      grid.setTopGridDataLocator(new GridDataLocator() {

        public Response loadData(
            int action,
            int startIndex,
            Map filteredColumns,
            ArrayList currentSortedColumns,
            ArrayList currentSortedVersusColumns,
            Class valueObjectType,
            Map otherGridParams) {
          ArrayList rows = new ArrayList();
          TestVO vo = new TestVO();
          vo.setDateValue(new java.sql.Date(System.currentTimeMillis()));
          vo.setStringValue("This is a locked row");
          rows.add(vo);
          return new VOListResponse(rows,false,rows.size());
        }

      });
*/

      // set bottom grid content, i.e. the last two rows...
      grid.setBottomGridDataLocator(new GridDataLocator() {

        public Response loadData(
            int action,
            int startIndex,
            Map filteredColumns,
            ArrayList currentSortedColumns,
            ArrayList currentSortedVersusColumns,
            Class valueObjectType,
            Map otherGridParams) {
          ArrayList rows = getTotals();
          return new VOListResponse(rows,false,rows.size());
        }

      });

      grid.setBottomGridController(new GridController() {

        /**
         * Method used to define the background color for each cell of the grid.
         * @param rowNumber selected row index
         * @param attributedName attribute name related to the column currently selected
         * @param value object contained in the selected cell
         * @return background color of the selected cell
         */
        public Color getBackgroundColor(int row,String attributedName,Object value) {
          return new Color(220,220,220);
        }

        /**
         * Method used to define the font to use for each cell of the grid.
         * @param rowNumber selected row index
         * @param attributeName attribute name related to the column currently selected
         * @param value object contained in the selected cell
         * @param defaultFont default font currently in used with this column
         * @return font to use for the current cell; null means default font usage; default value: null
         */
        public Font getFont(int row,String attributeName,Object value,Font defaultFont) {
          if (attributeName.equals("currencyValue") || attributeName.equals("numericValue"))
            return new Font(defaultFont.getFontName(),Font.BOLD,defaultFont.getSize());
          else
            return null;
        }

      });


      setVisible(true);

    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * @return calculate totals for numeric and currency colums and return one row having those totals.
   */
  private ArrayList getTotals() {
    ArrayList rows = new ArrayList();

    TestVO vo = new TestVO();
    vo.setDateValue(new java.sql.Date(System.currentTimeMillis()));
    vo.setStringValue("Total currencies");
    BigDecimal tot = new BigDecimal(0);
    BigDecimal tot2 = new BigDecimal(0);
    TestVO testVO = null;
    for(int i=0;i<grid.getVOListTableModel().getRowCount();i++) {
      testVO = (TestVO)grid.getVOListTableModel().getObjectForRow(i);
      if (testVO.getCurrencyValue()!=null)
        tot = tot.add(testVO.getCurrencyValue());
      if (testVO.getNumericValue()!=null)
        tot2 = tot2.add(testVO.getNumericValue());
    }
    vo.setCurrencyValue(tot);
    vo.setNumericValue(tot2);
    rows.add(vo);

    return rows;
  }



  public void reloadData() {
    grid.reloadData();
  }


  private void jbInit() throws Exception {

    grid.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    buttonsPanel.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    grid.setCopyButton(copyButton);
    grid.setDeleteButton(deleteButton);
    grid.setEditButton(editButton);
    grid.setExportButton(exportButton);
    grid.setInsertButton(insertButton);
    grid.setNavBar(navigatorBar1);
    grid.setReloadButton(reloadButton);
    grid.setSaveButton(saveButton);

//    grid.setLockedColumns(2);
/*
    grid.setLockedRowsOnTop(1);
*/
    grid.setLockedRowsOnBottom(1);

    grid.setValueObjectClassName("demo20.TestVO");
    colText.setColumnName("stringValue");
    colText.setColumnSortable(true);
    colText.setEditableOnInsert(true);
    colText.setSortVersus(org.openswing.swing.util.java.Consts.ASC_SORTED);
    colText.setMaxCharacters(5);
    colText.setTrimText(true);
    colText.setUpperCase(true);
    colText.setAdditionalHeaderColumnName("Text Values");
    colText.setAdditionalHeaderColumnSpan(2);
    colText.setColumnFilterable(true);
    colDecimal.setDecimals(2);
    colDecimal.setAdditionalHeaderColumnName("Number Values");
    colDecimal.setAdditionalHeaderColumnSpan(3);
    colDecimal.setColumnDuplicable(true);
    colDecimal.setColumnFilterable(true);
    colDecimal.setColumnName("numericValue");
    colDecimal.setColumnRequired(false);
    colDecimal.setColumnSortable(true);
    colDecimal.setEditableOnEdit(true);
    colDecimal.setEditableOnInsert(true);
    colCurrency.setAdditionalHeaderColumnSpan(0);
    colCurrency.setColumnDuplicable(true);
    colCurrency.setColumnName("currencyValue");
    colCurrency.setColumnRequired(false);
    colCurrency.setDecimals(3);
    colCurrency.setEditableOnEdit(true);
    colCurrency.setEditableOnInsert(true);
    colCurrency.setGrouping(true);
    colDate.setAdditionalHeaderColumnSpan(1);
    colDate.setColumnDuplicable(true);
    colDate.setColumnFilterable(true);
    colDate.setColumnName("dateValue");
    colDate.setColumnRequired(false);
    colDate.setEditableOnEdit(true);
    colDate.setEditableOnInsert(true);
    colCombo.setDomainId("ORDERSTATE");
    colCombo.setAdditionalHeaderColumnSpan(1);
    colCombo.setColumnDuplicable(true);
    colCombo.setColumnName("comboValue");
    colCombo.setColumnRequired(false);
    colCombo.setEditableOnEdit(true);
    colCombo.setEditableOnInsert(true);
    colLookup.setColumnDuplicable(true);
    colLookup.setColumnName("lookupValue");
    colLookup.setEditableOnEdit(true);
    colLookup.setEditableOnInsert(true);
    colLookup.setMaxCharacters(5);
    colLookup.setAdditionalHeaderColumnName("Lookup");
    colLookup.setAdditionalHeaderColumnSpan(2);
    textColumn1.setAdditionalHeaderColumnName("descrLookupValue");
    textColumn1.setColumnDuplicable(true);
    textColumn1.setColumnName("descrLookupValue");
    textColumn1.setColumnRequired(false);
    textColumn1.setPreferredWidth(150);
    colCheck.setAdditionalHeaderColumnSpan(1);
    colCheck.setColumnDuplicable(true);
    colCheck.setColumnName("checkValue");
    colCheck.setColumnRequired(false);
    colCheck.setEditableOnEdit(true);
    colCheck.setEditableOnInsert(true);

    colCheck.setEnableInReadOnlyMode(true);

    insertButton.setText("insertButton1");
    editButton.setText("editButton1");
    saveButton.setText("saveButton1");
    colButton.setAdditionalHeaderColumnSpan(1);
    colButton.setColumnName("button");
    colButton.setHeaderColumnName("button");
    colButton.setPreferredWidth(50);
    colFormattedText.setAdditionalHeaderColumnSpan(0);
    colFormattedText.setColumnFilterable(false);
    colFormattedText.setColumnName("formattedTextValue");
    colFormattedText.setColumnSortable(false);
    colFormattedText.setEditableOnEdit(true);
    colFormattedText.setEditableOnInsert(true);

    MaskFormatter mask = new MaskFormatter("###-##-####");
    mask.setValidCharacters("0123456789");

    colFormattedText.setFormatter(mask);

    colInt.setColumnFilterable(true);
    colInt.setColumnName("intValue");
    colInt.setColumnSortable(true);
    colInt.setEditableOnEdit(true);
    colInt.setEditableOnInsert(true);

    this.getContentPane().add(grid, BorderLayout.CENTER);
    this.getContentPane().add(buttonsPanel, BorderLayout.NORTH);
    buttonsPanel.add(insertButton, null);
    buttonsPanel.add(copyButton, null);
    buttonsPanel.add(editButton, null);
    buttonsPanel.add(reloadButton, null);
    buttonsPanel.add(saveButton, null);
    buttonsPanel.add(exportButton, null);
    buttonsPanel.add(deleteButton, null);
    buttonsPanel.add(navigatorBar1, null);

    grid.getColumnContainer().add(colText, null);
    grid.getColumnContainer().add(colFormattedText, null);
    grid.getColumnContainer().add(colDecimal, null);
    grid.getColumnContainer().add(colInt, null);
    grid.getColumnContainer().add(colCurrency, null);
    grid.getColumnContainer().add(colDate, null);
    grid.getColumnContainer().add(colCombo, null);
    grid.getColumnContainer().add(colButton, null);
    grid.getColumnContainer().add(colLookup, null);
    grid.getColumnContainer().add(textColumn1, null);
    grid.getColumnContainer().add(colCheck, null);

  }
  public GridControl getGrid() {
    return grid;
  }


}

