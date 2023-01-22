/* Copyright 2005 Elliotte Rusty Harold
   
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
package nu.xom.tests;

import nu.xom.*;
import nu.xom.Attribute.Type;

class ANodeFactory extends NodeFactory {

    public Nodes makeAttribute(String name, String URI, String value, Type type) {
        return new Nodes(new AnAttribute(name, URI, value, type));
    }

    public Nodes makeComment(String data) {
        return new Nodes(new AComment(data));
    }

    public Nodes makeText(String text) {
        return new Nodes(new AText(text));
    }

    public Nodes makeDocType(String rootElementName, String publicID, String systemID) {
        return new Nodes(new ADocType(rootElementName, publicID, systemID));
    }

    public Nodes makeProcessingInstruction(String target, String data) {
        return new Nodes(new AProcessingInstruction(target, data));
    }

    public Document startMakingDocument() {
        return new ADocument(new AElement("foo", "http://www,examle.com"));
    }

    public Element startMakingElement(String name, String namespace) {
        return new AElement(name, namespace);
    }

}


class AElement extends Element {
    
    public AElement(String name, String uri) {
        super(name, uri);
    }
    
}


class ADocument extends Document {

    public ADocument(Element root) {
        super(root);
    }
    
}


class AText extends Text {
    
    public AText(String text) {
        super(text);
    }
    
}


class AComment extends Comment {
    
    public AComment(String text) {
        super(text);
    }
    
}


class AProcessingInstruction extends ProcessingInstruction {

    public AProcessingInstruction(String target, String data) {
        super(target, data);
    }
    
}


class ADocType extends DocType {

    public ADocType(String rootElementName, String publicID, String systemID) {
        super(rootElementName, publicID, systemID);
    }
    
}


class AnAttribute extends Attribute {

    public AnAttribute(String name, String URI, String value, Type type) {
        super(name, URI, value, type);
    }
    
}