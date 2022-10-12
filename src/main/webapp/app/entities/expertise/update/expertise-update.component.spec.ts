import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ExpertiseService } from '../service/expertise.service';
import { IExpertise, Expertise } from '../expertise.model';

import { ExpertiseUpdateComponent } from './expertise-update.component';

describe('Expertise Management Update Component', () => {
  let comp: ExpertiseUpdateComponent;
  let fixture: ComponentFixture<ExpertiseUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let expertiseService: ExpertiseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ExpertiseUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ExpertiseUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExpertiseUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    expertiseService = TestBed.inject(ExpertiseService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const expertise: IExpertise = { id: 456 };

      activatedRoute.data = of({ expertise });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(expertise));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Expertise>>();
      const expertise = { id: 123 };
      jest.spyOn(expertiseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ expertise });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: expertise }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(expertiseService.update).toHaveBeenCalledWith(expertise);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Expertise>>();
      const expertise = new Expertise();
      jest.spyOn(expertiseService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ expertise });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: expertise }));
      saveSubject.complete();

      // THEN
      expect(expertiseService.create).toHaveBeenCalledWith(expertise);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Expertise>>();
      const expertise = { id: 123 };
      jest.spyOn(expertiseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ expertise });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(expertiseService.update).toHaveBeenCalledWith(expertise);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
