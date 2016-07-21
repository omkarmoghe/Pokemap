package com.omkarmoghe.pokemap.utils;

import java.math.BigInteger;

/**
 * This class provides some static methods for manipulating bytes and
 * byte arrays.
 *
 * @author Jonathan Coe
 */
public final class ByteUtils
{
    private ByteUtils()
    {
        // The constructor of this class is private in order to prevent the class being instantiated
    }

    /**
     * Converts a byte[] of unsigned bytes in big-endian order to a short.
     *
     * @param bytes - A byte[] containing the bytes to convert.
     *
     * @return A short containing the equivalent signed value of the given bytes.
     */
    public static short bytesToShort(byte[] bytes)
    {
        short s = 0;
        s |= (bytes[0] & 0xFF) << 8;
        s |= (bytes[1] & 0xFF);
        return s;
    }

    /**
     * Converts a byte[] of unsigned bytes in big-endian order to an int.
     *
     * @param bytes - A byte[] containing the bytes to convert.
     *
     * @return An int containing the equivalent signed value of the given bytes.
     */
    public static int bytesToInt(byte[] bytes)
    {
        int i = 0;
        i |= (bytes[0] & 0xFF) << 24;
        i |= (bytes[1] & 0xFF) << 16;
        i |= (bytes[2] & 0xFF) << 8;
        i |= (bytes[3] & 0xFF);
        return i;
    }

    /**
     * Converts a byte[] of unsigned bytes in big-endian order to a long.
     *
     * @param bytes - A byte[] containing the bytes to convert.
     *
     * @return A long containing the equivalent signed value of the given bytes.
     */
    public static long bytesToLong(byte[] bytes)
    {
        long l = 0;
        l |= (bytes[0] & 0xFFL) << 56;
        l |= (bytes[1] & 0xFFL) << 48;
        l |= (bytes[2] & 0xFFL) << 40;
        l |= (bytes[3] & 0xFFL) << 32;
        l |= (bytes[4] & 0xFFL) << 24;
        l |= (bytes[5] & 0xFFL) << 16;
        l |= (bytes[6] & 0xFFL) << 8;
        l |= (bytes[7] & 0xFFL);
        return l;
    }

    /**
     * Returns a byte array containing the bytes of the given short in big endian order.
     *
     * @param i - The short to convert.
     *
     * @return A byte array containing the 2 bytes of the given short in big endian order.
     */
    public static byte[] shortToBytes(short i)
    {
        return new byte[] { (byte) (i >> 8), (byte) (i & 0xFF) };
    }

    /**
     * Returns a byte array containing the bytes of the given integer in big endian order.
     *
     * @param i - The integer to convert.
     *
     * @return A byte array containing the 4 bytes of the given integer in big endian order.
     */
    public static byte[] intToBytes(int i)
    {
        return new byte[] { (byte) (i >> 24), (byte) (i >> 16 & 0xFF), (byte) (i >> 8 & 0xFF), (byte) (i & 0xFF) };
    }

    /**
     * Returns a byte array containing the bytes of the given long in big endian order.
     *
     * @param l - The long to convert
     *
     * @return A byte array containing the 8 bytes of the given long in big endian order.
     */
    public static byte[] longToBytes(long l)
    {
        return new byte[] { (byte) (l >> 56), (byte) (l >> 48), (byte) (l >> 40), (byte) (l >> 32), (byte) (l >> 24),
                (byte) (l >> 16 & 0xFF), (byte) (l >> 8 & 0xFF), (byte) (l & 0xFF) };
    }

