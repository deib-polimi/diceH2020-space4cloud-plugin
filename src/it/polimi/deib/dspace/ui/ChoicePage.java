package it.polimi.deib.dspace.ui;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import it.polimi.deib.dspace.control.Configuration;
import it.polimi.deib.dspace.net.NetworkManager;

public class ChoicePage extends WizardPage{
	private Composite container;
	private GridLayout layout;
	private Button pri;
	private Button pub;
	private int classes = 0;
	private int alternatives;
	private Text t1,h1,h2,h3;
	private List t2;
	private Label l1;
	private Label l2;
	private GridData g1,g3,g5,g6,f1,f2,f3;
	private Button existingLTC,nExistingLTC;
	private Text rTextField,SpsrTextField;
	private Composite ltcCompositeText;
	

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
        
        
        
        
        
        //ltcUberComposite.setLayoutData(grid1);

        g1 = new GridData();
        g1.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
        g1.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
        l1 = new Label(container, SWT.NONE);
        //l1.setLayoutData(g1);
        l1.setText("Number of classes:");
        
        new GridData(SWT.CENTER, SWT.BEGINNING, true, true);
        l2 = new Label(container, SWT.NONE);
        //l2.setLayoutData(g2);
        l2.setText("Select technology:");
        
        g3 = new GridData();
        g3.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
        g3.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
        t1 = new Text(container, SWT.BORDER);
        //t1.setLayoutData(g3);
        t1.setEditable(true);
        
        t1.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	getWizard().getContainer().updateButtons();
            }

        });
        
        new GridData(SWT.CENTER, SWT.BEGINNING, true, true);
        t2 = new List(container, SWT.BORDER);
        //t2.setLayoutData(g4);
        t2.setItems(NetworkManager.getInstance().getTechnologies());
        t2.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	Configuration.getCurrent().setTechnology(t2.getSelection()[0]);
            	getWizard().getContainer().updateButtons();
            }

        });
        
        f1 = new GridData();
        f1.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
        f1.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
        h1 = new Text(container, SWT.PUSH);
        h1.setVisible(false);
        //h1.setLayoutData(f1);
        
        f2 = new GridData();
        f2.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
        f2.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
        h2 = new Text(container, SWT.BORDER);
        h2.setVisible(false);
        //h2.setLayoutData(f2);
        
        g5 = new GridData();
        g5.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
        pri = new Button(container, SWT.RADIO);
        //pri.setLayoutData(g5);
        
        
        
        
        
        
        f3 = new GridData();
        f3.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
        f3.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
        h3 = new Text(container, SWT.BORDER);
        h3.setVisible(false);
        //h3.setLayoutData(f3);
        
        
        g6 = new GridData();
        g6.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
        pub = new Button(container, SWT.RADIO);
        //pub.setLayoutData(g6);
        

        pri.setVisible(true);
        pub.setVisible(true);
        pri.setText("Private");
        pub.setText("Public");
        
        Composite ltcUberComposite = new Composite(container, SWT.NONE);
        RowLayout layoutRow_uber = new RowLayout();
        layoutRow_uber.fill = true;
        ltcUberComposite.setLayout(layoutRow_uber);
        //Composite for ltc radio btns
        final Composite ltcComposite = new Composite(ltcUberComposite, SWT.NONE);
        RowLayout layoutRow = new RowLayout();
        layoutRow.type = SWT.VERTICAL;
        ltcComposite.setLayout(layoutRow);
        ltcComposite.setVisible(false);
        
        //Composite for ltc text inputs
        ltcCompositeText = new Composite(ltcUberComposite, SWT.NONE);
        RowLayout layoutRow_1 = new RowLayout();
        layoutRow_1.type = SWT.VERTICAL;
        ltcCompositeText.setLayout(layoutRow_1);
        ltcCompositeText.setVisible(false);
        
        Composite rTextComposite = new Composite(ltcCompositeText, SWT.NONE);
        RowLayout layoutRow_4 = new RowLayout();
        layoutRow_4.type = SWT.HORIZONTAL;
        layoutRow_4.pack = false;
        rTextComposite.setLayout(layoutRow_4);
        Label rTextLabel = new Label(rTextComposite, SWT.NONE);
        rTextLabel.setText("R");
        this.rTextField = new Text(rTextComposite, SWT.BORDER);
        this.rTextField.setEditable(true);
  
        
        Composite tTextComposite = new Composite(ltcCompositeText, SWT.NONE);
        RowLayout layoutRow_3 = new RowLayout();
        layoutRow_3.type = SWT.HORIZONTAL;
        layoutRow_3.pack = false;
        tTextComposite.setLayout(layoutRow_3);
        Label tTextLabel = new Label(tTextComposite, SWT.NONE);
        tTextLabel.setText("Spsr");
        this.SpsrTextField = new Text(tTextComposite,SWT.BORDER);
        this.SpsrTextField.setEditable(true);
        this.existingLTC = new Button(ltcComposite,SWT.RADIO);
        this.existingLTC.setText("Existing LTC");
        this.nExistingLTC = new Button(ltcComposite, SWT.RADIO);
        this.nExistingLTC.setText("Non existing LTC");
        
        
        
		
        
        
        
        pri.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	getWizard().getContainer().updateButtons();
            	System.out.println("Choice: PRIVATE");
            	ltcCompositeText.setVisible(false);
            	ltcComposite.setVisible(false);
            	Configuration.getCurrent().setPrivate(true);
            }

        });
        
        pub.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	getWizard().getContainer().updateButtons();
            	ltcComposite.setVisible(true);
            	if(existingLTC.getSelection()){
            		ltcCompositeText.setVisible(true);
            	}
            	System.out.println("Choice: PUBLIC");
            	Configuration.getCurrent().setPrivate(false);
            }

        });
        this.existingLTC.addSelectionListener(new SelectionAdapter(){
        	 public void widgetSelected(SelectionEvent e) {
             	ltcCompositeText.setVisible(true);
             	getWizard().getContainer().updateButtons();
             	Configuration.getCurrent().setLTC(true);
             }
        });
        this.rTextField.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
        });
        this.SpsrTextField.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
        });
        this.nExistingLTC.addSelectionListener(new SelectionAdapter(){
       	 public void widgetSelected(SelectionEvent e) {
            	ltcCompositeText.setVisible(false);
            	getWizard().getContainer().updateButtons();
            	Configuration.getCurrent().setLTC(false);
            }
       });
        
        setControl(container);
        setPageComplete(false);
	}
	
	public boolean getChoice(){
		if(this.ltcCompositeText.getVisible()){
			if(!this.rTextField.getText().equals("") &&
					!this.SpsrTextField.getText().equals("")){
				return true;
			}
			else{
				return false;
			}
		}
		return true;
		
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
		if(this.getChoice() && t2.getSelectionCount() > 0 && (pri.getSelection() || pub.getSelection()) && getClasses() != 0){
			return true;
		}
		return false;
	}
	
	public int getR(){
		return Integer.parseInt(rTextField.getText());
	}
	
	public float getSpsr(){
		return Float.parseFloat(SpsrTextField.getText());
	}
}
