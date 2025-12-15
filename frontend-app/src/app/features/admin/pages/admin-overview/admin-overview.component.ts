import { Component, OnInit, OnDestroy, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { interval, Subscription, startWith, switchMap } from 'rxjs';

import { AdminService } from '@core/services/admin/admin.service';
import { DashboardService } from '@core/services/dashboard/dashboard.service';

import { JustificacionPendiente } from '@core/models/admin.model';
import { KpiResumen, MonitorVivo } from '@core/models/dashboard.model';

import { UiBadgeComponent, BadgeVariant } from '@shared/components/ui-badge/ui-badge.component';
import { ResolutionModalComponent, ResolutionAction } from '../../components/resolution-modal/resolution-modal.component';

import { NgxEchartsModule } from 'ngx-echarts';
import { EChartsOption } from 'echarts';
import { getWeeklyChartOption } from '../../configs/admin-charts.config';

@Component({
  selector: 'app-admin-overview',
  standalone: true,
  imports: [
    CommonModule,
    UiBadgeComponent,
    ResolutionModalComponent,
    NgxEchartsModule
  ],
  templateUrl: './admin-overview.component.html',
  styleUrls: ['./admin-overview.component.css']
})
export class AdminOverviewComponent implements OnInit, OnDestroy {
  private adminService = inject(AdminService);
  private dashboardService = inject(DashboardService);

  @ViewChild(ResolutionModalComponent) modal!: ResolutionModalComponent;


  kpis = signal<KpiResumen | null>(null);
  monitorVivo = signal<MonitorVivo[]>([]);


  chartOption = signal<EChartsOption | null>(null);


  pendientes = signal<JustificacionPendiente[]>([]);
  isLoading = signal<boolean>(true);


  private pollingSubscription!: Subscription;

  ngOnInit() {
    this.loadInbox();
    this.startLiveMonitor();
    this.loadHistory();
  }

  ngOnDestroy() {

    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }
  }


  loadInbox() {
    this.isLoading.set(true);
    this.adminService.getPendientes().subscribe({
      next: (data) => {
        this.pendientes.set(data);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }


  startLiveMonitor() {
    this.pollingSubscription = interval(30000).pipe(
      startWith(0),
      switchMap(() => {
        this.dashboardService.getKpis().subscribe({
          next: (res) => this.kpis.set(res),
          error: (e) => console.error('Fallo en KPIs', e)
        });
        return this.dashboardService.getMonitorVivo();
      })
    ).subscribe({
      next: (vivo) => this.monitorVivo.set(vivo),
      error: (err) => console.error('Error en monitor vivo', err)
    });
  }


  loadHistory() {
    this.dashboardService.getHistoricoSemanal().subscribe({
      next: (data) => {

        const dias = data.map(d => d.dia);
        const puntuales = data.map(d => d.puntuales);
        const tardanzas = data.map(d => d.tardanzas);
        const faltas = data.map(d => d.faltas);


        const option = getWeeklyChartOption(dias, puntuales, tardanzas, faltas);


        this.chartOption.set(option);
      },
      error: (err) => console.error('Error cargando gráfico semanal:', err)
    });
  }


  get pieChartStyle() {
    const kpi = this.kpis();
    if (!kpi || kpi.presentes === 0) return { 'background': '#333' };

    const totalAsistentes = kpi.presentes;
    const pctPuntuales = (kpi.puntuales / totalAsistentes) * 100;

    return {
      'background': `conic-gradient(#4ade80 ${pctPuntuales}%, #facc15 0)`
    };
  }


  openDecision(id: number, action: ResolutionAction) {
    if (this.modal) {
      this.modal.open(id, action);
    }
  }

  onResolved(idResuelto: number) {
    this.pendientes.update(list => list.filter(item => item.idJustificacion !== idResuelto));
  }

  getVariant(tipo: string): BadgeVariant {
    if (tipo === 'SALUD') return 'danger';
    if (tipo === 'TRABAJO') return 'warning';
    return 'neutral';
  }
}
