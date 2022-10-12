import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IIssue } from '../issue.model';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import { IssueService } from '../service/issue.service';
import { IssueDeleteDialogComponent } from '../delete/issue-delete-dialog.component';
import { Status } from '../../enumerations/status.model';

@Component({
  selector: 'jhi-issue',
  templateUrl: './issue.component.html',
})
export class IssueComponent implements OnInit {
  issues?: IIssue[];
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  openIssue=0;
  closedIssue=0;
  issuesTally: any[];

  constructor(
    protected issueService: IssueService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {}

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.issueService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<IIssue[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        error: () => {
          this.isLoading = false;
          this.onError();
        },
      });
  }

  ngOnInit(): void {
    this.handleNavigation();
  }

  trackId(index: number, item: IIssue): number {
    return item.id!;
  }

  delete(issue: IIssue): void {
    const modalRef = this.modalService.open(IssueDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.issue = issue;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }
  
  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap]).subscribe(([data, params]) => {
      const page = params.get('page');
      const pageNumber = +(page ?? 1);
      const sort = (params.get(SORT) ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === ASC;
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(pageNumber, true);
      }
    });
  }

  protected onSuccess(data: IIssue[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;

    data?.forEach((issue)=>{
      if(issue.status===Status.OPEN){
        this.openIssue++;
      }
      else if(issue.status===Status.CLOSED){
        this.closedIssue++;
      }
    });
    this.issuesTally = [
      {
       "name": "Closed",
       "value": this.closedIssue
     }, {
       "name": "Open",
       "value": this.openIssue
     }
    ];
   localStorage.setItem('issueTally', JSON.stringify(this.issuesTally));
    if (navigate) {
      this.router.navigate(['/issue'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
        },
      });
    }
    this.issues = data ?? [];
    this.ngbPaginationPage = this.page;
  }
  

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
