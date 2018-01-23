package storymgr;

import java.util.ArrayList;

import CubeMgr.CubeManager;
import CubeMgr.CubeBase.CubeQuery;
import HighlightMgr.HighlightTable;
import TaskMgr.SubTask;
import TaskMgr.Task;
import TaskMgr.TaskActI;
import TaskMgr.TaskActII;
import TaskMgr.TaskIntro;
import TaskMgr.TaskOriginal;
import TaskMgr.TaskSummary;
import TextMgr.TextExtraction;
import TextMgr.TextExtractionPPTX;


public class Act {

	
	private TextExtraction txtMgr;
	/**
	 * @uml.property  name="id"
	 */
	private int id;
    /**
	 * @uml.property  name="episodes"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="StoryMgr.Episode"
	 */
    private ArrayList<Episode> Episodes;
    /**
	 * @uml.property  name="tsk"
	 * @uml.associationEnd  
	 */
    private Task tsk;
    /**
	 * @uml.property  name="creationTime"
	 */
    private long creationTime;

    /**
	 * @uml.property  name="actHighlights"
	 */
    private  String ActHighlights; 
             
    public Act(int id){
    	txtMgr = new TextExtractionPPTX();
    	this.id =id;
    	Episodes=new ArrayList<Episode>();
    	ActHighlights="";
    	creationTime = System.nanoTime();
    }

	public ArrayList<Episode> getEpisodes() {
		return Episodes;
	}

	public void addEpisode(Episode episode){
		Episodes.add(episode);
	}
	
	public Episode getEpisode(int i){
		return Episodes.get(i);
	}
	
	public int getNumEpisodes(){
		return Episodes.size();
	}	
	 public long getTimeCreation() {
		 return creationTime;
	 }
	
	/**
	 * @return  the id
	 * @uml.property  name="id"
	 */
	public int getId() {
		return id;
	}
	
	/* for Debug Reason*/
	public String toString(){
		return "Act id:"+String.valueOf(id)+"\n# Episodes:"
				+String.valueOf(this.getNumEpisodes());
	}
	
	public void doIntroTask(CubeQuery cubequery, boolean isAudioOn,  CubeManager CubeManager){
		tsk = new TaskIntro();
		tsk.addCubeQuery(cubequery);
		tsk.generateSubTasks(CubeManager.getCubeBase(),cubequery,null,"");
		tsk.constructActEpidoses(this);
		PptxSlide tmpslide=new PptxSlide();
		addEpisode(tmpslide);
		tmpslide.createSlideIntro(isAudioOn,txtMgr, tsk.getCubeQuery(0)); 
		creationTime = System.nanoTime() - creationTime;
	}
  	
	public SubTask doOriginalTask(CubeQuery cubequery, boolean isAudioOn,  CubeManager CubeManager){
		tsk =new TaskOriginal();
		tsk.addCubeQuery(cubequery);
		tsk.generateSubTasks(CubeManager.getCubeBase(),cubequery, null, "");
		tsk.constructActEpidoses(this);
		SubTask OriginSbTsk = tsk.getLastSubTask();
		if (OriginSbTsk.getExtractionMethod().getResult().getResultArray() == null) {
			System.err.println("Your query does not have result. Try again!");
			System.exit(2);
		}
		ActHighlights += ((PptxSlide) getEpisode(0)).createSlideOriginal(isAudioOn, txtMgr, tsk.getCubeQuery(0));
		creationTime = System.nanoTime() - creationTime;
		return OriginSbTsk;
	}

	public Act doTaskActI(CubeQuery cubequery, SubTask OriginSbTsk ,boolean isAudioOn,  CubeManager CubeManager,String measure){
		ArrayList<PptxSlide> slideToEnd = new ArrayList<PptxSlide>();
		tsk =new TaskActI();
		
		tsk.generateSubTasks(CubeManager.getCubeBase(), cubequery, OriginSbTsk, measure);
		tsk.constructActEpidoses(this);
		slideToEnd = setupTextAct1(cubequery, isAudioOn);
		creationTime = System.nanoTime() - creationTime;
		if (slideToEnd.size() > 0) {
			Act newAct = new Act(3);
			newAct.setupTextAct3(slideToEnd);
			return newAct;
		}
		return null;
	}
	

	
	private ArrayList<PptxSlide> setupTextAct1(CubeQuery origCubeQuery, boolean isAudioOn) {
		ArrayList<Integer> numSlideToRemove = new ArrayList<Integer>();
		ArrayList<PptxSlide> slideToEnd = new ArrayList<PptxSlide>();
		boolean ActHasWriteHiglights = false;
		for (int j = 0; j < getNumEpisodes(); j++) {
			PptxSlide currentSlide = (PptxSlide) getEpisode(j);
			if (j == 0) {
				currentSlide.createSlideAct1(isAudioOn);
			} else {
				if (currentSlide.checkDifferenceFromOrigin(-1)) {
					String addToNotes = currentSlide.createNotes(txtMgr,origCubeQuery );
					if (ActHasWriteHiglights == false && addToNotes.length() > 0) {
						ActHasWriteHiglights = true;
						ActHighlights +=  "@First, we tried to put the original result in context, by comparing its defining values with similar ones.\n\t";
					}
					ActHighlights += currentSlide.addNotes(addToNotes,isAudioOn );
				} else {
					slideToEnd.add(currentSlide);
					numSlideToRemove.add(j);
					currentSlide.createSlideAct1(origCubeQuery);
				}
			}
		}
		return slideToEnd;

	}
	
