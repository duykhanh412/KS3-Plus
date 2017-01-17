package io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import simulation.Config;
import simulation.InstanceResult;
import simulation.SetResult;
import simulation.SetResultList;
import tools.StdOut;

public class ExcelWriter {
	String filePath;
	FileOutputStream fos;

	public ExcelWriter(String filePath) {
		this.filePath = filePath;
		File file = new File(this.filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("File creation failed.");
				e.printStackTrace();
			}
		}
	}

	public boolean write(SetResultList srl) {
		Workbook workbook = new HSSFWorkbook();
		// write individual instance result to excel file, one set for one sheet
		for (int j = 0; j < srl.size(); ++j) {
			SetResult sr = srl.get(j);
			String sheetName = "N" + sr.numNodes + "-EC" + sr.edgeCoefficient + "-PC" + sr.pCoefficient + "-K"
					+ sr.numKeywords + "-KD" + sr.keywordDistance + "-Q" + sr.numQoS + "-D" + sr.qdCoefficient; // generate
																												// name
																												// of
																												// sheet
			Sheet sheet = workbook.createSheet(sheetName); // create a sheet

			int currentRowIndex = 0;

			Row row = sheet.createRow(currentRowIndex++);
			row.createCell(1).setCellValue("NORMAL");
			row.createCell(4).setCellValue("CONSTRAINT");
			row.createCell(7).setCellValue("OPTIMAL");
			row.createCell(10).setCellValue("IndividaulNormal");
			row.createCell(11).setCellValue("IndividaulConstraint");
			row.createCell(12).setCellValue("IndividaulOptimal");

			row = sheet.createRow(currentRowIndex++);
			row.createCell(0).setCellValue("Instance #");
			row.createCell(1).setCellValue("Successful");
			row.createCell(2).setCellValue("Time Consumption");
			row.createCell(3).setCellValue("Objective Value");
			row.createCell(4).setCellValue("Successful");
			row.createCell(5).setCellValue("Time Consumption");
			row.createCell(6).setCellValue("Objective Value");
			row.createCell(7).setCellValue("Successful");
			row.createCell(8).setCellValue("Time Consumption");
			row.createCell(9).setCellValue("Objective Value");
			row.createCell(10).setCellValue("Successful");
			row.createCell(11).setCellValue("Successful");
			row.createCell(12).setCellValue("Successful");

			for (int i = 0; i < sr.instancesResults.size(); ++i) {
				row = sheet.createRow(currentRowIndex++);
				row.createCell(0).setCellValue(i); // enter instance #

				// normal
				row.createCell(1).setCellValue(sr.instancesResults.get(i).isSuccessfulKS3Normal);
				if (sr.instancesResults.get(i).isSuccessfulKS3Normal) {
					row.createCell(2).setCellValue(sr.instancesResults.get(i).timeConsumptionSuccessfulKS3Normal);
				} else {
					row.createCell(2).setCellValue(sr.instancesResults.get(i).timeConsumptionFailedKS3Normal);
				}
				row.createCell(3).setCellValue(sr.instancesResults.get(i).objectiveValueKS3Normal);

				// constraint
				row.createCell(4).setCellValue(sr.instancesResults.get(i).isSuccessfulKS3Constraint);
				if (sr.instancesResults.get(i).isSuccessfulKS3Constraint) {
					row.createCell(5).setCellValue(sr.instancesResults.get(i).timeConsumptionSuccessfulKS3Constraint);
				} else {
					row.createCell(5).setCellValue(sr.instancesResults.get(i).timeConsumptionFailedKS3Constraint);
				}
				row.createCell(6).setCellValue(sr.instancesResults.get(i).objectiveValueKS3Constraint);

				// optimal
				row.createCell(7).setCellValue(sr.instancesResults.get(i).isSuccessfulKS3Optimal);
				if (sr.instancesResults.get(i).isSuccessfulKS3Optimal) {
					row.createCell(8).setCellValue(sr.instancesResults.get(i).timeConsumptionSuccessfulKS3Optimal);
				} else {
					row.createCell(8).setCellValue(sr.instancesResults.get(i).timeConsumptionFailedKS3Optimal);
				}
				row.createCell(9).setCellValue(sr.instancesResults.get(i).objectiveValueKS3Optimal);

				// individualNormal
				row.createCell(10).setCellValue(sr.instancesResults.get(i).isSuccessfulIndividualNormal);

				// individualConstraint
				row.createCell(11).setCellValue(sr.instancesResults.get(i).isSuccessfulIndividualConstraint);

				// individualOptimal
				row.createCell(12).setCellValue(sr.instancesResults.get(i).isSuccessfulIndividualOptimal);
			}

			row = sheet.createRow(currentRowIndex++);
			// normal
			row.createCell(0).setCellValue("TOTAL/AVERAGE");
			row.createCell(1).setCellValue(sr.numberSuccessfulRunsKS3Normal + "/" + sr.instancesResults.size());
			row.createCell(2).setCellValue(sr.averageTimeConsumptionSuccessfulRunsKS3Normal);
			row.createCell(3).setCellValue(sr.averageObjectiveValueKS3Normal);
			// constraint
			row.createCell(4).setCellValue(sr.numberSuccessfulRunsKS3Constraint + "/" + sr.instancesResults.size());
			row.createCell(5).setCellValue(sr.averageTimeConsumptionSuccessfulRunsKS3Constraint);
			row.createCell(6).setCellValue(sr.averageObjectiveValueKS3Constraint);
			// optimal
			row.createCell(7).setCellValue(sr.numberSuccessfulRunsKS3Optimal + "/" + sr.instancesResults.size());
			row.createCell(8).setCellValue(sr.averageTimeConsumptionSuccessfulRunsKS3Optimal);
			row.createCell(9).setCellValue(sr.averageObjectiveValueKS3Optimal);

			// individualNormal
			row.createCell(10).setCellValue(sr.numberSuccessfulRunsIndividualNormal + "/" + sr.instancesResults.size());

			// individualConstraint
			row.createCell(11)
					.setCellValue(sr.numberSuccessfulRunsIndividualConstraint + "/" + sr.instancesResults.size());

			// individualOptimal
			row.createCell(12)
					.setCellValue(sr.numberSuccessfulRunsIndividualOptimal + "/" + sr.instancesResults.size());
		}

		// write the average success rate of each set to the
		// "AverageSuccessRate" sheet
		Sheet sheet = workbook.createSheet("AverageSuccessRate");
		int currentRowIndex = 0;

		Row row = sheet.createRow(currentRowIndex++);

		ArrayList<String> cFactors = SetResultList.getChangingFactors();
		String cFactor = "";
		if (cFactors.size() == 0) {
			System.out.println("WARNING! NO FACTOR IS CHANGING!");
			// System.exit(-1);
		} else if (cFactors.size() == 1) {
			cFactor = cFactors.get(0);
			if (cFactor == "N") {
				cFactor = "Number of Nodes";
			} else if (cFactor == "EC") {
				cFactor = "Edge Coefficient";
			} else if (cFactor == "P") {
				cFactor = "P Coefficient";
			} else if (cFactor == "K") {
				cFactor = "Number of Keywords";
			} else if (cFactor == "KD") {
				cFactor = "Keyword Distance";
			} else if (cFactor == "D") {
				cFactor = "Quality Constraint Difficulty";
			}
		} else {
			for (int i = 0; i < cFactors.size(); ++i) {
				cFactor = cFactor + cFactors.get(i);
				if (i != (cFactors.size() - 1)) {
					cFactor += "/";
				}
			}
		}

		row.createCell(0).setCellValue(cFactor);

		row.createCell(1).setCellValue("Normal");
		row.createCell(2).setCellValue("Constraint");
		row.createCell(3).setCellValue("Optimal");
		row.createCell(4).setCellValue("IndividualNormal");
		row.createCell(5).setCellValue("IndividualConstraint");
		row.createCell(6).setCellValue("IndividualOptimal");

		for (int i = 0; i < srl.size(); ++i) {
			row = sheet.createRow(currentRowIndex++);
			SetResult sr = srl.get(i);

			if (cFactors.size() == 1) {
				cFactor = cFactors.get(0);
				if (cFactor == "N") {
					row.createCell(0).setCellValue(Config.MIN_NODES + i * 1000);
				} else if (cFactor == "EC") {
					row.createCell(0).setCellValue(Config.MIN_EDGE_COEFFICIENT + i * 0.5);
				} else if (cFactor == "PC") {
					row.createCell(0).setCellValue(Config.MIN_P_COEFFICIENT + i * 0.5);
				} else if (cFactor == "K") {
					row.createCell(0).setCellValue(Config.MIN_KEYWORDS + i * 1);
				} else if (cFactor == "KD") {
					row.createCell(0).setCellValue(Config.MIN_KEYWORD_DISTANCE + i * 1);
				} else if (cFactor == "QD") {
					row.createCell(0).setCellValue(Config.MIN_DIFF_COEFF + i * 10);
				}
			} else {
				String value = "";
				for (int j = 0; j < cFactors.size(); ++j) {
					if (cFactors.get(j) == "N") {
						value += Config.MIN_NODES + i * 100;
					} else if (cFactors.get(j) == "EC") {
						value += Config.MIN_EDGE_COEFFICIENT + i * 0.5;
					} else if (cFactors.get(j) == "PC") {
						value += Config.MIN_P_COEFFICIENT + i * 0.5;
					} else if (cFactors.get(j) == "K") {
						value += Config.MIN_KEYWORDS + i * 1;
					} else if (cFactors.get(j) == "KD") {
						value += Config.MIN_KEYWORD_DISTANCE + i * 1;
					} else if (cFactors.get(j) == "QD") {
						value += Config.MIN_DIFF_COEFF + i * 10;
					}
					if (j != (cFactors.size() - 1)) {
						value += "/";
					}
				}
				row.createCell(0).setCellValue(value);
			}

			row.createCell(1).setCellValue(sr.successRateKS3Normal);
			row.createCell(2).setCellValue(sr.successRateKS3Constraint);
			row.createCell(3).setCellValue(sr.successRateKS3Optimal);
			row.createCell(4).setCellValue(sr.successRateIndividualNormal);
			row.createCell(5).setCellValue(sr.successRateIndividualConstraint);
			row.createCell(6).setCellValue(sr.successRateIndividualOptimal);
		}

		// write the average successful time of each set to the
		// "AverageTimeConsumption" sheet
		sheet = workbook.createSheet("AverageTimeConsumption");
		currentRowIndex = 0;

		row = sheet.createRow(currentRowIndex++);
		row.createCell(0).setCellValue(cFactor);
		row.createCell(1).setCellValue("Normal");
		row.createCell(2).setCellValue("Constraint");
		row.createCell(3).setCellValue("Optimal");

		for (int i = 0; i < srl.size(); ++i) {
			row = sheet.createRow(currentRowIndex++);
			SetResult sr = srl.get(i);

			if (cFactors.size() == 1) {
				cFactor = cFactors.get(0);
				if (cFactor == "N") {
					row.createCell(0).setCellValue(Config.MIN_NODES + i * 1000);
				} else if (cFactor == "EC") {
					row.createCell(0).setCellValue(Config.MIN_EDGE_COEFFICIENT + i * 0.5);
				} else if (cFactor == "PC") {
					row.createCell(0).setCellValue(Config.MIN_P_COEFFICIENT + i * 0.5);
				} else if (cFactor == "K") {
					row.createCell(0).setCellValue(Config.MIN_KEYWORDS + i * 1);
				} else if (cFactor == "KD") {
					row.createCell(0).setCellValue(Config.MIN_KEYWORD_DISTANCE + i * 1);
				} else if (cFactor == "QD") {
					row.createCell(0).setCellValue(Config.MIN_DIFF_COEFF + i * 10);
				}
			} else {
				String value = "";
				for (int j = 0; j < cFactors.size(); ++j) {
					if (cFactors.get(j) == "N") {
						value += Config.MIN_NODES + i * 100;
					} else if (cFactors.get(j) == "EC") {
						value += Config.MIN_EDGE_COEFFICIENT + i * 0.5;
					} else if (cFactors.get(j) == "PC") {
						value += Config.MIN_P_COEFFICIENT + i * 0.5;
					} else if (cFactors.get(j) == "K") {
						value += Config.MIN_KEYWORDS + i * 1;
					} else if (cFactors.get(j) == "KD") {
						value += Config.MIN_KEYWORD_DISTANCE + i * 1;
					} else if (cFactors.get(j) == "QD") {
						value += Config.MIN_DIFF_COEFF + i * 10;
					}
					if (j != (cFactors.size() - 1)) {
						value += "/";
					}
				}
				row.createCell(0).setCellValue(value);
			}

			row.createCell(1).setCellValue(sr.averageTimeConsumptionSuccessfulRunsKS3Normal);
			row.createCell(2).setCellValue(sr.averageTimeConsumptionSuccessfulRunsKS3Constraint);
			row.createCell(3).setCellValue(sr.averageTimeConsumptionSuccessfulRunsKS3Optimal);
		}

		// write the average system cost of each set to the
		// "AverageObjectiveValue" sheet
		sheet = workbook.createSheet("AverageObjectiveValue");
		currentRowIndex = 0;

		row = sheet.createRow(currentRowIndex++);
		row.createCell(0).setCellValue(cFactor);
		row.createCell(1).setCellValue("Normal");
		row.createCell(2).setCellValue("Constraint");
		row.createCell(3).setCellValue("Optimal");

		for (int i = 0; i < srl.size(); ++i) {
			row = sheet.createRow(currentRowIndex++);
			SetResult sr = srl.get(i);

			if (cFactors.size() == 1) {
				cFactor = cFactors.get(0);
				if (cFactor == "N") {
					row.createCell(0).setCellValue(Config.MIN_NODES + i * 1000);
				} else if (cFactor == "EC") {
					row.createCell(0).setCellValue(Config.MIN_EDGE_COEFFICIENT + i * 0.5);
				} else if (cFactor == "PC") {
					row.createCell(0).setCellValue(Config.MIN_P_COEFFICIENT + i * 0.5);
				} else if (cFactor == "K") {
					row.createCell(0).setCellValue(Config.MIN_KEYWORDS + i * 1);
				} else if (cFactor == "KD") {
					row.createCell(0).setCellValue(Config.MIN_KEYWORD_DISTANCE + i * 1);
				} else if (cFactor == "QD") {
					row.createCell(0).setCellValue(Config.MIN_DIFF_COEFF + i * 10);
				}
			} else {
				String value = "";
				for (int j = 0; j < cFactors.size(); ++j) {
					if (cFactors.get(j) == "N") {
						value += Config.MIN_NODES + i * 100;
					} else if (cFactors.get(j) == "EC") {
						value += Config.MIN_EDGE_COEFFICIENT + i * 0.5;
					} else if (cFactors.get(j) == "PC") {
						value += Config.MIN_P_COEFFICIENT + i * 0.5;
					} else if (cFactors.get(j) == "K") {
						value += Config.MIN_KEYWORDS + i * 1;
					} else if (cFactors.get(j) == "KD") {
						value += Config.MIN_KEYWORD_DISTANCE + i * 1;
					} else if (cFactors.get(j) == "QD") {
						value += Config.MIN_DIFF_COEFF + i * 10;
					}
					if (j != (cFactors.size() - 1)) {
						value += "/";
					}
				}
				row.createCell(0).setCellValue(value);
			}

			row.createCell(1).setCellValue(sr.averageObjectiveValueKS3Normal);
			row.createCell(2).setCellValue(sr.averageObjectiveValueKS3Constraint);
			row.createCell(3).setCellValue(sr.averageObjectiveValueKS3Optimal);
		}

		try {
			FileOutputStream fos = new FileOutputStream(this.filePath);
			workbook.write(fos);
			fos.close();
			workbook.close();
			System.out.println(this.filePath + " is successfully written.");

			StdOut.println("Average time consumption for KS3Normal: "
					+ srl.get(0).averageTimeConsumptionSuccessfulRunsKS3Normal);
			StdOut.println("Average time consumption for KS3Constraint: "
					+ srl.get(0).averageTimeConsumptionSuccessfulRunsKS3Constraint);
			StdOut.println("Average time consumption for KS3Optimal: "
					+ srl.get(0).averageTimeConsumptionSuccessfulRunsKS3Optimal);

			StdOut.println("Average success rate for KS3Normal: " + srl.get(0).successRateKS3Normal);
			StdOut.println("Average success rate for KS3Constraint: " + srl.get(0).successRateKS3Constraint);
			StdOut.println("Average success rate for KS3Optimal: " + srl.get(0).successRateKS3Optimal);
			StdOut.println("Average success rate for IndividualNormal: " + srl.get(0).successRateIndividualNormal);
			StdOut.println(
					"Average success rate for IndividualConstraint: " + srl.get(0).successRateIndividualConstraint);
			StdOut.println("Average success rate for IndividualOptimal: " + srl.get(0).successRateIndividualOptimal);

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean writePW(SetResultList srl) {
		StdOut.println("Now writing the results of " + srl.get(0).instancesResults.size() + " instances!");

		Workbook workbook = new HSSFWorkbook();

		// categorize instances according to the number of keywords in
		// corresponding mashups
		HashMap<Integer, ArrayList<InstanceResult>> categorizedSetResultList = new HashMap<Integer, ArrayList<InstanceResult>>();
		ArrayList<InstanceResult> instancesResults = srl.get(0).instancesResults;

		HashMap<Integer, Double> categorizedTimeConsumption = new HashMap<Integer, Double>();

		InstanceResult instanceResult;
		for (int i = 0; i < instancesResults.size(); ++i) {
			instanceResult = instancesResults.get(i);
			StdOut.println("Now processing mashup #" + instanceResult.mashupID);
			if (categorizedSetResultList.containsKey(instanceResult.numKeywordsInMashup) == false) {
				ArrayList<InstanceResult> irs = new ArrayList<InstanceResult>();
				irs.add(instanceResult);
				categorizedSetResultList.put(instanceResult.numKeywordsInMashup, irs);
				StdOut.println("Category " + instanceResult.numKeywordsInMashup + " created!");

				Double time = new Double(instanceResult.timeConsumptionSuccessfulKS3Normal);
				categorizedTimeConsumption.put(instanceResult.numKeywordsInMashup, time);

			} else {
				categorizedSetResultList.get(instanceResult.numKeywordsInMashup).add(instanceResult);
				Double time = new Double(instanceResult.timeConsumptionSuccessfulKS3Normal);
				categorizedTimeConsumption.put(instanceResult.numKeywordsInMashup,
						categorizedTimeConsumption.get(instanceResult.numKeywordsInMashup) + time);
			}
		}
		StdOut.println("There are a total of " + categorizedSetResultList.size() + " categories!");
		// output categorizedSetResultList
		/*
		 * Set<HashMap.Entry<Integer, ArrayList<InstanceResult>>> entrySet =
		 * categorizedSetResultList.entrySet(); for (Entry<Integer,
		 * ArrayList<InstanceResult>> entry : entrySet) {
		 * System.out.print("Mashups containing " + entry.getKey() +
		 * " keywords: "); for(int i=0; i<entry.getValue().size(); ++i) {
		 * System.out.print(entry.getValue().get(i).mashupID + ", "); }
		 * System.out.println(""); }
		 */

		// write individual instance result to excel file, one set for one sheet
		Set<HashMap.Entry<Integer, ArrayList<InstanceResult>>> entrySet = categorizedSetResultList.entrySet();
		for (Entry<Integer, ArrayList<InstanceResult>> entry : entrySet) {
			int numKeywordsInMashup = entry.getKey();
			ArrayList<InstanceResult> isrs = entry.getValue();

			String sheetName = numKeywordsInMashup + " kws"; // generate name of
																// sheet
			Sheet sheet = workbook.createSheet(sheetName); // create a sheet

			int currentRowIndex = 0;

			Row row = sheet.createRow(currentRowIndex++);

			row.createCell(0).setCellValue("Instance #");
			row.createCell(1).setCellValue("Successful");
			row.createCell(2).setCellValue("Time Consumption");
			row.createCell(3).setCellValue("New Solution");

			int numNewSolutions = 0;
			for (int i = 0; i < isrs.size(); ++i) {
				row = sheet.createRow(currentRowIndex++);
				row.createCell(0).setCellValue(i); // enter instance #

				// normal //constraint and optimal results have been deleted
				row.createCell(1).setCellValue(isrs.get(i).isSuccessfulKS3Normal);
				if (isrs.get(i).isSuccessfulKS3Normal) {
					row.createCell(2).setCellValue(isrs.get(i).timeConsumptionSuccessfulKS3Normal);
				} else {
					row.createCell(2).setCellValue(isrs.get(i).timeConsumptionFailedKS3Normal);
				}

				if (isrs.get(i).newSolution) {
					row.createCell(3).setCellValue("1");
					++numNewSolutions;
				} else {
					row.createCell(3).setCellValue("0");
				}
			}

			row = sheet.createRow(currentRowIndex++);
			// normal
			row.createCell(0).setCellValue("TOTAL/AVERAGE");
			row.createCell(1).setCellValue(isrs.size() + "/" + isrs.size());
			row.createCell(2).setCellValue(categorizedTimeConsumption.get(numKeywordsInMashup) / isrs.size());
		}

		// write the average success rate of each set to the
		// "AverageSuccessRate" sheet
		Sheet sheet = workbook.createSheet("AverageSuccessRate");
		int currentRowIndex = 0;

		Row row = sheet.createRow(currentRowIndex++);
		row.createCell(1).setCellValue("Normal");

		row = sheet.createRow(currentRowIndex++);
		row.createCell(1).setCellValue(srl.get(0).successRateKS3Normal);

		// write the average successful time of each set to the
		// "AverageTimeConsumption" sheet
		sheet = workbook.createSheet("AverageTimeConsumption");
		currentRowIndex = 0;

		row = sheet.createRow(currentRowIndex++);
		row.createCell(1).setCellValue("Normal");

		row = sheet.createRow(currentRowIndex++);

		row.createCell(1).setCellValue(srl.get(0).averageTimeConsumptionSuccessfulRunsKS3Normal);

		// write the average system cost of each set to the
		// "AverageObjectiveValue" sheet
		sheet = workbook.createSheet("AverageObjectiveValue");
		currentRowIndex = 0;

		row = sheet.createRow(currentRowIndex++);
		row.createCell(1).setCellValue("Normal");

		row = sheet.createRow(currentRowIndex++);
		row.createCell(1).setCellValue(srl.get(0).averageObjectiveValueKS3Normal);

		try {
			FileOutputStream fos = new FileOutputStream(this.filePath);
			workbook.write(fos);
			fos.close();
			workbook.close();
			System.out.println(this.filePath + " is successfully written.");

			StdOut.println("Average time consumption for KS3Normal: "
					+ srl.get(0).averageTimeConsumptionSuccessfulRunsKS3Normal);
			StdOut.println("Average time consumption for KS3Constraint: "
					+ srl.get(0).averageTimeConsumptionSuccessfulRunsKS3Constraint);
			StdOut.println("Average time consumption for KS3Optimal: "
					+ srl.get(0).averageTimeConsumptionSuccessfulRunsKS3Optimal);

			StdOut.println("Average success rate for KS3Normal: " + srl.get(0).successRateKS3Normal);
			StdOut.println("Average success rate for KS3Constraint: " + srl.get(0).successRateKS3Constraint);
			StdOut.println("Average success rate for KS3Optimal: " + srl.get(0).successRateKS3Optimal);
			StdOut.println("Average success rate for IndividualNormal: " + srl.get(0).successRateIndividualNormal);
			StdOut.println(
					"Average success rate for IndividualConstraint: " + srl.get(0).successRateIndividualConstraint);
			StdOut.println("Average success rate for IndividualOptimal: " + srl.get(0).successRateIndividualOptimal);

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String composeExcelFilePath(String resultFolder, String versionOfAlgorithm) {
		String nPart, ecPart, pcPart, kPart, kdPart, qPart, dPart;
		if (Config.MIN_NODES != Config.MAX_NODES) {
			nPart = "NX";
		} else {
			nPart = "N" + Config.MIN_NODES;
		}
		if (Config.MIN_EDGE_COEFFICIENT != Config.MAX_EDGE_COEFFICIENT) {
			ecPart = "ECX";
		} else {
			ecPart = "EC" + Config.MIN_EDGE_COEFFICIENT;
		}
		if (Config.MIN_P_COEFFICIENT != Config.MAX_P_COEFFICIENT) {
			pcPart = "PCX";
		} else {
			pcPart = "PC" + Config.MIN_P_COEFFICIENT;
		}
		if (Config.MIN_KEYWORDS != Config.MAX_KEYWORDS) {
			kPart = "KX";
		} else {
			kPart = "K" + Config.MIN_KEYWORDS;
		}
		if (Config.MIN_KEYWORD_DISTANCE != Config.MAX_KEYWORD_DISTANCE) {
			kdPart = "KDX";
		} else {
			kdPart = "KD" + Config.MIN_KEYWORD_DISTANCE;
		}
		if (Config.MIN_NUM_QOS != Config.MAX_NUM_QOS) {
			qPart = "QX";
		} else {
			qPart = "Q" + Config.MIN_NUM_QOS;
		}
		if (Config.MIN_DIFF_COEFF != Config.MAX_DIFF_COEFF) {
			dPart = "DX";
		} else {
			dPart = "D" + Config.MIN_DIFF_COEFF;
		}
		return "D:\\ICSE2016_Results\\" + resultFolder + "\\" + nPart + "-" + ecPart + "-" + pcPart + "-" + kPart + "-"
				+ kdPart + "-" + qPart + "-" + dPart + "-" + versionOfAlgorithm + ".xls";
	}

	public static String composeExcelFilePathPW(String resultFolder, String versionOfAlgorithm) {
		String kPart, kdPart;
		if (Config.MIN_KEYWORDS != Config.MAX_KEYWORDS) {
			kPart = "KX";
		} else {
			kPart = "K" + Config.MIN_KEYWORDS;
		}
		if (Config.MIN_KEYWORD_DISTANCE != Config.MAX_KEYWORD_DISTANCE) {
			kdPart = "KDX";
		} else {
			kdPart = "KD" + Config.MIN_KEYWORD_DISTANCE;
		}
		return "D:\\TSE2016_Results\\" + resultFolder + "\\" + "PW_" + kPart + "-" + kdPart + "-" + versionOfAlgorithm
				+ ".xls";
	}
}
