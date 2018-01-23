package storymgr;
import java.util.ArrayList;
import java.util.TreeSet;

import TaskMgr.SubTask;
import TextMgr.TextExtraction;
import TextMgr.TextExtractionPPTX;
import AudioMgr.AudioEngine;
import AudioMgr.MaryTTSAudioEngine;
import CubeMgr.CubeBase.CubeQuery;
import CubeMgr.StarSchema.SqlQuery;
import HighlightMgr.HighlightCompareColumn;
import HighlightMgr.HighlightCompareRow;
import HighlightMgr.HighlightMax;
import HighlightMgr.HighlightMin;

public class PptxSlide extends Episode {
	
	private AudioEngine audioMgr;
	private String notes;
	private String Title;
	private String SubTitle;
	private String TitleColumn;
	private String TitleRow;
	public ArrayList<CubeQuery> CbQOfSlide;
	private long timeCreationAudio;
	private long timeCreationText;
	private long timeCreationTabular;
	private long timeCreationColorTable;
	private long timeCreationPutInPPTX;
	private long timeCombineSlide;
	private long timeComputeHighlights;
	
	public PptxSlide() {
		super();
		Title ="";
		SubTitle ="";
		notes = "";
		audioMgr = new MaryTTSAudioEngine();
		audioMgr.InitializeVoiceEngine();
		initializeTime();
		CbQOfSlide=new ArrayList<CubeQuery>();
	}
	
	
	public PptxSlide(String notes, String title, String subtitle, long timeCreationText){
		super();
		audioMgr = new MaryTTSAudioEngine();
		audioMgr.InitializeVoiceEngine();
		this.notes= notes;
		this.Title = title;
		this.SubTitle = subtitle;
		initializeTime();
		this.timeCreationText = timeCreationText;
	}

	private void initializeTime(){
		timeCreationAudio=0;
		timeCreationPutInPPTX=0;
		timeCreationTabular=0;
		timeCreationColorTable=0;
		timeCreationText=0;
		timeCreationPutInPPTX=0;
		timeCombineSlide=0;
		timeComputeHighlights=0;
	}
	
	public void addCubeQuery(CubeQuery cubeQuery){
		CbQOfSlide.add(cubeQuery);
	}
	
	public void setTimeCreationPutInPPTX(long timeCreationPutInPPTX ){
		this.timeCreationPutInPPTX = timeCreationPutInPPTX;
	}
	
	public void subTimeCreationPutInPPTX(long now ){
		timeCreationPutInPPTX = now  - timeCreationPutInPPTX;
	}
	
	public void addTimeCreationPutInPPTX(long now ){
		timeCreationPutInPPTX += now;
	}
	
	public void setTimeCreationTabular(long timeCreationTabular ){
		this.timeCreationTabular = timeCreationTabular;
	}
	
	public void subTimeCreationTabular(long now ){
		timeCreationTabular = now  - timeCreationTabular;
	}
	
	public void addTimeCreationTabular(long time ){
		timeCreationTabular += time;
	}
	
	public long getTimeCreationTabular(){
		return timeCreationTabular;
	}
			
	public void addTimeCombineSlide(long time ){
		timeCombineSlide += time;
	}
	
	public void setTimeComputeHighlights(long timeComputeHighlights ){
		this.timeComputeHighlights = timeComputeHighlights;
	}
	
	public void subTimeComputeHighlights(long now ){
		timeComputeHighlights = now  - timeComputeHighlights;
	}
	
	public void addTimeComputeHighlights(long time ){
		timeComputeHighlights += time;
	}
		

	public long getTimeComputeHighlights(){
		return timeComputeHighlights;
	}
	
	@Override
	public void setVisual(Visual vis) {
		this.visual=vis;		
	}
	
	public Visual getVisual(){
		return visual;
	}
	
	public String getFilenameAudio(){
		return audio.getFileName();
	}
	
