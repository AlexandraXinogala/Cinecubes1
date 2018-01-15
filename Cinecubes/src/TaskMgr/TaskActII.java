package TaskMgr;

import java.util.HashSet;
import java.util.TreeSet;

import storymgr.Act;
import storymgr.PptxSlide;
import storymgr.Tabular;
import CubeMgr.CubeBase.CubeBase;
import CubeMgr.CubeBase.CubeQuery;
import CubeMgr.StarSchema.SqlQuery;
import HighlightMgr.HighlightDominationColumn;
import HighlightMgr.HighlightMax;
import HighlightMgr.HighlightMin;
import HighlightMgr.HighlightTable;

public class TaskActII extends Task {

	public TaskActII() {
		super();
	}

	public void generateSubTasks(CubeBase cubeBase) {
		/* highlight for Original */
		generateSubTasks_per_row(cubeBase);
		generateSubTasks_per_col(cubeBase);
	}

	private void generateSubTasks_per_row(CubeBase cubeBase) {
		SqlQuery newSqlQuery = this.cubeQuery.get(1).getSqlQuery();
		HashSet<String> col_per_row = new HashSet<String>();
		for (int i = 0; i < newSqlQuery.getRowPivot().size(); i++) {
			String[][] table = newSqlQuery.getResultArray();
			String Value = newSqlQuery.getRowPivot().toArray()[i]
					.toString();

			col_per_row.clear();
			for (int j = 2; j < table.length; j++) {
				if (table[j][1].equals(Value))
					col_per_row.add(table[j][0]);
			}

			String[] todrillinValues = new String[2];
			todrillinValues[1] = Value;
			todrillinValues[0] = col_per_row.toArray()[0].toString();
			CubeQuery newQuery = new CubeQuery(this.cubeQuery.get(1));
			long strTime = System.nanoTime();
			if (cubeQuery.get(1).doDrillInRowVersion(cubeBase, todrillinValues, col_per_row, newQuery)) {
				addSubTask(newQuery, -4, 0, strTime, cubeBase);
				this.getLastSubTask().addDifferenceFromOrigin(i);
				this.getLastSubTask().addDifferenceFromOrigin(1);
			}

		}

	}

	private void generateSubTasks_per_col(CubeBase cubeBase) {
		SqlQuery newSqlQuery = this.cubeQuery.get(1).getSqlQuery();

		HashSet<String> row_per_col = new HashSet<String>();

		for (int i = 0; i < newSqlQuery.getColPivot().size(); i++) {
			String[][] table = newSqlQuery.getResultArray();
			String Value = newSqlQuery.getColPivot().toArray()[i]
					.toString();
			row_per_col.clear();
			for (int j = 2; j < table.length; j++) {
				if (table[j][0].equals(Value))
					row_per_col.add(table[j][1]);
			}

			String[] todrillinValues = new String[2];
			todrillinValues[1] = Value;
			todrillinValues[0] = row_per_col.toArray()[0].toString();
			CubeQuery newQuery = new CubeQuery(cubeQuery.get(1));
			long strTime = System.nanoTime();
			if (cubeQuery.get(1).doDrillInColVersion(cubeBase, todrillinValues, row_per_col,newQuery)) {
				addSubTask(newQuery, -5, 0, strTime, cubeBase);
				this.getLastSubTask().addDifferenceFromOrigin(i);
				this.getLastSubTask().addDifferenceFromOrigin(1);
			}

		}

	}
	
