package com.indra.asistencias.services;

import com.indra.asistencias.dto.reportes.ReporteAsistenciaDto;
import java.io.IOException;
import java.util.List;

public interface IExportService {


    byte[] generarExcelReporte(List<ReporteAsistenciaDto> datos) throws IOException;

    byte[] generarPdfReporte(List<ReporteAsistenciaDto> datos) throws IOException;
}