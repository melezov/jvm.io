package io.jvm.json;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;

public class JsonReader {
    private final Reader reader;

    public JsonReader(final Reader reader) {
        this.reader = reader;
    }

    private boolean _endOfStream;
    private char _last;
    private boolean _lastValid;

    /**
     * The next character read from the stream
     * @return The character read
     * @throws IOException The end of the stream was reached, or an I/O error has occured
     */
    public char next() throws IOException {
        if (_endOfStream) throw new IOException("Could not read past the end of stream");

        final int next = reader.read();
        //System.out.println("READ: " + (char) next + " (" + next + ")");
        if (next == -1) {
            _lastValid = false;
            _endOfStream = true;
            throw new IOException("Unexpected end of input stream");
        }

        _lastValid = true;
        return _last = (char) next;
    }

    /**
     * Consumes the next character from the streem and returns it's {@code int} value;
     * @return The {@code int} value of the next character from the stream
     * @throws IOException The end of the stream was reached, or an I/O error has occured
     */
    public int peek() throws IOException {
        if (_endOfStream) throw new IOException("Could not peek past the end of stream");

        final int peek = reader.read();        
        //System.out.println("PEEK: " + (char) peek + " (" + peek + ")");

        if (peek == -1) {
            _lastValid = false;
            _endOfStream = true;
        }
        else {
            _lastValid = true;
            _last = (char) peek;
        }
        return peek;
    }

    /**
     * The value of the last character consumed from the stream. 
     * @return The last character consumed from the stream.
     * @throws IOException If the last character consumed is invalid, or the end of the stream is reached
     */
    public char last() throws IOException {
        if (!_lastValid) {
            if (_endOfStream)
                throw new IOException("Could not reuse last() character because the stream has ended!");
            throw new IOException("Could not reuse last() character because it is not valid; use read() or next() instead!");
        }
        return _last;
    }

    /**
     * Reads a character from the stream.
     * @return The value of the last character read, if valid, otherwise the value of the next valid character.
     * @throws IOException The end of the stream was reached, or an I/O error has occured
     */
    public char read() throws IOException {
        return _lastValid ? _last : next();
    }

    /**
     * Invalidates the last character read from the stream.
     */
    public void invalidate() {    	
        _lastValid = false;
    }

    /**
     * Consumes whitespace characters in the stream (if any) and discards them
     * @throws IOException The end of the stream was reached, or an I/O error has occured
     */
    public void consumeWhitespaces() throws IOException{
    	while(Character.isWhitespace(read()));
    }
    
    /**
     * Asserts that the method {@link JsonReader#next()} returns <code>expected</code>
     * @param expected The expected value of the character
     * @throws IOException The next character read has a value different than <code>expected</code>
     * @throws IOException The end of the stream was reached, or an I/O error has occured
     */
    public void assertNext(final char expected) throws IOException {
        if (next() != expected) throw new IOException("Could not parse token, expected '" + expected + "', got '" + last() + "'");
        invalidate();
    }

    /**
     * Assert that the method {@link JsonReader#last()} returns <code>expected</code>
     * @param expected The expected value of the character
     * @throws IOException The last character read from the stream has a value different than <code>expected</code>
     * @throws IOException The last character read is invalid, or the end of the stream has been reached
     */
    public void assertLast(final char expected) throws IOException {
        if (last() != expected) throw new IOException("Could not parse token, expected '" + expected + "', got '" + last() + "'");
        invalidate();
    }

    /**
     * Assert that the method {@link JsonReader#read()} returns <code>expected</code>
     * @param expected The expected value of the character
     * @throws IOException The character has a value different than <code>expected</code>
     * @throws IOException The last character read is invalid, or the end of the stream has been reached
     */
    public void assertRead(final char expected) throws IOException {
        if (_lastValid) assertLast(expected); else assertNext(expected);
    }

    /**
     * Returns {@code int} value of the next hex digit read, or fails if the next character is not a hex digit.
     * @return The {@code int} value of the next hex digit read
     * @throws IOException The next character is not a hex digit
     * @throws IOException The end of the stream was reached, or an I/O error has occured
     */
    public int nextHexDigit() throws IOException {
        final char next = next();
        if (next >= '0' && next <= '9') return next - 0x30;
        if (next >= 'A' && next <= 'F') return next - 0x37;
        if (next >= 'a' && next <= 'f') return next - 0x57;
        throw new IOException("Could not parse unicode escape, expected a hexadecimal digit, got '" + next + "'");
    }      
    
    /**
     * Reads a {@code null} value from the stream
     * @return {@code (T) null}
     * @throws IOException The token read from the stream was not 'null'
     * @throws IOException The end of the stream was reached, or an I/O error has occured
     */
    public <T> T readNull() throws IOException {
        if (read() == 'n' && next() == 'u' && next() == 'l' && next() == 'l') {
            invalidate();
            return (T) null;
        }

        throw new IOException("Could not parse token, expected 'null'");
    }

