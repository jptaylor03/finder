package controller;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import view.Viewer;


/**
 * Respond to button pressing of the "Find" button
 * on the Viewer.
 */
public class AboutButtonActionListener implements ActionListener {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("controller");
	
//	/**
//	 * Viewer instance.
//	 */
//	private Viewer viewer;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public AboutButtonActionListener() {}
//	public AboutButtonActionListener(Viewer viewer) {
//		this.viewer = viewer;
//	}

	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
//	public Viewer getViewer() {
//		return this.viewer;
//	}
//	public void setViewer(Viewer viewer) {
//		this.viewer = viewer;
//	}
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Respond to an 'actionPerformed' event related to a command button click.
	 * 
	 * @param event ActionEvent that has occurred on the component.
	 */
	public void actionPerformed(final ActionEvent event) {
		//Viewer.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		JOptionPane.showMessageDialog(Viewer.frame, Viewer.getAppInfoFormatted(), (String)Viewer.appInfo[0], JOptionPane.PLAIN_MESSAGE, new ImageIcon((Image)Viewer.appInfo[4]));
		//Viewer.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

}
