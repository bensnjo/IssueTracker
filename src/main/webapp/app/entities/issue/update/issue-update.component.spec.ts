import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IssueService } from '../service/issue.service';
import { IIssue, Issue } from '../issue.model';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { IDeveloper } from 'app/entities/developer/developer.model';
import { DeveloperService } from 'app/entities/developer/service/developer.service';
import { IPriority } from 'app/entities/priority/priority.model';
import { PriorityService } from 'app/entities/priority/service/priority.service';
import { IDepartment } from 'app/entities/department/department.model';
import { DepartmentService } from 'app/entities/department/service/department.service';

import { IssueUpdateComponent } from './issue-update.component';

describe('Issue Management Update Component', () => {
  let comp: IssueUpdateComponent;
  let fixture: ComponentFixture<IssueUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let issueService: IssueService;
  let categoryService: CategoryService;
  let productService: ProductService;
  let developerService: DeveloperService;
  let priorityService: PriorityService;
  let departmentService: DepartmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [IssueUpdateComponent],
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
      .overrideTemplate(IssueUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(IssueUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    issueService = TestBed.inject(IssueService);
    categoryService = TestBed.inject(CategoryService);
    productService = TestBed.inject(ProductService);
    developerService = TestBed.inject(DeveloperService);
    priorityService = TestBed.inject(PriorityService);
    departmentService = TestBed.inject(DepartmentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Category query and add missing value', () => {
      const issue: IIssue = { id: 456 };
      const category: ICategory = { id: 20302 };
      issue.category = category;

      const categoryCollection: ICategory[] = [{ id: 22887 }];
      jest.spyOn(categoryService, 'query').mockReturnValue(of(new HttpResponse({ body: categoryCollection })));
      const additionalCategories = [category];
      const expectedCollection: ICategory[] = [...additionalCategories, ...categoryCollection];
      jest.spyOn(categoryService, 'addCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ issue });
      comp.ngOnInit();

      expect(categoryService.query).toHaveBeenCalled();
      expect(categoryService.addCategoryToCollectionIfMissing).toHaveBeenCalledWith(categoryCollection, ...additionalCategories);
      expect(comp.categoriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Product query and add missing value', () => {
      const issue: IIssue = { id: 456 };
      const product: IProduct = { id: 55561 };
      issue.product = product;

      const productCollection: IProduct[] = [{ id: 58522 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ issue });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(productCollection, ...additionalProducts);
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Developer query and add missing value', () => {
      const issue: IIssue = { id: 456 };
      const assignee: IDeveloper = { id: 28410 };
      issue.assignee = assignee;

      const developerCollection: IDeveloper[] = [{ id: 58296 }];
      jest.spyOn(developerService, 'query').mockReturnValue(of(new HttpResponse({ body: developerCollection })));
      const additionalDevelopers = [assignee];
      const expectedCollection: IDeveloper[] = [...additionalDevelopers, ...developerCollection];
      jest.spyOn(developerService, 'addDeveloperToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ issue });
      comp.ngOnInit();

      expect(developerService.query).toHaveBeenCalled();
      expect(developerService.addDeveloperToCollectionIfMissing).toHaveBeenCalledWith(developerCollection, ...additionalDevelopers);
      expect(comp.developersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Priority query and add missing value', () => {
      const issue: IIssue = { id: 456 };
      const priority: IPriority = { id: 11310 };
      issue.priority = priority;

      const priorityCollection: IPriority[] = [{ id: 19572 }];
      jest.spyOn(priorityService, 'query').mockReturnValue(of(new HttpResponse({ body: priorityCollection })));
      const additionalPriorities = [priority];
      const expectedCollection: IPriority[] = [...additionalPriorities, ...priorityCollection];
      jest.spyOn(priorityService, 'addPriorityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ issue });
      comp.ngOnInit();

      expect(priorityService.query).toHaveBeenCalled();
      expect(priorityService.addPriorityToCollectionIfMissing).toHaveBeenCalledWith(priorityCollection, ...additionalPriorities);
      expect(comp.prioritiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Department query and add missing value', () => {
      const issue: IIssue = { id: 456 };
      const department: IDepartment = { id: 97409 };
      issue.department = department;

      const departmentCollection: IDepartment[] = [{ id: 89753 }];
      jest.spyOn(departmentService, 'query').mockReturnValue(of(new HttpResponse({ body: departmentCollection })));
      const additionalDepartments = [department];
      const expectedCollection: IDepartment[] = [...additionalDepartments, ...departmentCollection];
      jest.spyOn(departmentService, 'addDepartmentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ issue });
      comp.ngOnInit();

      expect(departmentService.query).toHaveBeenCalled();
      expect(departmentService.addDepartmentToCollectionIfMissing).toHaveBeenCalledWith(departmentCollection, ...additionalDepartments);
      expect(comp.departmentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const issue: IIssue = { id: 456 };
      const category: ICategory = { id: 14991 };
      issue.category = category;
      const product: IProduct = { id: 94736 };
      issue.product = product;
      const assignee: IDeveloper = { id: 76015 };
      issue.assignee = assignee;
      const priority: IPriority = { id: 83918 };
      issue.priority = priority;
      const department: IDepartment = { id: 30451 };
      issue.department = department;

      activatedRoute.data = of({ issue });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(issue));
      expect(comp.categoriesSharedCollection).toContain(category);
      expect(comp.productsSharedCollection).toContain(product);
      expect(comp.developersSharedCollection).toContain(assignee);
      expect(comp.prioritiesSharedCollection).toContain(priority);
      expect(comp.departmentsSharedCollection).toContain(department);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Issue>>();
      const issue = { id: 123 };
      jest.spyOn(issueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ issue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: issue }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(issueService.update).toHaveBeenCalledWith(issue);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Issue>>();
      const issue = new Issue();
      jest.spyOn(issueService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ issue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: issue }));
      saveSubject.complete();

      // THEN
      expect(issueService.create).toHaveBeenCalledWith(issue);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Issue>>();
      const issue = { id: 123 };
      jest.spyOn(issueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ issue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(issueService.update).toHaveBeenCalledWith(issue);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackCategoryById', () => {
      it('Should return tracked Category primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackCategoryById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackProductById', () => {
      it('Should return tracked Product primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackProductById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackDeveloperById', () => {
      it('Should return tracked Developer primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackDeveloperById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackPriorityById', () => {
      it('Should return tracked Priority primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPriorityById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackDepartmentById', () => {
      it('Should return tracked Department primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackDepartmentById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
