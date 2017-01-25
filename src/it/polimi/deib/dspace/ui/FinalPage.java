package it.polimi.deib.dspace.ui;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class FinalPage extends WizardPage{
	private Composite container;
	private Label l1;
	private GridLayout layout;

	protected FinalPage(String title, String description) {
		super("Browse Files");
		setTitle(title);
		setDescription(description);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		
		layout = new GridLayout();
		container.setLayout(layout);
		
		l1 = new Label(container, SWT.None);
		l1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		l1.setText("Finish!");
        
        setControl(container);
        setPageComplete(true);
        getWizard().getContainer().updateButtons();
	}
}
