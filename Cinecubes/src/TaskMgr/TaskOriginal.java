package TaskMgr;

import CubeMgr.CubeBase.CubeBase;
import CubeMgr.CubeBase.CubeQuery;
import CubeMgr.StarSchema.SqlQuery;


public class TaskOriginal extends Task {

	public TaskOriginal() {
		super();
	}

	@Override
    public void generateSubTasks(CubeBase cubeBase,CubeQuery cubequery,
    		SubTask OriginSbTsk, String measure){
    	this.addNewSubTask();
		SqlQuery newSqlQuery = new SqlQuery();
        long time_produce_original = System.nanoTime();
        newSqlQuery.produceExtractionMethod(this.cubeQuery.get(0));
        time_produce_original = System.nanoTime() - time_produce_original;
        this.getLastSubTask().setTimeProduceOfExtractionMethod (time_produce_original,0);
        this.getLastSubTask().setExtractionMethod(newSqlQuery);
        this.getLastSubTask().execute(cubeBase.getDatabase());
        this.cubeQuery.get(0).setSqlQuery(newSqlQuery);
	}

}
