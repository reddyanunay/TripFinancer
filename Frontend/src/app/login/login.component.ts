import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ApicallsService } from '../apicalls.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  private scall=inject(ApicallsService);
  private router=inject(Router)
  private fb = inject(FormBuilder);

  errorMessage:string='';
  // private toastr = inject(ToastrService);

  loginForm = this.fb.group({
    

    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
   
   
  });

  onSubmit() {
    if (this.loginForm.valid && this.loginForm.dirty) {
      this.scall.loginUser(this.loginForm.value).subscribe((data:any)=>{
      console.log(data);
        if(data["user"])
          { 
            this.scall.saveToken(data["map"]["token"]);
            this.scall.setAuthentication();
            this.scall.setUserData(data['user']);

            setTimeout(() => {
              console.log('Form submitted successfully!',data);
                this.router.navigate(['/app-user-profile']);
              }
            , 1000);
          }
          else{
            console.error('Login failed: Invalid credentials');
            this.errorMessage = 'Invalid email or password. Please try again.';
          }
      }),
      (error:any) =>{
        console.error('Error:', error);
        this.errorMessage = 'An error occurred. Please try again later.';
      }
    } 
    else {
      console.log('Please fill the valid credentials');
    }
  }
}
