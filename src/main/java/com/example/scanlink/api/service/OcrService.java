package com.example.scanlink.api.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
@Service
public class OcrService {

    private Tesseract buildTesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata");
        tesseract.setLanguage("vie+eng");
        return tesseract;
    }

    public String extractText(File imageFile) throws TesseractException {
        return buildTesseract().doOCR(imageFile);
    }

    /**
     * Tạo searchable PDF từ ảnh (PDF có layer text ẩn bên dưới)
     * Output file sẽ được lưu tại outputPath
     */
    public File createSearchablePdf(File imageFile, String outputPath)
            throws TesseractException, IOException {

        Tesseract tesseract = buildTesseract();

        // Tess4J nhận list file + output path (không cần đuôi .pdf)
        String outputBase = outputPath.endsWith(".pdf")
                ? outputPath.substring(0, outputPath.length() - 4)
                : outputPath;

        tesseract.createDocuments(
                imageFile.getAbsolutePath(),  // input image path
                outputBase,                   // output base path (tess tự thêm .pdf)
                java.util.List.of(net.sourceforge.tess4j.ITesseract.RenderedFormat.PDF)
        );

        File pdfFile = new File(outputBase + ".pdf");

        if (!pdfFile.exists()) {
            throw new IOException("Tạo searchable PDF thất bại: " + pdfFile.getAbsolutePath());
        }

        return pdfFile;
    }

    /**
     * Overload: tự động tạo file tạm trong temp directory
     */
    public File createSearchablePdf(File imageFile)
            throws TesseractException, IOException {

        Path tempDir = Files.createTempDirectory("ocr_pdf_");
        String outputPath = tempDir.resolve("output").toString();

        return createSearchablePdf(imageFile, outputPath);
    }

}
