package nu.xom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;


public class GenericWriter extends TextWriter {
    
    private ByteArrayOutputStream bout;
    private OutputStreamWriter wout;

    public GenericWriter(Writer out, String encoding) 
      throws UnsupportedEncodingException {
        super(out, encoding);
        bout = new ByteArrayOutputStream(32);
        wout = new OutputStreamWriter(bout, encoding);
    }

    boolean needsEscaping(char c) {
       
        // assume everything has at least the ASCII characters
        if (c <= 127) return false;
        
        boolean result = false;
        try {
            wout.write(c);
            wout.flush();
            byte[] data = bout.toByteArray();    
            if (data.length == 0) result = true; // surrogate pair
            else if (data[0] == '?') result = true;
        }
        catch (IOException ex) {
            throw new RuntimeException(
              "Impossible to have an IOException when writing to a byte array"
            );
        }
        finally {
            bout.reset();
        }
        return result;
        
    }
    
}
