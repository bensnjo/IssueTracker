import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { PieReferenceComponent } from './pie-reference/pie-reference.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([HOME_ROUTE]), NgxChartsModule],
  declarations: [HomeComponent, PieReferenceComponent ],
  schemas: [ CUSTOM_ELEMENTS_SCHEMA ]
})
export class HomeModule {}
