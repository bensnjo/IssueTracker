import { IDeveloper } from 'app/entities/developer/developer.model';

export interface IExpertise {
  id?: number;
  name?: string | null;
  description?: string | null;
  developers?: IDeveloper[] | null;
}

export class Expertise implements IExpertise {
  constructor(
    public id?: number,
    public name?: string | null,
    public description?: string | null,
    public developers?: IDeveloper[] | null
  ) {}
}

export function getExpertiseIdentifier(expertise: IExpertise): number | undefined {
  return expertise.id;
}
