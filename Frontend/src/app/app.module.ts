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
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { SignupFormComponent } from './signup-form/signup-form.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoginComponent } from './login/login.component';
import { UserProfileComponent } from './user-profile/user-profile.component';
import { AuthInterceptor } from './auth.interceptor';
import { HomeComponent } from './home/home.component';
import { AnalysisComponent } from './analysis/analysis.component';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { BaseChartDirective } from 'ng2-charts';
import { CommonModule } from '@angular/common';


@NgModule({
  declarations: [
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
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,   // If you use buttons
    MatIconModule,
    NgxChartsModule,
    BaseChartDirective,
    CommonModule,
    NgxDatatableModule,
  ],
  providers: [{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }, provideCharts(withDefaultRegisterables())],
  bootstrap: [AppComponent]
})
export class AppModule { }
