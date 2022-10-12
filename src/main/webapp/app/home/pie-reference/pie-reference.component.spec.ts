import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PieReferenceComponent } from './pie-reference.component';

describe('PieReferenceComponent', () => {
  let component: PieReferenceComponent;
  let fixture: ComponentFixture<PieReferenceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PieReferenceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PieReferenceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