	public void addAudioToEpisode(){ 
		audio.setFileName("audio/" + audioMgr.randomIdentifier());
		timeCreationAudio= System.nanoTime();
		audioMgr.CreateAudio(notes, audio.getFileName());
		timeCreationAudio = System.nanoTime()  - timeCreationAudio;
	}
	
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getTitle() {
		return Title;
	}
 
	public String getSubTitle() {
		return SubTitle;
	}

	public void setSubTitle(String subTitle) {
		SubTitle = subTitle;
	}

	public String getTitleColumn() {
		return TitleColumn;
	}

	public void setTitleColumn(String titleColumn) {
		TitleColumn = titleColumn;
	}

	public String getTitleRow() {
		return TitleRow;
	}

	public void setTitleRow(String titleRow) {
		TitleRow = titleRow;
	}
	
	public void computeColorTable(){
		timeCreationColorTable = System.nanoTime();
    	((Tabular)visual).setColorTable(getHighlight());
    	timeCreationColorTable = System.nanoTime()  - timeCreationColorTable;      
   	}
	public void computePivotTable (TreeSet<String> rowPivot,TreeSet<String> colPivot,String queryResult[][], String[] extraPivot ){
		Tabular tbl = new Tabular();					//
		setVisual(tbl);
		setTimeCreationTabular(System.nanoTime());
		tbl.CreatePivotTable(rowPivot, colPivot, queryResult, extraPivot);
		subTimeCreationTabular(System.nanoTime());
	}
	
	public void calculateHighlights(String[][] Result, HighlightMin hlmin,
   			HighlightMax hlmax,  HighlightCompareColumn hlcmpcol,
   			HighlightCompareRow hlcmprow, CubeQuery currentCubeQuery,
   			TreeSet<String> rowPivot,TreeSet<String> colPivot, int i,
   			CubeQuery origCubeQuery) {
   		Tabular tbl = (Tabular) getVisual();
		int tmp_it=origCubeQuery.getIndexOfSigma(currentCubeQuery.getGammaExpressions().get(i)[0]);
       	setTimeComputeHighlights(System.nanoTime());
       	hlmin.execute(Result);
       	hlmax.execute(Result);
       	tbl.boldColumn=getBoldColumn(rowPivot,origCubeQuery.getSigmaExpressions().get(tmp_it)[2]);
   		tbl.boldRow=getBoldRow(colPivot,origCubeQuery.getSigmaExpressions().get(tmp_it)[2]);
       	hlcmpcol.bold=tbl.boldColumn;
   		if(tbl.boldColumn>-1) 
   			hlcmpcol.execute(tbl.getPivotTable());
   		hlcmprow.bold=tbl.boldRow;
   		if(tbl.boldRow>-1) 
   			hlcmprow.execute(tbl.getPivotTable());
   	    subTimeComputeHighlights(System.nanoTime());
   	} 
	
	private int getBoldColumn(TreeSet<String> Columns,String nameColumnToBold){
		for(int j=0;j<Columns.size();j++){
			if(("'"+Columns.toArray()[j].toString()+"'").equals(nameColumnToBold)) return j+1;
		}
		return -1;
	}
	
	private int getBoldRow(TreeSet<String> Rows,String nameRowToBold){
		for(int i=0;i<Rows.size();i++){
			if(("'"+Rows.toArray()[i].toString()+"'").equals(nameRowToBold)) return i+1;
		}
		return -1;
	}
	
