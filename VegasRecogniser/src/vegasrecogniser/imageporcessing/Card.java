
package vegasrecogniser.imageporcessing;

import org.opencv.core.Mat;

public class Card
	{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public Card(String symbol, String number, Mat mat)
		{
		super();
		this.symbol = symbol;
		this.number = number;
		this.mat = mat;
		}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	/*------------------------------*\
	|*				Set				*|
	\*------------------------------*/

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	public String getSymbol()
		{
		return this.symbol;
		}

	public String getNumber()
		{
		return this.number;
		}

	public Mat getMat()
		{
		return this.mat;
		}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/
	private String symbol;



	private String number;
	private Mat mat;


	}

