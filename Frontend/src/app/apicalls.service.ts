import { Injectable } from '@angular/core';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ApicallsService {
  constructor(private http: HttpClient) {}


  
  postTrip(trip:any) {
    return this.http.post('',trip);
  }

  //here data should be of format Map<string,object> (memberNames,trip) as fields,
  // memberNames is a array of strings and trip is a trip object
  postMembers(data:any){
    return this.http.post('',data);
  }
}
