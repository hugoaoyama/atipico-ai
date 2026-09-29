import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DocumentService } from '../../services/document.service';
import { DocumentDetailsResponse } from '../../models/document.model';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { DialogModule } from 'primeng/dialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { PatientService } from '../../services/patient.service';
import { Patient } from '../../models/patient.model';
import { Specialist } from '../../models/specialist.model';
import { SpecialistService } from '../../services/specialist.service';

@Component({
  selector: 'app-document-manager',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ToastModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    SelectModule,
    DialogModule,
    ConfirmDialogModule
  ],
  templateUrl: './document-manager.html',
  styleUrl: './document-manager.scss',
  providers: [MessageService, ConfirmationService]
})
export class DocumentManagerComponent implements OnInit {
  private documentService = inject(DocumentService);
  private patientService = inject(PatientService);
  private specialistService = inject(SpecialistService);
  private messageService: MessageService = inject(MessageService);
  private confirmationService: ConfirmationService = inject(ConfirmationService);

  documents = signal<DocumentDetailsResponse[]>([]);
  isLoading = signal<boolean>(false);
  patients: Patient[] = [];
  specialists: Specialist[] = [];

  // Modais
  displayResumoModal = false;
  displayUploadModal = false;
  isUploading = false;
  
  resumoAtual = '';
  documentoSelecionado: DocumentDetailsResponse | null = null;

  // Objeto do formulário de upload
  uploadForm: any = {
    title: '',
    documentType: '',
    documentDate: '',
    patientId: null, // Ajuste conforme o paciente selecionado na tela
    specialistId: null // Ajuste conforme o especialista selecionado na tela
  };
  selectedFile: File | null = null;

  patientId: number = 1;
  specialistId: number =0;
  selectedDocumentType: string = '';
  startDate: string = '';
  endDate: string = '';

  documentTypes = [
    { label: 'Todos os tipos', value: '' },
    { label: 'Laudo', value: 'Laudo' },
    { label: 'Exame', value: 'Exame' },
    { label: 'Receita', value: 'Receita' }
  ];

  ngOnInit(): void {
    this.carregarPacientes();
    this.carregarDocumentos();
    this.carregarEspecialistas();
  }

  carregarEspecialistas(): void {
    this.specialistService.listarEspecialistas().subscribe({
      next: (data) => this.specialists = data,
      error: (err) => console.error('Erro ao carregar especialistas', err)
    });
  }

  // Carrega os pacientes do usuário logado
  carregarPacientes(): void {
    this.patientService.listarPacientes().subscribe({
      next: (data) => {
        this.patients = data;
        if (data.length > 0 && !this.uploadForm.patientId) {
          this.uploadForm.patientId = data[0].id; // Seleciona o primeiro por padrão
        }
      },
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

  carregarDocumentos(): void {
    this.isLoading.set(true);
    
    this.documentService.listarDocumentos(
      this.patientId, 
      this.selectedDocumentType || undefined, 
      this.startDate || undefined, 
      this.endDate || undefined
    ).subscribe({
      next: (data) => {
        this.documents.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Erro ao carregar documentos:', err);
        this.isLoading.set(false);
        const erroBackend = err.error;
        const mensagemErro = erroBackend?.message || 'Não foi possível carregar os documentos. Tente novamente mais tarde.';
        this.messageService.add({
          severity: 'error',
          summary: 'Erro de Carregamento',
          detail: mensagemErro,
          life: 6000
        });
      }
    });
  }

  confirmarExclusao(doc: DocumentDetailsResponse): void {
   this.confirmationService.confirm({
    message: `Tem certeza que deseja excluir o documento "${doc.title}"?`,
    header: 'Confirmação de Exclusão',
    icon: 'pi pi-exclamation-triangle',
    acceptButtonStyleClass: 'p-button-danger', // Deixa o botão de confirmar vermelho
    rejectButtonStyleClass: 'p-button-text',   // Deixa o botão de cancelar discreto
    acceptLabel: 'Sim, excluir',
    rejectLabel: 'Cancelar',
    accept: () => {
      // Executado quando o usuário clica em confirmar
      this.documentService.excluirDocumento(doc.id).subscribe({
        next: () => {
          this.carregarDocumentos();
          this.messageService.add({ 
            severity: 'success', 
            summary: 'Excluído', 
            detail: 'Documento removido com sucesso.' 
          });
        },
        error: (err) => {
          console.error('Erro ao excluir documento:', err);
          const erroBackend = err.error;
          const mensagemErro = erroBackend?.message || 'Não foi possível excluir o documento.';
          
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

  abrirResumo(doc: DocumentDetailsResponse): void {
    this.documentoSelecionado = doc;
    this.resumoAtual = doc.aiSummary || 'Resumo não disponível para este documento.';
    this.displayResumoModal = true;
  }

  abrirModalUpload(): void {
    this.uploadForm = { 
      title: '', 
      documentType: 'Laudo', 
      documentDate: '', 
      patientId: this.patientId, 
      specialistId: this.specialistId 
    };
    this.selectedFile = null;
    this.displayUploadModal = true;
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  salvarUpload(): void {
   if (!this.selectedFile) {
    this.messageService.add({ severity: 'warn', summary: 'Atenção', detail: 'Por favor, selecione um arquivo PDF.' });
    return;
  }

    const formData = new FormData();
    formData.append('file', this.selectedFile);
    formData.append('title', this.uploadForm.title);
    formData.append('documentType', this.uploadForm.documentType);
    formData.append('documentDate', this.uploadForm.documentDate);
    formData.append('patientId', this.uploadForm.patientId.toString());
    if (this.uploadForm.specialistId) {
      formData.append('specialistId', this.uploadForm.specialistId.toString());
    }
    if(this.uploadForm.expirationDate) {
      formData.append('expirationDate', this.uploadForm.expirationDate);
    }

    this.isUploading = true;
    this.documentService.uploadDocumento(formData).subscribe({
      next: () => {
        this.isUploading = false;
        this.displayUploadModal = false;
        this.carregarDocumentos();
      },
      error: (err) => {
      console.error('Erro ao fazer upload do documento:', err);
      this.isUploading = false;

      // Extrai a mensagem tratada pelo GlobalExceptionHandler do back-end
      const erroBackend = err.error;
      let mensagemErro = 'Erro ao realizar o upload do documento.';

      if (erroBackend) {
        if (erroBackend.message) {
          mensagemErro = erroBackend.message;
        }
        // Se houver erros específicos por campo (validação do @Valid)
        if (erroBackend.fieldErrors) {
          const detalhesCampos = Object.values(erroBackend.fieldErrors).join('\n');
          mensagemErro = `${erroBackend.message}\n${detalhesCampos}`;
        }
      }

      // Toast de Erro (vermelho, no canto superior)
      this.messageService.add({ 
        severity: 'error', 
        summary: erroBackend?.error || 'Erro no Upload', 
        detail: mensagemErro,
        life: 6000 // Fica visível por 6 segundos se a mensagem for longa
      });
    }
  });
  }

  limparFiltros(): void {
    this.selectedDocumentType = '';
    this.startDate = '';
    this.endDate = '';
    this.carregarDocumentos();
  }
}