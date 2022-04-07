import { IExpertise } from 'app/entities/expertise/expertise.model';
import { IProduct } from 'app/entities/product/product.model';

export interface IDeveloper {
  id?: number;
  staffNo?: string;
  fullName?: string;
  email?: string;
  phoneNumber?: string;
  expertise?: IExpertise[] | null;
  products?: IProduct[] | null;
}

export class Developer implements IDeveloper {
  constructor(
    public id?: number,
    public staffNo?: string,
    public fullName?: string,
    public email?: string,
    public phoneNumber?: string,
    public expertise?: IExpertise[] | null,
    public products?: IProduct[] | null
  ) {}
}

export function getDeveloperIdentifier(developer: IDeveloper): number | undefined {
  return developer.id;
}
