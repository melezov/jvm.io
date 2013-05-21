package io.jvm.jsad;

import java.io.IOException;

public class Kekec {
    public static final void main(final String[] args) throws IOException {

        final Runner r = new Runner();
        final RunnerOutput ro = r.exec(new String[] {"cmd", "/c", "dir", "d:\\"}, new byte[0]);

        System.out.println(ro.code);
        System.out.println(new String(ro.error, "cp1250"));
        System.out.println(new String(ro.output, "cp1250"));

        System.out.println("KEKE!");
    }

}
