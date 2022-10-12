import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ExpertiseService } from '../service/expertise.service';

import { ExpertiseComponent } from './expertise.component';

describe('Expertise Management Component', () => {
  let comp: ExpertiseComponent;
  let fixture: ComponentFixture<ExpertiseComponent>;
  let service: ExpertiseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ExpertiseComponent],
    })
      .overrideTemplate(ExpertiseComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExpertiseComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ExpertiseService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.expertise?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
