import { Component, OnInit, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material';

import { AgmInfoWindow, MouseEvent, MapTypeStyle } from '@agm/core';

import { NgxSpinnerService } from 'ngx-spinner';
import { ReflectorService } from 'src/app/_services/reflector/reflector.service';

import { Reflector } from '../../_models/Reflector';


interface marker {
    lat: number;
    lng: number;
    label?: Object | string;
    draggable: boolean;
    locale?: string;
    address?: string;
    condition?: string;
    icon?: string;
}


@Component({
    templateUrl: 'dashboard.component.html'
})
export class DashboardComponent implements OnInit, OnDestroy {

    public zoom = 1.5;
    public lat = 36.409522;
    public lng = 127.831503;
    public clickedmarker: marker;

    public ReflectorData: Reflector[];

    /**
     * 현재 Map에 표시되는 Markers Array
     */
    public markers: any[] = [];

    public mapstyle: MapTypeStyle[] = [
        {
            elementType: 'labels',
            featureType: 'road',
            stylers: [
                { visibility: 'off' }
            ]
        }
    ];


    constructor(
        private dialog: MatDialog,
        private spinner: NgxSpinnerService,
        private reflectorService: ReflectorService
    ) {
        this.requestReflectorAllList();
    }
    ngOnInit() {
        this.spinner.show();
 
        setTimeout(() => {
            /** spinner ends after 5 seconds */
            this.spinner.hide();
        }, 1000);
    }
    ngOnDestroy() {
    }



    /**
     * Marker Mouse Over Event
     * @param infoWindow
     *                  AgmInfoWindow
     * @param gm
     *                  Agm Map
     * @param m
     *                  Marker Object
     */
    public markerMouseOver(infoWindow: AgmInfoWindow, gm: any, m: marker): void {
        console.log('Marker Mouse Over');
        // if (gm.lastOpen != null) {
        //     gm.lastOpen.close();
        // }
        this.clickedmarker = m;
        gm.lastOpen = infoWindow;
        infoWindow.open();
    }

    /**
     * Marker Mouse Out Event
     * @param infoWindow
     *                  AgmInfoWindow
     */
    public markerMouseout(infoWindow: AgmInfoWindow): void {
        console.log('Marker Mouse Out');
        infoWindow.close();
    }

    private requestReflectorAllList(): void {
        const that = this;
        this.reflectorService.getReflectorListPageable(0, 1000000000, '').subscribe(
            response => {
                console.log(response['message']);
                that.ReflectorData = response['result']['content'];
                that.makeMarker(that.ReflectorData);
            }, error => {
                alert('Reflector Data를 받아오는중 오류 발생');
            }
        )
    }

    private makeMarker(reflectorData: Reflector[]): void {
        for (let a = 0; a < reflectorData.length; a++) {
            const tmpMk = {};
            tmpMk['label'] = reflectorData[a].reflectorId + '';
            tmpMk['lat'] = reflectorData[a].lat;
            tmpMk['lng'] = reflectorData[a].lng;
            tmpMk['address'] = reflectorData[a].address;
            tmpMk['draggable'] = false;
            tmpMk['ip'] = reflectorData[a].reflectorIp;
            tmpMk['port'] = reflectorData[a].port;
            if (reflectorData[a].address !== 'test') {
                this.markers.push(tmpMk);
            }
        }
    }
}
