import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class RelativeGetPutTest extends AbstractTest {

    /* Each character takes up 2 bytes because of the encoding UTF-16BE.
     * By getting the first 2 we ensure we get the first letter of the phrase. */
    @Test
    public void get() {
        final ByteBuffer buffer = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));

        // The 2 get calls will advance the position cursor by 2.
        final byte a = buffer.get();
        final byte b = buffer.get();

        assertEquals("Buffer position invalid", 2, buffer.position());
        assertEquals("'H' not the first 2 bytes read", "H", new String(new byte[] { a, b }, BIG_ENDIAN_CHARSET));
    }

    @Test
    public void put() {
        final ByteBuffer buffer = ByteBuffer.allocate(24);

        buffer.put("H".getBytes(BIG_ENDIAN_CHARSET));
        buffer.put("e".getBytes(BIG_ENDIAN_CHARSET));
        buffer.put("l".getBytes(BIG_ENDIAN_CHARSET));
        buffer.put("l".getBytes(BIG_ENDIAN_CHARSET));
        buffer.put("o".getBytes(BIG_ENDIAN_CHARSET));

        buffer.put(" ".getBytes(BIG_ENDIAN_CHARSET));

        buffer.put("e".getBytes(BIG_ENDIAN_CHARSET));
        buffer.put("a".getBytes(BIG_ENDIAN_CHARSET));
        buffer.put("r".getBytes(BIG_ENDIAN_CHARSET));
        buffer.put("t".getBytes(BIG_ENDIAN_CHARSET));
        buffer.put("h".getBytes(BIG_ENDIAN_CHARSET));
        buffer.put("!".getBytes(BIG_ENDIAN_CHARSET));

        // It took 12 put calls which placed a 2 byte character, 12 times into the buffer, advancing the cursor 24 positions.
        assertEquals("Buffer position invalid", 24, buffer.position());
        
        // We flip the buffer to reset the position cursor for the purposes of converting to a String.
        buffer.flip();
        assertEquals("Text data invalid", "Hello earth!", byteBufferToString(buffer));
    }

    @Test
    public void bulkGet() {
        final ByteBuffer buffer = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));
        final byte[] output = new byte[10];

        // This will get the contents of the buffer in bulk and insert it into output.
        // The number of bytes will correspond to the length of the output array.
        buffer.get(output);

        assertEquals("Invalid bulk get data", "Hello", new String(output, BIG_ENDIAN_CHARSET));
        
        /* The position cursor of the source buffer will advance to the index corresponding
           to the length of output */
        assertEquals("Buffer position invalid", 10, buffer.position());
    }

    @Test
    public void bulkPut() {
        final ByteBuffer buffer = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));
        final byte[] output = new String("earth.").getBytes(BIG_ENDIAN_CHARSET);

        buffer.position(12);
        
        // The nett effect of this will be to overwrite 'world!' with 'earth.'
        buffer.put(output);

        assertEquals("Buffer position invalid", 24, buffer.position());
        buffer.flip();
        assertEquals("Text data invalid", "Hello earth.", byteBufferToString(buffer));
    }

    @Test
    public void getChar() {
        final ByteBuffer buffer = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));
        
        // We mark the position because we need to reset back to it later.
        buffer.mark();
        
        // Get the first letter.
        final byte a = buffer.get();
        final byte b = buffer.get();
        
        // Reset the position cursor to our previous mark.
        buffer.reset();

        // Via convenience method #getChar() we get the first character (same as 2 #get() calls above)
        char value = buffer.getChar();

        // Confirm our buffer position.
        assertEquals("Buffer position invalid", 2, buffer.position());
        
        // Ensure that the #getChar() and 2 #get() calls above return the same data.
        assertEquals("'H' not the first 2 bytes read", "H", new String(new byte[] { a, b }, BIG_ENDIAN_CHARSET));
        assertEquals("Value and byte array not equal", Character.toString(value), new String(new byte[] { a, b }, BIG_ENDIAN_CHARSET));
    }
    
    @Test
    public void putChar() {
        final ByteBuffer buffer = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));
        
        // Set the position of the buffer.
        buffer.position(22);
        
        // Any relative put will advance the buffer position by the number of bytes required for what is being inserted.
        buffer.putChar('.');
        
        assertEquals("Buffer position invalid", 24, buffer.position());        
        
        // We flip for writing the buffer to String.
        buffer.flip();        
        assertEquals("Text data invalid", "Hello world.", byteBufferToString(buffer));
    }
}
