import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApicallsService {
  constructor(private http: HttpClient) {}
  private apiUrl = 'http://localhost:8087';

  loginUser(userLogin:any):any{
    this.setAuthentication();
    return this.http.post<any>('',userLogin)
  }

  logoutUser(){
    localStorage.removeItem('user');
    localStorage.removeItem('authenticated');
  }
  setAuthentication(){
    localStorage.setItem('authenticated','true');
  }
  isAuthentication():boolean{
    return localStorage.getItem('authenticated')==='true'?true:false;
  }


  
  postTrip(trip:any): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/trips/trip`,trip).pipe(
      tap((response: any) => {
        const tripId = response?.tripId;
        if (tripId) {
          sessionStorage.setItem('tripId', tripId);
        }
      })
    );
  }
  registerUser(user:any): Observable<any> {
    return this.http.post('',user);
  }

  //here data should be of format Map<string,object> (memberNames,trip) as fields,
  // memberNames is a array of strings and trip is a trip object
  postMembers(data:any){
    return this.http.post('',data);
  }
  getMembers(tripId:any):Observable<any>{
    return this.http.get(`${this.apiUrl}/api/members/getAllMembers/${tripId}`);
  }
  getBills(tripId:any) : Observable<any>{
    return this.http.get(`${this.apiUrl}/api/bills/all/${tripId}`);
  }
  createBill(bill:any) : Observable<any>{
    console.log(bill);
    return this.http.post(`${this.apiUrl}/api/bills/createWithExpenses`,bill);
  }
  updateBill(bill:any) : Observable<any>{
    return this.http.put(`${this.apiUrl}/api/bills/update`,bill);
  }
  deleteBill(billId:any) : Observable<any>{
    return this.http.delete<string>(`${this.apiUrl}/api/bills/delete/${billId}`, { responseType: 'text' as 'json' });
  }
}