	public String createSlideOriginal(boolean isAudioOn, TextExtraction txtMgr, CubeQuery cubeQuery){
		String ActHighlights ="";
		Tabular tbl = (Tabular) getVisual();
		timeCreationText =System.nanoTime();
		Title += "Answer to the original question";
		notes +=  (((TextExtractionPPTX) txtMgr)
			.createTextForOriginalAct1(cubeQuery,getHighlightMaxValue(), getHighlightMinValue()));
		String add_to_notes = ((TextExtractionPPTX)txtMgr)
			.createTxtForColumnsDominate(tbl.getPivotTable(), highlight.get(2));
		add_to_notes += ((TextExtractionPPTX) txtMgr)
			.createTxtForRowsDominate(tbl.getPivotTable(), highlight.get(3));
		notes +="\n" + add_to_notes;
		timeCreationText = System.nanoTime()  - timeCreationText;
		if (add_to_notes.length() > 0)
			 ActHighlights +=("Concerning the original query, some interesting findings include:\n\t");
		 ActHighlights +=( add_to_notes.replace("\n", "\n\t"));
		 if (isAudioOn) 
			addAudioToEpisode();
		 return ActHighlights;
	}
	
	public void createSlideIntro(boolean isAudioOn,TextExtraction txtMgr, CubeQuery cubeQuery){
		timeCreationText =System.nanoTime();
		Title +=  "CineCube Report";
		SubTitle = ((TextExtractionPPTX) txtMgr).createTxtForIntroSlide(cubeQuery);
				notes += SubTitle;
		timeCreationText = System.nanoTime()  - timeCreationText;
		if (isAudioOn) 
			addAudioToEpisode();
	}
	
	public void createSlideAct1(boolean isAudioOn){
		timeCreationText =System.nanoTime();
		Title += ": Putting results in context";
		SubTitle = "In this series of slides we put the original result in context, by comparing the behavior of its defining values with the behavior of values that are similar to them.";
		notes += Title + "\n" + SubTitle;
		timeCreationText = System.nanoTime()  - timeCreationText;
		if (isAudioOn) 
			addAudioToEpisode();
	}
	
	public String createNotes( TextExtraction txtMgr, CubeQuery origCubeQuery){
		CubeQuery currentCubeQuery = CbQOfSlide.get(0);
		SubTask subtsk = getSubTasks().get(0);
		SqlQuery currentSqlQuery = (SqlQuery) subtsk.getExtractionMethod();
		String newNotes = "";
		Tabular tbl = (Tabular) visual;
		int gamma_index_change = subtsk.getDifferenceFromOrigin(1);
		timeCreationText = (System.nanoTime());
		Title = ("Assessing the behavior of "
				+ currentCubeQuery.getGammaExpressions()
				.get(gamma_index_change)[0].replace("_dim",	""));
		getVisual().getPivotTable()[0][0] = " Summary for "
				+ currentCubeQuery.getGammaExpressions()
						.get(gamma_index_change)[0].split("_")[0];
		setNotes(((TextExtractionPPTX) txtMgr)
				.createTextForAct1(
						currentCubeQuery,
						origCubeQuery.getSigmaExpressions(),
						currentSqlQuery.getResult().getResultArray(),
						getHighlightMaxValue(), 
						getHighlightMinValue(),
						subtsk.getDifferenceFromOrigin(1)));
		timeCreationText = System.nanoTime()  - timeCreationText;
		
		long strTimeTxt = System.nanoTime();
		if (gamma_index_change == 0) {
			newNotes = ((TextExtractionPPTX) txtMgr)
					.createTxtComparingToSiblingColumn(tbl
							.getPivotTable(), 
							getHighlight().get(2));
		} else {
			newNotes = ((TextExtractionPPTX) txtMgr)
				.createTxtComparingToSiblingRow(tbl.getPivotTable(),
				getHighlight().get(3));
		}
		timeCreationText += System.nanoTime() - strTimeTxt;
		setNotes(getNotes() + "\n"
				+ newNotes);
		return newNotes;
	}
	
	public String addNotes(String addToNotes, boolean isAudioOn){
		String ActHighlights = "";
		CubeQuery currentCubeQuery = CbQOfSlide.get(0);
		Tabular tbl = (Tabular) visual;
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

		ActHighlights +=addToNotes.replace("\n", "\n\t")
				.replace(toReplaceString1, newString + "In")
				.replace(toReplaceString2, newString);

		if (isAudioOn) 
			addAudioToEpisode();
		return ActHighlights;
	}
	
