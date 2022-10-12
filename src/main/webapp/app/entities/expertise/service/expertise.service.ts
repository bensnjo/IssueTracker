import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IExpertise, getExpertiseIdentifier } from '../expertise.model';

export type EntityResponseType = HttpResponse<IExpertise>;
export type EntityArrayResponseType = HttpResponse<IExpertise[]>;

@Injectable({ providedIn: 'root' })
export class ExpertiseService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/expertise');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(expertise: IExpertise): Observable<EntityResponseType> {
    return this.http.post<IExpertise>(this.resourceUrl, expertise, { observe: 'response' });
  }

  update(expertise: IExpertise): Observable<EntityResponseType> {
    return this.http.put<IExpertise>(`${this.resourceUrl}/${getExpertiseIdentifier(expertise) as number}`, expertise, {
      observe: 'response',
    });
  }

  partialUpdate(expertise: IExpertise): Observable<EntityResponseType> {
    return this.http.patch<IExpertise>(`${this.resourceUrl}/${getExpertiseIdentifier(expertise) as number}`, expertise, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IExpertise>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IExpertise[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addExpertiseToCollectionIfMissing(
    expertiseCollection: IExpertise[],
    ...expertiseToCheck: (IExpertise | null | undefined)[]
  ): IExpertise[] {
    const expertise: IExpertise[] = expertiseToCheck.filter(isPresent);
    if (expertise.length > 0) {
      const expertiseCollectionIdentifiers = expertiseCollection.map(expertiseItem => getExpertiseIdentifier(expertiseItem)!);
      const expertiseToAdd = expertise.filter(expertiseItem => {
        const expertiseIdentifier = getExpertiseIdentifier(expertiseItem);
        if (expertiseIdentifier == null || expertiseCollectionIdentifiers.includes(expertiseIdentifier)) {
          return false;
        }
        expertiseCollectionIdentifiers.push(expertiseIdentifier);
        return true;
      });
      return [...expertiseToAdd, ...expertiseCollection];
    }
    return expertiseCollection;
  }
}
