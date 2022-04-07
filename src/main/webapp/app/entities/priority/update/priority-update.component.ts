import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IPriority, Priority } from '../priority.model';
import { PriorityService } from '../service/priority.service';

@Component({
  selector: 'jhi-priority-update',
  templateUrl: './priority-update.component.html',
})
export class PriorityUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.maxLength(50)]],
    description: [null, [Validators.maxLength(255)]],
    sla: [null, [Validators.required]],
  });

  constructor(protected priorityService: PriorityService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ priority }) => {
      this.updateForm(priority);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const priority = this.createFromForm();
    if (priority.id !== undefined) {
      this.subscribeToSaveResponse(this.priorityService.update(priority));
    } else {
      this.subscribeToSaveResponse(this.priorityService.create(priority));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPriority>>): void {
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

  protected updateForm(priority: IPriority): void {
    this.editForm.patchValue({
      id: priority.id,
      name: priority.name,
      description: priority.description,
      sla: priority.sla,
    });
  }

  protected createFromForm(): IPriority {
    return {
      ...new Priority(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
      sla: this.editForm.get(['sla'])!.value,
    };
  }
}
