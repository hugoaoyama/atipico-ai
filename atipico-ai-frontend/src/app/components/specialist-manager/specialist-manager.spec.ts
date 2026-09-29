import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpecialistManager } from './specialist-manager';

describe('SpecialistManager', () => {
  let component: SpecialistManager;
  let fixture: ComponentFixture<SpecialistManager>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SpecialistManager]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SpecialistManager);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
