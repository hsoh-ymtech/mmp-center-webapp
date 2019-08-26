import { BrowserModule } from '@angular/platform-browser';
import { NgModule, ErrorHandler, APP_INITIALIZER } from '@angular/core';

import { AppComponent } from './app.component';
import { routing } from './app.routing';
import { HttpModule } from '@angular/http';
import { HttpClientModule } from '@angular/common/http';
import { COMPOSITION_BUFFER_MODE } from '@angular/forms';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatProgressBarModule, MatCheckboxModule, MatRadioModule } from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AuthGuard } from './_guards/auth.guard';

import { AuthenticationService } from './_services/auth/auth.service';

import { ReflectorService } from './_services/reflector/reflector.service';
import { QualityHistoryService } from './_services/qualityHistory/quality-history.service';
import { CurrentStatusService } from './_services/currentStatus/current-status.service';
import { ConfigService } from './_services/config/config.service';
import { AnalysisBigdataService } from './_services/AnalysisBigdata/analysis-bigdata.service';
import { ErrorsHandler } from './_services/error/error.handler';
import { ErrorService } from './_services/error/error.service';
import { MessageService } from './_services/Message/message.service';
import 'rxjs/add/operator/takeWhile';

import { AppConfig } from './_services/config/AppConfig';
export function initializeApp(appConfig: AppConfig) {
  return () => appConfig.load();
}

import { AgmCoreModule } from '@agm/core';

import { NgxSpinnerModule } from 'ngx-spinner';

import { OwlDateTimeModule, OwlNativeDateTimeModule } from 'ng-pick-datetime';

import {
  LoginComponent,
  
  DashboardComponent,
  
  QualityMeasurementComponent,
  QualityHistoryComponent,
  QualityGraphDialog,

  AnalysisBigdataComponent,
  AnalysisBigdataDialog,

  ObstacleHistoryComponent,
  
  ReflectorMgmtComponent,
  ReflectorAddDialog,
  ReflectorModifyDialog,
  
  ConfigComponent,

  ErrorComponent
} from './_views';
import { GeocodeService } from './_services/geocode/geocode.service';
import { LesionHistoryService } from './_services/lesionHistory/lesion-history.service';


@NgModule({
  declarations: [
    AppComponent,

    LoginComponent,
    DashboardComponent,
  
    QualityMeasurementComponent,
    QualityHistoryComponent,
    QualityGraphDialog,

    AnalysisBigdataComponent,
    AnalysisBigdataDialog,

    ObstacleHistoryComponent,

    ReflectorMgmtComponent,
    ReflectorAddDialog,
    ReflectorModifyDialog,
    
    ConfigComponent,

    ErrorComponent
  ],
  imports: [
    BrowserModule,
    MatDialogModule,
    MatProgressBarModule,
    BrowserAnimationsModule,
    MatCheckboxModule,
    MatRadioModule,
    HttpModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    NgxSpinnerModule,
    OwlDateTimeModule,
    OwlNativeDateTimeModule,
    AgmCoreModule.forRoot({
      apiKey: 'AIzaSyApKp8cF7q8closGar0zM5jNSM26JqEKds'
    }),
    routing
  ],
  entryComponents: [
    ReflectorAddDialog,
    ReflectorModifyDialog,
    QualityGraphDialog
  ],
  providers: [
    AppConfig,
    AuthGuard,
    AuthenticationService,
    {
      provide: APP_INITIALIZER,
      useFactory: initializeApp,
      deps: [AppConfig], multi: true
    },
    ReflectorService,
    QualityHistoryService,
    LesionHistoryService,
    AnalysisBigdataService,
    CurrentStatusService,
    ConfigService,
    {
      provide: ErrorHandler,
      useClass: ErrorsHandler,
    },
    {
      provide: COMPOSITION_BUFFER_MODE,
      useValue: false
    },
    MessageService,
    ErrorService,
    GeocodeService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
