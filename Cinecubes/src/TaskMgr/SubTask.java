package TaskMgr;

import java.sql.ResultSet;
import java.util.ArrayList;

import CubeMgr.StarSchema.Database;
import HelpTask.ExtractionMethod;
import HighlightMgr.Highlight;

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

	public void setDifferencesFromOrigin(ArrayList<Integer> differencesfromorigin) {
		this.differencesFromOrigin = differencesfromorigin;
	}
	
	public void addDifferenceFromOrigin(int num){
		this.differencesFromOrigin.add(num);
	}
	
	
}
