export interface IPriority {
  id?: number;
  name?: string;
  description?: string | null;
  sla?: number;
}

export class Priority implements IPriority {
  constructor(public id?: number, public name?: string, public description?: string | null, public sla?: number) {}
}

export function getPriorityIdentifier(priority: IPriority): number | undefined {
  return priority.id;
}
