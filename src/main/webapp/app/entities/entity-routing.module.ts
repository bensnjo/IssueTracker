import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'issue',
        data: { pageTitle: 'Issues' },
        loadChildren: () => import('./issue/issue.module').then(m => m.IssueModule),
      },
      {
        path: 'category',
        data: { pageTitle: 'Categories' },
        loadChildren: () => import('./category/category.module').then(m => m.CategoryModule),
      },
      {
        path: 'product',
        data: { pageTitle: 'Products' },
        loadChildren: () => import('./product/product.module').then(m => m.ProductModule),
      },
      {
        path: 'developer',
        data: { pageTitle: 'Developers' },
        loadChildren: () => import('./developer/developer.module').then(m => m.DeveloperModule),
      },
      {
        path: 'department',
        data: { pageTitle: 'Departments' },
        loadChildren: () => import('./department/department.module').then(m => m.DepartmentModule),
      },
      {
        path: 'expertise',
        data: { pageTitle: 'Expertise' },
        loadChildren: () => import('./expertise/expertise.module').then(m => m.ExpertiseModule),
      },
      {
        path: 'priority',
        data: { pageTitle: 'Priorities' },
        loadChildren: () => import('./priority/priority.module').then(m => m.PriorityModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
