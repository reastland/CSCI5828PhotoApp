import guiHelpers.MultiThreadedImageLoader;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.DefaultCaret;

import org.apache.commons.collections4.list.TreeList;
import org.jinstagram.Instagram;
import org.jinstagram.entity.users.feed.MediaFeedData;

import MetaDataDownloaders.DateByLocationMetaDataDownloader;
import MetaDataDownloaders.GeoPointMetaDataDownloader;
import MetaDataDownloaders.LikesMetaDataDownloader;
import MetaDataDownloaders.MetaDataDownloader;
import MetaDataDownloaders.PhotographerMetaDataDownloader;
import MetaDataDownloaders.PopularMetaDataDownloader;
import MetaDataDownloaders.RecentMetaDataDownloader;
import MetaDataDownloaders.TagMetaDataDownloader;
import MetaDataDownloaders.TagsMetaDataDownloader;

public class GUI extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JFrame frame;
	private JPanel bottomPanel = new JPanel();
	private GridBagLayout gridBagLayout = new GridBagLayout();
	private GridBagConstraints constraints = new GridBagConstraints();
//	private GridLayout gridLayout;
	private FlowLayout flowLayout;
	private JPanel centerPanel = new JPanel(new CardLayout());
	private JPanel controlPanel = new JPanel();
	private JPanel imagesPanel = new JPanel();
	private JPanel imagesContainer = new JPanel();
	private JPanel imagesButtonHolder = new JPanel();
	private JPanel infoPanel = new JPanel();
	private JPanel infoContainer = new JPanel();
	private JPanel infoButtonHolder = new JPanel();
	private JTextArea textArea;
	private JScrollPane outputTextArea;
	private JTextField txtNumImagesRequested;
	private JTextField txtNumImagesPerRow;
	private JLabel lblPhotographer;
	private JLabel lblTag;
	private JLabel lblGeoPoint;
	private JLabel lblLocation;
	private JLabel lblIDs;
	private JLabel lblDates;
	private JLabel lblVerificationCode;
	private JTextField txtPhotographer;
	private JTextField txtTag;
	private JTextField txtLat;
	private JTextField txtLng;
	private JTextField txtLocation;
	private JTextField txtMinId;
	private JTextField txtMaxId;
	private JTextField txtStartDate;
	private JTextField txtEndDate;
	private JTextField txtVerificationCode;
	private JScrollPane scroller;
	private Dimension standardButtonSize;
	private JList<String> algChooser;
	
	static List<MediaFeedData> imagesMetaDataList;
	private Instagram instagram;
	private int numImagesRequested;
	final private int defaultNumImages = 25;
	private Boolean verbose = true;
	private Boolean doSaveMetaDataToDisk = false;
	private Boolean appendToExistingList = false;
	private String[] filterByChoices;
	private String tag;//For image tags search
	private String[] tags; // for searching on multiple tags
	//For searching by date variables:
	private int minID;
	private int maxID;
	private Date maxDate;
	private Date minDate;
	private String location;
	private AccessTokenizer tokenizer;
	
	final private String IMAGES_PANEL = "images_panel";
	final private String CONTROL_PANEL = "control_panel";
	final private String INFO_PANEL = "info_panel";
	
	public GUI(Instagram i) {

		instagram = i;
		
		standardButtonSize = new Dimension(250, 30);

		filterByChoices = new String[6];
		filterByChoices[0] = "Popular";
		filterByChoices[1] = "Photographer";
		filterByChoices[2] = "Tags";
		filterByChoices[3] = "GeoPoint";
		filterByChoices[4] = "Likes";
		filterByChoices[5] = "Date";
		
		makeMainFrame();
		makePanels();
		controlPanelLayout();
		imagesPanelLayout();
		infoPanelLayout();
		
		JButton btnExit = new JButton("Exit");
		btnExit.setPreferredSize(standardButtonSize);
		btnExit.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            System.exit(0);
	        }
		});
		
		bottomPanel.add(btnExit);

		frame.setVisible(true);
		
		imagesMetaDataList = new TreeList<MediaFeedData>();

		hideAll();
	}
	
	private MetaDataDownloader getDownloader (int index) {
		
		double [] geoPoint = new double[2];
		geoPoint[0] = Double.parseDouble(txtLat.getText());
		geoPoint[1] = Double.parseDouble(txtLng.getText());
	
		location = txtLocation.getText();
		minID = Integer.parseInt(txtMinId.getText());
		maxID = Integer.parseInt(txtMaxId.getText());
		maxDate = null;
		minDate = null;
		
		try {
			minDate = new SimpleDateFormat("MM/dd/yyyy").parse(txtStartDate.getText());
			maxDate = new SimpleDateFormat("MM/dd/yyyy").parse(txtEndDate.getText());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		switch (index) {
			case 0: return new PopularMetaDataDownloader(instagram, numImagesRequested, textArea);
			case 1: return new PhotographerMetaDataDownloader(instagram, numImagesRequested, textArea, txtPhotographer.getText().trim());
			case 2: 
				if (tags.length == 1)
					return new TagMetaDataDownloader(instagram, numImagesRequested, textArea, tag);
				else // user has entered multiple tags - this class handles that
					return new TagsMetaDataDownloader(instagram, numImagesRequested, textArea, tags);
			
			case 3: return new GeoPointMetaDataDownloader(instagram, numImagesRequested, textArea, geoPoint);
			case 4: return new LikesMetaDataDownloader(instagram, numImagesRequested, textArea, txtVerificationCode.getText().trim());
			case 5: return new RecentMetaDataDownloader(instagram, numImagesRequested, textArea);
			case 6: return new DateByLocationMetaDataDownloader(instagram, numImagesRequested, textArea, location, minID, maxID, maxDate, minDate);
			default: return new PopularMetaDataDownloader(instagram, numImagesRequested, textArea);
		}

	}
	
	private void prepareForMetaDataSearch() {
        try {
        	numImagesRequested = Integer.parseInt(txtNumImagesRequested.getText());
        } catch (NumberFormatException ex) {
        	textArea.append("Please enter a number.\n");
        	return;
        }
        
        //Set user requested tag, there are some restrictions here in that users have like a parental filter set for them
        tag = txtTag.getText().replaceAll("\\s",""); // remove any whitespace     
        tags = tag.split(","); // multiple tags must be comma delimited
               
        // Checks for valid photographer
        if (algChooser.getSelectedIndex() == 1 && txtPhotographer.getText().trim() == "") {
        	textArea.append("Please enter a photographer.\n");
        	return;
        }

        if (numImagesRequested < 1) {
        	textArea.append("You must enter a number > 0\n");
        	return;
        }

        StringBuilder s = new StringBuilder(); 
        s.append("Fetching " + numImagesRequested + " Metadata Objects from server...\n");
        s.append((doSaveMetaDataToDisk) ? "Saving" : "Not saving");
        s.append(" metadata to disk.\n");
        System.out.println(s);
        textArea.append(s.toString());

        String filterChoice = algChooser.getSelectedValue();
        System.out.println(filterChoice);

        MetaDataDownloader downloader = getDownloader(algChooser.getSelectedIndex());
        
        try {
	        if (appendToExistingList)
				imagesMetaDataList = downloader.fetchImageMetaData(verbose, imagesMetaDataList);
			else 
	        	imagesMetaDataList = downloader.fetchImageMetaData(verbose, null);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
		if (doSaveMetaDataToDisk)
			writeMetadataToDisk();
		
		frame.setVisible(true);				
	}
	
	private Dimension calcPhotoSize() {
		Dimension photoSize = new Dimension();
		int numImagesPerRow = Integer.parseInt(txtNumImagesPerRow.getText());
		int widthOfScrollBar = scroller.getVerticalScrollBar().getWidth()+25;
		int horizontalSpace = frame.getWidth() - widthOfScrollBar;
		int numberOfRows = imagesMetaDataList.size() / numImagesPerRow;
		
		photoSize.width = (horizontalSpace - (numImagesPerRow * flowLayout.getHgap())) / numImagesPerRow;
		photoSize.height = photoSize.width; // make square per Instagram standard

		// adjust the height of the images container to fit everything	
		Dimension imagesContainerSize = new Dimension();
		imagesContainerSize.width = imagesContainer.getWidth(); // don't change this
		imagesContainerSize.height = numberOfRows * (photoSize.height + flowLayout.getVgap());
		
		imagesContainer.setMinimumSize(imagesContainerSize);
		imagesContainer.setPreferredSize(imagesContainerSize);
		imagesContainer.setMaximumSize(imagesContainerSize);
		frame.setVisible(true);
		
		return photoSize;
	}
	
	private void displayImages() {
    	
    	if (imagesMetaDataList.size() == 0) {
			textArea.append("Please populate your metadata list either from Instagram or from disk.\n\n");
			return;
		}

    	CardLayout cardLayout = (CardLayout) centerPanel.getLayout();
 	    cardLayout.show(centerPanel, IMAGES_PANEL);	

 	    removeExistingImages();
 	    
    	MultiThreadedImageLoader loader = new MultiThreadedImageLoader(
    			imagesMetaDataList, calcPhotoSize(), textArea, 
    			imagesContainer, infoContainer, centerPanel, frame, 
    			cardLayout, imagesMetaDataList.size());
    	
    	try {
			loader.loadImagesAndTrackTime();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
    	
	}
	
	private void removeExistingImages() {
	    Component[] images = imagesContainer.getComponents();
	    for(Component image : images) {
	        imagesContainer.remove(image);
	    }
	}
	
	private void writeMetadataToDisk() {
		if (FileHandler.writeMetaDataToFile(imagesMetaDataList, appendToExistingList)) {
			if (appendToExistingList) 
				textArea.append("Metadata successfully appended to existing list.\n\n");
			else 
				textArea.append("Metadata successfully written to new list.\n\n");
		} else {
			textArea.append("A problem was encountered writing the data to disk.\n\n");
		}
	}
	
	private void getListFromFileContents() {
		
		imagesMetaDataList.clear();
		imagesMetaDataList = FileHandler.readMetaDataFromFile();
		
		int listSize = imagesMetaDataList.size();
		
		if (listSize > 0) {
			textArea.append(listSize + " metadata objects successfully retrieved from file.\n\n");
		} else {
			textArea.append("No metadata objects exist on disk.\n\n");
		}
	}
	
	private void printListContents() {
		
		if (imagesMetaDataList.size() == 0) 
			textArea.append("Please refresh your metadata list first (either from disk or from Instagram).\n\n");
		
		for (MediaFeedData data : imagesMetaDataList) {
			textArea.append(data.getLink() + "\n");
			System.out.println(data.toString());
		}
		
		textArea.append("\n");
	}
	
	private void saveImagesToDisk() {
		
		if (imagesMetaDataList.size() == 0) {
			textArea.append("Please refresh your metadata list first (either from disk or from Instagram).\n\n");
			return;
		}
		
		
		PhotoSaver.savePhotosToDisk(imagesMetaDataList, textArea);
	}
	
	private void deleteImagesFromDisk() {
			
		File dir = new File("photos");
		
		if (!dir.exists()) {
			textArea.append("You haven't saved any photos with this application yet!\n\n");
			return;
		}
		
		Boolean nothingHasBeenDeleted = true;
		
		for(File file : dir.listFiles()) {
			file.delete();
			nothingHasBeenDeleted = false;
			System.out.println(file.getName());
		}
		
		if (nothingHasBeenDeleted) {
			textArea.append("No photos are currently stored by the application.\n\n");
			return;
		}
		
		textArea.append("All photos previously saved by this application have been deleted.\n\n");
	}
	
	private void updateArgumentDisplay(ListSelectionEvent e) {
		
		switch (algChooser.getSelectedIndex()) {
			case 0	: hideAll(); break;
			case 1	: displayMe(lblPhotographer); displayMe(txtPhotographer); break;
			case 2	: displayMe(lblTag); displayMe(txtTag); break;
			case 3	: displayMe(lblGeoPoint); displayMe(txtLat, txtLng); break;
			case 4	: displayMe(lblVerificationCode); displayMe(txtVerificationCode); break;
			case 5	: 
			case 6	: displayMe(lblLocation, lblIDs, lblDates); displayMe(txtLocation, txtMinId, txtMaxId, txtStartDate, txtEndDate); break;
			default : hideAll(); break;
		}
	}
	
	private void hideAll() {
		lblPhotographer.setVisible(false);
		lblTag.setVisible(false);
		lblGeoPoint.setVisible(false);
		lblLocation.setVisible(false);
		lblIDs.setVisible(false);
		lblDates.setVisible(false);
		lblVerificationCode.setVisible(false);
		txtPhotographer.setVisible(false);
		txtTag.setVisible(false);
		txtLat.setVisible(false);
		txtLng.setVisible(false);
		txtLocation.setVisible(false);
		txtMinId.setVisible(false);
		txtMaxId.setVisible(false);
		txtStartDate.setVisible(false);
		txtEndDate.setVisible(false);
		txtVerificationCode.setVisible(false);
	}
	
	private void displayMe(JTextField... textFields) {
		for (Component c : frame.getComponents()) {
			for (JTextField textField : textFields) {
				if (c.getName() == textField.getName())
					textField.setVisible(true);
					textField.requestFocus();
			}
		}
	}
	
	private void displayMe(JLabel... labels) {
		hideAll();
		for (JLabel label : labels)
			label.setVisible(true);
	}
	
	private void controlPanelLayout() {

		controlPanel.setLayout(gridBagLayout);
		
//		850254615510991961_194612768
		
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.NONE;
		
		//////////////////////////////////////////////////////////////////////////////
		
		//MENU BAR
		JMenuBar menubar = new JMenuBar();
		frame.setJMenuBar(menubar);
		//Create File option
		JMenu file = new JMenu ("File");
		menubar.add(file);//Add 'help' to menu bar
		//Create help option
		JMenu help = new JMenu ("Help");
		menubar.add(help);//Add 'help' to menu bar
		//Add Popup menus, this is a way to add an interactive popup menu
		/*final JPopupMenu aboutPopUp = new JPopupMenu();
		JMenuItem menuItem1 = new JMenuItem("This App Was Created By:");	
		JMenuItem menuItem2 = new JMenuItem("Randall Eastland");
		JMenuItem menuItem3 = new JMenuItem("Kelvin Kosbab");
		JMenuItem menuItem4 = new JMenuItem("Russell Mehring");
		JMenuItem menuItem5 = new JMenuItem("Daniel Larrabee");
	    aboutPopUp.add(menuItem1);
	    aboutPopUp.add(menuItem2);
	    aboutPopUp.add(menuItem3);
	    aboutPopUp.add(menuItem4);
	    aboutPopUp.add(menuItem5);*/
	    
		//JMenu Items with listeners:
		//File menu items
		//Exit
		JMenuItem exit = new JMenuItem ("Exit");
		file.add(exit);//Add the exit selection to help menu				
		//Add an action listener to the exit selection
		class exitAction implements ActionListener{
			public void actionPerformed (ActionEvent e){
				System.exit(0);//Exit the program normally
			}
		}
		exit.addActionListener(new exitAction());//Action listener added to menu item 'exit'
		
		//Help menu items
		//About
		JMenuItem about = new JMenuItem ("About");
		help.add(about);//Add the exit selection to help menu				
		//Add an action listener to the 'about' selection
		class aboutAction extends MouseAdapter{
			public void mousePressed (MouseEvent e){			
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException evt) {
					// TODO Auto-generated catch block
					evt.printStackTrace();
				} catch (InstantiationException evt) {
					// TODO Auto-generated catch block
					evt.printStackTrace();
				} catch (IllegalAccessException evt) {
					// TODO Auto-generated catch block
					evt.printStackTrace();
				} catch (UnsupportedLookAndFeelException evt) {
					// TODO Auto-generated catch block
					evt.printStackTrace();
				}

			    UIManager.put("OptionPane.background", Color.RED);
				JOptionPane.showMessageDialog(e.getComponent(), "This App Was Created By:\n" + "Randall Eastland\n" + "Kelvin Kosbab\n"
			    		                       + "Russell Mehring\n" + "Daniel Larrabee");
			}
		}
		about.addMouseListener(new aboutAction());//Action listener added to menu item 'about'
		
		//Search Tips
		JMenuItem searchTipsgp = new JMenuItem ("Geo Point Search Tips");
		help.add(searchTipsgp);//Add the exit selection to help menu				
		//Add an action listener to the 'about' selection
		class searchTipsAction extends MouseAdapter{
			public void mousePressed (MouseEvent evt){							    
			    JOptionPane.showMessageDialog(evt.getComponent(), "Geo-Points\n" + ("London: 51.507351, -0.127758\n")+
			    		           ("Cairo: 30.044420, 31.235712\n") + ("Tokyo: 35.689487, 139.691706\n") + ("Berlin: 52.520007, 13.404954\n")
			    		           + ("Washington D.C.: 38.907192, -77.036871\n") + ("Galápagos Islands: -0.829278, -90.982067\n") +
			    		           ("Find more at: http://itouchmap.com/latlong.html\n"));
			}
		}
		searchTipsgp.addMouseListener(new searchTipsAction());//Action listener added to menu item 'search tips'
		
		//Search Tips Photographers
		JMenuItem searchTipsps = new JMenuItem ("Photographer Search Tips");
		help.add(searchTipsps);//Add the exit selection to help menu				
		//Add an action listener to the 'about' selection
		class searchTipspsAction extends MouseAdapter{
			public void mousePressed (MouseEvent evt){							    
			    JOptionPane.showMessageDialog(evt.getComponent(), "Top Photographer Usernames:\n" + ("Zak Shelhamer\n")
			    		+("Patricia Abi-Rached\n") + ("Eelco Roos\n") + ("Goldie Van Schoonhoven\n") + ("Vadim Makhorov\n"));
			}
		}
		searchTipsps.addMouseListener(new searchTipspsAction());//Action listener added to menu item 'search tips'
		
		//////////////////////////////////////////////////////////////////////////////////////
		// row one
		addComponentToGridbag(new JLabel("   "), 0,0,1,1);
		addComponentToGridbag(new JLabel("   "), 0,13,1,1);

		JLabel lblNumImages = new JLabel("Number of Images Requested");
		lblNumImages.setHorizontalAlignment(JLabel.LEFT);
		lblNumImages.setForeground(Color.WHITE);
		addComponentToGridbag(lblNumImages, 0,1,1,1);
		
		JLabel lblImagesPerRow = new JLabel("Number of Images Displayed per Row");
		lblImagesPerRow.setHorizontalAlignment(JLabel.LEFT);
		lblImagesPerRow.setForeground(Color.WHITE);
		addComponentToGridbag(lblImagesPerRow, 1,1,1,1);
		
		// force the grid bag layout to respect many columns for 
		// flexibility placing widgets
		constraints.fill = GridBagConstraints.HORIZONTAL;
//		addComponentToGridbag(new JLabel("         "), 0,2,1,1);
		addComponentToGridbag(new JLabel("         "), 0,3,1,1);
		addComponentToGridbag(new JLabel("         "), 0,4,1,1);
		addComponentToGridbag(new JLabel("         "), 0,5,1,1);
		addComponentToGridbag(new JLabel("         "), 0,6,1,1);
		addComponentToGridbag(new JLabel("         "), 0,7,1,1);
		addComponentToGridbag(new JLabel("         "), 0,8,1,1);
		addComponentToGridbag(new JLabel("         "), 0,9,1,1);
		addComponentToGridbag(new JLabel("         "), 0,10,1,1);
		addComponentToGridbag(new JLabel("         "), 0,11,1,1);
		
//		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.BOTH;

		txtNumImagesRequested = new JTextField(5);
		txtNumImagesRequested.setText(Integer.toString(defaultNumImages));
		txtNumImagesRequested.setHorizontalAlignment(JTextField.RIGHT);
		txtNumImagesRequested.setPreferredSize(new Dimension(50, 30));
		addComponentToGridbag(txtNumImagesRequested, 0,2,1,1);

		txtNumImagesPerRow = new JTextField(5);
		txtNumImagesPerRow.setText(Integer.toString(5));
		txtNumImagesPerRow.setHorizontalAlignment(JTextField.RIGHT);
		txtNumImagesPerRow.setPreferredSize(new Dimension(50, 30));
		addComponentToGridbag(txtNumImagesPerRow, 1,2,1,1);
		
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		
//		JTextField txtPhotographer = new JTextField("Photographer");
//		addComponentToGridbag(txtPhotographer, 5, 5, 1, 1);

		
		JCheckBox checkBox1 = new JCheckBox("Save Results To Disk?");
		checkBox1.setForeground(Color.WHITE);
		checkBox1.setOpaque(false);
		checkBox1.addItemListener(new ItemListener() {
	         public void itemStateChanged(ItemEvent e) {         
	        	 doSaveMetaDataToDisk = (e.getStateChange()==1) ? true : false;
	         }           
	      });
		addComponentToGridbag(checkBox1, 2,2,1,1);

		JCheckBox checkBox2 = new JCheckBox("Append to Previous Results?");
		checkBox2.setForeground(Color.WHITE);
		checkBox2.setOpaque(false);
		checkBox2.addItemListener(new ItemListener() {
	         public void itemStateChanged(ItemEvent e) {         
	        	 appendToExistingList = (e.getStateChange()==1) ? true : false;
	         }           
	      });
		addComponentToGridbag(checkBox2, 3,2,1,1);

		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.BOTH;

		//This adds the search box for photographers
		lblPhotographer = new JLabel("Photographer: ");
		lblPhotographer.setHorizontalAlignment(JLabel.RIGHT);
		lblPhotographer.setForeground(Color.WHITE);
		addComponentToGridbag(lblPhotographer, 4,3,1,1);
		
		txtPhotographer = new JTextField();
		txtPhotographer.setText("");
//		txtPhotographer.setHorizontalAlignment(JTextField.RIGHT);
		addComponentToGridbag(txtPhotographer, 4,4,6,1);
		//Photographer search box ends here
		
		//This adds the search box for image tags
		lblTag = new JLabel("Tag: ");
		lblTag.setHorizontalAlignment(JLabel.RIGHT);
		lblTag.setForeground(Color.WHITE);
		addComponentToGridbag(lblTag, 4,3,1,1);
		
		txtTag = new JTextField();
		txtTag.setText("dog,cat, squirrel");
//		txtTag.setHorizontalAlignment(JTextField.RIGHT);
		addComponentToGridbag(txtTag, 4,4,6,1);
		//Image tag search box ends here

		constraints.fill = GridBagConstraints.BOTH;
		
		//This adds the search box for image tags
		lblGeoPoint = new JLabel("Coordinates: ");
		lblGeoPoint.setHorizontalAlignment(JLabel.RIGHT);
		lblGeoPoint.setForeground(Color.WHITE);
		addComponentToGridbag(lblGeoPoint, 4,3,1,1);

		txtLat = new JTextField();
		txtLat.setText("40.0149900");
		txtLat.setHorizontalAlignment(JTextField.RIGHT);
		addComponentToGridbag(txtLat, 4,4,3,1);
		
		txtLng = new JTextField();
		txtLng.setText("-105.2705500");
		txtLng.setHorizontalAlignment(JTextField.RIGHT);
		addComponentToGridbag(txtLng, 4,7,3,1);
		constraints.fill = GridBagConstraints.BOTH;

		// Location ID
		lblLocation = new JLabel("Location ID: ");
		lblLocation.setHorizontalAlignment(JLabel.RIGHT);
		lblLocation.setForeground(Color.WHITE);
		addComponentToGridbag(lblLocation, 4,3,1,1);

		txtLocation = new JTextField();
		txtLocation.setText("5182341");
		addComponentToGridbag(txtLocation, 4,4,6,1);
		
		// Max-Min ID
		lblIDs = new JLabel("ID Range: ");
		lblIDs.setHorizontalAlignment(JLabel.RIGHT);
		lblIDs.setForeground(Color.WHITE);
		addComponentToGridbag(lblIDs, 5,3,1,1);

		txtMinId = new JTextField();
		txtMinId.setText("0");
		txtMinId.setHorizontalAlignment(JTextField.RIGHT);
		addComponentToGridbag(txtMinId, 5,4,3,1);
		
		txtMaxId = new JTextField();
		txtMaxId.setText("0");
		txtMaxId.setHorizontalAlignment(JTextField.RIGHT);
		addComponentToGridbag(txtMaxId, 5,7,3,1);
		
		//Date Range
		lblDates = new JLabel("Date Range: ");
		lblDates.setHorizontalAlignment(JLabel.RIGHT);
		lblDates.setForeground(Color.WHITE);
		addComponentToGridbag(lblDates, 6,3,1,1);

		txtStartDate = new JTextField();
		txtStartDate.setText("00/01/2012");
		txtStartDate.setHorizontalAlignment(JTextField.RIGHT);
		addComponentToGridbag(txtStartDate, 6,4,3,1);
		
		txtEndDate = new JTextField();
		txtEndDate.setText("06/01/2014");
		txtEndDate.setHorizontalAlignment(JTextField.RIGHT);
		addComponentToGridbag(txtEndDate, 6,7,3,1);
		constraints.fill = GridBagConstraints.BOTH;
		
		// verifier code for user authentication
		lblVerificationCode = new JLabel("Verification Code: ");
		lblVerificationCode.setHorizontalAlignment(JTextField.RIGHT);
		lblVerificationCode.setForeground(Color.WHITE);
		addComponentToGridbag(lblVerificationCode, 4,3,1,1);
		
		txtVerificationCode = new JTextField();
		addComponentToGridbag(txtVerificationCode, 4,4,6,1);
		
//		constraints.weightx = 0;
//		constraints.fill = GridBagConstraints.NONE;
		
		constraints.anchor = GridBagConstraints.EAST;
		JButton btnFetchMetaData = new JButton("Fetch Metadata From Instagram");
		btnFetchMetaData.setPreferredSize(standardButtonSize);
		btnFetchMetaData.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	prepareForMetaDataSearch();	        	
	        }
		});   
		addComponentToGridbag(btnFetchMetaData, 0,12,1,1);

		JButton btnGetListFromFile = new JButton("Retrieve Metadata From Disk");
		btnGetListFromFile.setPreferredSize(standardButtonSize);
		btnGetListFromFile.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	getListFromFileContents();      	
	        }
		});   
		addComponentToGridbag(btnGetListFromFile, 1,12,1,1);

		JButton btnPrintList = new JButton("Print URLs to Screen");
		btnPrintList.setPreferredSize(standardButtonSize);
		btnPrintList.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	printListContents();      	
	        }
		});   
		addComponentToGridbag(btnPrintList, 2,12,1,1);

		JButton btnSaveImages = new JButton("Save Images to Disk");
		btnSaveImages.setPreferredSize(standardButtonSize);
		btnSaveImages.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	saveImagesToDisk();      	
	        }
		});   
		addComponentToGridbag(btnSaveImages, 3,12,1,1);

		JButton btnDeleteImages = new JButton("Delete All Images from Disk");
		btnDeleteImages.setPreferredSize(standardButtonSize);
		btnDeleteImages.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	
	        	Object[] options = {"Yes", "Cancel"};
	        	int response = JOptionPane.showOptionDialog(
	        			null, 
	        			"This will delete ALL the photos that you have downloaded with this application. Are you sure?", 
	        			"",
	        			JOptionPane.YES_NO_OPTION, 
			            JOptionPane.WARNING_MESSAGE, 
			            null, options, options[1]);
	        	
				if (response == JOptionPane.YES_OPTION) {
		        	deleteImagesFromDisk();
				}
	        	
      	
	        }
		});   
		addComponentToGridbag(btnDeleteImages, 4,12,1,1);

		JButton btnGetVerficationCode = new JButton("Get Verification Code From Instagram");
		btnGetVerficationCode.setPreferredSize(standardButtonSize);
		btnGetVerficationCode.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	tokenizer = new AccessTokenizer(textArea);
	        	tokenizer.getVerficationCode(textArea);
	        	algChooser.setSelectedIndex(4);
	        	
	        }
		});   
		addComponentToGridbag(btnGetVerficationCode, 5,12,1,1);
		
