import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class AbsoluteGetPutTest extends AbstractTest {

    @Test
    public void get() {
        final ByteBuffer buffer = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));

        // An absolute get will not advance the position cursor.
        final byte a = buffer.get(0);
        final byte b = buffer.get(1);

        assertEquals("Buffer position invalid", 0, buffer.position());
        assertEquals("'H' not the first 2 bytes read", "H", new String(new byte[] { a, b }, BIG_ENDIAN_CHARSET));
    }

    @Test
    public void put() {
        final ByteBuffer buffer = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));

        // This loop will insert the bytes required for the period space.
        // The inserts will be done absolutely
        final byte[] period = ".".getBytes(BIG_ENDIAN_CHARSET);
        int idx = 22;
        for (byte elem : period) {
            buffer.put(idx++, elem);
        }

        assertEquals("Position must remian 0", 0, buffer.position());
        assertEquals("Text data invalid", "Hello world.", byteBufferToString(buffer));
    }
    
    @Test
    public void getChar() {
        final ByteBuffer buffer = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));
        char value = buffer.getChar(22);

        assertEquals("Buffer position invalid", 0, buffer.position());
        assertEquals("Invalid final character", "!", Character.toString(value));
    }
    
    @Test
    public void putChar() {
        final ByteBuffer buffer = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));
        buffer.putChar(22, '.');
        
        assertEquals("Buffer position invalid", 0, buffer.position());        
        assertEquals("Text data invalid", "Hello world.", byteBufferToString(buffer));
    }
}
