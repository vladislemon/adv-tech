package net.vladislemon.mc.advtech.util;

/**
 * Created by Notezway on 12.08.2016.
 */
public class EncodedString {

    private static final byte key = 7*13;
    String encoded;

    public EncodedString(String value) {
        encoded = value;
    }

    public static String encode(String value) {
        byte[] bytes = value.getBytes();
        byte[] encBytes = new byte[bytes.length];
        for(int i = 0; i < bytes.length; i++) {
            encBytes[i] = (byte)(bytes[i] ^ key);
        }
        return new String(encBytes);
    }

    public static String decode(String value) {
        return encode(value);
    }

    public static EncodedString valueOf(String value) {
        return new EncodedString(value);
    }

    private static byte[] toBytes(int value) {
        return new byte[] {
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    private static int fromBytes(byte[] bytes) {
        return bytes[0] << 24 | bytes[1] << 16 | bytes[2] << 8 | bytes[3];
    }

    public String toString() {
        return encoded;
    }
}
