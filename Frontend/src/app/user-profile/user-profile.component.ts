import { Component } from '@angular/core';
import { ApicallsService } from '../apicalls.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent{
  trips:any[] = [];

  ngOnInit(): void {
    this.fetchTrips();
  }
  // getBills(): void {
  //   this.billService.getBills(sessionStorage.getItem('tripId')).subscribe(
  //     (data: any[]) => {
  //       this.bills = data;
  //     },
  //     (error) => {
  //       console.error('Error fetching bills', error);
  //     }
  //   );
  // }


  constructor(private ser: ApicallsService, private router: Router) {
  }

  fetchTrips() {
    // Replace with your actual service call to fetch trips
    this.ser.getTrips(this.ser.getUserFromLocalStorage().email).subscribe((data)=>{
      this.trips = data;
      console.log(this.trips);
    })
  }

  startNewTrip() {
    // Logic to create a new trip
    this.router.navigate(['/app-create-trip']); // Navigate to a create trip page
  }

  navigateToTrip(tripId: number) {
    // Navigate to the trip details page
    this.ser.tempTripId=tripId;
    this.router.navigate(['/app-bills']);
  }
  confirmDelete(event: MouseEvent, tripId: number): void {
    event.stopPropagation(); // Prevents the click event from propagating to the list item
    if (confirm('Are you sure you want to delete this trip?')) {
      this.deleteTrip(tripId);
    }
  }
  deleteTrip(tripId: number): void {
    this.ser.deleteTrip(tripId).subscribe(
      response => {
        // Assuming the response is a string message
        alert(response); // Show the response message (e.g., "Trip deleted successfully.")
        
        // Optionally, you can also refresh the list of trips
        this.ser.getTrips(this.ser.getUserFromLocalStorage().email).subscribe(
          trips => {
            this.trips = trips; // Update the trips list with the refreshed data
          },
          error => {
            console.error('Error fetching trips:', error);
            alert('There was an error refreshing the trip list.');
          }
        );
      },
      error => {
        console.error('Error deleting trip:', error);
        alert('There was an error deleting the trip.');
      }
    );
  }

}
