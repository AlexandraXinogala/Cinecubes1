package storymgr;

import java.util.ArrayList;

import CubeMgr.CubeManager;
import TaskMgr.SubTask;
import CubeMgr.CubeBase.CubeQuery;

public class Story {

	private String act_story_time;
	private String measure;
	private FinalResult FinRes;
	private ArrayList<Act> Acts;

	public Story() {
		Acts = new ArrayList<Act>();
	}

	public Act getLastAct() {
		return Acts.get(Acts.size() - 1);
	}

	public FinalResult getFinalResult() {
		return FinRes;
	}

	public void setFinalResult(FinalResult finRes) {
		FinRes = finRes;
	}

	public ArrayList<Act> getActs() {
		return Acts;
	}

	public void createActs(CubeQuery cubeQuery, boolean isAudioOn,
			CubeManager cubeManager, String measure) {
		
		this.measure = measure;
		createIntroAct(cubeQuery, isAudioOn, cubeManager);
		SubTask originSbTsk = createOriginalAct(cubeQuery, isAudioOn,
				cubeManager);
		act_story_time += "Original Act\t" + getLastAct().getTimeCreation()
				+ "\n";
		createActI(cubeQuery, originSbTsk, isAudioOn, cubeManager);
		createActII(cubeQuery, originSbTsk, isAudioOn, cubeManager);
		createSummaryAct(isAudioOn, cubeManager);
	}

	public void createIntroAct(CubeQuery cubeQuery, boolean isAudioOn,
			CubeManager cubeManager) {
		createAct(0);
		getLastAct().doIntroTask(cubeQuery, isAudioOn, cubeManager);
		act_story_time += "Intro Act\t" + getLastAct().getTimeCreation() + "\n";
	}

	private void createAct(int idAct) {
		Acts.add(new Act(idAct));
		getLastAct().setTimeCreation(System.nanoTime());
	}

	private SubTask createOriginalAct(CubeQuery cubeQuery, boolean isAudioOn,
			CubeManager cubeManager) {
		createAct(20);
		return getLastAct().doOriginalTask(cubeQuery, isAudioOn, cubeManager);
	}

	private void createActI(CubeQuery cubeQuery, SubTask originSbTsk,
			boolean isAudioOn, CubeManager cubeManager) {
		createAct(1);
		ArrayList<PptxSlide> slideToEnd = getLastAct().doTaskActI(cubeQuery,
				originSbTsk, isAudioOn, cubeManager, measure);
		/* ======== Act 3 ============= */
		if (slideToEnd.size() > 0) {
			Acts.add(new Act(3));
			PptxSlide newSlide = new PptxSlide();
			newSlide.setTitle("Auxiliary slides for Act I");
			getLastAct().addEpisode(newSlide);
			for (int k = 0; k < slideToEnd.size(); k++) {
				getLastAct().getEpisodes().remove(slideToEnd.get(k));
				getLastAct().addEpisode(slideToEnd.get(k));
			}
			slideToEnd.clear();
		}
		act_story_time += "Act 1\t" + getLastAct().getTimeCreation() + "\n";
	}

	private void createActII(CubeQuery cubeQuery, SubTask originSbTsk,
			boolean isAudioOn, CubeManager cubeManager) {
		createAct(2);
		getLastAct().doTaskActII(cubeQuery, originSbTsk, isAudioOn,
				cubeManager, measure);
		act_story_time += "Act 2\t" + getLastAct().getTimeCreation() + "\n";
	}

	private void createSummaryAct(boolean isAudioOn, CubeManager cubeManager) {
		createAct(-1);
		getLastAct().doSummaryTask(Acts, isAudioOn, cubeManager);
		act_story_time += "End Act\t" + getLastAct().getTimeCreation() + "\n";
	}

	public int getNumberOfSlides(){
		int num_slide_create = 0;
		for (Act actItem : Acts) {
			if (actItem.getEpisodes().size() > 1 || num_slide_create == 0
					|| actItem.getId() == -1 || actItem.getId() == 20)
				num_slide_create += actItem.getEpisodes().size();
		}
		return num_slide_create ;
	}
	
	
}
