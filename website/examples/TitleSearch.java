import nu.xom.*;
import java.io.IOException;

public class TitleSearch {

  public static void main(String[] args) {
   
   if (args.length == 0) {
     System.err.println("Usage: java TitleSearch url");
     return;
   }      

   String pageURL = args[0];
   
   Builder builder = new Builder();
   try {
     Document doc = builder.build(pageURL);
     Element html = doc.getRootElement();
     Element head = html.getFirstChildElement("head");
     if (head == null) {
       head = html.getFirstChildElement("head", "http://www.w3.org/1999/xhtml");
     }
     Element title = head.getFirstChildElement("title");  
     if (title == null) {
       title = head.getFirstChildElement("title", "http://www.w3.org/1999/xhtml"); 
     }
     System.out.println(title.getValue());
   }
   catch (NullPointerException ex) {
     System.err.println(pageURL + " does not have a title.");     
   }
   catch (ParsingException ex) {
     System.err.println(pageURL + " is malformed.");     
   }
   catch (IOException ex) {
     System.err.println("Could not read " + pageURL); 
   }  
    
  }
  
}
