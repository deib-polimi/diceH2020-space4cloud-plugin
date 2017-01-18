package it.polimi.deib.dspace.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.polimi.deib.dspace.control.DICEWrap;

public class AltDialog extends JFrame{
	private JPanel contentPane;
	private JButton browse;
	private JLabel l1;
	private JLabel l2;
	private JButton close;
	
	public AltDialog(){
		super();
		create();
		setVisible(true);
	}
	
	private void create(){
		contentPane = (JPanel)this.getContentPane();
		contentPane.setLayout(new FlowLayout());
		contentPane.setVisible(true);
		
		browse = new JButton("Browse...");
		browse.setVisible(true);
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				JFileChooser chooser= new JFileChooser();
            	chooser.setMultiSelectionEnabled(false); //JUST ONE UML FILE
            	
            	int choice = chooser.showOpenDialog(null);

            	if (choice != JFileChooser.APPROVE_OPTION) return;
            	
            	String chosenFile = chooser.getSelectedFile().getPath();
            	
            	try {
					DICEWrap.getWrapper().buildAnalyzableModel(chosenFile);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
					return;
				}
            	
            	try {
					DICEWrap.getWrapper().genGSPN();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
					return;
				}
            	
            	close.setEnabled(true);
            	doLayout();
			}
        });
		
		l1 = new JLabel("Select UML file:");
		l1.setVisible(true);
		
		close = new JButton("Finish");
		close.setVisible(true);
		close.setEnabled(false);
		
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				dispose();
			}
        });
		
		contentPane.add(l1);
		contentPane.add(browse);
		contentPane.add(close);
		
		pack();
		
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setResizable(false);
	}
}
