// Copyright 2002-2004 Elliotte Rusty Harold
// 
// This library is free software; you can redistribute 
// it and/or modify it under the terms of version 2.1 of 
// the GNU Lesser General Public License as published by  
// the Free Software Foundation.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General 
// Public License along with this library; if not, write to the 
// Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
// Boston, MA  02111-1307  USA
// 
// You can contact Elliotte Rusty Harold by sending e-mail to
// elharo@metalab.unc.edu. Please include the word "XOM" in the
// subject line. The XOM home page is temporarily located at
// http://www.cafeconleche.org/XOM/  but will eventually move
// to http://www.xom.nu/


package nu.xom;

/**
 * <p>
 *  This class represents an XML processing instruction.
 *  Each processing instruction has two key properties:
 * </p>
 * 
 * <ul>
 *   <li>The target, a non-colonized name</li>
 *   <li>The data, a string which does not contain the two character
 *       sequence <code>?&gt;</code>. The syntax of the data
 *       depends completely on the processing instruction. 
 *       Other than forbidding <code>?&gt;</code>, XML defines
 *       no rules for processing instruction data.
 *   </li>
 * </ul>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0a1
 *
 */
public class ProcessingInstruction extends Node {

    private String target;
    private String data;

    /**
     * <p>
     * Create a processing instruction with a certain target and data.
     * </p>
     * 
     * @param target the target of the processing instruction
     * @param data the processing instruction data
     * 
     * @throws IllegalTargetException if the target is not a 
     *    non-colonized name or is made of the string "xml" 
     *    in any case.
     * @throws IllegalDataException if data contains "?>" or any 
     *   other illegal characters
     */
    public ProcessingInstruction(String target, String data) {
        setTarget(target);  
        setValue(data);  
    }

    
    /**
     * <p>
     * Create a copy of a processing instruction.
     * </p>
     * 
     * @param instruction the processing instruction to copy
     * 
     */
    public ProcessingInstruction(ProcessingInstruction instruction) {
        this.target = instruction.target;
        this.data = instruction.data;  
    }
    
    
    private ProcessingInstruction() {}
    
    static ProcessingInstruction build(String target, String data) {
        ProcessingInstruction result = new ProcessingInstruction();
        result.target = target;
        result.data = data;
        return result;
    }
    
    
    /**
     * <p>
     * Returns the processing instruction target.
     * </p>
     * 
     * @return the target
     */
    public final String getTarget() {
        return target;
    }

    
    /**
     * <p>
     * Sets the target.
     * </p>
     * 
     * @param target the new target
     * 
     * @throws IllegalTargetException if the proposed target 
     *   is not an XML 1.0 non-colonized name
     * @throws XMLException if the proposed target does not 
     *     satisfy the local constraints
     */
    public void setTarget(String target) {
        
        try {
            Verifier.checkNCName(target);
        }
        catch (IllegalNameException ex) {
            IllegalTargetException tex = new IllegalTargetException(ex.getMessage()); 
            tex.setData(target);
            throw tex;
        }
        
        if (target.equalsIgnoreCase("xml")) {
            IllegalTargetException tex = new IllegalTargetException(
              "Processing instructions targets cannot be XML better message????.");
            tex.setData(target);
            throw tex;
        }
        
        this.target = target;
        
    }  

    
    /**
     * <p>
     * Sets the data.
     * </p>
     * 
     * @param data the data to set
     * 
     * @throws IllegalDataException if <code>data</code> is null
     *      or otherwise not legal XML processing instruction data
     * @throws XMLException if the proposed data does not satisfy 
     *   the local constraints
     */
    public void setValue(String data) {
        
        Verifier.checkPCDATA(data);
        if (data.length() != 0) {
            if (data.indexOf("?>") >= 0) {
                IllegalDataException ex = new IllegalDataException(
                  "Processing instruction data must not contain \"?>\""
                );
                ex.setData(data);
                throw ex;
            }
            if (data.indexOf('\r') >= 0) {
                IllegalDataException ex = new IllegalDataException(
                  "Processing instruction data cannot contain carriage returns"
                );
                ex.setData(data);
                throw ex;
            }
            
            char first = data.charAt(0);
            if (first == ' ' || first == '\n' || first == '\t') {
                IllegalDataException ex =  new IllegalDataException(
                  "Processing instruction data cannot contain " +
                  "leading white space"
                );
                ex.setData(data);
                throw ex;
            }
        }
        this.data = data;
        
    }

    
    /**
     * <p>
     * Returns the processing instruction data.
     * </p>
     * 
     * @return the data of the processing instruction
     *
     * @see nu.xom.Node#getValue()
     *
     */
    public final String getValue() {
        return data;
    }

    
    /**
     * <p>
     * Throws <code>IndexOutOfBoundsException</code> because 
     * processing instructions do not have children.
     * </p>
     * 
     * @return never returns because processing instructions do not 
     *     have children; always throws an exception.
     * 
     * @param position the index of the child node to return
     * 
     * @throws IndexOutOfBoundsException because processing  
     *     instructions do not have children
     */
    public final Node getChild(int position) {
        throw new IndexOutOfBoundsException(
          "LeafNodes do not have children");        
    }

    
    /**
     * <p>
     * Returns 0 because processing instructions do not have children.
     * </p>
     * 
     * @return zero
     * 
     * @see nu.xom.Node#getChildCount()
     */
    public final int getChildCount() {
        return 0;   
    }

    
    /**
     * <p>
     * Returns the actual XML form of this processing instruction, 
     * such as might be copied and pasted from the original document.
     * </p>
     * 
     * @return an XML representation of this processing instruction 
     *         as a <code>String</code>
     *
     * @see nu.xom.Node#toXML()
     */
    public final String toXML() {
        
        StringBuffer result = new StringBuffer("<?");
        result.append(target);
        if (data.length() > 0) {
            result.append(' ');
            result.append(data);
        }
        result.append("?>");
        return result.toString();   
    }

    
    /**
     * <p>
     * Returns a deep copy of this processing instruction with no
     * parent, that can be added to this document or a different 
     * one.
     * </p>
     * 
     * @return a copy of this <code>ProcessingInstruction</code> 
     *         with no parent
     * 
     * @see nu.xom.Node#copy()
     */
    public Node copy() {
        return new ProcessingInstruction(target, data);
    }

    
    boolean isProcessingInstruction() {
        return true;   
    }

    
    /**
     * <p>
     * Returns a <code>String</code> representation 
     * of this processing instruction suitable for
     * debugging and diagnosis. This is <em>not</em>
     * the XML representation of this processing instruction.
     * </p>
     * 
     * @return a non-XML string representation of this 
     *      <code>ProcessingInstruction</code>
     * 
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        return "[" + getClass().getName() + ": target=\"" 
         + target + "\"; data=\"" + data +"\"]";
    }

    
}
