package guiHelpers;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.common.Likes;
import org.jinstagram.entity.common.User;

public class ImageButton extends JButton {

	JFrame frame;
	JPanel infoPanel;
	JPanel centerPanel;
	CardLayout cardLayout;
	JButton button;
	Tuple tuple;
	JPopupMenu popup;
	static final long serialVersionUID = 1;
	
	public ImageButton(Tuple tuple, Dimension imageSize, JFrame fr, 
			JPanel panel, JPanel centerPanel, CardLayout layout) {
    	super(new ImageIcon(tuple.getImage()));
    	frame = fr;
    	infoPanel = panel;
    	this.centerPanel = centerPanel;
    	cardLayout = layout;
    	this.tuple = tuple;
    	this.setMinimumSize(imageSize);
    	this.setPreferredSize(imageSize);
    	this.setMaximumSize(imageSize);
    	this.setVisible(true);
    	button = this;
    	popup = new JPopupMenu();
    	addMenuItemsToPopup();
	}

	private void addMenuItemsToPopup() {
        popup.add(new JMenuItem(new AbstractAction("Show In Instagram") { 
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
	            openInDefaultBrowser();
	        }
        }));
	        
        popup.add(new JMenuItem(new AbstractAction("Show Comments") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				showComments();            
            }
        }));
	        
        popup.add(new JMenuItem(new AbstractAction("Show Likes") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
                showLikes();
            }
        }));
        
        popup.add(new JMenuItem(new AbstractAction("Remove Image") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
                removeImage();
            }
        }));
        
        popup.add(new JMenuItem(new AbstractAction("Send To Beginning") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				sendImageToBeginning();
            }
        }));
        
        popup.add(new JMenuItem(new AbstractAction("Send To End") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				sendImageToEnd();
            }
        }));
        
        popup.add(new JMenuItem(new AbstractAction("Save Image As") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				saveImage();
            }
        }));
        
        popup.add(new JMenuItem(new AbstractAction("Upload To Facebook") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				uploadToFacebook();
            }
        }));
	}
	
	public void addMouseMotionListener() {
		this.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(java.awt.event.MouseEvent mouseEvent) {
				JButton thisButton = (JButton) mouseEvent.getComponent();
				JPanel parentImagesContainer = (JPanel) thisButton.getParent();
				Point position = mouseEvent.getPoint();
				int cellWidth = thisButton.getWidth();
				int cellHeight = thisButton.getHeight();
				Rectangle bounds = new Rectangle(cellWidth, 0, cellWidth, cellHeight);
				int imageIndex = parentImagesContainer.getComponentZOrder(thisButton);
				if (position.getX() < 0 && imageIndex > 0) {
					// Dragged to the left
					parentImagesContainer.remove(thisButton);
					parentImagesContainer.add(thisButton, imageIndex - 1);
					parentImagesContainer.revalidate();
					parentImagesContainer.repaint();
				} else if (position.getX() > cellWidth && imageIndex + 1 != parentImagesContainer.getComponentCount()) {
					// Dragged to the right
					parentImagesContainer.remove(thisButton);
					parentImagesContainer.add(thisButton, imageIndex + 1);
					parentImagesContainer.revalidate();
					parentImagesContainer.repaint();
				}
			}

			@Override
			public void mouseMoved(java.awt.event.MouseEvent mouseEvent) {}
    	});
	}
	
	public void addPopupMenu(final JFrame fr) {
	    this.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {			
		        // display the popup menu
                popup.pack();
                Point pos = new Point();
                // get the preferred size of the menu...
                Dimension size = popup.getPreferredSize();
                // Adjust the x position so that the left side of the popup
                // appears at the center of  the component
                pos.x = (button.getWidth() / 2);
                // Adjust the y position so that the y postion (top corner)
                // is positioned so that the bottom of the popup
                // appears in the center
                pos.y = (button.getHeight() / 2) - size.height;
                popup.show(button, pos.x, pos.y);
				
//                popup.show(button, button., 0);
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {}
	    	
	    });
	}
	
    private void openInDefaultBrowser() {

    	// open the default web browser for the HTML page
    	String url = tuple.getData().getLink();
    	try {
			 if (java.awt.Desktop.isDesktopSupported()) 
				 System.out.println("yes");
			 else System.out.println("no"); 
    		java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
       		System.out.println("here");
		} catch (IOException e) {
			System.err.println("exception thrown");
			e.printStackTrace();
		}
    	System.out.println("done");
   }
    
    private void showComments() {
		StringBuilder builder = new StringBuilder(); 
		List<CommentData> comments = tuple.getData().getComments().getComments();
		
		if (comments.size() > 0) {
			builder.append("Partial List of Comments\n");
			builder.append("------------------------\n");
			for (CommentData commentData : comments) {
				String commentor = commentData.getCommentFrom().getFullName();
				String comment = commentData.getText();
				builder.append(commentor);
				builder.append(" says:\n");
				builder.append(comment);
				builder.append("\n\n");
			}
		} else {
			builder.append("No one has commented on this photo (yet...)");
		}
		
//    	JTextArea textArea = new JTextArea(builder.toString());
//    	textArea.setSize(new Dimension(infoPanel.getWidth(), infoPanel.getHeight()));
//    	infoPanel.add(textArea);
//    	
//    	cardLayout.show(centerPanel, "info_panel");
    	
        JOptionPane.showMessageDialog(frame, builder.toString());    
    }
    
    private void showLikes() {
    	
    	StringBuilder builder = new StringBuilder();
    	Likes likes = tuple.getData().getLikes();
    	
    	if (likes.getCount() > 0) {
	    	builder.append("Total Number of Likes: ");
	    	builder.append(likes.getCount());
	    	builder.append("\n\n");
	    	
	    	if (likes.getCount() > 0)
	    		builder.append("The following Instagram users like this photo (partial list):");
	    	
	    	for (User user : likes.getLikesUserList()) {
	    		builder.append("\n\t");
	    		builder.append(user.getFullName());
	    	}
    	} else {
    		builder.append("There are no likes for this photo (yet...).");
    	}
    	
//    	JTextArea textArea = new JTextArea(builder.toString());
//    	textArea.setSize(new Dimension(infoPanel.getWidth(), infoPanel.getHeight()));
//    	infoPanel.add(textArea);
//    	
//    	cardLayout.show(centerPanel, "info_panel");
    	
    	JOptionPane.showMessageDialog(frame, builder.toString());  
    }
    
    private void removeImage() {
		JPanel parentImagesContainer = (JPanel) button.getParent();
		parentImagesContainer.remove(button);
		parentImagesContainer.revalidate();
		parentImagesContainer.repaint();
    }
    
    private void sendImageToEnd() {
    	JPanel parentImagesContainer = (JPanel) button.getParent();
    	
    	// Remove image
		parentImagesContainer.remove(button);
		parentImagesContainer.revalidate();
		parentImagesContainer.repaint();
		
		// Add image to end
		parentImagesContainer.add(button);
		parentImagesContainer.revalidate();
		parentImagesContainer.repaint();
    }
    
    private void sendImageToBeginning() {
    	JPanel parentImagesContainer = (JPanel) button.getParent();
    	
    	// Remove image
		parentImagesContainer.remove(button);
		parentImagesContainer.revalidate();
		parentImagesContainer.repaint();
		
		// Add image to end
		parentImagesContainer.add(button, 0);
		parentImagesContainer.revalidate();
		parentImagesContainer.repaint();
    }
    
    private void saveImage() {
    	// Filechooser object
    	JFileChooser fileChooser = new JFileChooser();
    	
    	// Set filechooser preferences
    	fileChooser.setApproveButtonText("Save");
    	
    	// Set current directory of filechooser to user's home
    	fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Image", "png", "image");
    	fileChooser.setFileFilter(filter);
    	
    	// Show file chooser
    	int result = fileChooser.showOpenDialog(null);
    	
    	// If user selects a file
    	if (result == JFileChooser.APPROVE_OPTION) {
    		// Get the selected file
    		File outputFile = fileChooser.getSelectedFile();
    		
    		// Set correct file extension
    		outputFile = new File(outputFile.getAbsolutePath() + ".png");
    		
    		// Convert Image to BufferedImage for ImageIO
    		Image img = tuple.getImage();
    		
    		// Create a buffered image with transparency
    	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

    	    // Draw the image on to the buffered image
    	    Graphics2D bGr = bimage.createGraphics();
    	    bGr.drawImage(img, 0, 0, null);
    	    bGr.dispose();
    		
    		// Write the image to the file
    		try {
				ImageIO.write(bimage, "png", outputFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    private void uploadToFacebook() {
    	System.out.println("facebook");
    	System.out.println(tuple.getId());
    	FacebookUploader.uploadPhoto();
    	
    }
}
