import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DeveloperComponent } from '../list/developer.component';
import { DeveloperDetailComponent } from '../detail/developer-detail.component';
import { DeveloperUpdateComponent } from '../update/developer-update.component';
import { DeveloperRoutingResolveService } from './developer-routing-resolve.service';

const developerRoute: Routes = [
  {
    path: '',
    component: DeveloperComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DeveloperDetailComponent,
    resolve: {
      developer: DeveloperRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DeveloperUpdateComponent,
    resolve: {
      developer: DeveloperRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DeveloperUpdateComponent,
    resolve: {
      developer: DeveloperRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(developerRoute)],
  exports: [RouterModule],
})
export class DeveloperRoutingModule {}
