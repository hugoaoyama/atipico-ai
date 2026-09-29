import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SpecialistSummary } from '../models/specialist.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/reports';

  obterPreviewResumos(patientId: number): Observable<SpecialistSummary[]> {
    return this.http.get<SpecialistSummary[]>(`${this.apiUrl}/patient/${patientId}/preview`, {
      withCredentials: true
    });
  }

  gerarRelatorioPdf(patientId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/patient/${patientId}`, {
      responseType: 'blob',
      withCredentials: true
    });
  }
}