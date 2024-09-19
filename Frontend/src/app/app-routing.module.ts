import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreateTripComponent } from './create-trip/create-trip.component';
import { BillsComponent } from './bills/bills.component';
import { SignupFormComponent } from './signup-form/signup-form.component';
import { LoginComponent } from './login/login.component';
import { UserProfileComponent } from './user-profile/user-profile.component';
import { authGuard } from './auth.guard';
import { HomeComponent } from './home/home.component';
import { AnalysisComponent } from './analysis/analysis.component';

const routes: Routes = [
  {path:'',redirectTo:'home',pathMatch:'full'},
  {path:'home',component:HomeComponent},
  {path:'app-signup-form', component : SignupFormComponent},
  {path:'app-create-trip', component : CreateTripComponent,canActivate:[authGuard]},
  {path:'app-bills', component : BillsComponent,canActivate:[authGuard]},
  {path:'app-login', component : LoginComponent},
  {path:'app-analysis',component:AnalysisComponent,canActivate:[authGuard]},
  {path:'app-user-profile',component:UserProfileComponent,canActivate:[authGuard]},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
