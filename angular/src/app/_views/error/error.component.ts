import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
    templateUrl: 'error.component.html'
})
export class ErrorComponent implements OnInit {

    routeParams;

    constructor(
      private activatedRoute: ActivatedRoute,
    ) { }

    ngOnInit() {
      this.routeParams = this.activatedRoute.snapshot.queryParams;
    }

}
