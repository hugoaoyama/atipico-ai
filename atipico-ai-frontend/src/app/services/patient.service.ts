import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/api/v1/patients';

  listarPacientes(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { withCredentials: true });
  }

  salvarPaciente(paciente: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, paciente, { withCredentials: true });
  }

  excluirPaciente(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }
}