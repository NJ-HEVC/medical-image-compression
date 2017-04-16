package com.cmput414w17.medical;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestBPGImageEncoding.class, TestDicomImageConversion.class, TestImageProcessing.class,
		TestImageConversion.class })
public class AllTests {

}
