package util.image;

import games.strategy.engine.framework.GameRunner2;
import games.strategy.engine.framework.ProcessRunnerUtil;
import games.strategy.net.DesktopUtilityBrowserLauncher;
import games.strategy.triplea.image.UnitImageFactory;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * A frame that will run the different map making utilities we have.
 * 
 * @author veqryn
 * 
 */
public class MapCreator extends JFrame
{
	private static final long serialVersionUID = 3593102638082774498L;
	private static final String TRIPLEA_MAP_FOLDER = "triplea.map.folder";
	private static final String TRIPLEA_UNIT_ZOOM = "triplea.unit.zoom";
	private static final String TRIPLEA_UNIT_WIDTH = "triplea.unit.width";
	private static final String TRIPLEA_UNIT_HEIGHT = "triplea.unit.height";
	private static long s_memory = Runtime.getRuntime().maxMemory();
	private static File s_mapFolderLocation = null;
	private static double s_unit_zoom = 0.75;
	private static int s_unit_width = UnitImageFactory.DEFAULT_UNIT_ICON_SIZE;
	private static int s_unit_height = UnitImageFactory.DEFAULT_UNIT_ICON_SIZE;
	private static boolean s_runUtilitiesAsSeperateProcesses = true;
	
	final JPanel m_mainPanel;
	final JPanel m_sidePanel;
	final JButton m_part1;
	final JButton m_part2;
	final JButton m_part3;
	final JButton m_part4;
	final JPanel m_panel1 = new JPanel();
	final JPanel m_panel2 = new JPanel();
	final JPanel m_panel3 = new JPanel();
	final JPanel m_panel4 = new JPanel();
	
	public static String[] getProperties()
	{
		return new String[] { TRIPLEA_MAP_FOLDER, TRIPLEA_UNIT_ZOOM, TRIPLEA_UNIT_WIDTH, TRIPLEA_UNIT_HEIGHT };
	}
	
	public static void main(final String[] args)
	{
		/*try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception e)
		{
			e.printStackTrace();
		}*/
		handleCommandLineArgs(args);
		final MapCreator creator = new MapCreator();
		creator.setSize(800, 600);
		creator.setLocationRelativeTo(null);
		creator.setVisible(true);
	}
	
	public MapCreator()
	{
		super("TripleA Map Creator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// components
		m_mainPanel = new JPanel();
		m_sidePanel = new JPanel();
		m_part1 = new JButton("Step 1: Map Properties");
		m_part2 = new JButton("Step 2: Map Utilities");
		m_part3 = new JButton("Step 3: Game XML");
		m_part4 = new JButton("Other: Optional Things");
		m_sidePanel.setLayout(new BoxLayout(m_sidePanel, BoxLayout.PAGE_AXIS));
		m_sidePanel.add(Box.createVerticalGlue());
		m_sidePanel.add(m_part1);
		m_part1.setAlignmentX(Component.CENTER_ALIGNMENT);
		m_sidePanel.add(Box.createVerticalGlue());
		m_sidePanel.add(m_part2);
		m_part2.setAlignmentX(Component.CENTER_ALIGNMENT);
		m_sidePanel.add(Box.createVerticalGlue());
		m_sidePanel.add(m_part3);
		m_part3.setAlignmentX(Component.CENTER_ALIGNMENT);
		m_sidePanel.add(Box.createVerticalGlue());
		m_sidePanel.add(m_part4);
		m_part4.setAlignmentX(Component.CENTER_ALIGNMENT);
		m_sidePanel.add(Box.createVerticalGlue());
		createPart1Panel();
		createPart2Panel();
		createPart3Panel();
		createPart4Panel();
		
		m_part1.addActionListener(new AbstractAction("Part 1")
		{
			private static final long serialVersionUID = 5363944759664271421L;
			
			public void actionPerformed(final ActionEvent e)
			{
				setupMainPanel(m_panel1);
			}
		});
		m_part2.addActionListener(new AbstractAction("Part 2")
		{
			private static final long serialVersionUID = -8158213072422149296L;
			
			public void actionPerformed(final ActionEvent e)
			{
				setupMainPanel(m_panel2);
			}
		});
		m_part3.addActionListener(new AbstractAction("Part 3")
		{
			private static final long serialVersionUID = 881434681054088699L;
			
			public void actionPerformed(final ActionEvent e)
			{
				setupMainPanel(m_panel3);
			}
		});
		m_part4.addActionListener(new AbstractAction("Part 4")
		{
			private static final long serialVersionUID = 2794249359841059679L;
			
			public void actionPerformed(final ActionEvent e)
			{
				setupMainPanel(m_panel4);
			}
		});
		
		// set up the menu actions
		final Action exitAction = new AbstractAction("Exit")
		{
			private static final long serialVersionUID = 5363944759664271421L;
			
			public void actionPerformed(final ActionEvent event)
			{
				System.exit(0);
			}
		};
		exitAction.putValue(Action.SHORT_DESCRIPTION, "Exit The Program");
		// set up the menu items
		final JMenuItem exitItem = new JMenuItem(exitAction);
		// set up the menu bar
		final JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		final JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);
		// set up the layout manager
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(new JScrollPane(m_sidePanel), BorderLayout.WEST);
		this.getContentPane().add(new JScrollPane(m_mainPanel), BorderLayout.CENTER);
		// now set up the main screen
		setupMainPanel(m_panel1);
	}
	
