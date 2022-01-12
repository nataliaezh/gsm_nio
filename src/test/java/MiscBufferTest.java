import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class MiscBufferTest extends AbstractTest {

    @Test
    public void compact() {
        final ByteBuffer buffer = ByteBuffer.allocate(24);
        buffer.putChar('H');
        buffer.putChar('e');
        buffer.putChar('l');
        buffer.putChar('l');
        buffer.putChar('o');        
        
        // simulate partial write -> move the cursor to position 12
        buffer.flip();
        buffer.position(4);
        buffer.compact();
        
        assertEquals("Buffer position invalid", 6, buffer.position());
        
        buffer.putChar('n');
        buffer.putChar('g');
        
        assertEquals("Buffer position invalid", 10, buffer.position());
        buffer.flip();
        assertEquals("Invalid text", "llong", byteBufferToString(buffer));
    }
    
    @Test
    public void testDuplicate() {
        final ByteBuffer buffer = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));
        final ByteBuffer duplicate = buffer.duplicate();
        
        assertEquals("Invalid position", 0, duplicate.position());
        assertEquals("Invalid limit", buffer.limit(), duplicate.limit());
        assertEquals("Invalid capacity", buffer.capacity(), duplicate.capacity());
        
        buffer.putChar(22, '.');
        
        assertEquals("Text data invalid", "Hello world.", byteBufferToString(buffer));
        assertEquals("Text data invalid",  byteBufferToString(duplicate), byteBufferToString(buffer));
    }
    
    @Test
    public void slice() {
        final ByteBuffer buffer = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));
        buffer.position(12);
        
        final ByteBuffer sliced = buffer.slice();
        assertEquals("Text data invalid", "world!", byteBufferToString(sliced));
        assertEquals("Invalid position", 0, sliced.position());
        assertEquals("Invalid limit", buffer.remaining(), sliced.limit());
        assertEquals("Invalid capacity", buffer.remaining(), sliced.capacity());
        
        buffer.putChar(22, '.');
        assertEquals("Text data invalid", "world.", byteBufferToString(sliced));        
    }
    
    @Test
    public void rewind() {
        final ByteBuffer buffer = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));
        
        final byte a = buffer.get();
        final byte b = buffer.get();
        
        assertEquals("Invalid position", 2, buffer.position());
        
        buffer.rewind();
        
        assertEquals("Invalid position", 0, buffer.position());
        assertSame("byte a not same", a, buffer.get());
        assertSame("byte a not same", b, buffer.get());
    }
    
    @Test
    public void compare() {
        final ByteBuffer a = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));
        final ByteBuffer b = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));
        
        assertTrue("a is not the same as b", a.compareTo(b) == 0);
    }
    
    /* Unsynchronized positions on identical data ByteBuffers will render a lexicographically dissimilar comparison.
     * */
    @Test
    public void compareDiffPositions() {
        final ByteBuffer a = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));
        final ByteBuffer b = ByteBuffer.wrap("Hello world!".getBytes(BIG_ENDIAN_CHARSET));
        
        a.position(2);
        
        assertTrue("a is the same as b", a.compareTo(b) != 0);
    }    
}
