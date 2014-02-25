package saliencydetector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;
/**
 * the saliency detector based on face detector, where the face region
 * will receive high response. For input, please use gray image
 * @author qzhang53
 *
 */
public class FaceSaliencyDetector extends SaliencyDetector{
	/**
	 * the detector
	 */
	protected static CascadeClassifier detector;
	protected static String detectorPath="lbpcascade_frontalface.xml";
	/**
	 * constants
	 */
	public static final Scalar background=new Scalar(0);
	public static final Scalar foreground=new Scalar(1);
	/*
	 * detections
	 */
	protected MatOfRect face;
	
	public FaceSaliencyDetector(int r, int c, String path) {
		super(r, c);
		// TODO Auto-generated constructor stub
		if (detector==null)
		{
			loadDetector();
		}
	}

	public FaceSaliencyDetector(Mat input, String path)
	{
		super(input);
		assert(input.type()==CvType.CV_8U);
		if (detector==null)
		{
			loadDetector();
		}
	}
	
	public FaceSaliencyDetector(Mat input, Mat output, String path)
	{
		super(output);
		assert(input.type()==CvType.CV_8U);
		if (detector==null)
		{
			loadDetector();
		}
	}
	/**
	 * set the input. This should be compatible with the (row, col)
	 * @param input
	 */
	public void set(Mat input)
	{
		assert(input.type()==CvType.CV_8U);
		super.set(input);
	}
	/**
	 * load the sacascade detector file
	 */
	public static void loadDetector(String path)
	{
		path=detectorPath;
		loadDetector();
	}
	/**
	 * load the sacascade detector file
	 */
	public static void loadDetector()
	{
        detector = new CascadeClassifier(detectorPath);
        if (detector.empty()) 
        {
            System.err.println("Failed to load cascade classifier");
            detector = null;
        }
	}
	/**
	 * detect the face
	 */
	@Override
	public void compute()
	{
		detector.detectMultiScale(img, face);
		// the face region will be assigned to a high score
		sal.setTo(background);
		for (Rect rect: face.toArray())
		{
			Core.rectangle(sal, rect.tl(), rect.br(), foreground);
		}
		// normalize the region
		Scalar sum=Core.sumElems(sal);
		Core.divide(sal, sum, sal);
	}
}
