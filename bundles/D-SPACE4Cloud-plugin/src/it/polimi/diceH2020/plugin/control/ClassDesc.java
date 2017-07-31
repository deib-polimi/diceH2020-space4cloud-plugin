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

package it.polimi.diceH2020.plugin.control;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class descriptor. Holds all information about a class (i.e. parameters,
 * alternatives).
 * 
 * @author kom
 *
 */
public class ClassDesc implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int id;
	private Map<String, String> altDtsm;
	private String ddsmPath;

	// Storm only parameter
	private double stormU;

	// Hadoop-only parameters
	private Map<String, Map<String, String>> altDtsmHadoop; // Parameters from DTSM files
	private Map<String, String> hadoopParUD; // User defined parameters
	private String mlPath;

	public ClassDesc(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public Map<String, String> getAltDtsm() {
		return altDtsm;
	}

	public void setAltDtsm(HashMap<String, String> altDtsm) {
		this.altDtsm = new HashMap<String, String>(altDtsm);
	}

	public void setDdsmPath(String ddsmPath) {
		this.ddsmPath = ddsmPath;
	}

	public String getDdsmPath() {
		return ddsmPath;
	}

	public Map<String, Map<String, String>> getAltDtsmHadoop() {
		if (!Configuration.getCurrent().getTechnology().equals("Hadoop Map-reduce")
				&& !Configuration.getCurrent().getTechnology().equals("Spark")) {
			return null;
		}
		return altDtsmHadoop;
	}

	/**
	 * Expands alternatives with given Hadoop parameters
	 * 
	 * @param alt
	 *            Alternative to expand with Hadoop params
	 * @param exp
	 *            Map of Hadoop parameters
	 */
	public void expandAltDtsmHadoop(String alt, Map<String, String> exp) {
		if (altDtsmHadoop == null) {
			altDtsmHadoop = new HashMap<String, Map<String, String>>();
		}
		exp.put("file", altDtsm.get(alt));
		altDtsmHadoop.put(alt, exp);
	}

	public Map<String, String> getHadoopParUD() {
		if (Configuration.getCurrent().getTechnology().equals("Hadoop Map-reduce")
				|| Configuration.getCurrent().getTechnology().equals("Spark")) {
			return hadoopParUD;
		}
		return null;
	}

	public void setHadoopParUD(Map<String, String> hadoopParUD) {
		this.hadoopParUD = hadoopParUD;
	}

	public double getStormU() {
		return stormU;
	}

	public void setStormU(double stormU) {
		this.stormU = stormU;
	}

	public String getMlPath() {
		return mlPath;
	}

	public void setMlPath(String mlPath) {
		this.mlPath = mlPath;
	}
}
