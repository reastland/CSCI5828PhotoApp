import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class ArgumentSetter implements ListSelectionListener {
	
//	private GUI gui;
//	private JPanel panel;
//	private JTextField minDateArg;
//	private JTextField maxDateArg;
	private String photographer;
	private String tag;
	private double[] geoPoint;
	private String text;
	private String[] dateRange;
	
	public ArgumentSetter (JPanel p, GUI g) {
//		panel = p;
//		gui = g;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		ListSelectionModel lsm = (ListSelectionModel)e.getSource();
		
		switch (getSelectedIndex(lsm)) {
			case 0: break; // popular selected
			case 1: layoutTextFieldsForPhotographerArgs(); // photographer selected
			case 2: layoutTextFieldsForTagsArgs(); // tags selected
			case 3: layoutTextFieldsForGeoPointArgs(); // geopoint selected
			case 4: layoutTextFieldsForTextArgs(); // text selected
			case 5: layoutTextFieldsForDateArgs(); // date selected
			default: break;
		}	
	}

	private void layoutTextFieldsForPhotographerArgs() {
		
		photographer = "Joe Photographer";
		
		// this isn't working
//		JTextField txtPhotographer = new JTextField("Photographer");
//		gui.addComponentToGridbag(txtPhotographer, 5, 4, 1, 2);
	}
	
	private void layoutTextFieldsForTagsArgs() {
		tag = "test";
	}
	
	private void layoutTextFieldsForGeoPointArgs() {
		double latitude =  40.0149900;
		double longitude = -105.2705500;
		geoPoint = new double[2];
		geoPoint[0] = latitude;
		geoPoint[1] = longitude;
	}
	
	private void layoutTextFieldsForTextArgs() {
		text = "something";
	}
	
	private void layoutTextFieldsForDateArgs() {
		
		String beginDate = "01/01/2014";
		String endDate = "12/31/2014";
		dateRange = new String[2];
		dateRange[0] = beginDate;
		dateRange[1] = endDate;
		
		
//		minDateArg = new JTextField(5);
//		minDateArg.setText("1/1/2014");
//		minDateArg.setHorizontalAlignment(JTextField.RIGHT);
//		gui.addComponentToGridbag(minDateArg, 0,9,1,1);
	}
	
	private int getSelectedIndex(ListSelectionModel lsm) {
        int minIndex = lsm.getMinSelectionIndex();
        int maxIndex = lsm.getMaxSelectionIndex();
        for (int i = minIndex; i <= maxIndex; i++) {
            if (lsm.isSelectedIndex(i))
            	return i;
        }
        
        return -1;
	}

	public String getPhotographer() {return photographer;}
	public String getTag() {return tag;}
	public double[] getGeoPoint() {return geoPoint;}
	public String getText() {return text;}
	public String[] getDates() {return dateRange;}
}
