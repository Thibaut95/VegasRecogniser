
package vegasrecogniser.desktopversion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import vegasrecogniser.imageporcessing.Card;
import vegasrecogniser.imageporcessing.Constant;
import vegasrecogniser.imageporcessing.Recogniser;
import vegasrecogniser.imageporcessing.Tools;

public class MainJFrame extends JFrame
	{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public MainJFrame()
		{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		recogniser = new Recogniser();

		labelSymbol = new JLabel("");

		createButton();
		createPanel();

		geometry();
		controle();
		apparence();
		}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void createButton()
		{
		buttonLoad = new JButton("Load");
		buttonStart = new JButton("Start");
		buttonNext = new JButton("Next");
		}

	private void createPanel()
		{
		Mat mat2 = Imgcodecs.imread("resources/cards/hand3.jpg",-1).t();
		Mat mat3 = mat2.clone();
		Core.flip(mat2, mat3, 1);

		BufferedImage bufferedImage=Tools.convertToBufferedImage(mat3);

		panelInput = new ImagePanel(bufferedImage,600);
		panelOutput = new LinkedList<ImagePanel>();
		for(int i = 1; i <= n; i++)
			{
			ImagePanel panel = new ImagePanel(new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType()),250);
			panelOutput.add(panel);
			}
		}

	private void apparence()
		{
		setTitle("VegasRecogniser");
		setSize(1400, 900);
		setLocationRelativeTo(null);

		getContentPane().setBackground(Color.PINK);

		setVisible(true);//forcément être la dernière instruction
		}

	private void controle()
		{

		buttonNext.addActionListener(new ActionListener()
			{

			@Override
			public void actionPerformed(ActionEvent e)
				{
				nbCard++;
				if (nbCard > 13)
					{
					nbColor++;
					nbCard = 1;
					if (nbColor > 3)
						{
						nbColor = 0;
						}
					}
				String number;
				if (nbCard < 10)
					{
					number = "0" + nbCard;
					}
				else
					{
					number = "" + nbCard;
					}
				BufferedImage bufferedImage = Tools.loadFromFile("resources/cards/" + Constant.TABCOLORFORM[nbColor] + number + ".png");
				panelInput.setImage(bufferedImage);
				panelInput.repaint();
				startSearch();
				}
			});

		buttonStart.addActionListener(new ActionListener()
			{

			@Override
			public void actionPerformed(ActionEvent e)
				{
				startSearch();
				}
			});

		buttonLoad.addActionListener(new ActionListener()
			{

			@Override
			public void actionPerformed(ActionEvent e)
				{
				final JFileChooser fc = new JFileChooser("C:\\Users\\thibaut.piquerez\\Desktop\\cards");

				fc.showOpenDialog(null);
				fc.setPreferredSize(new Dimension(1000, 800));
				File file = fc.getSelectedFile();
				panelInput.setImage(Tools.loadFromFile(file.getPath()));
				panelInput.repaint();
				}
			});

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

	private void geometry()
		{
		setLayout(new BorderLayout());

		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		southPanel.add(buttonLoad);
		southPanel.add(buttonStart);
		southPanel.add(buttonNext);

		JPanel centerPanel = new JPanel(new GridLayout());
		centerPanel.add(panelInput);

		JPanel cardsPanel = new JPanel(new GridLayout(0, 3));
		for(ImagePanel imagePanel:panelOutput)
			{
			cardsPanel.add(imagePanel);
			}


		centerPanel.add(cardsPanel);

		add(centerPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
		}

	private void startSearch()
	{

	Mat mat2 = Imgcodecs.imread("resources/cards/hand3.jpg",-1).t();
	Mat mat3 = mat2.clone();
	Core.flip(mat2, mat3, 1);

	panelInput.setImage(Tools.convertToBufferedImage(mat3));
	panelInput.repaint();

	List<Card> listCard=recogniser.work(mat3);

	int i=0;
	for(ImagePanel panel:panelOutput)
		{
		panel.setImage(Tools.convertToBufferedImage(listCard.get(i).getMat()));
		panel.setText(listCard.get(i).getNumber()+" of "+listCard.get(i).getSymbol());
		i++;
		panel.repaint();
		}


	getContentPane();
	labelSymbol.setText(recogniser.getNumber() + " of " +recogniser.getSymbol());

	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	JButton buttonLoad;
	JButton buttonStart;
	JButton buttonNext;

	JLabel labelSymbol;

	ImagePanel panelInput;
	List<ImagePanel> panelOutput;

	Recogniser recogniser;

	int nbColor = 0;
	int nbCard = 1;
	int n=7;

	}
