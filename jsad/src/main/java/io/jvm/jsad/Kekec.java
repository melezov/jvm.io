package io.jvm.jsad;

import java.io.File;
import java.io.IOException;

public class Kekec {
    public static final void main(final String[] args) throws IOException {
        final Runner r = new Runner();
        final Runner.Output ro = r.exec(new String[] { "cmd", "/c", "R:\\defluff", "<", "R:\\a.zip", ">", "R:\\b.zip", "2>", "R:\\c.zip"}, new byte[0], new File("R:/"));

        System.out.println(ro.code);
        System.out.println(new String(ro.error, "cp1250"));
        System.out.println(new String(ro.output, "cp1250"));

        System.out.println("KEKE!");

        r.finalize();
/*

        RandomAccessFile f = new RandomAccessFile("/home/dinko/workspace-code/test.jar", "r");
        byte[] b = new byte[(int)f.length()];
        f.read(b);

        Defluff dfluff = new Defluff(new File("/home/dinko/workspace-code/"), 1000);

        FileOutputStream fos = new FileOutputStream("/home/dinko/workspace-code/testDeFluffed.jar");
        fos.write(dfluff.process(b));
        fos.close();
*/
    }

}
