import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PatientService } from '../../services/patient.service';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { ConfirmationService, MessageService } from 'primeng/api';

@Component({
  selector: 'app-patient-manager',
  standalone: true,
  imports: [CommonModule, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './patient-manager.html',
  styleUrl: './patient-manager.scss'
})
export class PatientManagerComponent implements OnInit {
  private patientService = inject(PatientService);
  private confirmationService = inject(ConfirmationService);
  private messageService = inject(MessageService);

  patients: any[] = [];
  displayModal = false;
  cidsInput = ''; 
  
  novoPaciente: any = {
    name: '',
    birthDate: '',
    cids: [],
    notes: ''
  };

  ngOnInit(): void {
    this.carregarPacientes();
  }

  carregarPacientes(): void {
    this.patientService.listarPacientes().subscribe({
      next: (data) => this.patients = data,
      error: (err) => {
        console.error('Erro ao carregar pacientes', err);
        const erroBackend = err.error;
        const mensagemErro = erroBackend?.message || 'Não foi possível carregar a lista de pacientes. Tente novamente mais tarde.';

        this.messageService.add({
          severity: 'error',
          summary: 'Erro de Carregamento',
          detail: mensagemErro,
          life: 6000
        });
      }
    });
  }

  abrirModalNovo(): void {
    this.novoPaciente = { name: '', birthDate: '', cids: [], notes: '' };
    this.cidsInput = '';
    this.displayModal = true;
  }

  salvarPaciente(): void {
    this.novoPaciente.cids = this.cidsInput
      ? this.cidsInput.split(',').map(c => c.trim()).filter(c => c.length > 0)
      : [];

    this.patientService.salvarPaciente(this.novoPaciente).subscribe({
      next: () => {
        this.displayModal = false;
        this.carregarPacientes();
        this.messageService.add({ 
          severity: 'success', 
          summary: 'Sucesso', 
          detail: 'Paciente salvo com sucesso.' 
        }); 
      },
      error: (err) => {
        console.error('Erro ao salvar paciente', err);
        const erroBackend = err.error;
        const mensagemErro = erroBackend?.message || 'Erro desconhecido ao salvar paciente.';
        this.messageService.add({ 
          severity: 'error', 
          summary: 'Erro', 
          detail: mensagemErro 
        });
      }
    });
  }

  excluirPaciente(id: number): void {
    this.confirmationService.confirm({
      message: 'Deseja realmente excluir este paciente e seus vínculos?',
      header: 'Confirmação de Exclusão',
      icon: 'pi pi-exclamation-triangle',
      acceptButtonStyleClass: 'p-button-danger', // Deixa o botão de confirmar vermelho
    rejectButtonStyleClass: 'p-button-text',   // Deixa o botão de cancelar discreto
    acceptLabel: 'Sim, excluir',
    rejectLabel: 'Cancelar',
    accept: () => {
        this.patientService.excluirPaciente(id).subscribe({
          next: () => {
            this.carregarPacientes();
            this.messageService.add({ 
              severity: 'success', 
              summary: 'Excluído', 
              detail: 'Documento removido com sucesso.' 
          });
        },
          error: (err) => {
            console.error('Erro ao excluir paciente:', err);
            const erroBackend = err.error;
            const mensagemErro = erroBackend?.message || 'Erro desconhecido ao excluir paciente.';
            this.messageService.add({ 
              severity: 'error', 
              summary: 'Erro',
              detail: mensagemErro
            });
          }
        });
    },
    reject: () => {
      // Opcional: Ação ao cancelar (se necessário)
    }
  });
}

}
