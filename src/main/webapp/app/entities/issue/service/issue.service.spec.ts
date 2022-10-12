import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { Status } from 'app/entities/enumerations/status.model';
import { IIssue, Issue } from '../issue.model';

import { IssueService } from './issue.service';

describe('Issue Service', () => {
  let service: IssueService;
  let httpMock: HttpTestingController;
  let elemDefault: IIssue;
  let expectedResult: IIssue | IIssue[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(IssueService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      defectNumber: 'AAAAAAA',
      description: 'AAAAAAA',
      version: 'AAAAAAA',
      status: Status.OPEN,
      dateIdentified: currentDate,
      dateClosed: currentDate,
      comments: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          dateIdentified: currentDate.format(DATE_FORMAT),
          dateClosed: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Issue', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          dateIdentified: currentDate.format(DATE_FORMAT),
          dateClosed: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateIdentified: currentDate,
          dateClosed: currentDate,
        },
        returnedFromService
      );

      service.create(new Issue()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Issue', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          defectNumber: 'BBBBBB',
          description: 'BBBBBB',
          version: 'BBBBBB',
          status: 'BBBBBB',
          dateIdentified: currentDate.format(DATE_FORMAT),
          dateClosed: currentDate.format(DATE_FORMAT),
          comments: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateIdentified: currentDate,
          dateClosed: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Issue', () => {
      const patchObject = Object.assign(
        {
          version: 'BBBBBB',
          comments: 'BBBBBB',
        },
        new Issue()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          dateIdentified: currentDate,
          dateClosed: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Issue', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          defectNumber: 'BBBBBB',
          description: 'BBBBBB',
          version: 'BBBBBB',
          status: 'BBBBBB',
          dateIdentified: currentDate.format(DATE_FORMAT),
          dateClosed: currentDate.format(DATE_FORMAT),
          comments: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateIdentified: currentDate,
          dateClosed: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Issue', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addIssueToCollectionIfMissing', () => {
      it('should add a Issue to an empty array', () => {
        const issue: IIssue = { id: 123 };
        expectedResult = service.addIssueToCollectionIfMissing([], issue);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(issue);
      });

      it('should not add a Issue to an array that contains it', () => {
        const issue: IIssue = { id: 123 };
        const issueCollection: IIssue[] = [
          {
            ...issue,
          },
          { id: 456 },
        ];
        expectedResult = service.addIssueToCollectionIfMissing(issueCollection, issue);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Issue to an array that doesn't contain it", () => {
        const issue: IIssue = { id: 123 };
        const issueCollection: IIssue[] = [{ id: 456 }];
        expectedResult = service.addIssueToCollectionIfMissing(issueCollection, issue);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(issue);
      });

      it('should add only unique Issue to an array', () => {
        const issueArray: IIssue[] = [{ id: 123 }, { id: 456 }, { id: 87457 }];
        const issueCollection: IIssue[] = [{ id: 123 }];
        expectedResult = service.addIssueToCollectionIfMissing(issueCollection, ...issueArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const issue: IIssue = { id: 123 };
        const issue2: IIssue = { id: 456 };
        expectedResult = service.addIssueToCollectionIfMissing([], issue, issue2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(issue);
        expect(expectedResult).toContain(issue2);
      });

      it('should accept null and undefined values', () => {
        const issue: IIssue = { id: 123 };
        expectedResult = service.addIssueToCollectionIfMissing([], null, issue, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(issue);
      });

      it('should return initial array if no Issue is added', () => {
        const issueCollection: IIssue[] = [{ id: 123 }];
        expectedResult = service.addIssueToCollectionIfMissing(issueCollection, undefined, null);
        expect(expectedResult).toEqual(issueCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
