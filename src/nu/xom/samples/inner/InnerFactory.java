package nu.xom.samples.inner;

import nu.xom.*;

public class InnerFactory extends NodeFactory {

    public Element startMakingElement(String namespaceURI, String name) {
        return new InnerElement(namespaceURI, name);
    }
    
}
