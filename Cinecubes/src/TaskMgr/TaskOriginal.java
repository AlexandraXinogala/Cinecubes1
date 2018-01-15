package TaskMgr;

import storymgr.Act;
import storymgr.PptxSlide;
import storymgr.Tabular;
import CubeMgr.CubeBase.CubeBase;
import CubeMgr.CubeBase.CubeQuery;
import CubeMgr.StarSchema.SqlQuery;
import HighlightMgr.HighlightDominationColumn;
import HighlightMgr.HighlightDominationRow;
import HighlightMgr.HighlightMax;
import HighlightMgr.HighlightMin;
import HighlightMgr.HighlightTable;

public class TaskOriginal extends Task {

	public TaskOriginal() {
		super();
	}

	@Override
	public void generateSubTasks(CubeBase cubeBase) {
		this.addNewSubTask();
		SqlQuery newSqlQuery = new SqlQuery();
        long time_produce_original = System.nanoTime();
        newSqlQuery.produceExtractionMethod(this.cubeQuery.get(0));
        time_produce_original = System.nanoTime() - time_produce_original;
        this.getLastSubTask().setTimeProduceOfExtractionMethod (time_produce_original,0);
        this.getLastSubTask().setExtractionMethod(newSqlQuery);
        this.getLastSubTask().execute(cubeBase.getDatabase());
        this.cubeQuery.get(0).setSqlQuery(newSqlQuery);
	}

	@Override
	public void constructActEpidoses(Act currentAct) {
		PptxSlide newSlide = new PptxSlide();
		SubTask subtsk = getSubTask(0);
		CubeQuery currentCubeQuery = cubeQuery.get(0);
    	Tabular tbl = new Tabular();
    	String[] extraPivot = new String[2];
    	 if(subtsk.getHighlight()==null)
    		 subtsk.setHighlight(new HighlightTable());

    	HighlightMin hlmin = new HighlightMin();
    	HighlightMax hlmax = new HighlightMax();
    	HighlightDominationRow hldomrow = new HighlightDominationRow();
    	HighlightDominationColumn hldomcol = new HighlightDominationColumn();
	    newSlide.addSubTask(subtsk);
	    newSlide.getHighlight().add(hlmin);
	    newSlide.getHighlight().add(hlmax);
	    newSlide.getHighlight().add(hldomcol);
	    newSlide.getHighlight().add(hldomrow);
    	newSlide.addCubeQuery(currentCubeQuery);
    	
        newSlide.setVisual(tbl);
        
        
        extraPivot[0] = "";
        extraPivot[1] = "";
		
	    /*====== Compute Pivot Table =======*/
    	newSlide.setTimeCreationTabular(System.nanoTime());
	    tbl.CreatePivotTable(subtsk.getExtractionMethod().getRowPivot(),
	    		 			  subtsk.getExtractionMethod().getColPivot(),
	    		 			  subtsk.getExtractionMethod().getResultArray(),
	    		 			  extraPivot);
	    newSlide.subTimeCreationTabular(System.nanoTime());
	    currentAct.addEpisode(newSlide);
	    
	    /*====== Calculate Highlioghts =======*/
    	newSlide.setTimeComputeHighlights(System.nanoTime());
    	hlmin.execute(subtsk.getExtractionMethod().getResultArray());
    	hlmax.execute(subtsk.getExtractionMethod().getResultArray());
    	
    	hldomcol.semanticValue = hlmax.semanticValue;
    	hldomcol.helpValues2 = hlmin.semanticValue;
    	hldomcol.execute(tbl.getPivotTable());
    	
    	hldomrow.semanticValue = hlmax.semanticValue;
    	hldomrow.helpValues2 = hlmin.semanticValue;
    	hldomrow.execute(tbl.getPivotTable());
    	newSlide.subTimeComputeHighlights(System.nanoTime());
    	
    	/*====== Compute Color Table =======*/
    	newSlide.computeColorTable(tbl); 
    	/*====== Calculate domination Highlioghts =======*/
    	long start_creation_domination = System.nanoTime();
		newSlide.addTimeComputeHighlights(System.nanoTime() - start_creation_domination);
	}

}
