/* Copyright 2002-2004 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the 
   Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
   Boston, MA 02111-1307  USA
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.samples;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;


/**
 * <p>
 * Demonstrates building a structured XML document,
 * from relational data using JDBC. A different version of this 
 * example was originally developed for Example 4.14 of Chapter 4 of 
 * <cite><a target="_top"
 * href="http://www.cafeconleche.org/books/xmljava/">Processing 
 * XML with Java</a></cite>.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class SQLToXML {
        
  public static void main(String[] args ) {
        
    // Load the ODBC driver
    try {
      Class.forName( "sun.jdbc.odbc.JdbcOdbcDriver" );
    }
    catch (ClassNotFoundException ex) {
      System.err.println("Could not load the JDBC-ODBC Bridge");
      return;
    }
    
    try {      
      Element budget = new Element("Budget");
      writeAgencies(budget);
      
      Document doc = new Document(budget);
      Serializer sout = new Serializer(System.out, "UTF-8");
      sout.write(doc); 
      sout.flush();
    }
    catch (IOException ex) {
      System.err.println(ex);
    }

    
  }
  
  private static void writeAgencies(Element parent) {

    Connection conn = null;
    Statement stmnt = null;
    try {
      conn = DriverManager.getConnection(
       "jdbc:odbc:budauth", "", "");
      stmnt = conn.createStatement();
      String query = "SELECT DISTINCT AgencyName, AgencyCode"
       + " FROM BudgetAuthorizationTable;";
      ResultSet agencies = stmnt.executeQuery( query );

      while( agencies.next() ) {
        
        String agencyName = agencies.getString("AgencyName");
        String agencyCode = agencies.getString("AgencyCode");
        Element agency = new Element("Agency");
        Element name = new Element("Name");
        Element code = new Element("Code");
        name.appendChild(agencyName);
        code.appendChild(agencyCode);
        agency.appendChild(name);
        agency.appendChild(code);
        writeBureaus(agency, conn, agencyCode);
        parent.appendChild(agency);
      }
    }
    catch (SQLException e) {
      System.err.println(e);
      e.printStackTrace();       
    }
    finally {
      try {
        stmnt.close();
        conn.close();
      }
      catch(SQLException ex) {
        System.err.println(ex);
      }
    }
              
  }
  
  private static void writeBureaus(Element parent, Connection conn, 
   String agencyCode) throws SQLException {

    String query 
     = "SELECT DISTINCT BureauName, BureauCode "
     + "FROM BudgetAuthorizationTable WHERE AgencyCode='" 
     + agencyCode + "';";
    Statement stmnt = conn.createStatement();
    ResultSet bureaus = stmnt.executeQuery(query);

    while( bureaus.next() ) {
      String bureauName = bureaus.getString("BureauName");
      String bureauCode = bureaus.getString("BureauCode");
      Element bureau = new Element("Bureau");
      Element name = new Element("Name");
      Element code = new Element("Code");
      name.appendChild(bureauName);
      code.appendChild(bureauCode);
      bureau.appendChild(name);
      bureau.appendChild(code);
      writeAccounts(bureau, conn, agencyCode, bureauCode);
      parent.appendChild(bureau);
    }        

  }
  
  private static void writeAccounts(Element parent, Connection conn, 
   String agencyCode, String bureauCode)
   throws SQLException {

    String query = "SELECT DISTINCT AccountName, AccountCode "
     + "FROM BudgetAuthorizationTable WHERE AgencyCode='" 
     + agencyCode + "' AND BureauCode='" + bureauCode + "';";
    Statement stmnt = conn.createStatement();
    ResultSet accounts = stmnt.executeQuery(query);

    while( accounts.next() ) {
      String accountName = accounts.getString("AccountName");
      String accountCode = accounts.getString("AccountCode");
      Element account = new Element("Account");
      Element name = new Element("Name");
      Element code = new Element("Code");
      name.appendChild(accountName);
      code.appendChild(accountCode);
      account.appendChild(name);
      account.appendChild(code);
      writeSubfunctions(
       account, conn, agencyCode, bureauCode, accountCode
      );
      parent.appendChild(account);
    }        
        
  }
  
  private static void writeSubfunctions(Element parent,  
   Connection conn, String agencyCode, String bureauCode, 
   String accountCode) throws SQLException {

    String query = "SELECT * FROM BudgetAuthorizationTable"
     + " WHERE AgencyCode='" + agencyCode + "' AND BureauCode='" 
     + bureauCode + "' AND AccountCode='" + accountCode + "';";
    Statement stmnt = conn.createStatement();
    ResultSet subfunctions = stmnt.executeQuery(query);

    while( subfunctions.next() ) {
      String subfunctionTitle 
       = subfunctions.getString("SubfunctionTitle");
      String subfunctionCode 
       = subfunctions.getString("SubfunctionCode");
      Element subfunction = new Element("Subfunction");
      Element name = new Element("Name");
      Element code = new Element("Code");
      name.appendChild(subfunctionTitle);
      code.appendChild(subfunctionCode);
      subfunction.appendChild(name);
      subfunction.appendChild(code);
      Element amount = new Element("Amount");
      amount.addAttribute(new Attribute("year", "TransitionQuarter"));
      amount.appendChild(
       String.valueOf(subfunctions.getInt("TransitionQuarter") * 1000L));
      subfunction.appendChild(amount);
      for (int year = 1976; year <= 2006; year++) {
        String fy = "FY" + year;
        long amt = subfunctions.getInt(fy) * 1000L;
         amount = new Element("Amount");
         amount.addAttribute(new Attribute("year", String.valueOf(year)));
         amount.appendChild(String.valueOf(amt));
         subfunction.appendChild(amount);
      }
      parent.appendChild(subfunction);
    }        
        
  }
 
 }