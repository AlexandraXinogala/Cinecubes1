package TaskMgr;

import java.sql.ResultSet;
import java.sql.SQLException;

import storymgr.Act;
import storymgr.PptxSlide;

import CubeMgr.CubeBase.CubeBase;
import CubeMgr.CubeBase.CubeQuery;
import CubeMgr.CubeBase.Level;
import CubeMgr.StarSchema.SqlQuery;
import HighlightMgr.HighlightCompareColumn;
import HighlightMgr.HighlightCompareRow;
import HighlightMgr.HighlightMax;
import HighlightMgr.HighlightMin;
import HighlightMgr.HighlightTable;

public class TaskActI extends Task {

	public TaskActI() {
		super();
	}
        
    /* Cubequery Version to Generate Subtasks
     * 
     */
     public void generateSubTasks(CubeBase cubeBase,CubeQuery cubequery,
    		 SubTask OriginSbTsk, String measure){
    	long timeSbts = System.nanoTime();
		addNewSubTask();
		getLastSubTask().setExtractionMethod(createCubeQueryStartOfActSlide( "I", measure));
		getLastSubTask().execute(cubeBase.getDatabase());
		getLastSubTask().setTimeCreationOfSbTsk(System.nanoTime(), timeSbts);
		getSubTasks().add(OriginSbTsk);
		addCubeQuery(cubequery);
		
    	for(int i=0;i<this.cubeQuery.get(1).getSigmaExpressions().size();i++){
    		createSummarizeSubTask(i,cubeBase);
    	} 	
    }
     
     public SqlQuery createCubeQueryStartOfActSlide(String num_act, String measure) {
 		long strTime = System.nanoTime();
 		CubeQuery cubequery = new CubeQuery("Act " + String.valueOf(num_act));
 		cubequery.setAggregateFunction( "Act " + String.valueOf(num_act));
 		cubequery.addMeasure(1,measure);
 		cubequery.setBasicStoredCube(null);
 		getLastSubTask().setTimeProduceOfCubeQuery(System.nanoTime(), strTime);

 		addCubeQuery(cubequery);
 		SqlQuery newSqlQuery = new SqlQuery();
 		strTime = System.nanoTime();
 		newSqlQuery.produceExtractionMethod(cubequery);
 		getLastSubTask().setTimeProduceOfCubeQuery(System.nanoTime(), strTime);
 		cubequery.setSqlQuery(newSqlQuery);
 		return newSqlQuery;

 	}  
     
   public void computePivotTable(PptxSlide newSlide, SubTask subtsk,
		   CubeQuery currentCubeQuery,Act currentAct  ){
	   
     
	    
	    if(subtsk.getHighlight()==null)
	    	subtsk.setHighlight(new HighlightTable());
	  
	    HighlightMin hlmin=new HighlightMin();
	    HighlightMax hlmax=new HighlightMax();
	    HighlightCompareColumn hlcmpcol=new HighlightCompareColumn();
   		HighlightCompareRow hlcmprow=new HighlightCompareRow();
   		newSlide.addSubTask(subtsk);
   		newSlide.addCubeQuery(currentCubeQuery);
   		newSlide.getHighlight().add(hlmin);
	    newSlide.getHighlight().add(hlmax);
	    newSlide.getHighlight().add(hlcmpcol);
	    newSlide.getHighlight().add(hlcmprow);
	    if(subtsk.getDifferenceFromOrigin(0)==-1) 
	    	newSlide.calculateHighlights( subtsk.getExtractionMethod().getResultArray(),
	    		hlmin, hlmax, hlcmpcol, hlcmprow, currentCubeQuery,
	    		subtsk.getExtractionMethod().getColPivot(),
	    		subtsk.getExtractionMethod().getRowPivot(),
	    		subtsk.getDifferenceFromOrigin(1),cubeQuery.get(1));
	    newSlide.computeColorTable();   	
   }
    
  
      @Override
    public void constructActEpidoses(Act currentAct) {			
		for(int j = 0; j< getNumSubTasks(); j++){
    		if( j == 1 ) 
    			continue;
			SubTask subtsk= getSubTask(j);
    		SqlQuery currentSqlQuery=((SqlQuery)subtsk.getExtractionMethod());
    		CubeQuery currentCubeQuery = cubeQuery.get(j);
    		PptxSlide newSlide=new PptxSlide();		
		    
		    if((currentSqlQuery.getResultArray()!=null)){
		    	/*====== Compute Pivot Table =======*/
		    	String[] extraPivot=new String[2];
		        extraPivot[0]="";
		        extraPivot[1]="";
		        
		 	   newSlide.computePivotTable(subtsk.getExtractionMethod().getRowPivot(),
		 	    		 			  subtsk.getExtractionMethod().getColPivot(),
		 	    		 			  subtsk.getExtractionMethod().getResultArray(),
		 	    		 			  extraPivot);
		 	    currentAct.addEpisode(newSlide);
		    	computePivotTable(newSlide, subtsk, currentCubeQuery,
		    			   	      currentAct);
		    } else if (j == 0) {
		    	newSlide.createNewSlide(currentCubeQuery, subtsk,
		    			currentSqlQuery.getTitleosColumns());
        		currentAct.addEpisode(newSlide);
		    }
		    
		}
	}
    
