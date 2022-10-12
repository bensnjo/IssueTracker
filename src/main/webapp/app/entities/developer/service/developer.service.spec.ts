import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDeveloper, Developer } from '../developer.model';

import { DeveloperService } from './developer.service';

describe('Developer Service', () => {
  let service: DeveloperService;
  let httpMock: HttpTestingController;
  let elemDefault: IDeveloper;
  let expectedResult: IDeveloper | IDeveloper[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DeveloperService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      staffNo: 'AAAAAAA',
      fullName: 'AAAAAAA',
      email: 'AAAAAAA',
      phoneNumber: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Developer', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Developer()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Developer', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          staffNo: 'BBBBBB',
          fullName: 'BBBBBB',
          email: 'BBBBBB',
          phoneNumber: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Developer', () => {
      const patchObject = Object.assign(
        {
          staffNo: 'BBBBBB',
          fullName: 'BBBBBB',
        },
        new Developer()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Developer', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          staffNo: 'BBBBBB',
          fullName: 'BBBBBB',
          email: 'BBBBBB',
          phoneNumber: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Developer', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addDeveloperToCollectionIfMissing', () => {
      it('should add a Developer to an empty array', () => {
        const developer: IDeveloper = { id: 123 };
        expectedResult = service.addDeveloperToCollectionIfMissing([], developer);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(developer);
      });

      it('should not add a Developer to an array that contains it', () => {
        const developer: IDeveloper = { id: 123 };
        const developerCollection: IDeveloper[] = [
          {
            ...developer,
          },
          { id: 456 },
        ];
        expectedResult = service.addDeveloperToCollectionIfMissing(developerCollection, developer);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Developer to an array that doesn't contain it", () => {
        const developer: IDeveloper = { id: 123 };
        const developerCollection: IDeveloper[] = [{ id: 456 }];
        expectedResult = service.addDeveloperToCollectionIfMissing(developerCollection, developer);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(developer);
      });

      it('should add only unique Developer to an array', () => {
        const developerArray: IDeveloper[] = [{ id: 123 }, { id: 456 }, { id: 25145 }];
        const developerCollection: IDeveloper[] = [{ id: 123 }];
        expectedResult = service.addDeveloperToCollectionIfMissing(developerCollection, ...developerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const developer: IDeveloper = { id: 123 };
        const developer2: IDeveloper = { id: 456 };
        expectedResult = service.addDeveloperToCollectionIfMissing([], developer, developer2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(developer);
        expect(expectedResult).toContain(developer2);
      });

      it('should accept null and undefined values', () => {
        const developer: IDeveloper = { id: 123 };
        expectedResult = service.addDeveloperToCollectionIfMissing([], null, developer, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(developer);
      });

      it('should return initial array if no Developer is added', () => {
        const developerCollection: IDeveloper[] = [{ id: 123 }];
        expectedResult = service.addDeveloperToCollectionIfMissing(developerCollection, undefined, null);
        expect(expectedResult).toEqual(developerCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
