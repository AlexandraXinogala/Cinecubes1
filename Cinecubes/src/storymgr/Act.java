package storymgr;

import java.util.ArrayList;

import CubeMgr.CubeManager;
import CubeMgr.CubeBase.CubeQuery;
import CubeMgr.StarSchema.SqlQuery;
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
    
    public String getActHighlights(){
    	return ActHighlights;
    }
        
    public void setTimeCreation(long creationTime){
    	this.creationTime = creationTime;
    }
    
    public long getTimeCreation(){
    	return creationTime ;
    }
    
    public Act(int id){
    	txtMgr = new TextExtractionPPTX();
    	this.id =id;
    	Episodes=new ArrayList<Episode>();
    	ActHighlights="";
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
	
	public Task getTask(){
		return tsk;
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
	
	public void doIntroTask(CubeQuery cubequery,boolean isAudioOn,  CubeManager CubeManager){
		tsk = new TaskIntro();
		tsk.addCubeQuery(cubequery);
		tsk.generateSubTasks(CubeManager.getCubeBase());
		tsk.constructActEpidoses(this);
		PptxSlide tmpslide=new PptxSlide();
		addEpisode(tmpslide);
		constructTxtIntroAct(isAudioOn);
		creationTime = System.nanoTime() - creationTime;
	}
    	
	private void constructTxtIntroAct( boolean isAudioOn) {
		PptxSlide tmpslide = (PptxSlide) getEpisode(0);
		tmpslide.setTitle("CineCube Report");

		tmpslide.setTimeCreationText(System.nanoTime());
		tmpslide.setSubTitle(((TextExtractionPPTX) txtMgr).createTxtForIntroSlide(
				tsk.getCubeQuery(0)));
		tmpslide.subTimeCreationText(System.nanoTime());
		tmpslide.setNotes(tmpslide.getSubTitle());

		if (isAudioOn) 
			tmpslide.addAudioToEpisode();
	}
	
	public SubTask doOriginalTask(CubeQuery cubequery,boolean isAudioOn,  CubeManager CubeManager){
		tsk =new TaskOriginal();
		tsk.addCubeQuery(cubequery);
		tsk.generateSubTasks(CubeManager.getCubeBase());
		tsk.constructActEpidoses(this);
		SubTask OriginSbTsk = tsk.getLastSubTask();
		if (OriginSbTsk.getExtractionMethod().getResult().getResultArray() == null) {
			System.err.println("Your query does not have result. Try again!");
			System.exit(2);
		}
		constructTxtOriginalAct( isAudioOn);
		creationTime = System.nanoTime() - creationTime;
		return OriginSbTsk;
	}

	private void constructTxtOriginalAct(boolean isAudioOn) {

		CubeQuery currentCubeQuery = tsk.getCubeQuery(0);
		PptxSlide newSlide = (PptxSlide) getEpisode(0);
		Tabular tbl = (Tabular) newSlide.getVisual();

		/* ====== Create Txt For Original ======= */
		newSlide.setTimeCreationText(System.nanoTime());
		newSlide.setTitle("Answer to the original question");
		newSlide.setNotes(((TextExtractionPPTX) txtMgr)
				.createTextForOriginalAct1(currentCubeQuery,
						newSlide.getHighlight()).replace("  ", " "));

		String add_to_notes = ((TextExtractionPPTX)txtMgr)
				.createTxtForColumnsDominate(tbl.getPivotTable(), newSlide
						.getHighlight().get(2));
		add_to_notes += ((TextExtractionPPTX) txtMgr)
				.createTxtForRowsDominate(tbl.getPivotTable(), newSlide
						.getHighlight().get(3));
		newSlide.setNotes(newSlide.getNotes() + "\n" + add_to_notes);
		newSlide.subTimeCreationText(System.nanoTime());
		if (add_to_notes.length() > 0)
			 ActHighlights +=("Concerning the original query, some interesting findings include:\n\t");
		 ActHighlights +=( add_to_notes.replace("\n", "\n\t"));

		 if (isAudioOn) 
			 newSlide.addAudioToEpisode();
	}

	public ArrayList<PptxSlide> doTaskActI(CubeQuery cubequery,SubTask OriginSbTsk ,boolean isAudioOn,  CubeManager CubeManager,String measure){
		ArrayList<PptxSlide> slideToEnd = new ArrayList<PptxSlide>();
		tsk =new TaskActI();
		long timeSbts = System.nanoTime();
		tsk.addNewSubTask();
		tsk.getLastSubTask().setExtractionMethod(createCubeQueryStartOfActSlide( "I", measure));
		tsk.getLastSubTask().execute(CubeManager.getCubeBase().getDatabase());
		tsk.getLastSubTask().setTimeCreationOfSbTsk(System.nanoTime(), timeSbts);
		tsk.getSubTasks().add(OriginSbTsk);
		tsk.addCubeQuery(cubequery);
		tsk.generateSubTasks(CubeManager.getCubeBase());
		tsk.constructActEpidoses(this);
		slideToEnd = setupTextAct1(cubequery, isAudioOn);
		creationTime = System.nanoTime() - creationTime;
		return slideToEnd;
	}
	
	public SqlQuery createCubeQueryStartOfActSlide(String num_act, String measure) {
		long strTime = System.nanoTime();
		CubeQuery cubequery = new CubeQuery("Act " + String.valueOf(num_act));
		cubequery.setAggregateFunction( "Act " + String.valueOf(num_act));
		cubequery.addMeasure(1,measure);
		cubequery.setBasicStoredCube(null);
		tsk.getLastSubTask().setTimeProduceOfCubeQuery(System.nanoTime(), strTime);

		tsk.addCubeQuery(cubequery);
		SqlQuery newSqlQuery = new SqlQuery();
		strTime = System.nanoTime();
		newSqlQuery.produceExtractionMethod(cubequery);
		tsk.getLastSubTask().setTimeProduceOfCubeQuery(System.nanoTime(), strTime);
		cubequery.setSqlQuery(newSqlQuery);
		return newSqlQuery;

	}
	
	private ArrayList<PptxSlide> setupTextAct1(CubeQuery origCubeQuery, boolean isAudioOn) {
		ArrayList<Integer> numSlideToRemove = new ArrayList<Integer>();
		ArrayList<PptxSlide> slideToEnd = new ArrayList<PptxSlide>();
		boolean ActHasWriteHiglights = false;
		for (int j = 0; j < getNumEpisodes(); j++) {
			PptxSlide currentSlide = (PptxSlide) getEpisode(j);
			if (j == 0) {
				currentSlide.setTimeCreationText(System.nanoTime());
				currentSlide.setTitle(currentSlide.getTitle()
						+ ": Putting results in context");
				currentSlide
						.setSubTitle("In this series of slides we put the original result in context, by comparing the behavior of its defining values with the behavior of values that are similar to them.");
				currentSlide.setNotes(currentSlide.getTitle() + "\n"
						+ currentSlide.getSubTitle());
				currentSlide.subTimeCreationText(System.nanoTime());

				if (isAudioOn) 
					currentSlide.addAudioToEpisode();
			} else {
				SubTask subtsk = currentSlide.getSubTasks().get(0);
				CubeQuery currentCubeQuery = currentSlide.CbQOfSlide.get(0);
				SqlQuery currentSqlQuery = (SqlQuery) subtsk
						.getExtractionMethod();
				Tabular tbl = (Tabular) currentSlide.getVisual();
				if (subtsk.getDifferenceFromOrigin(0) == -1) {
					int gamma_index_change = subtsk.getDifferenceFromOrigin(1);
					currentSlide.setTimeCreationText(System.nanoTime());
					currentSlide.setTitle("Assessing the behavior of ");
					currentSlide.setTitle(currentSlide.getTitle()
							+ currentCubeQuery.getGammaExpressions()
									.get(gamma_index_change)[0].replace("_dim",
									""));
					currentSlide.getVisual().getPivotTable()[0][0] = " Summary for "
							+ currentCubeQuery.getGammaExpressions()
									.get(gamma_index_change)[0].split("_")[0];
					currentSlide.setNotes(((TextExtractionPPTX) txtMgr)
							.createTextForAct1(
									currentCubeQuery.getGammaExpressions(),
									origCubeQuery.getSigmaExpressions(),
									currentCubeQuery.getSigmaExpressions(),
									currentSqlQuery.getResult().getResultArray(),
									currentSlide.getHighlight(),
									subtsk.getDifferenceFromOrigin(1),
									currentCubeQuery.getAggregateFunction(),
									currentCubeQuery.getListMeasure().get(0).getName()));
					currentSlide.subTimeCreationText(System.nanoTime());
					String add_to_notes = "";
					if (gamma_index_change == 0) {
						long strTimeTxt = System.nanoTime();
						add_to_notes = ((TextExtractionPPTX) txtMgr)
								.createTxtComparingToSiblingColumn(tbl
										.getPivotTable(), currentSlide
										.getHighlight().get(2));
						currentSlide.addTimeCreationText(System.nanoTime() - strTimeTxt);
					} else {
						long strTimeTxt = System.nanoTime();
						add_to_notes = ((TextExtractionPPTX) txtMgr)
								.createTxtComparingToSiblingRow(tbl
										.getPivotTable(), currentSlide
										.getHighlight().get(3));
						currentSlide.addTimeCreationText(System.nanoTime() - strTimeTxt);
					}
					currentSlide.setNotes(currentSlide.getNotes() + "\n"
							+ add_to_notes);

					if (ActHasWriteHiglights == false
							&& add_to_notes.length() > 0) {
						ActHasWriteHiglights = true;
						ActHighlights +=( "@First, we tried to put the original result in context, by comparing its defining values with similar ones.\n\t");
					}
					String toReplaceString1 = "Compared to its sibling we observe that in";
					String toReplaceString2 = "Compared to its sibling we observe the following:";
					String newString = "##When we compared ";

					if (tbl.boldColumn > -1)
						newString += tbl.getPivotTable()[0][tbl.boldColumn];
					if (tbl.boldRow > -1)
						newString += tbl.getPivotTable()[tbl.boldRow][0];

					newString += " to its siblings, grouped by "
							+ currentCubeQuery.getGammaExpressions().get(0)[0]
									.replace("_dim", "")
							+ " and "
							+ currentCubeQuery.getGammaExpressions().get(1)[0]
									.replace("_dim", "")
							+ ", we observed the following:\n~~";

					ActHighlights +=(add_to_notes.replace("\n", "\n\t")
							.replace(toReplaceString1, newString + "In")
							.replace(toReplaceString2, newString));

					if (isAudioOn) 
						currentSlide.addAudioToEpisode();
				} else {
					slideToEnd.add(currentSlide);
					numSlideToRemove.add(j);
					currentSlide.setTimeCreationText(System.nanoTime());
					currentSlide.setTitle("The ~ which changed @ : ");
					for (int i = 0; i < subtsk.getDifferencesFromOrigin()
							.size(); i++) {
						if (i > 0)
							currentSlide.setTitle(currentSlide.getTitle()
									+ " AND ");
						currentSlide
								.setTitle(currentSlide.getTitle()
										+ tsk.getCubeQuery(1).getGammaExpressions().get(subtsk
												.getDifferenceFromOrigin(i))[0]);
					}
					String text_cond = "Conditions";
					String text_are = "are";
					if (subtsk.getDifferencesFromOrigin().size() == 1) {
						text_cond = "Condition";
						text_are = "is";
					}
					currentSlide.setTitle(currentSlide.getTitle()
							.replace("~", text_cond).replace("@", text_are));
					currentSlide.subTimeCreationText(System.nanoTime());
				}
			}
		}
		return slideToEnd;

	}
	
	public void doTaskActII(CubeQuery cubequery,SubTask OriginSbTsk ,boolean isAudioOn,  CubeManager CubeManager, String measure){
		tsk = new TaskActII();
		tsk.addNewSubTask();
		tsk.getLastSubTask().setExtractionMethod(createCubeQueryStartOfActSlide( "II", measure));
		tsk.getLastSubTask().execute(CubeManager.getCubeBase().getDatabase());
		tsk.getSubTasks().add(OriginSbTsk);
		tsk.addCubeQuery(cubequery);
		tsk.generateSubTasks(CubeManager.getCubeBase());
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
			SubTask subtsk = currentSlide.getSubTasks().get(0);
			CubeQuery currentCubeQuery = currentSlide.CbQOfSlide.get(0);
			SqlQuery currentSqlQuery = (SqlQuery) subtsk.getExtractionMethod();
			Tabular tbl = (Tabular) currentSlide.getVisual();
			HighlightTable hltbl = (HighlightTable) subtsk.getHighlight();
			if (j == 0) {
				currentSlide.setTimeCreationText(System.nanoTime());
				currentSlide.setTitle(currentSlide.getTitle()
						+ ": Explaining results");
				currentSlide.setNotes(currentSlide.getTitle());
				currentSlide
						.setSubTitle("In this series of slides we will present a detailed analysis of the values involved in the result of the original query. To this end, "
								+ "we drill-down the hierarchy of grouping levels of the result to one level of aggregation lower, whenever this is possible.");
				currentSlide.setNotes(currentSlide.getTitle() + "\n"
						+ currentSlide.getSubTitle());
				currentSlide.subTimeCreationText(System.nanoTime());
			} else if (subtsk.getDifferencesFromOrigin().size() > 1) {

				long start_creation_domination = System.nanoTime();
				hltbl.findDominatedRowsColumns(tbl.getPivotTable(),
						tbl.colortable);
				currentSlide.addTimeComputeHighlights(System.nanoTime() - start_creation_domination);


				String add_to_notes = "";
				currentSlide.setNotes("");
				if (subtsk.getDifferencesFromOrigin().get(0) == -4) {
					currentSlide.setTimeCreationText(System.nanoTime());
					currentSlide
							.setTitle("Drilling down the Rows of the Original Result");
					currentSlide
							.setNotes(((TextExtractionPPTX) txtMgr)
									.createTextForAct2(
											origCubeQuery.getGammaExpressions(),
											origCubeQuery.getSigmaExpressions(),
											currentSlide.getVisual()
													.getPivotTable(),
											0,
											origCubeQuery.getAggregateFunction(),
											origCubeQuery.getListMeasure().get(0).getName(),
											origSubtsk.getExtractionMethod().getResult()
													.getRowPivot().size(),
											currentCubeQuery.getGammaExpressions()
													.get(currentSlide
															.getSubTasks()
															.get(0)
															.getDifferenceFromOrigin(
																	2))));

					add_to_notes = ((TextExtractionPPTX) txtMgr)
							.createTxtForDominatedRowColumns(
									tbl.getPivotTable(), tbl.colortable,
									currentSlide.getHighlight(), false, true);
					currentSlide.subTimeCreationText(System.nanoTime());
					currentSlide.setNotes(currentSlide.getNotes()
							+ add_to_notes);
				} else if (subtsk.getDifferencesFromOrigin().get(0) == -5) {

					currentSlide.setTimeCreationText(System.nanoTime());
					currentSlide
							.setTitle("Drilling down the Columns of the Original Result");
					currentSlide
							.setNotes(((TextExtractionPPTX) txtMgr)
									.createTextForAct2(
											origCubeQuery.getGammaExpressions(),
											origCubeQuery.getSigmaExpressions(),
											currentSlide.getVisual()
													.getPivotTable(),
											1,
											origCubeQuery.getAggregateFunction(),
											origCubeQuery.getListMeasure().get(0).getName(),
											origSubtsk.getExtractionMethod().getResult()
													.getColPivot().size(),
											currentCubeQuery.getGammaExpressions()
													.get(currentSlide
															.getSubTasks()
															.get(0)
															.getDifferenceFromOrigin(
																	2))));

					add_to_notes = ((TextExtractionPPTX) txtMgr)
							.createTxtForDominatedRowColumns(
									tbl.getPivotTable(), tbl.colortable,
									currentSlide.getHighlight(), false, true);
					currentSlide.subTimeCreationText(System.nanoTime());
					currentSlide.setNotes(currentSlide.getNotes()
							+ add_to_notes);
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
				currentSlide.setTimeCreationText(System.nanoTime());
				currentSlide.setTitle("Answer to the original question");
				currentSlide.setNotes(((TextExtractionPPTX) txtMgr)
						.createTextForOriginalAct2(
								currentCubeQuery.getGammaExpressions(),
								currentCubeQuery.getSigmaExpressions(),
								currentSqlQuery.getResult().getResultArray()).replace(
								"  ", " "));
				currentSlide.subTimeCreationText(System.nanoTime());
			}
			if (isAudioOn) 
				currentSlide.addAudioToEpisode();
		}
	}
	
	public void doSummaryTask(ArrayList<Act> acts, boolean isAudioOn,  CubeManager CubeManager){
		tsk = new TaskSummary();
		tsk.generateSubTasks(CubeManager.getCubeBase());
		tsk.constructActEpidoses(this);
		PptxSlide tmpslide=new PptxSlide();
		addEpisode(tmpslide);
		constructTxtEndAct(acts,isAudioOn);
		creationTime = System.nanoTime() - creationTime;
	}
	
	public void constructTxtEndAct(ArrayList<Act> acts, boolean isAudioOn) {
		PptxSlide newSlide = (PptxSlide) getEpisode(0);
		newSlide.setTimeCreationText(System.nanoTime());
		newSlide.setNotes("In this slide we summarize our findings.");
		newSlide.setTitle("Summary");
		for (Act actItem : acts) {
			if (actItem.getActHighlights().length() > 0) {
				if (newSlide.getNotes().length() > 0)
					newSlide.setNotes(newSlide.getNotes() + "@");
				newSlide.setNotes(newSlide.getNotes() + actItem.getActHighlights());
			}
			newSlide.setNotes(newSlide.getNotes().replace("\n\n\n", "\n")
					.replace("\n\n", "\n").replace("\n\t\n", "\n\t"));
		}
		newSlide.setNotes(newSlide.getNotes().replace("\n\n\n", "\n")
				.replace("\n\n", "\n").replace("\t", "").replace("\r", ""));
		newSlide.subTimeCreationText(System.nanoTime());

		if (isAudioOn) 
			newSlide.addAudioToEpisode();
	}

}
