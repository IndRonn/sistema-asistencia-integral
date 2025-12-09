import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UiBadgeComponent } from './ui-badge.component';

describe('UiBadgeComponent', () => {
  let component: UiBadgeComponent;
  let fixture: ComponentFixture<UiBadgeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UiBadgeComponent]
    });
    fixture = TestBed.createComponent(UiBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