	@Override
	public void constructActEpidoses(Act currentAct) {
		SubTask origSubtsk = getSubTask(1);
		CubeQuery origCubeQuery = cubeQuery.get(1);
		for (int j = 0; j < getNumSubTasks(); j++) {
			if (j == 1)
				continue;
			SubTask subtsk = getSubTask(j);
			SqlQuery currentSqlQuery = ((SqlQuery) subtsk.getExtractionMethod());
			CubeQuery currentCubeQuery = cubeQuery.get(j);
			PptxSlide newSlide = new PptxSlide();			//go to eppisode
			newSlide.addCubeQuery(currentCubeQuery);		//
			Tabular tbl = new Tabular();					//
			newSlide.setVisual(tbl);						//

			if ((currentSqlQuery.getResultArray() != null)) {
			
				String[] extraPivot = createExtraPivot(subtsk, origSubtsk, origCubeQuery);
				/* ====== Compute Pivot Table ======= */
			
				newSlide.setTimeCreationTabular(System.nanoTime());
				tbl.CreatePivotTable(
						subtsk.getExtractionMethod().getRowPivot(),
						subtsk.getExtractionMethod().getColPivot(),
						subtsk.getExtractionMethod().getResultArray(),
						extraPivot);
				newSlide.subTimeCreationTabular(System.nanoTime());

				if (subtsk.getHighlight() == null)
					subtsk.setHighlight(new HighlightTable());

				if (subtsk.getDifferencesFromOrigin().size() > 0
						&& (subtsk.getDifferencesFromOrigin().get(0) == -4 || subtsk
								.getDifferencesFromOrigin().get(0) == -5)
						&& subtsk.getDifferencesFromOrigin().get(1) > 0) {

					/* ====== Combine Subtask and Pivot Table ======= */
					PptxSlide tmpSlide = (PptxSlide) currentAct
							.getEpisode(currentAct.getNumEpisodes() - 1);
					long strTimecombine = System.nanoTime();
					String[][] SlideTable = tmpSlide.getVisual()
							.getPivotTable();
					String[][] currentTable = tbl.getPivotTable();
					String[][] newTable = customCopyArray(SlideTable, currentTable);
					tmpSlide.getVisual().setPivotTable(newTable);
					tmpSlide.addTimeCombineSlide(System.nanoTime()- strTimecombine);
					tmpSlide.addTimeCreationTabular(newSlide.getTimeCreationTabular());
					tmpSlide.addTimeComputeHighlights(newSlide.getTimeComputeHighlights());
					tmpSlide.addSubTask(subtsk);
					tmpSlide.addCubeQuery(currentCubeQuery);

				} else {
					newSlide.addSubTask(subtsk);
					currentAct.addEpisode(newSlide);
				}
			} else if (currentSqlQuery.getTitleosColumns() != null
					&& currentSqlQuery.getTitleosColumns().contains("Act")) {
				newSlide.setTimeCreationText(0);
				newSlide.setTimeCreationText(System.nanoTime());
				newSlide.setTitle(currentSqlQuery.getTitleosColumns());
				newSlide.setTimeCreationText( System.nanoTime());

				newSlide.addSubTask(subtsk);
				currentAct.addEpisode(newSlide);
			}
		}

		for (int i = 1; i < currentAct.getNumEpisodes(); i++) {
			PptxSlide currentSlide = (PptxSlide) currentAct.getEpisode(i);
			Tabular tbl = (Tabular) currentSlide.getVisual();
			currentSlide.getHighlight().clear();
			if (currentSlide.getSubTasks().get(0).getDifferencesFromOrigin()
					.size() > 1) {

				HighlightMin hlmin = new HighlightMin();
				HighlightMax hlmax = new HighlightMax();
				HighlightDominationColumn hldomcol = new HighlightDominationColumn();
				currentSlide.getHighlight().add(hlmin);
				currentSlide.getHighlight().add(hlmax);
				currentSlide.getHighlight().add(hldomcol);
				String[][] allResult = null;
				int data_length = 0;
				for (int k = 0; k < currentSlide.getSubTasks().size(); k++) {
					data_length += currentSlide.getSubTasks().get(k)
							.getExtractionMethod().getResultArray().length - 2;
				}
				allResult = new String[data_length + 2][3];
				int pos_to_copy = 0;
				for (int k = 0; k < currentSlide.getSubTasks().size(); k++) {
					if (k == 0) {
						System.arraycopy(currentSlide.getSubTasks().get(k)
								.getExtractionMethod().getResultArray(), 0,
								allResult, pos_to_copy, currentSlide
										.getSubTasks().get(k)
										.getExtractionMethod()
										.getResultArray().length);
						pos_to_copy += currentSlide.getSubTasks().get(k)
								.getExtractionMethod().getResultArray().length;
					} else {

						System.arraycopy(currentSlide.getSubTasks().get(k)
								.getExtractionMethod().getResultArray(), 2,
								allResult, pos_to_copy, currentSlide
										.getSubTasks().get(k)
										.getExtractionMethod()
										.getResultArray().length - 2);
						pos_to_copy += currentSlide.getSubTasks().get(k)
								.getExtractionMethod().getResultArray().length - 2;
					}

				}
				currentSlide.setTimeComputeHighlights(System.nanoTime());
				hlmin.execute(allResult);
				hlmax.execute(allResult);

				hldomcol.semanticValue = hlmax.semanticValue;
				hldomcol.helpValues2 = hlmin.semanticValue;
				hldomcol.execute(tbl.getPivotTable());

				currentSlide.subTimeComputeHighlights(System.nanoTime());
					
			} else {
				HighlightMin hlmin = new HighlightMin();
				HighlightMax hlmax = new HighlightMax();
				currentSlide.getHighlight().add(hlmin);
				currentSlide.getHighlight().add(hlmax);
				currentSlide.setTimeComputeHighlights(System.nanoTime());
				hlmin.execute(currentSlide.getSubTasks().get(0)
						.getExtractionMethod().getResultArray());
				hlmax.execute(currentSlide.getSubTasks().get(0)
						.getExtractionMethod().getResultArray());
				currentSlide.subTimeComputeHighlights(System.nanoTime());

			}
			currentSlide.computeColorTable(tbl);   	
		}
	}

