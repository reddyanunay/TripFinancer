import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ApicallsService } from '../apicalls.service';

@Component({
  selector: 'app-start-atrip',
  templateUrl: './start-atrip.component.html',
  styleUrls: ['./start-atrip.component.css']
})
export class StartATripComponent {
  constructor(private router:Router,private ser:ApicallsService){}

  navigateToOtherPage(){
    console.log('Navigating to /app-create-trip');
    if(this.ser.isAuthentication()){
      this.router.navigate(['/app-create-trip']);
    }
    else{
      this.router.navigate(['/app-signup-form']);
    }
    
  }

}
