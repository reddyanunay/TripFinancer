import { Component } from '@angular/core';
import { ApicallsService } from '../apicalls.service';

@Component({
  selector: 'app-create-trip',
  templateUrl: './create-trip.component.html',
  styleUrls: ['./create-trip.component.css']
})
export class CreateTripComponent {
  constructor(private apiser:ApicallsService){}
  members: string[] = [];
  Trip={
    tripId:0,
    trip_name:'',
    no_of_people:0
  }
  get memberInput(){
    return{
      memberNames:this.members,
      trip:this.Trip,
    }
  }

  generateMemberInputs() {
    if (this.Trip.no_of_people > 15) {
      this.Trip.no_of_people = 15;
    }
    this.members = new Array(this.Trip.no_of_people).fill('').map(() => '');
  }
  submitDetails() {

    this.apiser.postTrip(this.Trip).subscribe(
      (trip:any)=>{
        this.Trip.tripId=trip.tripId;
      }
    );
    this.apiser.postMembers(this.memberInput).subscribe(alert("members inserted"));
    
    // Reset the form fields after submission if needed
    // this.Trip.tripId = 0;
    // this.Trip.trip_name = '';
    // this.Trip.no_of_people = 0;
    // this.members = [];
  }


}
