package com;

import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class CNSApplet extends JApplet implements ActionListener {

	private static final long serialVersionUID = 3891383430603851524L;
	Button openButton, openCertificate, certificate;

	// Called when this applet is loaded into the browser.
	public CNSApplet() {
		openButton = new Button("Select a File...");
		openButton.addMouseListener(this);
		add(openButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openButton) {
			FileOpenService fos = null;
			FileContents fileContents = null;

			try {
				fos = (FileOpenService) ServiceManager
						.lookup("javax.jnlp.FileOpenService");
			} catch (UnavailableServiceException exc) {
			}

			if (fos != null) {
				try {
					fileContents = fos.openFileDialog(null, null);
				} catch (Exception exc) {
					log.append("Open command failed: "
							+ exc.getLocalizedMessage() + newline);
					log.setCaretPosition(log.getDocument().getLength());
				}
			}

			if (fileContents != null) {
				try {
					// This is where a real application would do something
					// with the file.
					log.append("Opened file: " + fileContents.getName() + "."
							+ newline);
				} catch (IOException exc) {
					log.append("Problem opening file: "
							+ exc.getLocalizedMessage() + newline);
				}
			} else {
				log.append("User canceled open request." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());
		}
	}

}
