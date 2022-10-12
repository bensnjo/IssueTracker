import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IProduct, Product } from '../product.model';
import { ProductService } from '../service/product.service';
import { IDeveloper } from 'app/entities/developer/developer.model';
import { DeveloperService } from 'app/entities/developer/service/developer.service';

@Component({
  selector: 'jhi-product-update',
  templateUrl: './product-update.component.html',
})
export class ProductUpdateComponent implements OnInit {
  isSaving = false;

  developersSharedCollection: IDeveloper[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.maxLength(100)]],
    description: [null, [Validators.maxLength(255)]],
    developers: [],
  });

  constructor(
    protected productService: ProductService,
    protected developerService: DeveloperService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ product }) => {
      this.updateForm(product);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const product = this.createFromForm();
    if (product.id !== undefined) {
      this.subscribeToSaveResponse(this.productService.update(product));
    } else {
      this.subscribeToSaveResponse(this.productService.create(product));
    }
  }

  trackDeveloperById(index: number, item: IDeveloper): number {
    return item.id!;
  }

  getSelectedDeveloper(option: IDeveloper, selectedVals?: IDeveloper[]): IDeveloper {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProduct>>): void {
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

  protected updateForm(product: IProduct): void {
    this.editForm.patchValue({
      id: product.id,
      name: product.name,
      description: product.description,
      developers: product.developers,
    });

    this.developersSharedCollection = this.developerService.addDeveloperToCollectionIfMissing(
      this.developersSharedCollection,
      ...(product.developers ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.developerService
      .query()
      .pipe(map((res: HttpResponse<IDeveloper[]>) => res.body ?? []))
      .pipe(
        map((developers: IDeveloper[]) =>
          this.developerService.addDeveloperToCollectionIfMissing(developers, ...(this.editForm.get('developers')!.value ?? []))
        )
      )
      .subscribe((developers: IDeveloper[]) => (this.developersSharedCollection = developers));
  }

  protected createFromForm(): IProduct {
    return {
      ...new Product(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
      developers: this.editForm.get(['developers'])!.value,
    };
  }
}
