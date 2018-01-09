package TaskMgr;

import storymgr.Act;
import CubeMgr.CubeBase.CubeBase;

public class TaskIntro extends Task {

	public TaskIntro() {
		super();
	}

	@Override
	public void generateSubTasks(CubeBase DB) {
		this.addNewSubTask();
	}
	
	@Override
	public void constructActEpidoses(Act currentAct) {
		//PptxSlide tmpslide=new PptxSlide();
		//currentAct.addEpisode(tmpslide);
	}

}
