import { Component, inject } from '@angular/core';
import { ApicallsService } from '../apicalls.service';
import { Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-signup-form',
  templateUrl: './signup-form.component.html',
  styleUrls: ['./signup-form.component.css']
})
export class SignupFormComponent {
  private scall=inject(ApicallsService)
  private router=inject(Router)
  private fb = inject(FormBuilder);
  // private toastr = inject(ToastrService);

  signupForm = this.fb.group({
    
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],

    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
   
  });


  onSubmit() {
   

    if (this.signupForm.valid && this.signupForm.dirty) {

      // console.log('Form submitted successfully!',this.signupForm);
      this.scall.registerUser(this.signupForm.value).subscribe((data:any)=>{
        console.log('Form submitted successfully!',data);
        // this.toastr.success("registered successfully")
        // alert('Form submitted successfully!');
        // this.scall.sendEmail().subscribe((data: any)=> {
        //   console.log(data)
        // })
        this.router.navigate(['/app-login']);
      })
     
      
    } else {
      console.log('Please fill in all the required fields.');
      // this.toastr.error("enter valid details");
  }

  }

}
