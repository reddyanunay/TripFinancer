import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';  // For buttons if needed
import { MatIconModule } from '@angular/material/icon';
import { AppComponent } from './app.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { provideCharts, withDefaultRegisterables } from 'ng2-charts';
import { HeaderComponent } from './header/header.component';
import { BillsComponent } from './bills/bills.component';
import { CreateTripComponent } from './create-trip/create-trip.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { SignupFormComponent } from './signup-form/signup-form.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoginComponent } from './login/login.component';
import { UserProfileComponent } from './user-profile/user-profile.component';
import { AuthInterceptor } from './auth.interceptor';
import { HomeComponent } from './home/home.component';
import { AnalysisComponent } from './analysis/analysis.component';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { BaseChartDirective } from 'ng2-charts';
import { CommonModule, registerLocaleData } from '@angular/common';
import { NZ_I18N } from 'ng-zorro-antd/i18n';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzMenuModule } from 'ng-zorro-antd/menu';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzMessageModule } from 'ng-zorro-antd/message';
import { NzProgressModule } from 'ng-zorro-antd/progress';
import { NzPaginationModule } from 'ng-zorro-antd/pagination';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { NzPopoverModule } from 'ng-zorro-antd/popover';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzPageHeaderModule } from 'ng-zorro-antd/page-header';
import { NzListModule } from 'ng-zorro-antd/list';
import { NzDescriptionsModule } from 'ng-zorro-antd/descriptions';
import { NzAlertModule } from 'ng-zorro-antd/alert';
import { en_US } from 'ng-zorro-antd/i18n';
import en from '@angular/common/locales/en';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

registerLocaleData(en);


@NgModule({ declarations: [
        AppComponent,
        HeaderComponent,
        BillsComponent,
        CreateTripComponent,
        SignupFormComponent,
        LoginComponent,
        UserProfileComponent,
        HomeComponent,
        AnalysisComponent,
    ],
    bootstrap: [AppComponent], imports: [BrowserModule,
        AppRoutingModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule, // If you use buttons
        MatIconModule,
        NgxChartsModule,
        BaseChartDirective,
        CommonModule,
        NgxDatatableModule,
        NzCardModule,
        NzGridModule,
        NzListModule,
        NzTableModule,
        NzLayoutModule,
        NzProgressModule,
        NzPageHeaderModule,
        NzPopoverModule,
        NzAlertModule,
        NzPaginationModule,
        NzMessageModule,
        NzDescriptionsModule,
        NzIconModule,
        NzMenuModule,
        NzDropDownModule,
        NzButtonModule,
        FormsModule], providers: [{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }, provideCharts(withDefaultRegisterables()), provideHttpClient(withInterceptorsFromDi()), { provide: NZ_I18N, useValue: en_US }, provideAnimationsAsync(), provideHttpClient()] })
export class AppModule { }
