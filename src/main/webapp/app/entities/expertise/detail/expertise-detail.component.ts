import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IExpertise } from '../expertise.model';

@Component({
  selector: 'jhi-expertise-detail',
  templateUrl: './expertise-detail.component.html',
})
export class ExpertiseDetailComponent implements OnInit {
  expertise: IExpertise | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ expertise }) => {
      this.expertise = expertise;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
