/*
 *  Copyright (C) 2007-2008 Steffen Mecke
 *
 * This file is part of UTF2ISO. See the file LICENSE.txt for the licensing information
 * applying to this file.
 */

package magellan.ext.utf2iso;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

// class UTF82ISO
// created on Dec 11, 2007
//
// Copyright 2003-2007 by Steffen Mecke
//
// Author : $Author: $
// $Id: $
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program (see doc/LICENCE.txt); if not, write to the
// Free Software Foundation, Inc., 
// 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// 

/**
 * An application for conversion between different charsets. Primary purpose:
 * Conversion of Eressea .cr-files from UTF-8 to ISO-8859-1 or vice versa.
 * 
 * @author stm
 * 
 */
public class UTF2ISO extends JFrame {

  private static final long serialVersionUID = -6050038586322316223L;

  JPanel optionPanel;
  
  public UTF2ISO() {
    // application should terminate if this window is closed
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        close();
      }
    });

    // //////////////////////////////////////////////////////////////////////////////////
    // create window content
    optionPanel = new MyOptionPanel();
    optionPanel.setSize(new Dimension(600, 400));
    optionPanel.setVisible(true);
    add(optionPanel);
  }
  
  public void run(){
    // show window
    pack();
    setVisible(true);
    optionPanel.setVisible(true);
  }

  /**
   * Close window and exit.
   */
  public void close() {
    setVisible(false);
    dispose();
    System.exit(0);
  }

  class MyOptionPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = -3857575575042332111L;

    // the file chooser for the input file(s)
    MyChooser inputFC;

    // list of input file names
    File[] filenames = null;

    // output director name
    File outputDir = null;

    // radio buttons for direction of conversion
    private JRadioButton utf2isoButton;

    private JRadioButton iso2utfButton;

    protected class MyChooser extends JFileChooser {
      private static final long serialVersionUID = 8447412978715835187L;

      public void approveSelection() {
        convert();
      }

      public void cancelSelection() {
        close();
      }
    }

    /**
     * A helper class which sets up the option panel
     */
    protected MyOptionPanel() {
      // the file chooser for the input file(s)
      inputFC = new MyChooser();
      inputFC.setMultiSelectionEnabled(true);
      inputFC.setControlButtonsAreShown(false);
      inputFC.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6), BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("UTF82ISO.inputFiles"), javax.swing.border.TitledBorder.LEADING, TitledBorder.TOP))); //$NON-NLS-1$
      FileFilter crFilter = new ExtensionFileFilter("cr", Messages.getString("UTF82ISO.crFiles")); //$NON-NLS-1$ //$NON-NLS-2$
      inputFC.setFileFilter(crFilter);
      JPanel fcPanel = new JPanel(new BorderLayout());
      fcPanel.add(inputFC, BorderLayout.CENTER);
      inputFC.setControlButtonsAreShown(true);
      inputFC.setApproveButtonText(Messages.getString("UTF82ISO.convertButton")); //$NON-NLS-1$

      // radio buttons determining direction of conversion
      ButtonGroup directionButtonGroup = new ButtonGroup();
      utf2isoButton = new JRadioButton(Messages.getString("UTF82ISO.utf2iso"), true); //$NON-NLS-1$
      iso2utfButton = new JRadioButton(Messages.getString("UTF82ISO.iso2utf"), false); //$NON-NLS-1$
      directionButtonGroup.add(utf2isoButton);
      directionButtonGroup.add(iso2utfButton);

      JPanel radioBoxPanel = new JPanel(new FlowLayout());
      radioBoxPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(), BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Messages.getString("UTF82ISO.direction"), javax.swing.border.TitledBorder.LEADING, TitledBorder.TOP))); //$NON-NLS-1$
      radioBoxPanel.add(utf2isoButton);
      radioBoxPanel.add(iso2utfButton);
      utf2isoButton.setSelected(true);

      // An explanatory message
      JPanel messagePanel = new JPanel();
      messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
      messagePanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
      JTextArea explanation = new JTextArea(Messages.getString("UTF82ISO.explanation")); //$NON-NLS-1$
      explanation.setMaximumSize(new Dimension(600, 100));
      explanation.setRows(2);
      explanation.setLineWrap(true);
      explanation.setWrapStyleWord(true);
      messagePanel.add(explanation, BorderLayout.LINE_START);

      // arrange components
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      fcPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      radioBoxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      messagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      add(fcPanel);

      add(radioBoxPanel);

      add(messagePanel);
    }

    public void actionPerformed(ActionEvent e) {
      // System.err.println(e);
      // if (e.getSource() == goButton) {
      // convert();
      // }
    }

    /**
     * Interprets user input and starts conversion.
     */
    protected void convert() {
      // get input files
      filenames = inputFC.getSelectedFiles();
      if (filenames == null || filenames.length == 0) {
        JOptionPane.showMessageDialog(this, Messages.getString("UTF82ISO.noneSelected")); //$NON-NLS-1$
        return;
      }

      // answer for "do you really want to" confirmation message
      int prompt = 2; // default answer is "no"

      // lists of files where conversion succeeded or failed
      StringBuffer success = new StringBuffer();
      StringBuffer wrongFormat = new StringBuffer();
      StringBuffer fail = new StringBuffer();
      int tried = 0;

      // interpret direction radio buttons
      String infix = "xxx"; //$NON-NLS-1$
      String fromFormat = "xxx"; //$NON-NLS-1$
      String toFormat = "xxx"; //$NON-NLS-1$
      if (utf2isoButton.isSelected()) {
        fromFormat = "UTF-8"; //$NON-NLS-1$
        toFormat = "ISO-8859-1"; //$NON-NLS-1$
        infix = "iso"; //$NON-NLS-1$
      } else {
        fromFormat = "ISO-8859-1"; //$NON-NLS-1$
        toFormat = "UTF-8"; //$NON-NLS-1$
        infix = "utf"; //$NON-NLS-1$
      }

      for (int i = 0; i < filenames.length; ++i) {
        File file = filenames[i];
        // if no output directory is selected, use input file
        // directory
        outputDir = new File(file.getParent());
        File inputFile = file;
        String filename = inputFile.getName();

        // the start index of the file name extension
        int extensionStart = filename.lastIndexOf('.');
        File outputFile = new File(outputDir, filename.substring(0, extensionStart) + "." + infix + filename.substring(extensionStart)); //$NON-NLS-1$

        String options[] = { Messages.getString("UTF82ISO.yes"), Messages.getString("UTF82ISO.yesAll"), Messages.getString("UTF82ISO.no"), Messages.getString("UTF82ISO.noNone") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        // ask for confirmation and...
        if (prompt == 0 || prompt == 2)
          prompt = JOptionPane.showOptionDialog(this, Messages.getString("UTF82ISO.convertPrompt.prefix") + fromFormat + Messages.getString("UTF82ISO.convertPrompt.infix1") + "\n" + inputFile + "\n" + Messages.getString("UTF82ISO.convertPrompt.infix2") + toFormat + Messages.getString("UTF82ISO.convertPrompt.suffix") + "\n" + outputFile, null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        if (prompt <= 1) {
          // ... if answer is yes, convert
          tried++;
          int result = convertFile(inputFile, outputFile, fromFormat, toFormat);
          if (result == 1)
            success.append(inputFile + "\n"); //$NON-NLS-1$
          else if (result == -1)
            fail.append(inputFile + "\n"); //$NON-NLS-1$
          else
            wrongFormat.append(inputFile + "\n"); //$NON-NLS-1$
        }
      }
      // show result message
      if (tried > 0)
        JOptionPane.showMessageDialog(this, Messages.getString("UTF82ISO.success.ok") + "\n" + (success.toString().equals("") ? "---" : success.toString()) + "\n" + Messages.getString("UTF82ISO.success.noTag") + fromFormat + Messages.getString("UTF82ISO.success.noTag.suffix") + "\n" + (wrongFormat.toString().equals("") ? "---" : wrongFormat.toString()) + "\n" + Messages.getString("UTF82ISO.success.error") + "\n" + (fail.toString().equals("") ? "---" : fail.toString()), Messages.getString("UTF82ISO.result"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    (new UTF2ISO()).run();
  }

  /**
   * Convert input file to output file.
   * 
   * @param inputFile
   * @param outputFile
   * @param fromFormat
   *          The input charset, a string like "UTF-8"
   * @param toFormat
   *          The output charset, a string like "ISO8859-1"
   * @return <code>-1</code> on failure, number of encountered lines with
   *         "charset string"; charset
   */
  public int convertFile(File inputFile, File outputFile, String fromFormat, String toFormat) {
    BufferedReader in = null;
    BufferedWriter out = null;
    int found = 0;
    try {
      // setup readers with correct encoding
      InputStream inStream = new BufferedInputStream(new FileInputStream(inputFile));
      BOMReader inReader = new BOMReader(inStream, fromFormat);
      in = new BufferedReader(inReader);

      OutputStream outStream = new BufferedOutputStream(new FileOutputStream(outputFile));
      OutputStreamWriter outWriter = new OutputStreamWriter(outStream, toFormat);
      out = new BufferedWriter(outWriter);

      // try to get input file format from BOM
      inReader.init();
      String realFormat = inReader.getEncoding();
      // correct anomalies of getEncoding

      InputStreamReader dummy = new InputStreamReader(inStream, fromFormat);
      String normalizedFormat = dummy.getEncoding();

      int answer = JOptionPane.YES_OPTION;
      if (!realFormat.equals(normalizedFormat)) {
        answer = JOptionPane.showConfirmDialog(this, inputFile + Messages.getString("UTF82ISO.wrongformatInfix") + fromFormat + Messages.getString("UTF82ISO.wrongFormatSuffix")); //$NON-NLS-1$ //$NON-NLS-2$
      }

      if (answer != JOptionPane.YES_OPTION)
        return -1;

      // do conversion
      while (in.ready()) {
        String line = in.readLine();
        // convert lines with charset information (cr-specific!)
        if (line.toString().equals("\"" + fromFormat + "\";charset")) { //$NON-NLS-1$ //$NON-NLS-2$
          found++;
          System.err.println(Messages.getString("UTF82ISO.found") + line); //$NON-NLS-1$
          line = "\"" + toFormat + "\";charset"; //$NON-NLS-1$ //$NON-NLS-2$
          System.err.println(Messages.getString("UTF82ISO.writing") + line); //$NON-NLS-1$
        }
        out.write(line);
        out.newLine();
      }
    } catch (FileNotFoundException e) {
      JOptionPane.showMessageDialog(this, Messages.getString("UTF82ISO.fileNotFound") + inputFile, Messages.getString("UTF82ISO.errorTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
      e.printStackTrace();
      return -1;
    } catch (UnsupportedEncodingException e) {
      JOptionPane.showMessageDialog(this, Messages.getString("UTF82ISO.encodingNotSupported") + e.getLocalizedMessage(), Messages.getString("UTF82ISO.errorTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
      e.printStackTrace();
      return -1;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, Messages.getString("UTF82ISO.ioError") + e.getLocalizedMessage(), Messages.getString("UTF82ISO.errorTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
      e.printStackTrace();
      return -1;
    } finally {
      // close streams
      if (in != null)
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      if (out != null)
        try {
          out.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
    return found;
  }
}
