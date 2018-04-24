
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

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

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
		BufferedImage bufferedImage = Tools.loadFromFile("resources/cards/c01.png");

		panelInput = new ImagePanel(bufferedImage);
		panelOutput = new ImagePanel(bufferedImage);
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
		southPanel.add(labelSymbol);
		southPanel.add(buttonLoad);
		southPanel.add(buttonStart);
		southPanel.add(buttonNext);

		JPanel centerPanel = new JPanel(new GridLayout());
		centerPanel.add(panelInput);
		centerPanel.add(panelOutput);

		add(centerPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
		}

	private void startSearch()
	{
	Mat mat = Tools.convertToMat(panelInput.getImage());
	Mat mat2 = Imgcodecs.imread("resources/cards/3.jpg",-1).t();
	Mat mat3 = mat2.clone();
	Core.flip(mat2, mat3, 1);
	panelOutput.setImage(Tools.convertToBufferedImage(recogniser.work(mat3)));
	//panelOutput.setImage(Tools.convertToBufferedImage(mat3));
	getContentPane();
	labelSymbol.setText(recogniser.getNumber() + " of " +recogniser.getSymbol());
	panelOutput.repaint();
	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	JButton buttonLoad;
	JButton buttonStart;
	JButton buttonNext;

	JLabel labelSymbol;

	ImagePanel panelInput;
	ImagePanel panelOutput;

	Recogniser recogniser;

	int nbColor = 0;
	int nbCard = 1;

	}
