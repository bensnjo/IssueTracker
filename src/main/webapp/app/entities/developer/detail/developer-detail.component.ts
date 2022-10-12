import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDeveloper } from '../developer.model';

@Component({
  selector: 'jhi-developer-detail',
  templateUrl: './developer-detail.component.html',
})
export class DeveloperDetailComponent implements OnInit {
  developer: IDeveloper | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ developer }) => {
      this.developer = developer;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
