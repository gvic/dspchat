package com;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.util.Iterator;
import java.awt.*;
import javax.swing.*;
import org.bouncycastle.bcpg.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;


public class CNSApplet extends JApplet implements ActionListener {

	private static final long serialVersionUID = 3891383430603851524L;
	private JPanel panel;
	private JButton selectFile, selectCertificate, sign;
	private JTextArea log;
	private JFileChooser fc;
	private JComboBox cbox;
	private String nl = "\n";
	private File fileToSign, keyFile;
	private String userSelected;

	public CNSApplet() {super();}
	// Called when this applet is loaded into the browser.
	public void init(){
		initializeComponents();
	}
	
	private void initializeComponents(){
		this.panel = new JPanel(new FlowLayout());
		selectFile = new JButton("Select a file");
		selectFile.addActionListener(this);
		selectCertificate = new JButton("Select a key file");
		selectCertificate.addActionListener(this);
		selectCertificate.setEnabled(false);
	    cbox = new JComboBox();
	    try {
			populateCB(cbox);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PGPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    cbox.addActionListener(this);
		sign = new JButton("Sign the selected file");
		sign.setEnabled(false);
		sign.addActionListener(this);
		log = new JTextArea(100,70);
	    panel.add(selectFile);
	    panel.add(selectCertificate);
	    panel.add(cbox);
	    panel.add(sign);
	    panel.add(log);
	    Container content = getContentPane();
	    content.add(panel);
	    fc = new JFileChooser();
	    boolean hidingEnabled = fc.isFileHidingEnabled();
	    fc.setFileHidingEnabled(false);
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
			userSelected = cbox.getSelectedItem().toString();

			System.out.println(userSelected+"blabla");
			String str = JOptionPane.showInputDialog(null, "Enter password : ", 
					"Password", 1);
			try {
				sign(fileToSign.getAbsolutePath(), keyFile.getAbsolutePath(), str, userSelected);
			} catch (NoSuchProviderException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (PGPException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public void sign(String filename, String keyRingName, String pass, String user) throws IOException, PGPException, NoSuchProviderException, NoSuchAlgorithmException
	{
		Boolean foundMatch = false;
		Security.addProvider(new BouncyCastleProvider());
	    FileInputStream keyIn = new FileInputStream(keyRingName);
	    FileOutputStream out = new FileOutputStream(filename + ".bpg");
	    InputStream in = PGPUtil.getDecoderStream(keyIn);
	    PGPSecretKeyRingCollection pgpSec = 
	                               new PGPSecretKeyRingCollection(in);
	    PGPSecretKey key = null;
	    Iterator<?> rIt = pgpSec.getKeyRings();
	    PGPPrivateKey pgpPrivKey = null;
	    PGPSecretKey keyMatch = null;
	    while (key == null && rIt.hasNext()) {
	      PGPSecretKeyRing kRing = (PGPSecretKeyRing)rIt.next();
	      Iterator<?> kIt = kRing.getSecretKeys();
	      while ( key == null && kIt.hasNext() ) {
	        PGPSecretKey k = (PGPSecretKey)kIt.next();
	        System.out.println(k + " [secret key]");
	        if ( k.isSigningKey() ) {
	        	key = k; 
	        	try{
	        		pgpPrivKey = key.extractPrivateKey(pass.toCharArray(), "BC");
        			keyMatch = key;
	        	}catch (Exception e) {key = null; keyMatch = null;}
	        }
	      }
	      String tmpU = "";
	      try{
	      tmpU = keyMatch.getUserIDs().next().toString();
	      System.out.println(tmpU);
	      }
	      catch (Exception e)
	      {
	    	  tmpU = "";
	      }
			if (tmpU.equals(user)){
				System.out.println("Matched!");
				foundMatch = true;
				
			}
	    }
	    if (key == null) {
	      JOptionPane.showMessageDialog(null, "No such key");
	    }

	    if(foundMatch && key != null){
	    PGPSignatureGenerator sGen = new PGPSignatureGenerator(
	         key.getPublicKey().getAlgorithm(), PGPUtil.SHA1, "BC");
	    sGen.initSign(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);
	    PGPCompressedDataGenerator cGen = new PGPCompressedDataGenerator(
	         PGPCompressedDataGenerator.ZLIB);
	    BCPGOutputStream bOut = new BCPGOutputStream(cGen.open(out));
	    FileInputStream fIn = new FileInputStream(filename);
	    int ch = 0;
	    while ( (ch = fIn.read()) >= 0 ) { try {
			sGen.update((byte)ch);
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} }
	    try {
			sGen.generate().encode(bOut);
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    cGen.close();
	    fIn.close();
	    bOut.close();
	    }

	    keyIn.close();
	    out.close();
	    in.close();
	}
	public void populateCB(JComboBox c) throws IOException, PGPException{
		String keyRingName = "/home/hykth/.gnupg/secring.gpg";
		Security.addProvider(new BouncyCastleProvider());
	    FileInputStream keyIn = new FileInputStream(keyRingName);
	    InputStream in = PGPUtil.getDecoderStream(keyIn);
	    PGPSecretKeyRingCollection pgpSec = 
	                               new PGPSecretKeyRingCollection(in);
	    PGPSecretKey key = null;
	    Iterator<?> rIt = pgpSec.getKeyRings();
	    PGPPrivateKey pgpPrivKey = null;
	    while (key == null && rIt.hasNext()) {
	      PGPSecretKeyRing kRing = (PGPSecretKeyRing)rIt.next();
	      Iterator<?> kIt = kRing.getSecretKeys();
	      while ( kIt.hasNext() ) {
	        PGPSecretKey k = (PGPSecretKey)kIt.next();
	        if ( k.isSigningKey() ) {
	        	key = k; 
	        	try{
	        		c.addItem(key.getUserIDs().next().toString());
	        	}catch (Exception e) {key = null;}
	        }
	      }
	    }
	    if (key == null) {
	    	System.out.println("no key?");
	    }
	    
	    try {
			keyIn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    in.close();
	
	}
}


