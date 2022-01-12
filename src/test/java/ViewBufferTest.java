import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import static org.junit.Assert.assertEquals;

public class ViewBufferTest extends AbstractTest {

    @Test
    public void asCharacterBuffer() {
        final ByteBuffer buffer = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));        
        final CharBuffer charBuffer = buffer.asCharBuffer();
        
        assertEquals("Buffer position invalid", 0, buffer.position());
        assertEquals("CharBuffer position invalid", 0, charBuffer.position());
        assertEquals("Text data invalid", charBuffer.toString(), byteBufferToString(buffer));
    }
    
    @Test
    public void asCharacterBufferSharedData() {
        final ByteBuffer buffer = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));        
        final CharBuffer charBuffer = buffer.asCharBuffer();
        
        assertEquals("Buffer position invalid", 0, buffer.position());
        assertEquals("CharBuffer position invalid", 0, charBuffer.position());
        
        final byte[] period = ".".getBytes(BIG_ENDIAN_CHARSET);
        int idx = 22;
        for (byte elem : period) {
            buffer.put(idx++, elem);
        }
        
        assertEquals("Text data invalid", "Hello world.", byteBufferToString(buffer));
        assertEquals("Text data invalid", charBuffer.toString(), byteBufferToString(buffer));
    }
}
