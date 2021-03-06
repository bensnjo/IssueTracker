import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IIssue } from '../issue.model';
import { IssueService } from '../service/issue.service';

@Component({
  templateUrl: './issue-delete-dialog.component.html',
})
export class IssueDeleteDialogComponent {
  issue?: IIssue;

  constructor(protected issueService: IssueService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.issueService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
