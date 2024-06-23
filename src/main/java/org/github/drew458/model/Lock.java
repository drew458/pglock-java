package org.github.drew458.model;

import org.springframework.lang.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * The ASCII encoding works as follows:
 * Each ASCII char is 7 bits allowing for 9 chars = 63 bits in total.
 * In order to differentiate between different-length strings with leading '\0',
 * we additionally fill the next bit after the string ends with 0. We then fill any
 * remaining bits with 1. Therefore, the final 64 bit value is 0-9 7-bit characters followed by 0,
 * followed by N=63-(7*length) 1s
 */
public class Lock {

    private static final int ASCII_CHAR_BITS = 7;
    private static final int MAX_ASCII_VALUE = (1 << ASCII_CHAR_BITS) - 1;
    private static final int MAX_ASCII_LENGTH = (8 * Long.BYTES) / ASCII_CHAR_BITS;

    private static final char HASH_STRING_SEPARATOR = ',';
    private static final int HASH_PART_LENGTH = 8, // 8-byte hex numbers
            HASH_STRING_LENGTH = 16, // 2 hashes
            SEPARATED_HASH_STRING_LENGTH = HASH_STRING_LENGTH + 1; // separated by comma

    private enum KeyEncoding {
        INT_64,
        INT_32_PAIR,
        ASCII,
    }

    private long key;

    private KeyEncoding keyEncoding;

    private static boolean hasSeparator = false;

    private LockType lockType = LockType.SESSION_LOCK;

    public Lock(long key) {
        this.key = key;
        this.keyEncoding = KeyEncoding.INT_64;
    }

    public Lock(long key, LockType lockType) {
        this.key = key;
        this.keyEncoding = KeyEncoding.INT_64;
        this.lockType = lockType;
    }

    public Lock(int key1, int key2) {
        this.key = combineKeys(key1, key2);
        this.keyEncoding = KeyEncoding.INT_32_PAIR;
    }

    public Lock(int key1, int key2, LockType lockType) {
        this.key = combineKeys(key1, key2);
        this.keyEncoding = KeyEncoding.INT_32_PAIR;
        this.lockType = lockType;
    }

    /**
     * Constructs a key based on a string as a key.
     * <p>
     * If the string is of the form 16-digit hex or (8-digit hex, 8-digit hex), this will be parsed into numeric keys.
     * <p>
     * If the string is an ascii string with 9 or fewer characters, it will be mapped to a key that does not collide with
     * any other key based on such a string or based on a 32-bit value.
     * <p>
     * Other string names will be rejected unless "allowHashing" is specified, in which case it will be hashed to
     * a 64-bit key value.
     *
     * @param name         The lock key
     * @param allowHashing if True, the string will be hashed if other methods fail
     */
    public Lock(@NonNull String name, boolean allowHashing) throws Exception {

        try {
            this.key = tryEncodeAscii(name);
            this.keyEncoding = KeyEncoding.ASCII;

        } catch (Exception e) {

            try {
                this.key = tryEncodeHashString(name);
                this.keyEncoding = hasSeparator ? KeyEncoding.INT_32_PAIR : KeyEncoding.INT_64;

            } catch (Exception ex) {
                if (allowHashing) {
                    try {
                        this.key = hashString(name);
                        this.keyEncoding = KeyEncoding.INT_64;

                    } catch (Exception exc) {

                        throw new Exception(
                                String.format(
                                        "Name '%s' could not be encoded. Please specify allowHashing or use one of the following formats: " +
                                                "(1) a 0-%s character string using only ASCII characters, " +
                                                "(2) a %s character hex string, such as the result of %s, " +
                                                "(3) a 2-part, %s character string of the form XXXXXXXX%sXXXXXXXX, where the X's are %s hex strings " +
                                                "such as the result of %s). " +
                                                "Note that each unique string provided for formats 1 and 2 will map to a unique hash value," +
                                                "with no collisions across formats. Format 3 strings use the same key space as 2.",
                                        name, MAX_ASCII_LENGTH, HASH_STRING_LENGTH, Integer.MAX_VALUE,
                                        SEPARATED_HASH_STRING_LENGTH, HASH_STRING_SEPARATOR, HASH_PART_LENGTH, HASH_PART_LENGTH)
                        );
                    }
                }
            }
        }
    }

    private static long tryEncodeAscii(String name) throws Exception {

        if (name.length() > MAX_ASCII_LENGTH) {
            throw new Exception("Name is longer than 73, the maximum characters allowed");
        }

        // load the chars into result
        var result = 0L;
        for (var character : name.toCharArray()) {
            if (character > MAX_ASCII_VALUE) {
                throw new Exception("Character value is bigger than the maximum value allowed of 127");
            }

            result = (result << ASCII_CHAR_BITS) | character;
        }

        // add padding
        result <<= 1; // load zero
        for (var i = name.length(); i < MAX_ASCII_LENGTH; ++i) {
            result = (result << ASCII_CHAR_BITS) | MAX_ASCII_VALUE; // load 1s
        }

        return result;
    }

    private static long tryEncodeHashString(String name) throws Exception {

        if (name.length() == SEPARATED_HASH_STRING_LENGTH && name.toCharArray()[HASH_PART_LENGTH] == HASH_STRING_SEPARATOR) {
            hasSeparator = true;
        } else {
            hasSeparator = false;

            if (name.length() != HASH_STRING_LENGTH) {
                throw new Exception();
            }
        }

        return tryParseHashKeys(name);
    }

    private static long tryParseHashKeys(String text) {

        var key1 = Integer.parseInt(text.substring(0, HASH_PART_LENGTH), 16);
        var key2 = Integer.parseInt(text.substring(text.length() - HASH_PART_LENGTH), 16);
        return combineKeys(key1, key2);
    }

    private static long combineKeys(Integer key1, Integer key2) {
        return (long) key1 << (8 * Integer.BYTES / 8) | (key2 & 0xFFFFFFFFL);
    }

    private static List<Integer> splitKey(Long key) {
        return List.of(
                (int) (key >> (8 * Integer.BYTES)),
                (int) (key & Integer.MAX_VALUE)
        );
    }

    /**
     * The hash result from SHA1 is too large, so we have to truncate (recommended practice and does not
     * weaken the hash other than due to using fewer bytes)
     *
     * @param name The string to hash
     * @return The hashed string as long
     */
    private static long hashString(String name) throws NoSuchAlgorithmException {

        var md = MessageDigest.getInstance("SHA-1");
        var hashBytes = md.digest(name.getBytes(StandardCharsets.UTF_8));

        // We don't use BitConverter here because we want to be endianess-agnostic.
        // However, this code replicates that result on little-endian
        var result = 0L;
        for (var i = Long.BYTES - 1; i >= 0; --i) {
            result = (result << 8) | hashBytes[i];
        }
        return result;
    }

    public boolean hasSingleKey() {
        return keyEncoding == KeyEncoding.INT_64;
    }

    public long getKey() {
        return key;
    }

    public List<Integer> getKeys() {
        return splitKey(key);
    }

    public void setKey(long key) {
        this.key = key;
    }

    public LockType getLockType() {
        return lockType;
    }

    public void setLockType(LockType lockType) {
        this.lockType = lockType;
    }
}
