import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DocumentDetailsResponse, DocumentUploadResponse } from '../models/document.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/documents';

  listarDocumentos(
    patientId: number, 
    documentType?: string, 
    startDate?: string, 
    endDate?: string
  ): Observable<DocumentDetailsResponse[]> {
    
    let params = new HttpParams();
    if (documentType) params = params.set('documentType', documentType);
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);

    return this.http.get<DocumentDetailsResponse[]>(`${this.apiUrl}/patient/${patientId}`, {
      params,
      withCredentials: true
    });
  }

  excluirDocumento(documentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${documentId}`, {
      withCredentials: true
    });
  }

  // Ajustado para receber o FormData diretamente
  uploadDocumento(formData: FormData): Observable<DocumentUploadResponse> {
    return this.http.post<DocumentUploadResponse>(`${this.apiUrl}/upload`, formData, {
      withCredentials: true
    });
  }
}