
package vegasrecogniser.imageporcessing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Recogniser
	{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public Recogniser()
		{
		listRect = new LinkedList<Rect>();
		listPoint = new LinkedList<Point>();
		listMatNumber = new LinkedList<Mat>();

		loadImgNumber();
		}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public Mat work(Mat mat)
		{
//		searchRect(mat);
//		searchAngle(mat);
//
//		detectSymbol();
//		detectNumber(mat);

		//drawRect(mat);
		binaryImage(mat);
		return mat;
		}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void loadImgNumber()
	{
		for(int i = 1; i <= 13; i++)
			{
			listMatNumber.add(Imgcodecs.imread("resources/number/" + i+".jpg",-1));
			}
	}

	private void drawRect(Mat mat)
		{

		for(Point point:listPoint)
			{
			Imgproc.circle(mat, point, 10, new Scalar(0, 0, 255, 255), 1, 8, 0);
			}

		for(Rect rect:listRect)
			{

			Imgproc.rectangle(mat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0, 255), 5);
			}
		}

	private Mat searchRect(Mat mat)
		{
		Mat mat1 = mat.clone();
		List<Rect> listRectOut = new LinkedList<Rect>();

		binaryImage(mat1);
		listRect = getListRect(mat1);
		System.out.println(listRect.size());
		//deleteMaxRect(listRect);
		searchGoodRecangle(listRectOut, mat1, listRect);
		listRect = listRectOut;

		return mat1;
		}

	private Mat searchAngle(Mat mat)
		{
		Mat mat1 = mat.clone();

		binaryImage(mat1);
		detectCorner(mat1);
		listPoint = searchPointPosition(mat1);

		return mat1;
		}

	/**
	 * Detect the number of the card
	 */
	private void detectNumber(Mat mat)
	{
	int methode  = Imgproc.TM_SQDIFF;
	Mat mat1 = mat.clone();

	binaryImage(mat1);

	Rect rect = listRect.get(0);
	Mat subMat=mat.submat(rect.y, rect.y+rect.height, rect.x, rect.x+rect.width);

	Mat matResized=new Mat(110, 65, subMat.type());
	Imgproc.resize(subMat, matResized, new Size(65, 110));

	double minVal = Double.MAX_VALUE;
	int index=0;
	int i=0;

	for(Mat mat2:listMatNumber)
		{
		i++;

		Mat matResult = new Mat(1, 1, mat.type());
		Imgproc.matchTemplate(matResized, mat2, matResult, methode);
		MinMaxLocResult resMinMax = Core.minMaxLoc(matResult);

		if(resMinMax.minVal<minVal)
			{
			minVal=resMinMax.minVal;
			index=i;
			}
		}
	number=Constant.TABNUMBER[index-1];
	System.out.println(number);
	}

	/**
	 * Detect the symbol of the card
	 */
	private void detectSymbol()
		{
		List<Point> listForm = new LinkedList<Point>();
		for(Point point:listPoint)
			{
			if (point.inside(listRect.get(1)))
				{
				listForm.add(point);
				}
			}
		listForm.sort((p1, p2) -> Double.compare(p1.y, p2.y));
		int nbAngle = listForm.size();
		System.out.println(nbAngle);
		if (nbAngle == 6)
			{
			symbol = Symbol.CLUB.getName();
			}
		else if (nbAngle == 5)
			{
			symbol = Symbol.SPADE.getName();
			}
		else if (nbAngle == 2)
			{
			if (Math.abs(listForm.get(0).y - listRect.get(1).y) < 5)
				{
				symbol = Symbol.DIAMOND.getName();
				}
			else
				{
				symbol = Symbol.HEART.getName();
				}
			}
		}

	/**
	 * Return a list of point that correspond to positions of white area
	 * @param mat1
	 * @param mat2
	 * @return
	 */
	private List<Point> searchPointPosition(Mat mat1)
		{
		Mat mat2 = mat1.clone();

		int delta = 5;
		List<Point> list = new LinkedList<Point>();

		// Drawing a circle around corners
		for(int i = 0; i < mat2.cols(); i++)
			{
			for(int j = 0; j < mat2.rows(); j++)
				{
				if ((int)mat2.get(j, i)[0] == 255)
					{
					boolean isCorrect = true;

					for(Point point:list)
						{
						if (Math.abs(point.x - i) < delta && Math.abs(point.y - j) < delta)
							{
							isCorrect = false;
							}
						}
					if (isCorrect)
						{
						list.add(new Point(i, j));
						//Imgproc.circle(mat1, new Point(i, j), 10, new Scalar(255, 255, 255, 255), 1, 8, 0);
						}
					}
				}
			}
		return list;
		}

	/**
	 * Detect corner with Harris corner
	 * @param mat1
	 */
	private void detectCorner(Mat mat1)
		{
		Mat mat2 = new Mat(mat1.height(), mat1.width(), mat1.type());

		int blockSize = 4;
		int apertureSize = 3;
		double k = 0.04;
		int thresh = 100;

		/// Detecting corners
		Imgproc.cornerHarris(mat1, mat2, blockSize, apertureSize, k);

		/// Normalizing
		Core.normalize(mat2, mat1, 0, 255, Core.NORM_MINMAX, mat1.type(), new Mat());
		Core.convertScaleAbs(mat1, mat2);

		//Binary image
		Imgproc.threshold(mat2, mat1, thresh, 255, Imgproc.THRESH_BINARY);
		}

	/**
	 * Search two rectangle that contain the number and th symbol of the card
	 * @param listRectOut
	 * @param mat1
	 * @param listRect
	 */
	private void searchGoodRecangle(List<Rect> listRectOut, Mat mat1, List<Rect> listRect)
		{
		listRect.sort((rect1, rect2) -> Integer.compare(rect1.x, rect2.x));

		int j = 0;
		for(Rect rect:listRect)
			{
			listRectOut.add(rect);
			//Imgproc.rectangle(mat1, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 255, 255, 255), 2);
			j++;
			if(rect.width<10)
				{
				j--;
				}
			if (j == 2)
				{
				break;
				}
			}

		listRectOut.sort((rect1, rect2) -> Integer.compare(rect1.y, rect2.y));
		}

	/**
	 * Delete two rectangles in border of the card
	 * @param listRect
	 */
	private void deleteMaxRect(List<Rect> listRect)
		{
		for(int j = 0; j < 2; j++)
			{
			int maxHeight = 0;
			Rect rectToDelete = null;
			for(Rect rect:listRect)
				{
				if (rect.height > maxHeight)
					{
					rectToDelete = rect;
					maxHeight = rect.height;
					}
				}
			listRect.remove(rectToDelete);
			}
		}

	/***
	 * Return a list of rectangle for each different area in a binary image
	 * @param mat
	 * @return
	 */
	private List<Rect> getListRect(Mat mat)
		{
		Mat mat2 = new Mat(mat.height(), mat.width(), mat.type());

		//Inversion
		Core.bitwise_not(mat, mat2);

		final List<MatOfPoint> points = new ArrayList<>();
		final Mat hierarchy = new Mat();
		Imgproc.findContours(mat2, points, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		Core.repeat(mat2, 1, 1, mat);

		List<Rect> listRect = new LinkedList<Rect>();
		for(MatOfPoint matOfPoint:points)
			{
			Rect rect = Imgproc.boundingRect(matOfPoint);
			listRect.add(rect);
			}

		return listRect;
		}

	/**
	 * Convert colored image in binary black and white
	 * @param mat
	 * @return
	 */
	private void binaryImage(Mat mat)
		{
		Mat mat2 = new Mat(mat.height(), mat.width(), mat.type());

		System.out.println(mat.type());


		//Level of gray
		Imgproc.cvtColor(mat, mat2, Imgproc.COLOR_BGR2GRAY);

		//Binary image
		Imgproc.threshold(mat2, mat, 100, 255, Imgproc.THRESH_BINARY);

		System.out.println(mat.type());
		}

	/*------------------------------*\
	|*				Set				*|
	\*------------------------------*/

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	public String getSymbol()
		{
		return symbol;
		}

	public String getNumber()
		{
		return number;
		}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private List<Rect> listRect;
	private List<Point> listPoint;
	private List<Mat> listMatNumber;

	private String symbol;
	private String number;

	}
