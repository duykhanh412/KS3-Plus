package simulation;

import java.io.File;

import io.ExcelWriter;

import javax.swing.JFrame;

import tools.StdOut;
import visual.Visualiser;
import elements.DataGraph;

public class SimulationProcess {
	public static void main(String[] args) {
		SimulationProcess sp = new SimulationProcess();
		// sp.run();
		sp.runPW();
	}

	public void run() {
		SetResultList srlOld = new SetResultList();

		SetResultList srlNew = new SetResultList();

		int setCounter = 0;
		for (int N = Config.MIN_NODES; N <= Config.MAX_NODES; N = N + 2000) {
			for (double EC = Config.MIN_EDGE_COEFFICIENT; EC <= Config.MAX_EDGE_COEFFICIENT; EC = EC + 0.5) {
				for (double PC = Config.MIN_P_COEFFICIENT; PC <= Config.MAX_P_COEFFICIENT; PC = PC + 0.5) {
					for (int K = Config.MIN_KEYWORDS; K <= Config.MAX_KEYWORDS; ++K) {
						for (int KD = Config.MIN_KEYWORD_DISTANCE; KD <= Config.MAX_KEYWORD_DISTANCE; ++KD) {
							for (int Q = Config.MIN_NUM_QOS; Q <= Config.MAX_NUM_QOS; ++Q) {
								for (int D = Config.MIN_DIFF_COEFF; D <= Config.MAX_DIFF_COEFF; D = D + 10) {
									// SetResult setResult = new SetResult(N,
									// EC, PC, K, KD, Q, D);
									SetResult setOldResult = new SetResult(N, EC, PC, K, KD, Q, D);
									SetResult setNewResult = new SetResult(N, EC, PC, K, KD, Q, D);
									++setCounter;
									for (int R = 0; R < Config.NUM_RUNS; ++R) {
										StdOut.println("********************************* RUNNING SET " + setCounter
												+ " RUN #" + R + " *********************************");
										Instance instance = new Instance(N, EC, PC, K, KD, "normal", Q, D);

										// Previous Algorithm Result
										instance.runOldAlgorithm();
										setOldResult.add(instance.oldResult);

										// New Algorithm Result
										instance.runNewAlgorithm();
										setNewResult.add(instance.newResult);
									}
									// setResult.averageResultsAll();
									// srl.add(setResult);

									// Previous Algorithm Result
									setOldResult.averageResultsAll();
									srlOld.add(setOldResult);

									// New Algorithm Result
									setNewResult.averageResultsAll();
									srlNew.add(setNewResult);
								}
							}
						}
						String oldFilePath = ExcelWriter.composeExcelFilePath("Old");
						// Previous Algorithm Result
						StringBuilder oldFilePathStringBuilder = new StringBuilder();
						oldFilePathStringBuilder.append(oldFilePath.substring(0, 20));
						oldFilePathStringBuilder.append("Old_Results\\");
						oldFilePathStringBuilder.append(oldFilePath.substring(20));
						ExcelWriter ewOld = new ExcelWriter(oldFilePathStringBuilder.toString());
						ewOld.write(srlOld);

						// New Algorithm Result
						String newFilePath = ExcelWriter.composeExcelFilePath("Old");
						StringBuilder newFilePathStringBuilder = new StringBuilder();
						newFilePathStringBuilder.append(newFilePath.substring(0, 20));
						newFilePathStringBuilder.append("New_Results\\");
						newFilePathStringBuilder.append(newFilePath.substring(20));
						ExcelWriter ewNew = new ExcelWriter(newFilePathStringBuilder.toString());
						ewNew.write(srlNew);
					}
				}
			}
		}
		// String filePath = ExcelWriter.composeExcelFilePath();
		// ExcelWriter ew = new ExcelWriter(filePath);
		// ew.write(srl);
	}

	public void runPW() { // run experiments on programmableweb dataset
		StdOut.println(
				"************************************RUNNING ON PROGRAMMABLEWEB DATASET************************************");

		SetResultList srlOld = new SetResultList();
		SetResult setOldResult = new SetResult(0, 0, 0, 0, 0, 0, 0);

		SetResultList srlNew = new SetResultList();
		SetResult setNewResult = new SetResult(0, 0, 0, 0, 0, 0, 0);

		InstancePW instancePW;
		for (int M = 6001; M <= 6294; ++M) {
			StdOut.println("********************************* Verifying " + "Mashup #" + M
					+ " *********************************");
			Setting setting = new Setting(0, 0, 0, 0, 0, "normal", 0, 0);
			DataGraph dg = new DataGraph(new File("d:\\mashup_api_related.sql"), setting);
			instancePW = new InstancePW(dg, M, 0, 0, 0, 0, 0, "normal", 0, 0);
			// instancePW.run();
			// setResult.add(instancePW.result);
			// New Algorithm
			instancePW.runNewAlgorithm(M);
			setNewResult.add(instancePW.newResult);

			// Previous Algorithm
			// instancePW.runOldAlgorithm(M);
			// setOldResult.add(instancePW.oldResult);

			// Check Original SBS
			// instancePW.checkOriginalSBS(M);
			// setNewResult.add(instancePW.newResult);
		}
		// Save the results for the previous algorithm
		// setOldResult.averageResultsAll();
		// srlOld.add(setOldResult);
		// writeOldResultsPW(srlOld);

		// Save the results for the new algorithm
		setNewResult.averageResultsAll();
		srlNew.add(setNewResult);
		writeNewResultsPW(srlNew);
		// displayGraph(dg);
	}

	public static void writeResults(SetResultList srl) {
		String filePath = ExcelWriter.composeExcelFilePath("");
		ExcelWriter ew = new ExcelWriter(filePath);
		ew.write(srl);
	}

	public static void writeOldResultsPW(SetResultList srl) { // to be modified
																// to pw version
		String filePath = ExcelWriter.composeExcelFilePathPW();
		StringBuilder oldFilePath = new StringBuilder();
		oldFilePath.append(filePath.substring(0, 19));
		oldFilePath.append("Old_Results\\");
		oldFilePath.append(filePath.substring(19));
		ExcelWriter ew = new ExcelWriter(oldFilePath.toString());
		ew.writePW(srl);
	}

	public static void writeNewResultsPW(SetResultList srl) { // to be modified
																// to pw version
		String filePath = ExcelWriter.composeExcelFilePathPW();
		StringBuilder newFilePath = new StringBuilder();
		newFilePath.append(filePath.substring(0, 19));
		newFilePath.append("New_Results\\");
		newFilePath.append(filePath.substring(19));
		ExcelWriter ew = new ExcelWriter(newFilePath.toString());
		ew.writePW(srl);
	}

	public static void displayGraph(DataGraph dg) {
		Visualiser applet = new Visualiser();
		applet.initDataGraph(dg);
		applet.init();

		JFrame frame = new JFrame();
		frame.getContentPane().add(applet);
		frame.setTitle("JGraphT Adapter to JGraph Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
