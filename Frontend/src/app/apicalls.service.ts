import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApicallsService {
  constructor(private http: HttpClient) {}
  private apiUrl = 'http://localhost:8087';


  
  postTrip(trip:any): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/trips/trip`,trip);
  }

  //here data should be of format Map<string,object> (memberNames,trip) as fields,
  // memberNames is a array of strings and trip is a trip object
  postMembers(data:any){
    return this.http.post('',data);
  }
  getMembers(tripId:any):Observable<any>{
    return this.http.get(`${this.apiUrl}/api/members/getAllMembers/${tripId}`);
  }
  getBills() : Observable<any>{
    return this.http.get(`${this.apiUrl}/api/bills/all`);
  }
  createBill(bill:any) : Observable<any>{
    return this.http.post('',bill);
  }
  updateBill(bill:any) : Observable<any>{
    return this.http.put('',bill);
  }
}
