
package vegasrecogniser.imageporcessing;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class Tools
	{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public static BufferedImage loadFromFile(String fileName)
		{
		BufferedImage bufferedImage = null;
		try
			{
			bufferedImage = ImageIO.read(new File(fileName));
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		return bufferedImage;
		}


	public static Mat convertToMat(BufferedImage image)
		{
		Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC4);
		byte[] data = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		return mat;
		}

	public static BufferedImage convertToBufferedImage(Mat mat)
		{
//		MatOfByte mob = new MatOfByte();
//		Imgcodecs.imencode(".png", mat, mob);
//		byte ba[] = mob.toArray();
//
//		BufferedImage bi = null;
//		try
//			{
//			bi = ImageIO.read(new ByteArrayInputStream(ba));
//			}
//		catch (IOException e)
//			{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			}
//		return bi;



//		BufferedImage out;
//        byte[] data = new byte[mat.width()*mat.height()*4];
//        int type;
//        mat.get(0, 0, data);
//
//
//        type = BufferedImage.TYPE_4BYTE_ABGR;
//
//        out = new BufferedImage(mat.width(), mat.height(), type);
//
//        out.getRaster().setDataElements(0, 0, mat.width(), mat.height(), data);
//        return out;

		BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        mat.get(0, 0, data);
        return image;
		}

	/*------------------------------*\
	|*				Set				*|
	\*------------------------------*/

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/
	}
