package com.example.scanlink.api.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;

public class FileWord implements CreateFileOutput {
    @Override
    public File CreateFile(String text) throws Exception {
        XWPFDocument document =
                new XWPFDocument();

        XWPFParagraph paragraph =
                document.createParagraph();

        XWPFRun run =
                paragraph.createRun();

        run.setText(text);

        FileOutputStream out =
                new FileOutputStream("output.docx");

        document.write(out);

        out.close();
        document.close();

        return new File("output.docx");
    }
}
