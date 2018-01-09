package test;

import java.io.FileInputStream;
import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class DocParser {

	public DocParser() { }
	
	public static void main(final String[] args) {
		for (int i = 1; i < 4; i++) {
			String oldText = readDocxFile("TestFiles/CubeQueryLoan" 
		+ i + "A.docx");
			String newText = readDocxFile("OutputFiles/CubeQueryLoan"
		+ i + ".docx");
			compareWordFile(oldText, newText);
			System.out.println("End\n ");
		}
	}

    public static String readDocxFile(final String fileName) {
    	String text = "";
    	try {
            FileInputStream fis = new FileInputStream(fileName);
            XWPFDocument document = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph para : paragraphs) {
            	text += para.getText() + "\n";
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public static void compareWordFile(final String oldText, 
    		final String newText) {
    	if (newText.equals(oldText)) {
			System.out.println("Text is same");
		} else {
			System.out.println("Text is not same");
		}
    }
    
    
}