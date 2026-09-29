import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportService } from '../../services/report.service';
import { SpecialistSummary } from '../../models/specialist.model';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { ProgressSpinnerModule } from 'primeng/progressspinner';

@Component({
  selector: 'app-report-preview',
  standalone: true,
  imports: [CommonModule, CardModule, ButtonModule, ProgressSpinnerModule],
  templateUrl: './report-preview.html',
  styleUrl: './report-preview.scss'
})
export class ReportPreviewComponent implements OnInit {
  private reportService = inject(ReportService);

  resumos = signal<SpecialistSummary[]>([]);
  carregando = signal<boolean>(true);
  patientId: number = 1; // Substitua pelo ID do paciente ativo no contexto

  ngOnInit(): void {
    this.carregarPreview();
  }

  carregarPreview(): void {
    this.carregando.set(true);
    this.reportService.obterPreviewResumos(this.patientId).subscribe({
      next: (data) => {
        this.resumos.set(data);
        this.carregando.set(false);
      },
      error: (err) => {
        console.error('Erro ao carregar preview dos resumos:', err);
        this.carregando.set(false);
      }
    });
  }

  baixarPdf(): void {
    this.reportService.gerarRelatorioPdf(this.patientId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `relatorio-clinico-paciente-${this.patientId}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Erro ao baixar o PDF:', err);
      }
    });
  }
}