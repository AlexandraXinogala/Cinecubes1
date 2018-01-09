package TaskMgr;

import storymgr.Act;
import CubeMgr.CubeBase.CubeBase;


public class TaskSummary extends Task {

	public TaskSummary() {
		super();
	}

	@Override
	public void generateSubTasks(CubeBase DB) {
		this.addNewSubTask();
	}

	@Override
	public void constructActEpidoses(Act currentAct) {
	//	PptxSlide tmpslide=new PptxSlide();
	//	currentAct.addEpisode(tmpslide);
	}

}
