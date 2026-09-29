import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, RouterLink } from '@angular/router';

import { ButtonModule } from 'primeng/button';
import { DrawerModule } from 'primeng/drawer'; // Componente moderno de Drawer/Sidebar no PrimeNG 20
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, ButtonModule, DrawerModule],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.scss'
})
export class MainLayoutComponent {
  private router = inject(Router);
  private authService = inject(AuthService);
  
  sidebarVisible = false;

  irParaDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  fazerLogout(): void {
    this.sidebarVisible = false;
    
    this.authService.logout().subscribe({
      next: () => {
        // Redireciona para a raiz ou tela de login após destruir a sessão no backend
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error('Erro ao deslogar:', err);
        // Mesmo se houver erro, força o redirecionamento
        this.router.navigate(['/']);
      }
    });
  }
}