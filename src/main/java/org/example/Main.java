package org.example;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import pl.setblack.wth.forkjoin.Investigate;
import pl.setblack.wth.forkjoin.Option;

public class Main {
    public static void main(String[] args) {

        System.out.println(System.getProperty("java.version"));
        Investigate.detectCommonForkJoinPoolUse(Investigate.Actions.failWhenPackagesInStacktrace(List.of("org.example")), Option.DontFailOnInstall, Option.WarnToSlf4j);
        ForkJoinPool.commonPool().execute(() -> System.out.println("Hello, World from Task"));
        System.out.println("End of main");
    }
}
