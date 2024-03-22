import { Component } from '@angular/core';

@Component({
  selector: 'app-create-trip',
  templateUrl: './create-trip.component.html',
  styleUrls: ['./create-trip.component.css']
})
export class CreateTripComponent {
  tripName: string = '';
  numMembers: number = 0;
  members: string[] = [];

  generateMemberInputs() {
    if (this.numMembers > 15) {
      // If the number of members exceeds the limit, set it to the maximum allowed value
      this.numMembers = 15;
    }
    this.members = new Array(this.numMembers).fill('').map(() => '');
  }
  submitDetails() {
    // Implement the logic to submit the trip details, such as sending an HTTP request to a server
    console.log('Trip Name:', this.tripName);
    console.log('Members:', this.members);
    // Reset the form fields after submission if needed
    this.tripName = '';
    this.numMembers = 0;
    this.members = [];
  }

}
