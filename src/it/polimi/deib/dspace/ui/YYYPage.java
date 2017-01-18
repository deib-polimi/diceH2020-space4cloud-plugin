package it.polimi.deib.dspace.ui;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class YYYPage extends WizardPage{
	private static final Logger LOGGER = Logger.getLogger( YYYPage.class.getName() );
	private Composite container;
	private Button browse;
	private GridLayout layout;
	private GridData grid1;
	private GridData grid2;
	private GridData grid3;
	private Label label1;
	private Label list;

	protected YYYPage(String title, String description) {
		super("Browse Files");
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
        layout.horizontalSpacing = 40;
        layout.verticalSpacing = 50;
        
        grid1 = new GridData();
        grid1.horizontalAlignment = GridData.END;
        label1 = new Label(container, SWT.PUSH);
        label1.setText("Choose files...");
        label1.setLayoutData(grid1);

        grid2 = new GridData();
        grid2.horizontalAlignment = GridData.BEGINNING;
        browse = new Button(container, SWT.PUSH);
        browse.setText("Browse...");
        browse.setLayoutData(grid2);
        
        grid3 = new GridData();
        grid3.horizontalAlignment = GridData.FILL;
        grid3.horizontalSpan = 2;
        list = new Label(container, SWT.PUSH);
        list.setLayoutData(grid3);
        list.setText("Selected files:");
        
        browse.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	JFileChooser chooser= new JFileChooser();
            	chooser.setMultiSelectionEnabled(true);
            	
            	int choice = chooser.showOpenDialog(null);

            	if (choice != JFileChooser.APPROVE_OPTION) return;

            	Vector<File> chosenFiles = new Vector<File>(Arrays.asList(chooser.getSelectedFiles()));
    			for(File I:chosenFiles){
            		if(getFileExtension(I.getPath()).equals("yyy")){
            			LOGGER.fine(I.getName()+" added.");
            			list.setText(list.getText()+"\n"+I.getPath());
            			list.getParent().layout();
            		}
            		else{
            			JOptionPane.showMessageDialog(null, "Error while loading "+I.getName()+". " +
            					"Wrong extension! XXX files needed at this step.");
            			LOGGER.severe("Error while loading file "+I.getName()+". Wrong extension.");
        				list.setText("Selected files:");
            			list.getParent().layout();
            			return;
            		}
            	}
            	setPageComplete(true);
            	System.out.println(canFlipToNextPage() ? "true" : "false");
            }

        });
        
        
        setPageComplete(false);
        setControl(container);
	}
	
	private String getFileExtension(String name){
		System.out.println(name);
		return name.substring(name.lastIndexOf('.')+1);
	}

}

