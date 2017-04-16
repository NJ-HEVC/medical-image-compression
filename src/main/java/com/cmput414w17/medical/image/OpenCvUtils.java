package com.cmput414w17.medical.image;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * A series of OpenCV image processing utility functions.
 *
 * @author David Yee
 */
public class OpenCvUtils {

	/**
	 * Modifies the OpenCV frame, performing a flood-fill replacement based on a
	 * given threshold of difference at a given point in the image.
	 * 
	 * @param frame
	 *            The frame that represents the image.
	 * @param point
	 *            The point in the image. This point must not exceed the
	 *            boundaries of the image.
	 * @return The modified frame with the background replaced with a solid
	 *         colour.
	 */
	// Code by Luigi De Russis
	// https://github.com/opencv-java/image-segmentation
	public static Mat doBackgroundRemovalFloodFill(Mat frame, Point point) {
		Mat output = new Mat();
		frame.copyTo(output);

		Scalar newVal = new Scalar(255, 255, 255);
		Scalar loDiff = new Scalar(50, 50, 50);
		Scalar upDiff = new Scalar(50, 50, 50);
		Point seedPoint = point;
		Mat mask = new Mat();
		Rect rect = new Rect();

		Imgproc.floodFill(output, mask, seedPoint, newVal, rect, loDiff, upDiff, Imgproc.FLOODFILL_FIXED_RANGE);

		return output;
	}

	/**
	 * Retrieve the inverted thresholded difference image between the original
	 * image and the flood-filled image.
	 * 
	 * @param original
	 *            The original image.
	 * @param filled
	 *            The original image with a portion flood-filled removed.
	 * @return The inverted thresholded difference image.
	 */
	// Based on the code and ideas of 
	// Michale Koval (http://stackoverflow.com/users/111426/michael-koval)
	// From http://stackoverflow.com/a/5740264/2557554 and licensed under 
	// CC-BY-SA 3.0 (https://creativecommons.org/licenses/by-sa/3.0/deed.en)
	public static Mat getFloodFilledRegion(Mat original, Mat filled) {
		Mat dst = new Mat();
		Core.absdiff(filled, original, dst);

		// reduces false-positives
		final int threshold = 10;

		Imgproc.threshold(dst, dst, threshold, 255, Imgproc.THRESH_BINARY_INV);

		return dst;
	}
}