//		JButton btnTemp = new JButton("Show Info Container");
//		btnTemp.setPreferredSize(standardButtonSize);
//		btnTemp.addActionListener(new ActionListener() {
//	        public void actionPerformed(ActionEvent e) {
//	        	CardLayout cardLayout = (CardLayout)centerPanel.getLayout();
//	    		cardLayout.show(centerPanel, INFO_PANEL);   	
//	        }
//		});   
//		addComponentToGridbag(btnTemp, 5,12,1,1);
		
		JButton btnDisplayImages = new JButton("Display Images");
		btnDisplayImages.setPreferredSize(standardButtonSize);
		btnDisplayImages.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	displayImages();
	        }
		});   
		addComponentToGridbag(btnDisplayImages, 6,12,1,1);
		
		constraints.anchor = GridBagConstraints.WEST;
		
		JLabel chooserTitle = new JLabel("Select Filter Algorithm");
		chooserTitle.setHorizontalAlignment(JLabel.LEFT);
		chooserTitle.setForeground(Color.WHITE);
		addComponentToGridbag(chooserTitle, 7,1,1,1);

//		constraints.weightx = 0.1; // can grow wider
//		constraints.weighty = 1; // can grow taller
		constraints.fill = GridBagConstraints.BOTH;


//		String[] filterByChoices = {"Popular", "Photographer", "Tags" , "GeoPoint", "Likes", "Recent", "Date"};
		algChooser = new JList<String>(filterByChoices);
		algChooser.setVisibleRowCount(5);
		algChooser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		algChooser.setSelectedIndex(0);
