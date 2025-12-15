package com.indra.asistencias.services;

import com.indra.asistencias.dto.dashboard.EstadisticaSemanalDto;
import com.indra.asistencias.dto.dashboard.KpiResumenDto;
import com.indra.asistencias.dto.dashboard.MonitorVivoDto;


import java.util.List;

public interface IDashboardService {

    List<MonitorVivoDto> getMonitorEnVivo();
    KpiResumenDto getKpis();

    List<EstadisticaSemanalDto> getEstadisticasSemanales();
}
