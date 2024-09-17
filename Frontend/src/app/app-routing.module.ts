import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StartATripComponent } from './start-atrip/start-atrip.component';
import { CreateTripComponent } from './create-trip/create-trip.component';
import { BillsComponent } from './bills/bills.component';
import { SignupFormComponent } from './signup-form/signup-form.component';

const routes: Routes = [
  {path:'',redirectTo:'home',pathMatch:'full'},
  {path:'home',component:StartATripComponent},
  {path:'app-signup-form', component : SignupFormComponent},
  {path:'app-create-trip', component : CreateTripComponent},
  {path:'app-bills', component : BillsComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
