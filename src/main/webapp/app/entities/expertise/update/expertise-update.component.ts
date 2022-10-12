import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IExpertise, Expertise } from '../expertise.model';
import { ExpertiseService } from '../service/expertise.service';

@Component({
  selector: 'jhi-expertise-update',
  templateUrl: './expertise-update.component.html',
})
export class ExpertiseUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.maxLength(50)]],
    description: [null, [Validators.maxLength(255)]],
  });

  constructor(protected expertiseService: ExpertiseService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ expertise }) => {
      this.updateForm(expertise);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const expertise = this.createFromForm();
    if (expertise.id !== undefined) {
      this.subscribeToSaveResponse(this.expertiseService.update(expertise));
    } else {
      this.subscribeToSaveResponse(this.expertiseService.create(expertise));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExpertise>>): void {
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

  protected updateForm(expertise: IExpertise): void {
    this.editForm.patchValue({
      id: expertise.id,
      name: expertise.name,
      description: expertise.description,
    });
  }

  protected createFromForm(): IExpertise {
    return {
      ...new Expertise(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }
}
