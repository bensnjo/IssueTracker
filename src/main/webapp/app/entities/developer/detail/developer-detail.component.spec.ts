import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DeveloperDetailComponent } from './developer-detail.component';

describe('Developer Management Detail Component', () => {
  let comp: DeveloperDetailComponent;
  let fixture: ComponentFixture<DeveloperDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DeveloperDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ developer: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(DeveloperDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DeveloperDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load developer on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.developer).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
