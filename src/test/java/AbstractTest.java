import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public abstract class AbstractTest {
    
    protected static final String UTF_16_BIG_ENDIAN = "UTF-16BE";
    
    /* Because we using UTF-16 we ensure that the byte order is predictable on encode and decode by
     * forcing the Charset to be UTF-16BE BigEndian. */
    protected static final Charset BIG_ENDIAN_CHARSET = Charset.forName(UTF_16_BIG_ENDIAN);
    protected static final CharsetEncoder UTF_16_ENCODER = BIG_ENDIAN_CHARSET.newEncoder();
    protected static final CharsetDecoder UTF_16_DECODER = BIG_ENDIAN_CHARSET.newDecoder();
    
    /* Remember ByteBuffers are not thread safe and nor is this method, should the ByteBuffer be used 
     * in a multithreaded context it would be best to lock on some shared visible lock (perhaps the ByteBuffer itself)
     * while doing this or any compound action on the ByteBuffer, remember this method will effect the ByteBuffers 
     * position possibly corrupting the invariant of some other methods expectations by some other Thread.
     * 
     * One could preserve the current position by marking it and then resetting back to it just prior to leaving the method.
     */
    protected final String byteBufferToString(final ByteBuffer buffer) {
        final byte[] bytes = new byte[buffer.remaining()];        
        buffer.duplicate().get(bytes);        
        
        // Ensure we decode the bytes using the correct Charset.
        final String result = new String(bytes, BIG_ENDIAN_CHARSET);
        return result;
    }

    @BeforeClass
    public static void init() {
        Thread.currentThread().getContextClassLoader().setDefaultAssertionStatus(true);
    }

    @AfterClass
    public static void destroy() {
        Thread.currentThread().getContextClassLoader().setDefaultAssertionStatus(false);
    }
}
