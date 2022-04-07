import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IIssue, getIssueIdentifier } from '../issue.model';

export type EntityResponseType = HttpResponse<IIssue>;
export type EntityArrayResponseType = HttpResponse<IIssue[]>;

@Injectable({ providedIn: 'root' })
export class IssueService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/issues');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(issue: IIssue): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(issue);
    return this.http
      .post<IIssue>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(issue: IIssue): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(issue);
    return this.http
      .put<IIssue>(`${this.resourceUrl}/${getIssueIdentifier(issue) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(issue: IIssue): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(issue);
    return this.http
      .patch<IIssue>(`${this.resourceUrl}/${getIssueIdentifier(issue) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IIssue>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IIssue[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addIssueToCollectionIfMissing(issueCollection: IIssue[], ...issuesToCheck: (IIssue | null | undefined)[]): IIssue[] {
    const issues: IIssue[] = issuesToCheck.filter(isPresent);
    if (issues.length > 0) {
      const issueCollectionIdentifiers = issueCollection.map(issueItem => getIssueIdentifier(issueItem)!);
      const issuesToAdd = issues.filter(issueItem => {
        const issueIdentifier = getIssueIdentifier(issueItem);
        if (issueIdentifier == null || issueCollectionIdentifiers.includes(issueIdentifier)) {
          return false;
        }
        issueCollectionIdentifiers.push(issueIdentifier);
        return true;
      });
      return [...issuesToAdd, ...issueCollection];
    }
    return issueCollection;
  }

  protected convertDateFromClient(issue: IIssue): IIssue {
    return Object.assign({}, issue, {
      dateIdentified: issue.dateIdentified?.isValid() ? issue.dateIdentified.format(DATE_FORMAT) : undefined,
      dateClosed: issue.dateClosed?.isValid() ? issue.dateClosed.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dateIdentified = res.body.dateIdentified ? dayjs(res.body.dateIdentified) : undefined;
      res.body.dateClosed = res.body.dateClosed ? dayjs(res.body.dateClosed) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((issue: IIssue) => {
        issue.dateIdentified = issue.dateIdentified ? dayjs(issue.dateIdentified) : undefined;
        issue.dateClosed = issue.dateClosed ? dayjs(issue.dateClosed) : undefined;
      });
    }
    return res;
  }
}
