package nu.xom.samples.inner;

import java.io.IOException;

import junit.framework.TestCase;
import nu.xom.*;


public class InnerTest extends TestCase {

    private Builder builder = new Builder(new InnerFactory());
    
    protected void setUp() {
    }
    
    public void testGetInner() throws ValidityException, ParsingException, IOException {
        Document doc = builder.build("<root><a>test</a><b>test2</b></root>", null);
        InnerElement root = (InnerElement) doc.getRootElement();
        assertEquals("<a>test</a><b>test2</b>", root.getInnerXML());
    }
    
    public void testSetInner() throws ValidityException, ParsingException, IOException {
        Document doc = builder.build("<root><a>test</a><b>test2</b></root>", null);
        InnerElement root = (InnerElement) doc.getRootElement();
        root.setInnerXML("a<c/>d");
        assertEquals("a<c />d", root.getInnerXML());
    }
    
    public void testGetInnerMixed() throws ValidityException, ParsingException, IOException {
        Document doc = builder.build("<root><a>test</a>test<b>test2</b></root>", null);
        InnerElement root = (InnerElement) doc.getRootElement();
        assertEquals("<a>test</a>test<b>test2</b>", root.getInnerXML());
    }
    
    
}
