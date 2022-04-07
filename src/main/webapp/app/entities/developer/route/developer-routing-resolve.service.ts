import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDeveloper, Developer } from '../developer.model';
import { DeveloperService } from '../service/developer.service';

@Injectable({ providedIn: 'root' })
export class DeveloperRoutingResolveService implements Resolve<IDeveloper> {
  constructor(protected service: DeveloperService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDeveloper> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((developer: HttpResponse<Developer>) => {
          if (developer.body) {
            return of(developer.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Developer());
  }
}
