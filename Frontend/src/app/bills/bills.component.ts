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
  expenseList: any[] = [];
  isModalOpen = false;
  selectedBill: any = null;
  billForm: FormGroup;

  constructor(private fb: FormBuilder, private billService: ApicallsService) {
    this.billForm = this.fb.group({
      billId: [null], 
      billAmount: [''],
      paidByMember: [''], 
      trip: [''], 
      description: [''],
      members: this.fb.array([]) // Initialize the members form array here
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

  // Get members from Backend
  getMembers() {
    this.billService.getMembers(sessionStorage.getItem('tripId')).subscribe(
      (data: any[]) => {
        this.allMembers = data;
        console.log(this.allMembers);
        this.initializeNewMembersFormArray();
      },
      (error) => {
        console.error('Error fetching members', error);
      }
    );
  }

  // After getting members from Backend set them in the form array
  initializeNewMembersFormArray(): void {
    const membersArray = this.members;
  
    // Clear the form array to avoid duplicates
    while (membersArray.length !== 0) {
      membersArray.removeAt(0);
    }

    this.allMembers.forEach(member => {
      membersArray.push(this.fb.group({
        memberId: [member.memberId],
        name: [member.name],
        selected: [false], // This controls whether the member is included in the bill or not
        amount: [0] // You can set the default amount as 0 or leave it blank
      }));
    });
  }
  initializeUpdateMembersFormArray(bill:any): void {
    const membersArray = this.members;

    // Clear the form array to avoid duplicates
    while (membersArray.length !== 0) {
      membersArray.removeAt(0);
    }

    const expenses = bill.allExpenses || []; // Adjust property name based on actual data structure

    // Get the ID of the member who paid for the bill
    const paidByMemberId = bill.paidByMemberId; // Adjust property name based on actual data structure

    this.allMembers.forEach((member, index) => {
      // Find the corresponding expense for each member
      const expense = expenses.find((e: { memberId: any; }) => e.memberId === member.memberId);

      membersArray.push(this.fb.group({
        memberId: [member.memberId],
        name: [member.name],
        selected: [!!expense], // Set to true if the member is in expenses
        amount: [expense ? expense.amount : 0], // Set the amount if exists, otherwise 0
      }));

      // Set the paidByMember value if the member is the one who paid
      if (member.memberId === paidByMemberId) {
        this.setPaidByMember(index); // Use the function to set the payer
      }
    });
  }

  //Setting the paid by member
  setPaidByMember(index: number): void {
    const selectedMember = this.members.at(index).get('memberId')?.value;
    this.billForm.patchValue({ paidByMember: selectedMember });
  }

  // Get all the bills from Backend
  getBills(): void {
    this.billService.getBills(sessionStorage.getItem('tripId')).subscribe(
      (data: any[]) => {
        this.bills = data;
      },
      (error) => {
        console.error('Error fetching bills', error);
      }
    );
  }

  //Opening the floating window
  openBillModal(bill: any = null): void {
    this.selectedBill = bill;
    this.isModalOpen = true;

    if (bill) {
      console.log('Selected bill:', bill);
      this.billForm.patchValue({
        billId: bill.billId,
        billAmount: bill.billAmount,
        paidByMember: bill.paidByMember?.memberId,
        trip: bill.trip,
        description: bill.description
      });

      // Update members with the selected bill's data
      this.billForm.setControl('members', this.fb.array([])); // Reset members array
      this.initializeUpdateMembersFormArray(bill);
      // this.updateMembersWithExpenses(bill.allExpenses) // Reinitialize with members and update their selections if necessary
    } else {
      this.billForm.reset();
      this.initializeNewMembersFormArray(); // Reinitialize for a new bill
    }
  }

  // updateMembersWithExpenses(expenses: any[]): void {
  //   const membersArray = this.members;
    
  //   // Iterate over each expense and update the members form array
  //   expenses.forEach(expense => {
  //     const memberControl = membersArray.controls.find(control => control.get('memberId')?.value === expense.member.memberId);
      
  //     if (memberControl) {
  //       // If the member is part of the bill's expenses, mark them as selected and set their amount
  //       memberControl.patchValue({
  //         selected: true,
  //         amount: expense.share // Set the member's share
  //       });
  //     }
  //   });
  // }
  
  

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedBill = null;
  }

  isPaidByMember(index: number): boolean {
    return this.billForm.get('paidByMember')?.value === this.members.at(index).get('memberId')?.value;
  }

  validateShares(): boolean {
    const totalShare = this.members.controls
    .filter(memberControl => memberControl.get('selected')?.value) // Only include selected members
    .reduce((sum, memberControl) => {
      const amount = memberControl.get('amount')?.value || 0;
      return sum + amount;
    }, 0);
      const billAmount = this.billForm.get('billAmount')?.value;
      // Ensure the total share is exactly equal to the bill amount
      if (totalShare !== billAmount) {
        alert(`The total shares must sum exactly to the bill amount (${billAmount}). Current total: ${totalShare}`);
        return false;
      }
    return true;
  }


  onMemberSelectChange(index: number): void {
    const memberControl = this.members.at(index);
    const selected = memberControl.get('selected')?.value;
    // Enable/disable the amount input field based on whether the member is selected
    if (!selected) {
      memberControl.get('amount')?.setValue(0); // Reset amount to 0 if unchecked
    }
  }

  calculateExpenseList(members: any[]): void {
    this.expenseList = members.map(member => ({
      name: member.name,
      share: member.amount
    }));
    console.log('Expense List:', this.expenseList); // For debugging
  }


  onSubmit(): void {
    if (!this.validateShares()) {
      return; // Prevent form submission if shares do not match the bill amount
    }
  
    if (!this.billForm.get('paidByMember')?.value) {
      alert('Please select the member who paid for the bill.');
      return; // Prevent form submission if no member is set as paidByMember
    }
  
    const selectedMembers = this.members.controls
      .filter(memberControl => memberControl.get('selected')?.value) // Only selected members
      .map(memberControl => ({
        memberId: memberControl.get('memberId')?.value,
        name: memberControl.get('name')?.value,
        amount: memberControl.get('amount')?.value
      }));
  
    const billData = {
      billId: this.billForm.get('billId')?.value,
      description: this.billForm.get('description')?.value,
      billAmount: this.billForm.get('billAmount')?.value,
      paidByMemberId: this.billForm.get('paidByMember')?.value, // Must not be null
      trip: sessionStorage.getItem('tripId'), // Get the trip ID from session storage
      allExpenses: selectedMembers // Only selected members are included
    };
    this.calculateExpenseList(selectedMembers);
  
    if (this.selectedBill) {
      console.log('Updating bill:', billData);
      this.billService.updateBill(billData).subscribe(
        (response: any) => {
          console.log('Bill updated:', response);
          this.getBills();
        },
        (error: any) => {
          console.error('Error updating bill:', error);
        });
    } else {
      this.billService.createBill(billData).subscribe(
        (response:any)=>{
          console.log('Bill created:', response);
          this.getBills();
        },
        (error: any) => {
          console.error('Error creating bill:', error);
        });
    }
    this.closeModal();
  }
}
