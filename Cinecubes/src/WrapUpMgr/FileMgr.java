package WrapUpMgr;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;

import storymgr.Act;
import storymgr.FinalResult;
import storymgr.PptxSlide;
import storymgr.Story;
import storymgr.Tabular;

public abstract class FileMgr {
    /**
	 * @uml.property  name="finalResult"
	 * @uml.associationEnd  
	 */
    protected FinalResult finalResult;
     
	public FinalResult getFinalResult() {
		return finalResult;
	}

	public void setFinalResult(FinalResult finalresult) {
		finalResult = finalresult;
	}
   
	public void createFile(Story story){
		int slide_so_far_created = 0;
		for (Act actItem : story.getActs()) {
			if (actItem.getId() == 0) {
				
				PptxSlide slide = (PptxSlide) actItem.getEpisodes().get(0);
				slide.subTimeCreationText(System.nanoTime());
					
				createIntroSlide(slide,slide_so_far_created);
				slide.subTimeCreationText(System.nanoTime());
				slide_so_far_created += actItem.getEpisodes().size();
			} else if (actItem.getId() == -1) {
				PptxSlide slide = (PptxSlide) actItem.getEpisodes().get(0);
				slide.setTimeCreationText(System.nanoTime());
				createSummarySlide(slide, slide_so_far_created + 2);
				slide.subTimeCreationText(System.nanoTime());
				slide_so_far_created += actItem.getEpisodes().size();
			} else if (actItem.getEpisodes().size() > 1
					|| actItem.getId() == 20) {
				for (int j = 0; j < actItem.getEpisodes().size(); j++) {
					
					PptxSlide slide = (PptxSlide) actItem.getEpisodes().get(j);

					slide.setTimeCreationText(System.nanoTime());

					if (slide.getTitle().contains("Act"))
						createNewSlide(null, null, slide.getAudio()
								.getFileName(), slide.getTitle(), j
								+ slide_so_far_created + 2, null, null,
								slide.getSubTitle(), null,
								(actItem.getId() == 3 ? 0 : 1));
					else if (slide.getNotes().length() == 0) {
						Tabular tmp_tbl = ((Tabular) slide.getVisual());
						createNewSlide(slide.getVisual().getPivotTable(),
								tmp_tbl.colortable, null, slide.getTitle(), j
										+ slide_so_far_created + 2, null, null,
								slide.getSubTitle(),
								(Tabular) slide.getVisual(),
								(actItem.getId() == 3 ? 0 : 1));
					} else {
						Tabular tmp_tbl = ((Tabular) slide.getVisual());
						createNewSlide(slide.getVisual().getPivotTable(),
								tmp_tbl.colortable, slide.getAudio()
										.getFileName(), slide.getTitle(), j
										+ slide_so_far_created + 2,
								slide.getTitleColumn(), slide.getTitleRow(),
								slide.getSubTitle(),
								(Tabular) slide.getVisual(),
								(actItem.getId() == 3 ? 0 : 1));
						addNotesOnSlide(slide.getNotes());
					}
					slide.subTimeCreationText(System.nanoTime());
				}
				slide_so_far_created += actItem.getEpisodes().size();
			}
		}
		

		FileOutputStream fout;
		try {
			fout = new FileOutputStream(this.finalResult.getFilename());
			writeOutput(fout);
			fout.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	 }

		abstract protected void createNewSlide(String[][] table, Color[][] colorTable,
				String AudioFilename, String Title, int slideid,
				String titleColumn, String titleRow, String subtitle,
				Tabular tabular, int hide_slide);
		
		abstract protected void addNotesOnSlide(String notes); 
		
		abstract protected void createIntroSlide(PptxSlide episode, int slide_so_far_created);
		
		abstract protected void createSummarySlide(PptxSlide episode, int slideId);
		
		abstract protected void writeOutput(FileOutputStream fout) throws IOException;


}