	public void createSlideAct1(CubeQuery origCubeQuery){
		SubTask subtsk = getSubTasks().get(0);
		timeCreationText =System.nanoTime();
		Title ="The ~ which changed @ : ";
		for (int i = 0; i < subtsk.getDifferencesFromOrigin().size(); i++) {
			if (i > 0)
				Title +=  " AND ";
			Title += origCubeQuery.getGammaExpressions().get(subtsk
									.getDifferenceFromOrigin(i))[0];
		}
		String text_cond = "Conditions";
		String text_are = "are";
		if (subtsk.getDifferencesFromOrigin().size() == 1) {
			text_cond = "Condition";
			text_are = "is";
		}
		Title = Title.replace("~", text_cond).replace("@", text_are);
		timeCreationText = System.nanoTime()  - timeCreationText;
	}

	public void createSlideAct2(){
		timeCreationText =System.nanoTime();
		Title +=  ": Explaining results" ;
		SubTitle = ("In this series of slides we will present a detailed analysis of the values involved in the result of the original query. To this end, "
				+ "we drill-down the hierarchy of grouping levels of the result to one level of aggregation lower, whenever this is possible.");
		notes += Title + "\n" + SubTitle;
		timeCreationText = System.nanoTime()  - timeCreationText;
	}
	
	public void createSlideAct2( TextExtraction txtMgr){
		SubTask subtsk = getSubTasks().get(0);
		CubeQuery currentCubeQuery = CbQOfSlide.get(0);
		SqlQuery currentSqlQuery = (SqlQuery) subtsk.getExtractionMethod();
		timeCreationText =System.nanoTime();
		Title +=  "Answer to the original question" ;
		notes += ((TextExtractionPPTX) txtMgr).createTextForOriginalAct2(
				currentCubeQuery.getGammaExpressions(), currentCubeQuery.getSigmaExpressions(),
			currentSqlQuery.getResult().getResultArray()).replace("  ", " ");
		timeCreationText = System.nanoTime()  - timeCreationText;
	}
	
	public String createSlideAct2( String word,TextExtraction txtMgr, CubeQuery origCubeQuery,
			 int size){
		String add_to_notes = "";
		CubeQuery currentCubeQuery = CbQOfSlide.get(0);
		Tabular tbl = (Tabular) visual;
		timeCreationText =System.nanoTime();
		Title += "Drilling down the " + word + " of the Original Result";
		notes += ((TextExtractionPPTX) txtMgr).createTextForAct2(
			origCubeQuery.getGammaExpressions(), origCubeQuery.getSigmaExpressions(),
			visual.getPivotTable(), 1, origCubeQuery.getAggregateFunction(),
			origCubeQuery.getListMeasure().get(0).getName(),
			size,currentCubeQuery.getGammaExpressions().get(subTask.get(0)
			.getDifferenceFromOrigin(2)));
		add_to_notes = ((TextExtractionPPTX) txtMgr)
				.createTxtForDominatedRowColumns(
						tbl.getPivotTable(), tbl.colortable,
						getHighlight(), false, true);
		timeCreationText = System.nanoTime()  - timeCreationText;
		notes +=  add_to_notes;
		return add_to_notes;
	}
	
	
	public void createSlideEnd(boolean isAudioOn, String notes){
		timeCreationText = System.nanoTime();
		Title += "Summary";
		this.notes += notes;
		timeCreationText = System.nanoTime()  - timeCreationText;
		if (isAudioOn) 
			addAudioToEpisode();
	}
	
	
	public void createNewSlide(CubeQuery currentCubeQuery, SubTask subtsk, String title){
		addCubeQuery(currentCubeQuery);
    	addSubTask(subtsk);
		timeCreationText = System.nanoTime();
		this.Title += title;
		timeCreationText = System.nanoTime()  - timeCreationText;
	}
	

