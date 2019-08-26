import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { Url } from '../../_models/url';
import { CurrentStatus } from '../../_models/CurrentStatus';

@Injectable()
export class CurrentStatusService {
  private url = new Url();

  constructor(
    private httpClient: HttpClient
  ) { }

  getCurrentStatusListPageable(page: number, size: number, sort: string): Observable<Object> {
    return this.httpClient.get(this.url.getUrl() + '/current-status', {
      params: {
        'page' : '' + page,
        'size' : '' + size,
        'sort' : sort
      }
    });
  }

  startQualityMeasure(currentStatus: CurrentStatus): Observable<Object> {
    return this.httpClient.post(this.url.getUrl() + '/current-status', currentStatus);
  }
  startMeasureStop(currentStatus: CurrentStatus): Observable<Object> {
    return this.httpClient.post(this.url.getUrl() + '/current-status/' + currentStatus.sessId + '/stop-measure', {});
  }
}
