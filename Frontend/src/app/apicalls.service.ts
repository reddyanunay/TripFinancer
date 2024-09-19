import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { UserData } from './UserData';

@Injectable({
  providedIn: 'root'
})
export class ApicallsService {
  constructor(private http: HttpClient) {}
  private apiUrl = 'http://localhost:8087';
  user:UserData={
    email:'',
    firstName:'',
    lastName:'',
    role:''
  }
  tempTripId:any;

  saveToken(token: string): void {
    localStorage.setItem('authToken', token); // or sessionStorage.setItem
  }

  getToken(): string | null {
    return localStorage.getItem('authToken'); // or sessionStorage.getItem
  }

  loginUser(userLogin:any):any{
    return this.http.post<any>(`${this.apiUrl}/api/users/login`,userLogin)
  }

  logoutUser(){
    localStorage.removeItem('user');
    localStorage.removeItem('authenticated');
    localStorage.removeItem('authToken');
  }
  setAuthentication(){
    localStorage.setItem('authenticated','true');
  }
  isAuthentication():boolean{
    return localStorage.getItem('authenticated')==='true'?true:false;
  }
  setUserData(UserLogin:any){
    localStorage.setItem('email',UserLogin.email);
    localStorage.setItem('firstName',UserLogin.firstName);
    localStorage.setItem('lastName',UserLogin.lastName);
    // localStorage.setItem('role',UserLogin.role);
  }
  getUserFromLocalStorage():any {
    this.user.email=localStorage.getItem('email') as string;
    this.user.firstName=localStorage.getItem('firstName') as string;
    this.user.lastName=localStorage.getItem('lastName') as string;
    this.user.role=localStorage.getItem('role') as string;
    return this.user;
  }


  
  postTrip(trip:any): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/trips/trip`,trip);
  }
  getTrips(email:any):Observable<any>{
    return this.http.get(`${this.apiUrl}/api/trips/getTripsWithEmail/${email}`)
  }
  deleteTrip(tripId:number):Observable<String>{
    return this.http.delete<String>(`${this.apiUrl}/api/trips/delete/${tripId}`, { responseType: 'text' as 'json' });
  }
  registerUser(user:any): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/users/register`,user);
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


  //Analysis endpoints
  getTotalTripSummary(tripId:any):Observable<any>{
    return this.http.get(`${this.apiUrl}/api/trips/${tripId}/summary`);
  }

  getAnalysisForMember(extUrl: string): Observable<any> {
    return this.http.get<any>(this.apiUrl+extUrl);
  }
}
