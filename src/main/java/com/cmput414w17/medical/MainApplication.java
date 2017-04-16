package com.cmput414w17.medical;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.BasicConfigurator;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.cmput414w17.medical.image.ImageUtils;
import com.cmput414w17.medical.image.RegionOfInterestImage;
import com.cmput414w17.medical.image.UnsupportedFileTypeException;

import nu.pattern.OpenCV;

public class MainApplication {
	static {
		OpenCV.loadShared();
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
		BasicConfigurator.configure();
	}

	public static void main(String[] args) throws InterruptedException, UnsupportedFileTypeException {
		File directory = new File("input");
		File output = new File("output");
		if (args.length > 0) {
			// Assume the argument is a path to a folder
			directory = new File(args[0]);

			if (args.length > 1) {
				output = new File(args[1]);
			}
		}

		if (directory.isDirectory() == false) {
			System.err.println(String.format(
					"The path, '%s', does not appear to be a directory! Please verify that this path exists and points to a directory.",
					directory.getAbsolutePath()));
			return;
		}

		File[] files = directory.listFiles();

		final String[] formats = { "jpeg", "jpeg2000", "png", "bpg" };

		final String roiSuffix = "_roi";
		final String nonRoiSuffix = "_nonroi";

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
								ImageUtils.convertImageToBpg(roiOut, roiFormatImageOut, true);
								ImageUtils.convertImageToBpg(nonRoiOut, nonRoiFormatImageOut, false);
							} else {
								ImageUtils.convertImageToFormat(roiBufferedImage, roiFormatImageOut, format, 1.0f);
								ImageUtils.convertImageToFormat(nonRoiBufferedImage, nonRoiFormatImageOut, format,
										0.75f);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
