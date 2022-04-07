import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ExpertiseComponent } from '../list/expertise.component';
import { ExpertiseDetailComponent } from '../detail/expertise-detail.component';
import { ExpertiseUpdateComponent } from '../update/expertise-update.component';
import { ExpertiseRoutingResolveService } from './expertise-routing-resolve.service';

const expertiseRoute: Routes = [
  {
    path: '',
    component: ExpertiseComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ExpertiseDetailComponent,
    resolve: {
      expertise: ExpertiseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ExpertiseUpdateComponent,
    resolve: {
      expertise: ExpertiseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ExpertiseUpdateComponent,
    resolve: {
      expertise: ExpertiseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(expertiseRoute)],
  exports: [RouterModule],
})
export class ExpertiseRoutingModule {}
