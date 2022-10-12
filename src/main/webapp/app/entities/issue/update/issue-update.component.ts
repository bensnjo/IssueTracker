import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IIssue, Issue } from '../issue.model';
import { IssueService } from '../service/issue.service';
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
import { Status } from 'app/entities/enumerations/status.model';

@Component({
  selector: 'jhi-issue-update',
  templateUrl: './issue-update.component.html',
})
export class IssueUpdateComponent implements OnInit {
  isSaving = false;
  statusValues = Object.keys(Status);

  categoriesSharedCollection: ICategory[] = [];
  productsSharedCollection: IProduct[] = [];
  developersSharedCollection: IDeveloper[] = [];
  prioritiesSharedCollection: IPriority[] = [];
  departmentsSharedCollection: IDepartment[] = [];

  editForm = this.fb.group({
    id: [],
    defectNumber: [null, [Validators.required, Validators.maxLength(10)]],
    description: [null, [Validators.maxLength(255)]],
    version: [null, [Validators.maxLength(10)]],
    status: [null, [Validators.required]],
    dateIdentified: [null, [Validators.required]],
    dateClosed: [],
    comments: [null, [Validators.maxLength(1000)]],
    category: [],
    product: [],
    assignee: [],
    priority: [],
    department: [],
  });

  constructor(
    protected issueService: IssueService,
    protected categoryService: CategoryService,
    protected productService: ProductService,
    protected developerService: DeveloperService,
    protected priorityService: PriorityService,
    protected departmentService: DepartmentService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ issue }) => {
      this.updateForm(issue);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const issue = this.createFromForm();
    if (issue.id !== undefined) {
      this.subscribeToSaveResponse(this.issueService.update(issue));
    } else {
      this.subscribeToSaveResponse(this.issueService.create(issue));
    }
  }

  trackCategoryById(index: number, item: ICategory): number {
    return item.id!;
  }

  trackProductById(index: number, item: IProduct): number {
    return item.id!;
  }

  trackDeveloperById(index: number, item: IDeveloper): number {
    return item.id!;
  }

  trackPriorityById(index: number, item: IPriority): number {
    return item.id!;
  }

  trackDepartmentById(index: number, item: IDepartment): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IIssue>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(issue: IIssue): void {
    this.editForm.patchValue({
      id: issue.id,
      defectNumber: issue.defectNumber,
      description: issue.description,
      version: issue.version,
      status: issue.status,
      dateIdentified: issue.dateIdentified,
      dateClosed: issue.dateClosed,
      comments: issue.comments,
      category: issue.category,
      product: issue.product,
      assignee: issue.assignee,
      priority: issue.priority,
      department: issue.department,
    });

    this.categoriesSharedCollection = this.categoryService.addCategoryToCollectionIfMissing(
      this.categoriesSharedCollection,
      issue.category
    );
    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing(this.productsSharedCollection, issue.product);
    this.developersSharedCollection = this.developerService.addDeveloperToCollectionIfMissing(
      this.developersSharedCollection,
      issue.assignee
    );
    this.prioritiesSharedCollection = this.priorityService.addPriorityToCollectionIfMissing(
      this.prioritiesSharedCollection,
      issue.priority
    );
    this.departmentsSharedCollection = this.departmentService.addDepartmentToCollectionIfMissing(
      this.departmentsSharedCollection,
      issue.department
    );
  }

  protected loadRelationshipsOptions(): void {
    this.categoryService
      .query()
      .pipe(map((res: HttpResponse<ICategory[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategory[]) =>
          this.categoryService.addCategoryToCollectionIfMissing(categories, this.editForm.get('category')!.value)
        )
      )
      .subscribe((categories: ICategory[]) => (this.categoriesSharedCollection = categories));

    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing(products, this.editForm.get('product')!.value))
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));

    this.developerService
      .query()
      .pipe(map((res: HttpResponse<IDeveloper[]>) => res.body ?? []))
      .pipe(
        map((developers: IDeveloper[]) =>
          this.developerService.addDeveloperToCollectionIfMissing(developers, this.editForm.get('assignee')!.value)
        )
      )
      .subscribe((developers: IDeveloper[]) => (this.developersSharedCollection = developers));

    this.priorityService
      .query()
      .pipe(map((res: HttpResponse<IPriority[]>) => res.body ?? []))
      .pipe(
        map((priorities: IPriority[]) =>
          this.priorityService.addPriorityToCollectionIfMissing(priorities, this.editForm.get('priority')!.value)
        )
      )
      .subscribe((priorities: IPriority[]) => (this.prioritiesSharedCollection = priorities));

    this.departmentService
      .query()
      .pipe(map((res: HttpResponse<IDepartment[]>) => res.body ?? []))
      .pipe(
        map((departments: IDepartment[]) =>
          this.departmentService.addDepartmentToCollectionIfMissing(departments, this.editForm.get('department')!.value)
        )
      )
      .subscribe((departments: IDepartment[]) => (this.departmentsSharedCollection = departments));
  }

  protected createFromForm(): IIssue {
    return {
      ...new Issue(),
      id: this.editForm.get(['id'])!.value,
      defectNumber: this.editForm.get(['defectNumber'])!.value,
      description: this.editForm.get(['description'])!.value,
      version: this.editForm.get(['version'])!.value,
      status: this.editForm.get(['status'])!.value,
      dateIdentified: this.editForm.get(['dateIdentified'])!.value,
      dateClosed: this.editForm.get(['dateClosed'])!.value,
      comments: this.editForm.get(['comments'])!.value,
      category: this.editForm.get(['category'])!.value,
      product: this.editForm.get(['product'])!.value,
      assignee: this.editForm.get(['assignee'])!.value,
      priority: this.editForm.get(['priority'])!.value,
      department: this.editForm.get(['department'])!.value,
    };
  }
}
