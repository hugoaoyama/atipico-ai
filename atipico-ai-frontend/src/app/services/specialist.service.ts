import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SpecialistService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/specialists'; // Ajuste o endpoint se necessário conforme seu Controller

  listarEspecialistas(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { withCredentials: true });
  }

  salvarEspecialista(specialist: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, specialist, { withCredentials: true });
  }

  excluirEspecialista(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }
}