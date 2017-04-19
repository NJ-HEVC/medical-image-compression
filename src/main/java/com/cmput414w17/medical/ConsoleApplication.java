package com.cmput414w17.medical;

import java.io.File;

import org.apache.log4j.BasicConfigurator;

import com.cmput414w17.medical.image.UnsupportedFileTypeException;

public class ConsoleApplication {
    static {
        BasicConfigurator.configure();
    }

    public static void main(String[] args) throws InterruptedException, UnsupportedFileTypeException {
        File input = new File("input");
        File output = new File("output");
        if (args.length > 0) {
            // Assume the argument is a path to a folder
            input = new File(args[0]);

            if (args.length > 1) {
                output = new File(args[1]);
            }
        }

        MedicalImageProcessor processor = new MedicalImageProcessor(input, output);
        processor.process();
    }

}
