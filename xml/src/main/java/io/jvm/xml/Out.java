package io.jvm.xml;

import java.util.Stack;

public class Out {
    private final StringBuilder sb;

    private final String newline;
    private final String padding;

    public Out(final String newline, final String padding, final String root) {
        sb = new StringBuilder();
        depth = new Stack<String>();

        this.newline = newline;
        this.padding = padding;

        start(root);
    }

    public Out(final String root) {
        this("\n", "\t", root);
    }

    // ---------------------------------------------------------------------

    private final Stack<String> depth;

    public Out newline() {
        sb.append(newline);
        return this;
    }

    public Out pad() {
        for (int i = depth.size(); i > 1; i--) sb.append(padding);
        return this;
    }


    // ---------------------------------------------------------------------

    public Out open(final String name) {
        depth.add(name);
        pad();
        sb.append('<').append(name).append('>');
        return this;
    }

    public Out attribute(final String key, final String value) {
        sb.setLength(sb.length() - 1);
        sb.append(' ').append(key).append("=\"").append(value).append("\">");
        return this;
    }

    public Out close() {
        sb.append("</").append(depth.pop()).append('>');
        return newline();
    }

    // ---------------------------------------------------------------------

    public Out node(final String name, final Object text) {
        return open(name).text(text).close();
    }

    public Out empty(final String name) {
        open(name);
        sb.insert(sb.length() - 1, '/');
        depth.pop();
        return newline();
    }

    // ---------------------------------------------------------------------

    public Out start(final String name) {
        return open(name).newline();
    }

    public Out text(final Object text) {
        for (final char ch: text.toString().toCharArray()) {
            if (ch == '&') sb.append("&amp;");
            else if (ch == '<') sb.append("&lt;");
            else if (ch == '>') sb.append("&gt;");
            else sb.append(ch);
        }
        return this;
    }

    public Out raw(final Object raw) {
        sb.append(raw);
        return this;
    }

    public Out end() {
        return pad().close();
    }

    // ---------------------------------------------------------------------

    @Override
    public String toString() {
        while (!depth.isEmpty()) end();
        return sb.toString();
    }
}
