package io.jvm.jsad;

import io.jvm.jsad.wrapper.Defluff;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Kekec {
    public static final void main(final String[] args) throws IOException {

        final Runner r = new Runner();
        final RunnerOutput ro = r.exec(new String[] {"ls"}, new byte[0]);

        System.out.println(ro.code);
        System.out.println(new String(ro.error, "cp1250"));
        System.out.println(new String(ro.output, "cp1250"));

        System.out.println("KEKE!");


        RandomAccessFile f = new RandomAccessFile("/home/dinko/workspace-code/test.jar", "r");
        byte[] b = new byte[(int)f.length()];
        f.read(b);

        Defluff dfluff = new Defluff(new File("/home/dinko/workspace-code/"), 1000);

        FileOutputStream fos = new FileOutputStream("/home/dinko/workspace-code/testDeFluffed.jar");
        fos.write(dfluff.process(b));
        fos.close();
    }

}
