import { Component } from '@angular/core';
import { ApicallsService } from '../apicalls.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  constructor(public ser:ApicallsService){
  }

  onlogout(){
    this.ser.logoutUser();
  }


}
