export interface IDepartment {
  id?: number;
  name?: string | null;
}

export class Department implements IDepartment {
  constructor(public id?: number, public name?: string | null) {}
}

export function getDepartmentIdentifier(department: IDepartment): number | undefined {
  return department.id;
}
