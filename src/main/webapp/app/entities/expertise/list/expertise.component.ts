import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IExpertise } from '../expertise.model';
import { ExpertiseService } from '../service/expertise.service';
import { ExpertiseDeleteDialogComponent } from '../delete/expertise-delete-dialog.component';

@Component({
  selector: 'jhi-expertise',
  templateUrl: './expertise.component.html',
})
export class ExpertiseComponent implements OnInit {
  expertise?: IExpertise[];
  isLoading = false;

  constructor(protected expertiseService: ExpertiseService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.expertiseService.query().subscribe({
      next: (res: HttpResponse<IExpertise[]>) => {
        this.isLoading = false;
        this.expertise = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IExpertise): number {
    return item.id!;
  }

  delete(expertise: IExpertise): void {
    const modalRef = this.modalService.open(ExpertiseDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.expertise = expertise;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
