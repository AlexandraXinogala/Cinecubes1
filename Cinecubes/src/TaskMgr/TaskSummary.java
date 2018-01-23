package TaskMgr;

import storymgr.Act;
import CubeMgr.CubeBase.CubeBase;
import CubeMgr.CubeBase.CubeQuery;
import CubeMgr.StarSchema.SqlQuery;


public class TaskSummary extends Task {

	public TaskSummary() {
		super();
	}

	@Override
    public void generateSubTasks(CubeBase cubeBase,CubeQuery cubequery,
    		SubTask OriginSbTsk, String measure){
		this.addNewSubTask();
	}

	@Override
	public void constructActEpidoses(Act currentAct) {
	//	PptxSlide tmpslide=new PptxSlide();
	//	currentAct.addEpisode(tmpslide);
	}

}
