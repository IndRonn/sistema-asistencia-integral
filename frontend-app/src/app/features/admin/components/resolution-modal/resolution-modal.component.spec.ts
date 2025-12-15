import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResolutionModalComponent } from './resolution-modal.component';

describe('ResolutionModalComponent', () => {
  let component: ResolutionModalComponent;
  let fixture: ComponentFixture<ResolutionModalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ResolutionModalComponent]
    });
    fixture = TestBed.createComponent(ResolutionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
