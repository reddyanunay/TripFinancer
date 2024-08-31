import { Component } from '@angular/core';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { ApicallsService } from '../apicalls.service';

@Component({
  selector: 'app-bills',
  templateUrl: './bills.component.html',
  styleUrls: ['./bills.component.css']
})
export class BillsComponent {
  bills: any[] = [];
  allMembers: any[] = [];
  isModalOpen = false;
  selectedBill: any = null;
  billForm: FormGroup;

  constructor(private fb: FormBuilder, private billService: ApicallsService) {
    this.billForm = this.fb.group({
      billId: [null], // To capture the bill ID when editing
      billAmount: [''],
      paidByMember: [''], // Consider updating this to handle member objects
      trip: [''], // Consider updating this to handle trip objects
      description: [''],
      bill_all_expenses: this.fb.array([]) // For future expansion
    });
  }

  ngOnInit(): void {
    this.getBills();
    this.getMembers();
    console.log(this.allMembers);
  }
  get members(): FormArray {
    return this.billForm.get('members') as FormArray;
  }
  getMembers(){
    this.billService.getMembers().subscribe(
      (data: any[]) => {
        this.allMembers = data;
        this.initializeMembersFormArray();
      },
      (error) => {
        console.error('Error fetching members', error);
      }
    );
  }
  initializeMembersFormArray(): void {
    this.allMembers.forEach(() => this.members.push(this.fb.control(false)));
  }

  getBills(): void {
    this.billService.getBills().subscribe(
      (data: any[]) => {
        console.log(data);
        this.bills = data;
        console.log(this.bills);
      },
      (error) => {
        console.error('Error fetching bills', error);
      }
    );
  }

  openBillModal(bill: any = null): void {
    this.selectedBill = bill;
    this.isModalOpen = true;

    if (bill) {
      // Populate the form with the selected bill details
      this.billForm.patchValue({
        billId: bill.billId,
        billAmount: bill.billAmount,
        paidByMember: bill.paidByMember,
        trip: bill.trip,
        description: bill.description
      });
    } else {
      // Reset the form for creating a new bill
      this.billForm.reset();
    }
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedBill = null;
  }

  onSubmit(): void {
    const billData = this.billForm.value;

    if (this.selectedBill) {
      // Update the existing bill
      this.billService.updateBill(billData).subscribe(
        (updatedBill) => {
          const index = this.bills.findIndex(b => b.billId === updatedBill.billId);
          this.bills[index] = updatedBill;
          this.closeModal();
        },
        (error) => {
          console.error('Error updating bill', error);
        }
      );
    } else {
      // Add a new bill
      this.billService.createBill(billData).subscribe(
        (newBill) => {
          this.bills.push(newBill);
          this.closeModal();
        },
        (error) => {
          console.error('Error creating bill', error);
        }
      );
    }
  }
}