    /**
     * Concatenates two byte arrays
     *
     * @param a - The first byte[]
     * @param b - The second byte[]
     *
     * @return A byte[] containing the combined data from a and b, in that order
     */
    public static byte[] concatenateByteArrays (byte[] a, byte[] b)
    {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    /**
     * Concatenates three byte arrays
     *
     * @param a - The first byte[]
     * @param b - The second byte[]
     * @param c - The third byte[]
     *
     * @return A byte[] containing the combined data from a, b, and c, in that order
     */
    public static byte[] concatenateByteArrays (byte[] a, byte[] b, byte[] c)
    {
        byte[] d = new byte[a.length + b.length + c.length];
        System.arraycopy(a, 0, d, 0, a.length);
        System.arraycopy(b, 0, d, a.length, b.length);
        System.arraycopy(c, 0, d, a.length + b.length, c.length);
        return d;
    }

    /**
     * Concatenates four byte arrays
     *
     * @param a - The first byte[]
     * @param b - The second byte[]
     * @param c - The third byte[]
     * @param d - The fourth byte[]
     *
     * @return A byte[] containing the combined data from a, b, c, and d, in that order
     */
    public static byte[] concatenateByteArrays (byte[] a, byte[] b, byte[] c, byte[] d)
    {
        byte[] e = new byte[a.length + b.length + c.length + d.length];
        System.arraycopy(a, 0, e, 0, a.length);
        System.arraycopy(b, 0, e, a.length, b.length);
        System.arraycopy(c, 0, e, a.length + b.length, c.length);
        System.arraycopy(d, 0, e, a.length + b.length + c.length, d.length);
        return e;
    }

    /**
     * Concatenates five byte arrays
     *
     * @param a - The first byte[]
     * @param b - The second byte[]
     * @param c - The third byte[]
     * @param d - The fourth byte[]
     * @param e - The fifth byte[]
     *
     * @return A byte[] containing the combined data from a, b, c, d, and e, in that order
     */
    public static byte[] concatenateByteArrays (byte[] a, byte[] b, byte[] c, byte[] d, byte[] e)
    {
        byte[] f = new byte[a.length + b.length + c.length + d.length + e.length];
        System.arraycopy(a, 0, f, 0, a.length);
        System.arraycopy(b, 0, f, a.length, b.length);
        System.arraycopy(c, 0, f, a.length + b.length, c.length);
        System.arraycopy(d, 0, f, a.length + b.length + c.length, d.length);
        System.arraycopy(e, 0, f, a.length + b.length + c.length + d.length, e.length);
        return f;
    }

    /**
     * Returns a positive BigInteger from the given bytes. (Big endian)
     *
     * @param data - A byte[] containing the data to convert
     * @param start - An int representing the position of the first byte to copy.
     * @param length - An int representing the number of bytes to process.
     *
     * @return A BigInteger created from the given bytes.
     */
    public static BigInteger getUnsignedBigInteger(byte[] data, int start, int length)
    {
        if (length == 0)
        {
            return BigInteger.ZERO;
        }

        byte[] value = new byte[length + 1];
        System.arraycopy(data, start, value, 1, length);

        return new BigInteger(value);
    }

    /**
     * Returns an unsigned byte[] representation of the given big integer.
     *
     * @param number - The BigInteger. Must be >= 0
     * @param length - An int representing the maximum length
     *
     * @return A byte[] containing the last bytes of the given big integer, filled with zeros if necessary.
     */
    public static byte[] getUnsignedBytes(BigInteger number, int length)
    {
        byte[] value = number.toByteArray();

        if (value.length > length + 1)
        {
            throw new IllegalArgumentException
                    ("The given BigInteger does not fit into a byte array with the given length: " + value.length
                            + " > " + length);
        }

        byte[] result = new byte[length];

        int i = value.length == length + 1 ? 1 : 0;
        for (; i < value.length; i++)
        {
            result[i + length - value.length] = value[i];
        }

        return result;
    }

    /**
     * Removes a specified range of bytes from the middle of a byte[], then
     * returns the remaining bytes from the array.
     *
     * @param original - The original byte array
     * @param start - The starting index of the range of bytes to be removed
     * @param end - The end index of the range of bytes to be removed
     *
     * @return A byte[] containing the remaining bytes from the array
     */
    public static byte[] removeBytesFromArray(byte[] original, int start, int end)
    {
        byte[] firstPart = ArrayCopier.copyOfRange(original, 0, start);
        byte[] secondPart = ArrayCopier.copyOfRange(original, end, original.length);
        return concatenateByteArrays(firstPart, secondPart);
    }

    /**
     * Strips any leading zero bytes from a given byte[].
     *
     * @param input - The input byte[]
     *
     * @return A byte[] with any leading zeros removed
     */
    public static byte[] stripLeadingZeros(byte[] input)
    {
        while (input[0] == (byte) 0)
        {
            input = ArrayCopier.copyOfRange(input, 1, input.length);
        }
        return input;
    }

    /**
     * Takes a byte[] and pads it with leading zeros until its total
     * length is equal to the 'finalLength' parameter. <br><br>
     *
     * If the length of the supplied byte[] is already equal to or greater
     * than the supplied 'finalLength' value then the byte[] will be returned unchanged.
     *
     * @param input - The byte[] to be padded with leading zeros
     * @param finalLength - The desired total length of the byte[] once it
     * has been padded with leading zeros
     *
     * @return The byte[] with leading zeros added
     */
    public static byte[] padWithLeadingZeros(byte[] input, int finalLength)
    {
        while (input.length < finalLength)
        {
            byte[] zeroByte = new byte[]{0};
            input = ByteUtils.concatenateByteArrays(zeroByte, input);
        }
        return input;
    }
}