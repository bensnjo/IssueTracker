import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DeveloperComponent } from './list/developer.component';
import { DeveloperDetailComponent } from './detail/developer-detail.component';
import { DeveloperUpdateComponent } from './update/developer-update.component';
import { DeveloperDeleteDialogComponent } from './delete/developer-delete-dialog.component';
import { DeveloperRoutingModule } from './route/developer-routing.module';

@NgModule({
  imports: [SharedModule, DeveloperRoutingModule],
  declarations: [DeveloperComponent, DeveloperDetailComponent, DeveloperUpdateComponent, DeveloperDeleteDialogComponent],
  entryComponents: [DeveloperDeleteDialogComponent],
})
export class DeveloperModule {}
