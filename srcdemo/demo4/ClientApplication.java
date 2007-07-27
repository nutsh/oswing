package demo4;

import java.util.*;

import org.openswing.swing.internationalization.java.EnglishOnlyResourceFactory;
import org.openswing.swing.domains.java.*;
import java.sql.*;
import org.openswing.swing.util.client.*;
import org.openswing.swing.permissions.java.ButtonsAuthorizations;
import org.openswing.swing.internationalization.java.*;


/**
 * <p>Title: OpenSwing Demo</p>
 * <p>Description: Used to start application from main method:
 * it creates a grid frame and a detail frame accessed by double click on the grid.</p>
 * <p>Copyright: Copyright (C) 2006 Mauro Carniel</p>
 * <p> </p>
 * @author Mauro Carniel
 * @version 1.0
 */
public class ClientApplication {


  Connection conn = null;



  public ClientApplication() {

    Hashtable domains = new Hashtable();
    Properties props = new Properties();
    props.setProperty("this text will be translated","This text will be translated");
    props.setProperty("date","Date");
    props.setProperty("combobox","Combobox");
    props.setProperty("opened","Opened");
    props.setProperty("suspended","Suspended");
    props.setProperty("delivered","Delivered");
    props.setProperty("closed","Closed");
    props.setProperty("radio button","Radio Button");
    props.setProperty("stringValue","Text");
    props.setProperty("dateValue","Date");
    props.setProperty("checkValue","CheckBox");
    props.setProperty("radioButtonValue","RadioButton");
    props.setProperty("comboValue","ComboBox");
    props.setProperty("currencyValue","Currency");
    props.setProperty("numericValue","Number");
    props.setProperty("lookupValue","Lookup Code");
    props.setProperty("descrLookupValue","Lookup Description");
    props.setProperty("taValue","Text Area");
    props.setProperty("formattedTextValue","Formatted Text");
    props.setProperty("combo","Combo");

    ButtonsAuthorizations auth = new ButtonsAuthorizations();
    auth.addButtonAuthorization("F1",true,false,true);

    ClientSettings clientSettings = new ClientSettings(
        new EnglishOnlyResourceFactory("�",props,false),
        domains,
        auth
    );
    ClientSettings.VIEW_MANDATORY_SYMBOL = true;
    ClientSettings.FILTER_PANEL_ON_GRID = true;
    ClientSettings.SHOW_SORTING_ORDER = true;

    Domain orderStateDomain = new Domain("ORDERSTATE");
    ComboVO comboVO = null;
    comboVO = new ComboVO(); comboVO.setCode("O"); comboVO.setDescription("opened"); orderStateDomain.addDomainPair(comboVO,comboVO.getDescription());
    comboVO = new ComboVO(); comboVO.setCode("S"); comboVO.setDescription("suspended"); orderStateDomain.addDomainPair(comboVO,comboVO.getDescription());
    comboVO = new ComboVO(); comboVO.setCode("D"); comboVO.setDescription("delivered"); orderStateDomain.addDomainPair(comboVO,comboVO.getDescription());
    comboVO = new ComboVO(); comboVO.setCode("C"); comboVO.setDescription("closed"); orderStateDomain.addDomainPair(comboVO,comboVO.getDescription());
    domains.put(
      orderStateDomain.getDomainId(),
      orderStateDomain
    );


    createConnection();


    new GridFrameController(conn);
  }


  public static void main(String[] argv) {
    new ClientApplication();
  }


  /**
   * Create the database connection (using Hypersonic DB - in memory) and initialize tables...
   */
  private void createConnection() {
    try {
      Class.forName("org.hsqldb.jdbcDriver");
      conn = DriverManager.getConnection("jdbc:hsqldb:mem:"+"a"+Math.random(),"sa","");
      PreparedStatement stmt = null;
      try {
        stmt = conn.prepareStatement("create table DEMO4(TEXT VARCHAR,FORMATTED_TEXT VARCHAR,DECNUM DECIMAL(10,2),CURRNUM DECIMAL(10,2),THEDATE DATE,COMBO VARCHAR,CHECK_BOX CHAR(1),RADIO CHAR(1),CODE VARCHAR,TA VARCHAR,LIST VARCHAR,PRIMARY KEY(TEXT))");
        stmt.execute();
        stmt.close();

        stmt = conn.prepareStatement("create table DEMO4_LOOKUP(CODE VARCHAR,DESCRCODE VARCHAR,PRIMARY KEY(CODE))");
        stmt.execute();

        for(int i=0;i<200;i++) {
          stmt.close();
          stmt = conn.prepareStatement("insert into DEMO4 values('ABC"+getCode(3,i+1)+"',null,"+12+i+0.333+","+1234+i+0.56+",?,'C','Y','Y','A"+i+"','AAAAAA"+i+"','C')");
          stmt.setObject(1,new java.sql.Date(System.currentTimeMillis()+86400000*i));
          stmt.execute();
        }

        for(int i=0;i<200;i++) {
          stmt.close();
          stmt = conn.prepareStatement("insert into DEMO4_LOOKUP values('A"+i+"','ABCDEF"+i+"')");
          stmt.execute();
        }

      }
      catch (SQLException ex1) {
        ex1.printStackTrace();
      }
      finally {
        try {
          stmt.close();
        }
        catch (SQLException ex2) {
        }
      }

      conn.commit();

    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  private String getCode(int len,int num) {
    String code = String.valueOf(num);
    for(int i=code.length();i<len;i++)
      code = "0"+code;
    return code;
  }


}
