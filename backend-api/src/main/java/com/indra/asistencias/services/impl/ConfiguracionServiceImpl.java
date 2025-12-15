package com.indra.asistencias.services.impl;

import com.indra.asistencias.models.Configuracion;
import com.indra.asistencias.repositories.ConfiguracionRepository;
import com.indra.asistencias.services.IConfiguracionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConfiguracionServiceImpl implements IConfiguracionService {

    private final ConfiguracionRepository configRepository;

    @Override
    @Cacheable(value = "sys_config")
    public List<Configuracion> listarConfiguracion() {
        return configRepository.findAll();
    }

    @Override
    @Transactional
    @CacheEvict(value = "sys_config", allEntries = true)
    public void actualizarConfiguracion(List<Map<String, String>> cambis) {
        for (Map<String, String> item : cambis) {
            String clave = item.get("clave");
            String valor = item.get("valor");

            Configuracion conf = configRepository.findByClave(clave)
                    .orElseThrow(() -> new RuntimeException("Clave no encontrada: " + clave));

            conf.setValor(valor);
            configRepository.save(conf);
        }
    }
}