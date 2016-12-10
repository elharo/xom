import java.io.*;
import nu.xom.*

public class StreamingROT13 extends NodeFactory {

    public static String rot13(String s) {
    
        StringBuffer out = new StringBuffer(s.length());
        for (int i = 0; i < s.length(); i++) {
            int c = s.charAt(i);
            if (c >= 'A' && c <= 'M') out.append((char) (c+13));
            else if (c >= 'N' && c <= 'Z') out.append((char) (c-13));
            else if (c >= 'a' && c <= 'm') out.append((char) (c+13));
            else if (c >= 'n' && c <= 'z') out.append((char) (c-13));
            else out.append((char) c);
        } 
        return out.toString();
    
    }

    public Nodes makeComment(String data) {
        return new Nodes(new Comment(rot13(data)));
    }    

    public Nodes makeText(String data) {
        return new Nodes(new Text(rot13(data)));  
    }    

    public Nodes makeAttribute(String name, String namespace, 
      String value, Attribute.Type type) {
        return new Nodes(new Attribute(name, namespace, rot13(value), type));  
    }

    public Nodes makeProcessingInstruction(
      String target, String data) {
        return new Nodes(new ProcessingInstruction(rot13(target), rot13(data)));
    }

    public static void main(String[] args) {

        if (args.length <= 0) {
          System.out.println("Usage: java nu.xom.samples.StreamingROT13 URL");
          return;
        }
    
        try {
          Builder parser = new Builder(new StreamingROT13());
      
          // Read the document
          Document document = parser.build(args[0]); 
      
          // Write it out again
          Serializer serializer = new Serializer(System.out);
          serializer.write(document);

        }
        catch (IOException ex) { 
          System.out.println(
          "Due to an IOException, the parser could not encode " + args[0]
          ); 
        }
        catch (ParsingException ex) { 
          System.out.println(ex); 
          ex.printStackTrace(); 
        }
     
    } // end main
  
}