import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [CommonModule, CardModule, ButtonModule],
  templateUrl: './about.html',
  styleUrl: './about.scss'
})
export class AboutComponent {
  private router = inject(Router);

  voltarDashboard(): void {
    this.router.navigate(['/dashboard']);
  }
}