	private void setupMainPanel(final JPanel panel)
	{
		m_mainPanel.removeAll();
		m_mainPanel.add(panel);
		setWidgetActivation();
	}
	
	private void setWidgetActivation()
	{
		if (!SwingUtilities.isEventDispatchThread())
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					setWidgetActivation();
				}
			});
			return;
		}
		m_mainPanel.validate();
		m_mainPanel.repaint();
		this.validate();
		this.repaint();
	}
	
	private void createPart1Panel()
	{
		m_panel1.removeAll();
		m_panel1.setLayout(new BoxLayout(m_panel1, BoxLayout.PAGE_AXIS));
		m_panel1.add(Box.createVerticalStrut(30));
		final JTextArea text = new JTextArea(12, 10);
		text.setWrapStyleWord(true);
		text.setLineWrap(true);
		text.setText("Welcome to Veqryn's map creator program for TripleA." +
					"\r\nThis program just runs utilities inside the triplea.jar file for you, and you could easily " +
					"run them yourself from the command line by reading the docs/developer_documentation.html" +
					"\r\n\r\nBefore you begin, go create a folder in your directory: Users\\yourname\\triplea\\maps" +
					"\r\nName the folder with a short name of your map, do not use any special characters in the name." +
					"\r\nNext, create 5 folders inside your map folder, with these names: " +
					"flags, units, baseTiles, reliefTiles, games" +
					"\r\nThen, create a text file and rename it \"map.properties\" or use one created by this utility." +
					"\r\n\r\nTo start the Map Utilities, have a png image of your map with just the territory borders " +
					"and nothing else. The borders must be in black (hex: 000000) and there should not be any " +
					"anti-aliasing (smoothing) of the lines or edges that stick out." +
					"\r\nCreate a small image of the map (approx 250 pixels wide) and name it \"smallMap.jpeg\"." +
					"\r\nPut these in the map's root folder. You can now start the map maker by clicking and filling " +
					"in the details below, before moving on to 'Step 2' and running the map utilities.");
		final JScrollPane scrollText = new JScrollPane(text);
		m_panel1.add(scrollText);
		m_panel1.add(Box.createVerticalStrut(30));
		m_panel1.add(new JLabel("Click button open up the readme file on how to make maps:"));
		final JButton helpButton = new JButton("Start Tutorial  /  Show Help Document");
		helpButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					DesktopUtilityBrowserLauncher.openFile(new File(GameRunner2.getRootFolder(), "doc" + File.separator + "map_and_map_skin_making_overview.html"));
					// DesktopUtilityBrowserLauncher.openURL(GameRunner.getRootFolder().getAbsoluteFile() + File.separator + "doc" + File.separator + "map_and_map_skin_making_overview.html");
				} catch (final Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});
		m_panel1.add(helpButton);
		m_panel1.add(Box.createVerticalStrut(30));
		m_panel1.add(new JLabel("Click button to select where your map folder is:"));
		final JButton mapFolderButton = new JButton("Select Map Folder");
		mapFolderButton.addActionListener(new AbstractAction("Select Map Folder")
		{
			private static final long serialVersionUID = 3918797244306320614L;
			
			public void actionPerformed(final ActionEvent e)
			{
				final String path = new FileSave("Where is your map's folder?", null, s_mapFolderLocation).getPathString();
				if (path != null)
				{
					final File mapFolder = new File(path);
					if (mapFolder.exists())
					{
						s_mapFolderLocation = mapFolder;
						System.setProperty(TRIPLEA_MAP_FOLDER, s_mapFolderLocation.getPath());
					}
				}
			}
		});
		m_panel1.add(mapFolderButton);
		m_panel1.add(Box.createVerticalStrut(30));
		m_panel1.add(new JLabel("Set the unit scaling (unit image zoom): "));
		m_panel1.add(new JLabel("Choose one of: 1.25, 1, 0.875, 0.8333, 0.75, 0.6666, 0.5625, 0.5"));
		final JTextField unitZoomText = new JTextField("" + s_unit_zoom);
		unitZoomText.setMaximumSize(new Dimension(100, 20));
		unitZoomText.addFocusListener(new FocusListener()
		{
			public void focusGained(final FocusEvent e)
			{
			}
			
			public void focusLost(final FocusEvent e)
			{
				try
				{
					s_unit_zoom = Math.min(4.0, Math.max(0.1, Double.parseDouble(unitZoomText.getText())));
					System.setProperty(TRIPLEA_UNIT_ZOOM, "" + s_unit_zoom);
				} catch (final Exception ex)
				{
				}
				unitZoomText.setText("" + s_unit_zoom);
			}
		});
		/*
		unitZoomText.addActionListener(new AbstractAction("Set the unit scaling (unit image zoom)")
		{
			private static final long serialVersionUID = -1453970155236023493L;
			
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					s_unit_zoom = Math.min(4.0, Math.max(0.1, Double.parseDouble(unitZoomText.getText())));
				} catch (final Exception ex)
				{
				}
				unitZoomText.setText("" + s_unit_zoom);
			}
		});*/
		m_panel1.add(unitZoomText);
		m_panel1.add(Box.createVerticalStrut(30));
		m_panel1.add(new JLabel("Set the width of the unit images: "));
		final JTextField unitWidthText = new JTextField("" + s_unit_width);
		unitWidthText.setMaximumSize(new Dimension(100, 20));
		unitWidthText.addFocusListener(new FocusListener()
		{
			public void focusGained(final FocusEvent e)
			{
			}
			
			public void focusLost(final FocusEvent e)
			{
				try
				{
					s_unit_width = Math.min(400, Math.max(1, Integer.parseInt(unitWidthText.getText())));
					System.setProperty(TRIPLEA_UNIT_WIDTH, "" + s_unit_width);
				} catch (final Exception ex)
				{
				}
				unitWidthText.setText("" + s_unit_width);
			}
		});
		/*
		unitWidthText.addActionListener(new AbstractAction("Set the width of the unit images")
		{
			private static final long serialVersionUID = 7853232919699181788L;
			
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					s_unit_width = Math.min(400, Math.max(1, Integer.parseInt(unitWidthText.getText())));
				} catch (final Exception ex)
				{
				}
				unitWidthText.setText("" + s_unit_width);
			}
		});*/
		m_panel1.add(unitWidthText);
		m_panel1.add(Box.createVerticalStrut(30));
		m_panel1.add(new JLabel("Set the height of the unit images: "));
		final JTextField unitHeightText = new JTextField("" + s_unit_height);
		unitHeightText.setMaximumSize(new Dimension(100, 20));
		unitHeightText.addFocusListener(new FocusListener()
		{
			public void focusGained(final FocusEvent e)
			{
			}
			
			public void focusLost(final FocusEvent e)
			{
				try
				{
					s_unit_height = Math.min(400, Math.max(1, Integer.parseInt(unitHeightText.getText())));
					System.setProperty(TRIPLEA_UNIT_HEIGHT, "" + s_unit_height);
				} catch (final Exception ex)
				{
				}
				unitHeightText.setText("" + s_unit_height);
			}
		});
		/*
		unitHeightText.addActionListener(new AbstractAction("Set the height of the unit images")
		{
			private static final long serialVersionUID = 3141138787371791705L;
			
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					s_unit_height = Math.min(400, Math.max(1, Integer.parseInt(unitHeightText.getText())));
				} catch (final Exception ex)
				{
				}
				unitHeightText.setText("" + s_unit_height);
			}
		});*/
		m_panel1.add(unitHeightText);
		m_panel1.add(Box.createVerticalStrut(30));
		m_panel1.add(new JLabel("<html>Here you can set the 'max memory' that utilities like the Polygon Grabber will use.<br>" +
					"This is useful is you have a very large map, or ever get any Java Heap Space errors.</html>"));
		m_panel1.add(new JLabel("Set the amount of memory to use when running new processes (in megabytes [mb]):"));
		final JTextField memoryText = new JTextField("" + (s_memory / (1024 * 1024)));
		memoryText.setMaximumSize(new Dimension(100, 20));
		memoryText.addFocusListener(new FocusListener()
		{
			public void focusGained(final FocusEvent e)
			{
			}
			
			public void focusLost(final FocusEvent e)
			{
				try
				{
					s_memory = (long) 1024 * 1024 * Math.min(4096, Math.max(256, Integer.parseInt(memoryText.getText())));
				} catch (final Exception ex)
				{
				}
				memoryText.setText("" + (s_memory / (1024 * 1024)));
			}
		});
		/*
		memoryText.addActionListener(new AbstractAction("Set the amount of memory to use when running these utilities (in megabytes [mb])")
		{
			private static final long serialVersionUID = 4620736097666227543L;
			
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					s_memory = (long) 1024 * 1024 * Math.min(4096, Math.max(256, Integer.parseInt(memoryText.getText())));
				} catch (final Exception ex)
				{
				}
				memoryText.setText("" + (s_memory / (1024 * 1024)));
			}
		});*/
		m_panel1.add(memoryText);
		final JCheckBox runTypeBox = new JCheckBox("Run All Utilities as Separate Processes");
		runTypeBox.setSelected(s_runUtilitiesAsSeperateProcesses);
		runTypeBox.addActionListener(new AbstractAction("Run All Utilities as Separate Processes")
		{
			private static final long serialVersionUID = 363422421871497915L;
			
			public void actionPerformed(final ActionEvent e)
			{
				s_runUtilitiesAsSeperateProcesses = runTypeBox.isSelected();
			}
		});
		m_panel1.add(runTypeBox);
		m_panel1.add(Box.createVerticalStrut(30));
		m_panel1.validate();
	}
	
	private void createPart2Panel()
	{
		m_panel2.removeAll();
		m_panel2.setLayout(new BoxLayout(m_panel2, BoxLayout.PAGE_AXIS));
		m_panel2.add(Box.createVerticalStrut(30));
		m_panel2.add(new JLabel("Map Skin Utilities:"));
		m_panel2.add(Box.createVerticalStrut(30));
		final JButton mapPropertiesMakerButton = new JButton("Run the Map Properties Maker");
		mapPropertiesMakerButton.addActionListener(new AbstractAction("Run the Map Properties Maker")
		{
			private static final long serialVersionUID = -5708777348010034859L;
			
			public void actionPerformed(final ActionEvent e)
			{
				if (s_runUtilitiesAsSeperateProcesses)
					runUtility("util.image.MapPropertiesMaker");
				else
				{
					(new Thread()
					{
						@Override
						public void run()
						{
							MapPropertiesMaker.main(new String[0]);
						}
					}).start();
				}
			}
		});
		m_panel2.add(mapPropertiesMakerButton);
		m_panel2.add(Box.createVerticalStrut(30));
		final JButton centerPickerButton = new JButton("Run the Center Picker");
		centerPickerButton.addActionListener(new AbstractAction("Run the Center Picker")
		{
			private static final long serialVersionUID = -2070004374472175438L;
			
			public void actionPerformed(final ActionEvent e)
			{
				if (s_runUtilitiesAsSeperateProcesses)
					runUtility("util.image.CenterPicker");
				else
				{
					(new Thread()
					{
						@Override
						public void run()
						{
							CenterPicker.main(new String[0]);
						}
					}).start();
				}
			}
		});
		m_panel2.add(centerPickerButton);
		m_panel2.add(Box.createVerticalStrut(30));
		final JButton polygonGrabberButton = new JButton("Run the Polygon Grabber");
		polygonGrabberButton.addActionListener(new AbstractAction("Run the Polygon Grabber")
		{
			private static final long serialVersionUID = -5708777348010034859L;
			
			public void actionPerformed(final ActionEvent e)
			{
				if (s_runUtilitiesAsSeperateProcesses)
					runUtility("util.image.PolygonGrabber");
				else
				{
					(new Thread()
					{
						@Override
						public void run()
						{
							PolygonGrabber.main(new String[0]);
						}
					}).start();
				}
			}
		});
		m_panel2.add(polygonGrabberButton);
		m_panel2.add(Box.createVerticalStrut(30));
		final JButton autoPlacerButton = new JButton("Run the Automatic Placement Finder");
		autoPlacerButton.addActionListener(new AbstractAction("Run the Automatic Placement Finder")
		{
			private static final long serialVersionUID = 7557803418683843877L;
			
			public void actionPerformed(final ActionEvent e)
			{
				if (s_runUtilitiesAsSeperateProcesses)
					runUtility("util.image.AutoPlacementFinder");
				else
				{
					(new Thread()
					{
						@Override
						public void run()
						{
							AutoPlacementFinder.main(new String[0]);
						}
					}).start();
				}
			}
		});
		m_panel2.add(autoPlacerButton);
		m_panel2.add(Box.createVerticalStrut(30));
		final JButton placementPickerButton = new JButton("Run the Placement Picker");
		placementPickerButton.addActionListener(new AbstractAction("Run the Placement Picker")
		{
			private static final long serialVersionUID = 2456185407945946528L;
			
			public void actionPerformed(final ActionEvent e)
			{
				if (s_runUtilitiesAsSeperateProcesses)
					runUtility("util.image.PlacementPicker");
				else
				{
					(new Thread()
					{
						@Override
						public void run()
						{
							PlacementPicker.main(new String[0]);
						}
					}).start();
				}
			}
		});
		m_panel2.add(placementPickerButton);
		m_panel2.add(Box.createVerticalStrut(30));
		final JButton tileBreakerButton = new JButton("Run the Tile Image Breaker");
		tileBreakerButton.addActionListener(new AbstractAction("Run the Tile Image Breaker")
		{
			private static final long serialVersionUID = 8636496829644907047L;
			
			public void actionPerformed(final ActionEvent e)
			{
				if (s_runUtilitiesAsSeperateProcesses)
					runUtility("util.image.TileImageBreaker");
				else
				{
					(new Thread()
					{
						@Override
						public void run()
						{
							try
							{
								TileImageBreaker.main(new String[0]);
							} catch (final Exception e)
							{
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
		m_panel2.add(tileBreakerButton);
		m_panel2.add(Box.createVerticalStrut(30));
		final JButton decorationPlacerButton = new JButton("Run the Decoration Placer");
		decorationPlacerButton.addActionListener(new AbstractAction("Run the Decoration Placer")
		{
			private static final long serialVersionUID = 8981678371888002420L;
			
			public void actionPerformed(final ActionEvent e)
			{
				if (s_runUtilitiesAsSeperateProcesses)
					runUtility("util.image.DecorationPlacer");
				else
				{
					(new Thread()
					{
						@Override
						public void run()
						{
							try
							{
								DecorationPlacer.main(new String[0]);
							} catch (final Exception e)
							{
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
		m_panel2.add(decorationPlacerButton);
		m_panel2.add(Box.createVerticalStrut(30));
		m_panel2.validate();
	}
	
	private void createPart3Panel()
	{
		m_panel3.removeAll();
		m_panel3.setLayout(new BoxLayout(m_panel3, BoxLayout.PAGE_AXIS));
		m_panel3.add(Box.createVerticalStrut(30));
		m_panel3.add(new JLabel("Game XML Utilities:"));
		m_panel3.add(new JLabel("Sorry but for now the only XML creator is Wisconsin's 'Part 2' of his map maker."));
		m_panel3.add(new JLabel("You can try downloading it from our dev forum: http://triplea.sourceforge.net/mywiki/Forum"));
		m_panel3.add(Box.createVerticalStrut(30));
		final JButton goToWebButton = new JButton("Go To Dev Forum");
		goToWebButton.addActionListener(new AbstractAction("Go To Dev Forum")
		{
			private static final long serialVersionUID = 5059004450673029377L;
			
			public void actionPerformed(final ActionEvent e)
			{
				try
				{
					DesktopUtilityBrowserLauncher.openURL("http://triplea.sourceforge.net/mywiki/Forum");
				} catch (final Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});
		m_panel3.add(goToWebButton);
		m_panel3.add(Box.createVerticalStrut(30));
		final JButton connectionFinderButton = new JButton("Run the Connection Finder");
		connectionFinderButton.addActionListener(new AbstractAction("Run the Connection Finder")
		{
			private static final long serialVersionUID = 8778155499250138516L;
			
			public void actionPerformed(final ActionEvent e)
			{
				if (s_runUtilitiesAsSeperateProcesses)
					runUtility("util.image.ConnectionFinder");
				else
				{
					(new Thread()
					{
						@Override
						public void run()
						{
							try
							{
								ConnectionFinder.main(new String[0]);
							} catch (final Exception e)
							{
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
		m_panel3.add(connectionFinderButton);
		m_panel3.add(Box.createVerticalStrut(30));
		m_panel3.validate();
	}
	
	private void createPart4Panel()
	{
		m_panel4.removeAll();
		m_panel4.setLayout(new BoxLayout(m_panel4, BoxLayout.PAGE_AXIS));
		m_panel4.add(Box.createVerticalStrut(30));
		m_panel4.add(new JLabel("Other or Optional Utilities:"));
		m_panel4.add(Box.createVerticalStrut(30));
		final JButton reliefBreakerButton = new JButton("Run the Relief Image Breaker");
		reliefBreakerButton.addActionListener(new AbstractAction("Run the Relief Image Breaker")
		{
			private static final long serialVersionUID = 8981678371888002420L;
			
			public void actionPerformed(final ActionEvent e)
			{
				if (s_runUtilitiesAsSeperateProcesses)
					runUtility("util.image.ReliefImageBreaker");
				else
				{
					(new Thread()
					{
						@Override
						public void run()
						{
							try
							{
								ReliefImageBreaker.main(new String[0]);
							} catch (final Exception e)
							{
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
		m_panel4.add(reliefBreakerButton);
		m_panel4.add(Box.createVerticalStrut(30));
		final JButton imageShrinkerButton = new JButton("Run the Image Shrinker");
		imageShrinkerButton.addActionListener(new AbstractAction("Run the Image Shrinker")
		{
			private static final long serialVersionUID = 8778155499250138516L;
			
			public void actionPerformed(final ActionEvent e)
			{
				if (s_runUtilitiesAsSeperateProcesses)
					runUtility("util.image.ImageShrinker");
				else
				{
					(new Thread()
					{
						@Override
						public void run()
						{
							try
							{
								ImageShrinker.main(new String[0]);
							} catch (final Exception e)
							{
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
		m_panel4.add(imageShrinkerButton);
		m_panel4.add(Box.createVerticalStrut(30));
		final JButton tileImageReconstructorButton = new JButton("Run the Tile Image Reconstructor");
		tileImageReconstructorButton.addActionListener(new AbstractAction("Run the Tile Image Reconstructor")
		{
			private static final long serialVersionUID = -1743544461266802405L;
			
			public void actionPerformed(final ActionEvent e)
			{
				if (s_runUtilitiesAsSeperateProcesses)
					runUtility("util.image.TileImageReconstructor");
				else
				{
					(new Thread()
					{
						@Override
						public void run()
						{
							try
							{
								TileImageReconstructor.main(new String[0]);
							} catch (final Exception e)
							{
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
		m_panel4.add(tileImageReconstructorButton);
		m_panel4.add(Box.createVerticalStrut(30));
		m_panel4.validate();
	}
	
	private void runUtility(final String javaClass)
	{
		final List<String> commands = new ArrayList<String>();
		ProcessRunnerUtil.populateBasicJavaArgs(commands, s_memory);
		if (s_mapFolderLocation != null && s_mapFolderLocation.exists())
		{
			commands.add("-D" + TRIPLEA_MAP_FOLDER + "=" + s_mapFolderLocation.getAbsolutePath()); // no need for quotes, that will just screw up the process builder
		}
		commands.add("-D" + TRIPLEA_UNIT_ZOOM + "=" + s_unit_zoom);
		commands.add("-D" + TRIPLEA_UNIT_WIDTH + "=" + s_unit_width);
		commands.add("-D" + TRIPLEA_UNIT_HEIGHT + "=" + s_unit_height);
		commands.add(javaClass);
		ProcessRunnerUtil.exec(commands);
		// example: java -classpath triplea.jar -Dtriplea.map.folder="C:/Users" util/image/CenterPicker
	}
	
	private static String getValue(final String arg)
	{
		final int index = arg.indexOf('=');
		if (index == -1)
			return "";
		return arg.substring(index + 1);
	}
	
	private static void handleCommandLineArgs(final String[] args)
	{
		final String[] properties = getProperties();
		if (args.length == 1)
		{
			String value;
			if (args[0].startsWith(TRIPLEA_MAP_FOLDER))
			{
				value = getValue(args[0]);
			}
			else
			{
				value = args[0];
			}
			final File mapFolder = new File(value);
			if (mapFolder.exists())
				s_mapFolderLocation = mapFolder;
			else
				System.out.println("Could not find directory: " + value);
		}
		
		boolean usagePrinted = false;
		for (int argIndex = 0; argIndex < args.length; argIndex++)
		{
			boolean found = false;
			String arg = args[argIndex];
			final int indexOf = arg.indexOf('=');
			if (indexOf > 0)
			{
				arg = arg.substring(0, indexOf);
				for (int propIndex = 0; propIndex < properties.length; propIndex++)
				{
					if (arg.equals(properties[propIndex]))
					{
						final String value = getValue(args[argIndex]);
						System.getProperties().setProperty(properties[propIndex], value);
						System.out.println(properties[propIndex] + ":" + value);
						found = true;
						break;
					}
				}
			}
			if (!found)
			{
				System.out.println("Unrecogized:" + args[argIndex]);
				if (!usagePrinted)
				{
					usagePrinted = true;
					System.out.println("Arguments\r\n"
								+ "   " + TRIPLEA_MAP_FOLDER + "=<FILE_PATH>\r\n"
								+ "   " + TRIPLEA_UNIT_ZOOM + "=<UNIT_ZOOM_LEVEL>\r\n"
								+ "   " + TRIPLEA_UNIT_WIDTH + "=<UNIT_WIDTH>\r\n"
								+ "   " + TRIPLEA_UNIT_HEIGHT + "=<UNIT_HEIGHT>\r\n");
				}
			}
		}
		final String folderString = System.getProperty(TRIPLEA_MAP_FOLDER);
		if (folderString != null && folderString.length() > 0)
		{
			final File mapFolder = new File(folderString);
			if (mapFolder.exists())
				s_mapFolderLocation = mapFolder;
			else
				System.out.println("Could not find directory: " + folderString);
		}
		final String zoomString = System.getProperty(TRIPLEA_UNIT_ZOOM);
		if (zoomString != null && zoomString.length() > 0)
		{
			try
			{
				s_unit_zoom = Double.parseDouble(zoomString);
				System.out.println("Unit Zoom Percent to use: " + s_unit_zoom);
			} catch (final Exception ex)
			{
				System.err.println("Not a decimal percentage: " + zoomString);
			}
		}
		final String widthString = System.getProperty(TRIPLEA_UNIT_WIDTH);
		if (widthString != null && widthString.length() > 0)
		{
			try
			{
				s_unit_width = Integer.parseInt(widthString);
				System.out.println("Unit Width to use: " + s_unit_width);
			} catch (final Exception ex)
			{
				System.err.println("Not an integer: " + widthString);
			}
		}
		final String heightString = System.getProperty(TRIPLEA_UNIT_HEIGHT);
		if (heightString != null && heightString.length() > 0)
		{
			try
			{
				s_unit_height = Integer.parseInt(heightString);
				System.out.println("Unit Height to use: " + s_unit_height);
			} catch (final Exception ex)
			{
				System.err.println("Not an integer: " + heightString);
			}
		}
	}
}
