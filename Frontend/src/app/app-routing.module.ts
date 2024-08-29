import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StartATripComponent } from './start-atrip/start-atrip.component';
import { CreateTripComponent } from './create-trip/create-trip.component';

const routes: Routes = [
  {path:'', component : StartATripComponent},
  {path:'app-create-trip', component : CreateTripComponent},
  {path:'app-bills', component : CreateTripComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
