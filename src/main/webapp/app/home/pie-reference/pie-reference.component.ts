import { Component, OnInit } from '@angular/core';
import { Color, ScaleType } from '@swimlane/ngx-charts';


@Component({
  selector: 'jhi-app-product-pie-charts',
  templateUrl: './pie-reference.component.html',
  styleUrls: ['./pie-reference.component.scss']
})
export class PieReferenceComponent implements OnInit {

  issuesTally: any[];


  view: [number, number] = [800, 400];

  // options
  showLegend = true;
  showLabels = true;

  gradient = false;
  isDoughnut = true;

  legendPosition = 'below';

  colorScheme: Color = {
    name: 'myScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#000000', '#DA3832', '#5B6FC8'],
  };
 
  constructor() {


    Object.assign(this, { this: this.issuesTally });
  }

  ngOnInit(): void {
    const datae = localStorage.getItem('issueTally');
    this.issuesTally = JSON.parse(datae?? '');
  }

  onActivate(data: any): void {
    console.log('Activate', JSON.parse(JSON.stringify(data))); // eslint-disable-line no-console
  }

  onDeactivate(data: any): void {
    console.log('Deactivate', JSON.parse(JSON.stringify(data))); // eslint-disable-line no-console
  }

  onSelect(data: any): void {
    console.log('Item clicked', JSON.parse(JSON.stringify(data))); // eslint-disable-line no-console
  }
}

