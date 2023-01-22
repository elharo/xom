/* Copyright 2002, 2003 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library. If not, see
   <https://www.gnu.org/licenses/>.
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/

package nu.xom.samples;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;
import nu.xom.XMLException;


/**
 * <p>
 * Demonstrates the building of a structured XML document,
 * from a relational database using JDBC. A different version of  
 * this example was originally developed for Chapter 4 of 
 * <cite><a 
 * href="http://www.cafeconleche.org/books/xmljava/">Processing
 * XML with Java</a></cite>.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class DatabaseConverter {

    private Connection connection;
  
    // The string passed to the constructor must be a JDBC URL that
    // contains all necessary information for connecting to the
    // database such as host, port, username, password, and
    // database name. For example, 
    // jdbc:mysql://host:port]/dbname?user=username&password=pass
    // The driver should have been loaded before this method is
    // called
    public DatabaseConverter(String jdbcURL) throws SQLException {
       connection = DriverManager.getConnection(jdbcURL);
    }

    public Document extract(String selectQuery) 
      throws IOException {
    
        try {
            Statement statement = connection.createStatement();
            ResultSet data = statement.executeQuery(selectQuery);
            ResultSetMetaData metadata = data.getMetaData();
            int numFields = metadata.getColumnCount();
      
            Element table = new Element("table");
          
            while (data.next()) {
                Element record = new Element("record");
                for (int field = 1; field <= numFields; field++) {
                
                    Element fieldElement = new Element("field");
                    int type = metadata.getColumnType(field);
                    String typeName = getSchemaType(type);
                    fieldElement.addAttribute(new Attribute("xsi:type", 
                     "http://www.w3.org/2001/XMLSchema-instance", 
                     typeName, Attribute.Type.NMTOKEN));
                    String name = metadata.getColumnName(field);
                    fieldElement.addAttribute(new Attribute("name", name));
          
                    // Convert nulls to empty elements with xsi:nil="true"
                    Object value = data.getObject(field);
                    if (value == null) { // null value in database
                        fieldElement.addAttribute(new Attribute("xsi:nil",
                          "http://www.w3.org/2001/XMLSchema-instance", "true"));
                     }
                     else { // non-null value
                        fieldElement.appendChild(convertToXML(data, field, type));
                    }
                    record.appendChild(fieldElement);
                 }
                 table.appendChild(record);
             } // end while
             statement.close();
             return new Document(table);
        }
        catch (SQLException ex) {  // convert exception type
            throw new XMLException("SQL error", ex); 
        }
    
  }

  // I want the XML document to store values in the standard W3C
  // XML Schema Language forms. This requires certain conversions 
  // depending on the type of the data
    private Node convertToXML(ResultSet data, int field, int type)
      throws SQLException, IOException {

        switch (type) {
          case Types.BINARY: 
          case Types.VARBINARY: 
          case Types.LONGVARBINARY: 
            return hexEncode(data.getBinaryStream(field));
          case Types.BLOB:
            Blob blob = data.getBlob(field);
            return hexEncode(blob.getBinaryStream());
          // String types may contain C0 control characters that are
          // not legal in XML. If so an exception is thrown.
          case Types.CLOB: 
            Clob clob = data.getClob(field);
            Reader r = clob.getCharacterStream();
            char[] text = new char[1024];
            int numRead;
            StringBuilder result = new StringBuilder();
            while ((numRead = r.read(text, 0, 1024)) != -1) {
              result.append(text, 0, numRead); 
            }
            return new Text(result.toString());
          case Types.ARRAY:
            Array array = data.getArray(field);
            return writeArray(array);
          default: // All other types can be handled as strings
            Object o = data.getObject(field); 
            if (o == null) return new Text("");                
            return new Text(o.toString()); 
        }     

  }
  
    private Text hexEncode(InputStream in) 
      throws IOException {
    
    	StringBuilder result = new StringBuilder();

        int octet;
        while ((octet = in.read()) != -1) {
            if (octet < 16) result.append('0');
            result.append(Integer.toHexString(octet));
        }
        return new Text(result.toString());
    
    }
  
    private Element writeArray(Array array) 
      throws IOException, SQLException {
    
        Element holder = new Element("array");
        ResultSet data = array.getResultSet();
        int type = array.getBaseType();
        String typeName = getSchemaType(type);

        while (data.next()) {
            Element component = new Element("component");
            component.addAttribute(new Attribute("xsi:type",
              "http://www.w3.org/2001/XMLSchema-instance", typeName));
            component.appendChild(convertToXML(data, 2, type));
            holder.appendChild(component);
        }
        return holder;
    
  }
  
  public static String getSchemaType(int type) {
   
    switch (type) {
      case Types.ARRAY:         return "array";
      case Types.BIGINT:        return "xsd:long";
      case Types.BINARY:        return "xsd:hexBinary";
      case Types.BIT:           return "xsd:boolean";
      case Types.BLOB:          return "xsd:hexBinary";
      case Types.CHAR:          return "xsd:string";
      case Types.CLOB:          return "xsd:string";
      case Types.DATE:          return "xsd:date";
      case Types.DECIMAL:       return "xsd:decimal";
      case Types.DOUBLE:        return "xsd:double";
      case Types.FLOAT:         return "xsd:decimal";
      case Types.INTEGER:       return "xsd:int";
      case Types.JAVA_OBJECT:   return "xsd:string";
      case Types.LONGVARBINARY: return "xsd:hexBinary";
      case Types.LONGVARCHAR:   return "xsd:string";
      case Types.NUMERIC:       return "xsd:decimal";
      case Types.REAL:          return "xsd:float";
      case Types.REF:           return "xsd:IDREF";
      case Types.SMALLINT:      return "xsd:short";
      case Types.STRUCT:        return "struct";
      case Types.TIME:          return "xsd:time";
      case Types.TIMESTAMP:     return "xsd:dateTime";
      case Types.TINYINT:       return "xsd:byte";
      case Types.VARBINARY:     return "xsd:hexBinary";
                                // most general type
      default:                  return "xsd:string"; 
    }
    
  }

}