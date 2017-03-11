package com.cmput414w17.medical.image;

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
		Scalar newVal = new Scalar(255, 255, 255);
		Scalar loDiff = new Scalar(50, 50, 50);
		Scalar upDiff = new Scalar(50, 50, 50);
		Point seedPoint = point;
		Mat mask = new Mat();
		Rect rect = new Rect();

		// Imgproc.floodFill(frame, mask, seedPoint, newVal);
		Imgproc.floodFill(frame, mask, seedPoint, newVal, rect, loDiff, upDiff, Imgproc.FLOODFILL_FIXED_RANGE);

		return frame;
	}

}
