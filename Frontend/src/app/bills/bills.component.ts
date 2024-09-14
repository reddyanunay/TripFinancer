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
  }

  get members(): FormArray {
    return this.billForm.get('members') as FormArray;
  }

  getMembers() {
    this.billService.getMembers(sessionStorage.getItem('tripId')).subscribe(
      (data: any[]) => {
        this.allMembers = data;
        this.initializeNewMembersFormArray();
      },
      (error) => {
        console.error('Error fetching members', error);
      }
    );
  }

  initializeNewMembersFormArray(): void {
    const membersArray = this.members;
    while (membersArray.length !== 0) {
      membersArray.removeAt(0);
    }
    this.allMembers.forEach(member => {
      membersArray.push(this.fb.group({
        memberId: [member.memberId],
        name: [member.name],
        selected: [false],
        amount: [0]
      }));
    });
  }

  initializeUpdateMembersFormArray(bill: any): void {
    const membersArray = this.members;
    while (membersArray.length !== 0) {
      membersArray.removeAt(0);
    }
    const expenses = bill.allExpenses || [];
    const paidByMemberId = bill.paidByMemberId;

    this.allMembers.forEach((member, index) => {
      const expense = expenses.find((e: { memberId: any; }) => e.memberId === member.memberId);
      membersArray.push(this.fb.group({
        memberId: [member.memberId],
        name: [member.name],
        selected: [!!expense],
        amount: [expense ? expense.amount : 0],
      }));
      if (member.memberId === paidByMemberId) {
        this.setPaidByMember(index);
      }
    });
  }

  setPaidByMember(index: number): void {
    const selectedMember = this.members.at(index).get('memberId')?.value;
    this.billForm.patchValue({ paidByMember: selectedMember });
  }

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

  openBillModal(bill: any = null): void {
    this.selectedBill = bill;
    this.isModalOpen = true;
    if (bill) {
      this.billForm.patchValue({
        billId: bill.billId,
        billAmount: bill.billAmount,
        paidByMember: bill.paidByMember?.memberId,
        trip: bill.trip,
        description: bill.description
      });
      this.billForm.setControl('members', this.fb.array([]));
      this.initializeUpdateMembersFormArray(bill);
    } else {
      this.billForm.reset();
      this.initializeNewMembersFormArray();
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
      .filter(memberControl => memberControl.get('selected')?.value)
      .reduce((sum, memberControl) => {
        const amount = memberControl.get('amount')?.value || 0;
        return sum + amount;
      }, 0);
    const billAmount = this.billForm.get('billAmount')?.value;
    if (totalShare !== billAmount) {
      alert(`The total shares must sum exactly to the bill amount (${billAmount}). Current total: ${totalShare}`);
      return false;
    }
    return true;
  }

  onMemberSelectChange(index: number): void {
    const memberControl = this.members.at(index);
    const selected = memberControl.get('selected')?.value;
    if (!selected) {
      memberControl.get('amount')?.setValue(0);
    }
  }

  calculateExpenseList(members: any[]): void {
    this.expenseList = members.map(member => ({
      name: member.name,
      share: member.amount
    }));
  }

  onSubmit(): void {
    if (!this.validateShares()) {
      return;
    }
    if (!this.billForm.get('paidByMember')?.value) {
      alert('Please select the member who paid for the bill.');
      return;
    }
    const selectedMembers = this.members.controls
      .filter(memberControl => memberControl.get('selected')?.value)
      .map(memberControl => ({
        memberId: memberControl.get('memberId')?.value,
        name: memberControl.get('name')?.value,
        amount: memberControl.get('amount')?.value
      }));
    const billData = {
      billId: this.billForm.get('billId')?.value,
      description: this.billForm.get('description')?.value,
      billAmount: this.billForm.get('billAmount')?.value,
      paidByMemberId: this.billForm.get('paidByMember')?.value,
      trip: sessionStorage.getItem('tripId'),
      allExpenses: selectedMembers
    };
    this.calculateExpenseList(selectedMembers);
    if (this.selectedBill) {
      this.billService.updateBill(billData).subscribe(
        (response: any) => {
          this.getBills();
        },
        (error: any) => {
          console.error('Error updating bill:', error);
        });
    } else {
      this.billService.createBill(billData).subscribe(
        (response: any) => {
          this.getBills();
        },
        (error: any) => {
          console.error('Error creating bill:', error);
        });
    }
    this.closeModal();
  }

  deleteBill(billId: number): void {
    // Confirm delete action with the user
    if (confirm('Are you sure you want to delete this bill?')) {
      this.billService.deleteBill(billId).subscribe(
        response => {
          // If the response is text, handle it accordingly
          console.log('Delete response:', response);
          
          // Update the UI after successful deletion
          this.getBills();
          this.closeModal();
        },
        error => {
          // Handle any errors
          console.error('Error deleting bill:', error);
        }
      );
    }
  }
  
  
  
}
