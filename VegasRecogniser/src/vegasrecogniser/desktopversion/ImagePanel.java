
package vegasrecogniser.desktopversion;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel
	{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public ImagePanel(BufferedImage bufferedImage, int height)
		{
		this.text="";
		this.image = bufferedImage;
		this.height=height;
		}

	public ImagePanel(BufferedImage bufferedImage, int height, String text)
		{
		this.text=text;
		this.image = bufferedImage;
		this.height=height;
		}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public void setImage(BufferedImage bufferedImage)
		{
		this.image = bufferedImage;
		}

	public void setText(String text)
	{
	this.text=text;
	}

	public BufferedImage getImage()
		{
		return image;
		}

	@Override
	protected void paintComponent(Graphics g)
		{
		super.paintComponent(g);
		float ratio = (float)image.getWidth() / (float)image.getHeight();
		g.drawImage(image, 0, 0, (int)(height * ratio), height, this); // see javadoc for more info on the parameters
		g.drawString(text, 10, height+20);
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

	private BufferedImage image;
	private int height;
	private String text="";

	}