	public String[][] customCopyArray(PptxSlide newSlide) {
		String[][] SlideTable = ((Tabular)getVisual()).getPivotTable();
		String[][] currentTable = ((Tabular)newSlide.getVisual()).getPivotTable();
		int rows_width = SlideTable.length + currentTable.length;
		TreeSet<String> cols = new TreeSet<String>();
		for (int i = 2; i < SlideTable[0].length; i++)
			cols.add(SlideTable[0][i]);
		for (int i = 2; i < currentTable[0].length; i++)
			cols.add(currentTable[0][i]);
		String[][] newTable = new String[rows_width][cols.size() + 2];
		int col_width = SlideTable[0].length;
		if (SlideTable[0].length < currentTable[0].length) {
			col_width = currentTable[0].length;
		}

		for (int i = 0; i < SlideTable.length; i++) {
			if (i == 0) {
				newTable[i][0] = SlideTable[i][0];
				newTable[i][1] = SlideTable[i][1];
				for (int j = 0; j < cols.size(); j++) {
					newTable[i][j + 2] = cols.toArray()[j].toString();
				}
			} else {
				newTable[i][0] = SlideTable[i][0];
				newTable[i][1] = SlideTable[i][1];

				for (int j = 2; j < newTable[i].length; j++) {
					newTable[i][j] = "-";
					for (int k = 0; k < SlideTable[i].length; k++) {
						if (newTable[0][j].equals(SlideTable[0][k])) {
							newTable[i][j] = SlideTable[i][k];
						}
					}
				}
			}
		}

		for (int cols_index = 0; cols_index < col_width; cols_index++) {
			newTable[SlideTable.length][cols_index] = "";
		}

		for (int i = 0; i < currentTable.length; i++) {
			if (i == 0) {
				newTable[SlideTable.length + i][0] = currentTable[i][0];
				newTable[SlideTable.length + i][1] = currentTable[i][1];
				for (int j = 0; j < cols.size(); j++) {
					newTable[i][j + 2] = cols.toArray()[j].toString();

				}
			} else {
				newTable[SlideTable.length + i][0] = currentTable[i][0];
				newTable[SlideTable.length + i][1] = currentTable[i][1];

				for (int j = 2; j < newTable[i].length; j++) {
					newTable[SlideTable.length + i][j] = "-";
					for (int k = 0; k < currentTable[i].length; k++) {
						if (newTable[0][j].equals(currentTable[0][k])) {
							newTable[SlideTable.length + i][j] = currentTable[i][k];
						}
					}
				}
			}
		}

		for (int k = 0; k < newTable.length; k++) {
			for (int l = 0; l < newTable[k].length; l++) {
				if (newTable[k][l] == null) {
					newTable[k][l] = "";
				}
			}
		}
		return newTable;
	}
	
	
	public String[][] createResultArray(){
		String[][] allResult = null;
		int data_length = 0;
		for (int k = 0; k < subTask.size(); k++) {
			data_length += getSubTasks().get(k)
					.getExtractionMethod().getResultArray().length - 2;
		}
		allResult = new String[data_length + 2][3];
		int pos_to_copy = 0;
		for (int k = 0; k < subTask.size(); k++) {
			if (k == 0) {
				System.arraycopy(subTask.get(k).getExtractionMethod().
					getResultArray(), 0,allResult, pos_to_copy, 
					subTask.get(k).getExtractionMethod().getResultArray().length);
				pos_to_copy += subTask.get(k).getExtractionMethod().
						getResultArray().length;
			} else {
				System.arraycopy(subTask.get(k).getExtractionMethod().
						getResultArray(), 2,allResult, pos_to_copy, subTask.get(k).
						getExtractionMethod().getResultArray().length - 2);
				pos_to_copy += subTask.get(k).getExtractionMethod().
						getResultArray().length - 2;
			}
		}
		return allResult;
	}
	
}