//		ListSelectionModel listSelectionModel = algChooser.getSelectionModel();
//		argSetter = new ArgumentSetter(controlPanel, this);
//		listSelectionModel.addListSelectionListener(argSetter);
		algChooser.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateArgumentDisplay(e);
			}
		});
		
		addComponentToGridbag(algChooser, 9,1,2,1);

		constraints.fill = GridBagConstraints.BOTH;
//		addComponentToGridbag(new JTextField("   "), 6,2,1,1);
//		addComponentToGridbag(new JLabel("   "), 6,2,1,1);
		
		
		constraints.fill = GridBagConstraints.NONE;
		JLabel lblRunResults = new JLabel("Run Results");
		lblRunResults.setHorizontalAlignment(JLabel.LEFT);
		lblRunResults.setForeground(Color.WHITE);
		addComponentToGridbag(lblRunResults, 9,1,3,1);

		constraints.anchor = GridBagConstraints.EAST;
//		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;

		JButton btnClearOutput = new JButton("Clear Output");
		btnClearOutput.setPreferredSize(standardButtonSize);
		btnClearOutput.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	textArea.setText("");	        	
	        }
		});   
		addComponentToGridbag(btnClearOutput, 9,12,1,1);

		
//		constraints.weightx = 100; // can grow wider
		constraints.fill = GridBagConstraints.BOTH;
		
		constraints.weightx = 100; // can grow wider
		constraints.weighty = 100;
		textArea = new JTextArea(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		textArea.setEditable(false);
		textArea.setBackground(Color.LIGHT_GRAY);

		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		outputTextArea = new JScrollPane(textArea);		
		addComponentToGridbag(outputTextArea, 10,1,12,10);
		
		constraints.weightx = 0;
		

//		addComponentToGridbag(new JLabel("   "), 10,0,1,1);								

		addComponentToGridbag(new JLabel("   "), 11,0,1,1);
	}
	
	public void addComponentToGridbag(Component component, int row, int column, int width, int height) {
		constraints.gridx = column;
		constraints.gridy = row;
		constraints.gridwidth = width;
		constraints.gridheight = height;
		gridBagLayout.setConstraints(component, constraints);
		controlPanel.add(component);
	}
	
	private void imagesPanelLayout() {		
		JButton btnShowControls = new JButton("Back to Control Panel");
		btnShowControls.setPreferredSize(standardButtonSize);
		btnShowControls.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	
	    		for (Component c : imagesPanel.getComponents()) {
    				if (c instanceof JButton) imagesPanel.remove(c);	
	    		}
	    		CardLayout cardLayout = (CardLayout)centerPanel.getLayout();
	    		cardLayout.show(centerPanel, CONTROL_PANEL);
	        }
		});
		
		//Add a scroller to the display images panel
		scroller = new JScrollPane(imagesContainer); //Add a scroller to imagesPanel 
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setBounds(0, 0, imagesContainer.getWidth(), imagesContainer.getHeight());
		imagesPanel.add(scroller);
		
		imagesButtonHolder.add(btnShowControls);
	}
	
	private void infoPanelLayout() {		
		JButton btnShowImages = new JButton("Back to Images");
		btnShowImages.setPreferredSize(standardButtonSize);
		btnShowImages.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	
	    		for (Component c : infoPanel.getComponents()) {
//	    			if (!(c instanceof JButton)) infoPanel.remove(c);	
	    		}
	    		CardLayout cardLayout = (CardLayout)centerPanel.getLayout();
	    		cardLayout.show(centerPanel, IMAGES_PANEL);
	        }
		});
		
