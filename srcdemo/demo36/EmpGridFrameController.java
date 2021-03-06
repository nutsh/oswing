package demo36;

import org.openswing.swing.table.client.GridController;
import java.util.*;
import org.openswing.swing.message.receive.java.*;
import java.sql.*;
import org.openswing.swing.message.send.java.FilterWhereClause;
import org.openswing.swing.table.java.GridDataLocator;
import org.openswing.swing.mdi.client.MDIFrame;
import java.awt.Color;
import org.openswing.swing.server.QueryUtil;
import org.openswing.swing.message.send.java.GridParams;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.SelectQuery;


/**
 * <p>Title: OpenSwing Framework</p>
 * <p>Description: Grid controller for employees.</p>
 * <p>Copyright: Copyright (C) 2006 Mauro Carniel</p>
 * <p> </p>
 * @author Mauro Carniel
 * @version 1.0
 */
public class EmpGridFrameController extends GridController implements GridDataLocator {

  private EmpGridFrame grid = null;
  private DataContext context = null;


  public EmpGridFrameController(DataContext context) {
    this.context = context;
    grid = new EmpGridFrame(this);
    MDIFrame.add(grid);
  }


  /**
   * Callback method invoked when the user has double clicked on the selected row of the grid.
   * @param rowNumber selected row index
   * @param persistentObject v.o. related to the selected row
   */
  public void doubleClick(int rowNumber,ValueObject persistentObject) {
    EmpVO vo = (EmpVO)persistentObject;
    new EmpDetailFrameController(grid,vo.getEmpCode(),context);
  }


  /**
   * Callback method invoked to load data on the grid.
   * @param action fetching versus: PREVIOUS_BLOCK_ACTION, NEXT_BLOCK_ACTION or LAST_BLOCK_ACTION
   * @param startPos start position of data fetching in result set
   * @param filteredColumns filtered columns
   * @param currentSortedColumns sorted columns
   * @param currentSortedVersusColumns ordering versus of sorted columns
   * @param valueObjectType v.o. type
   * @param otherGridParams other grid parameters
   * @return response from the server: an object of type VOListResponse if data loading was successfully completed, or an ErrorResponse onject if some error occours
   */
  public Response loadData(
      int action,
      int startIndex,
      Map filteredColumns,
      ArrayList currentSortedColumns,
      ArrayList currentSortedVersusColumns,
      Class valueObjectType,
      Map otherGridParams) {
    try {

      SelectQuery select = new SelectQuery(EmpVO.class);
      List list = context.performQuery(select);

      return new VOListResponse(new ArrayList(list),false,list.size());

//      READ WHOLE RESULT-SET...
//      Response res = HibernateUtils.getAllFromQuery(
//        filteredColumns,
//        currentSortedColumns,
//        currentSortedVersusColumns,
//        valueObjectType,
//        baseSQL,
//        new Object[0],
//        new Type[0],
//        "EMPS",
//        sessions,
//        sess
//      );
//      sess.close();
//      return res;
//      END READ WHOLE RESULT-SET...


//    READ A BLOCK OF DATA FROM RESULT-SET...
//      Response res = HibernateUtils.getBlockFromQuery(
//        action,
//        startIndex,
//        50, // block size...
//        filteredColumns,
//        currentSortedColumns,
//        currentSortedVersusColumns,
//        valueObjectType,
//        baseSQL,
//        new Object[0],
//        new Type[0],
//        "EMPS",
//        sessions,
//        sess
//      );
//      sess.close();
//      return res;
//    END READ A BLOCK OF DATA FROM RESULT-SET...
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return new ErrorResponse(ex.getMessage());
    }
  }


  /**
   * Method invoked when the user has clicked on delete button and the grid is in READONLY mode.
   * @param persistentObjects value objects to delete (related to the currently selected rows)
   * @return an ErrorResponse value object in case of errors, VOResponse if the operation is successfully completed
   */
  public Response deleteRecords(ArrayList persistentObjects) throws Exception {
    try {
      context.deleteObjects(persistentObjects);
      context.commitChanges();
      return new VOResponse(new Boolean(true));
    }
    catch (Exception ex) {
      try {
        context.rollbackChanges();
      }
      catch (Exception ex1) {
      }
      ex.printStackTrace();
      return new ErrorResponse(ex.getMessage());
    }
  }


  public DataContext getContext() {
    return context;
  }



}
