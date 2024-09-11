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
    this.billService.getMembers(1).subscribe(
      (data: any[]) => {
        this.allMembers = data;
        console.log(this.allMembers);
        this.initializeMembersFormArray();
      },
      (error) => {
        console.error('Error fetching members', error);
      }
    );
  }

  // After getting members from Backend set them in the form array
  initializeMembersFormArray(): void {
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

  //Setting the paid by member
  setPaidByMember(index: number): void {
    const selectedMember = this.members.at(index).get('memberId')?.value;
    this.billForm.patchValue({ paidByMember: selectedMember });
  }

  // Get all the bills from Backend
  getBills(): void {
    this.billService.getBills().subscribe(
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
    this.billForm.patchValue({
      billId: bill.billId,
      billAmount: bill.billAmount,
      paidByMember: bill.paidByMember,
      trip: bill.trip,
      description: bill.description
    });
    // Update members with the selected bill's data
    this.billForm.setControl('members', this.fb.array([])); // Reset members array
    this.initializeMembersFormArray(); // Reinitialize with members and update their selections if necessary
  } else {
    this.billForm.reset();
    this.initializeMembersFormArray(); // Reinitialize for a new bill
  }
  }

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
      paidByMember: this.billForm.get('paidByMember')?.value, // Must not be null
      trip: this.billForm.get('trip')?.value,
      members: selectedMembers // Only selected members are included
    };
  
    console.log(billData);
    this.calculateExpenseList(selectedMembers);
  
    if (this.selectedBill) {
      this.billService.updateBill(billData).subscribe(/* handle response */);
    } else {
      this.billService.createBill(billData).subscribe(/* handle response */);
    }
  
    this.closeModal();
  }
}
