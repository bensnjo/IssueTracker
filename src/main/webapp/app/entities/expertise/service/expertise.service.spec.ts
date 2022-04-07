import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IExpertise, Expertise } from '../expertise.model';

import { ExpertiseService } from './expertise.service';

describe('Expertise Service', () => {
  let service: ExpertiseService;
  let httpMock: HttpTestingController;
  let elemDefault: IExpertise;
  let expectedResult: IExpertise | IExpertise[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ExpertiseService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      description: 'AAAAAAA',
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

    it('should create a Expertise', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Expertise()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Expertise', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Expertise', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          description: 'BBBBBB',
        },
        new Expertise()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Expertise', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          description: 'BBBBBB',
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

    it('should delete a Expertise', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addExpertiseToCollectionIfMissing', () => {
      it('should add a Expertise to an empty array', () => {
        const expertise: IExpertise = { id: 123 };
        expectedResult = service.addExpertiseToCollectionIfMissing([], expertise);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(expertise);
      });

      it('should not add a Expertise to an array that contains it', () => {
        const expertise: IExpertise = { id: 123 };
        const expertiseCollection: IExpertise[] = [
          {
            ...expertise,
          },
          { id: 456 },
        ];
        expectedResult = service.addExpertiseToCollectionIfMissing(expertiseCollection, expertise);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Expertise to an array that doesn't contain it", () => {
        const expertise: IExpertise = { id: 123 };
        const expertiseCollection: IExpertise[] = [{ id: 456 }];
        expectedResult = service.addExpertiseToCollectionIfMissing(expertiseCollection, expertise);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(expertise);
      });

      it('should add only unique Expertise to an array', () => {
        const expertiseArray: IExpertise[] = [{ id: 123 }, { id: 456 }, { id: 44869 }];
        const expertiseCollection: IExpertise[] = [{ id: 123 }];
        expectedResult = service.addExpertiseToCollectionIfMissing(expertiseCollection, ...expertiseArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const expertise: IExpertise = { id: 123 };
        const expertise2: IExpertise = { id: 456 };
        expectedResult = service.addExpertiseToCollectionIfMissing([], expertise, expertise2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(expertise);
        expect(expectedResult).toContain(expertise2);
      });

      it('should accept null and undefined values', () => {
        const expertise: IExpertise = { id: 123 };
        expectedResult = service.addExpertiseToCollectionIfMissing([], null, expertise, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(expertise);
      });

      it('should return initial array if no Expertise is added', () => {
        const expertiseCollection: IExpertise[] = [{ id: 123 }];
        expectedResult = service.addExpertiseToCollectionIfMissing(expertiseCollection, undefined, null);
        expect(expectedResult).toEqual(expertiseCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