    /**
     * Reads a {@code true} value from the stream
     * @return The {@code boolean} literal {@code true}
     * @throws IOException The token read from the stream was not 'true'
     * @throws IOException The end of the stream was reached, or an I/O error has occured
     */
    public boolean readTrue() throws IOException {
        if (read() == 't' && next() == 'r' && next() == 'u' && next() == 'e') {
            invalidate();
            return true;
        }

        throw new IOException("Could not parse token, expected 'true'");
    }

    /**
     * Reads a {@code false} value from the stream
     * @return The {@code boolean} literal {@code false}
     * @throws IOException The token read from the stream was not 'false'
     * @throws IOException The end of the stream was reached, or an I/O error has occured
     */
    public boolean readFalse() throws IOException {
        if (read() == 'f' && next() == 'a' && next() == 'l' && next() == 's' && next() == 'e') {
            invalidate();
            return false;
        }

        throw new IOException("Could not parse token, expected 'false'");
    }

    /**
     * Reads a raw number from the stream, and returns it as a {@link java.lang.StringBuilder}
     * @param sb The {@link StringBuilder} to read the number into
     * @return the populated {@link StringBuilder}
     * @throws IOException The number was not in a correct format
     * @throws IOException The end of the stream was reached, or an I/O error has occured
     */
    public StringBuilder readRawNumber(final StringBuilder sb) throws IOException {
        char ch = read();
        if (ch == '-') {
            sb.append(ch);
            ch = next();
        }

        final int length = sb.length();
        if (ch == '0') {
            sb.append(ch);
            final int chp = peek();
            if (chp == -1) return sb;
            ch = (char) chp;
        } else if (ch >= '1' && ch <= '9') {
            sb.append(ch);
            for (;;) {
                final int chp = peek();
                if (chp == -1) return sb;

                ch = (char) chp;
                if (ch < '0' || ch > '9') break;
                sb.append(ch);
            }
        }

        if (ch == '.') {
            sb.append(ch);
            ch = next();

            if (ch < '0' || ch > '9') throw new IOException("Expected decimal after floating point, got: " + ch);

            sb.append(ch);
            for (;;) {
                final int chp = peek();
                if (chp == -1) return sb;

                ch = (char) chp;
                if (ch < '0' || ch > '9') break;
                sb.append(ch);
            }
        }

        if (ch == 'e' || ch == 'E') {
            sb.append(ch);
            ch = next();

            if (ch == '-' || ch == '+') {
                sb.append(ch);
                ch = next();
            }

            if (ch < '0' || ch > '9') throw new IOException("Expected decimal after exponent sign, got: " + ch);

            sb.append(ch);
            for (;;) {
                final int chp = peek();
                if (chp == -1) return sb;

                ch = (char) chp;
                if (ch < '0' || ch > '9') break;
                sb.append(ch);
            }
        }

        if (sb.length() == length)
            throw new IOException("Could not parse number - no leading digits found!");

        return sb;
    }    
        
    @SuppressWarnings("unchecked")
    public <T> T readOpt(final JsonDeserializer<T> deserializer) throws IOException {
        return read() == 'n' ? (T) readNull() : deserializer.fromJson(this);
    }

    public <T> T[] readArray(final JsonDeserializer<T> deserializer) throws IOException {
        return readList(deserializer).toArray(deserializer.getZeroArray());
    }

    public <T> T[] readArrayOpt(final JsonDeserializer<T> deserializer) throws IOException {
        return readListOpt(deserializer).toArray(deserializer.getZeroArray());
    }

    @SuppressWarnings("unchecked")
    public <T> T[] readOptArray(final JsonDeserializer<T> deserializer) throws IOException {
        return read() == 'n' ? (T[]) readNull() : readArray(deserializer);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] readOptArrayOpt(final JsonDeserializer<T> deserializer) throws IOException {
        return read() == 'n' ? (T[]) readNull() : readArrayOpt(deserializer);
    }
  
    public <T> ArrayList<T> readList(final JsonDeserializer<T> deserializer) throws IOException {
        assertRead('[');
        consumeWhitespaces();
        
        final ArrayList<T> values = new ArrayList<T>();
        final StringBuilder sb = new StringBuilder();
        boolean needComma = false;
        while (read() != ']') {
        	consumeWhitespaces();
            if (needComma) {
                assertLast(',');
                sb.setLength(0);
                next();
            }

            final T value = deserializer.fromJson(this);
            values.add(value);
            needComma = true;
        }
        invalidate();

        return values;
    }

    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> readListOpt(final JsonDeserializer<T> deserializer) throws IOException {
        assertRead('[');

        final ArrayList<T> values = new ArrayList<T>();
        final StringBuilder sb = new StringBuilder();
        boolean needComma = false;
        while (read() != ']') {
            if (needComma) {
                assertLast(',');
                sb.setLength(0);
                next();
            }

            final T value = read() == 'n'
                    ? (T) readNull()
                    : deserializer.fromJson(this);
            values.add(value);
            needComma = true;
        }
        invalidate();

        return values;
    }

    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> readOptList(final JsonDeserializer<T> deserializer) throws IOException {
        return read() == 'n' ? (ArrayList<T>) readNull() : readList(deserializer);
    }

    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> readOptListOpt(final JsonDeserializer<T> deserializer) throws IOException {
        return read() == 'n' ? (ArrayList<T>) readNull() : readListOpt(deserializer);
    }

