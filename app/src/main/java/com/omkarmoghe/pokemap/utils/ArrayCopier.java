package com.omkarmoghe.pokemap.utils;

/**
 * A utility class required because the standard Java method Arrays.copyOfRange is
 * not available in Android API 8 or earlier
 *
 * @author Jonathan Coe
 */

public final class ArrayCopier
{
    private ArrayCopier()
    {
        // The constructor of this class is private in order to prevent the class being instantiated
    }

    /**
     * Copies a specified range of bytes from a given byte array.
     *
     * @param from - The byte array to be copied from
     * @param start - The starting position within the byte aray for the range of data to be copied
     * @param end - The end position within the byte aray for the range of data to be copied
     * @return A new byte array containing the copied data
     */
    public static byte[] copyOfRange(byte[] from, int start, int end)
    {
        int length = end - start;
        byte[] result = new byte[length];
        System.arraycopy(from, start, result, 0, length);
        return result;
    }

    /**
     * Copies a specified number of bytes from a given byte array, starting at the
     * beginning of the array provided.
     *
     * @param from - The byte array to be copied from
     * @param length - The number of bytes to be copied
     * @return A new byte array containing the copied data
     */
    public static byte[] copyOf(byte[] from, int length)
    {
        byte[] result = new byte[length];
        System.arraycopy(from, 0, result, 0, length);
        return result;
    }
}
