package com.example.scanlink.api.service.imp;

import com.example.scanlink.api.service.interfaces.CreateFileOutput;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;

public class FilePdf implements CreateFileOutput {
    @Override
    public File CreateFile(String text) throws Exception {
        String path = "output.pdf";

        PdfWriter writer = new PdfWriter(path);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph(text));

        document.close();

        return new File(path);
    }
}
