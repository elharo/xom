package nu.xom.tests;

import org.xml.sax.Locator;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * <p>
 *  Makes sure SAX does not have a locator. 
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.2b2
 *
 */
class LocatorFilter extends XMLFilterImpl {
    
    public LocatorFilter(XMLReader reader) {
        super(reader);
    }

    public void setDocumentLocator(Locator loc) {
        super.setDocumentLocator(new NullLocator(loc));
    }
    
}


class NullLocator implements Locator {

    private Locator loc;
    
    public NullLocator(Locator loc) {
        this.loc = loc;
    }

    public String getSystemId() {
        return null;
    }

    public String getPublicId() {
        return loc.getPublicId();
    }

    public int getLineNumber() {
        return loc.getLineNumber();
    }

    public int getColumnNumber() {
        return loc.getColumnNumber();
    }
    
}
