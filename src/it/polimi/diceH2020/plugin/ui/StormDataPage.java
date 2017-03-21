package it.polimi.diceH2020.plugin.ui;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class StormDataPage extends WizardPage{
	private Composite container;
	private GridLayout layout;
	private double stormU;
	private Text stormUTextField;
	protected StormDataPage(String pageName) {
		super("Select data for hadoop Technology");
		setTitle(pageName);
		stormU=-1;
	}

	@Override
	public void createControl(Composite arg0) {
		container = new Composite(arg0, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns=1;
		container.setLayout(layout);
		Label l1;
		l1 = new Label(container, SWT.None);
		l1.setText("Set U parameter");
		this.stormUTextField = new Text(container, SWT.BORDER);
		//t1.setLayoutData(g3);
		stormUTextField.setEditable(true);

		stormUTextField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				try{
					stormU=Double.parseDouble(stormUTextField.getText());
				}catch(NumberFormatException e){

				}
				getWizard().getContainer().updateButtons();
			}

		});
		setControl(container);
		setPageComplete(false);	

	}

	@Override
	public boolean canFlipToNextPage(){
		if(stormU!=-1){
			return true;
		}
		return false;
	}

	public void reset(){
		this.stormU=-1;
		this.stormUTextField.setText("");
	}
	public double getStormU(){
		return this.stormU;
	}
}
