import { Component } from '@angular/core';
import { ApicallsService } from '../apicalls.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  constructor(public ser:ApicallsService,private router:Router){
  }
  yourTrips(){
    this.router.navigate(['/app-user-profile']);
  }

  onlogout(){
    this.ser.logoutUser();
    this.router.navigate(['/home']);
  }
}
