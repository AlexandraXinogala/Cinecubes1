package TaskMgr;

import java.sql.ResultSet;
import java.util.ArrayList;

import CubeMgr.CubeBase.CubeBase;
import CubeMgr.CubeBase.CubeQuery;
import CubeMgr.StarSchema.Database;
import CubeMgr.StarSchema.SqlQuery;
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
    }
        
    public boolean execute(Database dB){
    	timeExecutionQuery=System.nanoTime();
    	ResultSet rset=dB.executeSql(extractionMethod.toString());
    	timeExecutionQuery=System.nanoTime()-timeExecutionQuery;
    	return extractionMethod.setResult(rset);
    };
     
	public void setTimeProduceOfCubeQuery(long end, long start){
		timeProduceOfCubeQuery = end - start;
	}

	public void setTimeProduceOfExtractionMethod(long end, long start){
		timeProduceOfExtractionMethod = end - start;
	}
	
	public void setTimeCreationOfSbTsk(long end, long start){
		timeCreationOfSbTsk = end - start;
	}
	
	public Highlight getHighlight() {
		return highlight;
	}

	
	public void setHighlight(Highlight Hghlght) {
		highlight = Hghlght;
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
		SqlQuery newSqlQuery=new SqlQuery();
        long strTimeProduce=System.nanoTime();
        newSqlQuery.produceExtractionMethod(cubequery);
        timeProduceOfExtractionMethod = System.nanoTime() - strTimeProduce;
        cubequery.setSqlQuery(newSqlQuery);
        setExtractionMethod(newSqlQuery);
        if (replace == 1) 
        	 differencesFromOrigin.add(-1);
        HighlightTable hltbl=new HighlightTable();
        setHighlight(hltbl);
        differencesFromOrigin.add(difference);
    	timeProduceOfCubeQuery = endTime - strTime;
		timeCreationOfSbTsk = System.nanoTime() - strTime;
		execute(cubeBase.getDatabase());
	}
	
}
