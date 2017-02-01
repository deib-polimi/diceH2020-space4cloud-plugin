package it.polimi.deib.dspace.ui;

import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import it.polimi.deib.dspace.net.NetworkManager;

public class ChoicePage extends WizardPage{
	private static final Logger LOGGER = Logger.getLogger(ChoicePage.class.getName() );
	private Composite container;
	private GridLayout layout;
	private Button pri;
	private Button pub;
	private boolean choice ; // t - public, f - private
	private int classes = 0;
	private int alternatives;
	private Text t1,h1,h2,h3;
	private List t2;
	private Label l1;
	private Label l2;
	private GridData g1,g2,g3,g4,g5,g6,f1,f2,f3;

	protected ChoicePage(String title, String description) {
		super("Choose service type");
		setTitle(title);
		setDescription(description);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		layout = new GridLayout();
		container.setLayout(layout);
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = true;

        g1 = new GridData();
        g1.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
        g1.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
        l1 = new Label(container, SWT.NONE);
        l1.setLayoutData(g1);
        l1.setText("Number of classes:");
        
        g2 = new GridData(SWT.CENTER, SWT.BEGINNING, true, true);
        l2 = new Label(container, SWT.NONE);
        l2.setLayoutData(g2);
        l2.setText("Select technology:");
        
        g3 = new GridData();
        g3.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
        g3.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
        t1 = new Text(container, SWT.BORDER);
        t1.setLayoutData(g3);
        t1.setEditable(true);
        
        t1.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	getWizard().getContainer().updateButtons();
            }

        });
        
        g4 = new GridData(SWT.CENTER, SWT.BEGINNING, true, true);
        t2 = new List(container, SWT.BORDER);
        t2.setLayoutData(g4);
        t2.setItems(NetworkManager.getInstance().getTechnologies());
        t2.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	getWizard().getContainer().updateButtons();
            }

        });
        
        f1 = new GridData();
        f1.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
        f1.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
        h1 = new Text(container, SWT.PUSH);
        h1.setVisible(false);
        h1.setLayoutData(f1);
        
        f2 = new GridData();
        f2.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
        f2.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
        h2 = new Text(container, SWT.BORDER);
        h2.setVisible(false);
        h2.setLayoutData(f2);
        
        g5 = new GridData();
        g5.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
        pri = new Button(container, SWT.RADIO);
        pri.setLayoutData(g5);
        
        f3 = new GridData();
        f3.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
        f3.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
        h3 = new Text(container, SWT.BORDER);
        h3.setVisible(false);
        h3.setLayoutData(f3);
        
        g6 = new GridData();
        g6.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
        pub = new Button(container, SWT.RADIO);
        pub.setLayoutData(g6);

        pri.setVisible(true);
        pub.setVisible(true);
        pri.setText("Private");
        pub.setText("Public");
        
        
        pri.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	choice = false;
            	getWizard().getContainer().updateButtons();
            	System.out.println("Choice: PRIVATE");
            }

        });
        
        pub.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	choice = true;
            	getWizard().getContainer().updateButtons();
            	System.out.println("Choice: PUBLIC");
            }

        });
        
        setControl(container);
        setPageComplete(false);
	}
	
	public boolean getChoice(){
		return choice;
	}
	
	public int getClasses(){
		classes = Integer.parseInt(t1.getText());
		return classes;
	}

	public int getAlternatives(){
		return alternatives;
	}
	
	public String getTechnology(){
		return t2.getSelection()[0];
	}
	
	@Override
	public boolean canFlipToNextPage(){
		if(t2.getSelectionCount() > 0 && (pri.getSelection() || pub.getSelection()) && getClasses() != 0){
			return true;
		}
		return false;
	}
}
