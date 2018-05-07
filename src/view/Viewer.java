package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import controller.AboutButtonActionListener;
import controller.FindButtonActionListener;
import controller.FindInFilesActionListener;
import finder.Finder;

/**
 * Front-end for the Finder functionality.
 * 
 * @author James P. Taylor
 */
public class Viewer extends JPanel {

	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Default s/n for this class.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Object array containing basic information about the application.
	 */
	public static final Object[] appInfo = {
		"Nested Archive Searcher"	// [0] == Application Name
	,	"2.00"						// [1] == Application Version
	,	"04/30/2015"				// [2] == Application Build Date
	,	"jptaylor03@yahoo.com"		// [3] == Application Author Contact
	,	Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("resources/finder.png")) // [4] == Application Icon
	};
	
	/**
	 * Obtain formatted version of the application info.
	 * 
	 * @return String containing application info formatted for display.
	 */
	public static String getAppInfoFormatted() {
		return	"Name: "	+ appInfo[0] + "\n" +
				"Version: "	+ appInfo[1] + "\n" +
				"Build: "	+ appInfo[2] + "\n" +
				"Author: "	+ appInfo[3] + "\n" +
				"Supported Archive Formats: " + Arrays.toString(Finder.ARCHIVE_FILE_EXTENSIONS);
	}
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("view");
	
	/**
	 * Ratio which determines the default size of the window when the application first opens.
	 */
	private static float DEFAULT_WINDOW_RATIO = 0.85F;
	
	/**
	 * Viewer instance of this class.
	 */
	private static Viewer viewer = null;
	public static Viewer getInstance() {
		return viewer;
	}
	
	/**
	 * Frame instance containing a reference to the frame used by this panel.
	 */
	public static JFrame frame = new JFrame();
	
	/**
	 * Handle to Viewer's "path" field.
	 */
	private JTextField pathEntry = null;
	
	/**
	 * Handle to Viewer's "filename" field.
	 */
	private JTextField filenameEntry = null;
	
	/**
	 * Handle to Viewer's "leafnode" checkbox.
	 */
	private JCheckBox leafNodeCheckbox = null;
	
	/**
	 * Handle to Viewer's "findInFiles" panel.
	 */
	public JPanel findInFilesPanel = null;
	
	/**
	 * Handle to Viewer's "findInFiles" checkbox.
	 */
	private JCheckBox findInFilesCheckbox = null;
	
	/**
	 * Handle to Viewer's "findInFilesLabel" field.
	 */
	private JLabel findInFilesLabel = null;
	
	/**
	 * Handle to Viewer's "findInFilesEntry" field.
	 */
	private JTextField findInFilesEntry = null;
	
	/**
	 * Handle to Viewer's "findInFilesInsensitive" checkbox.
	 */
	private JCheckBox caseInsensitiveCheckbox = null;
	
	/**
	 * Handle to Viewer's "find" button.
	 */
	private JButton findButton = null;
	
	/**
	 * Handle to Viewer's "results" field.
	 */
	public JTextArea consoleOutput = null;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public Viewer() {
		super(new BorderLayout());
	}
	
	/********************
	 * Helper method(s) *
	 ********************/
	
	/**
	 * Main entry point.
	 * <ol>
	 *  NOTE: Possible command-line arguments:
	 *  <li>path to be searched</li>
	 *  <li>regular expression (of entries) to match against</li>
	 * </ol>
	 * 
	 * @param args String array for command-line arguments.
	 */
	public static void main(String[] args) {
		//if (args == null || args.length < 2 || "".equals(args[0].trim()) || "".equals(args[1].trim())) {
		//	JOptionPane.showMessageDialog(frame, "Syntax: java -jar finder.jar (path to search) (regular expression for entries to find) (match against leaf node only) [regular expression for text to find] [whether search should ignore case]\nExample: java -jar finder.jar C:\\temp\\myEAR.ear .*[.]properties true label[.]required false\nExample: java -jar finder.jar c:/temp/myFolder .*[.]log false Exception true");
		//	return;
		//}
		
		String  path            = args != null && args.length >= 1?args[0]:"";
		String  filename        = args != null && args.length >= 2?args[1]:"";
		boolean leafNodeOnly    = args != null && args.length >= 3?Boolean.parseBoolean(args[2]):false;
		String  findInFiles     = args != null && args.length >= 4?args[3]:null;
		boolean caseInsensitive = args != null && args.length >= 5?Boolean.parseBoolean(args[4]):false;
		
		// Create and set up the window
		if (frame.getTitle() == null || "".equals(frame.getTitle().trim())) {
			frame.setTitle(appInfo[0]+"");
			frame.setVisible(false);
		//	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					frame.setVisible(false);
					// Perform any other operations you might need before exit.
					System.exit(0);
				}
			});
		}

		// Begin creating and setting up the content pane
		if (viewer == null) {
			viewer = new Viewer();
			viewer.setOpaque(true); // Content pane(s) must be opaque
			viewer.prepareViewer(path, filename, leafNodeOnly, findInFiles, caseInsensitive);
		}
		
		// Display the frame (and viewer panel)
		frame.setContentPane(viewer);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		//frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setIconImage((Image)appInfo[4]);
	}
	
	/**
	 * Creates the GUI shown inside the frame's content pane.
	 */
	private void prepareViewer(String path, String filename, boolean leafNodeOnly, String findInFiles, boolean caseInsensitive) {
		logger.info("Preparing viewer: START");
		
		final JFileChooser fc = new JFileChooser();
		
		JLabel pathLabel = new JLabel("Path: ");
		pathLabel.setToolTipText("Path to search");
		pathEntry = new JTextField();
		pathEntry.setColumns(50);
		pathEntry.setText(path);
		pathEntry.setToolTipText(pathLabel.getToolTipText());
		pathEntry.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
		        if (e.getKeyCode()==KeyEvent.VK_ENTER){
		        	//Viewer.performFind();
		            findButton.doClick();
		        }
			}
		});
		JButton pathBrowse = new JButton("...");
		pathBrowse.setToolTipText("Browse to select a path to search");
		pathBrowse.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				fc.setCurrentDirectory(("".equals(pathEntry.getText().trim())?null:new File(pathEntry.getText())));
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		        int returnVal = fc.showOpenDialog(frame);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            pathEntry.setText(file.getAbsolutePath());
		        }
			}
		});

		JLabel filenameLabel = new JLabel("Filename: ");
		filenameLabel.setToolTipText("Regular expression to match against folder/file names (within the path)");
		filenameEntry = new JTextField();
		filenameEntry.setColumns(50);
		filenameEntry.setText(filename);
		filenameEntry.setToolTipText(filenameLabel.getToolTipText());
		filenameEntry.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
		        if (e.getKeyCode()==KeyEvent.VK_ENTER){
		            //Viewer.performFind();
		        	findButton.doClick();
		        }
			}
		});
		JButton filenameBrowse = new JButton("...");
		filenameBrowse.setToolTipText("Browse to select a filename to search for (within the path)");
		filenameBrowse.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				fc.setCurrentDirectory(("".equals(pathEntry.getText().trim())?null:new File(pathEntry.getText())));
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		        int returnVal = fc.showOpenDialog(frame);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            filenameEntry.setText(file.getName());
		        }
			}
		});

		findInFilesLabel = new JLabel("Find Text: ");
		findInFilesLabel.setToolTipText("Regular expression to match against file contents (within the path)");
		findInFilesEntry = new JTextField();
		findInFilesEntry.setColumns(50);
		findInFilesEntry.setText(findInFiles);
		findInFilesEntry.setToolTipText(findInFilesLabel.getToolTipText());
		findInFilesEntry.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
		        if (e.getKeyCode()==KeyEvent.VK_ENTER){
		            //Viewer.performFind();
		        	findButton.doClick();
		        }
			}
		});

		caseInsensitiveCheckbox = new JCheckBox("Ignore case");
		caseInsensitiveCheckbox.setToolTipText("Ignore case while matching against file contents");
		caseInsensitiveCheckbox.setSelected(caseInsensitive);

		leafNodeCheckbox = new JCheckBox("Match against leaf node only");
		leafNodeCheckbox.setToolTipText("Match against the path's leaf node only (versus the entire path)");
		leafNodeCheckbox.setSelected(leafNodeOnly);

		findInFilesCheckbox = new JCheckBox("Find specific text within files");
		findInFilesCheckbox.setToolTipText("Search for specific text within file contents (versus matching filename only)");
		findInFilesCheckbox.setSelected(findInFiles != null && !"".equals(findInFiles.trim()));
		findInFilesCheckbox.addActionListener(new FindInFilesActionListener());

		findButton = new JButton("Find");
		findButton.setToolTipText("Perform the find using current parameters");
		findButton.addActionListener(new FindButtonActionListener());
		
		JButton aboutButton = new JButton("About");
		aboutButton.setToolTipText("Display information about the application");
		aboutButton.addActionListener(new AboutButtonActionListener());
		
		consoleOutput = new JTextArea(40, 140);
		consoleOutput.setToolTipText("Results of Find");
		consoleOutput.setFont(new Font("Courier", Font.PLAIN, 11));
		consoleOutput.setEditable(false);
		
		JScrollPane scrollPanel = new JScrollPane(consoleOutput);
		
		JPanel boxPanel = new JPanel();
		boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.PAGE_AXIS));
		
		//JPanel controlPanel = new JPanel(new FlowLayout());
		//boxPanel.add(controlPanel); //, BorderLayout.PAGE_START
		
		JPanel pathPanel = new JPanel(new FlowLayout());
		pathPanel.add(pathLabel);
		pathPanel.add(pathEntry);
		pathPanel.add(pathBrowse);
		
		JPanel filenamePanel = new JPanel(new FlowLayout());
		filenamePanel.add(filenameLabel);
		filenamePanel.add(filenameEntry);
		filenamePanel.add(filenameBrowse);
		
		findInFilesPanel = new JPanel(new FlowLayout());
		findInFilesPanel.add(findInFilesLabel);
		findInFilesPanel.add(findInFilesEntry);
		findInFilesPanel.add(caseInsensitiveCheckbox);
		findInFilesPanel.setVisible(findInFiles != null&&!"".equals(findInFiles.trim()));
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(leafNodeCheckbox);
		buttonPanel.add(findInFilesCheckbox);
		buttonPanel.add(findButton);
		buttonPanel.add(aboutButton);
		
		boxPanel.add(pathPanel);
		boxPanel.add(filenamePanel);
		boxPanel.add(findInFilesPanel);
		boxPanel.add(buttonPanel);
		
		viewer.add(boxPanel, BorderLayout.NORTH);
		viewer.add(scrollPanel, BorderLayout.CENTER);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension windowSize = new Dimension((int)(screenSize.getWidth()*DEFAULT_WINDOW_RATIO), (int)(screenSize.getHeight()*DEFAULT_WINDOW_RATIO)); //new Dimension(1024, 768)
		viewer.setPreferredSize(windowSize);
		logger.info("Preparing viewer: END");
	}
	
	/**
	 * Delegate to the Finder class to perform the actual search.
	 */
	public static void performFind() {
		if ("".equals(viewer.pathEntry.getText().trim()) || "".equals(viewer.filenameEntry.getText().trim())) {
			JOptionPane.showMessageDialog(frame, "Path and Filename values are both required.");
		} else {
			try {
				viewer.consoleOutput.setForeground(Color.BLACK);
				viewer.consoleOutput.setText("Searching '" + viewer.pathEntry.getText() + "' for" + (viewer.leafNodeCheckbox.isSelected()?" LEAFNODE-ONLY":"") + " entries named like '" + viewer.filenameEntry.getText() + "'" + (viewer.findInFilesPanel.isVisible()&&viewer.findInFilesEntry.getText()!=null&&!"".equals(viewer.findInFilesEntry.getText().trim())?" and containing '" + viewer.findInFilesEntry.getText() + "'" + (viewer.caseInsensitiveCheckbox.isSelected()?" (CASE INSENSITIVE)":""):"") + "...");
				List<String> result = new Finder().execute(viewer.pathEntry.getText(), viewer.filenameEntry.getText(), viewer.leafNodeCheckbox.isSelected(), (viewer.findInFilesPanel.isVisible()&&viewer.findInFilesEntry.getText()!=null&&!"".equals(viewer.findInFilesEntry.getText().trim())?viewer.findInFilesEntry.getText():null), (viewer.caseInsensitiveCheckbox.isSelected()?true:false));
				viewer.consoleOutput.append("\nFound " + result.size() + (viewer.leafNodeCheckbox.isSelected()?" LEAFNODE-ONLY":"") + " entries in '" + viewer.pathEntry.getText() + "' named like '" + viewer.filenameEntry.getText() + "'" + (viewer.findInFilesPanel.isVisible()&&viewer.findInFilesEntry.getText()!=null&&!"".equals(viewer.findInFilesEntry.getText().trim())?" and containing '" + viewer.findInFilesEntry.getText() + "'" + (viewer.caseInsensitiveCheckbox.isSelected()?" (CASE INSENSITIVE)":""):"") + ".");
				for (int x = 0; x < result.size(); x++) {
					String entry = result.get(x);
					viewer.consoleOutput.append("\n(" + x + ") " + entry);
				}
			} catch (Exception ex) {
				//logger.error("Error invoking Finder.execute(" + viewer.pathEntry.getText() + ", " + viewer.regexEntry.getText() + ")", ex);
				viewer.consoleOutput.setForeground(Color.RED);
				viewer.consoleOutput.append("\n*** ERROR ***\n" + ex.toString());
			}
			viewer.consoleOutput.setCaretPosition(0);
		}
	}
}
