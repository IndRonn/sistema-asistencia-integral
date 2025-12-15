package com.indra.asistencias.services;

import com.indra.asistencias.models.Configuracion;
import java.util.List;
import java.util.Map;

public interface IConfiguracionService {


    List<Configuracion> listarConfiguracion();


    void actualizarConfiguracion(List<Map<String, String>> cambios);
}