	private void setupTextAct3(  ArrayList<PptxSlide>  slideToEnd){
		PptxSlide newSlide = new PptxSlide("","Auxiliary slides for Act I","", System.nanoTime());
		addEpisode(newSlide);
		for (int k = 0; k < slideToEnd.size(); k++) {
			getEpisodes().remove(slideToEnd.get(k));
			addEpisode(slideToEnd.get(k));
		}
		slideToEnd.clear();
	}
	
	public void doTaskActII(CubeQuery cubequery,SubTask OriginSbTsk ,boolean isAudioOn,  CubeManager CubeManager, String measure){
		tsk = new TaskActII();
		
		tsk.generateSubTasks(CubeManager.getCubeBase(), cubequery, OriginSbTsk, measure);
		if (tsk.getNumSubTasks() > 2) {
			tsk.constructActEpidoses(this);
			this.setupTextAct2(cubequery,OriginSbTsk, isAudioOn);
		}
		creationTime = System.nanoTime() - creationTime;
	}
	
	public void setupTextAct2( CubeQuery origCubeQuery, SubTask origSubtsk, boolean isAudioOn) {
		boolean ActHasWriteHiglights = false;
		for (int j = 0; j < getNumEpisodes(); j++) {
			PptxSlide currentSlide = (PptxSlide) getEpisode(j);
			if (j == 0) {
				currentSlide.createSlideAct2();
			} else if (currentSlide.checkCountOfSubTask()) {
				SubTask subtsk = currentSlide.getSubTasks().get(0);
				CubeQuery currentCubeQuery = currentSlide.CbQOfSlide.get(0);
				Tabular tbl = (Tabular) currentSlide.getVisual();
				HighlightTable hltbl = (HighlightTable) subtsk.getHighlight();
				long start_creation_domination = System.nanoTime();
				hltbl.findDominatedRowsColumns(tbl.getPivotTable(),
						tbl.colortable);
				currentSlide.addTimeComputeHighlights(System.nanoTime() - start_creation_domination);
				String add_to_notes = "";
				if (currentSlide.checkDifferenceFromOrigin(-4)) {
					add_to_notes = currentSlide.createSlideAct2("Rows", txtMgr, origCubeQuery,
							origSubtsk.getExtractionMethod().getResult().getColPivot().size());
				} else if (currentSlide.checkDifferenceFromOrigin(-5)) {
					add_to_notes = currentSlide.createSlideAct2("Columns", txtMgr, origCubeQuery,
							origSubtsk.getExtractionMethod().getResult().getColPivot().size());
				}
				if (ActHasWriteHiglights == false && add_to_notes.length() > 0) {
					ActHasWriteHiglights = true;
					 ActHighlights +=("Then we analyzed the results by drilling down one level in the hierarchy.\n\t\n\t");
				}
				try {

					 ActHighlights +=( "##When we drilled down "
							+ currentCubeQuery.getGammaExpressions()
									.get(currentSlide.getSubTasks().get(0)
											.getDifferenceFromOrigin(2))[0]
									.replace("_dim", ", ")
							+ " we observed the following facts:\n"
							+ "~~"
							+ add_to_notes.split(":")[1].replace("\n", "\n\t")
									.replace("\t\t", "\t"));
					 ActHighlights = ActHighlights.replace("  ", " ");
				} catch (Exception ex) {
					// Do Nothing
				}
			} else {
				currentSlide.createSlideAct2(txtMgr);
			}
			if (isAudioOn) 
				currentSlide.addAudioToEpisode();
		}
	}
	
	public void doSummaryTask(ArrayList<Act> acts, boolean isAudioOn,  CubeManager CubeManager){
		tsk = new TaskSummary();
		tsk.generateSubTasks(CubeManager.getCubeBase(), null, null, "");
		tsk.constructActEpidoses(this);
		PptxSlide tmpslide=new PptxSlide();
		tmpslide.createSlideEnd(isAudioOn, composeActHighlights(acts));
		addEpisode(tmpslide);
		creationTime = System.nanoTime() - creationTime;
	}
	
	private String composeActHighlights(ArrayList<Act> acts) {
		String notesFromAct = "In this slide we summarize our findings.";
		for (Act actItem : acts) {
			if (actItem.ActHighlights.length() > 0) {
				if ( notesFromAct.length() > 0)
					 notesFromAct += "@";
				notesFromAct += actItem.ActHighlights;
			}
			notesFromAct = notesFromAct.replace("\n\n\n", "\n")
					.replace("\n\n", "\n").replace("\n\t\n", "\n\t");
		}
		notesFromAct = notesFromAct.replace("\n\n\n", "\n")
				.replace("\n\n", "\n").replace("\t", "").replace("\r", "");
		return notesFromAct;
	}

}
