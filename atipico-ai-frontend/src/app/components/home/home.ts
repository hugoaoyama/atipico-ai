import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ButtonModule, CardModule],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class HomeComponent implements OnInit {

  private authService: AuthService = inject(AuthService);
  private router: Router = inject(Router);

  ngOnInit(): void {
    // Verifica se já existe uma sessão ativa no backend
    this.authService.getUsuarioLogado().subscribe({
      next: (user) => {
        // Se o backend retornou o usuário com sucesso, a sessão é válida!
        if (user && user.email) {
          this.router.navigate(['/dashboard']); // Manda direto para o dashboard
        }
      },
      error: () => {
        // Se der erro (401 Unauthorized), significa que não há sessão ativa. 
        // O usuário continua na tela Home vendo o botão de login do Google normalmente.
      }
    });
  }

  // Endpoint exposto pelo Spring Security OAuth2 Client
  loginGoogle(): void {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  }
}