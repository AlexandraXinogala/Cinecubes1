package taskmgr;

import cubemanager.cubebase.CubeBase;
import cubemanager.cubebase.CubeQuery;
import exctractionmethod.ExtractionMethod;

public class TaskOriginal extends Task {

	public TaskOriginal() {
		super();
	}

	@Override
    public void generateSubTasks(CubeBase cubeBase,CubeQuery cubequery,
    		SubTask OriginSbTsk, String measure){
    	addNewSubTask();
        long time_produce_original = System.nanoTime();
        ExtractionMethod method =  cubeQuery.get(0).produceExtractionMethod();
        time_produce_original = System.nanoTime() - time_produce_original;
        getLastSubTask().addTimeProduceOfExtractionMethod (time_produce_original,0);
        getLastSubTask().setExtractionMethod(method);
        getLastSubTask().execute(cubeBase);
        
	}

}
