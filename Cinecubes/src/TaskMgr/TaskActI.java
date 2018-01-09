package TaskMgr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeSet;

import storymgr.Act;
import storymgr.PptxSlide;
import storymgr.Tabular;
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
    public void generateSubTasks(CubeBase cubeBase){
    	for(int i=0;i<this.cubeQuery.get(1).getSigmaExpressions().size();i++){
    		createSummarizeSubTask(i,cubeBase,this.cubeQuery.get(1));
    	} 	
    }
    
    @Override
	public void constructActEpidoses(Act currentAct) {
		CubeQuery origCubeQuery=this.cubeQuery.get(1);
		
		for(int j=0;j<currentAct.getTask().getNumSubTasks();j++){
    		if(j==1) continue;
			SubTask subtsk=currentAct.getTask().getSubTask(j);
    		SqlQuery currentSqlQuery=((SqlQuery)subtsk.getExtractionMethod());
    		CubeQuery currentCubeQuery=currentAct.getTask().cubeQuery.get(j);
    		PptxSlide newSlide=new PptxSlide();
    		
        	newSlide.addCubeQuery(currentCubeQuery);
	        Tabular tbl=new Tabular();
	        newSlide.setVisual(tbl);
	        
	        String[] extraPivot=new String[2];
	        extraPivot[0]="";
	        extraPivot[1]="";
    		
		    newSlide.addSubTask(subtsk);
		    
		    if((currentSqlQuery.getResultArray()!=null)){
		    	
		    	/*====== Compute Pivot Table =======*/
		    	newSlide.setTimeCreationTabular(System.nanoTime());
			    tbl.CreatePivotTable(subtsk.getExtractionMethod().getRowPivot(),
			    		 			  subtsk.getExtractionMethod().getColPivot(),
			    		 			  subtsk.getExtractionMethod().getResultArray(),
			    		 			  extraPivot);
			    newSlide.subTimeCreationTabular(System.nanoTime());
			    currentAct.addEpisode(newSlide);
			    
			    if(subtsk.getHighlight()==null) subtsk.setHighlight(new HighlightTable());
			  
			    HighlightMin hlmin=new HighlightMin();
		    	HighlightMax hlmax=new HighlightMax();
		    	HighlightCompareColumn hlcmpcol=new HighlightCompareColumn();
		    	HighlightCompareRow hlcmprow=new HighlightCompareRow();
		    	newSlide.getHighlight().add(hlmin);
			    newSlide.getHighlight().add(hlmax);
			    newSlide.getHighlight().add(hlcmpcol);
			    newSlide.getHighlight().add(hlcmprow);
			    
	        	if(subtsk.getDifferenceFromOrigin(0)==-1){
	        		
		        	int tmp_it=this.getIndexOfSigma(origCubeQuery.getSigmaExpressions(),currentCubeQuery.getGammaExpressions().get(subtsk.getDifferenceFromOrigin(1))[0]);
		        	
		        	/*====== Calculate Highlioghts =======*/
		        	newSlide.setTimeComputeHighlights(System.nanoTime());
		        	hlmin.execute(subtsk.getExtractionMethod().getResultArray());
		        	hlmax.execute(subtsk.getExtractionMethod().getResultArray());
		        	tbl.boldColumn=getBoldColumn(subtsk.getExtractionMethod().getColPivot(),origCubeQuery.getSigmaExpressions().get(tmp_it)[2]);
		    		tbl.boldRow=getBoldRow(subtsk.getExtractionMethod().getRowPivot(),origCubeQuery.getSigmaExpressions().get(tmp_it)[2]);
		        	
		    		hlcmpcol.bold=tbl.boldColumn;
		        	if(tbl.boldColumn>-1) hlcmpcol.execute(tbl.getPivotTable());
		        	
		        	hlcmprow.bold=tbl.boldRow;
		        	if(tbl.boldRow>-1) hlcmprow.execute(tbl.getPivotTable());
		        
			    	newSlide.subTimeComputeHighlights(System.nanoTime());
	        	}
		    		    	
		    	/*====== Compute Color Table =======*/
		    	newSlide.setTimeCreationColorTable(System.nanoTime());
		    	tbl.setColorTable(newSlide.getHighlight());
	        	newSlide.subTimeCreationColorTable(System.nanoTime());        	
			    
		    }
		    else if(j==0){
		    	newSlide.setTimeCreationText(System.nanoTime());
        		newSlide.setTitle(currentSqlQuery.getTitleosColumns());
        		newSlide.subTimeCreationText(System.nanoTime());
        		currentAct.addEpisode(newSlide);
		    }
		    
		}
	}
    
    private void createSummarizeSubTask(int i,CubeBase cubeBase,CubeQuery startQuery){
    	String dimension=startQuery.getSigmaExpressions().get(i)[0].split("\\.")[0];
		String lvlname=startQuery.getSigmaExpressions().get(i)[0].split("\\.")[1];
		String table=startQuery.getBasicStoredCube().getSqlTableByDimensionName(dimension);
		String field=startQuery.getBasicStoredCube().getSqlFieldByDimensionLevelName(dimension, lvlname);
		
		Level parentLvl=startQuery.getBasicStoredCube().getParentLevel(dimension,lvlname);
		if(parentLvl==null) return;
		String field2=parentLvl.lvlAttributes.get(0).getAttribute().getName();
		
		String tmp_query="SELECT DISTINCT "+field2+ " FROM "+table+" WHERE "+field+"="+startQuery.getSigmaExpressions().get(i)[2];
		//pithano shmeio kuklou task me schema
		ResultSet rs=cubeBase.getDatabase().executeSql(tmp_query);
		try {
			rs.beforeFirst();
			while(rs.next()){
				String newValue="'"+rs.getString(1)+"'";
				if(tryParseInt(rs.getString(1))) newValue=rs.getString(1);				
				createSubTask(startQuery,newValue,i,1,parentLvl.getName(),cubeBase);
				
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
    }
    
    private int getIndexOfSigma(ArrayList<String[]> sigmaExpressions,String gamma_dim) {
		int ret_value=-1;
		int i=0;
		for(String[] sigma : sigmaExpressions ){
			if(sigma[0].split("\\.")[0].equals(gamma_dim)) ret_value=i;
			i++;
		}
		return ret_value;
	}
    
   
              
    private void createSubTask(CubeQuery startQuery,String value,int toChange,int toReplace,String changevalue,CubeBase cubeBase){
    				
		/* This peace of code to see again */
		
		/* That check if a sigma expression is in gamma then add sigma to gamma
		 * and do two insertion of Subtask . Each subtask was one gamma same and
		 * change the other. 
		 * I must check if the gamma which add is correct  
		 */
		if(checkIfSigmaExprIsInGamma(toChange,startQuery)==false){
			for(int i=0;i<startQuery.getGammaExpressions().size();i++){
				long strTime=System.nanoTime();
				CubeQuery newQuery1=new CubeQuery("");
				copyListofArrayString(startQuery.getGammaExpressions(), newQuery1.getGammaExpressions());
				copyListofArrayString(startQuery.getSigmaExpressions(), newQuery1. getSigmaExpressions());
				newQuery1.setAggregateFunction(startQuery.getAggregateFunction());
				newQuery1.setBasicStoredCube(startQuery.getBasicStoredCube());
				newQuery1.setMsr(startQuery.getMsr());
				String [] tmp=newQuery1. getSigmaExpressions().get(toChange)[0].split("\\.");
				newQuery1.getGammaExpressions().get(i)[0]=tmp[0];
				newQuery1.getGammaExpressions().get(i)[1]=tmp[1];
				if(toReplace==1){
					newQuery1. getSigmaExpressions().get(toChange)[0]=tmp[0]+"."+changevalue;
					newQuery1. getSigmaExpressions().get(toChange)[2]=value;
				}

				long endTime=System.nanoTime();
				addSubTask(newQuery1,i,toReplace);
				this.getLastSubTask().setTimeCreationOfSbTsk(System.nanoTime(), strTime);
				this.getLastSubTask().setTimeProduceOfCubeQuery(endTime, strTime);
				this.getLastSubTask().execute(cubeBase.getDatabase());
		        
			}
        }
		else {
			long strTime=System.nanoTime();
			CubeQuery newQuery=new CubeQuery("");
			copyListofArrayString(startQuery.getGammaExpressions(), newQuery.getGammaExpressions());
			copyListofArrayString(startQuery.getSigmaExpressions(), newQuery.getSigmaExpressions());
			newQuery.setAggregateFunction(startQuery.getAggregateFunction());
			newQuery.setBasicStoredCube(startQuery.getBasicStoredCube());
			newQuery.setMsr(startQuery.getMsr());		
			
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
			long endTime=System.nanoTime();
			addSubTask(newQuery,getGammaPositionOfSigma(toChange,newQuery),toReplace);
			this.getLastSubTask().setTimeProduceOfCubeQuery(endTime, strTime);
			this.getLastSubTask().setTimeCreationOfSbTsk(System.nanoTime(), strTime);
			this.getLastSubTask().execute(cubeBase.getDatabase());
		}
    }
    
    private void addSubTask(CubeQuery cubequery,int difference,int replace){
    	this.addNewSubTask();
		this.cubeQuery.add(cubequery);
        SqlQuery newSqlQuery=new SqlQuery();
        long strTime=System.nanoTime();
        newSqlQuery.produceExtractionMethod(cubequery);
        this.getLastSubTask().setTimeProduceOfExtractionMethod(System.nanoTime(), strTime);
        cubequery.setSqlQuery(newSqlQuery);
        this.getLastSubTask().setExtractionMethod(newSqlQuery);
        if(replace==1) this.getLastSubTask().addDifferenceFromOrigin(-1);
        
        HighlightTable hltbl=new HighlightTable();
        this.getLastSubTask().setHighlight(hltbl);
        
    	this.getLastSubTask().addDifferenceFromOrigin(difference);
    }
    
    private boolean checkIfSigmaExprIsInGamma(int toChange, CubeQuery newQuery) {
			boolean ret_value=false;
			String [] tmp=newQuery.getSigmaExpressions().get(toChange)[0].split("\\.");
			for(String [] gammaExpr : newQuery.getGammaExpressions()){
				if(gammaExpr[0].equals(tmp[0])) ret_value=true; 
			}
			return ret_value;
	}

    private int getGammaPositionOfSigma(int toChange, CubeQuery newQuery) {
		int ret_value=0;
		String [] tmp=newQuery.getSigmaExpressions().get(toChange)[0].split("\\.");
		for(int i=0; i< newQuery.getGammaExpressions().size();i++){
			String [] gammaExpr=newQuery.getGammaExpressions().get(i);
			if(gammaExpr[0].equals(tmp[0])) {
				ret_value=i;
				break;
			}
		}
		return ret_value;
    }
	
	boolean tryParseInt(String value){
		boolean ret_value=true;
	    try{
	    	Integer.parseInt(value);
	    }catch(NumberFormatException nfe){  
	          ret_value=false;
	    }  
	     	return ret_value;  
	}

	
	void copyListofArrayString(ArrayList<String[]> from,ArrayList<String[]> to){
		for(int i=0;i<from.size();i++){
			String[] old=from.get(i);
			String[] toadd=new String[old.length];
			for(int j=0;j<old.length;j++){
				toadd[j]=old[j];
			}
			to.add(toadd);
		}
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
}
