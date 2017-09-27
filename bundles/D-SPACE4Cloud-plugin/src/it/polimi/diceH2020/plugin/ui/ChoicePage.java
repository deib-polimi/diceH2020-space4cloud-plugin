/*
Copyright 2017 Arlind Rufi
Copyright 2017 Gianmario Pozzi
Copyright 2017 Giorgio Pea

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package it.polimi.diceH2020.plugin.ui;

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

import it.polimi.diceH2020.plugin.control.Configuration;
import it.polimi.diceH2020.plugin.net.NetworkManager;
import utils.Premium;

/**
 * Initial page. The user can: -choose among private/public solution -select
 * computation technology -tune some general parameters (e.g. number of classes)
 * 
 * @author kom
 *
 */
public class ChoicePage extends WizardPage {
	private Composite container;
	private GridLayout layout;
	private Button pri;
	private Button pub;
	private int classes = 0;
	private int alternatives;
	private Text t1, h1, h2, h3;
	private List t2;
	private Label l1;
	private Label l2;
	private GridData g1, g3, g5, g6, f1, f2, f3;
	private Button existingLTC, nExistingLTC;
	private Text rTextField, SpsrTextField;
	private Composite ltcCompositeText;
	private Label errSR;

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
		l1.setText("Number of classes:");

		new GridData(SWT.CENTER, SWT.BEGINNING, true, true);
		l2 = new Label(container, SWT.NONE);
		l2.setText("Select technology:");

		g3 = new GridData();
		g3.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		g3.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;

		t1 = new Text(container, SWT.BORDER);
		t1.setEditable(true);

		t1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});

		new GridData(SWT.CENTER, SWT.BEGINNING, true, true);
		t2 = new List(container, SWT.BORDER);
		t2.setItems(NetworkManager.getInstance().getTechnologies());

		t2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Configuration.getCurrent().setTechnology(getTechnology());
				if (getTechnology().equalsIgnoreCase("Storm")) {
					pri.setEnabled(false);
				} 
				else {
					pri.setEnabled(true);
				}
				
				if (getTechnology().equalsIgnoreCase("Spark")){
					t1.setText("1");
					t1.setEnabled(false);
				} 
				else {
					t1.setEnabled(true);
					t1.setEditable(true);
				}
				getWizard().getContainer().updateButtons();
			}
		});

		f1 = new GridData();
		f1.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		f1.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;

		h1 = new Text(container, SWT.PUSH);
		h1.setVisible(false);

		f2 = new GridData();
		f2.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		f2.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;

		h2 = new Text(container, SWT.BORDER);
		h2.setVisible(false);

		g5 = new GridData();
		g5.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		pri = new Button(container, SWT.RADIO);

		f3 = new GridData();
		f3.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		f3.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;

		h3 = new Text(container, SWT.BORDER);
		h3.setVisible(false);

		g6 = new GridData();
		g6.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		pub = new Button(container, SWT.RADIO);

		pri.setVisible(true);
		pub.setVisible(true);
		pri.setText("Private");
		pub.setText("Public");

		Composite ltcUberComposite = new Composite(container, SWT.NONE);
		RowLayout layoutRow_uber = new RowLayout();
		layoutRow_uber.fill = true;
		ltcUberComposite.setLayout(layoutRow_uber);

		// Composite for ltc radio btns
		final Composite ltcComposite = new Composite(ltcUberComposite, SWT.NONE);

		RowLayout layoutRow = new RowLayout();
		layoutRow.type = SWT.VERTICAL;
		ltcComposite.setLayout(layoutRow);
		ltcComposite.setVisible(false);

		// Composite for ltc text inputs
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
		rTextLabel.setText("# reserved");
		rTextField = new Text(rTextComposite, SWT.BORDER);
		rTextField.setEditable(true);

		Composite tTextComposite = new Composite(ltcCompositeText, SWT.NONE);
		RowLayout layoutRow_3 = new RowLayout();
		layoutRow_3.type = SWT.HORIZONTAL;
		layoutRow_3.pack = false;
		tTextComposite.setLayout(layoutRow_3);

		Label tTextLabel = new Label(tTextComposite, SWT.NONE);
		tTextLabel.setText("Spot ratio");
		SpsrTextField = new Text(tTextComposite, SWT.BORDER);
		SpsrTextField.setEditable(true);
		if (!Premium.isPremium()) {
			tTextLabel.setVisible(false);
			SpsrTextField.setVisible(false);
		}

		Composite err = new Composite(ltcCompositeText, SWT.NONE);
		RowLayout errRow_3 = new RowLayout();
		errRow_3.type = SWT.HORIZONTAL;
		errRow_3.pack = false;
		err.setLayout(errRow_3);

		errSR = new Label(err, SWT.BORDER);

		errSR.setText("Not a valid spot ratio");
		errSR.setVisible(false);

		existingLTC = new Button(ltcComposite, SWT.RADIO);
		existingLTC.setText("Existing LTC");

		nExistingLTC = new Button(ltcComposite, SWT.RADIO);
		nExistingLTC.setText("Non existing LTC");

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
				if (existingLTC.getSelection()) {
					ltcCompositeText.setVisible(true);
				}
				System.out.println("Choice: PUBLIC");
				Configuration.getCurrent().setPrivate(false);
			}
		});

		existingLTC.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ltcCompositeText.setVisible(true);
				getWizard().getContainer().updateButtons();
				Configuration.getCurrent().setLTC(true);
			}
		});

		rTextField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});

		if (Premium.isPremium()) {
			SpsrTextField.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent arg0) {
					try {
						if (Float.parseFloat(SpsrTextField.getText()) <= 1) {
							errSR.setVisible(false);
						} else {
							errSR.setVisible(true);
						}
					} catch (NumberFormatException e) {

					}

					getWizard().getContainer().updateButtons();
				}
			});
		}

		nExistingLTC.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ltcCompositeText.setVisible(false);
				getWizard().getContainer().updateButtons();
				Configuration.getCurrent().setLTC(false);
			}
		});

		setControl(container);
		setPageComplete(false);
	}

	private boolean areLtcFieldsValid() {
		if (ltcCompositeText.getVisible()) {
			if (rTextField.getText().equals("")) {
				return false;
			}
			try {
				if (Premium.isPremium()
						&& (SpsrTextField.getText().equals("") || Float.parseFloat(SpsrTextField.getText()) > 1)) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		}
		return true;
	}

	public int getClasses() {
		classes = Integer.parseInt(t1.getText());
		return classes;
	}

	public int getAlternatives() {
		return alternatives;
	}

	public String getTechnology() {
		return t2.getSelection()[0];
	}

	@Override
	public boolean canFlipToNextPage() {
		if (areLtcFieldsValid() && t2.getSelectionCount() > 0 && (pri.getSelection() || pub.getSelection())
				&& getClasses() != 0) {
			if (getTechnology().equalsIgnoreCase("Storm") && pri.getSelection()) {
				return false;
			}
			return true;
		}
		return false;
	}

	public int getR() {
		return Integer.parseInt(rTextField.getText());
	}

	public float getSpsr() {
		if (SpsrTextField.getText().isEmpty()) {
			return 0;
		} else {
			return Float.parseFloat(SpsrTextField.getText());
		}

	}
}