    private void createSummarizeSubTask(int i,CubeBase cubeBase){
    	Level parentLvl = cubeQuery.get(1).getNameParentLevel(i);
		if(parentLvl==null)
			return;
		String tmp_query= cubeQuery.get(1).createQuery(i, parentLvl);
		ResultSet rs=cubeBase.getDatabase().executeSql(tmp_query);
		try {
			rs.beforeFirst();
			while(rs.next()) {
				String newValue="'"+rs.getString(1)+"'";
				if(tryParseInt(rs.getString(1))) 
					newValue=rs.getString(1);				
				createSubTask(cubeQuery.get(1),newValue,i,1,parentLvl.getName(),cubeBase);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
      
                 
    private void createSubTask(CubeQuery startQuery,String value,int toChange,int toReplace,String changevalue,CubeBase cubeBase){
    				
		/* This peace of code to see again */
		
		/* That check if a sigma expression is in gamma then add sigma to gamma
		 * and do two insertion of Subtask . Each subtask was one gamma same and
		 * change the other. 
		 * I must check if the gamma which add is correct  
		 */
		if(startQuery.checkIfSigmaExprIsInGamma(toChange)==false){
			for(int i=0;i<startQuery.getGammaExpressions().size();i++){
				long strTime=System.nanoTime();
				CubeQuery newQuery1=new CubeQuery(startQuery);
				String [] tmp=newQuery1. getSigmaExpressions().get(toChange)[0].split("\\.");
				newQuery1.getGammaExpressions().get(i)[0]=tmp[0];
				newQuery1.getGammaExpressions().get(i)[1]=tmp[1];
				if(toReplace == 1){
					newQuery1.getSigmaExpressions().get(toChange)[0] = tmp[0] + "." + changevalue;
					newQuery1.getSigmaExpressions().get(toChange)[2] = value;
				}
				addSubTask(newQuery1, i, toReplace, strTime, cubeBase);
			}
        } else {
			long strTime=System.nanoTime();
			CubeQuery newQuery=new CubeQuery(startQuery);
			newQuery.getSigmaExpressions().get(toChange)[2]=value;
			if(toReplace==1){			
				String[] tobeGamma=newQuery.getSigmaExpressions().get(toChange)[0].split("\\.");
				for(int i=0;i<newQuery.getGammaExpressions().size();i++){
					if(newQuery.getGammaExpressions().get(i)[0].equals(tobeGamma[0])){
						newQuery.getGammaExpressions().get(i)[1]=tobeGamma[1];
					}
				}
				newQuery.getSigmaExpressions().get(toChange)[0]=tobeGamma[0]+"."+changevalue;
			}
			
			addSubTask(newQuery,newQuery.getGammaPositionOfSigma(toChange),toReplace, strTime,cubeBase);
		}
    }
   	
	 private boolean tryParseInt(String value){
		boolean ret_value=true;
	    try{
	    	Integer.parseInt(value);
	    }catch(NumberFormatException nfe){  
	          ret_value=false;
	    }  
	     	return ret_value;  
	}
	
	
}
