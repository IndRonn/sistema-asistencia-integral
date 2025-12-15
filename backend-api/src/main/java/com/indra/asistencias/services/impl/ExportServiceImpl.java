package com.indra.asistencias.services.impl;

import com.indra.asistencias.dto.reportes.ReporteAsistenciaDto;
import com.indra.asistencias.services.IExportService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color; // Color para PDF
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExportServiceImpl implements IExportService {

    @Override
    public byte[] generarExcelReporte(List<ReporteAsistenciaDto> datos) throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reporte de Asistencia");

            CellStyle headerStyle = workbook.createCellStyle();

            org.apache.poi.ss.usermodel.Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);
            String[] columnas = {"Usuario", "Nombre Completo", "Fecha", "Entrada", "Salida", "Estado", "Justificación"};

            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }


            int rowIdx = 1;
            for (ReporteAsistenciaDto item : datos) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(item.getUsername());
                row.createCell(1).setCellValue(item.getNombreCompleto());
                row.createCell(2).setCellValue(item.getFecha().toString());
                row.createCell(3).setCellValue(item.getHoraEntrada());
                row.createCell(4).setCellValue(item.getHoraSalida());
                row.createCell(5).setCellValue(item.getEstadoDescripcion());
                row.createCell(6).setCellValue(item.getJustificacion() != null ? item.getJustificacion() : "-");
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Override
    public byte[] generarPdfReporte(List<ReporteAsistenciaDto> datos) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // 1. Crear Documento
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);

            document.open();

            // 2. Título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Paragraph title = new Paragraph("Reporte de Asistencias", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // 3. Tabla (7 Columnas)
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2f, 4f, 2f, 2f, 2f, 2f, 4f});

            // 4. Encabezados
            String[] headers = {"Usuario", "Nombre", "Fecha", "Entrada", "Salida", "Estado", "Justif."};
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(Color.DARK_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // 5. Datos
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

            for (ReporteAsistenciaDto item : datos) {
                addCell(table, item.getUsername(), dataFont);
                addCell(table, item.getNombreCompleto(), dataFont);
                addCell(table, item.getFecha().toString(), dataFont);
                addCell(table, item.getHoraEntrada(), dataFont);
                addCell(table, item.getHoraSalida() != null ? item.getHoraSalida() : "-", dataFont);

                // Color condicional para el Estado
                PdfPCell stateCell = new PdfPCell(new Phrase(item.getEstadoDescripcion(), dataFont));

                // Lógica visual de alertas
                if ("TARDANZA".equals(item.getEstadoDescripcion())) {
                    stateCell.setBackgroundColor(new Color(255, 230, 153)); // Amarillo
                } else if ("AUSENTE".equals(item.getEstadoDescripcion())) {
                    stateCell.setBackgroundColor(new Color(248, 203, 173)); // Rojo
                } else {
                    stateCell.setBackgroundColor(Color.WHITE); // Limpiar color por si acaso
                }

                stateCell.setPadding(4); // Padding también para la celda de estado
                table.addCell(stateCell);

                addCell(table, item.getJustificacion() != null ? item.getJustificacion() : "", dataFont);
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (DocumentException e) {
            throw new IOException("Error generando PDF", e);
        }
    }

    // Helper para celdas simples
    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(4);
        table.addCell(cell);
    }
}