import dayjs from 'dayjs/esm';
import { ICategory } from 'app/entities/category/category.model';
import { IProduct } from 'app/entities/product/product.model';
import { IDeveloper } from 'app/entities/developer/developer.model';
import { IPriority } from 'app/entities/priority/priority.model';
import { IDepartment } from 'app/entities/department/department.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface IIssue {
  id?: number;
  defectNumber?: string;
  description?: string | null;
  version?: string | null;
  status?: Status;
  dateIdentified?: dayjs.Dayjs;
  dateClosed?: dayjs.Dayjs | null;
  comments?: string | null;
  category?: ICategory | null;
  product?: IProduct | null;
  assignee?: IDeveloper | null;
  priority?: IPriority | null;
  department?: IDepartment | null;
}

export class Issue implements IIssue {
  static status: import("/home/benson/Desktop/IssueTracker/src/main/webapp/app/entities/enumerations/status.model").Status;
  constructor(
    public id?: number,
    public defectNumber?: string,
    public description?: string | null,
    public version?: string | null,
    public status?: Status,
    public dateIdentified?: dayjs.Dayjs,
    public dateClosed?: dayjs.Dayjs | null,
    public comments?: string | null,
    public category?: ICategory | null,
    public product?: IProduct | null,
    public assignee?: IDeveloper | null,
    public priority?: IPriority | null,
    public department?: IDepartment | null
  ) {}
}

export function getIssueIdentifier(issue: IIssue): number | undefined {
  return issue.id;
}
