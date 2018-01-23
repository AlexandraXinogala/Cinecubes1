package storymgr;

import java.util.ArrayList;

import AudioMgr.Audio;
import HighlightMgr.Highlight;
import TaskMgr.SubTask;

public abstract class Episode {
    
    protected ArrayList<SubTask> subTask;
    protected Visual visual;   
    protected Audio audio;
    protected ArrayList<Highlight> highlight;
    
    public Episode(){    	
    	audio=new Audio();
    	subTask=new ArrayList<SubTask>();
    	setHighlight(new ArrayList<Highlight>());
    }
	
	public ArrayList<SubTask> getSubTasks() {
		return subTask;
	}

	public void setSubTasks(ArrayList<SubTask> subtask) {
		subTask=subtask;
	}

	public void addSubTask(SubTask subtask) {
		subTask.add(subtask);
	}
	
	abstract public void setVisual(Visual vis);

	public ArrayList<Highlight> getHighlight() {
		return highlight;
	}

	public void setHighlight(ArrayList<Highlight> highlight) {
		this.highlight = highlight;
	}

	public String getHighlightMaxValue(){
		String maxText = "";
		if(highlight.get(0).semanticValue.size()>1)
    		maxText+="s";
		maxText += " with " + highlight.get(0).maxcolor_name + " "; 
		return maxText;
	}
	
	public String getHighlightMinValue(){
		String minText = "";
		if(highlight.get(1).semanticValue.size()>1)
    		minText += "s";
		minText += " with " + highlight.get(0).mincolor_name + " color. "; 
		return minText;
	}
	
	public boolean checkDifferenceFromOrigin(int value){
		return subTask.get(0).getDifferenceFromOrigin(0) == value;
	}
	
	public boolean checkCountOfSubTask(){
		return subTask.get(0).getDifferencesFromOrigin().size() > 1;
	}
	
}
