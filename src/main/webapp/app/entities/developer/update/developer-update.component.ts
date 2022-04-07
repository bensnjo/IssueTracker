import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IDeveloper, Developer } from '../developer.model';
import { DeveloperService } from '../service/developer.service';
import { IExpertise } from 'app/entities/expertise/expertise.model';
import { ExpertiseService } from 'app/entities/expertise/service/expertise.service';

@Component({
  selector: 'jhi-developer-update',
  templateUrl: './developer-update.component.html',
})
export class DeveloperUpdateComponent implements OnInit {
  isSaving = false;

  expertiseSharedCollection: IExpertise[] = [];

  editForm = this.fb.group({
    id: [],
    staffNo: [null, [Validators.required, Validators.maxLength(50)]],
    fullName: [null, [Validators.required, Validators.maxLength(100)]],
    email: [null, [Validators.required, Validators.maxLength(50)]],
    phoneNumber: [null, [Validators.required, Validators.maxLength(50)]],
    expertise: [],
  });

  constructor(
    protected developerService: DeveloperService,
    protected expertiseService: ExpertiseService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ developer }) => {
      this.updateForm(developer);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const developer = this.createFromForm();
    if (developer.id !== undefined) {
      this.subscribeToSaveResponse(this.developerService.update(developer));
    } else {
      this.subscribeToSaveResponse(this.developerService.create(developer));
    }
  }

  trackExpertiseById(index: number, item: IExpertise): number {
    return item.id!;
  }

  getSelectedExpertise(option: IExpertise, selectedVals?: IExpertise[]): IExpertise {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDeveloper>>): void {
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

  protected updateForm(developer: IDeveloper): void {
    this.editForm.patchValue({
      id: developer.id,
      staffNo: developer.staffNo,
      fullName: developer.fullName,
      email: developer.email,
      phoneNumber: developer.phoneNumber,
      expertise: developer.expertise,
    });

    this.expertiseSharedCollection = this.expertiseService.addExpertiseToCollectionIfMissing(
      this.expertiseSharedCollection,
      ...(developer.expertise ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.expertiseService
      .query()
      .pipe(map((res: HttpResponse<IExpertise[]>) => res.body ?? []))
      .pipe(
        map((expertise: IExpertise[]) =>
          this.expertiseService.addExpertiseToCollectionIfMissing(expertise, ...(this.editForm.get('expertise')!.value ?? []))
        )
      )
      .subscribe((expertise: IExpertise[]) => (this.expertiseSharedCollection = expertise));
  }

  protected createFromForm(): IDeveloper {
    return {
      ...new Developer(),
      id: this.editForm.get(['id'])!.value,
      staffNo: this.editForm.get(['staffNo'])!.value,
      fullName: this.editForm.get(['fullName'])!.value,
      email: this.editForm.get(['email'])!.value,
      phoneNumber: this.editForm.get(['phoneNumber'])!.value,
      expertise: this.editForm.get(['expertise'])!.value,
    };
  }
}
