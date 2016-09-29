package io.mycat.util;

import java.io.IOException;
import java.nio.ByteBuffer;

import io.mycat.net2.ConDataBuffer;

/**
 * String utils.
 * 
 * @author little-pan
 * @since 2016-09-29
 *
 */
public final class StringUtil {
	
	private StringUtil(){
		
	}
	
	/**
	 * An interface that gets a byte from buffer such as byte array etc.
	 * 
	 * @author little-pan
	 * @since 2016-09-26
	 * 
	 */
	public interface ByteGetable{
		
		byte get(int i);
		
	}
	
	public static class ByteArrayGetable implements ByteGetable{
		final byte[] buffer;
		
		public ByteArrayGetable(final byte[] buffer){
			this.buffer = buffer;
		}

		@Override
		public byte get(final int i) {
			return (buffer[i]);
		}
		
	}
	
	public static class ConDataBufferGetable implements ByteGetable{
		
		final ConDataBuffer buffer;
		
		public ConDataBufferGetable(final ConDataBuffer buffer){
			this.buffer = buffer;
		}

		@Override
		public byte get(final int i){
			try {
				return (buffer.getByte(i));
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	public static class ByteBufferGetable implements ByteGetable{
		
		final ByteBuffer buffer;
		
		public ByteBufferGetable(final ByteBuffer buffer){
			this.buffer = buffer;
		}

		@Override
		public byte get(final int i){
			return (buffer.get(i));
		}
		
	}
	
	public final static String dumpAsHex(final byte[] buffer) {
		return dumpAsHex(buffer, 0, buffer.length);
	}
	
	public final static String dumpAsHex(final byte[] buffer, final int length) {
		return dumpAsHex(buffer, 0, length);
	}
	
	public final static String dumpAsHex(final byte[] buffer, final int offset, final int length) {
		return dumpAsHex(new ByteArrayGetable(buffer), offset, length);
	}
	
	/**
     * Dumps the given bytes as a hex dump (from offset up to length bytes).
     * 
     * @param byteBuffer the data to print as hex
     * @param offset the begin index of bytes to print
     * @param length the number of bytes to print
     * @param g the get a byte interface from buffer such as byte array etc
     * 
     * @return hex string
     */
    public final static String dumpAsHex(final ByteGetable g, final int offset, final int length) {
        final StringBuilder out = new StringBuilder(length * 4);
        int p = offset;
        int rows = length / 8;

        // rows
        for (int i = 0; (i < rows) && (p < length); i++) {
            // - hex string in a line
            for (int j = 0, k = p; j < 8; j++, k++) {
                final String hexs = Integer.toHexString(g.get(k) & 0xff);
                if (hexs.length() == 1) {
                	out.append('0');
                }
                out.append(hexs).append(' ');
            }
            out.append("    ");
            // - ascii char in a line
            for (int j = 0; j < 8; j++, p++) {
                final int b = 0xff & g.get(p);
                if (b > 32 && b < 127) {
                	out.append((char) b);
                } else {
                	out.append('.');
                }
                out.append(' ');
            }
            out.append('\n');
        }

        // remain bytes
        int n = 0;
        for (int i = p; i < length; i++, n++) {
            final String hexs = Integer.toHexString(g.get(i) & 0xff);
            if (hexs.length() == 1) {
            	out.append('0');
            }
            out.append(hexs).append(' ');
        }
        for (int i = n; i < 8; i++) {
        	out.append("   ");
        }
        out.append("    ");
        
        for (int i = p; i < length; i++) {
            final int b = 0xff & g.get(i);
            if (b > 32 && b < 127) {
            	out.append((char) b);
            } else {
            	out.append('.');
            }
            out.append(' ');
        }
        if(p < length){
        	out.append('\n');
        }
        
        return (out.toString());
    }
    
    public final static String dumpAsHex(final ByteBuffer buffer){
    	return (dumpAsHex(buffer, 0, buffer.position()));
    }
    
    public final static String dumpAsHex(final ByteBuffer buffer, final int length){
    	return (dumpAsHex(buffer, 0, length));
    }
    
    public final static String dumpAsHex(final ByteBuffer buffer, final int offset, final int length){
    	return (dumpAsHex(new ByteBufferGetable(buffer), offset, length));
    }
    
    public final static String dumpAsHex(final ConDataBuffer buffer){
    	try {
			return (dumpAsHex(buffer, 0, buffer.writingPos()));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
    }
    
    public final static String dumpAsHex(final ConDataBuffer buffer, final int length){
    	return (dumpAsHex(buffer, 0, length));
    }
    
    public final static String dumpAsHex(final ConDataBuffer buffer, final int offset, final int length){
    	return (dumpAsHex(new ConDataBufferGetable(buffer), offset, length));
    }
    
    public static void main(String args[]){
    	final byte[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 48, 49, 50, 97, 98, 99};
    	
    	System.out.println("test - byte array");
    	System.out.println(dumpAsHex(array, 0));
    	System.out.println(dumpAsHex(array, 0, 5));
    	System.out.println(dumpAsHex(array, 8));
    	System.out.println(dumpAsHex(array, 15));
    	System.out.println(dumpAsHex(array));
    	
    	System.out.println("test - ByteBuffer");
    	final ByteBuffer buffer = ByteBuffer.wrap(array);
    	buffer.position(buffer.limit());
    	System.out.println(dumpAsHex(buffer));
    }
    
}
