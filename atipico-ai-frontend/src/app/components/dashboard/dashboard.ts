import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { AuthService } from '../../services/auth.service'; // Importe o service

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, CardModule, ButtonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit {
  private router = inject(Router);
  private authService = inject(AuthService); // Injeta o AuthService

  userName = signal<string>('Usuário');

  ngOnInit(): void {
    this.carregarUsuario();
  }

  carregarUsuario(): void {
    this.authService.getUsuarioLogado().subscribe({
      next: (res) => {
        if (res && res.name) {
          this.userName.set(res.name);
        }
      },
      error: (err) => {
        console.error('Erro ao buscar dados do usuário:', err);
        this.userName.set('Pai/Mãe');
      }
    });
  }

  irParaDocumentos(): void {
    this.router.navigate(['/documents']);
  }

  irParaPacientes(): void {
    this.router.navigate(['/patients']);
  }   

  irParaEspecialistas(): void {
    this.router.navigate(['/specialists']);
  }

  irParaRelatorio(): void {
    this.router.navigate(['/reports']); // <--- Nova rota de relatórios
  }
}