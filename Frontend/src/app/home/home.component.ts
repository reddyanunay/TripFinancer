import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ApicallsService } from '../apicalls.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  constructor(private router:Router,private ser:ApicallsService){}
  isLoggedIn:boolean | undefined;
  ngOnInit(): void {
    this.isLoggedIn = this.ser.isAuthentication();
  }

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
