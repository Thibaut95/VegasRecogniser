
package vegasrecogniser.desktopversion;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel
	{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public ImagePanel(BufferedImage bufferedImage)
		{
		/*

		*/
		this.image = bufferedImage;
		}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public void setImage(BufferedImage bufferedImage)
		{
		this.image = bufferedImage;
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
		g.drawImage(image, 0, 0, (int)(600 * ratio), 600, this); // see javadoc for more info on the parameters
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

	}
