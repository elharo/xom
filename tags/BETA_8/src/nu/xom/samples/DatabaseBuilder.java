/* Copyright 2003 Elliotte Rusty Harold
   
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
   elharo@metalab.unc.edu. Please include the word "XOM" in the
   subject line. The XOM home page is temporarily located at
   http://www.cafeconleche.org/XOM/  but will eventually move
   to http://www.xom.nu/  */

package nu.xom.samples;

import nu.xom.*;
import java.sql.*;
import java.io.*;


/**
 * <p>
 *   Based on Example 8-13 in Processing XML with Java
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0b7
 */
public class DatabaseBuilder  {

    private Connection connection;
      
    
    // The string passed to the constructor must be a JDBC URL that
    // contains all necessary information for connecting to the
    // database such as host, port, username, password, and
    // database name. For example, 
    // jdbc:mysql://host:port]/dbname?user=username&password=pass
    // The driver should have been loaded before this method is
    // called
    public DatabaseBuilder(String jdbcURL) throws SQLException {
        connection = DriverManager.getConnection(jdbcURL);
    }
  
    
    public Document build(String selectQuery) 
      throws SQLException, ParsingException {
    
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
                Attribute typeAtt = new Attribute("xsi:type", 
                  "http://www.w3.org/2001/XMLSchema-instance", typeName);
                fieldElement.addAttribute(typeAtt);
                String name = metadata.getColumnName(field);
                Attribute nameAtt = new Attribute("name", name);
                fieldElement.addAttribute(nameAtt);
                // Convert nulls to empty elements with xsi:nil="true"
                Object value = data.getObject(field);
                if (value == null) { // null value in database
                    Attribute nilAtt = new Attribute("xsi:nil", 
                      "http://www.w3.org/2001/XMLSchema-instance", "true");
                    fieldElement.addAttribute(nilAtt);
                }
                else { // non-null value
                    fieldElement.appendChild(convertToXML(data, field, type));
                }
                record.appendChild(fieldElement);
            }
            table.appendChild(record);
        }
        statement.close();
      
        table.addNamespaceDeclaration("xsi",
          "http://www.w3.org/2001/XMLSchema-instance");
        table.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        return new Document(table);
    
    }

    
    // I want the XML document to store values in the standard W3C
    // XML Schema Language forms. This requires certain conversions 
    // depending on the type of the data
    private Node convertToXML(ResultSet data, int field, int type)
      throws SQLException, ParsingException {

        switch (type) {
          case Types.BINARY: 
          case Types.VARBINARY: 
          case Types.LONGVARBINARY: 
            return hexEncode(data.getBinaryStream(field));
          case Types.BLOB:
            Blob blob = data.getBlob(field);
            return hexEncode(blob.getBinaryStream());
          case Types.CLOB: 
            Clob clob = data.getClob(field);
            Reader r = clob.getCharacterStream();
            char[] text = new char[1024];
            int numRead;
            try {
              StringBuffer result = new StringBuffer();
              while ((numRead = r.read(text, 0, 1024)) != -1) {
                result.append(escapeText(text, 0, numRead)); 
              }
              return new Text(result.toString());
            }
            catch (IOException ex) {
              throw new ParsingException("Read from CLOB failed", ex); 
            }
          case Types.ARRAY:
            Array array = data.getArray(field);
            return writeArray(array);
          default: // All other types can be handled as strings
            Object o = data.getObject(field); 
            if (o == null) return new Text("");                
            String s = o.toString(); 
            char[] value = s.toCharArray();
            return escapeText(value, 0, value.length);
        }     

    }
  
    
    private Text hexEncode(InputStream in) {
    
        StringBuffer result = new StringBuffer();
        try {
            int octet;
            while ((octet = in.read()) != -1) {
                StringWriter out = new StringWriter(2);
                if (octet < 16) out.write('0');
                out.write(Integer.toHexString(octet));
                result.append(out.toString());
            }
            return new Text(result.toString());
        }
        catch (IOException ex) {
          throw new XMLException("Error while hex-encoding", ex);
        }
    
    }
 
    
  // String types may contain C0 control characters that are
  // not legal in XML. I convert these to the Unicode replacement
  // character 0xFFFD
    private Text escapeText(char[] text, int start, int length) {
        StringBuffer result = new StringBuffer(length);
        for (int i = start; i < length; i++) {
            result.append(escapeChar(text[i]));
        }
        return new Text(result.toString());
    }

    private char escapeChar(char c) {
        if (c >= 0x20) return c;
        else if (c == '\n') return c;
        else if (c == '\r') return c;
        else if (c == '\t') return c;
        return '\uFFFD';
    }
 
    private Node writeArray(Array array)
      throws SQLException, ParsingException {
    
        ResultSet data = array.getResultSet();
        int type = array.getBaseType();
        String typeName = getSchemaType(type);
    
        Element arrayElement = new Element("array");
        while (data.next()) {
            Element component = new Element("component");
            component.addAttribute(new Attribute("xsi:type", 
              "http://www.w3.org/2001/XMLSchema-instance",  
              typeName));
            component.appendChild(convertToXML(data, 2, type));
            arrayElement.appendChild(component);
        }
        return arrayElement;
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

    
    public static void main(String[] args) {
    
        if (args.length < 2) {
            System.out.println(
              "Usage: java DatabaseBuilder URL query driverClass");
            return;
        }
        String url = args[0];
        String query = args[1];
        String driverClass = "org.gjt.mm.mysql.Driver"; // MySQL
        if (args.length >= 3) driverClass = args[2];
    
        try {
          // Load JDBC driver
          Class.forName(driverClass).newInstance();
          // Technically, the newInstance() call isn't needed, 
          // but the MM.MySQL documentation suggests this to 
          // "work around some broken JVMs"
        
          DatabaseBuilder builder = new DatabaseBuilder(url);
          Serializer out = new Serializer(System.out);
          
          out.setIndent(2);
          Document doc = builder.build(query);
          out.write(doc);
          out.flush();
      }
      catch (InstantiationException ex) { 
          System.out.println(driverClass + " could not be instantiated");
      }
      catch (ClassNotFoundException ex) { 
          System.out.println(driverClass + " could not be found");
      }
      catch (Exception ex) { // SQL, SAX, and IO
          ex.printStackTrace();
      }
    
  }
  
}
