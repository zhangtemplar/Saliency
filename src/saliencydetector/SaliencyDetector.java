package saliencydetector;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * this will be served as the base class of saliency detector
 * @author qzhang53
 *
 */
public class SaliencyDetector implements Runnable{
	/**
	 * the input image
	 */
	protected Mat img;
	/**
	 * the output saliency map
	 */
	protected Mat sal;
	/**
	 * size of input
	 */
	protected int row, col;
	/**
	 * constructor with the size of the image. Note that, this should be compatible with 
	 * input image and output saliency map
	 * @param r
	 * @param c
	 */
	public SaliencyDetector(int r, int c)
	{
		row=r;
		col=c;
		img=null;
		sal=null;
	}
	/**
	 * another constructor
	 */
	public SaliencyDetector(Mat input)
	{
		row=input.height();
		col=input.width();
		set(input);
	}
	/**
	 * another constructor
	 */
	public SaliencyDetector(Mat input, Mat output)
	{
		row=input.height();
		col=input.width();
		set(input, output);
	}
	/**
	 * size of the input
	 * @return
	 */
	public int height()
	{
		return row;
	}
	/**
	 * size of the input
	 * @return
	 */
	public int width()
	{
		return col;
	}
	/**
	 * set the input. This should be compatible with the (row, col)
	 * @param input
	 */
	public void set(Mat input)
	{
		assert(input.height()==row && input.width()==col);
		img=input;
	}
	/**
	 * set the input and output. This should be compatible with the (row, col)
	 * @param input
	 * @param output
	 */
	public void set(Mat input, Mat output)
	{
		set(input);
		assert(output.height()==row && output.width()==col && output.type()==CvType.CV_32F);
		sal=output;
	}
	/**
	 * get the output. We will provide a deep copy of the saliency map. To avoid this, 
	 * please provide one with set(input, output). 
	 */
	public Mat get()
	{
		Mat result=new Mat();
		sal.copyTo(result);
		return result;
	}
	/**
	 * compute the saliency
	 */
	public void compute()
	{
		System.err.println("The saliency detector is not implemented");
	}
	/**
	 * 
	 */
	public void run()
	{
		compute();
	}
}
