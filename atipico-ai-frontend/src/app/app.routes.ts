import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home';
import { DashboardComponent } from './components/dashboard/dashboard';
import { DocumentManagerComponent } from './components/document-manager/document-manager';
import { PatientManagerComponent } from './components/patient-manager/patient-manager';
import { MainLayoutComponent } from './components/main-layout/main-layout';
import { SpecialistManagerComponent } from './components/specialist-manager/specialist-manager';
import { AboutComponent } from './components/about/about';
import { ReportPreviewComponent } from './components/report-preview/report-preview';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  
  // Rotas que utilizam o Header e o Menu Lateral padrão
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'about', component: AboutComponent },
      { path: 'documents', component: DocumentManagerComponent },
      { path: 'patients', component: PatientManagerComponent },
      { path: 'specialists', component: SpecialistManagerComponent },
      { path: 'reports', component: ReportPreviewComponent }
    ]
  },
  
  { path: '**', redirectTo: 'home' }
];
