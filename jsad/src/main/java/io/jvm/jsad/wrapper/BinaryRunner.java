//package io.jvm.jsad.wrapper;
//
//import io.jvm.jsad.Runner;
//import io.jvm.jsad.RunnerOutput;
//
//import java.io.File;
//import java.io.IOException;
//
//public abstract class BinaryRunner implements BinaryProcessor {
//    private final Runner runner;
//
//    public BinaryProcessor(final Runner runner) {
//        this.runner = runner;
//    }
//
//    public byte[] process(final byte[] array) throws IOException {
//        RunnerOutput out = runner.exec(processParams(), array);
//        if (out.code != 0)
//            throw new IOException("Process ended with code " + out.code + "\n"
//                    + new String(out.error));
//        if (out.error.length != 0)
//            throw new IOException(new String(out.error));
//        return out.output;
//    }
//}
