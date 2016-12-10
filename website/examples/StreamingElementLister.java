import nu.xom.*;
import java.io.IOException;

public class StreamingElementLister extends NodeFactory{

  private int depth = 0;
  private Nodes empty = new Nodes();

  public static void main(String[] args) {

    if (args.length == 0) {
        System.out.println(
          "Usage: java nu.xom.samples.StreamingElementLister URL"
        ); 
        return;
    } 
  
    Builder builder = new Builder(new StreamingElementLister());
 
    try {
        builder.build(args[0]);
    }  
    catch (ParsingException ex) { 
        System.out.println(args[0] + " is not well-formed.");
        System.out.println(ex.getMessage());
    }  
    catch (IOException ex) { 
        System.out.println(ex);
    }  

  }

  // We don't need the comments.     
  public Nodes makeComment(String data) {
    return empty;  
  }    

  // We don't need text nodes at all    
  public Nodes makeText(String data) {
    return empty;  
  }    

  public Element startMakingElement(String name, String namespace) {
    depth++; 
    printSpaces();
    System.out.println(name);           
    return new Element(name, namespace);
  }
  
  public Nodes finishMakingElement(Element element) {
    depth--;
    if (element.getParent() instanceof Document) {
        return new Nodes(element);
    }
    else return empty;
  }

  public Nodes makeAttribute(String name, String URI, 
    String value, Attribute.Type type) {
      return empty;
  }

  public Nodes makeDocType(String rootElementName, 
    String publicID, String systemID) {
      return empty;    
  }

  public Nodes makeProcessingInstruction(
    String target, String data) {
      return empty; 
  }  

  private void printSpaces() {    
    for (int i = 0; i <= depth; i++) {
      System.out.print(' '); 
    } 
  }

}