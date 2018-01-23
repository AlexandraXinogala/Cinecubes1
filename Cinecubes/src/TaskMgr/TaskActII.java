package TaskMgr;

import java.util.HashSet;
import CubeMgr.CubeBase.CubeBase;
import CubeMgr.CubeBase.CubeQuery;
import CubeMgr.StarSchema.SqlQuery;


public class TaskActII extends Task {

	public TaskActII() {
		super();
	}

    public void generateSubTasks(CubeBase cubeBase,CubeQuery cubequery, SubTask OriginSbTsk,
    		String measure){
		/* highlight for Original */
    	addNewSubTask();
		getLastSubTask().setExtractionMethod(createCubeQueryStartOfActSlide( "II", measure));
		getLastSubTask().execute(cubeBase.getDatabase());
		getSubTasks().add(OriginSbTsk);
		addCubeQuery(cubequery);
		generateSubTasks_per_row(cubeBase);
		generateSubTasks_per_col(cubeBase);
	}

	public SqlQuery createCubeQueryStartOfActSlide(String num_act, String measure) {
		long strTime = System.nanoTime();
		CubeQuery cubequery = new CubeQuery("Act " + String.valueOf(num_act));
		cubequery.setAggregateFunction( "Act " + String.valueOf(num_act));
		cubequery.addMeasure(1,measure);
		cubequery.setBasicStoredCube(null);
		getLastSubTask().setTimeProduceOfCubeQuery(System.nanoTime(), strTime);
		addCubeQuery(cubequery);
		SqlQuery newSqlQuery = new SqlQuery();
		strTime = System.nanoTime();
		newSqlQuery.produceExtractionMethod(cubequery);
		getLastSubTask().setTimeProduceOfCubeQuery(System.nanoTime(), strTime);
		cubequery.setSqlQuery(newSqlQuery);
		return newSqlQuery;

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
	
}
