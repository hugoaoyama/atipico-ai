import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientManager } from './patient-manager';

describe('PatientManager', () => {
  let component: PatientManager;
  let fixture: ComponentFixture<PatientManager>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientManager]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientManager);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
