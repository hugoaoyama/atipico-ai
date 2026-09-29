export interface DocumentDetailsResponse {
  id: number;
  title: string;
  fileName: string;
  documentType: string;
  documentDate?: string;
  expirationDate?: string;
  googleDriveUrl: string;
  aiSummary: string;
  createdAt: string;
}

export interface DocumentUploadResponse {
  fileId: string;
  fileName: string;
  webViewLink: string;
  message: string;
}

export interface DocumentUploadRequest {
  title: string;
  documentType: string;
  documentDate: string;
  expirationDate?: string;
  patientId: number;
  specialistId?: number; // Adicionado para permitir o envio do ID do especialista
}