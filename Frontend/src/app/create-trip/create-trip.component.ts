 import { Component } from '@angular/core';
import { ApicallsService } from '../apicalls.service';
import { FormArray, FormBuilder, FormGroup} from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-trip',
  templateUrl: './create-trip.component.html',
  styleUrls: ['./create-trip.component.css'] // add the ReactiveFormsModule to the imports array
})
export class CreateTripComponent {
  tripForm: FormGroup;
  Trip:any = {};

  constructor(private fb: FormBuilder,private apiser:ApicallsService,private router:Router) {
    this.tripForm = this.fb.group({
      trip_id: [],
      trip_name: [''],
      no_of_people: [0],
      members: this.fb.array([])
    });
  }

  get members() {
    return this.tripForm.get('members') as FormArray;
  }

  onNumberOfPeopleChange() {
    const numberOfMembers = Math.min(this.tripForm.value.no_of_people, 15);
    while (this.members.length !== numberOfMembers) {
      if (this.members.length < numberOfMembers) {
        this.members.push(this.fb.control(''));
      } else {
        this.members.removeAt(this.members.length - 1);
      }
    }
  }
  submitDetails() {
    const formValue = this.tripForm.value;
    const tripData = {
      tripName: formValue.trip_name,
      noOfPeople: formValue.no_of_people,
      members: formValue.members
    };
    console.log('Trip data:', tripData);
    this.apiser.postTrip(tripData).subscribe(
      (response:any)=>{
        this.Trip=response;
      },
      (error: any) => {
        console.error('Error posting trip:', error);
      }
    );
    this.router.navigate(['/app-bills']);
  }
}
