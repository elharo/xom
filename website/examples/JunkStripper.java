import nu.xom.*;

public class JunkStripper extends NodeFactory {

    private Nodes empty = new Nodes();

    public Nodes makeComment(String data) {
        return empty;  
    }    

    public Nodes makeProcessingInstruction(String target, String data) {
        return empty; 
    }
    
    public Nodes makeDocType(String rootElementName, 
      String publicID, String systemID) {
        return empty;    
    }

}