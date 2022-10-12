import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ExpertiseDetailComponent } from './expertise-detail.component';

describe('Expertise Management Detail Component', () => {
  let comp: ExpertiseDetailComponent;
  let fixture: ComponentFixture<ExpertiseDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ExpertiseDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ expertise: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ExpertiseDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ExpertiseDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load expertise on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.expertise).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
