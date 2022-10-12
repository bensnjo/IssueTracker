import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDeveloper } from '../developer.model';
import { DeveloperService } from '../service/developer.service';

@Component({
  templateUrl: './developer-delete-dialog.component.html',
})
export class DeveloperDeleteDialogComponent {
  developer?: IDeveloper;

  constructor(protected developerService: DeveloperService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.developerService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
