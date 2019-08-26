import { Routes, RouterModule } from '@angular/router';

import { AuthGuard } from './_guards/auth.guard';

import { 
    LoginComponent,
    
    DashboardComponent,
    
    QualityMeasurementComponent,
    QualityHistoryComponent,
    
    AnalysisBigdataComponent,
    
    ObstacleHistoryComponent,
    
    ReflectorMgmtComponent,
    
    ConfigComponent
} from './_views';

const appRoutes: Routes = [
    { path: '', component: DashboardComponent, canActivate: [AuthGuard] },
    { path: 'login', component: LoginComponent },
    { path: 'dashboard', component: DashboardComponent },
    { path: 'qualityMeasurement', component: QualityMeasurementComponent },
    { path: 'qualityHistory', component: QualityHistoryComponent },
    { path: 'analysisBigdata', component: AnalysisBigdataComponent },
    { path: 'obstacle', component: ObstacleHistoryComponent },
    { path: 'reflectorMgmt', component: ReflectorMgmtComponent },
    { path: 'config', component: ConfigComponent },

    { path: '**', redirectTo: '' }
];

export const routing = RouterModule.forRoot(appRoutes, {
    useHash: true
  });
