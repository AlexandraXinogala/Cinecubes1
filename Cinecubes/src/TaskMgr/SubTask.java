package TaskMgr;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.awt.Color;
import CubeMgr.CubeBase.CubeBase;
import CubeMgr.CubeBase.CubeQuery;
import HelpTask.ExtractionMethod;
import HighlightMgr.Highlight;
import HighlightMgr.HighlightTable;

public class SubTask {
    
    private Highlight highlight;
    private ExtractionMethod extractionMethod;  
    private ArrayList<Integer> differencesFromOrigin;
    private long timeExecutionQuery;
    private long timeProduceOfCubeQuery;
    private long timeProduceOfExtractionMethod;
    private long timeCreationOfSbTsk;
    
    public SubTask(){
    	differencesFromOrigin=new ArrayList<Integer>();
    	highlight = new HighlightTable();
    }
        
    public boolean execute(CubeBase cubeBase){
    	timeExecutionQuery=System.nanoTime();
    	ResultSet rset=cubeBase.getDatabase().executeSql(extractionMethod.toString());
    	timeExecutionQuery=System.nanoTime()-timeExecutionQuery;
    	return extractionMethod.setResult(rset);
    };
 
    public void addTimeProduceOfExtractionMethod(long end, long start){
		timeProduceOfExtractionMethod = end - start;
	}
	
	public void addTimeCreationOfSbTsk(long end, long start){
		timeCreationOfSbTsk = end - start;
	}
	
	public Highlight getHighlight() {
		return highlight;
	}

	public ExtractionMethod getExtractionMethod() {
		return extractionMethod;
	}

	public void setExtractionMethod(ExtractionMethod ExtractionMeth) {
		extractionMethod = ExtractionMeth;
	}
	
	public ArrayList<Integer> getDifferencesFromOrigin() {
		return differencesFromOrigin;
	}

	public int getDifferenceFromOrigin(int i) {
		return differencesFromOrigin.get(i);
	}

	public boolean checkOriginSubTask(){
		return extractionMethod.getResult().getResultArray() == null;
	}
	
	public void addDifferenceFromOrigin(int num){
		this.differencesFromOrigin.add(num);
	}
	
	public void createSubTask(CubeQuery cubequery,int difference,int replace, long strTime, CubeBase cubeBase){
		long endTime=System.nanoTime();
        long strTimeProduce=System.nanoTime();
        extractionMethod =  cubequery.produceExtractionMethod();
        timeProduceOfExtractionMethod = System.nanoTime() - strTimeProduce;
        if (replace == 1) 
        	 differencesFromOrigin.add(-1);
        differencesFromOrigin.add(difference);
    	timeProduceOfCubeQuery = endTime - strTime;
		timeCreationOfSbTsk = System.nanoTime() - strTime;
		execute(cubeBase);
	}
	 	
	public CubeQuery createNewExtractionMethod(String num_act, String measure){
		long strTime = System.nanoTime();
 		CubeQuery cubequery = new CubeQuery("Act " + String.valueOf(num_act));
 		cubequery.setAggregateFunction( "Act " + String.valueOf(num_act));
 		cubequery.addMeasure(1,measure);
 		cubequery.setBasicStoredCube(null);
 		timeProduceOfCubeQuery = System.nanoTime() - strTime;
 		strTime = System.nanoTime();
 		extractionMethod =  cubequery.produceExtractionMethod();
  		timeProduceOfCubeQuery = System.nanoTime() - strTime;
 		return cubequery;
	}
	
	public void job(String[][] pivotTable,Color[][]  colorTable){
		HighlightTable hltbl = (HighlightTable) getHighlight();
		hltbl.findDominatedRowsColumns(pivotTable, colorTable);
		
	}
}
