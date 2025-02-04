import { Component, OnInit } from '@angular/core';
import { ApicallsService } from '../apicalls.service';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
Chart.register(...registerables);

@Component({
  selector: 'app-analysis',
  templateUrl: './analysis.component.html',
  styleUrls: ['./analysis.component.css']
})
export class AnalysisComponent implements OnInit {
  billsColorMap: { [key: string]: string } = {};
  members: any[] = []; // List of members
  selectedMemberId: string = 'total'; // Default to 'total'
  analysisData: any;
  billsPaidColumns: any[] = []; // Define columns for bills paid table
  percentageData: number[] = []; // Data for percentage chart
  percentageLabels: string[] = []; // Labels for percentage chart
  totalExpenditureData: any[] = []; // Data for total expenditure bar chart
  totalExpenditureLabels: string[] = []; // Labels for total expenditure
  comparativeData: any[] = []; // Data for comparative analysis bar chart
  comparativeLabels: string[] = []; // Labels for comparative analysis
  sortedBills: { billName: string; amount: number }[] = [];
  sortOption: 'asc' | 'desc' = 'asc';
  displayedBills: { billName: string; amount: number }[] = [];
  pageSize = 5; // Number of bills to display per page
  currentPage = 1;
  MemberBarchartOptions: any = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: 'Comparative Analysis'
      }
    }
  } // Chart options
  MemberBarChartData:any;
  MemberBarChartLegend = true;
  colors: string[] = [];
  gridStyle = {
    width: '24.2%',              // 24% width for each grid
    marginRight: '1%',         // Space between grids, except for the last one
    textAlign: 'center',         // Align content to the left
  };
  lastGridStyle = {
    width: '24%',              // Same width for the last grid
    marginRight: '0',          // No margin for the last grid
    textAlign: 'center',         // Align content to the left
  };

  TotalCharts:any={
    doughnutChart: {
      labels: [],
      datasets: [],
      options: {}
    },
    barChart:{
      labels: [],
      datasets: [],
      options: {}
    }
  };

  constructor(private ser: ApicallsService) {}

  round(value: number): number {
    return Math.round(value);
  }
  getBillColor(index: number): string {
    return this.colors[index];
  }
  generateBillColors() {
    this.analysisData.billBreakdown.forEach(() => {
      this.colors.push(this.getRandomColor());
    });
  }
  getRandomColor(): string {
    const colorList: string[] = [
      "#007bff", 
      "#6cb2eb", 
      "#b8daff", 
      "#c3e6cb", 
      "#ffeeba", 
      "#f5c6cb", 
      "#d6e9c6", 
      "#f1f8ff", 
      "#aed6f1", 
      "#d4edda", 
      "#a3d5ff", 
      "#99c0ff", 
      "#c5e3f6", 
      "#e2eafc", 
      "#c3f0ca", 
      "#f9e8b2", 
      "#f9d6d3", 
      "#e3f6f5", 
      "#ffc1c1", 
      "#faf3d9", 
      "#d1ecf1", 
      "#bff0d1", 
      "#fce7b1", 
      "#e9f7ef", 
      "#f8d7da", 
      "#d9f2f1", 
      "#f0f8ff", 
      "#e3f2fd", 
      "#ccf2ff", 
      "#f6ddcc"
    ];    
    
    const randomIndex = Math.floor(Math.random() * colorList.length);
    return colorList[randomIndex];
  }

  ngOnInit() {
    this.ser.getMembers(this.ser.tempTripId).subscribe(data => {
      this.members = data;
      this.loadTotalSummary();
    });
  }

  loadTotalSummary() {
    this.ser.getTotalTripSummary(this.ser.tempTripId).subscribe(data => {
      this.analysisData = data;
      console.log('Total Summary Data:', this.analysisData);
      this.prepareTotalSummaryData();
      // You can add more logic here to handle the data and display it
      this.setTotalCharts();
      this.sortedBills = this.sortBills(this.analysisData.costBreakdownByBill, this.sortOption);
      this.updateDisplayedBills();
    });
  }
  prepareTotalSummaryData() {
    // Prepare data for percentage chart
    this.percentageData = this.analysisData.memberPercentages.map((member: { percentageOfTotal: any; }) => member.percentageOfTotal);
    this.percentageLabels = this.analysisData.memberPercentages.map((member: { memberName: any; }) => member.memberName);

    // Prepare data for total expenditure chart
    this.totalExpenditureData = this.analysisData.eachMemberTotalExpenditure.map((member: { totalExpenditure: any; memberName: any; memberId:any; }) => ({
      data: [member.totalExpenditure],
      label: member.memberName
    }));
    this.totalExpenditureLabels = this.analysisData.eachMemberTotalExpenditure.map((member: { memberName: any; }) => member.memberName);
    
    // Prepare comparative analysis data
    for (const key in this.analysisData.eachMemberTotalExpenditure) {
      if (this.analysisData.eachMemberTotalExpenditure.hasOwnProperty(key)) {
        const member = this.analysisData.eachMemberTotalExpenditure[key];
        this.comparativeData.push(member.totalExpenditure);
        this.comparativeLabels.push(member.memberName);
      }
    }

    // Define columns for bills paid table if needed
    this.billsPaidColumns = [
      { prop: 'billName', name: 'Bill Name' },
      { prop: 'amount', name: 'Amount' }
    ];
  }

  selectMember(memberId: any) {
    console.log('Selected Member ID:', memberId);
    this.selectedMemberId = memberId;
    if (memberId === 'total') {
      this.loadTotalSummary();
    } else {
      this.loadAnalysis(memberId);
    }
  }

  loadAnalysis(memberId: any) {
    let apiUrl = `/api/trips/${this.ser.tempTripId}/personal-summary/${memberId}`; 
    this.ser.getAnalysisForMember(apiUrl).subscribe(data => {
      this.analysisData = data;
      // Prepare data for individual member analysis
      console.log(this.analysisData); 
      this.prepareMemberAnalysisData();
      console.log(this.comparativeData,this.comparativeLabels)
      this.generateBillColors();
    });
  }
  prepareMemberAnalysisData() {
    // Prepare data for bills paid table
    this.analysisData.billsPaid = this.analysisData.billsPaid || [];
    this.billsPaidColumns = [
      { prop: 'billName', name: 'Bill Name' },
      { prop: 'amount', name: 'Amount' }
    ];

    // Prepare comparative analysis data
    this.comparativeData = [];
    this.comparativeLabels = [];
    this.MemberBarChartData = []
    for (const key in this.analysisData.comparativeAnalysis) {
      if (this.analysisData.comparativeAnalysis.hasOwnProperty(key)) {
        const member = this.analysisData.comparativeAnalysis[key];
        this.comparativeData.push(member.totalExpenditure);
        this.comparativeLabels.push(member.memberName);
      }
    }
    const colorsList = this.comparativeData.map(() => this.getRandomColor());
    this.MemberBarChartData = {
      labels: this.comparativeLabels,
      datasets: [
        { data: this.comparativeData,
          label: 'Total expenditures',
          backgroundColor: colorsList,
          borderWidth: 2,
          borderRadius: 5,
          borderSkipped: false, }
      ]
    };
  }

  getMemberName(memberId: any): any {
    const member = this.members.find(m => m.memberId === memberId);
    return member ? member.name : 'Unknown Member';
  }

  setTotalCharts(){
     
    //Doughnut chart
    const labelsList = this.analysisData.costBreakdownByBill.map((bill: { billName: any; }) => bill.billName);
    const dataList = this.analysisData.costBreakdownByBill.map((bill: { amount: any; }) => bill.amount);
    console.log(labelsList,dataList)
    this.TotalCharts.doughnutChart = {
      labels: labelsList,
      datasets: [
        { 
          data: dataList, 
          label: 'Cost Breakdown' 
        }
      ],
      options: {
        responsive: true
      }
    };

    //Bar chart
    const labelsList2 = this.analysisData.eachMemberTotalExpenditure.map((member: { memberName: any; }) => member.memberName);
    const dataList2 = this.analysisData.eachMemberTotalExpenditure.map((member: { totalExpenditure: any; }) => member.totalExpenditure);
    console.log(labelsList2,dataList2)
    const colorsList = dataList2.map(() => this.getRandomColor());
    this.TotalCharts.barChart = {
      labels: labelsList2,
      datasets: [
        { 
          data: dataList2, 
          label: 'Total Expenditure',
          backgroundColor: colorsList,
          borderWidth: 2,
          borderRadius: 5,
          borderSkipped: false,
        }
      ],
      options: {
        responsive: true
      }
    };


  }

  sortBills(bills: { billName: string; amount: number }[], order: 'asc' | 'desc'): { billName: string; amount: number }[] {
    return bills.sort((a, b) => order === 'asc' ? a.amount - b.amount : b.amount - a.amount);
  }
  changeSortOrder(order: 'asc' | 'desc') {
    this.sortOption = order;
    this.sortedBills = this.sortBills(this.analysisData.costBreakdownByBill, this.sortOption);
    this.updateDisplayedBills();
  }
  updateDisplayedBills() {
    const start = (this.currentPage - 1) * this.pageSize;
    this.displayedBills = this.sortedBills.slice(start, start + this.pageSize);
  }
  onPageChange(page: number): void {
    this.currentPage = page;
    this.updateDisplayedBills();
  }


}
