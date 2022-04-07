import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IExpertise, Expertise } from '../expertise.model';
import { ExpertiseService } from '../service/expertise.service';

@Injectable({ providedIn: 'root' })
export class ExpertiseRoutingResolveService implements Resolve<IExpertise> {
  constructor(protected service: ExpertiseService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IExpertise> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((expertise: HttpResponse<Expertise>) => {
          if (expertise.body) {
            return of(expertise.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Expertise());
  }
}
