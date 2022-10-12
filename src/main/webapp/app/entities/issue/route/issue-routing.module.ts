import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { IssueComponent } from '../list/issue.component';
import { IssueDetailComponent } from '../detail/issue-detail.component';
import { IssueUpdateComponent } from '../update/issue-update.component';
import { IssueRoutingResolveService } from './issue-routing-resolve.service';

const issueRoute: Routes = [
  {
    path: '',
    component: IssueComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: IssueDetailComponent,
    resolve: {
      issue: IssueRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: IssueUpdateComponent,
    resolve: {
      issue: IssueRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: IssueUpdateComponent,
    resolve: {
      issue: IssueRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(issueRoute)],
  exports: [RouterModule],
})
export class IssueRoutingModule {}