//		//Add a scroller to the display info panel
		scroller = new JScrollPane(infoContainer); 
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setBounds(0, 0, infoContainer.getWidth(), infoContainer.getHeight());
		infoPanel.add(scroller);
		
		infoButtonHolder.add(btnShowImages);
	}
	
	private void makeMainFrame() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int height = (int) (screenSize.height * 0.85);
		int width = (int) (screenSize.width * 0.75);
		
		frame = new JFrame();
		frame.setTitle("CSCI 5828 Photo App");
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
	}
	
	private void makePanels() {
		frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

		makeImagesPanel();
		makeInfoPanel();
		
		// make a JPanel to hold all of the buttons and text fields
		controlPanel.setBackground(Color.DARK_GRAY);
		
		// the 'center panel is a container that holds each of our primary
		// jpanels (control panel, images panel, info panel)
		frame.getContentPane().add(centerPanel, BorderLayout.CENTER);

		// the JPanel 'centerPanel' will hold both of these sub panels
		// using a CardLayout. This layout allows the two sub panels to
		// be switched easily so that the user can display either the 
		// control panel or the images panel with a button click.
		centerPanel.add(imagesPanel, IMAGES_PANEL);
		centerPanel.add(controlPanel, CONTROL_PANEL);
		centerPanel.add(infoPanel, INFO_PANEL);
		
		
		// display the control panel when the app starts
		CardLayout cardLayout = (CardLayout)centerPanel.getLayout();
		cardLayout.show(centerPanel, CONTROL_PANEL);

	}
	
	private void makeInfoPanel() {
		// make a JPanel to contain and layout all of the actual images
		infoPanel.setBackground(Color.LIGHT_GRAY);
		infoPanel.setLayout(new BorderLayout(0, 0));
		
		flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		infoContainer.setBackground(Color.LIGHT_GRAY);
		
		// set the size of the images container to match the size of the overall frame
		Dimension dimension = new Dimension(frame.getWidth(), frame.getHeight());
		infoContainer.setMinimumSize(dimension);
		infoContainer.setPreferredSize(dimension);
		infoContainer.setMaximumSize(dimension);
		infoContainer.setLayout(flowLayout);
		
		// place the images container at the center of the images panel
		infoPanel.add(infoContainer, BorderLayout.CENTER);
		
		// add a panel to place a 'return to control panel' button at the
		// bottom of the images panel
		infoButtonHolder.setBackground(Color.DARK_GRAY);
		infoPanel.add(infoButtonHolder, BorderLayout.SOUTH);
	}
	
	private void makeImagesPanel() {
		// make a JPanel to contain and layout all of the actual images
		imagesPanel.setBackground(Color.LIGHT_GRAY);
		imagesPanel.setLayout(new BorderLayout(0, 0));
		
//		gridLayout = new GridLayout(numRows, numCols);
		flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		imagesContainer.setBackground(Color.LIGHT_GRAY);
		
		// set the size of the images container to match the size of the overall frame
		Dimension dimension = new Dimension(frame.getWidth(), frame.getHeight());
		imagesContainer.setMinimumSize(dimension);
		imagesContainer.setPreferredSize(dimension);
		imagesContainer.setMaximumSize(dimension);
		imagesContainer.setLayout(flowLayout);
		
		// place the images container at the center of the images panel
		imagesPanel.add(imagesContainer, BorderLayout.CENTER);
		
		// add a panel to place a 'return to control panel' button at the
		// bottom of the images panel
		imagesButtonHolder.setBackground(Color.DARK_GRAY);
		imagesPanel.add(imagesButtonHolder, BorderLayout.SOUTH);
	}
	
	public JFrame getFrame() {return frame;}
	public JTextArea getOutputFrame() {return textArea;}

}
