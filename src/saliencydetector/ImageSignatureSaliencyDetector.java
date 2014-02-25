package saliencydetector;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class ImageSignatureSaliencyDetector extends SaliencyDetector{
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
