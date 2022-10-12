import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDeveloper, getDeveloperIdentifier } from '../developer.model';

export type EntityResponseType = HttpResponse<IDeveloper>;
export type EntityArrayResponseType = HttpResponse<IDeveloper[]>;

@Injectable({ providedIn: 'root' })
export class DeveloperService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/developers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(developer: IDeveloper): Observable<EntityResponseType> {
    return this.http.post<IDeveloper>(this.resourceUrl, developer, { observe: 'response' });
  }

  update(developer: IDeveloper): Observable<EntityResponseType> {
    return this.http.put<IDeveloper>(`${this.resourceUrl}/${getDeveloperIdentifier(developer) as number}`, developer, {
      observe: 'response',
    });
  }

  partialUpdate(developer: IDeveloper): Observable<EntityResponseType> {
    return this.http.patch<IDeveloper>(`${this.resourceUrl}/${getDeveloperIdentifier(developer) as number}`, developer, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDeveloper>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDeveloper[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addDeveloperToCollectionIfMissing(
    developerCollection: IDeveloper[],
    ...developersToCheck: (IDeveloper | null | undefined)[]
  ): IDeveloper[] {
    const developers: IDeveloper[] = developersToCheck.filter(isPresent);
    if (developers.length > 0) {
      const developerCollectionIdentifiers = developerCollection.map(developerItem => getDeveloperIdentifier(developerItem)!);
      const developersToAdd = developers.filter(developerItem => {
        const developerIdentifier = getDeveloperIdentifier(developerItem);
        if (developerIdentifier == null || developerCollectionIdentifiers.includes(developerIdentifier)) {
          return false;
        }
        developerCollectionIdentifiers.push(developerIdentifier);
        return true;
      });
      return [...developersToAdd, ...developerCollection];
    }
    return developerCollection;
  }
}
