package ioto.cmp.service.sms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.HashMap;

public class Gsm7BitCharset extends Charset {

    private static final Logger logger = LoggerFactory.getLogger(Gsm7BitCharset.class);

    private boolean debug = false;

    protected static HashMap<String, Byte> defaultEncodeMap = new HashMap<>();
    protected static HashMap<Byte, String> defaultDecodeMap = new HashMap<>();
    protected static HashMap<String, Byte> extEncodeMap     = new HashMap<>();
    protected static HashMap<Byte, String> extDecodeMap     = new HashMap<>();

    public static final Object[][] GSM_CHARACTERS = {
            { "@",      (byte) 0x00 },
            { "£",      (byte) 0x01 },
            { "$",      (byte) 0x02 },
            { "¥",      (byte) 0x03 },
            { "è",      (byte) 0x04 },
            { "é",      (byte) 0x05 },
            { "ù",      (byte) 0x06 },
            { "ì",      (byte) 0x07 },
            { "ò",      (byte) 0x08 },
            { "Ç",      (byte) 0x09 },
            { "\n",     (byte) 0x0a },
            { "Ø",      (byte) 0x0b },
            { "ø",      (byte) 0x0c },
            { "\r",     (byte) 0x0d },
            { "Å",      (byte) 0x0e },
            { "å",      (byte) 0x0f },
            { "\u0394", (byte) 0x10 },
            { "_",      (byte) 0x11 },
            { "\u03A6", (byte) 0x12 },
            { "\u0393", (byte) 0x13 },
            { "\u039B", (byte) 0x14 },
            { "\u03A9", (byte) 0x15 },
            { "\u03A0", (byte) 0x16 },
            { "\u03A8", (byte) 0x17 },
            { "\u03A3", (byte) 0x18 },
            { "\u0398", (byte) 0x19 },
            { "\u039E", (byte) 0x1a },
            { "\u001B", (byte) 0x1b }, // 27 is Escape character
            { "Æ",      (byte) 0x1c },
            { "æ",      (byte) 0x1d },
            { "ß",      (byte) 0x1e },
            { "É",      (byte) 0x1f },
            { "\u0020", (byte) 0x20 },
            { "!",      (byte) 0x21 },
            { "\"",     (byte) 0x22 },
            { "#",      (byte) 0x23 },
            { "¤",      (byte) 0x24 },
            { "%",      (byte) 0x25 },
            { "&",      (byte) 0x26 },
            { "'",      (byte) 0x27 },
            { "(",      (byte) 0x28 },
            { ")",      (byte) 0x29 },
            { "*",      (byte) 0x2a },
            { "+",      (byte) 0x2b },
            { ",",      (byte) 0x2c },
            { "-",      (byte) 0x2d },
            { ".",      (byte) 0x2e },
            { "/",      (byte) 0x2f },
            { "0",      (byte) 0x30 },
            { "1",      (byte) 0x31 },
            { "2",      (byte) 0x32 },
            { "3",      (byte) 0x33 },
            { "4",      (byte) 0x34 },
            { "5",      (byte) 0x35 },
            { "6",      (byte) 0x36 },
            { "7",      (byte) 0x37 },
            { "8",      (byte) 0x38 },
            { "9",      (byte) 0x39 },
            { ":",      (byte) 0x3a },
            { ";",      (byte) 0x3b },
            { "<",      (byte) 0x3c },
            { "=",      (byte) 0x3d },
            { ">",      (byte) 0x3e },
            { "?",      (byte) 0x3f },
            { "¡",      (byte) 0x40 },
            { "A",      (byte) 0x41 },
            { "B",      (byte) 0x42 },
            { "C",      (byte) 0x43 },
            { "D",      (byte) 0x44 },
            { "E",      (byte) 0x45 },
            { "F",      (byte) 0x46 },
            { "G",      (byte) 0x47 },
            { "H",      (byte) 0x48 },
            { "I",      (byte) 0x49 },
            { "J",      (byte) 0x4a },
            { "K",      (byte) 0x4b },
            { "L",      (byte) 0x4c },
            { "M",      (byte) 0x4d },
            { "N",      (byte) 0x4e },
            { "O",      (byte) 0x4f },
            { "P",      (byte) 0x50 },
            { "Q",      (byte) 0x51 },
            { "R",      (byte) 0x52 },
            { "S",      (byte) 0x53 },
            { "T",      (byte) 0x54 },
            { "U",      (byte) 0x55 },
            { "V",      (byte) 0x56 },
            { "W",      (byte) 0x57 },
            { "X",      (byte) 0x58 },
            { "Y",      (byte) 0x59 },
            { "Z",      (byte) 0x5a },
            { "Ä",      (byte) 0x5b },
            { "Ö",      (byte) 0x5c },
            { "Ñ",      (byte) 0x5d },
            { "Ü",      (byte) 0x5e },
            { "§",      (byte) 0x5f },
            { "¿",      (byte) 0x60 },
            { "a",      (byte) 0x61 },
            { "b",      (byte) 0x62 },
            { "c",      (byte) 0x63 },
            { "d",      (byte) 0x64 },
            { "e",      (byte) 0x65 },
            { "f",      (byte) 0x66 },
            { "g",      (byte) 0x67 },
            { "h",      (byte) 0x68 },
            { "i",      (byte) 0x69 },
            { "j",      (byte) 0x6a },
            { "k",      (byte) 0x6b },
            { "l",      (byte) 0x6c },
            { "m",      (byte) 0x6d },
            { "n",      (byte) 0x6e },
            { "o",      (byte) 0x6f },
            { "p",      (byte) 0x70 },
            { "q",      (byte) 0x71 },
            { "r",      (byte) 0x72 },
            { "s",      (byte) 0x73 },
            { "t",      (byte) 0x74 },
            { "u",      (byte) 0x75 },
            { "v",      (byte) 0x76 },
            { "w",      (byte) 0x77 },
            { "x",      (byte) 0x78 },
            { "y",      (byte) 0x79 },
            { "z",      (byte) 0x7a },
            { "ä",      (byte) 0x7b },
            { "ö",      (byte) 0x7c },
            { "ñ",      (byte) 0x7d },
            { "ü",      (byte) 0x7e },
            { "à",      (byte) 0x7f }
    };

