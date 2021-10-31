package com.github.n9.mch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleLogger {


    public static void info(String... elements){
        for(String str : elements) {
            System.out.println(str);
        }
    }
}