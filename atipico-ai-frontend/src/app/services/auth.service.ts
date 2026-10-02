import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/v1/auth'; // Ajuste conforme a URL do seu endpoint

  // Busca os dados do usuário logado
  getUsuarioLogado(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/user`, { withCredentials: true });
  }

  logout(): Observable<any> {
    // Chama o endpoint de logout do Spring Security limpando a sessão
    return this.http.post(`${this.apiUrl}/logout`, {}, { withCredentials: true });
  }
}