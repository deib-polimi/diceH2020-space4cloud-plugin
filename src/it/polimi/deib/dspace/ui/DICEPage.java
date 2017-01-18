package it.polimi.deib.dspace.ui;

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

import it.polimi.deib.dspace.control.DICEWrap;

public class DICEPage extends WizardPage{
	private Composite container;
	private Button browse;
	private GridLayout layout;
	private GridData grid1;
	private GridData grid2;
	private GridData grid3;
	private Label label1;
	private Label list;

	protected DICEPage(String title, String description) {
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
        layout.verticalSpacing = 20;
        
        grid1 = new GridData();
        grid1.horizontalAlignment = GridData.END;
        label1 = new Label(container, SWT.PUSH);
        label1.setText("Load UML file: ");
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
        list.setText("Selected file:");
        
        browse.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	JFileChooser chooser= new JFileChooser();
            	chooser.setMultiSelectionEnabled(false); //JUST ONE UML FILE
            	
            	int choice = chooser.showOpenDialog(null);

            	if (choice != JFileChooser.APPROVE_OPTION) return;
            	
            	String chosenFile = chooser.getSelectedFile().getPath();
            	
            	try {
					DICEWrap.getWrapper().buildAnalyzableModel(chosenFile);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
					setPageComplete(false);
					return;
				}
            	
            	try {
					DICEWrap.getWrapper().genGSPN();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
					setPageComplete(false);
					return;
				}
            	list.setText(list.getText() + "\n" + chosenFile);
            	container.layout();
            	setPageComplete(true);
            }

        });
        
        
        setPageComplete(false);
        setControl(container);
	}

}

