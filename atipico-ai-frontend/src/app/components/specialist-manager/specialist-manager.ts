import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SpecialistService } from '../../services/specialist.service';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { ConfirmationService, MessageService } from 'primeng/api';

@Component({
  selector: 'app-specialist-manager',
  standalone: true,
  imports: [CommonModule, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './specialist-manager.html',
  styleUrl: './specialist-manager.scss'
})
export class SpecialistManagerComponent implements OnInit {
  private specialistService = inject(SpecialistService);
  private confirmationService = inject(ConfirmationService);
  private messageService = inject(MessageService);

  specialists: any[] = [];
  displayModal = false;

  novoSpecialist: any = {
    name: '',
    category: '',
    contactInfo: ''
  };

  ngOnInit(): void {
    this.carregarEspecialistas();
  }

  carregarEspecialistas(): void {
  this.specialistService.listarEspecialistas().subscribe({
    next: (data) => {
      this.specialists = data;
    },
    error: (err) => {
      console.error('Erro ao carregar especialistas:', err);

      // Extrai a mensagem do backend, se houver, ou exibe uma padrão
      const erroBackend = err.error;
      const mensagemErro = erroBackend?.message || 'Não foi possível carregar a lista de especialistas. Tente novamente mais tarde.';

      // Exibe o Toast de erro no meio/topo da tela
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
    this.novoSpecialist = { name: '', category: '', contactInfo: '' };
    this.displayModal = true;
  }

  salvarEspecialista(): void {
    this.specialistService.salvarEspecialista(this.novoSpecialist).subscribe({
      next: () => {
        this.displayModal = false;
        this.carregarEspecialistas();
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Especialista salvo com sucesso.'
        });
      },
      error: (err) => {
        console.error('Erro ao salvar especialista', err);
        const erroBackend = err.error;
        const mensagemErro = erroBackend?.message || 'Erro desconhecido ao salvar especialista.';
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: mensagemErro
        });
      }
    });
  }

  excluirEspecialista(id: number): void {
    this.confirmationService.confirm({
      message: 'Deseja realmente excluir este especialista?',
      header: 'Confirmação de Exclusão',
      icon: 'pi pi-exclamation-triangle',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-text',
      acceptLabel: 'Sim, excluir',
      rejectLabel: 'Cancelar',
      accept: () => {
        this.specialistService.excluirEspecialista(id).subscribe({
          next: () => {
            this.carregarEspecialistas();
            this.messageService.add({
              severity: 'success',
              summary: 'Excluído',
              detail: 'Especialista removido com sucesso.'
            });
          },
          error: (err) => {
            console.error('Erro ao excluir especialista', err);
            const erroBackend = err.error;
            const mensagemErro = erroBackend?.message || 'Erro desconhecido ao excluir especialista.';
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