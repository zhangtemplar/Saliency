package saliencydetector;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class ImageSignatureSaliencyDetector extends SaliencyDetector{
	/**
	 * we need to load the library first, but make sure it is only loaded once
	 */
	static
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	/**
	 * demo files
	 * @param args
	 */
	public static void main(String [] args)
	{
		Mat img=Highgui.imread("C:\\Users\\qzhang53\\Documents\\MATLAB\\JuddSaliencyModel\\sampleImage.jpeg");
		List<Mat> rgb=new ArrayList<Mat>(3);
		Core.split(img, rgb);
		ImageSignatureSaliencyDetector detector=new ImageSignatureSaliencyDetector(img.height(), img.width());
		Mat sal=new Mat(img.height(), img.width(), CvType.CV_32F);
		for (Mat c: rgb)
		{
			Mat tmp=new Mat();
			c.convertTo(tmp, CvType.CV_32F);
			detector.set(tmp, sal);
		}
		Highgui.imwrite("C:\\Users\\qzhang53\\Documents\\MATLAB\\JuddSaliencyModel\\sampleImage_sal.jpeg", sal);
	}
	
	/**
	 * coefficient for FFT
	 */
	private Mat coef;
	/**
	 * splitted coef
	 */
    private List<Mat> coef_complex;
    /**
     * magnitude specturm
     */
    private Mat mag;
    /**
     * saliency map
     */
    private Mat signature;
    
	public ImageSignatureSaliencyDetector(int r, int c) {
		super(r, c);
		// TODO Auto-generated constructor stub
		init();
	}

	public ImageSignatureSaliencyDetector(Mat input)
	{
		super(input);
		init();
	}
	
	public ImageSignatureSaliencyDetector(Mat input, Mat output)
	{
		super(input, output);
		init();
	}
	
	/**
	 * create the data
	 */
	private void init()
	{
		coef=new Mat(row, col, CvType.CV_32FC2);
		coef_complex=new ArrayList<Mat>(2);
		mag=new Mat(row, col,CvType.CV_32F);
		signature=new Mat(row, col,CvType.CV_32FC2);		
	}
	
	/**
	 * the core algorithm
	 */
	@Override
	public void run()
    {
		// forward dft
		Core.dft(img,coef,Core.DFT_COMPLEX_OUTPUT,img.rows());
		// unify the magnitude
		
		Core.split(coef,coef_complex);
		
		Core.magnitude(coef_complex.get(0), coef_complex.get(1), mag);
		Core.divide(coef_complex.get(0),mag,coef_complex.get(0));
		Core.divide(coef_complex.get(1),mag,coef_complex.get(1));
		Core.merge(coef_complex, coef);
		// apply the inverse dft
		
		Core.dft(coef,signature,Core.DCT_INVERSE,img.rows());
		// take square and magnitude
		Core.split(signature, coef_complex);
		Core.magnitude(coef_complex.get(0), coef_complex.get(1), mag);
		Core.multiply(mag, mag, mag);		
		
		// add this to the result
		Core.add(sal, mag, sal);        	
    }
}
