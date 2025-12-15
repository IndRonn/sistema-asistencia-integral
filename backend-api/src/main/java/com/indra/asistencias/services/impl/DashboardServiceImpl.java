package com.indra.asistencias.services.impl;

import com.indra.asistencias.dto.dashboard.EstadisticaSemanalDto;
import com.indra.asistencias.dto.dashboard.KpiResumenDto;
import com.indra.asistencias.dto.dashboard.MonitorVivoDto;
import com.indra.asistencias.repositories.DashboardRepository;
import com.indra.asistencias.services.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final DashboardRepository dashboardRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MonitorVivoDto> getMonitorEnVivo() {
        return dashboardRepository.obtenerEnVivo();
    }

    @Override
    @Transactional(readOnly = true)
    public KpiResumenDto getKpis() {
        return dashboardRepository.obtenerKpisDia();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstadisticaSemanalDto> getEstadisticasSemanales() {
        return dashboardRepository.obtenerEstadisticasSemanales();
    }
}