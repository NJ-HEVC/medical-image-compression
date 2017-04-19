package com.cmput414w17.medical;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.cmput414w17.medical.image.ImageUtils;
import com.cmput414w17.medical.image.RegionOfInterestImage;
import com.cmput414w17.medical.image.UnsupportedFileTypeException;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import nu.pattern.OpenCV;

public class MedicalImageProcessor {
    static {
        OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
        BasicConfigurator.configure();
    }

    private final File input;
    private final File output;

    private static final Logger LOGGER = Logger.getLogger(MedicalImageProcessor.class);

    // Progress double field idea by
    // James_D (http://stackoverflow.com/users/2189127/james-d)
    // From http://stackoverflow.com/a/34358090/2557554 and licensed under
    // CC-BY-SA 3.0 (https://creativecommons.org/licenses/by-sa/3.0/deed.en)
    private final ReadOnlyStringWrapper progressFiles = new ReadOnlyStringWrapper("");
    private final ReadOnlyDoubleWrapper progressOverall = new ReadOnlyDoubleWrapper(0.0);

    public MedicalImageProcessor(File input, File output) {
        final String filePathError = "The path, '%s', does not appear to be a directory! Please verify that this path exists and points to a directory.";

        if (input.isDirectory() == false) {
            throw new IllegalArgumentException(String.format(filePathError, input.getAbsolutePath()));
        }

        if (output.isDirectory() == false) {
            throw new IllegalArgumentException(String.format(filePathError, output.getAbsolutePath()));
        }

        this.input = input;
        this.output = output;
    }

    /**
     * Process the images in the file formats "jpeg", "jpeg2000", "png", and
     * "bpg".
     * 
     * @see #process(String...)
     */
    public void process() {
        this.process(null, new String[] { "jpeg", "jpeg2000", "png", "bpg" });
    }

    /**
     * Process the images in the given file formats.
     * 
     * @param task
     *            For task awareness. May be null.
     * @param formats
     *            File formats such as "jpeg", "jpeg2000", "png", and/or "bpg"
     */
    public void process(Task<?> task, String... formats) {
        File[] files = input.listFiles();

        final String roiSuffix = "_roi";
        final String nonRoiSuffix = "_nonroi";

        int numFiles = 0;
        int numFilesAndFormats = 0;

        int numFilesCurrent = 0;
        int numFilesAndFormatsCurrent = 0;

        // Estimate the work first
        for (File dir : files) {
            if (dir.isFile())
                continue;

            numFiles += dir.listFiles().length;

            if (formats.length > 0)
                numFilesAndFormats += dir.listFiles().length * formats.length;
            else
                numFilesAndFormats += dir.listFiles().length;
        }

        for (File dir : files) {
            if (dir.isFile())
                continue;

            for (File file : dir.listFiles()) {
                Mat frame = Imgcodecs.imread(file.getAbsolutePath());

                RegionOfInterestImage roiImage = new RegionOfInterestImage(frame);

                int fileLastDot = file.getName().lastIndexOf('.');

                String roiOutName = file.getName().substring(0, fileLastDot) + roiSuffix
                        + file.getName().substring(fileLastDot);
                String nonRoiOutName = file.getName().substring(0, fileLastDot) + nonRoiSuffix
                        + file.getName().substring(fileLastDot);

                File roiOut = new File(output, dir.getName() + File.separator + roiOutName);
                roiOut.getParentFile().mkdirs();

                File nonRoiOut = new File(output, dir.getName() + File.separator + nonRoiOutName);
                nonRoiOut.getParentFile().mkdirs();

                Imgcodecs.imwrite(roiOut.getAbsolutePath(), roiImage.getRoi());
                Imgcodecs.imwrite(nonRoiOut.getAbsolutePath(), roiImage.getNonRoi());

                try {
                    BufferedImage roiBufferedImage = ImageIO.read(roiOut);
                    BufferedImage nonRoiBufferedImage = ImageIO.read(nonRoiOut);

                    for (String format : formats) {
                        if (task != null && task.isCancelled())
                            return;

                        ++numFilesAndFormatsCurrent;
                        progressOverall.set((double) numFilesAndFormatsCurrent / numFilesAndFormats);

                        File roiFormatImageOut = new File(output,
                                dir.getName() + File.separator + format + File.separator
                                        + FilenameUtils.removeExtension(file.getName()) + roiSuffix + "." + format);
                        File nonRoiFormatImageOut = new File(output,
                                dir.getName() + File.separator + format + File.separator
                                        + FilenameUtils.removeExtension(file.getName()) + nonRoiSuffix + "." + format);

                        roiFormatImageOut.getParentFile().mkdirs();
                        nonRoiFormatImageOut.getParentFile().mkdirs();

                        try {
                            if (format.equals("bpg")) {
                                try {
                                    ImageUtils.convertImageToBpg(roiOut, roiFormatImageOut, true);
                                } catch (InterruptedException | UnsupportedFileTypeException e) {
                                    LOGGER.error(String.format("Could not convert an image %s to BPG! Skipping...",
                                            roiOut.getName()), e);
                                    continue;
                                }

                                try {
                                    ImageUtils.convertImageToBpg(nonRoiOut, nonRoiFormatImageOut, false);
                                } catch (InterruptedException | UnsupportedFileTypeException e) {
                                    LOGGER.error(String.format(
                                            "Could not convert the non-ROI portion of image %s to BPG! Skipping and deleting the ROI portion of the image...",
                                            nonRoiOut.getName()), e);
                                    if (roiOut.exists())
                                        roiOut.delete();
                                    continue;
                                }
                            } else {
                                ImageUtils.convertImageToFormat(roiBufferedImage, roiFormatImageOut, format, 1.0f);
                                ImageUtils.convertImageToFormat(nonRoiBufferedImage, nonRoiFormatImageOut, format,
                                        0.75f);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    ++numFilesCurrent;
                    progressFiles.set(String.format("Files Processed: %d / %d", numFilesCurrent, numFiles));

                    if (formats.length == 0)
                        progressOverall.set((double) numFilesCurrent / numFiles);

                    if (task != null && task.isCancelled())
                        return;
                } catch (IOException e) {
                    LOGGER.error(e.getLocalizedMessage(), e);
                }
            }
        }
    }

    public ReadOnlyStringProperty progressFilesProperty() {
        return progressFiles;
    }

    public ReadOnlyDoubleProperty progressOverallProperty() {
        return progressOverall;
    }
}
