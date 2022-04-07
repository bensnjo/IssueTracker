import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ExpertiseComponent } from './list/expertise.component';
import { ExpertiseDetailComponent } from './detail/expertise-detail.component';
import { ExpertiseUpdateComponent } from './update/expertise-update.component';
import { ExpertiseDeleteDialogComponent } from './delete/expertise-delete-dialog.component';
import { ExpertiseRoutingModule } from './route/expertise-routing.module';

@NgModule({
  imports: [SharedModule, ExpertiseRoutingModule],
  declarations: [ExpertiseComponent, ExpertiseDetailComponent, ExpertiseUpdateComponent, ExpertiseDeleteDialogComponent],
  entryComponents: [ExpertiseDeleteDialogComponent],
})
export class ExpertiseModule {}
