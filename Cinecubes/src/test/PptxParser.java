package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFNotes;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

public class PptxParser {

	public PptxParser() { }
	
	public static void main(final String[] args) {
		String[] fileName = getFilesName();
		int count =0;
		for (int i = 0; i < fileName.length; i++) {
			if (fileName[i].isEmpty()) continue;
			String oldFileName = "TestFiles/" + fileName[i].replace(".pptx",  "A.pptx") ;
			String newFileName = "OutputFiles/" +fileName[i];
			FileInputStream oldInputStream;
			FileInputStream newInputStream;

			try {
				oldInputStream = new FileInputStream(oldFileName);
				newInputStream = new FileInputStream(newFileName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}

			XMLSlideShow oldPpt;
			XMLSlideShow newPpt;

			try {
				oldPpt = new XMLSlideShow(oldInputStream);
				newPpt = new XMLSlideShow(newInputStream);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			System.out.println( count++ +") Compare " + fileName[i].replace(".pptx",  "A.pptx") + " with " + fileName[i] );
			System.out.println("Slides : ");
			compareSlidesPPT(oldPpt, newPpt);
			System.out.println("Notes : ");
			compareNotesPPT(oldPpt, newPpt);
			System.out.println("End\n ");
		}
	}

	public static void compareSlidesPPT(final XMLSlideShow oldPpt,
			final XMLSlideShow newPpt) {
		String oldSlides = readPPT(oldPpt);
		String newSlides = readPPT(newPpt);
		if (newSlides.equals(oldSlides)) {
			System.out.println("Slides is same");
		} else {
			System.out.println("Slides is not same");
		}
	}

	public static String readPPT(final XMLSlideShow ppt) {
		String slides = "";
		for (XSLFSlide slide : ppt.getSlides()) {
			XSLFShape[] shapes = slide.getShapes();
			for (XSLFShape shape : shapes) {
				if (shape instanceof XSLFTextShape) {
					XSLFTextShape textShape = (XSLFTextShape) shape;
					String text = textShape.getText();
					slides += text + "\n";
				}
			}
		}
		// System.out.println("Text: " + slides);
		return slides;
	}

	public static void compareNotesPPT(final XMLSlideShow oldPpt, 
			final XMLSlideShow newPpt) {
		String oldSlides = readNotesPPT(oldPpt);
		String newSlides = readNotesPPT(newPpt);
		if (newSlides.equals(oldSlides)) {
			System.out.println("Notes is same");
		} else {
			System.out.println("Notes is not same");
		}
	}

	public static String readNotesPPT(final XMLSlideShow ppt) {
		String notes = "";
		XSLFSlide[] slide = ppt.getSlides();
		for (int i = 0; i < slide.length; i++) {
			try {
				XSLFNotes note = slide[i].getNotes();
				for (XSLFShape shape : note) {
					if (shape instanceof XSLFTextShape) {
						XSLFTextShape txtShape = (XSLFTextShape) shape;
						for (XSLFTextParagraph xslfParagraph : txtShape
								.getTextParagraphs()) {
							notes += xslfParagraph.getText() + "\n";
							// System.out.println(xslfParagraph.getText());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return notes;
	}

	 public static String[] getFilesName() {
	      File dir = new File("OutputFiles/");
	      String[] files = dir.list();
	      if (files == null) 
	          return null;
	      for (int i = 0; i < files.length; i++)
	          if (files[i].contains(".docx"))
	        	  files[i] = "";
	      return files;
	  }
}