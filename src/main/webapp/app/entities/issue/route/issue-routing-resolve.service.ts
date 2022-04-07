import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IIssue, Issue } from '../issue.model';
import { IssueService } from '../service/issue.service';

@Injectable({ providedIn: 'root' })
export class IssueRoutingResolveService implements Resolve<IIssue> {
  constructor(protected service: IssueService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IIssue> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((issue: HttpResponse<Issue>) => {
          if (issue.body) {
            return of(issue.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Issue());
  }
}
