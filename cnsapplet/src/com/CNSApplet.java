package com;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.*;
import javax.swing.*;

public class CNSApplet extends JApplet implements ActionListener {

	private static final long serialVersionUID = 3891383430603851524L;
	private JPanel panel;
	private JButton selectFile, selectCertificate, sign;
	private JTextArea log;
	private JFileChooser fc;
	private String nl = "\n";
	private File fileToSign, keyFile;

	// Called when this applet is loaded into the browser.
	public CNSApplet(){
		initializeComponents();
	}
	
	private void initializeComponents(){
		this.panel = new JPanel(new FlowLayout());
		selectFile = new JButton("Select a file");
		selectFile.addActionListener(this);
		selectCertificate = new JButton("Select a key file");
		selectCertificate.addActionListener(this);
		selectCertificate.setEnabled(false);
		sign = new JButton("Sign the selected file");
		sign.setEnabled(false);
		log = new JTextArea(100,70);

	    panel.add(selectFile);
	    panel.add(selectCertificate);
	    panel.add(sign);
	    panel.add(log);
	    Container content = getContentPane();
	    content.add(panel);
	    fc = new JFileChooser();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == selectFile){
			int returnVal = fc.showOpenDialog(CNSApplet.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            fileToSign = fc.getSelectedFile();
	            selectCertificate.setEnabled(true);
	            log.append("Opening: " + fileToSign.getName() + "." + nl);
	        } else {
	        	fileToSign = null;
	        	selectCertificate.setEnabled(false);
	            log.append("Open command cancelled by user." + nl);
	        }
		}else if(e.getSource() == selectCertificate){
			int returnVal = fc.showOpenDialog(CNSApplet.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            keyFile = fc.getSelectedFile();
	            sign.setEnabled(true);
	            log.append("Opening: " + keyFile.getName() + "." + nl);
	        } else {
	        	keyFile = null;
	        	sign.setEnabled(false);
	            log.append("Open command cancelled by user." + nl);
	        }
		}else if(e.getSource() == sign){
			// REAL BUSINESS LOGIC MUST BE IMPLEMENTED HERE
			// SAKURA POWAAAA HERE YOUR CODING TIME COMES!!
		}
	}
}

