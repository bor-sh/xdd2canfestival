/**
 * Copyright (c) 2010-2012 Miko Kinski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in 
 * the Software without restriction, including without limitation the rights to 
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies 
 * of the Software, and to permit persons to whom the Software is furnished to do 
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 */
package main;
import java.awt.*;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;

import org.eclipse.emf.mwe2.launch.runtime.Mwe2Launcher;

public class MainGUI extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    JTextArea   text    = new JTextArea();
    private JScrollPane sbrText; // Scroll pane for text area

    String model        = "";
    String modelDir     = "";
    String metamodel;
    String dest;
  
    JDialog infodialog      = new InfoDialog();
    JMenuItem menuItemGen   = new JMenuItem("Generate");
    JLabel statusbar        = new JLabel();

    public MainGUI()
    {
      super("Objectdictionary Generator");      
      setSize(350,200);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
  
      Container c = getContentPane();
      c.setLayout(new FlowLayout());
      c.add(statusbar);
             
      text.setSize(200, 50);
      text.setBounds(400, 310, 100, 20);
      text.setLocation(400, 310);
      text.setVisible(true);
      text.setLineWrap(true);
      sbrText = new JScrollPane(text);
      sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

//      text.append("Output:\n");
      MeinPrintStream ausgabe = new MeinPrintStream(text);
      System.setOut(ausgabe);

//      c.add(sbrText);
      
      JMenuBar menuBar;
      JMenu menu;
      JMenuItem menuItem;

      //Create the menu bar.
      menuBar = new JMenuBar();

      //Build the first menu.
      menu      = new JMenu("File");
      menuItem  = new JMenuItem("Open XDD/XML");
      menuItem.addActionListener(new ButtonListener());
      menu.add(menuItem);
      menuBar.add(menu);
      menu      = new JMenu("Edit");
      menuItemGen.addActionListener(new ButtonListenerGen());
      menuItemGen.setEnabled(false);
      menu.add(menuItemGen);
      menuBar.add(menu);
      menu = new JMenu("Help");
      menuItem  = new JMenuItem("Info");
      menuItem.addActionListener(new ButtonListenerInfo());
      menu.add(menuItem);
      menuBar.add(menu);
      
      setJMenuBar(menuBar);
      
     }

    class ButtonListener implements ActionListener {
        
        public void actionPerformed(ActionEvent e) 
        {
            FileFilter filter = new FileNameExtensionFilter("XDD/XML-Files", "xml", "xdd");
            JFileChooser chooser = new JFileChooser();
            chooser.addChoosableFileFilter(filter);
            int option = chooser.showOpenDialog(MainGUI.this);
            
            if (option == JFileChooser.APPROVE_OPTION)
            {
                File sf  = chooser.getSelectedFile();
                model    = sf.getPath();
                modelDir = sf.getParent();
                menuItemGen.setEnabled(true);
                statusbar.setText("Goto Edit->Generate to start the generation process.");
//                System.out.println("Location of Model: ".concat(model));
            }
            else
            {
//                System.out.println("No File selected!");
            }
        }
    }

    class WaitThread extends Thread {
        
        private boolean stop = false;
        
        public void threadHalt(){
            stop = true;
        }
        
        public void run() {
          try {
             
              int count = 0;
              int modcnt = 0;
              while(!stop)
              {
                  if(count > 2)
                      count = 0;
                  modcnt = count % 3;
                  switch(modcnt)
                  {
                      case 0:
                          statusbar.setText("Please wait .  ");
                          break;
                      case 1:
                          statusbar.setText("Please wait  . ");
                          break;
                      case 2:
                          statusbar.setText("Please wait   .");
                          break;                              
                  }
                  count++;
                  Thread.sleep(1000);
              }
          } catch (RuntimeException ex) {
          } catch (InterruptedException e)
          {
          }
        }
    };

    
    class ButtonListenerGen implements ActionListener {
        
        public void actionPerformed(ActionEvent e) 
        {
            metamodel = ClassLoader.getSystemResource("metamodel/canopen.ecore").toString();
            dest = "";
            
            if(!modelDir.substring(0).equals("/"))
            {
                dest        = "file://";
                model       = model.replace('\\','/');
                modelDir    = modelDir.replace('\\','/');
            }            
            
            Thread worker = new Thread() {
                public void run() {
                  try {
                      WaitThread counting = new WaitThread();
                      counting.start();
                      try {
                      Mwe2Launcher.main(
                              new String[] {
                                      "workflow.Workflow",
                                      "-p", "model=".concat(dest).concat(model),
                                      "-p", "metamodel=".concat(metamodel),
                                      "-p", "fileEncoding=UTF-8",
                                      "-p", "template=templates::ObjectDictionaryC::Root FOR model",
                                      "-p", "srcpath=".concat(modelDir),
                              });                      
                      } catch (RuntimeException ex){
                          statusbar.setText("Wrong File!");                                                
                      }
                      counting.threadHalt();
                  } catch (RuntimeException ex) {
                      statusbar.setText("Wrong File!");                      
                  }

                  // Report the result using invokeLater().
                  SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                      statusbar.setText("Ready");
                      setEnabled(true);
                    }
                  });
                }
            };
            worker.start();
        }
    }

   class ButtonListenerInfo implements ActionListener {
        
        public void actionPerformed(ActionEvent e) 
        {
            infodialog.setVisible(true);
        }
    }
   
    class MeinPrintStream extends PrintStream
    {
        public MeinPrintStream(JTextArea anzeige)
        {
            super(new MeinOutputStream());
        }
    }

    class MeinOutputStream extends OutputStream
    {        
        
        public void write(int b)
        {
            char c=(char)b;
            text.append(String.valueOf(c));
        }
    }

    class InfoDialog extends JDialog {

        /**
         * 
         */
        private static final long serialVersionUID = 526622418686153064L;

        public InfoDialog() {

          Box b = Box.createVerticalBox();
          b.add(Box.createGlue());
          b.add(new JLabel("  Author: Miko Kinski"));
          b.add(new JLabel("  e-Mail: "));
          b.add(new JLabel("  Date  : 20.03.2011"));
          b.add(Box.createGlue());
          getContentPane().add(b, "Center");

          JPanel p2 = new JPanel();
          JButton ok = new JButton("Ok");
          p2.add(ok);
          getContentPane().add(p2, "South");

          ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
              setVisible(false);
            }
          });

          setSize(300, 150);
          setLocation(350,350);
        }
      }
 
    public static void main(final String[] args) 
    {
        try{
            MainGUI  mfc = new MainGUI();        
            mfc.setVisible(true);
            mfc.setLocation(300,300);
        } catch (RuntimeException ex){
            System.out.println("Oh oh");
        }
    }
}
