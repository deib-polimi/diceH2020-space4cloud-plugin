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

import java.awt.GridBagConstraints;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.polimi.diceH2020.plugin.control.PrivateConfiguration;
import it.polimi.diceH2020.plugin.control.VmClass;
import it.polimi.diceH2020.plugin.preferences.Preferences;

public class PrivateConfigPage extends WizardPage {
	private Composite container;
	private GridLayout layout;
	private Button addConfig, removeConfig, saveConfig, loadConfig;
	private Text nNodesText, cpuText, memNodeText;
	private List vmConfigsList;
	private int numNodes;
	private double cpuForNode;
	private double memForNode;
	private String selectedVmConfig;

	protected PrivateConfigPage(String pageName) {
		super("Select cluster parameters");
		setTitle(pageName);
		numNodes = -1;
		cpuForNode = -1;
		memForNode = -1;
	}

	@Override
	public void createControl(Composite arg0) {
		container = new Composite(arg0, SWT.NONE);
		layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;

		// Creation
		Label nNodeLab = new Label(container, SWT.NONE);
		nNodeLab.setText("Set number of nodes");

		Label cpuLabel = new Label(container, SWT.NONE);
		cpuLabel.setText("Cpu per node");

		nNodesText = new Text(container, SWT.BORDER);
		cpuText = new Text(container, SWT.BORDER);

		Label memLabel = new Label(container, SWT.NONE);
		memLabel.setText("Set memory per node [GB]");
		
		new Label(container, SWT.NONE); 						// Padding
		memNodeText = new Text(container, SWT.BORDER);
		
		new Label(container, SWT.NONE); 						// Padding
		new Label(container, SWT.NONE); 						// Padding
		new Label(container, SWT.NONE); 						// Padding
		

		vmConfigsList = new List(container, SWT.BORDER);
		vmConfigsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		addConfig = new Button(container, SWT.PUSH);
		addConfig.setText("Add new configuration");

		new Label(container, SWT.NONE);

		removeConfig = new Button(container, SWT.PUSH);
		removeConfig.setText("Remove configuration");

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		GridLayout inLayout = new GridLayout();
		inLayout.numColumns = 2;

		Composite butComp = new Composite(container, SWT.NONE);
		butComp.setLayout(inLayout);

		saveConfig = new Button(butComp, SWT.NONE);
		saveConfig.setText("Save");

		loadConfig = new Button(butComp, SWT.NONE);
		loadConfig.setText("Load");

		// Listeners
		nNodesText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				try {
					numNodes = Integer.parseInt(nNodesText.getText());
				} catch (NumberFormatException e) {
					numNodes = -1;
				}
				getWizard().getContainer().updateButtons();
			}
		});

		cpuText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				try {
					cpuForNode = Double.parseDouble(cpuText.getText());
				} catch (NumberFormatException e) {
					cpuForNode = -1;
				}
				getWizard().getContainer().updateButtons();
			}
		});

		memNodeText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				try {
					memForNode = Double.parseDouble(memNodeText.getText());
				} catch (NumberFormatException e) {
					memForNode = -1;
				}
				getWizard().getContainer().updateButtons();
			}
		});

		addConfig.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				String name = "";
				double mem = 0;
				double cost = 0;
				double cores = 0;

				GridBagConstraints c = new GridBagConstraints();
				JTextField coreField = new JTextField(5);
				JTextField memField = new JTextField(5);
				JTextField costField = new JTextField(5);
				JTextField nameField = new JTextField(5);

				JPanel myPanel = new JPanel();
				myPanel.add(new JLabel("Name of VM :"), c);
				myPanel.add(nameField, c);
				myPanel.add(new JLabel("Num Cores :"), c);
				myPanel.add(coreField);
				myPanel.add(new JLabel("Memory [GB]:"), c);
				myPanel.add(memField);
				myPanel.add(new JLabel("Cost [$/h]:"), c);
				myPanel.add(costField);

				int result = JOptionPane.showConfirmDialog(null, myPanel, "Please Enter VM parameters",
						JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					boolean canCreate = true;
					name = nameField.getText();

					try {
						mem = Double.parseDouble(memField.getText());
					} catch (NumberFormatException e1) {
						canCreate = false;
					}

					try {
						cores = Double.parseDouble(coreField.getText());
					} catch (NumberFormatException e1) {
						canCreate = false;
					}

					try {
						cost = Double.parseDouble(costField.getText());
					} catch (NumberFormatException e1) {
						canCreate = false;
					}

					if (!name.isEmpty() && canCreate) {
						PrivateConfiguration.getCurrent().addVmConfig(new VmClass(name, cores, mem, cost));
						vmConfigsList.add(name);
					} else {
						JOptionPane.showConfirmDialog(null, "Some of the parameters are wrong.. VM not created");
					}
				}
				getWizard().getContainer().updateButtons();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		vmConfigsList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedVmConfig = vmConfigsList.getSelection()[0];
				System.out.println(selectedVmConfig);
				getWizard().getContainer().updateButtons();
			}
		});

		removeConfig.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				vmConfigsList.remove(selectedVmConfig);
				System.out.println("here " + selectedVmConfig);
				PrivateConfiguration.getCurrent().removeVmConfig(selectedVmConfig);
				selectedVmConfig = "";
			}
		});

		saveConfig.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (memForNode != -1 && cpuForNode != -1 && numNodes != -1) {
					saveFile();
				} else {
					JOptionPane.showMessageDialog(null, "Can not save the configuration check parameters", "Info",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		loadConfig.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				loadFile();
				getWizard().getContainer().updateButtons();
			}
		});

		setControl(container);
		setPageComplete(false);
	}

	public String getVmList() {
		return selectedVmConfig;
	}

	@Override
	public boolean canFlipToNextPage() {
		if (this.memForNode != -1 && this.cpuForNode != -1 && this.numNodes != -1
				&& this.vmConfigsList.getItemCount() != 0) {
			return true;
		}
		return false;
	}

	private void saveFile() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("PrivateConfiguration");
			doc.appendChild(rootElement);

			// cloud elements
			Element cloud = doc.createElement("CloudParameters");
			rootElement.appendChild(cloud);

			// firstname elements
			Element mPar = doc.createElement("m");
			mPar.appendChild(doc.createTextNode(String.valueOf(memForNode)));
			cloud.appendChild(mPar);

			Element vPar = doc.createElement("v");
			vPar.appendChild(doc.createTextNode(String.valueOf(cpuForNode)));
			cloud.appendChild(vPar);

			Element nPar = doc.createElement("n");
			nPar.appendChild(doc.createTextNode(String.valueOf(numNodes)));
			cloud.appendChild(nPar);

