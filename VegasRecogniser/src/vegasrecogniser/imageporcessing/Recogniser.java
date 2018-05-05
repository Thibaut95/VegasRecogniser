
package vegasrecogniser.imageporcessing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
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
		listRectCard = new LinkedList<Rect>();
		listPoint = new LinkedList<Point>();
		listMatNumber = new LinkedList<Mat>();
		listMatSymbol = new LinkedList<Mat>();

		loadImgNumber();
		loadImgSymbol();
		}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public List<Card> work(Mat mat)
		{
		List<Card> listCard = new LinkedList<Card>();

		searchRectCard(mat);
		List<Mat> listMatCard=getCard(mat);
		for(Mat matCard:listMatCard)
			{

			List<RotatedRect> listRotRect=getCardStraight(matCard);

			int angleAdd = 0;
			if(listRotRect.get(0).size.width<listRotRect.get(0).size.height)
				{
				angleAdd=90;
				}

			Mat mat2 = new Mat(matCard.height(), matCard.width(), matCard.type());
			Mat rotation=Imgproc.getRotationMatrix2D(new Point(listRotRect.get(0).center.x,listRotRect.get(0).center.y), listRotRect.get(0).angle-angleAdd, 1);
			Imgproc.warpAffine(matCard, mat2, rotation, new Size(matCard.size().width,matCard.size().height));

//			//searchRectCardStraigth(mat2);

//			if(angleAdd==90)
//				{
//				Point point = new Point(listRotRect.get(0).center.x-listRotRect.get(0).size.height/2,listRotRect.get(0).center.y-listRotRect.get(0).size.width/2);
//				mat=mat2.submat(new Rect(point,new Size(listRotRect.get(0).size.height,listRotRect.get(0).size.width)));
//				}
//			else
//				{
//				Point point = new Point(listRotRect.get(0).center.x-listRotRect.get(0).size.width/2,listRotRect.get(0).center.y-listRotRect.get(0).size.height/2);
//				mat=mat2.submat(new Rect(point,listRotRect.get(0).size));
//				}

			Mat mat3 = new Mat(matCard.height(), matCard.width(), matCard.type());
			Core.rotate(mat2, mat3, Core.ROTATE_90_CLOCKWISE);


			searchRect(mat3);

			detectSymbol(mat3);
			detectNumber(mat3);
			drawRect(mat3, listRect);

			//binaryImage(mat2,treshDetectSmybol);
			Card card = new Card(symbol, number, mat3);
			listCard.add(card);


			//binaryImage(mat);
			}

		return listCard;
		}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void drawRotRect(Mat matCard, List<RotatedRect> listRotRect)
		{
		for(RotatedRect rotatedRect:listRotRect)
			{
			Point points[] = new Point[4];
			rotatedRect.points(points);
		    for(int i=0; i<4; ++i){
		        Imgproc.line(matCard, points[i], points[(i+1)%4],  new Scalar(255, 0, 0, 255),10);

			}

	    }

		}

	private List<RotatedRect> getCardStraight(Mat mat)
		{
		Mat mat1 = mat.clone();


		binaryImage(mat1,treshSeparateCard);
		Mat mat2 = new Mat(mat.height(), mat.width(), mat.type());

		//Inversion
		Core.bitwise_not(mat1, mat2);

		final List<MatOfPoint> points = new ArrayList<>();
		final Mat hierarchy = new Mat();
		Imgproc.findContours(mat2, points, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

//		Core.repeat(mat2, 1, 1, mat);

		List<RotatedRect> listRectRot = new LinkedList<RotatedRect>();
		for(MatOfPoint matOfPoint:points)
			{
			RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(matOfPoint.toArray()));
			listRectRot.add(rect);
			}

		listRectRot.sort((rect1, rect2) -> Double.compare(rect1.size.height, rect2.size.height)*-1);
		listRectRot.remove(0);


		return listRectRot.subList(0, 1);

		}

	private List<Mat> getCard(Mat mat)
	{
		List<Mat> listMatCard= new LinkedList<Mat>();
		for(Rect rect:listRectCard)
			{
				listMatCard.add(mat.submat(rect));
			}
		return listMatCard;
		//return mat;
	}

	private void loadImgNumber()
		{
		for(int i = 1; i <= 13; i++)
			{
			listMatNumber.add(Imgcodecs.imread("resources/number/" + i + ".jpg", -1));
			}
		}

	private void loadImgSymbol()
		{
		for(char letter:Constant.TABCOLORFORM)
			{
			listMatSymbol.add(Imgcodecs.imread("resources/symbol/" + letter + ".jpg", -1));
			System.out.println(letter);
			}
		}

	private void drawRect(Mat mat, List<Rect> listRect)
		{
		System.out.println(listRect.size());
		for(Rect rect:listRect)
			{
			Imgproc.rectangle(mat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0, 255), 10);
			}
		}

	private Mat searchRectCard(Mat mat)
		{
		Mat mat1 = mat.clone();
		List<Rect> listRectOut = new LinkedList<Rect>();

		binaryImage(mat1,treshSeparateCard);
		listRectCard = getListRect(mat1);

		deleteMaxRect(listRectCard, 1);
		deleteRectInside(listRectOut, listRectCard, 7);

		listRectCard = listRectOut;

		return mat1;
		}

	private Mat searchRectCardStraigth(Mat mat)
		{
		Mat mat1 = mat.clone();
		List<Rect> listRectOut = new LinkedList<Rect>();

		binaryImage(mat1,treshSeparateCard);
		listRectCard = getListRect(mat1);

		//deleteMaxRect(listRectCard, 1);
		//deleteRectInside(listRectOut, listRectCard, 5);

		//listRectCard = listRectOut;

		return mat1;
		}



	private Mat searchRect(Mat mat)
		{
		Mat mat1 = mat.clone();
		List<Rect> listRectOut = new LinkedList<Rect>();

		binaryImage(mat1,treshDetectSmybol);
		listRect = getListRect(mat1);

		deleteMaxRect(listRect);
		searchGoodRecangle(listRectOut, mat1, listRect);
		listRect = listRectOut;

		return mat1;
		}

	private Mat searchAngle(Mat mat)
		{
		Mat mat1 = mat.clone();

		binaryImage(mat1,treshDetectSmybol);
		detectCorner(mat1);
		listPoint = searchPointPosition(mat1);

		return mat1;
		}

	/**
	 * Detect the number of the card
	 */
	private void detectNumber(Mat mat)
		{
		int methode = Imgproc.TM_SQDIFF;
		Mat mat1 = mat.clone();

		binaryImage(mat1,treshDetectSmybol);

		Rect rect = listRect.get(0);
		Mat subMat = mat.submat(rect.y, rect.y + rect.height, rect.x, rect.x + rect.width);

		Mat matResized = new Mat(listMatNumber.get(0).rows(), listMatNumber.get(0).cols(), subMat.type());
		Imgproc.resize(subMat, matResized, new Size(listMatNumber.get(0).cols(), listMatNumber.get(0).rows()));

		double minVal = Double.MAX_VALUE;
		int index = 0;
		int i = 0;

		for(Mat mat2:listMatNumber)
			{
			i++;

			Mat matResult = new Mat(1, 1, mat.type());
			Imgproc.matchTemplate(matResized, mat2, matResult, methode);
			MinMaxLocResult resMinMax = Core.minMaxLoc(matResult);

			if (resMinMax.minVal < minVal)
				{
				minVal = resMinMax.minVal;
				index = i;
				}
			}
		number = Constant.TABNUMBER[index - 1];

		}

	/**
	 * Detect the symbol of the card
	 */
	private void detectSymbol(Mat mat)
		{
		int methode = Imgproc.TM_SQDIFF;
		Mat mat1 = mat.clone();

		binaryImage(mat1,treshDetectSmybol);

		Rect rect = listRect.get(1);
		Mat subMat = mat.submat(rect.y, rect.y + rect.height, rect.x, rect.x + rect.width);

		Mat matResized = new Mat(listMatSymbol.get(0).rows(), listMatSymbol.get(0).cols(), subMat.type());
		Imgproc.resize(subMat, matResized, new Size(listMatSymbol.get(0).cols(), listMatSymbol.get(0).rows()));

		double minVal = Double.MAX_VALUE;
		int index = 0;
		int i = 0;

		for(Mat mat2:listMatSymbol)
			{
			i++;

			Mat matResult = new Mat(1, 1, mat.type());
			Imgproc.matchTemplate(matResized, mat2, matResult, methode);
			MinMaxLocResult resMinMax = Core.minMaxLoc(matResult);

			if (resMinMax.minVal < minVal)
				{
				minVal = resMinMax.minVal;
				index = i;
				}
			}
		System.out.println(index);
		symbol = Constant.TABSYMBOL[index - 1];
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

	private void deleteRectInside(List<Rect> listRectOut, List<Rect> listRect, int nbRect)
		{
		listRect.sort((rect1, rect2) -> Integer.compare(rect1.height, rect2.height)*-1);
		listRectOut.addAll(listRect.subList(0, nbRect));

		}

	/**
	 * Search two rectangle that contain the number and th symbol of the card
	 * @param listRectOut
	 * @param mat1
	 * @param listRect
	 */
	private void searchGoodRecangle(List<Rect> listRectOut, Mat mat1, List<Rect> listRect)
		{
		searchGoodRecangle(listRectOut, mat1, listRect, 10);
		}

	private void searchGoodRecangle(List<Rect> listRectOut, Mat mat1, List<Rect> listRect, int minWidth)
		{
		//listRect.sort((rect1, rect2) -> Integer.compare(rect1.y, rect2.y));
		listRect.sort((rect1, rect2) -> Integer.compare(rect1.x, rect2.x));


		int j = 0;
		for(Rect rect:listRect)
			{
			//Imgproc.rectangle(mat1, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 255, 255, 255), 2);
			j++;
			if (rect.width < minWidth || rect.height < minWidth || rect.x<minWidth)
				{
				j--;
				}
			else
				{
				listRectOut.add(rect);
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
		deleteMaxRect(listRect, 2);
		}

	private void deleteMaxRect(List<Rect> listRect, int nbRect)
		{
		for(int j = 0; j < nbRect; j++)
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

		//Core.repeat(mat2, 1, 1, mat);

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
	private void binaryImage(Mat mat, int tresh)
		{
		Mat mat2 = new Mat(mat.height(), mat.width(), mat.type());

		//Level of gray
		Imgproc.cvtColor(mat, mat2, Imgproc.COLOR_BGR2GRAY);

		//Binary image
		Imgproc.threshold(mat2, mat, tresh, 255, Imgproc.THRESH_BINARY);

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
	private List<Rect> listRectCard;
	private List<Point> listPoint;
	private List<Mat> listMatNumber;
	private List<Mat> listMatSymbol;

	private String symbol;
	private String number;

	private int treshSeparateCard=100;
	private int treshDetectSmybol=150;

	}
