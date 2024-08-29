import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-start-atrip',
  templateUrl: './start-atrip.component.html',
  styleUrls: ['./start-atrip.component.css']
})
export class StartATripComponent {
  constructor(private router:Router){}

  navigateToOtherPage(){
    console.log('Navigating to /app-create-trip');
    this.router.navigate(['/app-create-trip']);
  }

}
