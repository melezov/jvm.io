package io.jvm.xml;

public class Main {
    public static void main(final String[] args) {
        final Out a =
          new Out("root")
            .start("credentials")
                .node("username", "admin")
                .node("password", "qweqwe")
            .end()
            .start("params")
                .empty("None")
                .open("script")
                    .raw("<[CDATA[")
                        .raw("function andFun(a, b) {return a & b;}")
                    .raw("]]>")
                .close()
                .open("Some")
                    .attribute("test1", "dinamo1")
                    .attribute("test2", "dinamo2")
                .close()
            .end()
        .end();


        System.out.println(a);
    }
}
