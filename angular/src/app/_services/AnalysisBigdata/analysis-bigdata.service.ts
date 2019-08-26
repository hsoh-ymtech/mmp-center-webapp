import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { Url } from '../../_models/url';
import { AnalysisBigdata } from '../../_models/AnalysisBigdata';

@Injectable()
export class AnalysisBigdataService {
    private url = new Url();

    constructor(
        private httpClient: HttpClient
    ) { }


    RequestAnalysisBigdata(asData: AnalysisBigdata, page: number, size: number, sort: string): Observable<Object> {
        return this.httpClient.post(this.url.getUrl() + '/analysis', asData, {
            params: {
                'page' : '' + page,
                'size' : '' + size,
                'sort' : sort
            }
        });
    }
}