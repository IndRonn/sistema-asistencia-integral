package com.indra.asistencias.dto.asistencia;

import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Data
@Builder
public class MarcaRespuestaDto implements Serializable {
    private String mensaje;
    private String tipoMarca;
    private String horaExacta;
    private String estadoAsistencia;
}