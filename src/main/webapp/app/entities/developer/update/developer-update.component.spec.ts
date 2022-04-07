import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DeveloperService } from '../service/developer.service';
import { IDeveloper, Developer } from '../developer.model';
import { IExpertise } from 'app/entities/expertise/expertise.model';
import { ExpertiseService } from 'app/entities/expertise/service/expertise.service';

import { DeveloperUpdateComponent } from './developer-update.component';

describe('Developer Management Update Component', () => {
  let comp: DeveloperUpdateComponent;
  let fixture: ComponentFixture<DeveloperUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let developerService: DeveloperService;
  let expertiseService: ExpertiseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DeveloperUpdateComponent],
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
      .overrideTemplate(DeveloperUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DeveloperUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    developerService = TestBed.inject(DeveloperService);
    expertiseService = TestBed.inject(ExpertiseService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Expertise query and add missing value', () => {
      const developer: IDeveloper = { id: 456 };
      const expertise: IExpertise[] = [{ id: 10999 }];
      developer.expertise = expertise;

      const expertiseCollection: IExpertise[] = [{ id: 69387 }];
      jest.spyOn(expertiseService, 'query').mockReturnValue(of(new HttpResponse({ body: expertiseCollection })));
      const additionalExpertise = [...expertise];
      const expectedCollection: IExpertise[] = [...additionalExpertise, ...expertiseCollection];
      jest.spyOn(expertiseService, 'addExpertiseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ developer });
      comp.ngOnInit();

      expect(expertiseService.query).toHaveBeenCalled();
      expect(expertiseService.addExpertiseToCollectionIfMissing).toHaveBeenCalledWith(expertiseCollection, ...additionalExpertise);
      expect(comp.expertiseSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const developer: IDeveloper = { id: 456 };
      const expertise: IExpertise = { id: 14087 };
      developer.expertise = [expertise];

      activatedRoute.data = of({ developer });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(developer));
      expect(comp.expertiseSharedCollection).toContain(expertise);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Developer>>();
      const developer = { id: 123 };
      jest.spyOn(developerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ developer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: developer }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(developerService.update).toHaveBeenCalledWith(developer);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Developer>>();
      const developer = new Developer();
      jest.spyOn(developerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ developer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: developer }));
      saveSubject.complete();

      // THEN
      expect(developerService.create).toHaveBeenCalledWith(developer);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Developer>>();
      const developer = { id: 123 };
      jest.spyOn(developerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ developer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(developerService.update).toHaveBeenCalledWith(developer);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackExpertiseById', () => {
      it('Should return tracked Expertise primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackExpertiseById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedExpertise', () => {
      it('Should return option if no Expertise is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedExpertise(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Expertise for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedExpertise(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Expertise is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedExpertise(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
