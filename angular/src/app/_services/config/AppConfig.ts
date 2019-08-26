import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { AppconfigModel } from './appconfig.model';
import { environment } from '../../../environments/environment';

@Injectable()
export class AppConfig {

    static settings: AppconfigModel;

    constructor (
        private http: Http
    ) {

    }

    public load(): Promise<void> {
        const jsonFile = `assets/config.${environment.name}.json`;
        console.log('environment Name : ' + jsonFile);
        return new Promise<void>((resolve, reject) => {
            this.http.get(jsonFile).toPromise().then((response: Response) => {
                AppConfig.settings = <AppconfigModel>response.json();
                resolve();
            }).catch((response: any) => {
                reject(`Cloud not load file '${jsonFile}': ${JSON.stringify(response)}`);
            });
        });
    }

}