	 public String[] createExtraPivot(SubTask subtsk,SubTask origSubtsk, CubeQuery origCubeQuery){
			String[] extraPivot = new String[2];
			extraPivot[0] = "";
			extraPivot[1] = "";
			
			if (subtsk.getDifferencesFromOrigin().size() > 0
					&& subtsk.getDifferencesFromOrigin().get(0) == -4) {
				extraPivot[0] = String.valueOf(subtsk.getDifferencesFromOrigin().get(0));
				extraPivot[1] = origSubtsk.getExtractionMethod().getRowPivot().
						toArray()[subtsk.getDifferencesFromOrigin().get(1)].toString();
			}
			if (subtsk.getDifferencesFromOrigin().size() > 0
					&& subtsk.getDifferencesFromOrigin().get(0) == -5) {
				extraPivot[0] = String.valueOf(subtsk.getDifferencesFromOrigin().get(0));
				extraPivot[1] = origCubeQuery.getSqlQuery().getColPivot().
						toArray()[subtsk.getDifferencesFromOrigin().get(1)].toString();
			}
			return extraPivot;
	}

	private String[][] customCopyArray(String[][] SlideTable, String[][] currentTable) {
		int rows_width = SlideTable.length + currentTable.length;
		TreeSet<String> cols = new TreeSet<String>();
		for (int i = 2; i < SlideTable[0].length; i++)
			cols.add(SlideTable[0][i]);
		for (int i = 2; i < currentTable[0].length; i++)
			cols.add(currentTable[0][i]);
		String[][] newTable = new String[rows_width][cols.size() + 2];
		int col_width = SlideTable[0].length;
		if (SlideTable[0].length < currentTable[0].length) {
			col_width = currentTable[0].length;
		}

		for (int i = 0; i < SlideTable.length; i++) {
			if (i == 0) {
				newTable[i][0] = SlideTable[i][0];
				newTable[i][1] = SlideTable[i][1];
				for (int j = 0; j < cols.size(); j++) {
					newTable[i][j + 2] = cols.toArray()[j].toString();
				}
			} else {
				newTable[i][0] = SlideTable[i][0];
				newTable[i][1] = SlideTable[i][1];

				for (int j = 2; j < newTable[i].length; j++) {
					newTable[i][j] = "-";
					for (int k = 0; k < SlideTable[i].length; k++) {
						if (newTable[0][j].equals(SlideTable[0][k])) {
							newTable[i][j] = SlideTable[i][k];
						}
					}
				}
			}
		}

		for (int cols_index = 0; cols_index < col_width; cols_index++) {
			newTable[SlideTable.length][cols_index] = "";
		}

		for (int i = 0; i < currentTable.length; i++) {
			if (i == 0) {
				newTable[SlideTable.length + i][0] = currentTable[i][0];
				newTable[SlideTable.length + i][1] = currentTable[i][1];
				for (int j = 0; j < cols.size(); j++) {
					newTable[i][j + 2] = cols.toArray()[j].toString();

				}
			} else {
				newTable[SlideTable.length + i][0] = currentTable[i][0];
				newTable[SlideTable.length + i][1] = currentTable[i][1];

				for (int j = 2; j < newTable[i].length; j++) {
					newTable[SlideTable.length + i][j] = "-";
					for (int k = 0; k < currentTable[i].length; k++) {
						if (newTable[0][j].equals(currentTable[0][k])) {
							newTable[SlideTable.length + i][j] = currentTable[i][k];
						}
					}
				}
			}
		}

		for (int k = 0; k < newTable.length; k++) {
			for (int l = 0; l < newTable[k].length; l++) {
				if (newTable[k][l] == null) {
					newTable[k][l] = "";
				}
			}
		}
		return newTable;
	}

}
