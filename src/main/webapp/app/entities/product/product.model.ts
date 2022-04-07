import { IDeveloper } from 'app/entities/developer/developer.model';

export interface IProduct {
  id?: number;
  name?: string;
  description?: string | null;
  developers?: IDeveloper[] | null;
}

export class Product implements IProduct {
  constructor(public id?: number, public name?: string, public description?: string | null, public developers?: IDeveloper[] | null) {}
}

export function getProductIdentifier(product: IProduct): number | undefined {
  return product.id;
}
