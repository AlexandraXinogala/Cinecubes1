package TaskMgr;

import CubeMgr.CubeBase.CubeBase;
import CubeMgr.CubeBase.CubeQuery;

public class TaskIntro extends Task {

	public TaskIntro() {
		super();
	}

	@Override
    public void generateSubTasks(CubeBase cubeBase,CubeQuery cubequery,
    		SubTask OriginSbTsk, String measure){
    	addNewSubTask();
	}
	

}