//			Element ePar = doc.createElement("e");
//			ePar.appendChild(doc.createTextNode(String.valueOf(costNode)));
//			cloud.appendChild(ePar);

			Element vmConf = doc.createElement("VMConfigurations");
			rootElement.appendChild(vmConf);

			for (VmClass v : PrivateConfiguration.getCurrent().getVmList()) {
				Element vm = doc.createElement("VM");
				vmConf.appendChild(vm);

				Element name = doc.createElement("name");
				name.appendChild(doc.createTextNode(v.getName()));
				vm.appendChild(name);

				Element core = doc.createElement("core");
				core.appendChild(doc.createTextNode(String.valueOf(v.getCore())));
				vm.appendChild(core);

				Element memory = doc.createElement("memory");
				memory.appendChild(doc.createTextNode(String.valueOf(v.getMemory())));
				vm.appendChild(memory);

				Element cost = doc.createElement("cost");
				cost.appendChild(doc.createTextNode(String.valueOf(v.getCost())));
				vm.appendChild(cost);
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(Preferences.getSavingDir() + "VMConfig.xml"));

			transformer.transform(source, result);

			System.out.println("File saved!");
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	private void loadFile() {
		// TODO this should be vmconfigs.json?
		if (!new File(Preferences.getSavingDir() + "VMConfig.xml").isFile()) {
			JOptionPane.showMessageDialog(null, "No configurations have been saved", "Info",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			try {
				File inputFile = new File(Preferences.getSavingDir() + "VMConfig.xml");
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

				Document doc = dBuilder.parse(inputFile);
				doc.getDocumentElement().normalize();

				NodeList nList = doc.getElementsByTagName("CloudParameters");
				Node param = nList.item(0);
				Element par = (Element) param;
				Node m = par.getElementsByTagName("m").item(0);
				PrivateConfiguration.getCurrent().setPriM(Double.parseDouble(m.getTextContent()));
				this.memForNode = Double.parseDouble(m.getTextContent());
				this.memNodeText.setText(m.getTextContent());

				//Node v = par.getElementsByTagName("e").item(0);
				//PrivateConfiguration.getCurrent().setPriM(Double.parseDouble(v.getTextContent()));
				//this.costNode = Double.parseDouble(v.getTextContent());
				//this.costNodeText.setText(v.getTextContent());

				Node n = par.getElementsByTagName("n").item(0);
				PrivateConfiguration.getCurrent().setPriM(Double.parseDouble(n.getTextContent()));
				this.numNodes = Integer.parseInt(n.getTextContent());
				this.nNodesText.setText(n.getTextContent());

				Node e = par.getElementsByTagName("v").item(0);
				PrivateConfiguration.getCurrent().setPriM(Double.parseDouble(e.getTextContent()));
				this.cpuForNode = Double.parseDouble(e.getTextContent());
				this.cpuText.setText(e.getTextContent());

				NodeList vmList = doc.getElementsByTagName("VM");
				for (int temp = 0; temp < vmList.getLength(); temp++) {
					Node nNode = vmList.item(temp);

					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						String name = eElement.getElementsByTagName("name").item(0).getTextContent();
						double core = Double
								.parseDouble(eElement.getElementsByTagName("core").item(0).getTextContent());
						double cost = Double
								.parseDouble(eElement.getElementsByTagName("cost").item(0).getTextContent());
						double memory = Double
								.parseDouble(eElement.getElementsByTagName("memory").item(0).getTextContent());

						PrivateConfiguration.getCurrent().addVmConfig(new VmClass(name, core, memory, cost));
						this.vmConfigsList.add(name);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public int getNumNodes() {
		return this.numNodes;
	}

	public double getMemForNode() {
		return this.memForNode;
	}

	public double getCpuNode() {
		return this.cpuForNode;
	}
}
