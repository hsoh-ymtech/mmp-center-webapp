import { Injectable } from '@angular/core';

@Injectable()
export class ErrorService {

  constructor() { }

  showAllListHttpError(error: any): void {
    if (error['status'] === 400) {
      console.log(error['error']['message']);
    } else {
      alert(error['status'] + ' : ' + error['statusText']);
    }
  }

  showSearchListHttpError(error: any): void {
    if (error['status'] === 400) {
      alert(error['error']['message']);
    } else {
      alert(error['status'] + ' : ' + error['statusText']);
    }
  }
}
