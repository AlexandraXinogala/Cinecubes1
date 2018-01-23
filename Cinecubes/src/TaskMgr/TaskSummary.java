package TaskMgr;

import CubeMgr.CubeBase.CubeBase;
import CubeMgr.CubeBase.CubeQuery;

public class TaskSummary extends Task {

	public TaskSummary() {
		super();
	}

	@Override
    public void generateSubTasks(CubeBase cubeBase,CubeQuery cubequery,
    		SubTask OriginSbTsk, String measure){
		this.addNewSubTask();
	}



}
