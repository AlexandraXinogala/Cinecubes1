package storymgr;
import java.util.ArrayList;

import TextMgr.TextExtraction;
import TextMgr.TextExtractionPPTX;
import AudioMgr.Audio;
import AudioMgr.AudioEngine;
import AudioMgr.MaryTTSAudioEngine;
import CubeMgr.CubeBase.CubeQuery;

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
	
	public void setTimeCreationText(long timeCreationText ){
		this.timeCreationText = timeCreationText;
	}
	
	public void addTimeCreationText(long time ){
		timeCreationText += time;
	}
		
	public void subTimeCreationText(long now ){
		timeCreationText = now  - timeCreationText;
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

	public void setAudioFile(String fileName) {
		this.audio.setFileName(fileName);
	}
	
	public Audio getAudio(){
		return audio;
	}
	
	public void addAudioToEpisode(){ 
		setAudioFile("audio/" + audioMgr.randomIdentifier());
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

	public void setTitle(String title) {
		Title = title;
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
	
	public void computeColorTable(Tabular tbl ){
		timeCreationColorTable = System.nanoTime();
    	tbl.setColorTable(getHighlight());
    	timeCreationColorTable = System.nanoTime()  - timeCreationColorTable;      
   	}
	
	
	public String createSlideOriginal(boolean isAudioOn, TextExtraction txtMgr, CubeQuery cubeQuery){
		String ActHighlights ="";
		Tabular tbl = (Tabular) getVisual();

		/* ====== Create Txt For Original ======= */
		timeCreationText =System.nanoTime();
		Title += "Answer to the original question";
		notes +=  (((TextExtractionPPTX) txtMgr)
			.createTextForOriginalAct1(cubeQuery,highlight).replace("  ", " "));//tsk.getCubeQuery(0),
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
	
	
	public void createSlideEnd(boolean isAudioOn, String notes){
		timeCreationText =System.nanoTime();
		Title +="Summary";
		this.notes += notes;
		timeCreationText = System.nanoTime()  - timeCreationText;
		if (isAudioOn) 
			addAudioToEpisode();
	}
}
