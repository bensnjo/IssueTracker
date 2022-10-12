import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { IssueDetailComponent } from './issue-detail.component';

describe('Issue Management Detail Component', () => {
  let comp: IssueDetailComponent;
  let fixture: ComponentFixture<IssueDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [IssueDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ issue: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(IssueDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(IssueDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load issue on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.issue).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