    public static final Object[][] GSM_EXTENSION_CHARACTERS = {
            { "\n",     (byte) 0x0a },
            { "^",      (byte) 0x14 },
            { " ",      (byte) 0x1b }, // reserved for future extensions
            { "{",      (byte) 0x28 },
            { "}",      (byte) 0x29 },
            { "\\",     (byte) 0x2f },
            { "[",      (byte) 0x3c },
            { "~",      (byte) 0x3d },
            { "]",      (byte) 0x3e },
            { "|",      (byte) 0x40 },
            { "€",      (byte) 0x65 }
    };

    static {
        // default alphabet
        int len = GSM_CHARACTERS.length;
        for (int i = 0; i < len; i++) {
            Object[] map = GSM_CHARACTERS[i];
            defaultEncodeMap.put((String) map[0], (Byte) map[1]);
            defaultDecodeMap.put((Byte) map[1], (String) map[0]);
        }

        // extended alphabet
        len = GSM_EXTENSION_CHARACTERS.length;
        for (int i = 0; i < len; i++) {
            Object[] map = GSM_EXTENSION_CHARACTERS[i];
            extEncodeMap.put((String) map[0], (Byte) map[1]);
            extDecodeMap.put((Byte) map[1], (String) map[0]);
        }
    }

    public static HashMap<String, Byte> getDefaultEncodeMap() {
        return defaultEncodeMap;
    }

    public static HashMap<String, Byte> getExtEncodeMap() {
        return extEncodeMap;
    }

    protected Gsm7BitCharset(String canonical, String[] aliases) {
        super(canonical, aliases);
    }

    public CharsetEncoder newEncoder() {
        return new Gsm7BitEncoder(this);
    }

    public CharsetDecoder newDecoder() {
        return new Gsm7BitDecoder(this);
    }

    public boolean contains(Charset cs) {
        return (false);
    }

    private class Gsm7BitEncoder extends CharsetEncoder {

        Gsm7BitEncoder(Charset cs) {
            super(cs, 1, 2);
        }

        protected CoderResult encodeLoop(CharBuffer cb, ByteBuffer bb) {
            CoderResult cr = CoderResult.UNDERFLOW;

            while (cb.hasRemaining()) {
                if (!bb.hasRemaining()) {
                    cr = CoderResult.OVERFLOW;
                    break;
                }
                char ch = cb.get();

                // first check the default alphabet
                Byte b = defaultEncodeMap.get("" + ch);
                if(debug)
                    logger.debug("Encoding ch " + ch + " to byte " + b);
                if (b != null) {
                    bb.put(b);
                } else {
                    // check extended alphabet
                    b = extEncodeMap.get("" + ch);
                    if(debug)
                        logger.debug("Trying extended map to encode ch " + ch + " to byte " + b);
                    if (b != null) {
                        // since the extended character set takes two bytes
                        // we have to check that there is enough space left
                        if (bb.remaining() < 2) {
                            // go back one step
                            cb.position(cb.position() - 1);
                            cr = CoderResult.OVERFLOW;
                            break;
                        }
                        // all ok, add it to the buffer
                        bb.put((byte) 0x1b);
                        bb.put(b);
                    } else {
                        // no match found, send a ?
                        b = (byte) 0x3F;
                        bb.put(b);
                    }
                }
            }
            return cr;
        }
    }

    private class Gsm7BitDecoder extends CharsetDecoder {

        Gsm7BitDecoder(Charset cs) {
            super(cs, 1, 1);
        }

        protected CoderResult decodeLoop(ByteBuffer bb, CharBuffer cb) {
            CoderResult cr = CoderResult.UNDERFLOW;

            while (bb.hasRemaining()) {
                if (!cb.hasRemaining()) {
                    cr = CoderResult.OVERFLOW;
                    break;
                }
                byte b = bb.get();

                // first check the default alphabet
                if(debug)
                    logger.debug("Looking up byte " + b);
                String s = defaultDecodeMap.get(b);
                if (s != null) {
                    char ch = s.charAt(0);
                    if (ch != '\u001B') {
                        if(debug)
                            logger.debug("Found string " + s);
                        cb.put(ch);
                    } else {
                        if(debug)
                            logger.debug("Found escape character");
                        // check the extended alphabet
                        if (bb.hasRemaining()) {
                            b = bb.get();
                            s = extDecodeMap.get(b);
                            if (s != null) {
                                if(debug)
                                    logger.debug("Found extended string " + s);
                                ch = s.charAt(0);
                                cb.put(ch);
                            } else {
                                cb.put('?');
                            }
                        }
                    }
                } else {
                    cb.put('?');
                }
            }
            return cr;
        }
    }
}