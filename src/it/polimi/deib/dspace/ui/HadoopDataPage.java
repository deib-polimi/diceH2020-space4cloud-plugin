package it.polimi.deib.dspace.ui;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class HadoopDataPage extends WizardPage{
	private Composite container;
	private GridLayout layout;
	private int thinkTime;
	private int hlow;
	private int hup;
	private double hadoopD;
	private Text thinkTextField,hlowTextField,hupTextField,hadoopDTextField;
	
	protected HadoopDataPage(String pageName) {
		super("Select data for hadoop Technology");
		setTitle(pageName);
		thinkTime=-1;
		hlow=-1;
		hup=-1;
		hadoopD=-1;
	}

	@Override
	public void createControl(Composite arg0) {
		container = new Composite(arg0, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns=1;
		container.setLayout(layout);
		Label l1;
		l1 = new Label(container, SWT.None);
		l1.setText("Set Think Time");
		this.thinkTextField = new Text(container, SWT.BORDER);
        //t1.setLayoutData(g3);
        thinkTextField.setEditable(true);
        
        thinkTextField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				try{
				thinkTime=Integer.parseInt(thinkTextField.getText());
				System.out.println(thinkTime);
				}catch(NumberFormatException e){
					
				}
				getWizard().getContainer().updateButtons();
			}

        });
        Label l2=new Label(container,SWT.None);
		l2.setText("Set D parameter");
        this.hadoopDTextField=new Text(container,SWT.BORDER);
        hadoopDTextField.setEditable(true);
		hadoopDTextField.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent arg0) {
				try{
				hadoopD=Double.parseDouble(hadoopDTextField.getText());
				System.out.println(hadoopD);
				}catch(NumberFormatException e){
					
				}
				getWizard().getContainer().updateButtons();
				
			}
			
		});
		Label l3=new Label(container,SWT.None);
		l3.setText("Set hlow parameter");
        hlowTextField=new Text(container,SWT.BORDER);
        hlowTextField.setEditable(true);
		hlowTextField.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent arg0) {
				try{
				hlow=Integer.parseInt(hlowTextField.getText());

				System.out.println(hlow);
				}catch(NumberFormatException e){
					
				}
				getWizard().getContainer().updateButtons();
				
			}
			
		});
		
		Label l4=new Label(container,SWT.None);
		l4.setText("Set hup parameter");
        hupTextField=new Text(container,SWT.BORDER);
        hupTextField.setEditable(true);
		hupTextField.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent arg0) {
				try{
				hup=Integer.parseInt(hupTextField.getText());

				System.out.println(hup);
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
		if(this.hadoopD!=-1&&this.thinkTime!=-1&&this.hlow!=-1&&this.hup!=-1){
			return true;
		}
		return false;
	}

	public int getThinkTime() {
		return thinkTime;
	}

	public void setThinkTime(int thinkTime) {
		this.thinkTime = thinkTime;
	}

	public int getHlow() {
		return hlow;
	}

	public double getHadoopD() {
		return hadoopD;
	}

	public int getHup() {
		return hup;
	}

	
	
	

}
