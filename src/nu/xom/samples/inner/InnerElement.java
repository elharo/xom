package nu.xom.samples.inner;

import java.io.IOException;

import nu.xom.*;

public class InnerElement extends Element {

    // Thread local????
    private static Builder builder = new Builder(new InnerFactory());
    
    public InnerElement(String name) {
        super(name);
    }

    public InnerElement(String namespace, String name) {
        super(namespace, name);
    }

    public InnerElement(Element element) {
        super(element);
    }

    public String getInnerXML() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < getChildCount(); i++) {
            sb.append(getChild(i).toXML());
        }
        return sb.toString();
    }

    public void setInnerXML(String xml) throws ParsingException {

        xml = "<fakeRoot>"
          + xml + "</fakeRoot>";
        Document doc;
        try {
            doc = builder.build(xml, null);
        }
        catch (IOException ex) {
            throw new ParsingException(ex.getMessage(), ex);
        }
        this.removeChildren();
        Nodes children = doc.getRootElement().removeChildren();
        for (int i = 0; i < children.size(); i++) {
            this.appendChild(children.get(i));
        }
        
    }

    public Node copy() {
        return new InnerElement(this);
    }
    
}
