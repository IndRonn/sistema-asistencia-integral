import { EChartsOption } from 'echarts';

/**
 * Genera la configuración visual para el gráfico semanal.
 * Recibe solo los datos puros y devuelve el objeto complejo de ECharts.
 */
export const getWeeklyChartOption = (
  dias: string[],
  puntuales: number[],
  tardanzas: number[],
  faltas: number[]
): EChartsOption => {

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(0,0,0,0.9)', // Fondo más oscuro
      borderColor: '#333',
      textStyle: { color: '#fff' },
      formatter: (params: any) => {
        // Personalización del tooltip (opcional)
        let tooltip = `<div class="font-bold mb-1">${params[0].name}</div>`;
        params.forEach((item: any) => {
          tooltip += `
            <div class="flex items-center gap-2 text-xs">
              <span style="display:inline-block;width:8px;height:8px;border-radius:50%;background-color:${item.color}"></span>
              <span class="w-16">${item.seriesName}:</span>
              <span class="font-mono font-bold">${item.value}</span>
            </div>`;
        });
        return tooltip;
      }
    },
    legend: {
      data: ['Puntual', 'Tardanza', 'Ausente'],
      textStyle: { color: '#9ca3af' }, // text-gray-400
      bottom: 0,
      itemGap: 20
    },
    grid: {
      left: '3%', right: '4%', bottom: '10%', top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dias,
      axisLine: { lineStyle: { color: '#4b5563' } },
      axisLabel: { color: '#9ca3af', fontWeight: 'bold' }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#333', type: 'dashed' } }, // Líneas punteadas sutiles
      axisLabel: { color: '#6b7280' }
    },
    series: [
      {
        name: 'Puntual',
        type: 'bar',
        stack: 'total',
        barWidth: '40%', // Barras más delgadas y elegantes
        itemStyle: { borderRadius: [0, 0, 0, 0] },
        color: '#4ade80', // green-400
        data: puntuales
      },
      {
        name: 'Tardanza',
        type: 'bar',
        stack: 'total',
        color: '#facc15', // yellow-400
        data: tardanzas
      },
      {
        name: 'Ausente',
        type: 'bar',
        stack: 'total',
        itemStyle: { borderRadius: [4, 4, 0, 0] }, // Redondeado solo arriba
        color: '#ef4444', // red-500
        data: faltas
      }
    ]
  };
};