    public <T> HashSet<T> readSet(final JsonDeserializer<T> deserializer) throws IOException {
        assertRead('[');

        final HashSet<T> values = new HashSet<T>();
        final StringBuilder sb = new StringBuilder();
        boolean needComma = false;
        while (read() != ']') {
            if (needComma) {
                assertLast(',');
                sb.setLength(0);
                next();
            }

            final T value = deserializer.fromJson(this);
            values.add(value);
            needComma = true;
        }
        invalidate();

        return values;
    }

    @SuppressWarnings("unchecked")
    public <T> HashSet<T> readSetOpt(final JsonDeserializer<T> deserializer) throws IOException {
        assertRead('[');

        final HashSet<T> values = new HashSet<T>();
        final StringBuilder sb = new StringBuilder();
        boolean needComma = false;
        while (read() != ']') {
            if (needComma) {
                assertLast(',');
                sb.setLength(0);
                next();
            }

            final T value = read() == 'n'
                    ? (T) readNull()
                    : deserializer.fromJson(this);
            values.add(value);
            needComma = true;
        }
        invalidate();

        return values;
    }

    @SuppressWarnings("unchecked")
    public <T> HashSet<T> readOptSet(final JsonDeserializer<T> deserializer) throws IOException {
        return read() == 'n' ? (HashSet<T>) readNull() : readSet(deserializer);
    }

    @SuppressWarnings("unchecked")
    public <T> HashSet<T> readOptSetOpt(final JsonDeserializer<T> deserializer) throws IOException {
        return read() == 'n' ? (HashSet<T>) readNull() : readSetOpt(deserializer);
    }

    public String readString() throws IOException {
        if (read() != '"')
            throw new IOException("Could not parse String, expected '\"', got '" + last() + "'");

        final StringBuilder sb = new StringBuilder();
        while (next() != '"') {
            if (last() == '\\') {
                final char ch = next();
                switch (ch) {
                    case 'b': sb.append('\b'); continue;
                    case 't': sb.append('\t'); continue;
                    case 'n': sb.append('\n'); continue;
                    case 'f': sb.append('\f'); continue;
                    case 'r': sb.append('\r'); continue;
                    case '"':
                    case '/':
                    case '\\': sb.append(ch); continue;
                    case 'u': sb.append(
                            (nextHexDigit() << 12) |
                            (nextHexDigit() <<  8) |
                            (nextHexDigit() <<  4) |
                            nextHexDigit()); continue;
                }

                throw new IOException("Could not parse String, got invalid escape combination '\\" + ch + "'");
            }

            sb.append(last());
        }

        invalidate();
        return sb.toString();
    }    
}

//    private Map<String, Object> readObject(final StringBuilder sb) throws IOException {
//        final Map<String, Object> object = new LinkedHashMap<String, Object>();
//        boolean needComma = false;
//        for(;;) {
//            char objCh = next();
//            if (objCh == '}') {
//                sb.append(objCh);
//                invalidate();
//                return;
//            }
//
//            if (needComma) {
//                if (objCh != ',') {
//                    throw new IOException("Could not parse object: expected comma, got: " + objCh);
//                }
//                sb.append(objCh);
//                objCh = next();
//            }
//
//            if (objCh != '"')
//                throw new IOException("Could not parse object: expected property name string, got: " + objCh);
//            sb.append(objCh);
//            readRawString(sb);
//
//            final char colon = next();
//            sb.append(colon);
//            if (colon != ':')
//                throw new IOException("Could not parse object: expected colon after property name, got: " + colon);
//            invalidate();
//
//            readRaw(sb);
//            needComma = true;
//        }
//    }
//
//    private void readRawArray(final StringBuilder sb) throws IOException {
//        boolean needComma = false;
//        for(;;) {
//            char arrCh = read();
//            if (arrCh == ']') {
//                sb.append(arrCh);
//                return;
//            }
//
//            if (needComma) {
//                if (arrCh != ',') {
//                    throw new IOException("Could not parse array: expected comma, got: " + arrCh);
//                }
//                sb.append(arrCh);
//                arrCh = next();
//            }
//
//            sb.append(arrCh);
//            readRaw(sb);
//            needComma = true;
//        }
//    }
//
//    private void readRaw(final StringBuilder sb) throws IOException {
//        final char ch = read();
//        sb.append(ch);
//        invalidate();
//
//        switch (ch) {
//            case '"': readRawString(sb); return;
//            case '{': readRawObject(sb); return;
//            case '[': readRawArray(sb); return;
//            case 'n': readRawNull(sb); return;
//            case 't': readRawTrue(sb); return;
//            case 'f': readRawFalse(sb); return;
//            default:
//                if (ch == '-' || ch >= '0' && ch <= '9') {
//                    readRawNumber(sb, ch);
//                    return;
//                }
//        }
//
//        throw new IOException("Could not parse token, received: " + ch);
//    }
//
//    public String readRaw() throws IOException {
//        final StringBuilder sb = new StringBuilder();
//        readRaw(sb);
//        return sb.toString();
//    }
